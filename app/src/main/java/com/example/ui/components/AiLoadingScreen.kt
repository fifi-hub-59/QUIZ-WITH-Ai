package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SoundManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AiLoadingScreen(
    equippedAvatarId: Int,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotating animation state
    val infiniteTransition = rememberInfiniteTransition(label = "ai_loader")
    
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Cycling status messages
    val statusMessages = listOf(
        "Initiating neural-link connection...",
        "Consulting the Gemini cosmic archives...",
        "Quantum-rendering mystery illustrations...",
        "Drafting hyper-intelligent visual riddles...",
        "Calibrating neural difficulty matrix...",
        "Engaging visual clarity shrouds...",
        "Populating answer grid with diversions..."
    )

    var currentStatusIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentStatusIndex = (currentStatusIndex + 1) % statusMessages.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicDeepSpace, CosmicSurface, CosmicDeepSpace)
                )
            )
    ) {
        // TOP CONTROLS (Back/Exit button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    SoundManager.playClick()
                    onExitClick()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("exit_loading_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cancel AI Generation",
                    tint = CosmicTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CONSTRUCTING SESSION",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.5.sp
            )
        }

        // MAIN CENTER CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rotating & Pulsing custom neural scanner
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Static Outer Ring
                    drawCircle(
                        color = CosmicNeonPurple.copy(alpha = 0.15f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    
                    // Rotating Inner Scanner
                    val scannerRadius = size.minDimension / 2.3f
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    drawArc(
                        color = CosmicNeonCyan,
                        startAngle = rotationAngle,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    
                    drawArc(
                        color = CosmicNeonMagenta,
                        startAngle = rotationAngle + 180f,
                        sweepAngle = 60f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // AI Avatar sitting in the center with breathing scale
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(CosmicSurface)
                        .border(2.dp, CosmicNeonPurple, CircleShape)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AiAvatar(
                        mood = "neutral",
                        avatarId = equippedAvatarId,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Spech bubble styled AI status box
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = CosmicNeonCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AnimatedContent(
                        targetState = statusMessages[currentStatusIndex],
                        transitionSpec = {
                            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                        },
                        label = "loading_status"
                    ) { message ->
                        Text(
                            text = message,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = CosmicTextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "The Gemini AI engine is currently composing your level content dynamically. This ensures a 100% unique experience!",
                        fontSize = 11.sp,
                        color = CosmicTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
