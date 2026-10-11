package com.example.data.model

import com.squareup.moshi.JsonClass

enum class TimeOfDay(val label: String, val hour: String, val icon: String) {
    PAGI("Pagi", "07:00", "🌅"),
    SIANG("Siang", "12:00", "☀️"),
    SORE("Sore", "17:00", "🌇"),
    MALAM("Malam", "21:00", "🌙")
}

enum class WeatherType(
    val label: String,
    val icon: String,
    val description: String,
    val energyMultiplier: Float,
    val moodImpact: Int
) {
    CERAH("Cerah Hangat", "☀️", "Langit biru cerah, hari menyenangkan untuk beraktivitas.", 1.0f, +5),
    HUJAN("Hujan Deras", "🌧️", "Hujan deras mengguyur kota. Bawa payung agar tidak flu!", 1.2f, -5),
    BADAI("Badai Petir", "⛈️", "Petir menggelegar dan angin kencang. Ongkos transport naik.", 1.5f, -10),
    GELOMBANG_PANAS("Gelombang Panas", "🔥", "Suhu udara sangat terik. Cepat dehidrasi dan lelah.", 1.4f, -8),
    KABUT("Kabut Dingin", "🌫️", "Kabut tebal dan sejuk. Suasana tenang untuk merenung di kafe.", 1.1f, +2)
}

enum class RelationshipStage(val label: String, val minHearts: Int, val badgeColorHex: Long) {
    KENALAN("Kenalan", 0, 0xFF888888),
    TEMAN_BAIK("Teman Baik", 25, 0xFF4CAF50),
    GEBETAN("Gebetan / PDKT", 60, 0xFFFF9800),
    PACARAN("Kekasih / Pacar", 100, 0xFFE91E63),
    TUNANGAN("Tunangan", 140, 0xFF9C27B0),
    MENIKAH("Pasangan Hidup (Menikah)", 180, 0xFFFF1744)
}

enum class Season(val label: String, val icon: String, val colorHex: Long, val climateVibe: String) {
    SEMI("Musim Semi (Spring)", "🌸", 0xFFFF8DA1, "Bunga sakura & melati mekar, angin sepoi-sepoi hangat."),
    PANAS("Musim Panas (Summer)", "☀️", 0xFFFFD166, "Hari-hari cerah ceria, langit malam berbintang dan festival kembang api."),
    GUGUR("Musim Gugur (Autumn)", "🍂", 0xFFFF9F1C, "Dedauanan keemasan berguguran, pesta panen dan pasar malam rakyat."),
    DINGIN("Musim Dingin (Winter)", "❄️", 0xFF00E5FF, "Udara sejuk berkabut, lampu kota berkilau, dan waktu hangat bersama keluarga.")
}

@JsonClass(generateAdapter = true)
data class FestivalActivity(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val cost: Long = 0L,
    val energyCost: Int = 15,
    val happinessBonus: Int = 30,
    val stressReduction: Int = 25,
    val lovePointsBonus: Int = 25,
    val rewardMoney: Long = 0L,
    val rewardItemKey: String? = null,
    val requiresPartner: Boolean = false,
    val partnerNpcId: String? = null
)

@JsonClass(generateAdapter = true)
data class CityFestival(
    val id: String,
    val name: String,
    val season: Season,
    val startDayInSeason: Int,
    val endDayInSeason: Int,
    val locationId: String,
    val locationName: String,
    val icon: String,
    val headline: String,
    val lore: String,
    val activities: List<FestivalActivity>
)

@JsonClass(generateAdapter = true)
data class PlayerAvatar(
    val hairStyle: Int = 0, // 0: Pendek, 1: Bergelombang, 2: Cepak, 3: Kuncir
    val hairColorIndex: Int = 0, // 0: Hitam, 1: Cokelat, 2: Pirang, 3: Merah
    val shirtColorIndex: Int = 0, // 0: Merah, 1: Biru, 2: Hijau, 3: Ungu
    val skinToneIndex: Int = 0, // 0: Terang, 1: Kuning Langsat, 2: Sawo Matang
    val accessory: Int = 0 // 0: Tidak ada, 1: Kacamata, 2: Topi
)

