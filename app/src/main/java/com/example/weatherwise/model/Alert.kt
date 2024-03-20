package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.PrimaryKey



data class Alert(

    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String> = listOf()
)


