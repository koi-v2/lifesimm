package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NPC
import com.example.data.model.PlayerStats
import com.example.data.model.RelationshipStage
import com.example.data.repository.GameRepository
import com.example.ui.pixel.*
import com.example.ui.theme.*

@Composable
fun NpcDialogModal(
    npc: NPC,
    player: PlayerStats,
    onDismiss: () -> Unit,
    onTalk: (Int) -> Unit,
    onGiveGift: (String) -> Unit,
    onAskDate: () -> Unit,
    onPropose: () -> Unit,
    onHoldWedding: () -> Unit
) {
    var showGiftPicker by remember { mutableStateOf(false) }
    var selectedDialogueTab by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        RetroCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            backgroundColor = PixelDarkBg,
            borderColor = Color(npc.avatarShirtColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with NPC Name & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = npc.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = PixelGold
                        )
                        Text(
                            text = npc.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelTextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(npc.relationshipStage.badgeColorHex))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = npc.relationshipStage.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // NPC Portrait & Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelSurface)
                        .border(1.dp, PixelBorderHighlight)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = GameRepository.getNpcDrawableRes(npc.id)),
                        contentDescription = npc.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .border(2.dp, Color(npc.avatarShirtColor))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        PixelHeartBar(hearts = npc.heartPoints)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = npc.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dialogue Box Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelSurfaceVariant)
                        .border(1.dp, PixelBorderHighlight)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "💬 Percakapan & Respon:",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        npc.dialogueOptions.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• \"$option\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PixelTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                RetroButton(
                                    onClick = { onTalk(index) },
                                    text = "Bahas",
                                    buttonColor = PixelEmerald,
                                    textColor = Color(0xFF00291D),
                                    testTag = "talk_topic_$index"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Text(
                    text = "Aksi Hubungan & Asmara:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RetroButton(
                        onClick = { showGiftPicker = !showGiftPicker },
                        text = "Beri Hadiah",
                        icon = "🎁",
                        buttonColor = PixelCyan,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        testTag = "give_gift_btn"
                    )
                    RetroButton(
                        onClick = onAskDate,
                        text = "Ajak Kencan",
                        icon = "🥂",
                        buttonColor = PixelPink,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        testTag = "ask_date_btn"
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Marriage Proposal & Wedding Actions
                if (npc.relationshipStage == RelationshipStage.PACARAN || npc.relationshipStage == RelationshipStage.TUNANGAN) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (npc.relationshipStage != RelationshipStage.TUNANGAN && npc.relationshipStage != RelationshipStage.MENIKAH) {
                            RetroButton(
                                onClick = onPropose,
                                text = "Lamar Cincin",
                                icon = "💍",
                                buttonColor = PixelGold,
                                textColor = Color(0xFF261800),
                                modifier = Modifier.weight(1f),
                                testTag = "propose_btn"
                            )
                        }
                        if (npc.relationshipStage == RelationshipStage.TUNANGAN) {
                            RetroButton(
                                onClick = onHoldWedding,
                                text = "Gelar Pernikahan",
                                icon = "💒",
                                buttonColor = PixelGold,
                                textColor = Color(0xFF261800),
                                modifier = Modifier.weight(1f),
                                testTag = "wedding_btn"
                            )
                        }
                    }
                }

                // Gift Selection Tray
                if (showGiftPicker) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PixelSurface)
                            .border(1.dp, PixelCyan)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Pilih item dari tasmu untuk dihadiahkan:",
                                style = MaterialTheme.typography.labelSmall,
                                color = PixelCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val catalog = GameRepository.getShopCatalog()
                            val giftableItems = player.inventory.filter { (id, count) ->
                                count > 0 && catalog.any { it.id == id && (it.category == "Hadiah" || it.category == "Kebutuhan") }
                            }

                            if (giftableItems.isEmpty()) {
                                Text(
                                    text = "Kamu tidak memiliki item hadiah. Beli di Pasar Swalayan!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PixelTextMuted
                                )
                            } else {
                                giftableItems.forEach { (itemId, count) ->
                                    val item = catalog.find { it.id == itemId }
                                    if (item != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.icon} ${item.name} ($count)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = PixelTextPrimary
                                            )
                                            RetroButton(
                                                onClick = {
                                                    onGiveGift(itemId)
                                                    showGiftPicker = false
                                                },
                                                text = "Berikan",
                                                buttonColor = PixelCyan,
                                                textColor = Color.White,
                                                testTag = "gift_action_$itemId"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                RetroButton(
                    onClick = onDismiss,
                    text = "Tutup Percakapan",
                    buttonColor = PixelSurfaceVariant,
                    textColor = PixelTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "close_npc_dialog"
                )
            }
        }
    }
}
