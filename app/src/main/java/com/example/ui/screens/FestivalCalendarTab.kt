package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CityFestival
import com.example.data.model.FestivalActivity
import com.example.data.model.GameState
import com.example.data.model.Season
import com.example.data.repository.GameRepository
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun FestivalCalendarTab(
    gameState: GameState,
    onParticipate: (FestivalActivity) -> Unit,
    onTravelToLocation: (String) -> Unit
) {
    val currentSeason = gameState.getSeason()
    val dayInSeason = gameState.getDayInSeason()
    val dayOfYear = gameState.getDayOfYear()
    val currentYear = gameState.getYear()

    val currentFestival = GameRepository.getCurrentFestival(gameState.day)
    val allFestivals = GameRepository.getFestivals()

    var selectedSeasonTab by remember { mutableStateOf(currentSeason) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Active Season Header Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurfaceVariant,
            borderColor = Color(currentSeason.colorHex)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentSeason.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentSeason.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = PixelGold
                        )
                    }
                    Text(
                        text = "Tahun ke-$currentYear • Hari ke-$dayInSeason dari 7 (Hari Total: ${gameState.day})",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(currentSeason.colorHex))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "AKTIF",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentSeason.climateVibe,
                style = MaterialTheme.typography.bodySmall,
                color = PixelTextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Festival Feature Banner Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF23163E),
            borderColor = PixelGold
        ) {
            // Festival Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .border(2.dp, PixelGold)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pixel_festival_banner_1791681351575),
                    contentDescription = "Pemandangan Festival Pikselia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(PixelDarkBg.copy(alpha = 0.88f))
                        .border(1.dp, PixelGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${currentFestival.icon} ${currentFestival.name.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PixelGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentFestival.headline,
                style = MaterialTheme.typography.titleSmall,
                color = PixelPink
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentFestival.lore,
                style = MaterialTheme.typography.bodySmall,
                color = PixelTextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 Lokasi: ${currentFestival.locationName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelEmerald
                )
                RetroButton(
                    onClick = { onTravelToLocation(currentFestival.locationId) },
                    text = "Pergi ke Lokasi",
                    buttonColor = PixelEmerald,
                    textColor = Color.Black,
                    testTag = "go_to_festival_loc"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Festival Activities List
        Text(
            text = "AKTIVITAS FESTIVAL HARI INI:",
            style = MaterialTheme.typography.labelSmall,
            color = PixelGold
        )
        Spacer(modifier = Modifier.height(6.dp))

        currentFestival.activities.forEach { act ->
            val isCompleted = gameState.completedFestivalActivities.contains(act.id)
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                backgroundColor = if (isCompleted) PixelSurfaceVariant else PixelSurface,
                borderColor = if (isCompleted) PixelEmerald else PixelBorderHighlight
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = act.icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = act.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = PixelTextPrimary
                            )
                            if (isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .background(PixelEmerald)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SELESAI ✓",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Text(
                            text = act.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelTextMuted
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Stats & Rewards breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (act.cost > 0) {
                                Text(
                                    text = "Biaya: Rp ${String.format("%,d", act.cost)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PixelGold
                                )
                            } else {
                                Text(
                                    text = "Biaya: Gratis!",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PixelEmerald
                                )
                            }
                            Text(
                                text = "Beban Energi: -${act.energyCost}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PixelTextSecondary
                            )
                        }

                        Text(
                            text = "Efek: +Happy ${act.happinessBonus} | -Stres ${act.stressReduction}${if (act.requiresPartner || act.partnerNpcId != null) " | +Cinta ${act.lovePointsBonus} LP" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelPink
                        )

                        if (act.rewardMoney > 0) {
                            Text(
                                text = "Hadiah Tunai: +Rp ${String.format("%,d", act.rewardMoney)} 🪙",
                                style = MaterialTheme.typography.labelSmall,
                                color = PixelMoneyColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    RetroButton(
                        onClick = { onParticipate(act) },
                        text = if (isCompleted) "Ikuti Lagi" else "Ikuti",
                        buttonColor = if (isCompleted) PixelSurfaceVariant else PixelGold,
                        textColor = if (isCompleted) PixelTextPrimary else Color.Black,
                        testTag = "participate_act_${act.id}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Seasons Road Map / Annual Calendar
        Text(
            text = "KALENDER 4 MUSIM KOTA PIKSELIA:",
            style = MaterialTheme.typography.labelSmall,
            color = PixelTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Season.values().forEach { season ->
                val isSelected = season == selectedSeasonTab
                RetroButton(
                    onClick = { selectedSeasonTab = season },
                    text = "${season.icon} ${season.name}",
                    buttonColor = if (isSelected) Color(season.colorHex) else PixelSurfaceVariant,
                    textColor = if (isSelected) Color.Black else PixelTextSecondary,
                    modifier = Modifier.weight(1f),
                    testTag = "season_tab_${season.name.lowercase()}"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val festivalForSeason = allFestivals.find { it.season == selectedSeasonTab }
        if (festivalForSeason != null) {
            val isCurrentFestival = festivalForSeason.id == currentFestival.id
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isCurrentFestival) PixelSurfaceVariant else PixelSurface,
                borderColor = Color(selectedSeasonTab.colorHex)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${festivalForSeason.icon} ${festivalForSeason.name}",
                        style = MaterialTheme.typography.titleSmall,
                        color = PixelGold
                    )
                    Text(
                        text = "Hari ${festivalForSeason.startDayInSeason} - ${festivalForSeason.endDayInSeason}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(selectedSeasonTab.colorHex)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = festivalForSeason.lore,
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Aktivitas Unggulan: ${festivalForSeason.activities.joinToString(", ") { it.title }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelEmerald
                )
            }
        }
    }
}
