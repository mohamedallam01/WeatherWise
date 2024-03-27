package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alert_table")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var senderName: String = "",
    var event: String = "",
    var start: Long = 0,
    var end: Long = 0,
    var description: String = "",
    //val tags: List<String> = listOf()
)


