package com.example.domain.model

data class Candle(
    val time: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
) {
    val isBullish: Boolean get() = close >= open
    val bodyTop: Double get() = maxOf(open, close)
    val bodyBottom: Double get() = minOf(open, close)
    val bodySize: Double get() = kotlin.math.abs(close - open)
    val range: Double get() = high - low
}

enum class Timeframe(val key: String, val label: String, val minutes: Int) {
    FIVE_MIN("5m", "5M", 5),
    FIFTEEN_MIN("15m", "15M", 15),
    ONE_HOUR("1h", "1H", 60),
    FOUR_HOUR("4h", "4H", 240),
    DAILY("1d", "1D", 1440);

    companion object {
        fun fromKey(key: String): Timeframe = entries.find { it.key == key } ?: FIFTEEN_MIN
    }
}

enum class SignalType {
    LONG, SHORT, WAIT
}

enum class TrendDirection {
    BULLISH, BEARISH, NEUTRAL
}

data class FuturesStats(
    val symbol: String,
    val lastPrice: Double,
    val markPrice: Double,
    val indexPrice: Double,
    val priceChange24h: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24h: Double,
    val fundingRate: Double,
    val nextFundingTime: Long,
    val openInterest: Double,
    val openInterestChange24h: Double = 0.0,
    val longShortRatio: Double? = null,
    val source: String = "Binance Futures API",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isFallback: Boolean = false
)

data class TechnicalIndicators(
    val ema9: Double?,
    val ema20: Double?,
    val ema50: Double?,
    val ema200: Double?,
    val sma20: Double?,
    val sma50: Double?,
    val sma200: Double?,
    val rsi14: Double?,
    val macdLine: Double?,
    val macdSignal: Double?,
    val macdHist: Double?,
    val bbUpper: Double?,
    val bbMiddle: Double?,
    val bbLower: Double?,
    val bbBandwidth: Double?,
    val bbPercentB: Double?,
    val atr14: Double?,
    val vwap: Double?,
    val stochK: Double?,
    val stochD: Double?,
    val volumeMa20: Double?,
    val currentVolume: Double,
    val volumeRatio: Double? // currentVolume / volumeMa20
)

data class SwingPoint(
    val index: Int,
    val time: Long,
    val price: Double,
    val isHigh: Boolean
)

data class FibonacciAnalysis(
    val swingHigh: Double,
    val swingLow: Double,
    val isUptrendSwing: Boolean,
    val retracement236: Double,
    val retracement382: Double,
    val retracement500: Double,
    val retracement618: Double,
    val retracement786: Double,
    val extension1272: Double,
    val extension1618: Double,
    val extension2000: Double,
    val extension2618: Double,
    val goldenPocketMin: Double,
    val goldenPocketMax: Double,
    val isPriceInGoldenPocket: Boolean
)

data class BreakOfStructure(
    val candleIndex: Int,
    val time: Long,
    val brokenLevel: Double,
    val isBullish: Boolean,
    val confirmed: Boolean
)

data class ChangeOfCharacter(
    val candleIndex: Int,
    val time: Long,
    val brokenLevel: Double,
    val isBullish: Boolean,
    val confirmed: Boolean
)

data class OrderBlock(
    val candleIndex: Int,
    val time: Long,
    val topPrice: Double,
    val bottomPrice: Double,
    val isBullish: Boolean, // Bullish = Demand (last red candle before rally), Bearish = Supply
    val mitigated: Boolean,
    val confirmed: Boolean
)

data class FairValueGap(
    val candleIndex: Int,
    val time: Long,
    val topPrice: Double,
    val bottomPrice: Double,
    val isBullish: Boolean,
    val mitigated: Boolean
)

data class LiquiditySweep(
    val candleIndex: Int,
    val time: Long,
    val sweptLevel: Double,
    val isHighSweep: Boolean, // high sweep = sweep of highs (bearish reversal), low sweep = sweep of lows (bullish reversal)
    val confirmed: Boolean
)

data class SmartMoneyConcepts(
    val swingHighs: List<SwingPoint>,
    val swingLows: List<SwingPoint>,
    val bosList: List<BreakOfStructure>,
    val chochList: List<ChangeOfCharacter>,
    val orderBlocks: List<OrderBlock>,
    val fvgList: List<FairValueGap>,
    val liquiditySweeps: List<LiquiditySweep>,
    val equalHighs: List<Double>,
    val equalLows: List<Double>,
    val activeDemandZone: OrderBlock?,
    val activeSupplyZone: OrderBlock?
)

data class MultiTimeframeAnalysis(
    val dailyTrend: TrendDirection,
    val fourHourTrend: TrendDirection,
    val oneHourTrend: TrendDirection,
    val fifteenMinTrend: TrendDirection,
    val fiveMinTrend: TrendDirection,
    val isAligned: Boolean,
    val hasConflict: Boolean = false,
    val conflictReason: String?
)

data class ConfluenceScore(
    val totalScore: Int, // 0..100
    val trendScore: Int, // 0..25
    val indicatorScore: Int, // 0..25
    val smcScore: Int, // 0..25
    val liquidityScore: Int, // 0..15
    val fibonacciScore: Int, // 0..10
    val supportingFactors: List<String>,
    val conflictingFactors: List<String>,
    val missingDataFactors: List<String>,
    val status: SignalType
)

data class PositionCalc(
    val accountBalance: Double,
    val riskPercent: Double,
    val leverage: Int,
    val riskAmount: Double,
    val positionSizeUsd: Double,
    val coinQuantity: Double,
    val estimatedFee: Double,
    val estimatedLiquidationPrice: Double,
    val stopLossDistancePercent: Double
)

data class TradingPlan(
    val symbol: String,
    val timeframe: Timeframe,
    val signal: SignalType,
    val currentPrice: Double,
    val entryMin: Double,
    val entryMax: Double,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val riskRewardRatio: Double,
    val invalidationLevel: Double,
    val timestamp: Long,
    val summary: String,
    val positionCalc: PositionCalc? = null
)
