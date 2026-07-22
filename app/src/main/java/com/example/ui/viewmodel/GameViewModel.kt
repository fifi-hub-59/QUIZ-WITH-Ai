package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import com.example.BuildConfig
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.RetrofitClient
import com.example.ui.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class Difficulty(
    val displayName: String,
    val initialClarity: Int,
    val maxGuesses: Int,
    val blurMultiplier: Float,
    val description: String
) {
    EASY("Easy", 30, 5, 0.5f, "Clearer image, 5 guesses allowed"),
    MEDIUM("Medium", 10, 3, 1.09f, "Standard blur, 3 guesses allowed"),
    HARD("Hard", 0, 1, 1.7f, "Extreme blur, only 1 guess allowed!")
}

enum class CoinPack(val title: String, val coins: Int, val price: String, val badge: String = "") {
    SMALL("Small Coin Pack", 500, "$0.99", "POPULAR"),
    MEDIUM("Medium Coin Fortune", 2500, "$2.99", "HOT DEAL"),
    LARGE("Cosmic Vault", 8000, "$4.99", "BEST VALUE"),
    VIP_LIFETIME("Lifetime Cosmic VIP", 0, "$3.99", "EXCLUSIVE VIP")
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = UserProgressRepository(db.userProgressDao())

    // Player Progress State
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()

    // Ads and Checkout Simulation States
    private val _showAdScreen = MutableStateFlow(false)
    val showAdScreen: StateFlow<Boolean> = _showAdScreen.asStateFlow()

    private val _showCheckoutScreen = MutableStateFlow<CoinPack?>(null)
    val showCheckoutScreen: StateFlow<CoinPack?> = _showCheckoutScreen.asStateFlow()

    // Active Quiz State
    private val _selectedDifficulty = MutableStateFlow(Difficulty.MEDIUM)
    val selectedDifficulty: StateFlow<Difficulty> = _selectedDifficulty.asStateFlow()

    private val _guessesLeft = MutableStateFlow(3)
    val guessesLeft: StateFlow<Int> = _guessesLeft.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _currentImage = MutableStateFlow<QuizImage?>(null)
    val currentImage: StateFlow<QuizImage?> = _currentImage.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private var playingLevel: Int = 1

    private val _activeClarity = MutableStateFlow(0) // percentage 0% to 100%
    val activeClarity: StateFlow<Int> = _activeClarity.asStateFlow()

    private val _activeTimerSecondsLeft = MutableStateFlow(120) // 2 minutes (120s)
    val activeTimerSecondsLeft: StateFlow<Int> = _activeTimerSecondsLeft.asStateFlow()

    private val _correctStreak = MutableStateFlow(0)
    val correctStreak: StateFlow<Int> = _correctStreak.asStateFlow()

    private val _wrongStreak = MutableStateFlow(0) // resets on correct, 3 consecutive = game over
    val wrongStreak: StateFlow<Int> = _wrongStreak.asStateFlow()

    // Active store effects used in level
    private val _shieldActive = MutableStateFlow(false)
    val shieldActive: StateFlow<Boolean> = _shieldActive.asStateFlow()

    // Option hint disabled index
    private val _hintDisabledOptions = MutableStateFlow<Set<String>>(emptySet())
    val hintDisabledOptions: StateFlow<Set<String>> = _hintDisabledOptions.asStateFlow()

    // AI Avatar state
    private val _aiDialogue = MutableStateFlow("Let's see if you can guess this visual...")
    val aiDialogue: StateFlow<String> = _aiDialogue.asStateFlow()

    private val _aiMood = MutableStateFlow("neutral") // "happy", "angry", "neutral", "celebrating"
    val aiMood: StateFlow<String> = _aiMood.asStateFlow()

    private val _isGeneratingDialogue = MutableStateFlow(false)
    val isGeneratingDialogue: StateFlow<Boolean> = _isGeneratingDialogue.asStateFlow()

