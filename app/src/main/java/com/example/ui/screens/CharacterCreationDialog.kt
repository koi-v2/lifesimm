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
import androidx.compose.ui.window.Dialog
import com.example.data.model.PlayerAvatar
import com.example.ui.pixel.PixelAvatarCanvas
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun CharacterCreationDialog(
    initialName: String,
    initialAvatar: PlayerAvatar,
    onSave: (String, PlayerAvatar) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var hairStyle by remember { mutableStateOf(initialAvatar.hairStyle) }
    var hairColor by remember { mutableStateOf(initialAvatar.hairColorIndex) }
    var shirtColor by remember { mutableStateOf(initialAvatar.shirtColorIndex) }
    var skinTone by remember { mutableStateOf(initialAvatar.skinToneIndex) }
    var accessory by remember { mutableStateOf(initialAvatar.accessory) }

    val currentAvatar = PlayerAvatar(
        hairStyle = hairStyle,
        hairColorIndex = hairColor,
        shirtColorIndex = shirtColor,
        skinToneIndex = skinTone,
        accessory = accessory
    )

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
                    text = "🎨 KREATOR KARAKTER RETRO",
                    style = MaterialTheme.typography.titleMedium,
                    color = PixelGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar Live Canvas Preview
                PixelAvatarCanvas(
                    avatar = currentAvatar,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Pemain", style = MaterialTheme.typography.labelSmall) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = PixelTextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PixelGold,
                        unfocusedBorderColor = PixelBorderHighlight,
                        focusedContainerColor = PixelSurface,
                        unfocusedContainerColor = PixelSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Customization controls
                RowSelector(
                    label = "Gaya Rambut",
                    currentValue = when (hairStyle) {
                        0 -> "Pendek"
                        1 -> "Bergelombang"
                        2 -> "Cepak"
                        else -> "Kuncir"
                    },
                    onPrev = { hairStyle = (hairStyle + 3) % 4 },
                    onNext = { hairStyle = (hairStyle + 1) % 4 }
                )

                RowSelector(
                    label = "Warna Rambut",
                    currentValue = when (hairColor) {
                        0 -> "Hitam"
                        1 -> "Cokelat"
                        2 -> "Pirang Emas"
                        else -> "Merah Api"
                    },
                    onPrev = { hairColor = (hairColor + 3) % 4 },
                    onNext = { hairColor = (hairColor + 1) % 4 }
                )

                RowSelector(
                    label = "Warna Pakaian",
                    currentValue = when (shirtColor) {
                        0 -> "Merah Retro"
                        1 -> "Biru Elektrik"
                        2 -> "Hijau Botol"
                        else -> "Ungu Mistis"
                    },
                    onPrev = { shirtColor = (shirtColor + 3) % 4 },
                    onNext = { shirtColor = (shirtColor + 1) % 4 }
                )

                RowSelector(
                    label = "Warna Kulit",
                    currentValue = when (skinTone) {
                        0 -> "Terang"
                        1 -> "Kuning Langsat"
                        else -> "Sawo Matang"
                    },
                    onPrev = { skinTone = (skinTone + 2) % 3 },
                    onNext = { skinTone = (skinTone + 1) % 3 }
                )

                RowSelector(
                    label = "Aksesori",
                    currentValue = when (accessory) {
                        0 -> "Tanpa Aksesori"
                        1 -> "Kacamata Pixel"
                        else -> "Topi Petualang"
                    },
                    onPrev = { accessory = (accessory + 2) % 3 },
                    onNext = { accessory = (accessory + 1) % 3 }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RetroButton(
                        onClick = onDismiss,
                        text = "Batal",
                        buttonColor = PixelSurfaceVariant,
                        textColor = PixelTextPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "cancel_char_btn"
                    )
                    RetroButton(
                        onClick = {
                            onSave(name, currentAvatar)
                            onDismiss()
                        },
                        text = "Simpan",
                        buttonColor = PixelGold,
                        textColor = Color(0xFF221500),
                        modifier = Modifier.weight(1f),
                        testTag = "save_char_btn"
                    )
                }
            }
        }
    }
}

@Composable
private fun RowSelector(
    label: String,
    currentValue: String,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(PixelSurface)
            .border(1.dp, PixelBorderDark)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PixelTextSecondary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RetroButton(
                onClick = onPrev,
                text = "<",
                buttonColor = PixelSurfaceVariant,
                textColor = PixelTextPrimary,
                testTag = "prev_${label}"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                color = PixelGold
            )
            Spacer(modifier = Modifier.width(6.dp))
            RetroButton(
                onClick = onNext,
                text = ">",
                buttonColor = PixelSurfaceVariant,
                textColor = PixelTextPrimary,
                testTag = "next_${label}"
            )
        }
    }
}
