package com.example.ui.pixel

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerAvatar
import com.example.ui.theme.*

@Composable
fun RetroCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PixelSurface,
    borderColor: Color = PixelBorderHighlight,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .border(2.dp, PixelBorderDark)
            .padding(2.dp)
            .border(2.dp, borderColor)
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun RetroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: String? = null,
    buttonColor: Color = PixelGold,
    textColor: Color = Color(0xFF1B1100),
    enabled: Boolean = true,
    testTag: String = "retro_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val offsetY = if (isPressed) 2.dp else 0.dp
    val shadowColor = if (enabled) Color(0xFF100926) else Color(0xFF22173E)
    val actualBg = if (enabled) buttonColor else Color(0xFF3B2F5C)
    val actualText = if (enabled) textColor else Color(0xFF7B6E9C)

    Box(
        modifier = modifier
            .padding(bottom = 2.dp)
            .testTag(testTag)
            .offset(y = offsetY)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .border(2.dp, PixelBorderDark)
            .padding(2.dp)
            .border(1.dp, if (enabled) Color.White.copy(alpha = 0.4f) else Color.Transparent)
            .background(actualBg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = actualText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PixelStatBar(
    label: String,
    currentValue: Int,
    maxValue: Int = 100,
    barColor: Color,
    icon: String,
    modifier: Modifier = Modifier,
    isWarningHigh: Boolean = false // e.g. Stress: high is dangerous
) {
    val progress = (currentValue.coerceIn(0, maxValue).toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    val totalBlocks = 10
    val activeBlocks = (progress * totalBlocks).toInt()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 13.sp, modifier = Modifier.padding(end = 4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
            }
            Text(
                text = "$currentValue%",
                style = MaterialTheme.typography.labelSmall,
                color = if (isWarningHigh && currentValue > 75) PixelStressColor else PixelTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        // Pixel block container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(PixelBorderDark)
                .border(1.dp, PixelBorderHighlight)
                .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            for (i in 0 until totalBlocks) {
                val isActive = i < activeBlocks
                val color = if (isActive) {
                    if (isWarningHigh && i >= 7) PixelStressColor else barColor
                } else {
                    Color(0xFF1B1433)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun PixelAvatarCanvas(
    avatar: PlayerAvatar,
    modifier: Modifier = Modifier.size(64.dp)
) {
    val skinColor = when (avatar.skinToneIndex) {
        0 -> Color(0xFFFFDBAC)
        1 -> Color(0xFFF1C27D)
        else -> Color(0xFFC68642)
    }

    val hairColor = when (avatar.hairColorIndex) {
        0 -> Color(0xFF1A1A1A)
        1 -> Color(0xFF5A3825)
        2 -> Color(0xFFE5C158)
        else -> Color(0xFFB73225)
    }

    val shirtColor = when (avatar.shirtColorIndex) {
        0 -> Color(0xFFD62246)
        1 -> Color(0xFF1E88E5)
        2 -> Color(0xFF43A047)
        else -> Color(0xFF8E24AA)
    }

    Canvas(modifier = modifier.border(2.dp, PixelGold)) {
        val gridSize = 16
        val pixelWidth = size.width / gridSize
        val pixelHeight = size.height / gridSize

        // Background
        drawRect(Color(0xFF191230), size = size)

        fun drawP(x: Int, y: Int, color: Color) {
            drawRect(
                color = color,
                topLeft = Offset(x * pixelWidth, y * pixelHeight),
                size = Size(pixelWidth, pixelHeight)
            )
        }

        // Hair Base
        for (x in 4..11) {
            for (y in 2..5) {
                drawP(x, y, hairColor)
            }
        }
        // Hair style variations
        if (avatar.hairStyle == 1) { // wavy / longer
            for (y in 5..9) {
                drawP(3, y, hairColor)
                drawP(12, y, hairColor)
            }
        } else if (avatar.hairStyle == 3) { // ponytail / kuncir
            drawP(3, 1, hairColor)
            drawP(2, 2, hairColor)
            drawP(2, 3, hairColor)
        }

        // Face
        for (x in 5..10) {
            for (y in 6..9) {
                drawP(x, y, skinColor)
            }
        }
        // Eyes
        drawP(6, 7, Color.Black)
        drawP(9, 7, Color.Black)
        // Blush / cheeks
        drawP(5, 8, Color(0xFFFF8DA1))
        drawP(10, 8, Color(0xFFFF8DA1))
        // Mouth
        drawP(7, 8, Color(0xFF8D4448))
        drawP(8, 8, Color(0xFF8D4448))

        // Neck
        drawP(7, 10, skinColor)
        drawP(8, 10, skinColor)

        // Shirt / Torso
        for (x in 4..11) {
            for (y in 11..14) {
                drawP(x, y, shirtColor)
            }
        }
        // Collar / detail
        drawP(7, 11, Color.White)
        drawP(8, 11, Color.White)

        // Accessory: Glasses or Hat
        if (avatar.accessory == 1) { // Kacamata
            drawP(5, 7, Color(0xFF00E5FF))
            drawP(6, 7, Color(0xFF00E5FF))
            drawP(7, 7, Color.Black)
            drawP(8, 7, Color.Black)
            drawP(9, 7, Color(0xFF00E5FF))
            drawP(10, 7, Color(0xFF00E5FF))
        } else if (avatar.accessory == 2) { // Topi
            for (x in 3..12) {
                drawP(x, 2, Color(0xFFFF9800))
            }
            for (x in 5..10) {
                drawP(x, 1, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun CRTScanlineOverlay(
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (!enabled) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val lineHeight = 3.dp.toPx()
        val totalLines = (size.height / lineHeight).toInt()

        for (i in 0 until totalLines step 2) {
            drawRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = Offset(0f, i * lineHeight),
                size = Size(size.width, lineHeight * 0.8f)
            )
        }
    }
}

@Composable
fun PixelHeartBar(
    hearts: Int,
    maxHearts: Int = 200,
    modifier: Modifier = Modifier
) {
    val totalHeartIcons = 5
    val currentRatio = (hearts.toFloat() / maxHearts).coerceIn(0f, 1f)
    val filledIcons = (currentRatio * totalHeartIcons).toInt()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 0 until totalHeartIcons) {
            val isFilled = i < filledIcons
            Text(
                text = if (isFilled) "❤️" else "🖤",
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$hearts LP",
            style = MaterialTheme.typography.labelSmall,
            color = PixelPink
        )
    }
}
