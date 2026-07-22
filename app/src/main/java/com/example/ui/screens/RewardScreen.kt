package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.SoundManager
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RewardScreen(
    isVip: Boolean,
    equippedAvatarId: Int,
    onClaimSuccess: (coinsEarned: Int, shieldsEarned: Int, hintsEarned: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var lootPhase by remember { mutableStateOf("sealed") } // "sealed", "unlocking", "revealed"
    
    // Core rewards
    val baseCoins = if (isVip) 40 else 20
    val finalCoins = if (equippedAvatarId == 4) baseCoins * 2 else baseCoins // Midas Bot doubles coins
    val extraShield = remember { if (Math.random() < 0.4) 1 else 0 }
    val extraHint = remember { if (Math.random() < 0.5) 1 else 0 }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "chest_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chest_float"
    )

    val scaleChest by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chest_scale"
    )

    // Radiant background glow rotation
    val glowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_rotation"
    )

    LaunchedEffect(lootPhase) {
        if (lootPhase == "unlocking") {
            delay(1500) // spin time
            lootPhase = "revealed"
            SoundManager.playCelebration()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0E17), CosmicDeepSpace, Color(0xFF161426))
                )
            )
            .testTag("reward_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = "MYSTERY QUANTUM VAULT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicNeonCyan,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lootPhase == "revealed") "REWARDS RETRIEVED" else "UNAUTHORIZED SECURE CHEST",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Central Chest / Loot Box Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Background Rotating Sunburst Glow (Only active when sealed/unlocking)
                if (lootPhase != "revealed") {
                    Canvas(
                        modifier = Modifier
                            .size(240.dp)
                            .graphicsLayer { rotationZ = glowRotation }
                    ) {
                        val brush = Brush.radialGradient(
                            colors = listOf(CosmicNeonPurple.copy(alpha = 0.4f), Color.Transparent)
                        )
                        drawCircle(brush = brush, radius = size.minDimension / 1.5f)
                    }
                }

                when (lootPhase) {
                    "sealed" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .bounceClick(playSound = false) {
                                    SoundManager.playClick()
                                    lootPhase = "unlocking"
                                }
                                .graphicsLayer {
                                    translationY = floatOffset
                                    scaleX = scaleChest
                                    scaleY = scaleChest
                                }
                        ) {
                            // High energy glowing lockbox image
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700).copy(alpha = 0.1f))
                                    .border(2.dp, Color(0xFFFFD700).copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.reward_chest),
                                    contentDescription = "Cosmic Cargo Chest",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "TAP TO SECURE PAYLOAD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700),
                                letterSpacing = 2.sp,
                                modifier = Modifier
                                    .background(Color(0xFFFFD700).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                    "unlocking" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = CosmicNeonCyan,
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "EXTRACTING DATA VECTORS...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicNeonCyan,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                    "revealed" -> {
                        // Display the Reward Cards list
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "VAULT SECURED SUCCESSFULLY",
                                fontSize = 11.sp,
                                color = CosmicNeonCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Gold Coin Card
                                RewardPayloadCard(
                                    emoji = "🪙",
                                    amount = "+$finalCoins",
                                    label = "Gold Coins",
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.weight(1f)
                                )

                                if (extraShield > 0) {
                                    RewardPayloadCard(
                                        emoji = "🛡️",
                                        amount = "+1",
                                        label = "Shield Matrix",
                                        color = Color(0xFF00E5FF),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (extraHint > 0) {
                                    RewardPayloadCard(
                                        emoji = "🔍",
                                        amount = "+1",
                                        label = "Eliminator Hint",
                                        color = Color(0xFFDFFF00),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Claim action button
            val claimEnabled = lootPhase == "revealed"
            val buttonBg = if (claimEnabled) CosmicNeonCyan else CosmicSurfaceVariant.copy(alpha = 0.5f)
            val buttonBorder = if (claimEnabled) Color.White.copy(alpha = 0.3f) else Color.Transparent
            val textCol = if (claimEnabled) CosmicDeepSpace else CosmicTextSecondary.copy(alpha = 0.5f)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(buttonBg)
                    .border(1.dp, buttonBorder, RoundedCornerShape(14.dp))
                    .bounceClick(enabled = claimEnabled) {
                        onClaimSuccess(finalCoins, extraShield, extraHint)
                    }
                    .testTag("submit_button")
            ) {
                Text(
                    text = if (lootPhase == "revealed") "INTEGRATE TO INVENTORY" else "AWAITING UNLOCK",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = textCol,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun RewardPayloadCard(
    emoji: String,
    amount: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.border(1.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = amount,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
