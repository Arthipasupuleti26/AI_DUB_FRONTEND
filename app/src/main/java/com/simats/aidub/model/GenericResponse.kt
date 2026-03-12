package com.simats.aidub.model

data class GenericResponse(
    val success: Boolean,
    val audio: String? = null,
    val voice: String? = null,

    // ⭐ FROM MERGE VIDEO API
    val stream: String? = null,
    val download: String? = null
)
