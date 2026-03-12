package com.simats.aidub.model

data class EmotionResponse(
    val success: Boolean,
    val emotion_happiness: Int?,
    val emotion_sadness: Int?,
    val emotion_anger: Int?,
    val emotion_neutral: Int?
)