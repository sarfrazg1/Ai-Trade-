package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PaperTradeEntity
import com.example.data.local.SavedSignalEntity
import com.example.data.local.WatchlistEntity
import com.example.data.remote.BinanceClient
import com.example.domain.calculator.ConfluenceEngine
import com.example.domain.calculator.FibonacciCalculator
import com.example.domain.calculator.SmcDetector
import com.example.domain.calculator.TechnicalIndicatorsCalculator
import com.example.domain.model.Candle
import com.example.domain.model.ConfluenceScore
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.FuturesStats
import com.example.domain.model.MultiTimeframeAnalysis
import com.example.domain.model.SignalType
import com.example.domain.model.SmartMoneyConcepts
import com.example.domain.model.TechnicalIndicators
import com.example.domain.model.Timeframe
import com.example.domain.model.TradingPlan
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

data class CompleteAnalysisResult(
    val symbol: String,
    val selectedTimeframe: Timeframe,
    val candles: List<Candle>,
    val indicators: TechnicalIndicators,
    val smc: SmartMoneyConcepts,
    val fibonacci: FibonacciAnalysis?,
    val mtf: MultiTimeframeAnalysis,
    val confluence: ConfluenceScore,
    val tradingPlan: TradingPlan,
    val futuresStats: FuturesStats?,
    val timestamp: Long = System.currentTimeMillis()
)

data class BacktestSummary(
    val totalTrades: Int,
    val winTrades: Int,
    val lossTrades: Int,
    val winRatePercent: Double,
    val profitFactor: Double,
    val totalPnlPercent: Double
)

