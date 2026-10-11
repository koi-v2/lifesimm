package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)

    private val _gameState = MutableStateFlow(GameRepository.createInitialGameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Load saved game if exists on launch
        viewModelScope.launch {
            val saved = repository.loadGameState()
            if (saved != null) {
                _gameState.value = saved
                showNotification("Permainan tersimpan berhasil dimuat!")
            }
        }
    }

    fun showNotification(message: String) {
        _snackbarMessage.value = message
    }

    fun clearNotification() {
        _snackbarMessage.value = null
    }

    fun toggleCrt() {
        _gameState.update { it.copy(crtScanlineEnabled = !it.crtScanlineEnabled) }
    }

    fun updatePlayerAvatar(avatar: PlayerAvatar, newName: String? = null) {
        _gameState.update { current ->
            current.copy(
                player = current.player.copy(
                    avatar = avatar,
                    name = newName?.takeIf { it.isNotBlank() } ?: current.player.name
                )
            )
        }
    }

    fun travelTo(locationId: String) {
        val current = _gameState.value
        if (current.currentLocationId == locationId) return

        val weatherMultiplier = current.weather.energyMultiplier
        var energyCost = (8 * weatherMultiplier).toInt()
        var healthLoss = 0

        // If bad weather and outside
        if (locationId != "home" && (current.weather == WeatherType.HUJAN || current.weather == WeatherType.BADAI)) {
            if (!current.player.hasUmbrella) {
                healthLoss = 10
                showNotification("Kehujanan tanpa payung! Kesehatan berkurang 10.")
            }
        }

        if (current.player.energy < energyCost) {
            showNotification("Energimu terlalu rendah untuk bepergian! Beristirahatlah dulu.")
            return
        }

        val updatedEnergy = (current.player.energy - energyCost).coerceAtLeast(0)
        val updatedHealth = (current.player.health - healthLoss).coerceAtLeast(10)

        _gameState.update { state ->
            state.copy(
                currentLocationId = locationId,
                player = state.player.copy(
                    energy = updatedEnergy,
                    health = updatedHealth
                )
            )
        }
    }

    fun advanceTime(hoursForward: Int = 1) {
        val current = _gameState.value
        val times = TimeOfDay.values()
        val currentIndex = current.timeOfDay.ordinal

        if (currentIndex < times.size - 1) {
            // Next time of day (Pagi -> Siang -> Sore -> Malam)
            val nextTime = times[currentIndex + 1]
            _gameState.update { state ->
                state.copy(
                    timeOfDay = nextTime,
                    currentBannerMessage = "${nextTime.label}: Kota Pikselia ${nextTime.icon} (${nextTime.hour})"
                )
            }
            checkRandomDilemma()
        } else {
            // End of day: Player must rest/sleep to Day + 1
            sleepToNextDay()
        }
    }

    fun sleepToNextDay() {
        val current = _gameState.value
        val nextDay = current.day + 1

        // Weather roll
        val weatherPool = WeatherType.values()
        val nextWeather = weatherPool[Random.nextInt(weatherPool.size)]

        // Rent deduction if residence not owned
        val currentRes = current.residences.find { it.id == current.player.currentResidenceId }
        val rent = if (currentRes != null && !currentRes.isOwned) currentRes.dailyRent else 0L

        // Bank interest (+0.1% daily)
        val bankInterest = (current.player.bankSavings * 0.001).toLong()
        val newBankSavings = current.player.bankSavings + bankInterest

        // Energy restoration based on residence comfort
        val comfortBonus = currentRes?.comfortBonus ?: 10
        val restoredEnergy = (current.player.energy + 60 + comfortBonus).coerceAtMost(100)
        val restoredStress = (current.player.stress - (15 + comfortBonus / 2)).coerceAtLeast(0)

        // Deduct rent from cash
        var remainingCash = current.player.money - rent
        if (remainingCash < 0) {
            remainingCash = 0
            showNotification("Uang tunai habis untuk sewa! Kamu berhutang sewa.")
        }

        // Spouse morning perk
        var spouseBonusText = ""
        var extraHappiness = 0
        var extraEnergy = 0
        if (current.player.spouseNpcId != null) {
            val spouse = current.npcs.find { it.id == current.player.spouseNpcId }
            if (spouse != null) {
                spouseBonusText = "Pasanganmu ${spouse.name} menyiapkan sarapan lezat & kopi hangat!"
                extraHappiness = 15
                extraEnergy = 10
            }
        }

        // Mental health: Burnout check
        var finalHealth = current.player.health
        var finalHappiness = (current.player.happiness + extraHappiness + nextWeather.moodImpact).coerceIn(0, 100)
        if (current.player.stress > 75) {
            finalHealth = (finalHealth - 12).coerceAtLeast(10)
            finalHappiness = (finalHappiness - 10).coerceAtLeast(0)
            showNotification("Peringatan Burnout! Stres tinggi membuat tubuhmu sakit dan lesu.")
        }

        // Dynamic Stocks fluctuation
        val updatedStocks = current.stocks.map { stock ->
            val change = (Random.nextFloat() * 14f) - 6.5f // -6.5% to +7.5%
            val newPrice = (stock.price * (1f + change / 100f)).toLong().coerceAtLeast(500L)
            stock.copy(price = newPrice, changePercent = change)
        }

        val logEntry = StoryLog(
            day = nextDay,
            timeLabel = "Pagi",
            eventText = "Hari ke-$nextDay dimulai. Cuaca: ${nextWeather.label}. $spouseBonusText",
            tag = "Hari Baru"
        )

        _gameState.update { state ->
            state.copy(
                day = nextDay,
                timeOfDay = TimeOfDay.PAGI,
                weather = nextWeather,
                currentLocationId = "home",
                player = state.player.copy(
                    money = remainingCash,
                    bankSavings = newBankSavings,
                    energy = (restoredEnergy + extraEnergy).coerceAtMost(100),
                    stress = restoredStress,
                    health = finalHealth,
                    happiness = finalHappiness
                ),
                stocks = updatedStocks,
                storyLogs = listOf(logEntry) + state.storyLogs,
                currentBannerMessage = "Hari ke-$nextDay: Cuaca ${nextWeather.label} ${nextWeather.icon}. Sewa dibayar: Rp ${String.format("%,d", rent)}."
            )
        }

        // Auto-save game in background
        viewModelScope.launch {
            repository.saveGameState(_gameState.value)
        }

        checkRandomDilemma()
    }

    private fun checkRandomDilemma() {
        if (_gameState.value.activeDilemma != null) return
        // 40% chance of narrative dilemma on time shift
        if (Random.nextFloat() < 0.40f) {
            val dilemmas = GameRepository.getDilemmas()
            val dilemma = dilemmas[Random.nextInt(dilemmas.size)]
            _gameState.update { it.copy(activeDilemma = dilemma) }
        }
    }

    fun resolveDilemma(choice: NarrativeChoice) {
        val current = _gameState.value
        val updatedCash = (current.player.money + choice.moneyChange).coerceAtLeast(0)
        val updatedStress = (current.player.stress + choice.stressChange).coerceIn(0, 100)
        val updatedHappiness = (current.player.happiness + choice.happinessChange).coerceIn(0, 100)

        // Update NPC relationship if applicable
        val updatedNpcs = current.npcs.map { npc ->
            if (choice.relationshipNpcId == npc.id || (choice.relationshipNpcId == null && choice.relationshipChange != 0)) {
                val newHearts = (npc.heartPoints + choice.relationshipChange).coerceAtLeast(0)
                val newStage = calculateStage(newHearts, npc.relationshipStage)
                npc.copy(heartPoints = newHearts, relationshipStage = newStage)
            } else {
                npc
            }
        }

        val log = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = choice.outcomeStory,
            tag = "Keputusan Hidup"
        )

        _gameState.update { state ->
            state.copy(
                activeDilemma = null,
                player = state.player.copy(
                    money = updatedCash,
                    stress = updatedStress,
                    happiness = updatedHappiness
                ),
                npcs = updatedNpcs,
                storyLogs = listOf(log) + state.storyLogs,
                currentBannerMessage = choice.outcomeStory
            )
        }
    }

    fun dismissDilemma() {
        _gameState.update { it.copy(activeDilemma = null) }
    }

    // CAREER & WORK
    fun workShift(isOvertime: Boolean = false) {
        val current = _gameState.value
        val career = GameRepository.getAvailableCareers().find { it.id == current.player.currentCareerId }
            ?: return
        val currentLvl = career.levels.find { it.level == current.player.careerLevel }
            ?: career.levels.first()

        val energyCost = if (isOvertime) (currentLvl.energyCost * 1.6f).toInt() else currentLvl.energyCost
        val stressGain = if (isOvertime) (currentLvl.stressGain * 1.8f).toInt() else currentLvl.stressGain
        val salary = if (isOvertime) (currentLvl.dailySalary * 1.5f).toLong() else currentLvl.dailySalary

        if (current.player.energy < energyCost) {
            showNotification("Energimu tidak cukup untuk bekerja shift ini!")
            return
        }

        val updatedEnergy = current.player.energy - energyCost
        val updatedStress = (current.player.stress + stressGain).coerceIn(0, 100)
        val updatedMoney = current.player.money + salary

        val workLog = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = if (isOvertime) {
                "Lembur kerja sebagai ${currentLvl.title}. Mendapatkan bonus Rp ${String.format("%,d", salary)}."
            } else {
                "Menyelesaikan shift kerja sebagai ${currentLvl.title}. Gaji masuk Rp ${String.format("%,d", salary)}."
            },
            tag = "Karier"
        )

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    energy = updatedEnergy,
                    stress = updatedStress,
                    money = updatedMoney
                ),
                storyLogs = listOf(workLog) + state.storyLogs,
                currentBannerMessage = "Gaji diterima: +Rp ${String.format("%,d", salary)}! Stres bertambah +$stressGain."
            )
        }
        advanceTime()
    }

    fun switchCareer(newCareerId: String) {
        val career = GameRepository.getAvailableCareers().find { it.id == newCareerId } ?: return
        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    currentCareerId = newCareerId,
                    careerLevel = 1
                ),
                currentBannerMessage = "Berganti karier ke ${career.name} sebagai level 1!"
            )
        }
    }

    fun requestPromotion(): Boolean {
        val current = _gameState.value
        val career = GameRepository.getAvailableCareers().find { it.id == current.player.currentCareerId }
            ?: return false
        val nextLevelIndex = current.player.careerLevel
        if (nextLevelIndex >= career.levels.size) {
            showNotification("Kamu sudah mencapai posisi puncak karier ini (${career.levels.last().title})!")
            return false
        }

        val nextLevel = career.levels[nextLevelIndex]
        val p = current.player

        val reqMet = p.intelligence >= nextLevel.requiredIntelligence &&
                p.charisma >= nextLevel.requiredCharisma &&
                p.creativity >= nextLevel.requiredCreativity

        if (!reqMet) {
            showNotification(
                "Syarat promosi belum cukup! Butuh: Logika ${nextLevel.requiredIntelligence}, Karisma ${nextLevel.requiredCharisma}, Kreativitas ${nextLevel.requiredCreativity}."
            )
            return false
        }

        if (p.stress > 65) {
            showNotification("Atasan melihat kamu terlalu stres untuk promosi! Turunkan stres terlebih dahulu.")
            return false
        }

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    careerLevel = nextLevel.level,
                    happiness = (state.player.happiness + 25).coerceAtMost(100)
                ),
                storyLogs = listOf(
                    StoryLog(
                        day = state.day,
                        timeLabel = state.timeOfDay.label,
                        eventText = "Selamat! Promosi jabatan menjadi ${nextLevel.title}!",
                        tag = "Promosi"
                    )
                ) + state.storyLogs,
                currentBannerMessage = "🎉 Selamat! Kamu dipromosikan menjadi ${nextLevel.title}!"
            )
        }
        return true
    }

    fun studyAtCampus(skillType: String) {
        val current = _gameState.value
        val cost = 300_000L
        val energyCost = 25

        if (current.player.money < cost) {
            showNotification("Uang tidak cukup untuk biaya kursus sertifikasi (Rp 300.000)!")
            return
        }
        if (current.player.energy < energyCost) {
            showNotification("Energi tidak cukup untuk belajar di kampus!")
            return
        }

        var newIntel = current.player.intelligence
        var newCharisma = current.player.charisma
        var newCreativity = current.player.creativity
        val skillLabel: String

        when (skillType) {
            "tech" -> {
                newIntel = (newIntel + 12).coerceAtMost(100)
                skillLabel = "Logika & Algoritma +12"
            }
            "business" -> {
                newCharisma = (newCharisma + 12).coerceAtMost(100)
                skillLabel = "Karisma & Negosiasi +12"
            }
            else -> {
                newCreativity = (newCreativity + 12).coerceAtMost(100)
                skillLabel = "Kreativitas & Desain +12"
            }
        }

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - cost,
                    energy = state.player.energy - energyCost,
                    stress = (state.player.stress + 10).coerceAtMost(100),
                    intelligence = newIntel,
                    charisma = newCharisma,
                    creativity = newCreativity
                ),
                currentBannerMessage = "Selesai mengikuti kursus di Universitas! $skillLabel."
            )
        }
        advanceTime()
    }

    // NPC & ROMANCE & MARRIAGE
    fun talkToNpc(npcId: String, dialogueIndex: Int) {
        val current = _gameState.value
        val npc = current.npcs.find { it.id == npcId } ?: return

        val addedHearts = 6
        val newHearts = npc.heartPoints + addedHearts
        val newStage = calculateStage(newHearts, npc.relationshipStage)

        val updatedNpcs = current.npcs.map {
            if (it.id == npcId) it.copy(heartPoints = newHearts, relationshipStage = newStage) else it
        }

        val speech = npc.dialogueOptions.getOrNull(dialogueIndex)
            ?: "Senang bisa mengobrol denganmu hari ini!"

        _gameState.update { state ->
            state.copy(
                npcs = updatedNpcs,
                player = state.player.copy(
                    happiness = (state.player.happiness + 8).coerceAtMost(100),
                    stress = (state.player.stress - 5).coerceAtLeast(0)
                ),
                currentBannerMessage = "${npc.name}: \"$speech\" (+6 Poin Hati)"
            )
        }
    }

    fun giveGiftToNpc(npcId: String, itemId: String) {
        val current = _gameState.value
        val npc = current.npcs.find { it.id == npcId } ?: return
        val count = current.player.inventory[itemId] ?: 0
        if (count <= 0) {
            showNotification("Kamu tidak memiliki item ini di tasmu!")
            return
        }

        val shopCatalog = GameRepository.getShopCatalog()
        val item = shopCatalog.find { it.id == itemId } ?: return

        val isFavorite = npc.favoriteGiftId == itemId
        val heartGain = if (isFavorite) (item.heartBonusIfGifted * 1.5f).toInt() else item.heartBonusIfGifted
        val newHearts = npc.heartPoints + heartGain
        val newStage = calculateStage(newHearts, npc.relationshipStage)

        // Deduct item from inventory
        val updatedInventory = current.player.inventory.toMutableMap()
        if (count == 1) updatedInventory.remove(itemId) else updatedInventory[itemId] = count - 1

        val updatedNpcs = current.npcs.map {
            if (it.id == npcId) it.copy(heartPoints = newHearts, relationshipStage = newStage) else it
        }

        val reactionText = if (isFavorite) {
            "\"Wah, ini hadiah favoritku! Terima kasih banyak, kamu sangat perhatian!\" (+$heartGain Hati)"
        } else {
            "\"Terima kasih atas hadiahnya yang manis!\" (+$heartGain Hati)"
        }

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    inventory = updatedInventory,
                    happiness = (state.player.happiness + 10).coerceAtMost(100)
                ),
                npcs = updatedNpcs,
                currentBannerMessage = "${npc.name}: $reactionText"
            )
        }
    }

    fun askOutOnDate(npcId: String) {
        val current = _gameState.value
        val npc = current.npcs.find { it.id == npcId } ?: return

        if (npc.heartPoints < 50) {
            showNotification("${npc.name} masih merasa canggung untuk berkencan. Dekatkan dirimu lebih dulu!")
            return
        }
        val dateCost = 200_000L
        if (current.player.money < dateCost) {
            showNotification("Uang tidak cukup untuk kencan makan malam romantis (Rp 200.000)!")
            return
        }

        val addedHearts = 22
        val newHearts = npc.heartPoints + addedHearts
        val newStage = calculateStage(newHearts, npc.relationshipStage)

        val updatedNpcs = current.npcs.map {
            if (it.id == npcId) it.copy(heartPoints = newHearts, relationshipStage = newStage) else it
        }

        val dateLog = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = "Berkencan romantis dengan ${npc.name} di bawah lampu gemerlap kota. Hubungan semakin mesra!",
            tag = "Asmara"
        )

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - dateCost,
                    energy = (state.player.energy - 15).coerceAtLeast(0),
                    stress = (state.player.stress - 25).coerceAtLeast(0),
                    happiness = (state.player.happiness + 30).coerceAtMost(100)
                ),
                npcs = updatedNpcs,
                storyLogs = listOf(dateLog) + state.storyLogs,
                currentBannerMessage = "Kencan manis bersama ${npc.name}! Stres berkurang drastis, hati bertambah +$addedHearts."
            )
        }
        advanceTime()
    }

    fun proposeMarriage(npcId: String): Boolean {
        val current = _gameState.value
        val npc = current.npcs.find { it.id == npcId } ?: return false

        val ringCount = current.player.inventory["cincin_lamaran"] ?: 0
        if (ringCount <= 0) {
            showNotification("Kamu membutuhkan Cincin Berlian Lamaran dari Butik Mewah!")
            return false
        }
        if (npc.heartPoints < 120) {
            showNotification("${npc.name} belum siap untuk menikah. Butuh minimal 120 Love Points!")
            return false
        }

        val updatedInventory = current.player.inventory.toMutableMap()
        if (ringCount == 1) updatedInventory.remove("cincin_lamaran") else updatedInventory["cincin_lamaran"] = ringCount - 1

        val newHearts = npc.heartPoints + 30
        val updatedNpcs = current.npcs.map {
            if (it.id == npcId) it.copy(
                heartPoints = newHearts,
                relationshipStage = RelationshipStage.TUNANGAN
            ) else it
        }

        val log = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = "Melamar ${npc.name} dengan cincin berlian! Lamaran diterima dengan tangis bahagia!",
            tag = "Pertunangan"
        )

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    inventory = updatedInventory,
                    happiness = 100,
                    stress = 0
                ),
                npcs = updatedNpcs,
                storyLogs = listOf(log) + state.storyLogs,
                currentBannerMessage = "💍 LAMARAN DITERIMA! ${npc.name} kini adalah tunanganmu! Segera gelar pernikahan di Balai Pernikahan."
            )
        }
        return true
    }

    fun holdWedding(npcId: String): Boolean {
        val current = _gameState.value
        val npc = current.npcs.find { it.id == npcId } ?: return false

        if (npc.relationshipStage != RelationshipStage.TUNANGAN && npc.heartPoints < 140) {
            showNotification("Kalian harus bertunangan terlebih dahulu sebelum menikah!")
            return false
        }

        val weddingFee = 2_000_000L
        if (current.player.money < weddingFee) {
            showNotification("Biaya pesta pernikahan di Balai Pernikahan membutuhkan Rp 2.000.000!")
            return false
        }

        val updatedNpcs = current.npcs.map {
            if (it.id == npcId) it.copy(
                heartPoints = it.heartPoints + 50,
                relationshipStage = RelationshipStage.MENIKAH
            ) else it
        }

        val weddingLog = StoryLog(
            day = current.day,
            timeLabel = "Malam",
            eventText = "Hari Bersejarah! Mengucap janji suci pernikahan bersama ${npc.name} dihadiri seisi Kota Pikselia!",
            tag = "Pernikahan"
        )

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - weddingFee,
                    spouseNpcId = npc.id,
                    happiness = 100,
                    stress = 0
                ),
                npcs = updatedNpcs,
                storyLogs = listOf(weddingLog) + state.storyLogs,
                currentBannerMessage = "💒 SAH MENIKAH! Kamu dan ${npc.name} resmi menjadi pasangan suami-istri sehidup semati!"
            )
        }
        return true
    }

    private fun calculateStage(hearts: Int, current: RelationshipStage): RelationshipStage {
        if (current == RelationshipStage.MENIKAH) return RelationshipStage.MENIKAH
        if (current == RelationshipStage.TUNANGAN) return RelationshipStage.TUNANGAN
        return when {
            hearts >= 100 -> RelationshipStage.PACARAN
            hearts >= 60 -> RelationshipStage.GEBETAN
            hearts >= 25 -> RelationshipStage.TEMAN_BAIK
            else -> RelationshipStage.KENALAN
        }
    }

    // MENTAL HEALTH & WELLNESS
    fun seekTherapyAtClinic() {
        val current = _gameState.value
        val cost = 250_000L
        if (current.player.money < cost) {
            showNotification("Biaya konsultasi terapi kesehatan jiwa butuh Rp 250.000!")
            return
        }

        val healedStress = (current.player.stress - 45).coerceAtLeast(0)
        val healedHappiness = (current.player.happiness + 25).coerceAtMost(100)
        val healedHealth = (current.player.health + 15).coerceAtMost(100)

        val log = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = "Sesi konseling mendalam di klinik dr. Kenji. Beban pikiran terangkat dan kedamaian kembali pulih.",
            tag = "Kesehatan Mental"
        )

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - cost,
                    stress = healedStress,
                    happiness = healedHappiness,
                    health = healedHealth
                ),
                storyLogs = listOf(log) + state.storyLogs,
                currentBannerMessage = "Sesi terapi jiwa tuntas: Stres berkurang drastis -45, pikiran kembali jernih!"
            )
        }
        advanceTime()
    }

    fun doSelfCare(type: String) {
        val current = _gameState.value
        val p = current.player
        when (type) {
            "meditasi" -> {
                _gameState.update { state ->
                    state.copy(
                        player = state.player.copy(
                            stress = (p.stress - 20).coerceAtLeast(0),
                            happiness = (p.happiness + 15).coerceAtMost(100),
                            energy = (p.energy - 5).coerceAtLeast(0)
                        ),
                        currentBannerMessage = "Meditasi tenang di taman kota: Stres berkurang -20."
                    )
                }
            }
            "game_retro" -> {
                _gameState.update { state ->
                    state.copy(
                        player = state.player.copy(
                            stress = (p.stress - 25).coerceAtLeast(0),
                            happiness = (p.happiness + 20).coerceAtMost(100),
                            energy = (p.energy - 10).coerceAtLeast(0)
                        ),
                        currentBannerMessage = "Bermain game pixel retro nostalgia: Kebahagiaan melonjak +20!"
                    )
                }
            }
            "ngopi_santai" -> {
                val cost = 30_000L
                if (p.money < cost) {
                    showNotification("Uang tidak cukup untuk secangkir kopi!")
                    return
                }
                _gameState.update { state ->
                    state.copy(
                        player = state.player.copy(
                            money = state.player.money - cost,
                            energy = (p.energy + 25).coerceAtMost(100),
                            stress = (p.stress - 10).coerceAtLeast(0),
                            happiness = (p.happiness + 10).coerceAtMost(100)
                        ),
                        currentBannerMessage = "Menikmati kopi hangat di teras kafe: Energi +25."
                    )
                }
            }
        }
    }

    // SHOP & INVENTORY
    fun buyShopItem(item: ShopItem) {
        val current = _gameState.value
        if (current.player.money < item.basePrice) {
            showNotification("Uang tunai tidak cukup untuk membeli ${item.name}!")
            return
        }

        val updatedCash = current.player.money - item.basePrice
        val currentCount = current.player.inventory[item.id] ?: 0
        val updatedInventory = current.player.inventory.toMutableMap()
        updatedInventory[item.id] = currentCount + 1

        showNotification("Berhasil membeli 1x ${item.name}!")
        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = updatedCash,
                    inventory = updatedInventory
                )
            )
        }
    }

    fun useInventoryItem(itemId: String) {
        val current = _gameState.value
        val count = current.player.inventory[itemId] ?: 0
        if (count <= 0) return

        val shopCatalog = GameRepository.getShopCatalog()
        val item = shopCatalog.find { it.id == itemId } ?: return

        val updatedInventory = current.player.inventory.toMutableMap()
        if (count == 1) updatedInventory.remove(itemId) else updatedInventory[itemId] = count - 1

        val p = current.player
        val newEnergy = (p.energy + item.energyRestore).coerceAtMost(100)
        val newStress = (p.stress - item.stressReduce).coerceAtLeast(0)
        val newHealth = (p.health + item.healthRestore).coerceAtMost(100)

        showNotification("Menggunakan ${item.name}!")
        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    inventory = updatedInventory,
                    energy = newEnergy,
                    stress = newStress,
                    health = newHealth
                )
            )
        }
    }

    // ECONOMY & STOCKS & BANK
    fun tradeStock(code: String, shares: Int, isBuy: Boolean) {
        val current = _gameState.value
        val stock = current.stocks.find { it.code == code } ?: return
        val totalCost = stock.price * shares

        if (isBuy) {
            if (current.player.money < totalCost) {
                showNotification("Uang tunai tidak cukup untuk membeli $shares lembar saham $code!")
                return
            }
            val updatedStocks = current.stocks.map {
                if (it.code == code) it.copy(ownedShares = it.ownedShares + shares) else it
            }
            _gameState.update { state ->
                state.copy(
                    player = state.player.copy(money = state.player.money - totalCost),
                    stocks = updatedStocks,
                    currentBannerMessage = "Membeli $shares lembar $code seharga Rp ${String.format("%,d", totalCost)}."
                )
            }
        } else {
            if (stock.ownedShares < shares) {
                showNotification("Kamu tidak memiliki $shares lembar saham $code!")
                return
            }
            val updatedStocks = current.stocks.map {
                if (it.code == code) it.copy(ownedShares = it.ownedShares - shares) else it
            }
            _gameState.update { state ->
                state.copy(
                    player = state.player.copy(money = state.player.money + totalCost),
                    stocks = updatedStocks,
                    currentBannerMessage = "Menjual $shares lembar $code, memperoleh Rp ${String.format("%,d", totalCost)}."
                )
            }
        }
    }

    fun depositBank(amount: Long) {
        val current = _gameState.value
        if (current.player.money < amount) {
            showNotification("Uang tunai tidak cukup!")
            return
        }
        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - amount,
                    bankSavings = state.player.bankSavings + amount
                ),
                currentBannerMessage = "Menyetor tabungan ke bank: Rp ${String.format("%,d", amount)}."
            )
        }
    }

    fun withdrawBank(amount: Long) {
        val current = _gameState.value
        if (current.player.bankSavings < amount) {
            showNotification("Saldo tabungan bank tidak cukup!")
            return
        }
        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money + amount,
                    bankSavings = state.player.bankSavings - amount
                ),
                currentBannerMessage = "Menarik uang dari bank: Rp ${String.format("%,d", amount)}."
            )
        }
    }

    fun buyResidenceProperty(residenceId: String) {
        val current = _gameState.value
        val res = current.residences.find { it.id == residenceId } ?: return
        if (res.isOwned) {
            showNotification("Properti ini sudah menjadi milikmu!")
            return
        }
        if (current.player.money < res.purchasePrice) {
            showNotification("Uang tidak cukup untuk membeli properti ini (Rp ${String.format("%,d", res.purchasePrice)})!")
            return
        }

        val updatedResidences = current.residences.map {
            if (it.id == residenceId) it.copy(isOwned = true) else it
        }

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = state.player.money - res.purchasePrice,
                    currentResidenceId = residenceId,
                    happiness = 100
                ),
                residences = updatedResidences,
                currentBannerMessage = "🎉 Selamat! Kamu membeli properti ${res.name}! Bebas biaya sewa selamanya."
            )
        }
    }

    // FESTIVALS & SEASONAL EVENTS
    fun participateInFestival(activity: FestivalActivity): Boolean {
        val current = _gameState.value
        val p = current.player
        val festival = GameRepository.getCurrentFestival(current.day)

        if (p.money < activity.cost) {
            showNotification("Uang tidak cukup untuk ${activity.title} (Rp ${String.format("%,d", activity.cost)})!")
            return false
        }

        if (p.energy < activity.energyCost) {
            showNotification("Energi tidak cukup untuk mengikuti aktivitas festival ini!")
            return false
        }

        var partnerName: String? = null
        if (activity.requiresPartner) {
            val partner = if (p.spouseNpcId != null) {
                current.npcs.find { it.id == p.spouseNpcId }
            } else {
                current.npcs.filter { it.heartPoints >= 50 }.maxByOrNull { it.heartPoints }
            }
            if (partner == null) {
                showNotification("Aktivitas ini membutuhkan pasangan atau gebetan (minimal 50 Love Points)!")
                return false
            }
            partnerName = partner.name
        }

        val updatedCash = p.money - activity.cost + activity.rewardMoney
        val updatedEnergy = (p.energy - activity.energyCost).coerceAtLeast(0)
        val updatedHappiness = (p.happiness + activity.happinessBonus).coerceAtMost(100)
        val updatedStress = (p.stress - activity.stressReduction).coerceAtLeast(0)

        val updatedInventory = p.inventory.toMutableMap()
        if (activity.rewardItemKey != null) {
            val cur = updatedInventory[activity.rewardItemKey] ?: 0
            updatedInventory[activity.rewardItemKey] = cur + 1
        }

        val targetNpcId = activity.partnerNpcId ?: if (activity.requiresPartner) {
            p.spouseNpcId ?: current.npcs.filter { it.heartPoints >= 50 }.maxByOrNull { it.heartPoints }?.id
        } else null

        val updatedNpcs = current.npcs.map { npc ->
            if (npc.id == targetNpcId) {
                val newHearts = npc.heartPoints + activity.lovePointsBonus
                val newStage = calculateStage(newHearts, npc.relationshipStage)
                npc.copy(heartPoints = newHearts, relationshipStage = newStage)
            } else npc
        }

        val partnerNote = if (partnerName != null) " bersama $partnerName" else ""
        val log = StoryLog(
            day = current.day,
            timeLabel = current.timeOfDay.label,
            eventText = "Mengikuti event ${festival.name}: ${activity.title}$partnerNote. Momen istimewa!",
            tag = "Festival Kota"
        )

        val completed = current.completedFestivalActivities + activity.id

        _gameState.update { state ->
            state.copy(
                player = state.player.copy(
                    money = updatedCash,
                    energy = updatedEnergy,
                    happiness = updatedHappiness,
                    stress = updatedStress,
                    inventory = updatedInventory
                ),
                npcs = updatedNpcs,
                completedFestivalActivities = completed,
                storyLogs = listOf(log) + state.storyLogs,
                currentBannerMessage = "🎆 ${activity.title} tuntas! +Happy ${activity.happinessBonus}, -Stres ${activity.stressReduction}."
            )
        }
        return true
    }

    // PERSISTENCE SAVE / LOAD
    fun saveGame() {
        viewModelScope.launch {
            val success = repository.saveGameState(_gameState.value)
            if (success) {
                showNotification("Permainan berhasil disimpan!")
            } else {
                showNotification("Gagal menyimpan permainan.")
            }
        }
    }

    fun loadGame() {
        viewModelScope.launch {
            val saved = repository.loadGameState()
            if (saved != null) {
                _gameState.value = saved
                showNotification("Permainan berhasil dimuat!")
            } else {
                showNotification("Tidak ditemukan data simpanan.")
            }
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            repository.clearSavedGame()
            _gameState.value = GameRepository.createInitialGameState()
            showNotification("Permainan direset ke awal!")
        }
    }
}
