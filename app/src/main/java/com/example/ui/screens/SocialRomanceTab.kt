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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.data.model.RelationshipStage
import com.example.data.repository.GameRepository
import com.example.ui.pixel.PixelHeartBar
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun SocialRomanceTab(
    gameState: GameState,
    onSelectNpc: (String) -> Unit
) {
    val npcs = gameState.npcs
    val spouse = npcs.find { it.id == gameState.player.spouseNpcId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Married Spouse Special Banner
        if (spouse != null) {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF2E193C),
                borderColor = PixelPink
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = GameRepository.getNpcDrawableRes(spouse.id)),
                        contentDescription = spouse.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .border(2.dp, PixelGold)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PASANGAN HIDUP: ${spouse.name.uppercase()}",
                            style = MaterialTheme.typography.titleSmall,
                            color = PixelGold
                        )
                        Text(
                            text = "\"Senang rasanya bisa pulang dan melihatmu setiap hari di rumah kita.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelPink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "❤️ Status: Menikah Bahagia | Bonus Pagi: Energi +10, Happy +15",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelEmerald
                        )
                    }
                    RetroButton(
                        onClick = { onSelectNpc(spouse.id) },
                        text = "Bicara",
                        buttonColor = PixelPink,
                        textColor = Color.White,
                        testTag = "talk_spouse_btn"
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text(
            text = "PENDUDUK KOTA & JALINAN HUBUNGAN:",
            style = MaterialTheme.typography.labelSmall,
            color = PixelTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        npcs.forEach { npc ->
            val isSpouse = npc.id == gameState.player.spouseNpcId
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                backgroundColor = if (isSpouse) Color(0xFF26193E) else PixelSurface,
                borderColor = if (isSpouse) PixelPink else Color(npc.avatarShirtColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = GameRepository.getNpcDrawableRes(npc.id)),
                        contentDescription = npc.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .border(2.dp, Color(npc.avatarShirtColor))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = npc.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = PixelTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(npc.relationshipStage.badgeColorHex))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = npc.relationshipStage.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = npc.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelTextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        PixelHeartBar(hearts = npc.heartPoints)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    RetroButton(
                        onClick = { onSelectNpc(npc.id) },
                        text = "Interaksi",
                        buttonColor = PixelGold,
                        textColor = Color(0xFF1B1100),
                        testTag = "interact_npc_${npc.id}"
                    )
                }
            }
        }
    }
}
