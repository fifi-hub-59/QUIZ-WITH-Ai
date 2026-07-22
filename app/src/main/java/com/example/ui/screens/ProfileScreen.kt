package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    progress: UserProgress,
    onSaveName: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(progress.name) }

    // Extract stats
    val totalPlayedImages = remember(progress.playedImageIds) {
        if (progress.playedImageIds.isBlank()) 0 
        else progress.playedImageIds.split(",").filter { it.isNotBlank() }.size
    }

    val claimedMilestones = remember(progress.claimedMilestones) {
        progress.claimedMilestones.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PLAYER IDENTITY",
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
                            onBack()
                        },
                        modifier = Modifier.testTag("back_button")
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // 1. TOP HEADER PANEL (Avatar & Name Edit)
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Companion Avatar representation
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, CosmicNeonCyan, RoundedCornerShape(16.dp))
                    ) {
                        AiAvatar(
                            mood = "neutral",
                            avatarId = progress.equippedAvatarId,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = progress.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = {
                                    SoundManager.playClick()
                                    tempName = progress.name
                                    showEditNameDialog = true
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("edit_name_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Name",
                                    tint = CosmicNeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Text(
                            text = if (progress.isVipActive) "👑 LIFETIME COSMIC VIP" else "Standard Player Account",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (progress.isVipActive) Color(0xFFFFD700) else CosmicTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mini Level Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "LVL ${progress.level}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CosmicNeonMagenta
                            )
                            Text(
                                text = "${progress.xp}/100 XP",
                                fontSize = 11.sp,
                                color = CosmicTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(CosmicDeepSpace)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress.xp / 100f)
                                    .background(CosmicNeonMagenta)
                            )
                        }
                    }
                }
            }

            // 2. STATISTICS GRID (2x2 Grid)
            Text(
                text = "STATISTICS & HOLDINGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Gold Coins",
                    value = "🪙 ${progress.coins}",
                    description = "Used to buy power-ups",
                    color = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Visual Realms",
                    value = "🧩 $totalPlayedImages",
                    description = "Total photos decoded",
                    color = CosmicNeonCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Safety Shields",
                    value = "🛡️ ${progress.shieldsCount}",
                    description = "Protects level answers",
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "System Hints",
                    value = "🔍 ${progress.hintsCount}",
                    description = "Hides wrong choices",
                    color = Color(0xFFDFFF00),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. MILESTONE BADGES SECTION
            Text(
                text = "MILESTONE ACHIEVEMENTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val milestones = listOf(
                        Triple(2, "🥉", "Novice"),
                        Triple(3, "🥈", "Decoder"),
                        Triple(5, "🥇", "Adept"),
                        Triple(7, "💎", "Sage"),
                        Triple(10, "👑", "God")
                    )

                    milestones.forEach { (level, emoji, name) ->
                        val isUnlocked = progress.level >= level
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(if (isUnlocked) 1f else 0.35f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) CosmicNeonPurple.copy(alpha = 0.2f)
                                        else CosmicDeepSpace
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isUnlocked) CosmicNeonCyan else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) CosmicTextPrimary else CosmicTextSecondary
                            )
                            Text(
                                text = "Lvl $level",
                                fontSize = 8.sp,
                                color = CosmicTextSecondary
                            )
                        }
                    }
                }
            }

            // 4. ACTIVE COMPANION SYSTEM
            val currentCompanion = remember(progress.equippedAvatarId) {
                AVATARS.firstOrNull { it.id == progress.equippedAvatarId } ?: AVATARS[0]
            }

            Text(
                text = "ACTIVE COMPANION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, currentCompanion.eyeColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AiAvatar(mood = "neutral", avatarId = progress.equippedAvatarId, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = currentCompanion.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CosmicTextPrimary
                        )
                        Text(
                            text = "${currentCompanion.abilityName}: ${currentCompanion.abilityDesc}",
                            fontSize = 11.sp,
                            color = currentCompanion.eyeColor,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Edit Name AlertDialog Dialog
    if (showEditNameDialog) {
        Dialog(onDismissRequest = { showEditNameDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicDeepSpace),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.5.dp, CosmicNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EDIT PLAYER NAME",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicNeonCyan,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = { Text("Enter nickname...", color = CosmicTextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary,
                            focusedBorderColor = CosmicNeonCyan,
                            unfocusedBorderColor = CosmicSurfaceVariant,
                            cursorColor = CosmicNeonCyan
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input_field")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (tempName.isNotBlank()) {
                                    SoundManager.playClick()
                                    onSaveName(tempName)
                                    showEditNameDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SAVE", color = CosmicDeepSpace, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                SoundManager.playClick()
                                showEditNameDialog = false
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTextSecondary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CosmicTextPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CANCEL")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CosmicTextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 9.sp,
                color = CosmicTextSecondary,
                lineHeight = 11.sp
            )
        }
    }
}
