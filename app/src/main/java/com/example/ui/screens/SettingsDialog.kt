package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun SettingsDialog(
    crtEnabled: Boolean,
    onToggleCrt: () -> Unit,
    onSaveGame: () -> Unit,
    onLoadGame: () -> Unit,
    onResetGame: () -> Unit,
    onOpenCharCreator: () -> Unit,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚙️ PENGATURAN PERMAINAN",
                    style = MaterialTheme.typography.titleMedium,
                    color = PixelGold
                )

                Spacer(modifier = Modifier.height(12.dp))

                RetroButton(
                    onClick = onOpenCharCreator,
                    text = "Ubah Karakter Avatar",
                    icon = "🎨",
                    buttonColor = PixelSurfaceVariant,
                    textColor = PixelTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "open_char_creator_btn"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RetroButton(
                    onClick = onToggleCrt,
                    text = if (crtEnabled) "Efek Layar CRT: AKTIF" else "Efek Layar CRT: NONAKTIF",
                    icon = "📺",
                    buttonColor = if (crtEnabled) PixelEmerald else PixelSurfaceVariant,
                    textColor = if (crtEnabled) Color.Black else PixelTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "toggle_crt_btn"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RetroButton(
                    onClick = onSaveGame,
                    text = "Simpan Permainan (Save Game)",
                    icon = "💾",
                    buttonColor = PixelCyan,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "save_game_btn"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RetroButton(
                    onClick = onLoadGame,
                    text = "Muat Permainan (Load Game)",
                    icon = "📂",
                    buttonColor = PixelGold,
                    textColor = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "load_game_btn"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RetroButton(
                    onClick = onResetGame,
                    text = "Mulai Ulang (Reset Data)",
                    icon = "🔄",
                    buttonColor = PixelStressColor,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "reset_game_btn"
                )

                Spacer(modifier = Modifier.height(14.dp))

                RetroButton(
                    onClick = onDismiss,
                    text = "Tutup Pengaturan",
                    buttonColor = PixelSurfaceVariant,
                    textColor = PixelTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "close_settings_btn"
                )
            }
        }
    }
}
