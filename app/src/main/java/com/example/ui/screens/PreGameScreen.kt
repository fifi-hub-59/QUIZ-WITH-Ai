package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreGameScreen(
    progress: UserProgress,
    selectedCategory: String?,
    selectedDifficulty: Difficulty,
    onStartGame: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentCompanion = remember(progress.equippedAvatarId) {
        AVATARS.firstOrNull { it.id == progress.equippedAvatarId } ?: AVATARS[0]
    }

    val categoryDetails = remember(selectedCategory) {
        when (selectedCategory) {
            "animal" -> Pair("Animals", "🐾 Furry friends & wild beasts")
            "fruit" -> Pair("Fruits", "🍎 Sweet & healthy harvest")
            "animated_celebrity" -> Pair("Celebrities", "🎬 Famous cartoon heroes")
            "body_part" -> Pair("Body Parts", "🧠 Anatomy & organic parts")
            "stadium" -> Pair("Stadiums", "🏟️ Grand sports arenas")
            "object" -> Pair("Objects", "🏺 Everyday relics & tools")
            else -> Pair("Mystic Visuals", "🌀 Ancient secret photographs")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "COSMIC BRIEFING",
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. CHOSEN REALM / CATEGORY DISPLAY CARD
                Text(
                    text = "TARGET DECODING FIELD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicTextSecondary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CosmicNeonPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = categoryDetails.second.substring(0, 2), // Emoji
                            fontSize = 36.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = categoryDetails.first.uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CosmicNeonCyan,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = categoryDetails.second.substring(3), // Description
                            fontSize = 13.sp,
                            color = CosmicTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. RUNTIME PARAMETERS (Difficulty details)
                Text(
                    text = "DECRYPTION SETTINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicTextSecondary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CosmicNeonPurple.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DIFFICULTY MATRIX",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTextSecondary
                            )
                            Text(
                                text = selectedDifficulty.displayName.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = when (selectedDifficulty) {
                                    Difficulty.EASY -> CosmicNeonCyan
                                    Difficulty.HARD -> CosmicNeonMagenta
                                    else -> CosmicNeonPurple
                                }
                            )
                        }
                        
                        VerticalDivider(
                            modifier = Modifier
                                .height(32.dp),
                            color = CosmicTextSecondary.copy(alpha = 0.2f)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "ALLOWED GUESSES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTextSecondary
                            )
                            Text(
                                text = "${selectedDifficulty.maxGuesses} ATTEMPTS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. EQUIPPED COMPANION & ACTIVE BUFF CARD
                Text(
                    text = "ACTIVE COMPANION SYSTEM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicTextSecondary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
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
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AiAvatar(
                                mood = "neutral",
                                avatarId = progress.equippedAvatarId,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentCompanion.name.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = CosmicTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ACTIVE PASSIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = currentCompanion.eyeColor,
                                    modifier = Modifier
                                        .border(1.dp, currentCompanion.eyeColor, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentCompanion.abilityDesc,
                                fontSize = 11.sp,
                                color = CosmicTextSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. CHAT COMMENTARY FROM CHOSEN COMPANION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AiAvatar(mood = "happy", avatarId = progress.equippedAvatarId, modifier = Modifier.size(40.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                            .background(CosmicSurfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, CosmicTextSecondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "My visual memory banks are synchronized. Secure 20 answers or guess the subject anytime!",
                            fontSize = 12.sp,
                            color = CosmicTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // START BUTTON
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CosmicNeonCyan)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .bounceClick {
                        onStartGame()
                    }
                    .testTag("submit_button")
            ) {
                Text(
                    text = "ENGAGE COGNITIVE RUN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicDeepSpace,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
