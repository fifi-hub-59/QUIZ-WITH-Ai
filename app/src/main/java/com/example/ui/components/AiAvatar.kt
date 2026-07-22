package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.*

@Composable
fun AiAvatar(
    mood: String, // "happy", "angry", "neutral", "celebrating"
    modifier: Modifier = Modifier,
    avatarId: Int = 0
) {
    // Infinite transition for breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_offset"
    )

    // Dynamic colors based on Avatar ID
    val baseGradientColors = when (avatarId) {
        0 -> listOf(Color(0xFF2C3E50), Color(0xFF3498DB)) // Core Alpha: Classic slate blue
        1 -> listOf(Color(0xFF3F2B96), Color(0xFFA8C0FF)) // Chronos-X: Deep neon violet
        2 -> listOf(Color(0xFF000428), Color(0xFF004E92)) // Optica-9: Deep space sonar blue
        3 -> listOf(Color(0xFF4B0082), Color(0xFFEE82EE)) // Socrates-v2: Mystic purple neon
        4 -> listOf(Color(0xFFBF953F), Color(0xFFFCF6BA)) // Midas Bot: Rich metallic gold
        5 -> listOf(Color(0xFF0F2027), Color(0xFF203A43)) // Aegis Zero: Armored carbon dark green
        6 -> listOf(Color(0xFFF000FF), Color(0xFF7B1FA2)) // Nova Elixir: Radiant star magenta
        7 -> listOf(Color(0xFFED213A), Color(0xFF93291E)) // Phoenix: Fiery cosmic solar red
        8 -> listOf(Color(0xFF1F4037), Color(0xFF99F2C8)) // Vortex: Toxic emerald kinetic green
        9 -> listOf(Color(0xFF1D976C), Color(0xFF93F9B9)) // Lotus Prime: Balanced transcendental teal
        10 -> listOf(Color(0xFF000000), Color(0xFF243B55)) // Shadow.exe: Stealth operations charcoal black
        else -> listOf(Color(0xFF2C3E50), Color(0xFF3498DB))
    }

    val themeEyeColor = when (avatarId) {
        0 -> CosmicNeonCyan
        1 -> CosmicNeonMagenta
        2 -> Color(0xFF39FF14) // Neon Green
        3 -> Color(0xFFDFFF00) // Neon Yellow
        4 -> Color(0xFFFFD700) // Gold Yellow
        5 -> Color(0xFF00FFFF) // Electric Cyan
        6 -> Color(0xFFEEFF41) // Lime Yellow
        7 -> Color(0xFFFF4500) // Bright Orange
        8 -> Color(0xFFE040FB) // Electric Purple
        9 -> Color(0xFF00E676) // Spring Green
        10 -> Color(0xFF00E5FF) // Cyber Cyan
        else -> CosmicNeonCyan
    }

    // Eye color transitions based on mood
    val eyeColor by animateColorAsState(
        targetValue = when (mood) {
            "happy" -> themeEyeColor
            "angry" -> Color(0xFFFF1744) // Always red for angry
            "celebrating" -> themeEyeColor
            else -> themeEyeColor
        },
        animationSpec = tween(500),
        label = "eye_color"
    )

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(baseGradientColors))
            .border(2.dp, themeEyeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (avatarId == 7) {
            Image(
                painter = painterResource(id = R.drawable.avatar_cyber_astronaut),
                contentDescription = "Cyber Astronaut Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val w = size.width
            val h = size.height

            // 1. Draw head/screen casing
            drawRoundRect(
                color = CosmicSurfaceVariant.copy(alpha = 0.85f),
                topLeft = Offset(2f, 2f + breathingOffset),
                size = Size(w - 4f, h - 4f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Inner glass bezel
            drawRoundRect(
                color = CosmicDeepSpace,
                topLeft = Offset(8f, 8f + breathingOffset),
                size = Size(w - 16f, h - 16f),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // 2. Draw avatar-specific cybernetic ornaments on the bezel
            when (avatarId) {
                1 -> { // Chronos-X (Clock/Horns)
                    drawLine(color = themeEyeColor.copy(alpha = 0.6f), start = Offset(w * 0.5f, h * 0.2f), end = Offset(w * 0.5f, h * 0.35f), strokeWidth = 3f)
                    drawLine(color = themeEyeColor.copy(alpha = 0.6f), start = Offset(w * 0.5f, h * 0.35f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = 3f)
                }
                2 -> { // Optica-9 (Grid / Radar lines)
                    drawRect(color = themeEyeColor.copy(alpha = 0.15f), topLeft = Offset(12f, 12f + breathingOffset), size = Size(w - 24f, h - 24f))
                    drawLine(color = themeEyeColor.copy(alpha = 0.3f), start = Offset(w * 0.5f, 12f), end = Offset(w * 0.5f, h - 12f), strokeWidth = 1f)
                    drawLine(color = themeEyeColor.copy(alpha = 0.3f), start = Offset(12f, h * 0.5f), end = Offset(w - 12f, h * 0.5f), strokeWidth = 1f)
                }
                3 -> { // Socrates-v2 (Circuit nodes)
                    drawCircle(color = themeEyeColor.copy(alpha = 0.4f), radius = 3f, center = Offset(w * 0.5f, h * 0.2f))
                    drawLine(color = themeEyeColor.copy(alpha = 0.3f), start = Offset(w * 0.5f, h * 0.2f), end = Offset(w * 0.3f, h * 0.3f), strokeWidth = 2f)
                    drawLine(color = themeEyeColor.copy(alpha = 0.3f), start = Offset(w * 0.5f, h * 0.2f), end = Offset(w * 0.7f, h * 0.3f), strokeWidth = 2f)
                }
                4 -> { // Midas Bot (Gold Crown/Dots)
                    drawCircle(color = Color(0xFFFFD700), radius = 4f, center = Offset(w * 0.25f, h * 0.2f))
                    drawCircle(color = Color(0xFFFFD700), radius = 5f, center = Offset(w * 0.50f, h * 0.15f))
                    drawCircle(color = Color(0xFFFFD700), radius = 4f, center = Offset(w * 0.75f, h * 0.2f))
                }
                5 -> { // Aegis Zero (Shield Visor)
                    val path = Path().apply {
                        moveTo(w * 0.2f, h * 0.3f)
                        lineTo(w * 0.8f, h * 0.3f)
                        lineTo(w * 0.7f, h * 0.5f)
                        lineTo(w * 0.3f, h * 0.5f)
                        close()
                    }
                    drawPath(path = path, color = themeEyeColor.copy(alpha = 0.15f))
                }
                6 -> { // Nova Elixir (Star ears / side wings)
                    drawLine(color = themeEyeColor, start = Offset(0f, h * 0.5f), end = Offset(-6f, h * 0.3f), strokeWidth = 4f)
                    drawLine(color = themeEyeColor, start = Offset(w, h * 0.5f), end = Offset(w + 6f, h * 0.3f), strokeWidth = 4f)
                }
                7 -> { // Phoenix (Flame crest)
                    val path = Path().apply {
                        moveTo(w * 0.4f, 0f)
                        quadraticTo(w * 0.5f, -10f, w * 0.6f, 0f)
                        quadraticTo(w * 0.5f, -3f, w * 0.4f, 0f)
                    }
                    drawPath(path = path, color = themeEyeColor)
                }
                10 -> { // Shadow.exe (Ninja mask lines)
                    drawLine(color = Color.White.copy(alpha = 0.15f), start = Offset(12f, h * 0.55f), end = Offset(w - 12f, h * 0.55f), strokeWidth = 4f)
                }
            }

            // 3. Draw Eyes depending on mood
            val eyeRadius = 6f
            val leftEyeCenter = Offset(w * 0.35f, h * 0.45f + breathingOffset)
            val rightEyeCenter = Offset(w * 0.65f, h * 0.45f + breathingOffset)

            when (mood) {
                "happy" -> {
                    // Happy curved eyes (arcs)
                    drawArc(
                        color = eyeColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(leftEyeCenter.x - 8f, leftEyeCenter.y - 6f),
                        size = Size(16f, 12f),
                        style = Stroke(width = 4f)
                    )
                    drawArc(
                        color = eyeColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(rightEyeCenter.x - 8f, rightEyeCenter.y - 6f),
                        size = Size(16f, 12f),
                        style = Stroke(width = 4f)
                    )
                }
                "angry" -> {
                    // Angry diagonal glowing slits
                    drawLine(
                        color = eyeColor,
                        start = Offset(leftEyeCenter.x - 10f, leftEyeCenter.y - 4f),
                        end = Offset(leftEyeCenter.x + 8f, leftEyeCenter.y + 4f),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = eyeColor,
                        start = Offset(rightEyeCenter.x + 10f, rightEyeCenter.y - 4f),
                        end = Offset(rightEyeCenter.x - 8f, rightEyeCenter.y + 4f),
                        strokeWidth = 5f
                    )
                }
                "celebrating" -> {
                    // Sparkling/star eyes (X's)
                    drawLine(
                        color = eyeColor,
                        start = Offset(leftEyeCenter.x - 6f, leftEyeCenter.y - 6f),
                        end = Offset(leftEyeCenter.x + 6f, leftEyeCenter.y + 6f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = eyeColor,
                        start = Offset(leftEyeCenter.x + 6f, leftEyeCenter.y - 6f),
                        end = Offset(leftEyeCenter.x - 6f, leftEyeCenter.y + 6f),
                        strokeWidth = 4f
                    )

                    drawLine(
                        color = eyeColor,
                        start = Offset(rightEyeCenter.x - 6f, rightEyeCenter.y - 6f),
                        end = Offset(rightEyeCenter.x + 6f, rightEyeCenter.y + 6f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = eyeColor,
                        start = Offset(rightEyeCenter.x + 6f, rightEyeCenter.y - 6f),
                        end = Offset(rightEyeCenter.x - 6f, rightEyeCenter.y + 6f),
                        strokeWidth = 4f
                    )
                }
                else -> {
                    // Neutral: Standard circular glowing eyes
                    drawCircle(color = eyeColor, radius = eyeRadius, center = leftEyeCenter)
                    drawCircle(color = eyeColor, radius = eyeRadius, center = rightEyeCenter)
                }
            }

            // 4. Draw Mouth depending on mood
            val mouthCenter = Offset(w * 0.5f, h * 0.72f + breathingOffset)
            when (mood) {
                "happy" -> {
                    // Curved smile
                    drawArc(
                        color = eyeColor,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(mouthCenter.x - 12f, mouthCenter.y - 6f),
                        size = Size(24f, 12f),
                        style = Stroke(width = 3f)
                    )
                }
                "angry" -> {
                    // Frown/grimace
                    drawArc(
                        color = eyeColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(mouthCenter.x - 10f, mouthCenter.y),
                        size = Size(20f, 10f),
                        style = Stroke(width = 3f)
                    )
                }
                "celebrating" -> {
                    // Open mouth (O shape)
                    drawCircle(
                        color = eyeColor,
                        radius = 6f,
                        center = mouthCenter,
                        style = Stroke(width = 3.5f)
                    )
                }
                else -> {
                    // Flat line
                    drawLine(
                        color = CosmicTextSecondary.copy(alpha = 0.8f),
                        start = Offset(mouthCenter.x - 10f, mouthCenter.y),
                        end = Offset(mouthCenter.x + 10f, mouthCenter.y),
                        strokeWidth = 3f
                    )
                }
            }

            // Decorate top with neat antenna (unless Lotus Prime or Phoenix)
            if (avatarId != 9 && avatarId != 7) {
                drawLine(
                    color = themeEyeColor.copy(alpha = 0.5f),
                    start = Offset(w * 0.5f, 0f + breathingOffset),
                    end = Offset(w * 0.5f, -4f + breathingOffset),
                    strokeWidth = 3f
                )
                drawCircle(
                    color = eyeColor,
                    radius = 3f,
                    center = Offset(w * 0.5f, -6f + breathingOffset)
                )
            }
            }
        }
    }
}
