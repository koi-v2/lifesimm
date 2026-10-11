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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.NarrativeChoice
import com.example.data.model.NarrativeDilemma
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun NarrativeEventDialog(
    dilemma: NarrativeDilemma,
    onChoose: (NarrativeChoice) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { /* Player must choose or can cancel */ onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        RetroCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            backgroundColor = PixelDarkBg,
            borderColor = PixelWarning
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ DILEMA KEHIDUPAN",
                        style = MaterialTheme.typography.titleMedium,
                        color = PixelWarning
                    )
                    Box(
                        modifier = Modifier
                            .background(PixelWarning.copy(alpha = 0.2f))
                            .border(1.dp, PixelWarning)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dilemma.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelWarning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title & Description
                Text(
                    text = dilemma.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = PixelTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PixelSurface)
                        .border(1.dp, PixelBorderHighlight)
                        .padding(10.dp)
                ) {
                    Text(
                        text = dilemma.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PixelTextSecondary,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PILIHAN KEPUTUSANMU:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelGold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                dilemma.choices.forEachIndexed { index, choice ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(PixelSurfaceVariant)
                            .border(1.dp, PixelBorderDark)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = choice.text,
                            style = MaterialTheme.typography.labelMedium,
                            color = PixelTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Konsekuensi: ${choice.statImpactDesc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PixelGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        RetroButton(
                            onClick = { onChoose(choice) },
                            text = "Pilih Langkah Ini",
                            buttonColor = when (index) {
                                0 -> PixelEmerald
                                1 -> PixelPink
                                else -> PixelCyan
                            },
                            textColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "choice_btn_$index"
                        )
                    }
                }
            }
        }
    }
}
