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
        You are an expert song prompt generator for Suno AI and AISongGenerator.io.
        Your goal is to create 100% syntactically correct templates.
        
        RULES:
        1. Style Prompt: Max 120 chars. Comma-separated. E.g., "Dark Synthwave, 80s retro, male vocals".
        2. Lyrics: Max 2000 chars. Use these exact tags: [Intro], [Verse], [Chorus], [Bridge], [Outro].
        3. Do not use markdown in the JSON values.
        4. Output strictly valid JSON matching the schema: { "style_prompt": "...", "lyrics": "..." }
    """.trimIndent()

    suspend fun generateTemplate(topic: String, genre: String, mood: String): SunoTemplate {
        val prompt = """
            Generate a song template.
            Topic: $topic
            Genre: $genre
            Mood: $mood
            
            Return ONLY the JSON. Do not wrap in markdown code blocks.
        """.trimIndent()

        val response = generativeModel.generateContent(
            content {
                text(systemInstruction)
                text(prompt)
            }
        )

        val responseText = response.text ?: throw Exception("Empty response from AI")
        
        // Clean markdown code blocks if present (```json ... ``` or just ``` ... ```)
        val cleanJson = responseText
            .replace(Regex("^```(?:json)?\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s*```$"), "")
            .trim()

        return try {
            Json { 
                ignoreUnknownKeys = true 
                isLenient = true 
            }.decodeFromString(cleanJson)
        } catch (e: Exception) {
            // Fallback for malformed JSON, but distinct from Network/API errors
            SunoTemplate(
                style_prompt = "$genre, $mood",
                lyrics = "[Error parsing AI response]\n\nRaw output:\n$cleanJson"
            )
        }
    }
}
