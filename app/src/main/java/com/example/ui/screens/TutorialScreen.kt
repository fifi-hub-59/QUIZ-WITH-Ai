package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.theme.*

@Composable
fun TutorialScreen(
    onTutorialComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableStateOf(0) }

    val slides = listOf(
        SlideData(
            title = "Mystery Images",
            description = "Welcome to Guess the Image! Pixel, our resident AI Opponent, has selected a mystery photo and hidden it behind a 100% thick blur filter.",
            tag = "BLURRED VIEW",
            accent = CosmicNeonCyan,
            avatarMood = "neutral"
        ),
        SlideData(
            title = "Answer 20 Questions",
            description = "To reveal the secret image, you must answer 20 rapid questions. Questions are generated dynamically by AI from the photo's secret metadata!",
            tag = "DYNAMIC QUIZ",
            accent = CosmicNeonPurple,
            avatarMood = "happy"
        ),
        SlideData(
            title = "Clarity & Streaks",
            description = "Correct answers increase clarity by +5%! Wrong answers decrease clarity by -5% and move you backward. Hit a streak of 5 or 10 correct for massive time and clarity bonuses!",
            tag = "REWARDS & PENALTIES",
            accent = CosmicNeonMagenta,
            avatarMood = "celebrating"
        ),
        SlideData(
            title = "Avoid 3 Failures",
            description = "Watch out! Getting 3 wrong answers in a row, or letting the 2-minute level timer tick to zero, results in an instant Game Over. Think fast and play smart!",
            tag = "DANGER ZONE",
            accent = CosmicNeonMagenta,
            avatarMood = "angry"
        )
    )

    val currentData = slides[currentSlide]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TUTORIAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicNeonCyan,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Skip",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = CosmicTextSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.clickable {
                        SoundManager.playClick()
                        onTutorialComplete()
                    }
                )
            }

            // Main Content Area with slide animations
            AnimatedContent(
                targetState = currentSlide,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "slide_transition",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { slideIdx ->
                val data = slides[slideIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // AI Avatar floating in center of tutorial
                    AiAvatar(
                        mood = data.avatarMood,
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Chip tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(data.accent.copy(alpha = 0.15f))
                            .border(1.dp, data.accent, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = data.tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = data.accent,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = data.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CosmicTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.description,
                        fontSize = 15.sp,
                        color = CosmicTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Navigation Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    slides.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (idx == currentSlide) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (idx == currentSlide) slides[currentSlide].accent
                                    else CosmicTextSecondary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    if (currentSlide > 0) {
                        IconButton(
                            onClick = {
                                SoundManager.playClick()
                                currentSlide--
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CosmicSurface)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = CosmicTextPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Main forward / Finish button
                    if (currentSlide == slides.size - 1) {
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                onTutorialComplete()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                                .testTag("submit_button")
                        ) {
                            Text(
                                text = "🎮 START GAME",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicDeepSpace,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                SoundManager.playClick()
                                currentSlide++
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(slides[currentSlide].accent)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next",
                                tint = CosmicDeepSpace
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class SlideData(
    val title: String,
    val description: String,
    val tag: String,
    val accent: Color,
    val avatarMood: String
)