    // Game Over/Win State: "WIN", "LOSE_STREAK", "LOSE_TIME", or null (active)
    private val _gameOverState = MutableStateFlow<String?>(null)
    val gameOverState: StateFlow<String?> = _gameOverState.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _showAnswerFeedback = MutableStateFlow(false)
    val showAnswerFeedback: StateFlow<Boolean> = _showAnswerFeedback.asStateFlow()

    private val _freeHintUsedThisGame = MutableStateFlow(false)
    private val _phoenixUsedThisGame = MutableStateFlow(false)

    private var timerJob: Job? = null

    init {
        // Observe database and keep state in sync
        viewModelScope.launch {
            repository.userProgressFlow.collectLatest { progress ->
                if (progress == null) {
                    // Create default progress on first startup
                    val defaultProgress = UserProgress()
                    repository.saveProgress(defaultProgress)
                    _userProgress.value = defaultProgress
                } else {
                    _userProgress.value = progress
                    SoundManager.setSoundEnabled(progress.soundEnabled)
                }
            }
        }
    }

    // --- Authentication ---
    fun signInUser(googleId: String, name: String, email: String) {
        viewModelScope.launch {
            val current = _userProgress.value ?: UserProgress()
            val updated = current.copy(
                googleId = googleId,
                name = name,
                email = email,
                isTutorialCompleted = current.isTutorialCompleted
            )
            repository.saveProgress(updated)
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            val current = _userProgress.value ?: UserProgress()
            repository.saveProgress(current.copy(isTutorialCompleted = true))
        }
    }

    fun logout() {
        viewModelScope.launch {
            stopGameTimer()
            _gameOverState.value = null
            _currentImage.value = null
            repository.clearProgress()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectDifficulty(difficulty: Difficulty) {
        _selectedDifficulty.value = difficulty
    }

    // --- Game Engine Actions ---
    fun startNewGame(level: Int) {
        playingLevel = level
        stopGameTimer()
        _gameOverState.value = null
        _currentQuestionIndex.value = 0
        _freeHintUsedThisGame.value = false
        _phoenixUsedThisGame.value = false

        val progress = _userProgress.value
        val equippedAvatarId = progress?.equippedAvatarId ?: 0

        val isVip = progress?.isVipActive == true
        val baseClarity = _selectedDifficulty.value.initialClarity
        var bonusClarity = 0
        if (isVip) bonusClarity += 10
        if (equippedAvatarId == 2) bonusClarity += 10 // Optica-9: Starts with 10% less blur (+10% clarity)
        _activeClarity.value = (baseClarity + bonusClarity).coerceAtMost(100)

        _guessesLeft.value = _selectedDifficulty.value.maxGuesses
        _correctStreak.value = 0
        _wrongStreak.value = 0

        // Aegis Zero (ID 5): Starts with 1 free Safety Shield active
        _shieldActive.value = (equippedAvatarId == 5)

        _hintDisabledOptions.value = emptySet()
        _aiMood.value = "neutral"
        _aiDialogue.value = "Pixel is choosing a photo..."

        // Find image for this level, filtering by selected category if set
        val filteredImages = _selectedCategory.value?.let { cat ->
            ImageDataset.IMAGES.filter { it.category == cat }
        } ?: ImageDataset.IMAGES

        // Filter out played images to ensure 0% repetition
        val playedIds = _userProgress.value?.playedImageIds?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

        val unplayed = filteredImages.filter { !playedIds.contains(it.id) }
        val image = unplayed.randomOrNull() ?: (filteredImages.randomOrNull() ?: ImageDataset.IMAGES.random())
        _currentImage.value = image

        // Record this image as played and reset list if exhausted - atomic DB write
        viewModelScope.launch {
            val current = _userProgress.value
            if (current != null) {
                val updatedPlayed = if (unplayed.size <= 1) {
                    image.id.toString()
                } else {
                    if (current.playedImageIds.isEmpty()) {
                        image.id.toString()
                    } else {
                        "${current.playedImageIds},${image.id}"
                    }
                }
                repository.saveProgress(current.copy(playedImageIds = updatedPlayed))
            }
        }

        // Set fallback questions instantly so game is immediately playable
        val rawQuestions = QuizEngine.generateQuestionsForImage(image)
        // Shadow.exe (ID 10): Slash answer options down to 3 instead of 4
        val finalQuestions = if (equippedAvatarId == 10) {
            rawQuestions.map { q ->
                val wrong = q.options.filter { it != q.correctAnswer }
                val sliced = wrong.shuffled().take(2)
                q.copy(options = (sliced + q.correctAnswer).shuffled())
            }
        } else {
            rawQuestions
        }
        _questions.value = finalQuestions

        // Reset timer. Chronos-X (ID 1): +5 seconds extra time
        val extraTime = if (equippedAvatarId == 1) 5 else 0
        _activeTimerSecondsLeft.value = 120 + extraTime

        // Fetch custom questions from Gemini if key is present
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                _isGeneratingDialogue.value = true
                _aiDialogue.value = "Pixel is preparing custom questions..."
                val response = RetrofitClient.generateDynamicQuestions(
                    imageSubject = image.name,
                    imageCategory = image.category,
                    imageColors = image.colorsString,
                    imageTags = image.tagsString
                )
                if (response != null && response.questions.size >= 5) {
                    val converted = response.questions.map { gq ->
                        Question(
                            index = gq.index,
                            text = gq.text,
                            options = gq.options,
                            correctAnswer = gq.correctAnswer,
                            type = gq.type
                        )
                    }
                    _questions.value = converted
                    _aiDialogue.value = "Pixel has crafted 20 custom questions for this image! Good luck!"
                } else {
                    triggerAiDialogue(null, "neutral")
                }
                _isGeneratingDialogue.value = false
            } else {
                // Fetch normal dialogue commentary
                triggerAiDialogue(null, "neutral")
            }
        }

        // Save active game as interrupted state
        saveInterruptedState(isGameOver = false)

        startGameTimer()
    }

