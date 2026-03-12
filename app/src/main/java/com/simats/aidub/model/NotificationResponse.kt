package com.simats.aidub.model

data class NotificationResponse(
    val success: Boolean,
    val notifications: List<NotificationItem>
)
