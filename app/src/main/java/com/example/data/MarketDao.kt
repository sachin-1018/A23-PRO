package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {
    @Query("SELECT * FROM markets WHERE isActive = 1 ORDER BY id ASC")
    fun getAllMarkets(): Flow<List<MarketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarket(market: MarketEntity): Long

    @Update
    suspend fun updateMarket(market: MarketEntity)

    @Delete
    suspend fun deleteMarket(market: MarketEntity)

    @Query("SELECT * FROM markets WHERE name = :name LIMIT 1")
    suspend fun getMarketByName(name: String): MarketEntity?
}
