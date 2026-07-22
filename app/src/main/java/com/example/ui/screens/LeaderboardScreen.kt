package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.theme.*

data class LeaderboardEntry(
    val name: String,
    val level: Int,
    val xp: Int,
    val isMe: Boolean = false,
    val avatarEmoji: String = "👽"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    progress: UserProgress,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Build list of static players plus the current user dynamically
    val leaderboardEntries = remember(progress.level, progress.xp, progress.name) {
        val list = mutableListOf(
            LeaderboardEntry("StarLord 🚀", 12, 80, avatarEmoji = "👨‍🎤"),
            LeaderboardEntry("CyberKnight 🤖", 8, 40, avatarEmoji = "🤖"),
            LeaderboardEntry("Nebula 🪐", 5, 90, avatarEmoji = "👩‍🚀"),
            LeaderboardEntry("PixelAI 👾", 3, 20, avatarEmoji = "👾"),
            LeaderboardEntry("AstroBoy ✨", 2, 70, avatarEmoji = "🧑‍🚀"),
            LeaderboardEntry("CosmicDust ☄️", 1, 50, avatarEmoji = "👽"),
            LeaderboardEntry("${progress.name} (You)", progress.level, progress.xp, isMe = true, avatarEmoji = "👑")
        )
        // Sort entries by calculated score: level * 100 + xp in descending order
        list.sortByDescending { it.level * 100 + it.xp }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "COSMIC LEADERBOARD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicNeonCyan,
                        letterSpacing = 1.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onBackClick()
                        },
                        modifier = Modifier.testTag("leaderboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CosmicTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CosmicDeepSpace,
                    titleContentColor = CosmicTextPrimary
                )
            )
        },
        containerColor = CosmicDeepSpace,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CosmicDeepSpace, CosmicSurface, CosmicDeepSpace)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Podium Section for Top 3
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicNeonPurple.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 2nd Place
                            if (leaderboardEntries.size > 1) {
                                PodiumItem(
                                    entry = leaderboardEntries[1],
                                    rank = 2,
                                    height = 70.dp,
                                    medalEmoji = "🥈"
                                )
                            }

                            // 1st Place
                            if (leaderboardEntries.isNotEmpty()) {
                                PodiumItem(
                                    entry = leaderboardEntries[0],
                                    rank = 1,
                                    height = 100.dp,
                                    medalEmoji = "🥇"
                                )
                            }

                            // 3rd Place
                            if (leaderboardEntries.size > 2) {
                                PodiumItem(
                                    entry = leaderboardEntries[2],
                                    rank = 3,
                                    height = 60.dp,
                                    medalEmoji = "🥉"
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "🏆 ALL ACTIVE CONTENDERS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    letterSpacing = 1.sp
                )

                // Main Leaderboard List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(leaderboardEntries.mapIndexed { index, entry -> Pair(index + 1, entry) }) { (rank, entry) ->
                        val itemBorderColor = if (entry.isMe) CosmicNeonCyan else Color.Transparent
                        val itemBgColor = if (entry.isMe) CosmicSurfaceVariant else CosmicSurface

                        Card(
                            colors = CardDefaults.cardColors(containerColor = itemBgColor),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (entry.isMe) CosmicNeonCyan else CosmicNeonPurple.copy(alpha = 0.1f),
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Rank Badge
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (rank) {
                                                    1 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                                    2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                                    3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                                    else -> CosmicDeepSpace
                                                }
                                            )
                                    ) {
                                        Text(
                                            text = when (rank) {
                                                1 -> "🥇"
                                                2 -> "🥈"
                                                3 -> "🥉"
                                                else -> "#$rank"
                                            },
                                            fontSize = if (rank <= 3) 14.sp else 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTextPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Avatar
                                    Text(
                                        text = entry.avatarEmoji,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    // Name
                                    Text(
                                        text = entry.name,
                                        fontSize = 15.sp,
                                        fontWeight = if (entry.isMe) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (entry.isMe) CosmicNeonCyan else CosmicTextPrimary,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // Level Badge
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = CosmicDeepSpace),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Lvl ${entry.level}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicNeonPurple,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // XP Text
                                    Text(
                                        text = "${entry.xp} XP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CosmicTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumItem(
    entry: LeaderboardEntry,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    medalEmoji: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "podium_bounce")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (rank == 1) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "podium_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        // Player Avatar with Medal in corner
        Box(contentAlignment = Alignment.Center) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(CosmicDeepSpace)
                    .border(
                        2.dp,
                        when (rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            else -> Color(0xFFCD7F32)
                        },
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Text(text = entry.avatarEmoji, fontSize = 28.sp)
            }
            // Medal Emoji Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(20.dp)
            ) {
                Text(text = medalEmoji, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Username
        Text(
            text = entry.name.split(" ")[0],
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (entry.isMe) CosmicNeonCyan else CosmicTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Level
        Text(
            text = "Lvl ${entry.level}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = CosmicNeonPurple,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Physical Podium Block
        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (rank) {
                    1 -> Color(0xFFFFD700).copy(alpha = 0.15f)
                    2 -> Color(0xFFC0C0C0).copy(alpha = 0.15f)
                    else -> Color(0xFFCD7F32).copy(alpha = 0.15f)
                }
            ),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .border(
                    1.dp,
                    when (rank) {
                        1 -> Color(0xFFFFD700).copy(alpha = 0.4f)
                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.4f)
                        else -> Color(0xFFCD7F32).copy(alpha = 0.4f)
                    },
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = when (rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        else -> Color(0xFFCD7F32)
                    }
                )
            }
        }
    }
}
