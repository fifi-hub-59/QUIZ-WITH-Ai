package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.QuizImage
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.components.bounceClick
import com.example.ui.theme.*

@Composable
fun ResultScreen(
    progress: UserProgress,
    gameOverState: String?, // "WIN", "LOSE_STREAK", "LOSE_TIME", "LOSE_GUESSES"
    currentImage: QuizImage?,
    currentQuestionIndex: Int,
    timerSecondsLeft: Int,
    correctStreak: Int,
    onClaimRewards: () -> Unit,
    onRetry: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWin = gameOverState == "WIN"
    val equippedAvatarId = progress.equippedAvatarId

    // Play victory or defeat sounds once when the screen opens
    LaunchedEffect(gameOverState) {
        if (isWin) {
            SoundManager.playCelebration()
        } else {
            SoundManager.playGameOver()
        }
    }

    // Animation values
    val scaleAnim = remember { Animatable(0.7f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isWin) {
                        listOf(Color(0xFF0D1B2A), CosmicDeepSpace, Color(0xFF0F2027))
                    } else {
                        listOf(Color(0xFF2D1120), CosmicDeepSpace, Color(0xFF1F1C1F))
                    }
                )
            )
            .testTag("result_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. HEADER BANNER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = if (isWin) "RUN ANALYSIS: SUCCESS" else "RUN ANALYSIS: COMPROMISED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isWin) CosmicNeonCyan else CosmicNeonMagenta,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isWin) "DECODING COMPLETE" else "DECODING FAILED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isWin) Color(0xFF00FFCC) else Color(0xFFFF1744),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 2. REVEALED SUBJECT CARD
            if (currentImage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .border(
                            width = 2.dp,
                            color = if (isWin) Color(0xFF00FFCC).copy(alpha = 0.5f) else Color(0xFFFF1744).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Full clear image (100% revealed) if won, or 60% blurred if lost
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentImage.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Revealed Subject Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(CosmicSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "📷 Visual Offline",
                                        color = CosmicTextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )

                        // Bottom gradient scrim for text legibility
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                        )

                        // Label info
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "SUBJECT REVEALED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicNeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentImage.name.uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 3. STATISTICS SUMMARY
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DIAGNOSTIC MATRIX",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(label = "Solved Nodes", value = "${currentQuestionIndex}/20", icon = "🧩")
                        StatItem(label = "Max Streak", value = "$correctStreak", icon = "🔥")
                        StatItem(label = "Time Left", value = "${timerSecondsLeft}s", icon = "⏳")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = CosmicTextSecondary.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dialog Commentary from Pixel
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AiAvatar(
                            mood = if (isWin) "celebrating" else "angry",
                            avatarId = equippedAvatarId,
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = if (isWin) "Pixel is astonished!" else "Pixel is gloating!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWin) CosmicNeonCyan else CosmicNeonMagenta
                            )
                            Text(
                                text = if (isWin) {
                                    "Remarkable speed. You bypassed my encryption and decoded the secret fields!"
                                } else {
                                    "The cognitive grid was too intense. Try upgrading shields at the store!"
                                },
                                fontSize = 12.sp,
                                color = CosmicTextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 4. ACTION BUTTONS (Claim or Retry)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isWin) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF00FFCC))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .bounceClick {
                                onClaimRewards()
                            }
                            .testTag("submit_button")
                    ) {
                        Text(
                            text = "CLAIM LEVEL REWARDS 🎁",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicDeepSpace,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CosmicNeonMagenta)
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .bounceClick {
                                onRetry()
                            }
                            .testTag("submit_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = CosmicDeepSpace,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "RETRY COGNITIVE RUN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicDeepSpace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, CosmicTextSecondary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .bounceClick {
                            onExit()
                        }
                ) {
                    Text(
                        text = "RETURN TO LOBBY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = CosmicTextPrimary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = CosmicTextSecondary
        )
    }
}
