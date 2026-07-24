package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE marketName = :marketName ORDER BY id DESC")
    fun getEntriesForMarket(marketName: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<EntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity): Long

    @Update
    suspend fun updateEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Query("SELECT * FROM entries WHERE marketName = :marketName AND date = :date LIMIT 1")
    suspend fun getEntryForMarketAndDate(marketName: String, date: String): EntryEntity?

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}
