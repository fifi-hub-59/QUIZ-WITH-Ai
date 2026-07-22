package com.example.ui.screens

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.Difficulty
import androidx.compose.foundation.clickable
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun HomeScreen(
    progress: UserProgress,
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    onStartGame: (level: Int) -> Unit,
    onEquipAvatar: (Int) -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onWatchAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAvatarSelector by remember { mutableStateOf(false) }

    // Breathing floating button scale
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicDeepSpace, CosmicSurface, CosmicDeepSpace)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. TOP HEADER ROW (Settings & Profile Summary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coins Display Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪙",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "${progress.coins}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                // Title Brand
                Text(
                    text = "LOBBY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicNeonCyan,
                    letterSpacing = 2.sp
                )

                // Settings & Exit Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Settings Gear Button
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onNavigateToSettings()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CosmicSurface)
                            .border(1.dp, CosmicNeonPurple.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = CosmicTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Exit Application Button
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            (context as? Activity)?.finish()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CosmicSurface)
                            .border(1.dp, CosmicNeonMagenta.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Text(
                            text = "🚪",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // 2. CENTRAL HERO CHARACTER & LEVEL CARD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // 16:9 Space Command Center Lobby Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .padding(bottom = 10.dp)
                        .border(1.5.dp, CosmicNeonPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_lobby_banner),
                        contentDescription = "Cosmic Command Center Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Floating AI Opponent avatar with edit/switch overlay
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    AiAvatar(
                        mood = "neutral",
                        avatarId = progress.equippedAvatarId,
                        modifier = Modifier
                            .size(110.dp)
                            .border(2.dp, CosmicNeonCyan, RoundedCornerShape(24.dp))
                            .bounceClick(playSound = false) {
                                SoundManager.playClick()
                                showAvatarSelector = true
                            }
                    )
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            showAvatarSelector = true
                        },
                        modifier = Modifier
                            .offset(x = 8.dp, y = 8.dp)
                            .size(34.dp)
                            .background(CosmicNeonPurple, CircleShape)
                            .border(1.5.dp, CosmicSurface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Switch Avatar",
                            tint = CosmicDeepSpace,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val companionName = when (progress.equippedAvatarId) {
                    0 -> "Core Alpha (Pixel)"
                    1 -> "Chronos-X"
                    2 -> "Optica-9"
                    3 -> "Socrates-v2"
                    4 -> "Midas Bot"
                    5 -> "Aegis Zero"
                    6 -> "Nova Elixir"
                    7 -> "Phoenix"
                    8 -> "Vortex"
                    9 -> "Lotus Prime"
                    10 -> "Shadow.exe"
                    else -> "Pixel"
                }

                Text(
                    text = companionName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicNeonCyan
                )
                Text(
                    text = "Active Companion",
                    fontSize = 12.sp,
                    color = CosmicTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // LEVEL & XP PROGRESS PANEL
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicNeonPurple.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .bounceClick(playSound = false) {
                            SoundManager.playClick()
                            onNavigateToProfile()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Welcome, ${progress.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (progress.isVipActive) Color(0xFFFFD700) else CosmicTextPrimary
                            )
                            if (progress.isVipActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "👑 VIP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEVEL ${progress.level}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicNeonMagenta
                            )
                            Text(
                                text = "${progress.xp} / 100 XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom XP Horizontal Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(CosmicDeepSpace)
                        ) {
                            val fraction = (progress.xp / 100f).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(CosmicNeonMagenta, CosmicNeonPurple)
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // 3. ACTIONS PANEL (Start Level, Resume, Store)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // DIFFICULTY SELECTOR SECTION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECT DIFFICULTY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicTextSecondary,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val difficulties = listOf(
                            Difficulty.EASY to CosmicNeonCyan,
                            Difficulty.MEDIUM to CosmicNeonPurple,
                            Difficulty.HARD to CosmicNeonMagenta
                        )

                        difficulties.forEach { (diff, color) ->
                            val isSelected = selectedDifficulty == diff
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) color.copy(alpha = 0.15f) else CosmicSurface.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) color else CosmicNeonPurple.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .bounceClick(playSound = false) {
                                        SoundManager.playClick()
                                        onDifficultySelected(diff)
                                    }
                                    .testTag("difficulty_${diff.name.lowercase()}_button")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = diff.displayName.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) color else CosmicTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${diff.maxGuesses} guesses",
                                        fontSize = 9.sp,
                                        color = if (isSelected) color.copy(alpha = 0.8f) else CosmicTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // QUICK REWARDED AD ACCESS
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, CosmicNeonCyan.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .bounceClick {
                            onWatchAdClick()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📺", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                            Column {
                                Text(
                                    text = "Watch Sponsored Ad",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicTextPrimary
                                )
                                Text(
                                    text = "Claim +50 Gold Coins instantly!",
                                    fontSize = 10.sp,
                                    color = CosmicNeonCyan
                                )
                            }
                        }
                        Text(
                            text = "CLAIM 🪙",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicNeonCyan
                        )
                    }
                }

                // PRIMARY ACTION: PLAY GAME
                Button(
                    onClick = {
                        SoundManager.playClick()
                        onStartGame(progress.level)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("submit_button")
                        .graphicsLayer {
                            scaleX = buttonScale
                            scaleY = buttonScale
                        },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "🎮 PLAY LEVEL ${progress.level}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicDeepSpace,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECONDARY ACTIONS: VISIT STORE & LEADERBOARD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Store Column (Left Half)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                onNavigateToStore()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C4B4)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("visit_store_button")
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Store",
                                    tint = CosmicDeepSpace,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "SHOP",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CosmicDeepSpace,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // OFF 70% Badge in the corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-8).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE91E63)) // Vibrant pinkish-red badge
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFF 70%",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Leaderboard Column (Right Half)
                    Button(
                        onClick = {
                            SoundManager.playClick()
                            onNavigateToLeaderboard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8126FF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("visit_leaderboard_button")
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Leaderboard",
                                tint = Color.White,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "RANKS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        if (showAvatarSelector) {
            AvatarSelectorDialog(
                progress = progress,
                onEquipAvatar = onEquipAvatar,
                onDismiss = { showAvatarSelector = false },
                onNavigateToStore = onNavigateToStore
            )
        }
    }
}

@Composable
fun AvatarSelectorDialog(
    progress: UserProgress,
    onEquipAvatar: (Int) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToStore: () -> Unit
) {
    val unlockedIds = remember(progress.unlockedAvatarIds) {
        progress.unlockedAvatarIds.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicDeepSpace),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(2.dp, CosmicNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECT COMPANION",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CosmicNeonCyan,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Equipped companions grant powerful passive abilities during your cosmic runs!",
                    fontSize = 11.sp,
                    color = CosmicTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AVATARS) { avatar ->
                        val isOwned = unlockedIds.contains(avatar.id)
                        val isEquipped = progress.equippedAvatarId == avatar.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isEquipped) CosmicSurfaceVariant else CosmicSurface
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isEquipped) 1.5.dp else 1.dp,
                                    color = if (isEquipped) CosmicNeonCyan else CosmicSurfaceVariant,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .bounceClick(enabled = isOwned, playSound = false) {
                                    SoundManager.playClick()
                                    onEquipAvatar(avatar.id)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    AiAvatar(
                                        mood = "neutral",
                                        avatarId = avatar.id,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = avatar.name,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOwned) CosmicTextPrimary else CosmicTextSecondary.copy(alpha = 0.5f),
                                            fontSize = 13.sp
                                        )
                                        if (!isOwned) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "🔒 Locked",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CosmicNeonMagenta.copy(alpha = 0.8f),
                                                modifier = Modifier
                                                    .background(CosmicNeonMagenta.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = avatar.abilityName + ": " + avatar.abilityDesc,
                                        fontSize = 10.sp,
                                        color = if (isOwned) avatar.eyeColor else CosmicTextSecondary.copy(alpha = 0.4f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                if (isEquipped) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Equipped",
                                        tint = CosmicNeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isOwned) {
                                    Text(
                                        text = "EQUIP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicNeonCyan,
                                        modifier = Modifier
                                            .border(1.dp, CosmicNeonCyan, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                } else {
                                    Text(
                                        text = "🪙 ${avatar.price}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTextSecondary.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            SoundManager.playClick()
                            onDismiss()
                            onNavigateToStore()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "GET COMPANIONS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicDeepSpace,
                            letterSpacing = 0.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            SoundManager.playClick()
                            onDismiss()
                        },
                        border = BorderStroke(1.dp, CosmicTextSecondary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CosmicTextPrimary),
                        modifier = Modifier.weight(0.7f)
                    ) {
                        Text(
                            text = "CLOSE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
