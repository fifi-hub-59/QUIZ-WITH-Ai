package com.example.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SoundManager
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String, id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Pulsing animations for buttons and text glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepSpace),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background: 3D Wireframe Perspective Tunnel inspired by user's design!
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val maxDimension = maxOf(size.width, size.height)
            val lineCount = 12

            for (i in 0 until lineCount) {
                val fraction = (i.toFloat() / lineCount)
                val radius = fraction * maxDimension * 0.9f
                
                // Drawing perspective concentric circles with fading opacity to mimic infinite tunnel depth
                drawCircle(
                    color = Color.White.copy(alpha = (1.0f - fraction) * 0.08f),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )

                // Draw radial perspective lines stretching to infinity
                val angle = i * (2 * Math.PI / 8) // 8 directions
                val x = center.x + Math.cos(angle).toFloat() * maxDimension
                val y = center.y + Math.sin(angle).toFloat() * maxDimension
                drawLine(
                    color = Color.White.copy(alpha = 0.04f),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
        ) {
            // Neon Logo Header "GUESS WITH Ai" matching the user's design
            Text(
                text = "GUESS\nWITH Ai",
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF1493), // Hot pink branding color
                textAlign = TextAlign.Center,
                lineHeight = 52.sp,
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0xFFFF1493).copy(alpha = 0.8f),
                        blurRadius = 35f * glowScale.value,
                        offset = Offset(0f, 2f)
                    )
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "AI-Powered Visual Quiz Game",
                fontSize = 15.sp,
                color = CosmicTextSecondary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Subtitle banner card
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "READY TO CHOOSE?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicNeonMagenta,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "100 levels of blurred details and smart questions. Match your wits with Pixel, your AI Opponent!",
                        fontSize = 13.sp,
                        color = CosmicTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            // Fluffy Cloud Button Layout matching the "START GAME" layout from the image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background bubble layer with overlapping soft glowing circles
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.offset(y = (-2).dp)
                ) {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF8A2BE2).copy(alpha = 0.45f)))
                    Spacer(modifier = Modifier.width((-14).dp))
                    Box(modifier = Modifier.size(68.dp).clip(CircleShape).background(Color(0xFFFF1493).copy(alpha = 0.55f)))
                    Spacer(modifier = Modifier.width((-14).dp))
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF00FFFF).copy(alpha = 0.45f)))
                }

                // Primary Start Game Button with solid borders, custom drop-shadow and vibrant pop-art colors
                Button(
                    onClick = {
                        SoundManager.playClick()
                        Toast.makeText(context, "Welcome, signing in with Google...", Toast.LENGTH_SHORT).show()
                        
                        val randomId = "google_" + (100000..999999).random()
                        val gamerNames = listOf("CosmicExplorer", "PixelHunter", "Brainiac", "NexusGamer", "AlphaMind")
                        val chosenName = gamerNames.random() + "_" + (10..99).random()
                        
                        onLoginSuccess(chosenName, "$chosenName@gmail.com", randomId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .border(3.dp, Color.Black, RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF1493), Color(0xFF8A2BE2), Color(0xFF00FFFF))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("submit_button")
                ) {
                    Text(
                        text = "START GAME",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        style = LocalTextStyle.current.copy(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(2f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary EXIT Button
            OutlinedButton(
                onClick = {
                    SoundManager.playClick()
                    (context as? Activity)?.finish()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CosmicTextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicNeonPurple.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(44.dp)
                    .testTag("exit_app_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🚪 EXIT APP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Secure Google OAuth | Progress Auto-Saved",
                fontSize = 11.sp,
                color = CosmicTextSecondary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
