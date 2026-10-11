package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FestivalActivity
import com.example.data.model.GameState
import com.example.data.model.NPC
import com.example.data.model.RelationshipStage
import com.example.data.repository.GameRepository
import com.example.ui.pixel.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

enum class MainTab(val label: String, val icon: String) {
    MAP("Peta", "🗺️"),
    CAREER("Karier", "💼"),
    SOCIAL("Sosial", "💬"),
    FESTIVAL("Event", "🎆"),
    ECONOMY("Ekonomi", "📈"),
    MENTAL("Jiwa", "🧠")
}

@Composable
fun MainGameScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var currentTab by remember { mutableStateOf(MainTab.MAP) }
    var selectedNpcId by remember { mutableStateOf<String?>(null) }
    var showWeddingCeremony by remember { mutableStateOf(false) }
    var weddingSpouse by remember { mutableStateOf<NPC?>(null) }
    var showCharCreator by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showFestivalCutscene by remember { mutableStateOf(false) }
    var activeFestivalCutsceneActivity by remember { mutableStateOf<FestivalActivity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelDarkBg)
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(PixelSurfaceVariant)
                            .border(2.dp, PixelGold)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelGold
                        )
                    }
                }
            },
            containerColor = PixelDarkBg,
            topBar = {
                TopGameHud(
                    gameState = gameState,
                    onOpenAvatar = { showCharCreator = true },
                    onOpenSettings = { showSettings = true },
                    onOpenFestival = { currentTab = MainTab.FESTIVAL },
                    onAdvanceTime = { viewModel.advanceTime() }
                )
            },
            bottomBar = {
                BottomGameNavigation(
                    currentTab = currentTab,
                    onSelectTab = { currentTab = it }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    MainTab.MAP -> CityMapTab(
                        gameState = gameState,
                        onTravelTo = { viewModel.travelTo(it) },
                        onQuickAction = { action ->
                            when (action) {
                                "tidur" -> viewModel.sleepToNextDay()
                                "game_retro" -> viewModel.doSelfCare("game_retro")
                                "kerja_shift" -> viewModel.workShift(false)
                                "kerja_lembur" -> viewModel.workShift(true)
                                "ngopi" -> viewModel.doSelfCare("ngopi_santai")
                                "meditasi" -> viewModel.doSelfCare("meditasi")
                                "terapi_jiwa" -> viewModel.seekTherapyAtClinic()
                                "kursus_coding" -> viewModel.studyAtCampus("tech")
                                "kursus_bisnis" -> viewModel.studyAtCampus("business")
                                "buka_pasar" -> currentTab = MainTab.ECONOMY
                                "pernikahan" -> {
                                    val candidate = gameState.npcs.find {
                                        it.relationshipStage == RelationshipStage.TUNANGAN || it.relationshipStage == RelationshipStage.PACARAN
                                    }
                                    if (candidate != null) {
                                        selectedNpcId = candidate.id
                                    } else {
                                        viewModel.showNotification("Kamu belum memiliki tunangan untuk dinikahi!")
                                    }
                                }
                            }
                        },
                        onOpenNpcModal = { selectedNpcId = it }
                    )
                    MainTab.CAREER -> CareerTab(
                        gameState = gameState,
                        onWorkShift = { viewModel.workShift(it) },
                        onRequestPromotion = { viewModel.requestPromotion() },
                        onSwitchCareer = { viewModel.switchCareer(it) },
                        onStudyCourse = { viewModel.studyAtCampus(it) }
                    )
                    MainTab.SOCIAL -> SocialRomanceTab(
                        gameState = gameState,
                        onSelectNpc = { selectedNpcId = it }
                    )
                    MainTab.FESTIVAL -> FestivalCalendarTab(
                        gameState = gameState,
                        onParticipate = { act ->
                            val success = viewModel.participateInFestival(act)
                            if (success) {
                                activeFestivalCutsceneActivity = act
                                showFestivalCutscene = true
                            }
                        },
                        onTravelToLocation = { locId ->
                            viewModel.travelTo(locId)
                            currentTab = MainTab.MAP
                        }
                    )
                    MainTab.ECONOMY -> EconomyShopTab(
                        gameState = gameState,
                        onTradeStock = { code, qty, isBuy -> viewModel.tradeStock(code, qty, isBuy) },
                        onDepositBank = { viewModel.depositBank(it) },
                        onWithdrawBank = { viewModel.withdrawBank(it) },
                        onBuyShopItem = { viewModel.buyShopItem(it) },
                        onUseItem = { viewModel.useInventoryItem(it) },
                        onBuyProperty = { viewModel.buyResidenceProperty(it) }
                    )
                    MainTab.MENTAL -> MentalHealthTab(
                        gameState = gameState,
                        onTherapy = { viewModel.seekTherapyAtClinic() },
                        onSelfCare = { viewModel.doSelfCare(it) }
                    )
                }
            }
        }

        // Active Narrative Dilemma Dialog
        gameState.activeDilemma?.let { dilemma ->
            NarrativeEventDialog(
                dilemma = dilemma,
                onChoose = { choice -> viewModel.resolveDilemma(choice) },
                onDismiss = { viewModel.dismissDilemma() }
            )
        }

        // NPC Dialogue & Romance Modal
        selectedNpcId?.let { npcId ->
            val npc = gameState.npcs.find { it.id == npcId }
            if (npc != null) {
                NpcDialogModal(
                    npc = npc,
                    player = gameState.player,
                    onDismiss = { selectedNpcId = null },
                    onTalk = { dialIndex -> viewModel.talkToNpc(npcId, dialIndex) },
                    onGiveGift = { itemId -> viewModel.giveGiftToNpc(npcId, itemId) },
                    onAskDate = {
                        viewModel.askOutOnDate(npcId)
                        selectedNpcId = null
                    },
                    onPropose = {
                        val success = viewModel.proposeMarriage(npcId)
                        if (success) selectedNpcId = null
                    },
                    onHoldWedding = {
                        val success = viewModel.holdWedding(npcId)
                        if (success) {
                            selectedNpcId = null
                            weddingSpouse = npc
                            showWeddingCeremony = true
                        }
                    }
                )
            }
        }

        // Grand Wedding Ceremony Cutscene Dialog
        if (showWeddingCeremony && weddingSpouse != null) {
            WeddingCeremonyDialog(
                spouse = weddingSpouse!!,
                player = gameState.player,
                day = gameState.day,
                onDismiss = {
                    showWeddingCeremony = false
                    weddingSpouse = null
                }
            )
        }

        // Festival Activity Celebration Cutscene Dialog
        if (showFestivalCutscene && activeFestivalCutsceneActivity != null) {
            FestivalCutsceneDialog(
                festival = GameRepository.getCurrentFestival(gameState.day),
                activity = activeFestivalCutsceneActivity!!,
                gameState = gameState,
                onDismiss = {
                    showFestivalCutscene = false
                    activeFestivalCutsceneActivity = null
                }
            )
        }

        // Character Creation & Customizer Dialog
        if (showCharCreator) {
            CharacterCreationDialog(
                initialName = gameState.player.name,
                initialAvatar = gameState.player.avatar,
                onSave = { name, avatar ->
                    viewModel.updatePlayerAvatar(avatar, name)
                },
                onDismiss = { showCharCreator = false }
            )
        }

        // Settings Dialog
        if (showSettings) {
            SettingsDialog(
                crtEnabled = gameState.crtScanlineEnabled,
                onToggleCrt = { viewModel.toggleCrt() },
                onSaveGame = { viewModel.saveGame() },
                onLoadGame = { viewModel.loadGame() },
                onResetGame = { viewModel.resetGame() },
                onOpenCharCreator = {
                    showSettings = false
                    showCharCreator = true
                },
                onDismiss = { showSettings = false }
            )
        }

        // CRT Scanline Overlay
        CRTScanlineOverlay(enabled = gameState.crtScanlineEnabled)
    }
}

