package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.data.repository.GameRepository
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun CareerTab(
    gameState: GameState,
    onWorkShift: (Boolean) -> Unit,
    onRequestPromotion: () -> Unit,
    onSwitchCareer: (String) -> Unit,
    onStudyCourse: (String) -> Unit
) {
    val careers = GameRepository.getAvailableCareers()
    val currentCareer = careers.find { it.id == gameState.player.currentCareerId } ?: careers.first()
    val currentLevel = currentCareer.levels.find { it.level == gameState.player.careerLevel }
        ?: currentCareer.levels.first()
    val nextLevel = currentCareer.levels.getOrNull(gameState.player.careerLevel) // 0-indexed matches current level value

    var showSwitchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Career Header Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurfaceVariant,
            borderColor = PixelCyan
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentCareer.icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentCareer.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = PixelGold
                        )
                    }
                    Text(
                        text = "Industri: ${currentCareer.industry}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelTextSecondary
                    )
                }

                RetroButton(
                    onClick = { showSwitchDialog = true },
                    text = "Ganti Karier",
                    buttonColor = PixelSurface,
                    textColor = PixelTextPrimary,
                    testTag = "switch_career_btn"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Level & Salary Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PixelSurface)
                    .border(1.dp, PixelBorderHighlight)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "JABATAN SAAT INI (LEVEL ${currentLevel.level}/${currentCareer.levels.size}):",
                        style = MaterialTheme.typography.labelSmall,
                        color = PixelEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentLevel.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = PixelTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gaji Harian: Rp ${String.format("%,d", currentLevel.dailySalary)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelMoneyColor
                        )
                        Text(
                            text = "Beban Energi: -${currentLevel.energyCost} | Stres: +${currentLevel.stressGain}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Work shift buttons
            Text(
                text = "AKSI KERJA HARI INI:",
                style = MaterialTheme.typography.labelSmall,
                color = PixelTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RetroButton(
                    onClick = { onWorkShift(false) },
                    text = "Shift Normal",
                    icon = "💼",
                    buttonColor = PixelGold,
                    textColor = Color(0xFF1B1100),
                    modifier = Modifier.weight(1f),
                    testTag = "work_normal_btn"
                )
                RetroButton(
                    onClick = { onWorkShift(true) },
                    text = "Lembur (+1.5x Gaji)",
                    icon = "⚡",
                    buttonColor = PixelOrange,
                    textColor = Color(0xFF1B1100),
                    modifier = Modifier.weight(1f),
                    testTag = "work_overtime_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Promotion & Skills Requirements Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurface,
            borderColor = PixelBorderHighlight
        ) {
            Text(
                text = "PROMOSI JABATAN BERIKUTNYA:",
                style = MaterialTheme.typography.titleSmall,
                color = PixelGold
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (nextLevel != null) {
                Text(
                    text = "Target: ${nextLevel.title} (Level ${nextLevel.level})",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PixelTextPrimary
                )
                Text(
                    text = "Gaji Baru: Rp ${String.format("%,d", nextLevel.dailySalary)} / hari",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelMoneyColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Stats requirements checklist
                val p = gameState.player
                SkillRequirementRow("Logika & Coding", p.intelligence, nextLevel.requiredIntelligence)
                SkillRequirementRow("Karisma & Bisnis", p.charisma, nextLevel.requiredCharisma)
                SkillRequirementRow("Kreativitas & Seni", p.creativity, nextLevel.requiredCreativity)

                Spacer(modifier = Modifier.height(10.dp))

                RetroButton(
                    onClick = onRequestPromotion,
                    text = "Ajukan Promosi Jabatan",
                    icon = "🏆",
                    buttonColor = PixelEmerald,
                    textColor = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "apply_promotion_btn"
                )
            } else {
                Text(
                    text = "🏆 Puncak Karier Tercapai! Kamu adalah ${currentLevel.title}!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PixelGold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // University Training & Courses Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurface,
            borderColor = PixelPurple
        ) {
            Text(
                text = "🎓 KELAS & SERTIFIKASI UNIVERSITAS:",
                style = MaterialTheme.typography.titleSmall,
                color = PixelPurple
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Biaya per kursus: Rp 300.000 (Energi -25, Nilai Skill +12)",
                style = MaterialTheme.typography.bodySmall,
                color = PixelTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            CourseItem("Bootcamp Coding & Algoritma", "Meningkatkan Logika", "tech", onStudyCourse)
            Spacer(modifier = Modifier.height(4.dp))
            CourseItem("Leadership & Negosiasi", "Meningkatkan Karisma", "business", onStudyCourse)
            Spacer(modifier = Modifier.height(4.dp))
            CourseItem("Workshop Seni & Desain Kreatif", "Meningkatkan Kreativitas", "creative", onStudyCourse)
        }

        // Switch Career Modal Dialog
        if (showSwitchDialog) {
            AlertDialog(
                onDismissRequest = { showSwitchDialog = false },
                title = {
                    Text(
                        text = "PILIH JALUR KARIER BARU",
                        style = MaterialTheme.typography.titleMedium,
                        color = PixelGold
                    )
                },
                text = {
                    Column {
                        careers.forEach { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${c.icon} ${c.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PixelTextPrimary
                                    )
                                    Text(
                                        text = c.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PixelTextMuted
                                    )
                                }
                                RetroButton(
                                    onClick = {
                                        onSwitchCareer(c.id)
                                        showSwitchDialog = false
                                    },
                                    text = "Pilih",
                                    buttonColor = PixelGold,
                                    textColor = Color.Black,
                                    testTag = "select_career_${c.id}"
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    RetroButton(
                        onClick = { showSwitchDialog = false },
                        text = "Tutup",
                        buttonColor = PixelSurfaceVariant,
                        textColor = PixelTextPrimary,
                        testTag = "close_switch_career"
                    )
                },
                containerColor = PixelDarkBg
            )
        }
    }
}

@Composable
private fun SkillRequirementRow(name: String, current: Int, required: Int) {
    val met = current >= required
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name: $current / $required",
            style = MaterialTheme.typography.bodyMedium,
            color = if (met) PixelEmerald else PixelTextSecondary
        )
        Text(
            text = if (met) "✓ Terpenuhi" else "✗ Belum Cukup",
            style = MaterialTheme.typography.labelSmall,
            color = if (met) PixelEmerald else PixelStressColor
        )
    }
}

@Composable
private fun CourseItem(
    title: String,
    desc: String,
    type: String,
    onStudy: (String) -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = PixelTextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = PixelTextSecondary
            )
        }
        RetroButton(
            onClick = { onStudy(type) },
            text = "Ikuti",
            buttonColor = PixelPurple,
            textColor = Color.White,
            testTag = "study_btn_$type"
        )
    }
}
