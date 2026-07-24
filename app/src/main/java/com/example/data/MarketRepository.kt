package com.example.data

import kotlinx.coroutines.flow.Flow

class MarketRepository(
    private val marketDao: MarketDao,
    private val entryDao: EntryDao
) {
    val allMarkets: Flow<List<MarketEntity>> = marketDao.getAllMarkets()
    val allEntries: Flow<List<EntryEntity>> = entryDao.getAllEntries()

    fun getEntriesForMarket(marketName: String): Flow<List<EntryEntity>> {
        return entryDao.getEntriesForMarket(marketName)
    }

    suspend fun addMarket(market: MarketEntity): Long {
        return marketDao.insertMarket(market)
    }

    suspend fun updateMarket(market: MarketEntity) {
        marketDao.updateMarket(market)
    }

    suspend fun deleteMarket(market: MarketEntity) {
        marketDao.deleteMarket(market)
    }

    suspend fun addOrUpdateEntry(entry: EntryEntity): Long {
        val existing = entryDao.getEntryForMarketAndDate(entry.marketName, entry.date)
        return if (existing != null) {
            val updated = entry.copy(id = existing.id)
            entryDao.updateEntry(updated)
            existing.id
        } else {
            entryDao.insertEntry(entry)
        }
    }

    suspend fun deleteEntry(entry: EntryEntity) {
        entryDao.deleteEntry(entry)
    }

    suspend fun deleteEntryById(id: Long) {
        entryDao.deleteEntryById(id)
    }

    /**
     * Seeds initial markets and historical entry data if empty
     */
    suspend fun seedInitialDataIfEmpty() {
        // We handle seeding on a background thread
        val existingMarkets = marketDao.getMarketByName("KALYAN")
        if (existingMarkets == null) {
            // Seed Markets
            val mKalyan = MarketEntity(name = "KALYAN", openTime = "03:45 PM", closeTime = "05:45 PM")
            val mShridevi = MarketEntity(name = "SHRIDEVI", openTime = "11:35 AM", closeTime = "12:35 PM")
            val mSvr = MarketEntity(name = "SVR", openTime = "01:00 PM", closeTime = "03:00 PM")
            val mMainBazar = MarketEntity(name = "MAIN BAZAR", openTime = "09:35 PM", closeTime = "12:05 AM")
            val mRajdhani = MarketEntity(name = "RAJDHANI NIGHT", openTime = "07:15 PM", closeTime = "09:15 PM")

            marketDao.insertMarket(mKalyan)
            marketDao.insertMarket(mShridevi)
            marketDao.insertMarket(mSvr)
            marketDao.insertMarket(mMainBazar)
            marketDao.insertMarket(mRajdhani)

            // Seed Entries for KALYAN
            val kalyanEntries = listOf(
                EntryEntity(marketName = "KALYAN", date = "11-07-2026", resultRaw = "335-16-790", openPana = "335", jodi = "16", closePana = "790"),
                EntryEntity(marketName = "KALYAN", date = "12-07-2026", resultRaw = "149-45-140", openPana = "149", jodi = "45", closePana = "140"),
                EntryEntity(marketName = "KALYAN", date = "13-07-2026", resultRaw = "445-36-260", openPana = "445", jodi = "36", closePana = "260"),
                EntryEntity(marketName = "KALYAN", date = "14-07-2026", resultRaw = "***-**-***", openPana = "***", jodi = "**", closePana = "***", isHoliday = true),
                EntryEntity(marketName = "KALYAN", date = "15-07-2026", resultRaw = "125-84-590", openPana = "125", jodi = "84", closePana = "590"),
                EntryEntity(marketName = "KALYAN", date = "16-07-2026", resultRaw = "129-20-370", openPana = "129", jodi = "20", closePana = "370"),
                EntryEntity(marketName = "KALYAN", date = "17-07-2026", resultRaw = "189-84-266", openPana = "189", jodi = "84", closePana = "266"),
                EntryEntity(marketName = "KALYAN", date = "18-07-2026", resultRaw = "149-47-133", openPana = "149", jodi = "47", closePana = "133"),
                EntryEntity(marketName = "KALYAN", date = "19-07-2026", resultRaw = "235-56-128", openPana = "235", jodi = "56", closePana = "128")
            )
            kalyanEntries.forEach { entryDao.insertEntry(it) }

            // Seed Entries for SHRIDEVI
            val shrideviEntries = listOf(
                EntryEntity(marketName = "SHRIDEVI", date = "12-07-2026", resultRaw = "234-91-128", openPana = "234", jodi = "91", closePana = "128"),
                EntryEntity(marketName = "SHRIDEVI", date = "13-07-2026", resultRaw = "110-23-456", openPana = "110", jodi = "23", closePana = "456"),
                EntryEntity(marketName = "SHRIDEVI", date = "14-07-2026", resultRaw = "678-15-230", openPana = "678", jodi = "15", closePana = "230"),
                EntryEntity(marketName = "SHRIDEVI", date = "15-07-2026", resultRaw = "140-58-233", openPana = "140", jodi = "58", closePana = "233"),
                EntryEntity(marketName = "SHRIDEVI", date = "16-07-2026", resultRaw = "345-22-110", openPana = "345", jodi = "22", closePana = "110"),
                EntryEntity(marketName = "SHRIDEVI", date = "17-07-2026", resultRaw = "220-40-145", openPana = "220", jodi = "40", closePana = "145"),
                EntryEntity(marketName = "SHRIDEVI", date = "18-07-2026", resultRaw = "156-27-289", openPana = "156", jodi = "27", closePana = "289")
            )
            shrideviEntries.forEach { entryDao.insertEntry(it) }
        }
    }
}