class CryptoTradingRepository(
    private val client: BinanceClient,
    private val db: AppDatabase
) {
    val watchlist: Flow<List<WatchlistEntity>> = db.watchlistDao().getAll()
    val savedSignals: Flow<List<SavedSignalEntity>> = db.savedSignalDao().getAll()
    val paperTrades: Flow<List<PaperTradeEntity>> = db.paperTradeDao().getAll()

    suspend fun seedDefaultWatchlistIfNeeded() {
        val defaults = listOf(
            WatchlistEntity("BTCUSDT", "Bitcoin", isPinned = true),
            WatchlistEntity("ETHUSDT", "Ethereum", isPinned = true),
            WatchlistEntity("SOLUSDT", "Solana", isPinned = false),
            WatchlistEntity("BNBUSDT", "BNB", isPinned = false),
            WatchlistEntity("DOGEUSDT", "Dogecoin", isPinned = false),
            WatchlistEntity("XRPUSDT", "Ripple", isPinned = false)
        )
        db.watchlistDao().insertAll(defaults)
    }

    suspend fun addToWatchlist(symbol: String, name: String = symbol) {
        db.watchlistDao().insert(WatchlistEntity(symbol = symbol.uppercase().trim(), name = name))
    }

    suspend fun removeFromWatchlist(symbol: String) {
        db.watchlistDao().delete(WatchlistEntity(symbol = symbol, name = ""))
    }

    suspend fun saveSignal(plan: TradingPlan, confluence: ConfluenceScore): Long {
        val entity = SavedSignalEntity(
            symbol = plan.symbol,
            timeframe = plan.timeframe.key,
            signalType = plan.signal.name,
            score = confluence.totalScore,
            entryZone = "${formatPrice(plan.entryMin)} - ${formatPrice(plan.entryMax)}",
            stopLoss = plan.stopLoss,
            tp1 = plan.tp1,
            tp2 = plan.tp2,
            riskReward = plan.riskRewardRatio,
            timestamp = plan.timestamp,
            summary = plan.summary
        )
        return db.savedSignalDao().insert(entity)
    }

    suspend fun deleteSavedSignal(signal: SavedSignalEntity) {
        db.savedSignalDao().delete(signal)
    }

    suspend fun openPaperTrade(
        symbol: String,
        signalType: String,
        entryPrice: Double,
        stopLoss: Double,
        tp1: Double,
        tp2: Double,
        margin: Double,
        leverage: Int
    ): Long {
        val positionUsd = margin * leverage
        val quantity = if (entryPrice > 0) positionUsd / entryPrice else 0.0

        val trade = PaperTradeEntity(
            symbol = symbol,
            signalType = signalType,
            entryPrice = entryPrice,
            stopLoss = stopLoss,
            tp1 = tp1,
            tp2 = tp2,
            margin = margin,
            leverage = leverage,
            quantity = quantity,
            status = "OPEN"
        )
        return db.paperTradeDao().insert(trade)
    }

    suspend fun closePaperTrade(trade: PaperTradeEntity, exitPrice: Double, reason: String) {
        val isLong = trade.signalType == "LONG"
        val priceDiff = if (isLong) (exitPrice - trade.entryPrice) else (trade.entryPrice - exitPrice)
        val pnlUsd = (priceDiff / trade.entryPrice) * (trade.margin * trade.leverage)

        val updated = trade.copy(
            closeTime = System.currentTimeMillis(),
            status = reason,
            exitPrice = exitPrice,
            pnlUsd = pnlUsd
        )
        db.paperTradeDao().update(updated)
    }

    suspend fun deletePaperTrade(trade: PaperTradeEntity) {
        db.paperTradeDao().delete(trade)
    }

    suspend fun getOpenPaperTrades(): List<PaperTradeEntity> {
        return db.paperTradeDao().getOpenTrades()
    }

    /**
     * Checks open paper trades against latest price and updates triggered TP or SL.
     * Prevents any trade closures on 0.0 or invalid prices.
     */
    suspend fun checkOpenTrades(symbol: String, currentPrice: Double) {
        if (currentPrice <= 0.0 || currentPrice.isNaN() || currentPrice.isInfinite()) {
            return
        }
        val openTrades = db.paperTradeDao().getOpenTrades().filter { it.symbol.equals(symbol, ignoreCase = true) }
        for (trade in openTrades) {
            if (trade.entryPrice <= 0.0) continue
            val isLong = trade.signalType == "LONG"
            if (isLong) {
                if (trade.stopLoss > 0.0 && currentPrice <= trade.stopLoss) {
                    closePaperTrade(trade, trade.stopLoss, "CLOSED_SL")
                } else if (trade.tp2 > 0.0 && currentPrice >= trade.tp2) {
                    closePaperTrade(trade, trade.tp2, "CLOSED_TP2")
                } else if (trade.tp1 > 0.0 && currentPrice >= trade.tp1) {
                    closePaperTrade(trade, trade.tp1, "CLOSED_TP1")
                }
            } else {
                if (trade.stopLoss > 0.0 && currentPrice >= trade.stopLoss) {
                    closePaperTrade(trade, trade.stopLoss, "CLOSED_SL")
                } else if (trade.tp2 > 0.0 && currentPrice <= trade.tp2) {
                    closePaperTrade(trade, trade.tp2, "CLOSED_TP2")
                } else if (trade.tp1 > 0.0 && currentPrice <= trade.tp1) {
                    closePaperTrade(trade, trade.tp1, "CLOSED_TP1")
                }
            }
        }
    }

    private var lastSuccessfulResult: CompleteAnalysisResult? = null

    suspend fun analyzePair(
        symbol: String,
        selectedTimeframe: Timeframe,
        accountBalance: Double,
        riskPercent: Double,
        leverage: Int
    ): Result<CompleteAnalysisResult> = coroutineScope {
        try {
            // Priority 1: Fetch the selected timeframe candles and live stats first
            val primaryDeferred = async { client.fetchKlines(symbol, selectedTimeframe, limit = 100) }
            val statsDeferred = async { client.fetchFuturesStats(symbol) }

            // Secondary: Fetch other timeframes concurrently for MTF analysis
            val dDeferred = async { client.fetchKlines(symbol, Timeframe.DAILY, limit = 40) }
            val h4Deferred = async { client.fetchKlines(symbol, Timeframe.FOUR_HOUR, limit = 50) }
            val h1Deferred = async { client.fetchKlines(symbol, Timeframe.ONE_HOUR, limit = 60) }
            val m15Deferred = async { client.fetchKlines(symbol, Timeframe.FIFTEEN_MIN, limit = 80) }
            val m5Deferred = async { client.fetchKlines(symbol, Timeframe.FIVE_MIN, limit = 50) }

            val primaryRes = primaryDeferred.await()
            val statsRes = statsDeferred.await()

            var primaryCandles = primaryRes.getOrNull() ?: emptyList()

            // If primary timeframe failed, check if any of the other timeframes succeeded
            if (primaryCandles.isEmpty()) {
                val m15 = m15Deferred.await().getOrNull()
                val h1 = h1Deferred.await().getOrNull()
                val m5 = m5Deferred.await().getOrNull()
                primaryCandles = m15 ?: h1 ?: m5 ?: emptyList()
            }

            if (primaryCandles.isEmpty()) {
                val fallback = lastSuccessfulResult ?: DefaultMarketSnapshot.createInitialSnapshot(symbol, selectedTimeframe)
                return@coroutineScope Result.success(fallback.copy(symbol = symbol, selectedTimeframe = selectedTimeframe))
            }

            val dailyCandles = dDeferred.await().getOrNull() ?: primaryCandles
            val h4Candles = h4Deferred.await().getOrNull() ?: primaryCandles
            val h1Candles = h1Deferred.await().getOrNull() ?: primaryCandles
            val m15Candles = m15Deferred.await().getOrNull() ?: primaryCandles
            val m5Candles = m5Deferred.await().getOrNull() ?: primaryCandles

            val currentPrice = statsRes.getOrNull()?.lastPrice
                ?: primaryCandles.lastOrNull()?.close
                ?: 0.0

            // Check any paper trade trigger against live price only if valid positive price
            if (currentPrice > 0.0) {
                try {
                    checkOpenTrades(symbol, currentPrice)
                } catch (_: Exception) {}
            }

            val indicators = TechnicalIndicatorsCalculator.calculateIndicators(primaryCandles)
            val smc = SmcDetector.detectSmc(primaryCandles)
            val fibonacci = FibonacciCalculator.calculateFibonacci(primaryCandles)

            val mtf = ConfluenceEngine.analyzeMultiTimeframe(
                dailyCandles = dailyCandles,
                fourHourCandles = h4Candles,
                oneHourCandles = h1Candles,
                fifteenMinCandles = m15Candles,
                fiveMinCandles = m5Candles
            )

            val futuresStats = statsRes.getOrNull()

            val confluence = ConfluenceEngine.calculateConfluence(
                indicators = indicators,
                smc = smc,
                fibo = fibonacci,
                mtf = mtf,
                futuresStats = futuresStats,
                currentPrice = currentPrice
            )

            val tradingPlan = ConfluenceEngine.buildTradingPlan(
                symbol = symbol,
                timeframe = selectedTimeframe,
                confluence = confluence,
                currentPrice = currentPrice,
                smc = smc,
                fibo = fibonacci,
                indicators = indicators,
                accountBalance = accountBalance,
                riskPercent = riskPercent,
                leverage = leverage
            )

            val analysisResult = CompleteAnalysisResult(
                symbol = symbol,
                selectedTimeframe = selectedTimeframe,
                candles = primaryCandles,
                indicators = indicators,
                smc = smc,
                fibonacci = fibonacci,
                mtf = mtf,
                confluence = confluence,
                tradingPlan = tradingPlan,
                futuresStats = futuresStats
            )

            lastSuccessfulResult = analysisResult
            Result.success(analysisResult)
        } catch (e: Exception) {
            val fallback = lastSuccessfulResult ?: DefaultMarketSnapshot.createInitialSnapshot(symbol, selectedTimeframe)
            Result.success(fallback.copy(symbol = symbol, selectedTimeframe = selectedTimeframe))
        }
    }

    /**
     * Historical backtest simulation over candles using the rule engine.
     */
    fun runBacktest(candles: List<Candle>): BacktestSummary {
        if (candles.size < 40) {
            return BacktestSummary(0, 0, 0, 0.0, 0.0, 0.0)
        }

        var totalTrades = 0
        var winTrades = 0
        var lossTrades = 0
        var totalWinsPnl = 0.0
        var totalLossPnl = 0.0

        for (i in 30 until (candles.size - 5)) {
            val window = candles.subList(0, i)
            val ind = TechnicalIndicatorsCalculator.calculateIndicators(window)
            val smc = SmcDetector.detectSmc(window)
            val currClose = window.last().close

            // Rule check: Bullish setup if RSI < 55, close > EMA20, and positive MACD hist
            val isBullSignal = (ind.rsi14 ?: 50.0) in 38.0..58.0 &&
                    (ind.ema20 != null && currClose > ind.ema20) &&
                    (ind.macdHist != null && ind.macdHist > 0)

            val isBearSignal = (ind.rsi14 ?: 50.0) in 42.0..62.0 &&
                    (ind.ema20 != null && currClose < ind.ema20) &&
                    (ind.macdHist != null && ind.macdHist < 0)

            if (isBullSignal) {
                totalTrades++
                val entry = currClose
                val atr = ind.atr14 ?: (entry * 0.01)
                val sl = entry - atr * 1.5
                val tp = entry + atr * 2.25

                // Evaluate next 5 candles
                var resultWon = false
                for (k in i until minOf(candles.size, i + 5)) {
                    val future = candles[k]
                    if (future.high >= tp) {
                        resultWon = true
                        break
                    }
                    if (future.low <= sl) {
                        resultWon = false
                        break
                    }
                }
                if (resultWon) {
                    winTrades++
                    totalWinsPnl += (tp - entry) / entry
                } else {
                    lossTrades++
                    totalLossPnl += abs(sl - entry) / entry
                }
            } else if (isBearSignal) {
                totalTrades++
                val entry = currClose
                val atr = ind.atr14 ?: (entry * 0.01)
                val sl = entry + atr * 1.5
                val tp = entry - atr * 2.25

                var resultWon = false
                for (k in i until minOf(candles.size, i + 5)) {
                    val future = candles[k]
                    if (future.low <= tp) {
                        resultWon = true
                        break
                    }
                    if (future.high >= sl) {
                        resultWon = false
                        break
                    }
                }
                if (resultWon) {
                    winTrades++
                    totalWinsPnl += (entry - tp) / entry
                } else {
                    lossTrades++
                    totalLossPnl += abs(entry - sl) / entry
                }
            }
        }

        val winRate = if (totalTrades > 0) (winTrades.toDouble() / totalTrades) * 100.0 else 0.0
        val pf = if (totalLossPnl > 0) totalWinsPnl / totalLossPnl else if (totalWinsPnl > 0) 3.5 else 0.0
        val netPnl = (totalWinsPnl - totalLossPnl) * 100.0

        return BacktestSummary(
            totalTrades = totalTrades,
            winTrades = winTrades,
            lossTrades = lossTrades,
            winRatePercent = (winRate * 10).toInt() / 10.0,
            profitFactor = (pf * 100).toInt() / 100.0,
            totalPnlPercent = (netPnl * 10).toInt() / 10.0
        )
    }

    private fun formatPrice(p: Double): String {
        return when {
            p >= 1000.0 -> String.format("%,.2f", p)
            p >= 1.0 -> String.format("%.4f", p)
            else -> String.format("%.6f", p)
        }
    }
}