@Composable
fun TopGameHud(
    gameState: GameState,
    onOpenAvatar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFestival: () -> Unit,
    onAdvanceTime: () -> Unit
) {
    val p = gameState.player
    val careerTitle = GameRepository.getCareerTitle(p.currentCareerId, p.careerLevel)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(PixelSurface)
            .border(2.dp, PixelBorderDark)
            .padding(8.dp)
    ) {
        // Line 1: Day, Time, Weather, Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenAvatar() }
            ) {
                PixelAvatarCanvas(avatar = p.avatar, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "HARI KE-${gameState.day}",
                        style = MaterialTheme.typography.titleSmall,
                        color = PixelGold
                    )
                    Text(
                        text = "${gameState.timeOfDay.label} (${gameState.timeOfDay.hour})",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelTextPrimary
                    )
                }
            }

            // Weather badge
            Box(
                modifier = Modifier
                    .background(PixelSurfaceVariant)
                    .border(1.dp, PixelCyan)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = gameState.weather.icon, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gameState.weather.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = PixelCyan
                    )
                }
            }

            // Festival badge
            val curFest = GameRepository.getCurrentFestival(gameState.day)
            Box(
                modifier = Modifier
                    .background(PixelSurfaceVariant)
                    .border(1.dp, PixelGold)
                    .clickable { onOpenFestival() }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = curFest.icon, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "EVENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = PixelGold
                    )
                }
            }

            // Advance Time Button & Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                RetroButton(
                    onClick = onAdvanceTime,
                    text = "Lanjut",
                    icon = "⌛",
                    buttonColor = PixelGold,
                    textColor = Color(0xFF1B1100),
                    testTag = "advance_time_hud_btn"
                )
                Spacer(modifier = Modifier.width(4.dp))
                RetroButton(
                    onClick = onOpenSettings,
                    text = "⚙️",
                    buttonColor = PixelSurfaceVariant,
                    textColor = PixelTextPrimary,
                    testTag = "settings_hud_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 2: Money and Career badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Rp ${String.format("%,d", p.money)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = PixelMoneyColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💼", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = careerTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 3: Compact Status Bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PixelStatBar(
                label = "Energi",
                currentValue = p.energy,
                barColor = PixelEnergyColor,
                icon = "⚡",
                modifier = Modifier.weight(1f)
            )
            PixelStatBar(
                label = "Fisik",
                currentValue = p.health,
                barColor = PixelHealthColor,
                icon = "💚",
                modifier = Modifier.weight(1f)
            )
            PixelStatBar(
                label = "Stres",
                currentValue = p.stress,
                barColor = PixelStressColor,
                icon = "🔥",
                isWarningHigh = true,
                modifier = Modifier.weight(1f)
            )
            PixelStatBar(
                label = "Happy",
                currentValue = p.happiness,
                barColor = PixelHappinessColor,
                icon = "✨",
                modifier = Modifier.weight(1f)
            )
        }

        // Live Banner
        if (gameState.currentBannerMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF100C1F))
                    .border(1.dp, PixelBorderHighlight)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "📢 ${gameState.currentBannerMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelGold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BottomGameNavigation(
    currentTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(PixelSurface)
            .border(2.dp, PixelBorderDark)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = tab == currentTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clickable { onSelectTab(tab) }
                    .border(
                        1.dp,
                        if (isSelected) PixelGold else PixelBorderDark
                    )
                    .background(if (isSelected) PixelSurfaceVariant else Color.Transparent)
                    .padding(vertical = 6.dp)
                    .testTag("nav_tab_${tab.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = tab.icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) PixelGold else PixelTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
