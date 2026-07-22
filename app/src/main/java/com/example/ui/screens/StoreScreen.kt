package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.components.AiAvatar
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoinPack

data class AvatarData(
    val id: Int,
    val name: String,
    val description: String,
    val abilityName: String,
    val abilityDesc: String,
    val price: Int,
    val gradientColors: List<Color>,
    val eyeColor: Color
)

val AVATARS = listOf(
    AvatarData(
        id = 0,
        name = "Core Alpha",
        description = "Standard companion droid. Simple, precise, and highly reliable.",
        abilityName = "Companion Core",
        abilityDesc = "No passive magical abilities. Your classic tutorial guide.",
        price = 0,
        gradientColors = listOf(Color(0xFF2C3E50), Color(0xFF3498DB)),
        eyeColor = Color(0xFF00FFCC)
    ),
    AvatarData(
        id = 1,
        name = "Chronos-X",
        description = "Temporal cyber-entity specialized in timeline manipulation.",
        abilityName = "Time Warp",
        abilityDesc = "Passive: Grants +5s extra time on all gameplay levels.",
        price = 150,
        gradientColors = listOf(Color(0xFF3F2B96), Color(0xFFA8C0FF)),
        eyeColor = Color(0xFFFF007F)
    ),
    AvatarData(
        id = 2,
        name = "Optica-9",
        description = "Advanced radar companion that clears optical noise instantly.",
        abilityName = "Super Vision",
        abilityDesc = "Passive: Starts level with 10% less blur (+10% base clarity).",
        price = 250,
        gradientColors = listOf(Color(0xFF000428), Color(0xFF004E92)),
        eyeColor = Color(0xFF39FF14)
    ),
    AvatarData(
        id = 3,
        name = "Socrates-v2",
        description = "Deep logic model that bypasses system confusion matrix.",
        abilityName = "Neural Link",
        abilityDesc = "Passive: Spawns 1 free Eliminate Hint at start of game.",
        price = 200,
        gradientColors = listOf(Color(0xFF4B0082), Color(0xFFEE82EE)),
        eyeColor = Color(0xFFDFFF00)
    ),
    AvatarData(
        id = 4,
        name = "Midas Bot",
        description = "Cosmic economy droid that synthesizes electronic gold fields.",
        abilityName = "Golden Touch",
        abilityDesc = "Passive: Doubles all gold coins earned from level clearances!",
        price = 350,
        gradientColors = listOf(Color(0xFFBF953F), Color(0xFFFCF6BA)),
        eyeColor = Color(0xFFFFD700)
    ),
    AvatarData(
        id = 5,
        name = "Aegis Zero",
        description = "Heavy armored sentinel with permanent energy shield layers.",
        abilityName = "Force Field",
        abilityDesc = "Passive: Spawns with 1 free Safety Shield active per level.",
        price = 180,
        gradientColors = listOf(Color(0xFF0F2027), Color(0xFF203A43)),
        eyeColor = Color(0xFF00FFFF)
    ),
    AvatarData(
        id = 6,
        name = "Nova Elixir",
        description = "Astral companion radiating high-density learning frequencies.",
        abilityName = "XP Synergy",
        abilityDesc = "Passive: Grants +50% XP multiplier on all correct answers.",
        price = 120,
        gradientColors = listOf(Color(0xFFF000FF), Color(0xFF7B1FA2)),
        eyeColor = Color(0xFFEEFF41)
    ),
    AvatarData(
        id = 7,
        name = "Phoenix",
        description = "Fiery companion that revives the master during fatal countdowns.",
        abilityName = "Resurrection",
        abilityDesc = "Passive: Restores 10s extra if timer hits zero once per game.",
        price = 220,
        gradientColors = listOf(Color(0xFFED213A), Color(0xFF93291E)),
        eyeColor = Color(0xFFFFA500)
    ),
    AvatarData(
        id = 8,
        name = "Vortex",
        description = "High-velocity kinetic companion that multiplies successive successes.",
        abilityName = "Streak Blitz",
        abilityDesc = "Passive: Doubles correctness streak timer and clarity rewards.",
        price = 190,
        gradientColors = listOf(Color(0xFF1F4037), Color(0xFF99F2C8)),
        eyeColor = Color(0xFFE040FB)
    ),
    AvatarData(
        id = 9,
        name = "Lotus Prime",
        description = "Transcendental zen model that calms the timeline coordinates.",
        abilityName = "Zen Mind",
        abilityDesc = "Passive: Slows down the game level timer countdown speed by 15%.",
        price = 160,
        gradientColors = listOf(Color(0xFF1D976C), Color(0xFF93F9B9)),
        eyeColor = Color(0xFF00E676)
    ),
    AvatarData(
        id = 10,
        name = "Shadow.exe",
        description = "Covert stealth ninja that hacks and decrypts wrong choices.",
        abilityName = "Decryption",
        abilityDesc = "Passive: Slashes multiple-choice answers from 4 to 3 options!",
        price = 300,
        gradientColors = listOf(Color(0xFF000000), Color(0xFF243B55)),
        eyeColor = Color(0xFF00E5FF)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    progress: UserProgress,
    onBuyExtraTime: () -> Unit = {},
    onBuyHint: () -> Unit = {},
    onBuyShield: () -> Unit = {},
    onBuyXpElixir: () -> Unit = {},
    onTransmuteXp: (Int, Int) -> Unit = { _, _ -> },
    onClaimMilestone: (Int) -> Unit = {},
    onWatchAd: () -> Unit = {},
    onBuyCoinPack: (CoinPack) -> Unit = {},
    onBack: () -> Unit,
    onBuyAvatar: (Int, Int) -> Unit = { _, _ -> },
    onEquipAvatar: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val unlockedIds = remember(progress.unlockedAvatarIds) {
        progress.unlockedAvatarIds.split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toIntOrNull() }
            .toSet()
    }

    var selectedAvatarId by remember { mutableStateOf(progress.equippedAvatarId) }
    val selectedAvatar = remember(selectedAvatarId) {
        AVATARS.firstOrNull { it.id == selectedAvatarId } ?: AVATARS[0]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AVATAR COMPANIONS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTextPrimary,
                        letterSpacing = 1.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        SoundManager.playClick()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CosmicTextPrimary
                        )
                    }
                },
                actions = {
                    // Coin balance inside TopBar
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙", fontSize = 14.sp, modifier = Modifier.padding(end = 4.dp))
                            Text(
                                text = "${progress.coins}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CosmicDeepSpace)
            )
        },
        containerColor = CosmicDeepSpace,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Introductory Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CosmicNeonPurple.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🤖", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "Magical Store Companions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicNeonCyan
                        )
                        Text(
                            text = "Each unique robot unlocks a passive superpower. Tap any of the 10 companions in the grid to review details, purchase, or equip them!",
                            fontSize = 12.sp,
                            color = CosmicTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Compact 5x2 Grid Layout
            Text(
                text = "🤖 GRID COMPANIONS (5 × 2)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            val shopAvatars = remember { AVATARS.filter { it.id > 0 } }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (row in 0..1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (col in 0..4) {
                            val index = row * 5 + col
                            if (index < shopAvatars.size) {
                                val avatar = shopAvatars[index]
                                val isOwned = unlockedIds.contains(avatar.id)
                                val isEquipped = progress.equippedAvatarId == avatar.id
                                val isSelected = selectedAvatarId == avatar.id

                                val cellBorderColor = when {
                                    isSelected -> CosmicNeonCyan
                                    isEquipped -> Color(0xFF00FFCC)
                                    isOwned -> CosmicTextSecondary.copy(alpha = 0.4f)
                                    else -> Color.Transparent
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(0.85f)
                                        .border(
                                            width = if (isSelected || isEquipped) 2.dp else 1.dp,
                                            color = cellBorderColor,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .bounceClick {
                                            selectedAvatarId = avatar.id
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp)
                                    ) {
                                        AiAvatar(
                                            mood = if (isEquipped) "happy" else "neutral",
                                            avatarId = avatar.id,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = avatar.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CosmicNeonCyan else CosmicTextPrimary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        // Indicator text or icon
                                        if (isEquipped) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF00FFCC),
                                                letterSpacing = 0.5.sp
                                            )
                                        } else if (isOwned) {
                                            Text(
                                                text = "OWNED",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CosmicTextSecondary,
                                                letterSpacing = 0.5.sp
                                            )
                                        } else {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = "🪙", fontSize = 7.sp)
                                                Text(
                                                    text = "${avatar.price}",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFFFFD700)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Interactive Detail Panel for the Selected Companion
            Text(
                text = "📁 SELECTED COMPANION PROFILE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = CosmicTextSecondary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(selectedAvatar.eyeColor.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AiAvatar(
                            mood = if (progress.equippedAvatarId == selectedAvatar.id) "celebrating" else "neutral",
                            avatarId = selectedAvatar.id,
                            modifier = Modifier.size(68.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedAvatar.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CosmicTextPrimary
                                )
                                if (progress.equippedAvatarId == selectedAvatar.id) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF00FFCC).copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF00FFCC),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedAvatar.description,
                                fontSize = 12.sp,
                                color = CosmicTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Ability description card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicDeepSpace),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                selectedAvatar.eyeColor.copy(alpha = 0.25f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "⚡", fontSize = 14.sp)
                                Text(
                                    text = selectedAvatar.abilityName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = selectedAvatar.eyeColor,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedAvatar.abilityDesc,
                                fontSize = 12.sp,
                                color = CosmicTextPrimary,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val isOwned = unlockedIds.contains(selectedAvatar.id)
                    val isEquipped = progress.equippedAvatarId == selectedAvatar.id
                    val canAfford = progress.coins >= selectedAvatar.price

                    if (isEquipped) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CosmicSurfaceVariant)
                        ) {
                            Text(
                                text = "ACTIVE COMPANION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTextSecondary.copy(alpha = 0.5f)
                            )
                        }
                    } else if (isOwned) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(selectedAvatar.eyeColor)
                                .bounceClick {
                                    onEquipAvatar(selectedAvatar.id)
                                }
                        ) {
                            Text(
                                text = "EQUIP COMPANION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicDeepSpace
                            )
                        }
                    } else {
                        val activeBg = if (canAfford) Color(0xFFFFD700) else CosmicSurfaceVariant
                        val textCol = if (canAfford) CosmicDeepSpace else CosmicTextSecondary.copy(alpha = 0.5f)

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(activeBg)
                                .bounceClick(enabled = canAfford) {
                                    onBuyAvatar(selectedAvatar.id, selectedAvatar.price)
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (canAfford) "UNLOCK COMPANION" else "INSUFFICIENT COINS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textCol
                                )
                                if (canAfford) {
                                    Text(
                                        text = "🪙 ${selectedAvatar.price}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CosmicDeepSpace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Also render Core Alpha selection tab if user wants to switch back
            if (selectedAvatarId != 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                        .bounceClick {
                            selectedAvatarId = 0
                        }
                ) {
                    Text(
                        text = "Show Core Alpha (Starter Companion)",
                        color = CosmicNeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
