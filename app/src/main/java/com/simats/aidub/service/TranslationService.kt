package com.simats.aidub.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Service to handle language translation.
 */
class TranslationService {

    private val translations = mapOf(
        "నమస్కారం అండి, ఈ వీడియోకి స్వాగతం." to "Hello Everyone, Welcome to this video.",
        "ఈ రోజు మనం ఒక ముఖ్యమైన విషయం గురించి చర్చిద్దాం." to "Today we will discuss an important topic.",
        "మీరు చూస్తున్న ఈ దృశ్యం చాలా అద్భుతంగా ఉంది కదా?" to "The scene you are seeing is wonderful, right?",
        "ఇక్కడ జరుగుతున్న విధానం మనకు చాలా నేర్పుతుంది." to "The process happening here teaches us a lot.",
        "ప్రతి ఒక్కరూ దీని గురించి తెలుసుకోవాలి." to "Everyone should know about this.",
        "మీకు నచ్చితే తప్పకుండా లైక్ చేయండి." to "If you like it, please do like the video.",
        "మరిన్ని వివరాల కోసం కామెంట్ సెక్షన్ లో అడగండి." to "Ask in the comment section for more details.",
        "నేను మీకు పూర్తి సమాచారం ఇస్తాను." to "I will give you complete information.",
        "ధన్యవాదాలు." to "Thank you."
    )

    fun translateLines(teluguText: String): Flow<Pair<String, String>> = flow {
        val lines = teluguText.split("\n").filter { it.isNotBlank() }
        
        for (line in lines) {
            val translation = translations[line] ?: "Processing with HappyScribe AI..."
            // Simulate HappyScribe processing time (AI Magic)
            delay((1200..2500).random().toLong())
            emit(line to translation)
        }
    }
}
