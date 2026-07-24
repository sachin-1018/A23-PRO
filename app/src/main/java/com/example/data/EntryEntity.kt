package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marketName: String,
    val date: String, // DD-MM-YYYY format
    val resultRaw: String, // e.g. "149-45-140" or "***-**-***"
    val openPana: String = "",
    val jodi: String = "",
    val closePana: String = "",
    val isHoliday: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
