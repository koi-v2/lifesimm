package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.ui.pixel.PixelStatBar
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun MentalHealthTab(
    gameState: GameState,
    onTherapy: () -> Unit,
    onSelfCare: (String) -> Unit
) {
    val p = gameState.player
    val isBurnout = p.stress > 75

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Mental Health Overview Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isBurnout) Color(0xFF381423) else PixelSurfaceVariant,
            borderColor = if (isBurnout) PixelStressColor else PixelEmerald
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🧠 SISTEM KESEHATAN MENTAL",
                        style = MaterialTheme.typography.titleMedium,
                        color = PixelGold
                    )
                    Text(
                        text = if (isBurnout) "⚠️ BAHAYA: Kamu Mengalami Burnout!" else "Kondisi Mental: Stabil & Terkendali",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isBurnout) PixelStressColor else PixelEmerald
                    )
                }

                Box(
                    modifier = Modifier
                        .background(if (isBurnout) PixelStressColor else PixelEmerald)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBurnout) "BURNOUT" else "SEHAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bars
            PixelStatBar(
                label = "Tingkat Stres (Beban Pikiran)",
                currentValue = p.stress,
                barColor = PixelStressColor,
                icon = "⚡",
                isWarningHigh = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            PixelStatBar(
                label = "Indeks Kebahagiaan & Suasana Hati",
                currentValue = p.happiness,
                barColor = PixelHappinessColor,
                icon = "✨"
            )

            Spacer(modifier = Modifier.height(6.dp))

            PixelStatBar(
                label = "Kesehatan Fisik & Imun",
                currentValue = p.health,
                barColor = PixelHealthColor,
                icon = "💚"
            )

            Spacer(modifier = Modifier.height(6.dp))

            PixelStatBar(
                label = "Daya Tahan & Energi",
                currentValue = p.energy,
                barColor = PixelEnergyColor,
                icon = "🔋"
            )

            if (isBurnout) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Efek Negatif Burnout: Kesehatan berkurang setiap hari, produktivitas kerja turun, dan resiko jatuh sakit meningkat drastis. Segera ikuti sesi terapi di klinik!",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelStressColor,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Healing & Relaxation Activities Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurface,
            borderColor = PixelPink
        ) {
            Text(
                text = "🌿 PROGRAM PEMULIHAN & SELF-CARE:",
                style = MaterialTheme.typography.titleSmall,
                color = PixelPink
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Therapy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelSurfaceVariant)
                    .border(1.dp, PixelBorderDark)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🩺 Konseling Terapi di Klinik dr. Kenji",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PixelTextPrimary
                    )
                    Text(
                        text = "Biaya Rp 250.000 | Stres -45, Kebahagiaan +25, Pulihkan Burnout",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelEmerald
                    )
                }
                RetroButton(
                    onClick = onTherapy,
                    text = "Konseling",
                    buttonColor = PixelPink,
                    textColor = Color.White,
                    testTag = "action_therapy_btn"
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Meditation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelSurfaceVariant)
                    .border(1.dp, PixelBorderDark)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🌸 Meditasi & Hirup Udara Segar Taman",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PixelTextPrimary
                    )
                    Text(
                        text = "Gratis | Stres -20, Kebahagiaan +15",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelGold
                    )
                }
                RetroButton(
                    onClick = { onSelfCare("meditasi") },
                    text = "Meditasi",
                    buttonColor = PixelEmerald,
                    textColor = Color.Black,
                    testTag = "action_meditate_btn"
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Retro Gaming
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelSurfaceVariant)
                    .border(1.dp, PixelBorderDark)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🕹️ Me-Time: Main Game Konsol Retro di Kamar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PixelTextPrimary
                    )
                    Text(
                        text = "Gratis | Stres -25, Kebahagiaan +20",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelCyan
                    )
                }
                RetroButton(
                    onClick = { onSelfCare("game_retro") },
                    text = "Main Game",
                    buttonColor = PixelCyan,
                    textColor = Color.White,
                    testTag = "action_gaming_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chronicles & Story Logs
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurface,
            borderColor = PixelBorderHighlight
        ) {
            Text(
                text = "📜 JURNAL KISAH HIDUP & KEPUTUSAN:",
                style = MaterialTheme.typography.titleSmall,
                color = PixelGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Catatan perjalanan takdir dan jejak moral yang kamu ukir di kota ini.",
                style = MaterialTheme.typography.bodySmall,
                color = PixelTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            gameState.storyLogs.take(15).forEach { log ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(PixelSurfaceVariant)
                        .border(1.dp, PixelBorderDark)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hari ke-${log.day} (${log.timeLabel})",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelGold
                        )
                        Text(
                            text = "[${log.tag}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.eventText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PixelTextPrimary
                    )
                }
            }
        }
    }
}
