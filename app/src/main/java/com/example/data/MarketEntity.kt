package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markets")
data class MarketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val openTime: String,
    val closeTime: String,
    val isActive: Boolean = true
)