@JsonClass(generateAdapter = true)
data class PlayerStats(
    val name: String = "Koi",
    val avatar: PlayerAvatar = PlayerAvatar(),
    val age: Int = 22,
    val money: Long = 2_500_000L, // Uang tunai dalam Rupiah
    val bankSavings: Long = 5_000_000L, // Tabungan bank
    val energy: Int = 100, // 0 - 100
    val health: Int = 95, // 0 - 100
    val stress: Int = 15, // 0 - 100 (jika > 75: Resiko Burnout)
    val happiness: Int = 85, // 0 - 100
    val intelligence: Int = 20, // 0 - 100
    val charisma: Int = 20, // 0 - 100
    val creativity: Int = 20, // 0 - 100
    val currentCareerId: String = "tech",
    val careerLevel: Int = 1,
    val currentResidenceId: String = "kost",
    val spouseNpcId: String? = null,
    val hasUmbrella: Boolean = true,
    val inventory: Map<String, Int> = mapOf("kopi" to 2, "vitamin" to 1)
)

@JsonClass(generateAdapter = true)
data class NPC(
    val id: String,
    val name: String,
    val role: String,
    val age: Int,
    val bio: String,
    val defaultLocation: String,
    val heartPoints: Int = 10,
    val relationshipStage: RelationshipStage = RelationshipStage.KENALAN,
    val favoriteGiftId: String,
    val avatarShirtColor: Long,
    val dialogueOptions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CareerLevel(
    val level: Int,
    val title: String,
    val dailySalary: Long,
    val requiredIntelligence: Int,
    val requiredCharisma: Int,
    val requiredCreativity: Int,
    val energyCost: Int,
    val stressGain: Int
)

@JsonClass(generateAdapter = true)
data class CareerPath(
    val id: String,
    val name: String,
    val industry: String,
    val icon: String,
    val description: String,
    val levels: List<CareerLevel>
)

@JsonClass(generateAdapter = true)
data class StockShare(
    val code: String,
    val name: String,
    val price: Long,
    val changePercent: Float,
    val ownedShares: Int = 0
)

@JsonClass(generateAdapter = true)
data class Residence(
    val id: String,
    val name: String,
    val type: String,
    val dailyRent: Long,
    val purchasePrice: Long,
    val isOwned: Boolean,
    val comfortBonus: Int, // Menambah pemulihan energi/stres saat tidur
    val icon: String
)

@JsonClass(generateAdapter = true)
data class ShopItem(
    val id: String,
    val name: String,
    val category: String, // "Kebutuhan", "Kesehatan", "Hadiah", "Spesial"
    val basePrice: Long,
    val description: String,
    val icon: String,
    val heartBonusIfGifted: Int = 0,
    val energyRestore: Int = 0,
    val stressReduce: Int = 0,
    val healthRestore: Int = 0
)

@JsonClass(generateAdapter = true)
data class NarrativeChoice(
    val id: String,
    val text: String,
    val statImpactDesc: String,
    val moneyChange: Long = 0,
    val stressChange: Int = 0,
    val happinessChange: Int = 0,
    val relationshipNpcId: String? = null,
    val relationshipChange: Int = 0,
    val careerPerfChange: Int = 0,
    val outcomeStory: String
)

@JsonClass(generateAdapter = true)
data class NarrativeDilemma(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Karier", "Asmara", "Moral", "Kesehatan"
    val choices: List<NarrativeChoice>
)

@JsonClass(generateAdapter = true)
data class StoryLog(
    val day: Int,
    val timeLabel: String,
    val eventText: String,
    val tag: String = "Umum"
)
