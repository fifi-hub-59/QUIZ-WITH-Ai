package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.SoundManager
import com.example.ui.theme.CosmicDeepSpace
import com.example.ui.theme.CosmicNeonCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Difficulty
import com.example.ui.components.SimulatedAdOverlay
import com.example.ui.components.SimulatedCheckoutOverlay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val gameViewModel: GameViewModel = viewModel()
                val navController = rememberNavController()

                // Observe player progress state reactively from Room Database
                val progressState by gameViewModel.userProgress.collectAsStateWithLifecycle()
                val showAdScreen by gameViewModel.showAdScreen.collectAsStateWithLifecycle()
                val showCheckoutScreen by gameViewModel.showCheckoutScreen.collectAsStateWithLifecycle()

                // High-fidelity edge-to-edge safe area scaffold
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CosmicDeepSpace
                ) { innerPadding ->
                    val progress = progressState
                    if (progress == null) {
                        // Display a beautiful neon space spinner during initial database load
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CosmicDeepSpace)
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CosmicNeonCyan)
                        }
                    } else {
                        // Synchronize user preferences with SoundManager
                        SoundManager.setSoundEnabled(progress.soundEnabled)
                        SoundManager.setMusicEnabled(progress.musicEnabled)

                        // Handle app background/foreground transitions
                        val lifecycleOwner = LocalLifecycleOwner.current
                        DisposableEffect(lifecycleOwner, navController) {
                            val observer = LifecycleEventObserver { _, event ->
                                val currentRoute = navController.currentBackStackEntry?.destination?.route
                                when (event) {
                                    Lifecycle.Event.ON_PAUSE -> {
                                        // Pause timer and background music when app goes to background
                                        gameViewModel.pauseGame()
                                        SoundManager.stopAllSounds()
                                    }
                                    Lifecycle.Event.ON_RESUME -> {
                                        // Resume timer or music when app returns to foreground
                                        if (currentRoute == "game") {
                                            gameViewModel.resumeGameTimer()
                                        } else if (currentRoute != null) {
                                            SoundManager.startMusicForScreen(currentRoute)
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)
                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }

                        // Listen to navigation destination changes to automatically play/stop background music
                        DisposableEffect(navController) {
                            val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
                                val route = destination.route
                                if (route != null) {
                                    SoundManager.startMusicForScreen(route)
                                }
                            }
                            navController.addOnDestinationChangedListener(listener)
                            onDispose {
                                navController.removeOnDestinationChangedListener(listener)
                            }
                        }

                        // Choose starting destination as splash for beautiful entry cinematic
                        val startDestination = "splash"

                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            // Set up reactive navigation routes matching React Navigation specs
                            NavHost(
                                navController = navController,
                                startDestination = startDestination,
                                modifier = Modifier.fillMaxSize(),
                                enterTransition = {
                                    androidx.compose.animation.slideInHorizontally(initialOffsetX = { it }) + androidx.compose.animation.fadeIn()
                                },
                                exitTransition = {
                                    androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -it }) + androidx.compose.animation.fadeOut()
                                },
                                popEnterTransition = {
                                    androidx.compose.animation.slideInHorizontally(initialOffsetX = { -it }) + androidx.compose.animation.fadeIn()
                                },
                                popExitTransition = {
                                    androidx.compose.animation.slideOutHorizontally(targetOffsetX = { it }) + androidx.compose.animation.fadeOut()
                                }
                            ) {
                                // --- Route 0: Splash Screen ---
                                composable("splash") {
                                    SplashScreen(
                                        onSplashComplete = {
                                            val nextDest = when {
                                                progress.googleId.isBlank() -> "login"
                                                !progress.isTutorialCompleted -> "tutorial"
                                                else -> "home"
                                            }
                                            navController.navigate(nextDest) {
                                                popUpTo("splash") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // --- Route 1: Login Screen ---
                                composable("login") {
                                    LoginScreen(
                                        onLoginSuccess = { name, email, id ->
                                            gameViewModel.signInUser(id, name, email)
                                            navController.navigate("tutorial") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // --- Route 2: Tutorial Slider Screen ---
                                composable("tutorial") {
                                    TutorialScreen(
                                        onTutorialComplete = {
                                            gameViewModel.completeTutorial()
                                            navController.navigate("home") {
                                                popUpTo("tutorial") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // --- Route 3: Home Lobby Screen ---
                                composable("home") {
                                    val selectedDifficulty by gameViewModel.selectedDifficulty.collectAsStateWithLifecycle()
                                    HomeScreen(
                                        progress = progress,
                                        selectedDifficulty = selectedDifficulty,
                                        onDifficultySelected = { gameViewModel.selectDifficulty(it) },
                                        onStartGame = { level ->
                                            navController.navigate("category_selection")
                                        },
                                        onEquipAvatar = { id ->
                                            gameViewModel.equipAvatar(id)
                                        },
                                        onNavigateToStore = {
                                            navController.navigate("store")
                                        },
                                        onNavigateToSettings = {
                                            navController.navigate("settings")
                                        },
                                        onNavigateToLeaderboard = {
                                            navController.navigate("leaderboard")
                                        },
                                        onNavigateToProfile = {
                                            navController.navigate("profile")
                                        },
                                        onWatchAdClick = {
                                            gameViewModel.startRewardedAd()
                                        }
                                    )
                                }

                                // --- Route: Player Profile Screen ---
                                composable("profile") {
                                    ProfileScreen(
                                        progress = progress,
                                        onSaveName = { newName ->
                                            gameViewModel.signInUser(progress.googleId, newName, progress.email)
                                        },
                                        onBack = {
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                // --- Category Selection Screen ---
                                composable("category_selection") {
                                    CategorySelectionScreen(
                                        onCategorySelected = { category ->
                                            gameViewModel.selectCategory(category)
                                            navController.navigate("pre_game") {
                                                popUpTo("home")
                                            }
                                        },
                                        onBack = {
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                // --- Route: Pre-Game Briefing Screen ---
                                composable("pre_game") {
                                    val selectedCategory by gameViewModel.selectedCategory.collectAsStateWithLifecycle()
                                    val selectedDifficulty by gameViewModel.selectedDifficulty.collectAsStateWithLifecycle()
                                    PreGameScreen(
                                        progress = progress,
                                        selectedCategory = selectedCategory,
                                        selectedDifficulty = selectedDifficulty,
                                        onStartGame = {
                                            gameViewModel.startNewGame(progress.level)
                                            navController.navigate("game") {
                                                popUpTo("home")
                                            }
                                        },
                                        onBack = {
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                // --- Route 4: Main Play Screen (Gameplay) ---
                                composable("game") {
                                    DisposableEffect(Unit) {
                                        onDispose {
                                            gameViewModel.pauseGame()
                                        }
                                    }
                                    val currentImage by gameViewModel.currentImage.collectAsStateWithLifecycle()
                                    val questions by gameViewModel.questions.collectAsStateWithLifecycle()
                                    val currentQuestionIndex by gameViewModel.currentQuestionIndex.collectAsStateWithLifecycle()
                                    val clarity by gameViewModel.activeClarity.collectAsStateWithLifecycle()
                                    val timerSecondsLeft by gameViewModel.activeTimerSecondsLeft.collectAsStateWithLifecycle()
                                    val correctStreak by gameViewModel.correctStreak.collectAsStateWithLifecycle()
                                    val wrongStreak by gameViewModel.wrongStreak.collectAsStateWithLifecycle()
                                    val shieldActive by gameViewModel.shieldActive.collectAsStateWithLifecycle()
                                    val hintDisabledOptions by gameViewModel.hintDisabledOptions.collectAsStateWithLifecycle()
                                    val aiDialogue by gameViewModel.aiDialogue.collectAsStateWithLifecycle()
                                    val aiMood by gameViewModel.aiMood.collectAsStateWithLifecycle()
                                    val gameOverState by gameViewModel.gameOverState.collectAsStateWithLifecycle()
                                    val guessesLeft by gameViewModel.guessesLeft.collectAsStateWithLifecycle()
                                    val selectedDifficulty by gameViewModel.selectedDifficulty.collectAsStateWithLifecycle()
                                    val selectedAnswer by gameViewModel.selectedAnswer.collectAsStateWithLifecycle()
                                    val showAnswerFeedback by gameViewModel.showAnswerFeedback.collectAsStateWithLifecycle()

                                    // Dynamic transition to results screen
                                    LaunchedEffect(gameOverState) {
                                        if (gameOverState != null) {
                                            navController.navigate("result") {
                                                popUpTo("home")
                                            }
                                        }
                                    }

                                    GameScreen(
                                        currentImage = currentImage,
                                        questions = questions,
                                        currentQuestionIndex = currentQuestionIndex,
                                        clarity = clarity,
                                        timerSecondsLeft = timerSecondsLeft,
                                        correctStreak = correctStreak,
                                        wrongStreak = wrongStreak,
                                        shieldActive = shieldActive,
                                        hintDisabledOptions = hintDisabledOptions,
                                        aiDialogue = aiDialogue,
                                        aiMood = aiMood,
                                        gameOverState = gameOverState,
                                        shieldsInventory = progress.shieldsCount,
                                        hintsInventory = progress.hintsCount,
                                        extraTimeInventory = progress.extraTimeCount,
                                        guessesLeft = guessesLeft,
                                        selectedDifficultyName = selectedDifficulty.displayName,
                                        difficultyMultiplier = selectedDifficulty.blurMultiplier,
                                        selectedAnswer = selectedAnswer,
                                        showAnswerFeedback = showAnswerFeedback,
                                        onUseShield = { gameViewModel.useShield() },
                                        onUseHint = { gameViewModel.useHint() },
                                        onUseExtraTime = { gameViewModel.useExtraTime() },
                                        onSubmitAnswer = { option -> gameViewModel.submitAnswer(option) },
                                        onSubmitDirectGuess = { guess -> gameViewModel.submitDirectGuess(guess) },
                                        onExitGame = {
                                            gameViewModel.pauseGame()
                                            navController.popBackStack("home", false)
                                        },
                                        equippedAvatarId = progress.equippedAvatarId
                                    )
                                }

                                // --- Route: Result Screen ---
                                composable("result") {
                                    val gameOverState by gameViewModel.gameOverState.collectAsStateWithLifecycle()
                                    val currentImage by gameViewModel.currentImage.collectAsStateWithLifecycle()
                                    val currentQuestionIndex by gameViewModel.currentQuestionIndex.collectAsStateWithLifecycle()
                                    val timerSecondsLeft by gameViewModel.activeTimerSecondsLeft.collectAsStateWithLifecycle()
                                    val correctStreak by gameViewModel.correctStreak.collectAsStateWithLifecycle()

                                    ResultScreen(
                                        progress = progress,
                                        gameOverState = gameOverState,
                                        currentImage = currentImage,
                                        currentQuestionIndex = currentQuestionIndex,
                                        timerSecondsLeft = timerSecondsLeft,
                                        correctStreak = correctStreak,
                                        onClaimRewards = {
                                            navController.navigate("reward") {
                                                popUpTo("home")
                                            }
                                        },
                                        onRetry = {
                                            gameViewModel.startNewGame(progress.level)
                                            navController.navigate("game") {
                                                popUpTo("home")
                                            }
                                        },
                                        onExit = {
                                            gameViewModel.pauseGame()
                                            navController.popBackStack("home", false)
                                        }
                                    )
                                }

                                // --- Route: Reward Screen ---
                                composable("reward") {
                                    RewardScreen(
                                        isVip = progress.isVipActive,
                                        equippedAvatarId = progress.equippedAvatarId,
                                        onClaimSuccess = { coinsEarned, shieldsEarned, hintsEarned ->
                                            gameViewModel.collectLootRewards(coinsEarned, shieldsEarned, hintsEarned)
                                            gameViewModel.pauseGame()
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // --- Route 5: Power-ups Store Screen ---
                                composable("store") {
                                    StoreScreen(
                                        progress = progress,
                                        onBuyExtraTime = { gameViewModel.buyExtraTime() },
                                        onBuyHint = { gameViewModel.buyHint() },
                                        onBuyShield = { gameViewModel.buyShield() },
                                        onBuyXpElixir = { gameViewModel.buyXpElixir() },
                                        onTransmuteXp = { xp, coins -> gameViewModel.transmuteXpToCoins(xp, coins) },
                                        onClaimMilestone = { milestone -> gameViewModel.claimMilestoneReward(milestone) },
                                        onWatchAd = { gameViewModel.startRewardedAd() },
                                        onBuyCoinPack = { pack -> gameViewModel.openCheckout(pack) },
                                        onBack = { navController.popBackStack() },
                                        onBuyAvatar = { id, price -> gameViewModel.buyAvatar(id, price) },
                                        onEquipAvatar = { id -> gameViewModel.equipAvatar(id) }
                                    )
                                }

                                // --- Route 6: Settings Screen ---
                                composable("settings") {
                                    SettingsScreen(
                                        progress = progress,
                                        onToggleSound = { gameViewModel.toggleSound(it) },
                                        onToggleMusic = { gameViewModel.toggleMusic(it) },
                                        onToggleDarkMode = { gameViewModel.toggleDarkMode(it) },
                                        onResetProgress = { gameViewModel.resetAllProgress() },
                                        onLogout = {
                                            gameViewModel.logout()
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        },
                                        onBack = { navController.popBackStack() }
                                    )
                                }

                                // --- Route 7: Leaderboard Screen ---
                                composable("leaderboard") {
                                    LeaderboardScreen(
                                        progress = progress,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                }
                            }

                            // --- Simulated Fullscreen Ad Overlay ---
                            if (showAdScreen) {
                                SimulatedAdOverlay(
                                    onAdCompleted = { gameViewModel.completeRewardedAd() },
                                    onAdClosed = { gameViewModel.closeRewardedAd() },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // --- Simulated Payment Checkout Dialog Overlay ---
                            showCheckoutScreen?.let { pack ->
                                SimulatedCheckoutOverlay(
                                    pack = pack,
                                    onPurchaseComplete = { gameViewModel.completeCheckout(it) },
                                    onCancel = { gameViewModel.closeCheckout() },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
