package com.crypto.forecast.data.model

enum class ForecastPeriod(val label: String, val days: Int) {
    THREE_DAYS("3 Gün", 3),
    SEVEN_DAYS("7 Gün", 7),
    ONE_MONTH("1 Ay", 30),
    THREE_MONTHS("3 Ay", 90),
    ONE_YEAR("1 Yıl", 365)
}

enum class AccessType {
    FREE,
    REWARDED_AD,
    VIP_PURCHASE
}

data class ForecastModel(
    val id: String,
    val name: String,
    val description: String,
    val accessType: AccessType,
    val accuracyScore: String
)

data class CryptoItem(
    val id: String,
    val symbol: String,
    val name: String,
    val currentPriceUsd: Double,
    val change24hPercent: Double,
    val iconUrl: String = ""
)

data class ForecastPoint(
    val dayIndex: Int,
    val price: Double,
    val upperBand: Double,
    val lowerBand: Double
)

data class ForecastResult(
    val cryptoId: String,
    val modelId: String,
    val period: ForecastPeriod,
    val targetPrice: Double,
    val expectedChangePercent: Double,
    val sentiment: String, // Bullish, Bearish, Neutral
    val points: List<ForecastPoint>
)
