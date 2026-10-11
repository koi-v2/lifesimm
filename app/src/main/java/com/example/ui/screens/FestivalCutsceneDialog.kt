package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CityFestival
import com.example.data.model.FestivalActivity
import com.example.data.model.GameState
import com.example.data.repository.GameRepository
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun FestivalCutsceneDialog(
    festival: CityFestival,
    activity: FestivalActivity,
    gameState: GameState,
    onDismiss: () -> Unit
) {
    val partner = if (activity.requiresPartner) {
        if (gameState.player.spouseNpcId != null) {
            gameState.npcs.find { it.id == gameState.player.spouseNpcId }
        } else {
            gameState.npcs.filter { it.heartPoints >= 50 }.maxByOrNull { it.heartPoints }
        }
    } else if (activity.partnerNpcId != null) {
        gameState.npcs.find { it.id == activity.partnerNpcId }
    } else null

    Dialog(onDismissRequest = onDismiss) {
        RetroCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            backgroundColor = PixelDarkBg,
            borderColor = PixelGold
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "${festival.icon} ${festival.name.uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = PixelGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "EVENT: ${activity.title.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelPink,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Beautiful Festival Banner Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .border(2.dp, PixelGold)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pixel_festival_banner_1791681351575),
                        contentDescription = "Suasana Festival Malam Pikselia",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(PixelDarkBg.copy(alpha = 0.85f))
                            .border(1.dp, PixelGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🎆 FESTIVAL MALAM",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Partner Interaction Box if present
                if (partner != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PixelSurface)
                            .border(1.dp, Color(partner.avatarShirtColor))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = GameRepository.getNpcDrawableRes(partner.id)),
                            contentDescription = partner.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .border(2.dp, Color(partner.avatarShirtColor))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = partner.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = PixelGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val quote = when (partner.id) {
                                "aoi", "citra" -> "\"Kelopak bunga di festival ini mengingatkanku betapa indahnya momen kebersamaan kita, Koi...\""
                                "ren", "arka" -> "\"Event malam ini seru banget! Lebih asyik dari game AAA mana pun kalau bareng kamu, Koi!\""
                                "reiko", "maya" -> "\"Malam ini aku melupakan sejenak bursa saham... hanya untuk menikmati waktu bersamamu, Koi.\""
                                "kenji", "rayhan" -> "\"Melihat senyummu di bawah lampion malam adalah obat jiwa paling manjur di dunia, Koi.\""
                                "ryo", "rio" -> "\"Petikan gitarku malam ini kupersembahkan khusus untukmu, kawan tercinta Koi!\""
                                "yuki" -> "\"Di bawah langit berbintang ini, rasanya seperti masuk ke halaman terindah dalam novel, Koi...\""
                                "daiki" -> "\"Oi Koi! Festival ini meriah banget! Ramen hangat dan senyumanmu bikin malam ini tak terlupakan!\""
                                else -> "\"Terima kasih sudah mengajakku merayakan festival malam ini, Koi!\""
                            }
                            Text(
                                text = quote,
                                style = MaterialTheme.typography.bodySmall,
                                color = PixelTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Reward & Celebration Details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelSurfaceVariant)
                        .border(1.dp, PixelBorderHighlight)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "HASIL KEGIATAN FESTIVAL:",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Kebahagiaan melonjak +${activity.happinessBonus} ✨",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelTextPrimary
                        )
                        Text(
                            text = "• Beban stres terhempas -${activity.stressReduction} 🔥",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelEmerald
                        )
                        if (partner != null) {
                            Text(
                                text = "• Ikatan cinta bersama ${partner.name} bertambah +${activity.lovePointsBonus} LP ❤️",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PixelPink
                            )
                        }
                        if (activity.rewardMoney > 0) {
                            Text(
                                text = "• Hadiah Uang Tunai: +Rp ${String.format("%,d", activity.rewardMoney)} 🪙",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PixelMoneyColor
                            )
                        }
                        if (activity.rewardItemKey != null) {
                            Text(
                                text = "• Hadiah Item Spesial telah masuk ke tas inventori! 🎁",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PixelGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                RetroButton(
                    onClick = onDismiss,
                    text = "Kembali ke Festival",
                    buttonColor = PixelGold,
                    textColor = Color(0xFF201300),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "dismiss_festival_cutscene"
                )
            }
        }
    }
}
