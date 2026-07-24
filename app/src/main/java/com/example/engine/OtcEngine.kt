package com.example.engine

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ParsedEntry(
    val dateStr: String,
    val resultRaw: String,
    val jodiInt: Int, // e.g. 45
    val jodiStr: String, // "45"
    val openDigit: Int, // 4
    val closeDigit: Int, // 5
    val isHoliday: Boolean
)

data class DayReportItem(
    val dateStr: String,
    val otcList: List<Int>,
    val otcStr: String, // "0 1 5 6"
    val jodiStr: String, // "56 65"
    val resultRaw: String, // "149-47-133"
    val isPass: Boolean
)

data class TodayPrediction(
    val mainOtc: String, // "0, 9"
    val supportOtc: String, // "0, 4, 5, 9"
    val superJodi: String, // "01, 98"
    val safeDay: String, // "Monday"
    val lastEntryDate: String,
    val lastJodi: String
)

object OtcEngine {

    fun parseRawResult(dateStr: String, raw: String): ParsedEntry {
        if (raw.contains("***") || raw.isBlank()) {
            return ParsedEntry(
                dateStr = dateStr,
                resultRaw = "***-**-***",
                jodiInt = -1,
                jodiStr = "**",
                openDigit = -1,
                closeDigit = -1,
                isHoliday = true
            )
        }

        // Try extracting numbers from formats like "149-45-140" or "149 45 140" or "45"
        val parts = raw.split("-", "/", " ").map { it.trim() }.filter { it.isNotEmpty() }
        var jodiString = ""
        
        if (parts.size >= 2) {
            // Second part is usually Jodi, e.g. "45"
            val digitsOnly = parts[1].filter { it.isDigit() }
            if (digitsOnly.length >= 2) {
                jodiString = digitsOnly.substring(0, 2)
            } else if (digitsOnly.isNotEmpty()) {
                jodiString = digitsOnly.padStart(2, '0')
            }
        } else if (parts.size == 1) {
            val digitsOnly = parts[0].filter { it.isDigit() }
            if (digitsOnly.length >= 2) {
                jodiString = digitsOnly.substring(0, 2)
            }
        }

        if (jodiString.length < 2) {
            return ParsedEntry(
                dateStr = dateStr,
                resultRaw = raw,
                jodiInt = -1,
                jodiStr = "**",
                openDigit = -1,
                closeDigit = -1,
                isHoliday = true
            )
        }

        val jodiVal = jodiString.toIntOrNull() ?: -1
        val openD = Character.getNumericValue(jodiString[0])
        val closeD = Character.getNumericValue(jodiString[1])

        return ParsedEntry(
            dateStr = dateStr,
            resultRaw = raw,
            jodiInt = jodiVal,
            jodiStr = jodiString,
            openDigit = openD,
            closeDigit = closeD,
            isHoliday = false
        )
    }

    /**
     * Calculates 7-day or full historical report card
     */
    fun calculateReportCard(entries: List<ParsedEntry>): List<DayReportItem> {
        val validRecords = entries.filter { !it.isHoliday && it.jodiInt != -1 }
        if (validRecords.size < 2) return emptyList()

        val reportList = mutableListOf<DayReportItem>()

        for (i in 1 until validRecords.size) {
            val prev = validRecords[i - 1].jodiInt
            val curr = validRecords[i]

            val pOpen = prev / 10
            val pClose = prev % 10
            val pOpenCut = (pOpen + 5) % 10
            val pCloseCut = (pClose + 5) % 10

            val pAnk = listOf(pOpen, pClose, pOpenCut, pCloseCut)
            val neighbors = mutableSetOf<Int>()
            for (a in pAnk) {
                neighbors.add((a - 1 + 10) % 10)
                neighbors.add((a + 1) % 10)
            }

            val isPass = (curr.openDigit in neighbors || curr.closeDigit in neighbors)

            // Generate calculated OTC & Jodi prediction that was issued for that day
            val valSq = String.format(Locale.US, "%04d", prev * prev)
            val base1 = Character.getNumericValue(valSq[valSq.length - 2])
            val base2 = Character.getNumericValue(valSq[valSq.length - 1])
            
            val otcSet = listOf(base1, base2, (base1 + 5) % 10, (base2 + 5) % 10).distinct().sorted()
            val otcStr = otcSet.joinToString(" ")
            
            val j1 = "${base1}${(base1 + 1) % 10}"
            val j2 = "${base2}${(base2 - 1 + 10) % 10}"
            val jodiStr = "$j1 $j2"

            reportList.add(
                DayReportItem(
                    dateStr = curr.dateStr,
                    otcList = otcSet,
                    otcStr = "( $otcStr )",
                    jodiStr = jodiStr,
                    resultRaw = curr.resultRaw,
                    isPass = isPass
                )
            )
        }

        return reportList.reversed() // Most recent first
    }

