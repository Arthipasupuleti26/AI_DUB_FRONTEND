package com.simats.aidub.model

/**
 * Data class representing a dubbing project.
 * 
 * Processing stages:
 * - "extracting_audio" - Audio is being extracted from video
 * - "transcribing" - Speech is being transcribed
 * - "translating" - Text is being translated
 * - "generating_voice" - AI voice is being generated
 * - "complete" - Project is ready
 */
data class Project(
    val id: String,
    val title: String,
    val description: String = "",
    val videoUri: String,
    val audioPath: String? = null,
    val originalLanguage: String = "Telugu",
    val targetLanguage: String = "English",
    val status: String = "Processing", // "Processing" or "Ready"
    val processingStage: String = "extracting_audio", // Current stage in processing pipeline
    val processingProgress: Int = 0, // 0-100 progress within current stage
    val transcribedText: String? = null,
    val translatedText: String? = null,
    val detectedEmotion: String? = null,
    val selectedVoice: String? = null,
    val selectedStyle: String? = null,
    val happiness: Int = 50,
    val excitement: Int = 50,
    val sadness: Int = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val selectedResolution: String? = "1080p",
    val selectedFormat: String? = "MP4",
    val createdAt: Long = System.currentTimeMillis()
)

