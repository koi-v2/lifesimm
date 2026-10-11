package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameState(
    val day: Int = 1,
    val timeOfDay: TimeOfDay = TimeOfDay.PAGI,
    val weather: WeatherType = WeatherType.CERAH,
    val inflationRate: Float = 0.04f, // 4% inflasi dasar
    val currentLocationId: String = "home", // home, office, cafe, hospital, campus, market, hall
    val player: PlayerStats = PlayerStats(),
    val npcs: List<NPC> = emptyList(),
    val stocks: List<StockShare> = emptyList(),
    val residences: List<Residence> = emptyList(),
    val activeDilemma: NarrativeDilemma? = null,
    val storyLogs: List<StoryLog> = emptyList(),
    val completedFestivalActivities: Set<String> = emptySet(),
    val crtScanlineEnabled: Boolean = true,
    val currentBannerMessage: String = "Selamat datang di Kota Pikselia! Mulai petualangan kehidupan barumu."
) {
    fun getDayOfYear(): Int = ((day - 1) % 28) + 1
    fun getYear(): Int = ((day - 1) / 28) + 1
    fun getSeason(): Season = when (getDayOfYear()) {
        in 1..7 -> Season.SEMI
        in 8..14 -> Season.PANAS
        in 15..21 -> Season.GUGUR
        else -> Season.DINGIN
    }
    fun getDayInSeason(): Int = ((getDayOfYear() - 1) % 7) + 1
}
