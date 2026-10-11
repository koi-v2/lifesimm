package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val savedTimestamp: Long,
    val playerName: String,
    val day: Int,
    val careerTitle: String,
    val money: Long,
    val stateJson: String
)
