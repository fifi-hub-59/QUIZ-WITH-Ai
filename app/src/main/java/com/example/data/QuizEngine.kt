package com.example.data

import kotlin.random.Random

data class Question(
    val index: Int, // 0 to 19
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val type: String
)

object QuizEngine {

    private val ALL_COLORS = listOf("Red", "Blue", "Green", "Yellow", "Orange", "Purple", "Pink", "Black", "White", "Grey", "Gold", "Silver", "Brown")
    private val ALL_CATEGORIES = listOf("Animal", "Food", "Nature", "Architecture", "People", "Object")

    fun generateQuestionsForImage(image: QuizImage, allImages: List<QuizImage> = ImageDataset.IMAGES): List<Question> {
        val questions = mutableListOf<Question>()
        val random = Random(image.id * 100) // Deterministic seed per image so questions are consistent

        // Let's generate exactly 20 questions
        for (i in 0 until 20) {
            val qType = when (i % 7) {
                0 -> "object"
                1 -> "color"
                2 -> "scene"
                3 -> "sentiment"
                4 -> "count"
                5 -> "orientation"
                else -> "category"
            }

            val question = when (qType) {
                "object" -> {
                    val correct = image.name
                    // Get 3 incorrect names from the dataset
                    val incorrect = allImages
                        .map { it.name }
                        .filter { it != correct }
                        .distinct()
                        .shuffled(random)
                        .take(3)
                    
                    val options = (incorrect + correct).shuffled(random)
                    val textTemplates = listOf(
                        "What is shown in the image?",
                        "Identify the main subject featured in this photo:",
                        "Which of the following describes this picture best?",
                        "What item or creature is the focus here?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "object"
                    )
                }
                "color" -> {
                    val primaryColor = image.colors.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "White"
                    val correct = primaryColor
                    // Get 3 incorrect colors
                    val incorrect = ALL_COLORS
                        .filter { it != correct && !image.colors.map { c -> c.lowercase() }.contains(it.lowercase()) }
                        .shuffled(random)
                        .take(3)
                    
                    val options = (incorrect + correct).shuffled(random)
                    val textTemplates = listOf(
                        "What is the predominant color of this subject?",
                        "Which of these colors is highly noticeable in this scene?",
                        "Select the main color tone shown in the image:",
                        "What color dominates this visual?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "color"
                    )
                }
                "scene" -> {
                    val correct = image.scene.replaceFirstChar { it.uppercase() }
                    val options = listOf("Indoor", "Outdoor", "Studio/Abstract", "Unclear").shuffled(random)
                    val textTemplates = listOf(
                        "Is this an indoor or outdoor scene?",
                        "Where was this photograph most likely taken?",
                        "What is the environment setting of this image?",
                        "Select the scene type depicted here:"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "scene"
                    )
                }
                "sentiment" -> {
                    val correct = image.sentiment.replaceFirstChar { it.uppercase() }
                    val options = listOf("Happy", "Sad", "Neutral", "Exciting").shuffled(random)
                    val textTemplates = listOf(
                        "What emotional tone or sentiment is expressed?",
                        "What vibe does this image radiate?",
                        "What is the overall emotional atmosphere?",
                        "Which of these sentiments matches the image vibe?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "sentiment"
                    )
                }
                "count" -> {
                    val correct = image.count.toString()
                    val correctInt = image.count
                    val incorrect = setOf(
                        (correctInt + 1).toString(),
                        (correctInt + 2).toString(),
                        (correctInt - 1).coerceAtLeast(0).toString(),
                        "4", "10", "50", "100"
                    ).filter { it != correct }.shuffled(random).take(3)
                    
                    val options = (incorrect + correct).shuffled(random)
                    val textTemplates = listOf(
                        "How many of these main objects can be counted in the image?",
                        "Select the correct quantity of the subject shown:",
                        "What is the count of items/creatures in this picture?",
                        "How many focal subjects exist in this frame?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "count"
                    )
                }
                "orientation" -> {
                    val correct = image.orientation.replaceFirstChar { it.uppercase() }
                    val options = listOf("Landscape", "Portrait", "Square", "Panoramic").shuffled(random)
                    val textTemplates = listOf(
                        "What is the orientation format of this image?",
                        "Is this image captured in landscape or portrait layout?",
                        "Select the layout framing of this picture:",
                        "What is the visual aspect style?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "orientation"
                    )
                }
                else -> { // category
                    val correct = image.category.replaceFirstChar { it.uppercase() }
                    val incorrect = ALL_CATEGORIES
                        .filter { it != correct }
                        .shuffled(random)
                        .take(3)
                    val options = (incorrect + correct).shuffled(random)
                    val textTemplates = listOf(
                        "Which primary category does this image fit into?",
                        "Select the visual classification of this photo:",
                        "Which group does this subject belong to?",
                        "What is the main topic group for this image?"
                    )
                    Question(
                        index = i,
                        text = textTemplates[random.nextInt(textTemplates.size)],
                        options = options,
                        correctAnswer = correct,
                        type = "category"
                    )
                }
            }
            questions.add(question)
        }
        return questions
    }
}