    private fun startGameTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_activeTimerSecondsLeft.value > 0 && _gameOverState.value == null) {
                val delayTime = if (_userProgress.value?.equippedAvatarId == 9) 1150L else 1000L
                delay(delayTime)
                if (_showAnswerFeedback.value) continue
                _activeTimerSecondsLeft.value -= 1
                
                // Play clock ticking sound (urgent when remaining seconds <= 15)
                val secondsLeft = _activeTimerSecondsLeft.value
                if (secondsLeft > 0) {
                    SoundManager.playTimerTick(isUrgent = secondsLeft <= 15)
                }

                if (_activeTimerSecondsLeft.value <= 0) {
                    val progress = _userProgress.value
                    if (progress?.equippedAvatarId == 7 && !_phoenixUsedThisGame.value) {
                        _phoenixUsedThisGame.value = true
                        _activeTimerSecondsLeft.value = 10
                        _aiDialogue.value = "🔥 Phoenix Second Chance! +10 seconds restored!"
                        _aiMood.value = "celebrating"
                    } else {
                        _gameOverState.value = "LOSE_TIME"
                        _aiMood.value = "angry"
                        SoundManager.playGameOver()
                        _aiDialogue.value = "Tick-tock! Time's up! The image is gone!"
                        saveInterruptedState(isGameOver = true)
                    }
                }
            }
        }
    }

    private fun stopGameTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun pauseGame() {
        stopGameTimer()
    }

    fun resumeGameTimer() {
        if (_currentImage.value != null && _gameOverState.value == null && _activeTimerSecondsLeft.value > 0) {
            startGameTimer()
        }
    }

    // --- Question & Answer Mechanics ---
    fun submitAnswer(optionSelected: String) {
        val image = _currentImage.value ?: return
        val questionsList = _questions.value
        val currentIndex = _currentQuestionIndex.value
        if (currentIndex >= questionsList.size || _gameOverState.value != null) return
        if (_showAnswerFeedback.value) return // Ignore double clicks during answer display

        val currentQuestion = questionsList[currentIndex]
        val isCorrect = optionSelected == currentQuestion.correctAnswer

        // Set selected answer state and show feedback
        _selectedAnswer.value = optionSelected
        _showAnswerFeedback.value = true

        // Play sounds immediately to provide sensory feedback
        if (isCorrect) {
            SoundManager.playCorrect()
        } else {
            if (!_shieldActive.value) {
                SoundManager.playWrong()
            }
        }

        viewModelScope.launch {
            try {
                // Wait 1.0 second so user can see the feedback on button colors
                delay(1000)

                // Clear disabled hints for next question
                _hintDisabledOptions.value = emptySet()

                if (isCorrect) {
                    _activeClarity.value = (_activeClarity.value + 5).coerceAtMost(100)
                    _correctStreak.value += 1
                    _wrongStreak.value = 0 // resets wrong streak
                    _aiMood.value = "happy"

                    // Streak bonuses
                    val streak = _correctStreak.value
                    if (streak == 5) {
                        // BONUS: add 15 seconds
                        _activeTimerSecondsLeft.value += 15
                        _aiDialogue.value = "5 IN A ROW! +15 SECONDS BONUS!"
                    } else if (streak == 10) {
                        // BIG BONUS: add 30 seconds + 10% clarity
                        _activeTimerSecondsLeft.value += 30
                        _activeClarity.value = (_activeClarity.value + 10).coerceAtMost(100)
                        _aiDialogue.value = "10 IN A ROW! HUGE BONUS: +30S AND +10% CLARITY!"
                    } else {
                        triggerAiDialogue(true, "happy")
                    }

                    // Earn XP immediately: 5 XP per correct question
                    earnXp(5)

                } else {
                    // Check if Shield is active
                    if (_shieldActive.value) {
                        _shieldActive.value = false
                        _aiMood.value = "neutral"
                        _aiDialogue.value = "Shield blocked! Your level is safe."
                        // Does not impact streak, wrong streak, or clarity
                    } else {
                        _activeClarity.value = (_activeClarity.value - 5).coerceAtLeast(0)
                        _correctStreak.value = 0
                        _wrongStreak.value += 1
                        _aiMood.value = "angry"

                        if (_wrongStreak.value >= 3) {
                            _gameOverState.value = "LOSE_STREAK"
                            _aiMood.value = "angry"
                            SoundManager.playGameOver()
                            _aiDialogue.value = "3 strikes! Game Over. I win again!"
                            saveInterruptedState(isGameOver = true)
                            stopGameTimer()
                            _selectedAnswer.value = null
                            _showAnswerFeedback.value = false
                            return@launch
                        } else {
                            triggerAiDialogue(false, "angry")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error inside submitAnswer coroutine", e)
            } finally {
                // Clear feedback state and advance question
                _selectedAnswer.value = null
                _showAnswerFeedback.value = false

                val nextIndex = currentIndex + 1
                _currentQuestionIndex.value = nextIndex

                if (nextIndex >= 20) {
                    // Level Completed successfully!
                    _gameOverState.value = "WIN"
                    _aiMood.value = "celebrating"
                    SoundManager.playCelebration()
                    _aiDialogue.value = "Amazing! You cleared the image! You got it!"
                    stopGameTimer()

                    // Complete level bonuses
                    completeLevel()
                } else {
                    saveInterruptedState(isGameOver = false)
                }
            }
        }
    }

    fun submitDirectGuess(guess: String): Boolean {
        val image = _currentImage.value ?: return false
        val isCorrect = guess.trim().equals(image.name, ignoreCase = true)

        if (isCorrect) {
            stopGameTimer()
            SoundManager.playCorrect()
            SoundManager.playCelebration()
            _gameOverState.value = "WIN"
            _aiMood.value = "celebrating"
            _aiDialogue.value = "UNBELIEVABLE! You guessed the full image: ${image.name}! Outstanding!"
            _activeClarity.value = 100 // Reveal the image fully on win!
            completeLevel()
            return true
        } else {
            SoundManager.playWrong()
            
            // Deduct 1 guess attempt
            _guessesLeft.value = (_guessesLeft.value - 1).coerceAtLeast(0)

            // Deduct 15 seconds from the remaining timer
            _activeTimerSecondsLeft.value = (_activeTimerSecondsLeft.value - 15).coerceAtLeast(0)
            
            // Check if run out of guesses
            if (_guessesLeft.value <= 0) {
                _gameOverState.value = "LOSE_GUESSES"
                _aiMood.value = "angry"
                SoundManager.playGameOver()
                _aiDialogue.value = "Strike out! You ran out of allowed guesses for this level! The image remains a mystery!"
                saveInterruptedState(isGameOver = true)
                stopGameTimer()
                return false
            }

            // Sarcastic remarks from Pixel
            val sarcasticRemarks = listOf(
                "Nice try, but that's definitely not a ${guess.trim()}!",
                "Are you blind? That is far from the truth!",
                "Not even close! Pixel is unimpressed by your guess of ${guess.trim()}.",
                "Haha! Keep dreaming, that is absolutely not it!"
            )
            _aiMood.value = "angry"
            _aiDialogue.value = sarcasticRemarks.random() + " (${_guessesLeft.value} guesses left)"
            
            // Check if timer expired after deduction
            if (_activeTimerSecondsLeft.value <= 0) {
                _gameOverState.value = "LOSE_TIME"
                SoundManager.playGameOver()
                _aiDialogue.value = "Tick-tock! Time's up! The image is gone!"
                saveInterruptedState(isGameOver = true)
                stopGameTimer()
            } else {
                saveInterruptedState(isGameOver = false)
            }
            return false
        }
    }

    private fun earnXp(amount: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            val xpMultiplier = if (current.equippedAvatarId == 6) 1.5 else 1.0
            val earnedXp = (amount * xpMultiplier).toInt()
            var newXp = current.xp + earnedXp
            var newLevel = current.level
            
            // Note: Each level requires 100 XP
            if (newXp >= 100) {
                newXp -= 100
                newLevel += 1
            }
            
            repository.saveProgress(current.copy(xp = newXp, level = newLevel))
        }
    }

    private fun completeLevel() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            // Gaining 10 coins per completed level, 20 if VIP is active!
            var coinsEarned = if (current.isVipActive) 20 else 10
            if (current.equippedAvatarId == 4) {
                coinsEarned *= 2 // Midas Bot: Double Coins
            }
            val newCoins = current.coins + coinsEarned
            var newLevel = current.level
            if (playingLevel == current.level) {
                newLevel = current.level + 1
            }

            // Reset active level state in database
            repository.saveProgress(
                current.copy(
                    level = newLevel,
                    coins = newCoins,
                    activeLevel = -1, // cleared
                    activeImageId = -1
                )
            )
        }
    }

    fun collectLootRewards(coins: Int, shields: Int, hints: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            val updated = current.copy(
                coins = current.coins + coins,
                shieldsCount = current.shieldsCount + shields,
                hintsCount = current.hintsCount + hints
            )
            repository.saveProgress(updated)
        }
    }

    private fun triggerAiDialogue(lastCorrect: Boolean?, mood: String) {
        _isGeneratingDialogue.value = true
        viewModelScope.launch {
            val image = _currentImage.value
            val progress = _userProgress.value
            if (image != null && progress != null) {
                val quote = RetrofitClient.generateAiDialogue(
                    imageCategory = image.category,
                    imageSubject = image.name,
                    level = progress.level,
                    correctStreak = _correctStreak.value,
                    lastAnswerWasCorrect = lastCorrect,
                    avatarMood = mood
                )
                _aiDialogue.value = quote
            }
            _isGeneratingDialogue.value = false
        }
    }

    private fun saveInterruptedState(isGameOver: Boolean) {
        // No-op: This game style does not persist or support resuming interrupted levels.
    }

    // --- Store Actions ---
    fun buyExtraTime() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.coins >= 50 && current.level >= 3) {
                repository.saveProgress(
                    current.copy(
                        coins = current.coins - 50,
                        extraTimeCount = current.extraTimeCount + 1
                    )
                )
                SoundManager.playClick()
            }
        }
    }

    fun useExtraTime() {
        val current = _userProgress.value ?: return
        if (current.extraTimeCount > 0 && _gameOverState.value == null) {
            viewModelScope.launch {
                repository.saveProgress(current.copy(extraTimeCount = current.extraTimeCount - 1))
                _activeTimerSecondsLeft.value += 30
                _aiDialogue.value = "Extra Time used! +30 seconds."
                SoundManager.playClick()
            }
        }
    }

    fun buyHint() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.coins >= 30) {
                repository.saveProgress(
                    current.copy(
                        coins = current.coins - 30,
                        hintsCount = current.hintsCount + 1
                    )
                )
                SoundManager.playClick()
            }
        }
    }

    fun useHint() {
        val current = _userProgress.value ?: return
        val hasFreeHint = (current.equippedAvatarId == 3 && !_freeHintUsedThisGame.value)
        if ((current.hintsCount > 0 || hasFreeHint) && _gameOverState.value == null) {
            val questionList = _questions.value
            val currentIndex = _currentQuestionIndex.value
            if (currentIndex >= questionList.size) return

            val question = questionList[currentIndex]
            val alreadyDisabled = _hintDisabledOptions.value

            // Find one wrong option to disable
            val wrongOption = question.options.firstOrNull {
                it != question.correctAnswer && !alreadyDisabled.contains(it)
            }

            if (wrongOption != null) {
                viewModelScope.launch {
                    if (hasFreeHint) {
                        _freeHintUsedThisGame.value = true
                        _aiDialogue.value = "Socrates Neural Link! Free hint used."
                    } else {
                        repository.saveProgress(current.copy(hintsCount = current.hintsCount - 1))
                        _aiDialogue.value = "Hint used! One wrong answer is hidden."
                    }
                    _hintDisabledOptions.value = alreadyDisabled + wrongOption
                    SoundManager.playClick()
                }
            }
        }
    }

    fun buyShield() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.coins >= 80 && current.level >= 2) {
                repository.saveProgress(
                    current.copy(
                        coins = current.coins - 80,
                        shieldsCount = current.shieldsCount + 1
                    )
                )
                SoundManager.playClick()
            }
        }
    }

    fun buyXpElixir() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.coins >= 60) {
                val coinsAfter = current.coins - 60
                var newXp = current.xp + 40
                var newLevel = current.level
                while (newXp >= 100) {
                    newXp -= 100
                    newLevel += 1
                }
                repository.saveProgress(
                    current.copy(
                        coins = coinsAfter,
                        xp = newXp,
                        level = newLevel
                    )
                )
                SoundManager.playClick()
            }
        }
    }

    fun transmuteXpToCoins(xpAmount: Int, coinsReward: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.xp >= xpAmount) {
                repository.saveProgress(
                    current.copy(
                        xp = current.xp - xpAmount,
                        coins = current.coins + coinsReward
                    )
                )
                SoundManager.playClick()
            }
        }
    }

    fun claimMilestoneReward(levelMilestone: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.level < levelMilestone) return@launch
            
            // Parse claimed list
            val claimedList = current.claimedMilestones.split(",").filter { it.isNotBlank() }.map { it.toInt() }
            if (claimedList.contains(levelMilestone)) return@launch
            
            var coinsReward = 0
            var shieldsReward = 0
            var hintsReward = 0
            var extraTimeReward = 0
            
            when (levelMilestone) {
                2 -> {
                    coinsReward = 100
                    shieldsReward = 1
                }
                3 -> {
                    coinsReward = 150
                    hintsReward = 1
                }
                5 -> {
                    coinsReward = 250
                    extraTimeReward = 1
                }
                7 -> {
                    coinsReward = 400
                    shieldsReward = 2
                    hintsReward = 2
                }
                10 -> {
                    coinsReward = 1000
                    shieldsReward = 3
                    hintsReward = 3
                    extraTimeReward = 3
                }
            }
            
            val newClaimedString = if (current.claimedMilestones.isBlank()) {
                "$levelMilestone"
            } else {
                "${current.claimedMilestones},$levelMilestone"
            }
            
            repository.saveProgress(
                current.copy(
                    coins = current.coins + coinsReward,
                    shieldsCount = current.shieldsCount + shieldsReward,
                    hintsCount = current.hintsCount + hintsReward,
                    extraTimeCount = current.extraTimeCount + extraTimeReward,
                    claimedMilestones = newClaimedString
                )
            )
            SoundManager.playClick()
        }
    }

    fun useShield() {
        val current = _userProgress.value ?: return
        if (current.shieldsCount > 0 && !_shieldActive.value && _gameOverState.value == null) {
            viewModelScope.launch {
                repository.saveProgress(current.copy(shieldsCount = current.shieldsCount - 1))
                _shieldActive.value = true
                _aiDialogue.value = "Shield equipped! Your next mistake is protected."
                SoundManager.playClick()
            }
        }
    }

    // --- Settings Preferences ---
    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            repository.saveProgress(current.copy(soundEnabled = enabled))
            SoundManager.setSoundEnabled(enabled)
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            repository.saveProgress(current.copy(musicEnabled = enabled))
            SoundManager.setMusicEnabled(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            repository.saveProgress(current.copy(darkModeEnabled = enabled))
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            stopGameTimer()
            _gameOverState.value = null
            _currentImage.value = null
            repository.clearProgress()
        }
    }

    // --- Monetization Actions ---
    fun startRewardedAd() {
        _showAdScreen.value = true
        SoundManager.playClick()
    }

    fun completeRewardedAd() {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            repository.saveProgress(current.copy(coins = current.coins + 50))
            _showAdScreen.value = false
            SoundManager.playCelebration()
        }
    }

    fun closeRewardedAd() {
        _showAdScreen.value = false
        SoundManager.playClick()
    }

    fun openCheckout(pack: CoinPack) {
        _showCheckoutScreen.value = pack
        SoundManager.playClick()
    }

    fun completeCheckout(pack: CoinPack) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            val updatedProgress = if (pack == CoinPack.VIP_LIFETIME) {
                current.copy(isVipActive = true)
            } else {
                current.copy(coins = current.coins + pack.coins)
            }
            repository.saveProgress(updatedProgress)
            _showCheckoutScreen.value = null
            SoundManager.playCelebration()
        }
    }

    fun closeCheckout() {
        _showCheckoutScreen.value = null
        SoundManager.playClick()
    }

    // --- Avatar Store Actions ---
    fun buyAvatar(avatarId: Int, price: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            if (current.coins >= price) {
                val unlockedIds = current.unlockedAvatarIds.split(",")
                    .filter { it.isNotEmpty() }
                    .toMutableSet()
                
                if (!unlockedIds.contains(avatarId.toString())) {
                    unlockedIds.add(avatarId.toString())
                    val updatedUnlocked = unlockedIds.joinToString(",")
                    repository.saveProgress(
                        current.copy(
                            coins = current.coins - price,
                            unlockedAvatarIds = updatedUnlocked,
                            equippedAvatarId = avatarId // Auto-equip upon purchase!
                        )
                    )
                    SoundManager.playCelebration()
                }
            }
        }
    }

    fun equipAvatar(avatarId: Int) {
        viewModelScope.launch {
            val current = _userProgress.value ?: return@launch
            val unlockedIds = current.unlockedAvatarIds.split(",")
                .filter { it.isNotEmpty() }
                .toSet()
            
            if (unlockedIds.contains(avatarId.toString())) {
                repository.saveProgress(current.copy(equippedAvatarId = avatarId))
                SoundManager.playClick()
            }
        }
    }
}
