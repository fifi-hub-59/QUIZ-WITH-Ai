package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.SoundManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var startAnimations by remember { mutableStateOf(false) }
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "logo_alpha"
    )
    
    // Ambient stars pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_stars")
    val starPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_pulse"
    )

    // Trigger animations and delayed transition
    LaunchedEffect(Unit) {
        startAnimations = true
        SoundManager.playClick() // Initial audio cue
        delay(2500) // 2.5 seconds cinematic delay
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0E17),
                        CosmicDeepSpace,
                        Color(0xFF161426)
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Starfield Canvas Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val count = 40
            val random = java.util.Random(1337)
            for (i in 0 until count) {
                val x = random.nextFloat() * size.width
                val y = random.nextFloat() * size.height
                val radius = random.nextFloat() * 4f + 1f
                val starAlpha = (random.nextFloat() * 0.5f + 0.5f) * starPulse
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        // Center Branding Box
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Neon Glyph Box
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(colors = listOf(CosmicNeonMagenta, CosmicNeonPurple)),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(CosmicSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cosmic_brand_logo),
                    contentDescription = "Cosmic Reveal Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Name
            Text(
                text = "COSMIC REVEAL",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = CosmicNeonCyan,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "NEURAL DECODING SYSTEM v1.0",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTextSecondary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress bar
            LinearProgressIndicator(
                color = CosmicNeonPurple,
                trackColor = CosmicSurface,
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BOOTING COGNITIVE INTERFACE...",
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CosmicNeonPurple.copy(alpha = 0.7f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
