package com.simats.aidub.model

data class RegisterResponse(
    val status: String,
    val message: String,
    val user: User?,
    val token: String?
)

data class User(
    val id: String,
    val name: String,
    val email: String
)
