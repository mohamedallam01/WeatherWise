package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alert_table")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    //val tags: List<String> = listOf()
)


