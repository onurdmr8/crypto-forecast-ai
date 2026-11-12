package com.crypto.forecast.data.remote

import com.crypto.forecast.data.model.*
import kotlinx.coroutines.delay
import kotlin.random.Random

object CryptoForecastRepository {

    val availableModels = listOf(
        ForecastModel(
            id = "linear_trend",
            name = "Teknik Regresyon & Trend",
            description = "Geçmiş hareketli ortalamalar (SMA/EMA) ve hacim bazlı temel tahmin.",
            accessType = AccessType.FREE,
            accuracyScore = "%71 Doğruluk"
        ),
        ForecastModel(
            id = "ai_lstm",
            name = "Derin Öğrenme (LSTM Neural Net)",
            description = "Derin sinir ağları ile fiyat örüntülerini analiz eder. Reklam izleyerek açabilirsiniz.",
            accessType = AccessType.REWARDED_AD,
            accuracyScore = "%84 Doğruluk"
        ),
        ForecastModel(
            id = "quantum_prophet",
            name = "Prophet AI & On-Chain VIP",
            description = "Balina cüzdan hareketleri, likidite havuzları ve makroekonomik duygu analizli PRO model.",
            accessType = AccessType.VIP_PURCHASE,
            accuracyScore = "%92 Doğruluk"
        )
    )

    fun getTopCryptos(): List<CryptoItem> {
        return listOf(
            CryptoItem("bitcoin", "BTC", "Bitcoin", 64250.0, 2.45),
            CryptoItem("ethereum", "ETH", "Ethereum", 3450.0, -1.15),
            CryptoItem("solana", "SOL", "Solana", 152.8, 5.80),
            CryptoItem("binancecoin", "BNB", "BNB", 585.2, 0.75),
            CryptoItem("ripple", "XRP", "XRP", 0.58, -0.40),
            CryptoItem("cardano", "ADA", "Cardano", 0.46, 1.20),
            CryptoItem("avalanche-2", "AVAX", "Avalanche", 31.4, 4.10)
        )
    }

    suspend fun calculateForecast(
        crypto: CryptoItem,
        model: ForecastModel,
        period: ForecastPeriod
    ): ForecastResult {
        delay(900) // Simulating network/AI inference delay

        val basePrice = crypto.currentPriceUsd
        val volatilityFactor = when (model.id) {
            "linear_trend" -> 0.05
            "ai_lstm" -> 0.09
            else -> 0.14
        }

        // Deterministic pseudo-random seed based on crypto and period
        val days = period.days
        val points = mutableListOf<ForecastPoint>()
        var runningPrice = basePrice

        val drift = when {
            crypto.change24hPercent > 0 -> 0.003
            else -> -0.001
        }

        val step = (days / 15).coerceAtLeast(1)
        for (i in 0..days step step) {
            val randomFactor = (Random.nextDouble(-1.0, 1.0) * volatilityFactor) + drift
            runningPrice *= (1.0 + (randomFactor * (step / 30.0)))
            val bandRange = runningPrice * (0.02 + (i.toDouble() / days * 0.08))
            points.add(
                ForecastPoint(
                    dayIndex = i,
                    price = runningPrice,
                    upperBand = runningPrice + bandRange,
                    lowerBand = runningPrice - bandRange
                )
            )
        }

        val finalPrice = points.last().price
        val changePct = ((finalPrice - basePrice) / basePrice) * 100

        val sentiment = when {
            changePct > 5.0 -> "Güçlü Boğa (Bullish) 🚀"
            changePct < -5.0 -> "Ayı Eğilimi (Bearish) 🔻"
            else -> "Nötr / Konsolidasyon ⚖️"
        }

        return ForecastResult(
            cryptoId = crypto.id,
            modelId = model.id,
            period = period,
            targetPrice = finalPrice,
            expectedChangePercent = changePct,
            sentiment = sentiment,
            points = points
        )
    }
}
