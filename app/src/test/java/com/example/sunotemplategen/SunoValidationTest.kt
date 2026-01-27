package com.example.sunotemplategen

import com.example.sunotemplategen.domain.SunoTemplate
import org.junit.Test
import org.junit.Assert.*

class SunoValidationTest {

    @Test
    fun `validate style prompt length`() {
        val template = SunoTemplate(
            style_prompt = "Upbeat Pop, Female Vocals, Summer Hits, Radio Ready, 2024, High Energy, Dance, Electronic, Synth-pop, Catchy Hook", // Too long?
            lyrics = "[Intro]\n..."
        )
        
        // Suno/AISongGenerator recommends < 120 chars
        assertTrue("Style prompt should be under 120 chars", template.style_prompt.length <= 120)
    }

    @Test
    fun `validate lyrics tags`() {
        val lyrics = """
            [Intro]
            (Music starts)
            
            [Verse 1]
            Hello world
            
            [Chorus]
            Singing loud
            
            [Outro]
            Goodbye
        """.trimIndent()
        
        val requiredTags = listOf("[Intro]", "[Verse", "[Chorus]", "[Outro]")
        
        requiredTags.forEach { tag ->
            assertTrue("Lyrics missing tag: $tag", lyrics.contains(tag))
        }
    }
}