    /**
     * Today's Prediction Formula based on main2.py
     */
    fun calculateTodayPrediction(entries: List<ParsedEntry>): TodayPrediction {
        val validRecords = entries.filter { !it.isHoliday && it.jodiInt != -1 }
        if (validRecords.isEmpty()) {
            return TodayPrediction(
                mainOtc = "0, 9",
                supportOtc = "0, 4, 5, 9",
                superJodi = "01, 98",
                safeDay = "Monday",
                lastEntryDate = "N/A",
                lastJodi = "N/A"
            )
        }

        val lastRec = validRecords.last()
        val jodi = lastRec.jodiInt
        val valSq = String.format(Locale.US, "%04d", jodi * jodi)

        val otcBase1 = Character.getNumericValue(valSq[valSq.length - 2])
        val otcBase2 = Character.getNumericValue(valSq[valSq.length - 1])

        val otcFinal = listOf(
            otcBase1,
            otcBase2,
            (otcBase1 + 5) % 10,
            (otcBase2 + 5) % 10
        ).distinct().sorted()

        val mainOtc = "$otcBase1, $otcBase2"
        val supportOtc = otcFinal.joinToString(", ")

        val j1 = "${otcBase1}${(otcBase1 + 1) % 10}"
        val j2 = "${otcBase2}${(otcBase2 - 1 + 10) % 10}"
        val superJodi = "$j1, $j2"

        val safeDay = calculateMarketSafeDay(validRecords)

        return TodayPrediction(
            mainOtc = mainOtc,
            supportOtc = supportOtc,
            superJodi = superJodi,
            safeDay = safeDay,
            lastEntryDate = lastRec.dateStr,
            lastJodi = lastRec.jodiStr
        )
    }

    /**
     * Calculates the safest day of the week with highest accuracy
     */
    private fun calculateMarketSafeDay(records: List<ParsedEntry>): String {
        val daysStat = mutableMapOf(
            "Monday" to 0,
            "Tuesday" to 0,
            "Wednesday" to 0,
            "Thursday" to 0,
            "Friday" to 0,
            "Saturday" to 0
        )

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        for (i in 1 until records.size) {
            try {
                val dt: Date? = sdf.parse(records[i].dateStr)
                if (dt != null) {
                    val cal = Calendar.getInstance().apply { time = dt }
                    val dayName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> "Monday"
                        Calendar.TUESDAY -> "Tuesday"
                        Calendar.WEDNESDAY -> "Wednesday"
                        Calendar.THURSDAY -> "Thursday"
                        Calendar.FRIDAY -> "Friday"
                        Calendar.SATURDAY -> "Saturday"
                        else -> "Monday"
                    }

                    val prev = records[i - 1].jodiInt
                    val pOpen = prev / 10
                    val pClose = prev % 10
                    val pPool = listOf(pOpen, pClose, (pOpen + 5) % 10, (pClose + 5) % 10)
                    
                    val neighbors = mutableListOf<Int>()
                    for (a in pPool) {
                        neighbors.add((a - 1 + 10) % 10)
                        neighbors.add((a + 1) % 10)
                    }

                    if (records[i].openDigit in neighbors || records[i].closeDigit in neighbors) {
                        daysStat[dayName] = (daysStat[dayName] ?: 0) + 1
                    }
                }
            } catch (_: Exception) {
            }
        }

        return daysStat.maxByOrNull { it.value }?.key ?: "Monday"
    }

    /**
     * Smart Split Helper: Analyzes Pana & Jodi digits
     */
    fun smartSplit(rawResult: String): Triple<String, String, String> {
        if (rawResult.contains("***")) return Triple("***", "**", "***")
        val parts = rawResult.split("-", "/", " ").map { it.trim() }
        val openPana = if (parts.isNotEmpty()) parts[0] else ""
        val jodi = if (parts.size >= 2) parts[1] else ""
        val closePana = if (parts.size >= 3) parts[2] else ""
        return Triple(openPana, jodi, closePana)
    }
}
