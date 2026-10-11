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
import com.example.data.model.NPC
import com.example.data.model.PlayerStats
import com.example.data.repository.GameRepository
import com.example.ui.pixel.PixelAvatarCanvas
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun WeddingCeremonyDialog(
    spouse: NPC,
    player: PlayerStats,
    day: Int,
    onDismiss: () -> Unit
) {
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
                Text(
                    text = "💒 UPACARA PERNIKAHAN AGUNG 💒",
                    style = MaterialTheme.typography.titleMedium,
                    color = PixelGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "🎉 SELAMAT MENEMPUH HIDUP BARU! 🎉",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelPink,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Wedding Couple Frame: Player Avatar & Spouse 2D Anime Portrait
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelSurface)
                        .border(2.dp, PixelGold)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelAvatarCanvas(
                            avatar = player.avatar,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelGold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💖", fontSize = 24.sp)
                        Text(
                            text = "JANJI SUCI",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelPink
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = GameRepository.getNpcDrawableRes(spouse.id)),
                            contentDescription = spouse.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .border(2.dp, PixelGold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = spouse.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Marriage Certificate
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2C1E4A))
                        .border(2.dp, PixelGold)
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PIAGAM PERNIKAHAN RESMI",
                            style = MaterialTheme.typography.labelMedium,
                            color = PixelGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "KOTA PIKSELIA - HARI KE-$day",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dengan ini disahkan bahwa:\n${player.name} & ${spouse.name}\nresmi dipersatukan dalam ikatan suci pernikahan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelTextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Di bawah langit berbintang Pikselia, kami berjanji untuk saling mencintai, mendampingi dalam suka dan duka, serta membangun impian bersama.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelPink,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "✨ Manfaat Menikah:\n• Pasangan tinggal bersama di rumahmu\n• Sarapan & kopi setiap pagi (+Energi & Kebahagiaan)\n• Pemulihan stres lebih cepat saat beristirahat bersama",
                    style = MaterialTheme.typography.bodySmall,
                    color = PixelEmerald,
                    lineHeight = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                RetroButton(
                    onClick = onDismiss,
                    text = "Mulai Babak Baru Bersama!",
                    buttonColor = PixelGold,
                    textColor = Color(0xFF221500),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "wedding_celebrate_btn"
                )
            }
        }
    }
}
