package com.example.sunotemplategen.domain

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SunoTemplate(
    val style_prompt: String,
    val lyrics: String
)

class SunoPromptGenerator(
    private val apiKey: String
) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    private val systemInstruction = """
        You are a world-class Electronic Music Producer specializing in Melodic House, Techno, and Deep Groove.
        Your goal is to create 3 DISTINCT, HIGH-FIDELITY song templates optimized for Suno AI/AISongGenerator.io.
        
        CRITICAL STYLE GUIDE:
        - VIBE: "Upper class", expensive production, complex textures, rich, full, "Grooving as F***".
        - AUDIO: Avoid "AI-sounding" genericism. Use terms like: "Analog warmth", "Tape saturation", "Moog Bass", "Roland TR-909", "Polyrhythmic percussion", "Spatial audio".
        - GENRE FOCUS: Melodic House, Peak-time Techno, Deep Tech.
        
        STRICT FORMATTING RULES:
        1. OUTPUT: A JSON ARRAY containing exactly 3 objects.
        2. Style Prompt: MAX 120 characters. Intense, descriptive, comma-separated.
        3. Lyrics/Structure: MAX 2000 characters. Use complex structures: [Intro], [Build-up], [Drop], [Breakdown], [Peak], [Outro].
        
        Example JSON Structure:
        [
          { "style_prompt": "...", "lyrics": "..." },
          { "style_prompt": "...", "lyrics": "..." },
          { "style_prompt": "...", "lyrics": "..." }
        ]
    """.trimIndent()

    suspend fun generateTemplate(topic: String, genre: String, mood: String): List<SunoTemplate> {
        val prompt = """
            Generate 3 unique templates.
            Topic: $topic
            Genre Override: $genre (Must blend with Techno/House/Groove)
            Mood: $mood
            
            Return ONLY the JSON ARRAY.
        """.trimIndent()

        val response = generativeModel.generateContent(
            content {
                text(systemInstruction)
                text(prompt)
            }
        )

        val responseText = response.text ?: throw Exception("Empty response from AI")
        
        // Clean markdown code blocks
        val cleanJson = responseText
            .replace(Regex("^```(?:json)?\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s*```$"), "")
            .trim()

        return try {
            Json { 
                ignoreUnknownKeys = true 
                isLenient = true 
            }.decodeFromString<List<SunoTemplate>>(cleanJson)
        } catch (e: Exception) {
            // Fallback: Return a single error item in a list
            listOf(
                SunoTemplate(
                    style_prompt = "Error Parsing",
                    lyrics = "Raw output:\n$cleanJson\n\nError: ${e.message}"
                )
            )
        }
    }
}
