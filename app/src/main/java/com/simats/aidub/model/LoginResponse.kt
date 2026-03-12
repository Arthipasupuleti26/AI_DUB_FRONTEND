package com.simats.aidub.model
data class LoginResponse(
    val status: String,
    val message: String,
    val session_id: String,
    val user: UserData
) {
    data class UserData(
        val id: String,
        val name: String,
        val email: String,
        val token: String
    )
}
