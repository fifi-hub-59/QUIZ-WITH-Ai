package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SoundManager
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoinPack
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SimulatedAdOverlay(
    onAdCompleted: () -> Unit,
    onAdClosed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(10) }
    val progress = (10 - timeLeft) / 10f

    // Animating background gradient
    val infiniteTransition = rememberInfiniteTransition(label = "ad_bg")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_offset"
    )

    // Animated spaceship position in the mock game ad
    val shipOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ship_pos"
    )

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            SoundManager.playClick()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .clickable(enabled = false) {}, // consume clicks
        contentAlignment = Alignment.Center
    ) {
        // Starfield animated background canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = Random(42)
            for (i in 0..40) {
                val x = (random.nextFloat() * size.width + animOffset) % size.width
                val y = random.nextFloat() * size.height
                val radius = random.nextFloat() * 2.5f + 1f
                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat() * 0.7f + 0.3f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .background(CosmicDeepSpace)
                .border(2.dp, CosmicNeonPurple.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SPONSORED AD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                // Close Button
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onAdClosed()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Ad",
                        tint = CosmicTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mock Game Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F0C20), Color(0xFF150A33))
                        )
                    )
                    .border(1.dp, CosmicNeonCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive looking game trailer drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    // Draw stars
                    drawCircle(Color(0xFFFFB300), 4f, Offset(centerX - 100f, centerY - 40f))
                    drawCircle(Color(0xFF00E5FF), 3f, Offset(centerX + 80f, centerY + 30f))

                    // Draw spaceship
                    val shipX = centerX + shipOffset
                    val shipY = centerY - 10f
                    // spaceship body
                    drawCircle(CosmicNeonMagenta, 12f, Offset(shipX, shipY))
                    // spaceship wings
                    drawCircle(CosmicNeonPurple, 6f, Offset(shipX - 14f, shipY + 4f))
                    drawCircle(CosmicNeonPurple, 6f, Offset(shipX + 14f, shipY + 4f))

                    // Laser trails
                    drawLine(
                        color = Color.Green,
                        start = Offset(shipX - 10f, shipY - 15f),
                        end = Offset(shipX - 10f, shipY - 80f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.Green,
                        start = Offset(shipX + 10f, shipY - 15f),
                        end = Offset(shipX + 10f, shipY - 80f),
                        strokeWidth = 3f
                    )
                }

                // Game Logo Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "COSMIC QUEST II",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicNeonCyan,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "★★★★★ OVER 10M DOWNLOADS!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Ad message
            Text(
                text = "Pre-register today for premium laser boosters and explore uncharted galaxy visual fields! Free coins rewarded upon completing this presentation.",
                fontSize = 12.sp,
                color = CosmicTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Timer & Progress
            if (timeLeft > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        color = CosmicNeonCyan,
                        trackColor = CosmicSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Reward unlocks in $timeLeft seconds...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicNeonCyan
                    )
                }
            } else {
                // BIG CLAIM BUTTON
                Button(
                    onClick = {
                        onAdCompleted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("claim_ad_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎁", fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "CLAIM +50 FREE COINS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicDeepSpace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedCheckoutOverlay(
    pack: CoinPack,
    onPurchaseComplete: (CoinPack) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isProcessing by remember { mutableStateOf(false) }
    var cardName by remember { mutableStateOf("Guest Explorer") }
    var cardNumber by remember { mutableStateOf("4532  8812  4901  5532") }
    var cardExpiry by remember { mutableStateOf("12 / 31") }
    var cardCvv by remember { mutableStateOf("381") }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(1800) // Simulate card authorization delay
            onPurchaseComplete(pack)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = false) {}, // consume clicks
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicDeepSpace),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(2.dp, CosmicNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Payment",
                            tint = CosmicNeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURE SANDBOX BILLING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicTextSecondary,
                            letterSpacing = 1.5.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!isProcessing) {
                                SoundManager.playClick()
                                onCancel()
                            }
                        },
                        enabled = !isProcessing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Checkout",
                            tint = CosmicTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    Column(
                        modifier = Modifier.height(300.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = CosmicNeonPurple,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "AUTHORIZING PAYMENT...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Verifying checkout via secure virtual tokens.",
                            fontSize = 12.sp,
                            color = CosmicTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // 1. STUNNING DESIGNER VIRTUAL CREDIT CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CosmicNeonPurple.copy(alpha = 0.8f), CosmicNeonMagenta.copy(alpha = 0.9f))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(18.dp)
                    ) {
                        // Card Details
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COSMIC PLAY SYSTEM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "VISA",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.White
                                )
                            }

                            // Card Number
                            Text(
                                text = cardNumber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Expiry & Holder
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "CARDHOLDER",
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = cardName.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "EXPIRES",
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = cardExpiry,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. INVOICE SUMMARY
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CosmicNeonPurple.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PURCHASE DETAILS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pack.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicTextPrimary
                                )
                                Text(
                                    text = pack.price,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CosmicTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (pack == CoinPack.VIP_LIFETIME) {
                                    "👑 Perks: Gold VIP tag, Double rewards (+20 coins) per level cleared, +10% initial starting clarity!"
                                } else {
                                    "🪙 Instant reward top up: +${pack.coins} gold coins added directly to your profile database."
                                },
                                fontSize = 11.sp,
                                color = CosmicNeonCyan,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. EDITABLE BILLING FORM FIELDS (Visual simulation)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cardName,
                            onValueChange = { cardName = it },
                            label = { Text("Cardholder Name", fontSize = 11.sp) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CosmicNeonPurple,
                                unfocusedBorderColor = CosmicSurfaceVariant,
                                focusedLabelColor = CosmicNeonPurple,
                                unfocusedLabelColor = CosmicTextSecondary,
                                focusedTextColor = CosmicTextPrimary,
                                unfocusedTextColor = CosmicTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it },
                                label = { Text("Expiry (MM/YY)", fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CosmicNeonPurple,
                                    unfocusedBorderColor = CosmicSurfaceVariant,
                                    focusedLabelColor = CosmicNeonPurple,
                                    unfocusedLabelColor = CosmicTextSecondary,
                                    focusedTextColor = CosmicTextPrimary,
                                    unfocusedTextColor = CosmicTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = { cardCvv = it },
                                label = { Text("CVV", fontSize = 11.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CosmicNeonPurple,
                                    unfocusedBorderColor = CosmicSurfaceVariant,
                                    focusedLabelColor = CosmicNeonPurple,
                                    unfocusedLabelColor = CosmicTextSecondary,
                                    focusedTextColor = CosmicTextPrimary,
                                    unfocusedTextColor = CosmicTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. PAY BUTTON
                    Button(
                        onClick = {
                            SoundManager.playClick()
                            isProcessing = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("pay_button")
                    ) {
                        Text(
                            text = "AUTHORIZE & PAY ${pack.price}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secure foot-note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Sandbox",
                            tint = CosmicTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Simulated billing Sandbox. No real money charged.",
                            fontSize = 9.sp,
                            color = CosmicTextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
