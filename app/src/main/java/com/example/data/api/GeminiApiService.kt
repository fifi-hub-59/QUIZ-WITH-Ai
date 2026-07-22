package com.example.data.api

import com.example.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
        retrofit
    }

    suspend fun generateAiDialogue(
        imageCategory: String,
        imageSubject: String,
        level: Int,
        correctStreak: Int,
        lastAnswerWasCorrect: Boolean?,
        avatarMood: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getFallbackDialogue(avatarMood)
        }

        val prompt = """
            You are a playful and competitive AI opponent named 'Pixel' hosting a visual guessing quiz game called 'Guess the Image'.
            The player is guessing a blurred image.
            
            Game State details:
            - Current Level: $level
            - Current Question Streak of Correct Answers: $correctStreak
            - Subject of the Image (secret to player, but you know): $imageSubject
            - Category: $imageCategory
            - Last player answer status: ${if (lastAnswerWasCorrect == null) "Game Started" else if (lastAnswerWasCorrect) "CORRECT" else "INCORRECT"}
            - Your current facial expression / mood: $avatarMood
            
            Based on this, write a short, punchy, reactive comment to the player (max 15 words). 
            Be witty, friendly-competitive, and react directly to whether they got it right/wrong, or if they are on a high streak!
            Do not reveal the secret image subject!
            
            Incorporate or match some of these catchphrases when applicable:
            - "OH MAN", "YOU ARE SMART", or "I LOVE YOU" (when player gets it right or is smart).
            - "GO OUT", "I HATE", or "HOW DOING" (when player gets it wrong).
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.8f, maxOutputTokens = 60),
            systemInstruction = Content(parts = listOf(Part(text = "You are a witty, talkative AI opponent named Pixel in a mobile quiz game. Keep responses under 15 words.")))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text.isNullOrBlank()) getFallbackDialogue(avatarMood) else text.trim()
        } catch (e: Exception) {
            getFallbackDialogue(avatarMood)
        }
    }

    suspend fun generateDynamicQuestions(
        imageSubject: String,
        imageCategory: String,
        imageColors: String,
        imageTags: String
    ): GeminiQuizResponse? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null
        }

        val prompt = """
            You are a professional quiz designer. Generate exactly 20 diverse, engaging multiple-choice questions for an image of a '$imageSubject' in the category '$imageCategory'.
            
            Image details:
            - Subject: $imageSubject
            - Category: $imageCategory
            - Primary colors: $imageColors
            - Search tags: $imageTags
            
            Generate a variety of question types:
            - 'object': identifying the object or features
            - 'color': about colors
            - 'scene': indoor vs outdoor
            - 'count': quantity of elements
            - 'sentiment': mood/vibe
            - 'trivia': interesting facts or science about the subject
            
            Every question MUST have:
            - index: integer from 0 to 19
            - text: question string in English
            - options: list of exactly 4 unique choices in English
            - correctAnswer: string matching one of the options exactly in English
            - type: string (one of 'object', 'color', 'scene', 'sentiment', 'count', 'category', 'trivia')
            
            Return a valid JSON object matching this schema:
            {
              "questions": [
                {
                  "index": 0,
                  "text": "...",
                  "options": ["...", "...", "...", "..."],
                  "correctAnswer": "...",
                  "type": "..."
                }
              ]
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.7f,
                maxOutputTokens = 2048,
                responseMimeType = "application/json"
            ),
            systemInstruction = Content(parts = listOf(Part(text = "You are a professional quiz question generator. Return ONLY strict JSON matching the schema, with no additional commentary, no markdown formatting, and all text in English.")))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text.isNullOrBlank()) null else {
                val moshi = com.squareup.moshi.Moshi.Builder().build()
                val adapter = moshi.adapter(GeminiQuizResponse::class.java)
                adapter.fromJson(text)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getFallbackDialogue(mood: String): String {
        return when (mood) {
            "happy" -> listOf(
                "OH MAN! YOU ARE SMART!",
                "I LOVE YOU! That's correct!",
                "Spot on! You're getting good at this.",
                "Nice one! That blur is fading fast.",
                "Correct! My virtual processor is impressed."
            ).random()
            "angry" -> listOf(
                "GO OUT! I HATE wrong answers!",
                "HOW DOING? That was way off!",
                "Hah! Not even close!",
                "Wrong! Better luck with the next pixel.",
                "Ouch, that's going to cost you!"
            ).random()
            "celebrating" -> listOf(
                "Incredible! Level complete!",
                "Victory is yours! But can you beat the next level?",
                "Magnificent! You cleared the image!",
                "OH MAN! You actually beat me!"
            ).random()
            else -> listOf(
                "Welcome! Let's see if you can guess this one.",
                "Can you reveal the secret photo?",
                "HOW DOING? Ready to match wits with me?",
                "Level started. Fire away!"
            ).random()
        }
    }
}
