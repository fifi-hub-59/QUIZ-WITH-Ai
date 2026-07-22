package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.Question
import com.example.data.QuizImage
import com.example.ui.SoundManager
import com.example.ui.components.AiAvatar
import com.example.ui.components.bounceClick
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    currentImage: QuizImage?,
    questions: List<Question>,
    currentQuestionIndex: Int,
    clarity: Int, // percentage 0% to 100%
    timerSecondsLeft: Int,
    correctStreak: Int,
    wrongStreak: Int,
    shieldActive: Boolean,
    hintDisabledOptions: Set<String>,
    aiDialogue: String,
    aiMood: String,
    gameOverState: String?, // "WIN", "LOSE_STREAK", "LOSE_TIME", or null
    shieldsInventory: Int,
    hintsInventory: Int,
    extraTimeInventory: Int,
    guessesLeft: Int,
    selectedDifficultyName: String,
    difficultyMultiplier: Float,
    selectedAnswer: String?,
    showAnswerFeedback: Boolean,
    onUseShield: () -> Unit,
    onUseHint: () -> Unit,
    onUseExtraTime: () -> Unit,
    onSubmitAnswer: (String) -> Unit,
    onSubmitDirectGuess: (String) -> Unit,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier,
    equippedAvatarId: Int = 0
) {
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept hardware/system back button
    BackHandler(enabled = true) {
        showExitDialog = true
    }

    // Confirm Exit Alert Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "⚠️ Abandon Level?",
                    color = CosmicTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to exit? Your progress on this level will be lost.",
                    color = CosmicTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SoundManager.playClick()
                        showExitDialog = false
                        onExitGame()
                    }
                ) {
                    Text("EXIT & LOSE PROGRESS", color = CosmicNeonMagenta, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    SoundManager.playClick()
                    showExitDialog = false
                }) {
                    Text("CANCEL", color = CosmicTextSecondary)
                }
            },
            containerColor = CosmicSurface
        )
    }

    var showGuessDialog by remember { mutableStateOf(false) }
    var guessText by remember { mutableStateOf("") }

    if (showGuessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showGuessDialog = false 
                guessText = ""
            },
            title = {
                Text(
                    text = "🎯 Enter Your Guess",
                    color = CosmicTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Type the exact name of the mystery image (case-insensitive). A wrong guess costs 15 seconds!\n\nGuesses left: $guessesLeft",
                        color = CosmicTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = guessText,
                        onValueChange = { guessText = it },
                        placeholder = { Text("e.g. Cat, Red Apple...", color = CosmicTextSecondary.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicNeonCyan,
                            unfocusedBorderColor = CosmicNeonPurple.copy(alpha = 0.5f),
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary,
                            cursorColor = CosmicNeonCyan
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guess_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (guessText.isNotBlank()) {
                            onSubmitDirectGuess(guessText)
                            showGuessDialog = false
                            guessText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SUBMIT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        SoundManager.playClick()
                        showGuessDialog = false
                        guessText = ""
                    }
                ) {
                    Text("CANCEL", color = CosmicTextSecondary)
                }
            },
            containerColor = CosmicSurface
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
    ) {
        if (currentImage == null || questions.isEmpty()) {
            com.example.ui.components.AiLoadingScreen(
                equippedAvatarId = equippedAvatarId,
                onExitClick = { showExitDialog = true }
            )
        } else {
            // Main Game Layout
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // ==================== TOP SECTION (67% Height) ====================
            Box(
                modifier = Modifier
                    .weight(0.67f)
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                // Blur factor adjusted by difficulty multiplier
                val maxBlur = 100f * difficultyMultiplier
                val blurRadiusValue = ((100 - clarity) * difficultyMultiplier).coerceIn(0.1f, maxBlur).dp
                SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentImage.uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Blurred Secret Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(blurRadiusValue),
                        error = {
                            // Offline placeholder fallback image
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CosmicSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📷 Photo offline\n(Guess Category: ${currentImage.category.uppercase()})",
                                    color = CosmicTextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    )

                    // Dynamic Mist / Dark cover overlay that clears up as clarity increases (for 100% initial blur sensation)
                    val coverAlpha = (1f - (clarity / 100f)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CosmicDeepSpace.copy(alpha = coverAlpha * 0.85f))
                    )

                    // 2. Cyberpunk pixelation / dot overlay for older Android SDKs
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val revealFraction = clarity / 100f
                        if (revealFraction < 1f) {
                            val dotSpacing = 16f
                            val dotRadius = 4f * (1f - revealFraction)
                            val paintColor = CosmicDeepSpace.copy(alpha = 0.85f * (1f - revealFraction))
                            
                            for (x in 0..size.width.toInt() step dotSpacing.toInt()) {
                                for (y in 0..size.height.toInt() step dotSpacing.toInt()) {
                                    drawCircle(
                                        color = paintColor,
                                        radius = dotRadius,
                                        center = Offset(x.toFloat(), y.toFloat())
                                    )
                                }
                            }
                        }
                    }

                // Gradient shadow overlay at the bottom for readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CosmicDeepSpace)
                            )
                        )
                )

                // Difficulty & Guesses Badge floating below the timer
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 92.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CosmicDeepSpace.copy(alpha = 0.85f))
                            .border(1.dp, CosmicNeonPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("difficulty_badge")
                    ) {
                        Text(
                            text = selectedDifficultyName.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = when (selectedDifficultyName) {
                                "EASY" -> CosmicNeonCyan
                                "HARD" -> CosmicNeonMagenta
                                else -> CosmicNeonPurple
                            }
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(12.dp)
                                .background(CosmicTextSecondary.copy(alpha = 0.5f))
                        )
                        Text(
                            text = "🎯 $guessesLeft GUESSES LEFT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTextPrimary
                        )
                    }
                }

                // 3. TOP ROW CONTROLS (Back Arrow & Timer Circle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow with Alert Action
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            showExitDialog = true
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CosmicDeepSpace.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Abandon Game",
                            tint = CosmicTextPrimary
                        )
                    }

                    // Circular Countdown Timer Widget (bolder, larger, highly exciting design)
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw bold grey track (8.dp)
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.25f),
                                style = Stroke(width = 8.dp.toPx())
                            )
                            // Draw vibrant exciting red warning progress arc (8.dp)
                            val sweepAngle = (timerSecondsLeft / 120f) * 360f
                            drawArc(
                                color = Color(0xFFFF1744), // Highly vibrant red
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx())
                            )
                        }

                        val mins = timerSecondsLeft / 60
                        val secs = timerSecondsLeft % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            fontSize = 16.sp, // 1 size larger!
                            fontWeight = FontWeight.Black, // Bolder!
                            color = Color(0xFFFF1744), // Highly vibrant red
                            textAlign = TextAlign.Center
                        )
                    }

                    // Placeholder right side to balance alignment
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // 4. FLOATING VERTICAL LEVEL PROGRESS OVERLAY (Right edge)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicDeepSpace.copy(alpha = 0.7f))
                        .border(1.dp, CosmicNeonPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                ) {
                    Text(
                        text = "REVEAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$clarity%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicNeonCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Small vertical tracking bar (levels 1-20 questions)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(100.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    ) {
                        val fraction = (currentQuestionIndex / 20f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(CircleShape)
                                .background(CosmicNeonCyan)
                                .align(Alignment.BottomCenter)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${currentQuestionIndex}/20",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextSecondary
                    )
                }

                // 5. OPPONENT AI CHAT DIALOGUE & AVATAR (Bottom aligned)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Speech bubble box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp))
                            .background(CosmicSurface.copy(alpha = 0.95f))
                            .border(1.dp, CosmicNeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Pixel (Opponent)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicNeonCyan,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = aiDialogue,
                                fontSize = 15.sp,
                                color = CosmicTextPrimary,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Avatar box
                    AiAvatar(
                        mood = aiMood,
                        avatarId = equippedAvatarId,
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.5.dp, CosmicNeonPurple, RoundedCornerShape(12.dp))
                    )
                }
            }

            // ==================== BOTTOM SECTION (33% Height) ====================
            Box(
                modifier = Modifier
                    .weight(0.33f)
                    .fillMaxWidth()
                    .background(CosmicDeepSpace)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                if (currentQuestionIndex < questions.size) {
                    val question = questions[currentQuestionIndex]
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Question Heading Text
                        Text(
                            text = question.text,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            lineHeight = 23.sp
                        )

                        // 2x2 Grid or Vertical list of clickable Answer Buttons
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Split 4 options into two rows of two buttons each
                            val rows = question.options.chunked(2)
                            rows.forEach { rowOptions ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowOptions.forEach { option ->
                                        val isDisabled = hintDisabledOptions.contains(option)
                                        
                                        // Determine background, text, and border colors based on feedback status
                                        val finalContainerColor = if (showAnswerFeedback) {
                                            if (option == question.correctAnswer) {
                                                Color(0xFF2E7D32) // Vibrant Material Green for correct answer
                                            } else if (option == selectedAnswer) {
                                                Color(0xFFC62828) // Vibrant Material Red for chosen incorrect answer
                                            } else {
                                                Color(0xFFFFD700).copy(alpha = 0.25f) // Muted yellow for other options
                                            }
                                        } else {
                                            Color(0xFFFFD700) // Default gold/yellow
                                        }

                                        val finalContentColor = if (showAnswerFeedback) {
                                            if (option == question.correctAnswer || option == selectedAnswer) {
                                                Color.White // High readability on red/green
                                            } else {
                                                CosmicTextSecondary.copy(alpha = 0.5f)
                                            }
                                        } else {
                                            Color.Black
                                        }

                                        val finalBorderColor = if (showAnswerFeedback) {
                                            if (option == question.correctAnswer) {
                                                Color(0xFF4CAF50)
                                            } else if (option == selectedAnswer) {
                                                Color(0xFFEF5350)
                                            } else {
                                                Color.Transparent
                                            }
                                        } else {
                                            if (isDisabled) Color.Transparent else Color(0xFFFFB300)
                                        }

                                        val actualBg = if (isDisabled) {
                                            finalContainerColor.copy(alpha = 0.2f)
                                        } else {
                                            finalContainerColor
                                        }

                                        val actualBorder = if (isDisabled) {
                                            Color.Transparent
                                        } else {
                                            finalBorderColor
                                        }

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(actualBg)
                                                .border(
                                                    width = 1.5.dp,
                                                    color = actualBorder,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .bounceClick(enabled = !isDisabled && !showAnswerFeedback) {
                                                    onSubmitAnswer(option)
                                                }
                                                .testTag("submit_button")
                                        ) {
                                            Text(
                                                text = if (isDisabled) "❌" else option,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = finalContentColor,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // GREEN PHOSPHORESCENT "GUESS IMAGE" ACTION BUTTON
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF39FF14))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .bounceClick {
                                    showGuessDialog = true
                                }
                                .testTag("guess_image_button")
                        ) {
                            Text(
                                text = "🎯 GUESS IMAGE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // BOOSTER ASSISTANCE ACTIONS ROW (At very bottom)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Shield booster
                            BoosterButton(
                                icon = "🛡️",
                                count = shieldsInventory,
                                active = shieldActive,
                                onClick = onUseShield,
                                label = "SHIELD"
                            )

                            // 2. Hint booster
                            BoosterButton(
                                icon = "🔍",
                                count = hintsInventory,
                                active = false,
                                onClick = onUseHint,
                                label = "HINT"
                            )

                            // 3. Extra Time booster
                            BoosterButton(
                                icon = "⏳",
                                count = extraTimeInventory,
                                active = false,
                                onClick = onUseExtraTime,
                                label = "+30S"
                            )
                        }
                    }
                }
            }
        }

        // ==================== OVERLAYS (GAME OVER & LEVEL CLEAR SCREENS) ====================
        if (gameOverState != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable(enabled = false) {}, // consume clicks
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(28.dp)
                ) {
                    AiAvatar(
                        mood = if (gameOverState == "WIN") "celebrating" else "angry",
                        avatarId = equippedAvatarId,
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = if (gameOverState == "WIN") "LEVEL CLEARED!" else "GAME OVER",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (gameOverState == "WIN") CosmicNeonCyan else CosmicNeonMagenta,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary description text
                    Text(
                        text = when (gameOverState) {
                            "WIN" -> "Fantastic job! You matched wits with Pixel, answered all questions correctly, and fully revealed the secret image!"
                            "LOSE_STREAK" -> "You hit 3 consecutive wrong answers! Pixel outsmarted you. Upgrade your power-ups and shields in the store!"
                            "LOSE_TIME" -> "You ran out of time! Each level has a 2-minute countdown limit. Tap faster next level!"
                            "LOSE_GUESSES" -> "You ran out of allowed direct guesses! Pixel is laughing at your failed theories."
                            else -> ""
                        },
                        fontSize = 14.sp,
                        color = CosmicTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (gameOverState == "WIN") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎉 Revealed Subject:", fontSize = 12.sp, color = CosmicTextSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = currentImage.name.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CosmicNeonCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            SoundManager.playClick()
                            onExitGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_button")
                    ) {
                        Text(
                            text = if (gameOverState == "WIN") "CLAIM REWARDS & CONTINUE" else "RETURN TO LOBBY",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicDeepSpace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
        }

        // ==================== ANIMATED ANSWER FEEDBACK OVERLAY ====================
        if (showAnswerFeedback && currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val isCorrect = selectedAnswer == question.correctAnswer
            
            var animateScale by remember { mutableStateOf(0.5f) }
            var animateAlpha by remember { mutableStateOf(0f) }
            LaunchedEffect(Unit) {
                animateScale = 1f
                animateAlpha = 1f
            }
            val scale by animateFloatAsState(
                targetValue = animateScale,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                label = "overlay_scale"
            )
            val alpha by animateFloatAsState(
                targetValue = animateAlpha,
                animationSpec = tween(250),
                label = "overlay_alpha"
            )

            val infiniteTransition = rememberInfiniteTransition(label = "overlay_glow")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f * alpha))
                    .clickable(enabled = false) {}, // consume clicks
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isCorrect) Color(0xFF1B5E20).copy(alpha = 0.95f) else Color(0xFFB71C1C).copy(alpha = 0.95f))
                        .border(
                            width = 3.dp,
                            color = if (isCorrect) Color(0xFF4CAF50).copy(alpha = glowAlpha) else Color(0xFFF44336).copy(alpha = glowAlpha),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 36.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = if (isCorrect) "🎉 CORRECT!" else "❌ WRONG!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isCorrect) "✨ +5% Image Clarity!" else "💥 -5% Image Clarity!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isCorrect) "You're decoding the mystery..." else "Be careful, the clock is ticking!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BoosterButton(
    icon: String,
    count: Int,
    active: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_active")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (active) CosmicNeonPurple.copy(alpha = 0.3f) else CosmicSurface)
                .border(
                    width = 1.dp,
                    color = when {
                        active -> CosmicNeonPurple.copy(alpha = borderAlpha)
                        count > 0 -> CosmicNeonCyan.copy(alpha = 0.3f)
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                )
                .bounceClick(enabled = count > 0 && !active) {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 18.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = if (count > 0 || active) 1f else 0.35f
                }
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "$label ($count)",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) CosmicNeonPurple else if (count > 0) CosmicNeonCyan else CosmicTextSecondary.copy(alpha = 0.4f)
        )
    }
}
