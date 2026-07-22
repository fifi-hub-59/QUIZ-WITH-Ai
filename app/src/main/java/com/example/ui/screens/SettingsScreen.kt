package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgress
import com.example.ui.SoundManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    progress: UserProgress,
    onToggleSound: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "🚨 Reset Progress?",
                    color = CosmicTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete your current level, total XP, items inventory, and coin balances. This action is irreversible! Are you sure?",
                    color = CosmicTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SoundManager.playClick()
                        onResetProgress()
                        showResetDialog = false
                    }
                ) {
                    Text("RESET EVERYTHING", color = CosmicNeonMagenta, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    SoundManager.playClick()
                    showResetDialog = false
                }) {
                    Text("CANCEL", color = CosmicTextSecondary)
                }
            },
            containerColor = CosmicSurface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GAME SETTINGS",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CosmicDeepSpace)
            )
        },
        containerColor = CosmicDeepSpace,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Preferences section
            item {
                Text(
                    text = "PREFERENCES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicNeonCyan,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, CosmicNeonPurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Sound Effects Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Sound Effects", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Play click & scoring beep SFX", fontSize = 12.sp, color = CosmicTextSecondary)
                            }
                            Switch(
                                checked = progress.soundEnabled,
                                onCheckedChange = {
                                    SoundManager.playClick()
                                    onToggleSound(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CosmicDeepSpace,
                                    checkedTrackColor = CosmicNeonCyan,
                                    uncheckedThumbColor = CosmicTextSecondary,
                                    uncheckedTrackColor = CosmicSurfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = CosmicSurfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        // Background Music Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Background Music", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Play ambient lobby music loop", fontSize = 12.sp, color = CosmicTextSecondary)
                            }
                            Switch(
                                checked = progress.musicEnabled,
                                onCheckedChange = {
                                    SoundManager.playClick()
                                    onToggleMusic(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CosmicDeepSpace,
                                    checkedTrackColor = CosmicNeonCyan,
                                    uncheckedThumbColor = CosmicTextSecondary,
                                    uncheckedTrackColor = CosmicSurfaceVariant
                                )
                            )
                        }

                        HorizontalDivider(color = CosmicSurfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        // Dark Mode Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cosmic Dark Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("High contrast night style override", fontSize = 12.sp, color = CosmicTextSecondary)
                            }
                            Switch(
                                checked = progress.darkModeEnabled,
                                onCheckedChange = {
                                    SoundManager.playClick()
                                    onToggleDarkMode(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CosmicDeepSpace,
                                    checkedTrackColor = CosmicNeonCyan,
                                    uncheckedThumbColor = CosmicTextSecondary,
                                    uncheckedTrackColor = CosmicSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Game Data Operations
            item {
                Text(
                    text = "DANGER ZONE & ACCOUNT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicNeonMagenta,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, CosmicNeonMagenta.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Reset progress
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                showResetDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = CosmicNeonMagenta,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("RESET CURRENT DATA", color = CosmicNeonMagenta, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicNeonMagenta),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_button")
                        ) {
                            Text("LOGOUT GOOGLE ACCOUNT", color = CosmicDeepSpace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Legal & About section
            item {
                Text(
                    text = "ABOUT & PRIVACY POLICY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTextSecondary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(1.dp, CosmicSurfaceVariant, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = CosmicNeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Privacy & Terms", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This app strictly respects your personal Google profile info and local progress data. No external ads, telemetry, trackers, or hidden backdoors are packaged. Gemini generative requests are processed securely with SSL.",
                            fontSize = 12.sp,
                            color = CosmicTextSecondary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Version 1.0.0 (Production Build - Kotlin/Compose)",
                            fontSize = 11.sp,
                            color = CosmicTextSecondary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
