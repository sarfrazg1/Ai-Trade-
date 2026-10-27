package com.example.domain.calculator

import com.example.domain.model.Candle
import com.example.domain.model.ConfluenceScore
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.FuturesStats
import com.example.domain.model.MultiTimeframeAnalysis
import com.example.domain.model.PositionCalc
import com.example.domain.model.SignalType
import com.example.domain.model.SmartMoneyConcepts
import com.example.domain.model.TechnicalIndicators
import com.example.domain.model.Timeframe
import com.example.domain.model.TradingPlan
import com.example.domain.model.TrendDirection
import kotlin.math.abs

object ConfluenceEngine {

    fun determineTrend(candles: List<Candle>): TrendDirection {
        if (candles.size < 20) return TrendDirection.NEUTRAL
        val closes = candles.map { it.close }
        val currentPrice = closes.last()
        val ema20 = TechnicalIndicatorsCalculator.calculateEmaSeries(closes, 20).lastOrNull()
        val ema50 = TechnicalIndicatorsCalculator.calculateEmaSeries(closes, 50).lastOrNull()

        return when {
            ema50 != null && currentPrice > ema50 && (ema20 == null || ema20 > ema50) -> TrendDirection.BULLISH
            ema50 != null && currentPrice < ema50 && (ema20 == null || ema20 < ema50) -> TrendDirection.BEARISH
            ema20 != null && currentPrice > ema20 -> TrendDirection.BULLISH
            ema20 != null && currentPrice < ema20 -> TrendDirection.BEARISH
            else -> TrendDirection.NEUTRAL
        }
    }

    fun analyzeMultiTimeframe(
        dailyCandles: List<Candle>,
        fourHourCandles: List<Candle>,
        oneHourCandles: List<Candle>,
        fifteenMinCandles: List<Candle>,
        fiveMinCandles: List<Candle>
    ): MultiTimeframeAnalysis {
        val dailyTrend = determineTrend(dailyCandles)
        val fourHourTrend = determineTrend(fourHourCandles)
        val oneHourTrend = determineTrend(oneHourCandles)
        val fifteenMinTrend = determineTrend(fifteenMinCandles)
        val fiveMinTrend = determineTrend(fiveMinCandles)

        // Check alignment: Macro (1d/4h) vs Execution (15m/1h)
        val macroTrend = if (fourHourTrend != TrendDirection.NEUTRAL) fourHourTrend else dailyTrend
        val execTrend = if (fifteenMinTrend != TrendDirection.NEUTRAL) fifteenMinTrend else oneHourTrend

        val hasConflict = macroTrend != TrendDirection.NEUTRAL &&
                execTrend != TrendDirection.NEUTRAL &&
                macroTrend != execTrend

        val conflictReason = if (hasConflict) {
            "Higher-Timeframe ($macroTrend) conflicts with lower-timeframe ($execTrend). Do not force trade counter-trend."
        } else null

        val isAligned = !hasConflict && macroTrend != TrendDirection.NEUTRAL && macroTrend == execTrend

        return MultiTimeframeAnalysis(
            dailyTrend = dailyTrend,
            fourHourTrend = fourHourTrend,
            oneHourTrend = oneHourTrend,
            fifteenMinTrend = fifteenMinTrend,
            fiveMinTrend = fiveMinTrend,
            isAligned = isAligned,
            hasConflict = hasConflict,
            conflictReason = conflictReason
        )
    }

    fun calculateConfluence(
        indicators: TechnicalIndicators,
        smc: SmartMoneyConcepts,
        fibo: FibonacciAnalysis?,
        mtf: MultiTimeframeAnalysis,
        futuresStats: FuturesStats?,
        currentPrice: Double
    ): ConfluenceScore {
        val supporting = mutableListOf<String>()
        val conflicting = mutableListOf<String>()
        val missing = mutableListOf<String>()

        var trendPoints = 0
        var indicatorPoints = 0
        var smcPoints = 0
        var liquidityPoints = 0
        var fiboPoints = 0

        var bullVotes = 0
        var bearVotes = 0

        // 1. Multi-Timeframe & Trend (Max 25 pts)
        if (mtf.hasConflict) {
            conflicting.add(mtf.conflictReason ?: "Higher and Lower timeframes are in conflict")
            trendPoints = 5
        } else if (mtf.isAligned) {
            trendPoints = 25
            if (mtf.fourHourTrend == TrendDirection.BULLISH) {
                bullVotes += 3
                supporting.add("Multi-timeframe trend alignment: BULLISH across 4H & 15M")
            } else if (mtf.fourHourTrend == TrendDirection.BEARISH) {
                bearVotes += 3
                supporting.add("Multi-timeframe trend alignment: BEARISH across 4H & 15M")
            }
        } else {
            trendPoints = 12
            supporting.add("Partial timeframe alignment (${mtf.fourHourTrend} 4H, ${mtf.fifteenMinTrend} 15M)")
        }

        // 2. Technical Indicators (Max 25 pts)
        if (indicators.rsi14 != null) {
            val rsi = indicators.rsi14
            when {
                rsi in 40.0..62.0 -> {
                    indicatorPoints += 8
                    supporting.add("RSI (${rsi.toInt()}) has healthy momentum without being overbought/oversold")
                }
                rsi > 70.0 -> {
                    conflicting.add("RSI (${rsi.toInt()}) in Overbought territory (>70); long risk elevated")
                    bearVotes += 1
                }
                rsi < 30.0 -> {
                    conflicting.add("RSI (${rsi.toInt()}) in Oversold territory (<30); short risk elevated")
                    bullVotes += 1
                }
                else -> {
                    indicatorPoints += 5
                }
            }
        } else {
            missing.add("Insufficient candle data for RSI 14")
        }

        // EMA structure
        if (indicators.ema20 != null && indicators.ema50 != null) {
            if (currentPrice > indicators.ema20 && indicators.ema20 > indicators.ema50) {
                indicatorPoints += 9
                bullVotes += 2
                supporting.add("Bullish EMA 20/50 alignment: Price trading above key moving averages")
            } else if (currentPrice < indicators.ema20 && indicators.ema20 < indicators.ema50) {
                indicatorPoints += 9
                bearVotes += 2
                supporting.add("Bearish EMA 20/50 alignment: Price trading below key moving averages")
            } else {
                indicatorPoints += 4
                conflicting.add("EMAs tangled/neutral; price compressing")
            }
        }

        // MACD
        if (indicators.macdHist != null) {
            if (indicators.macdHist > 0) {
                indicatorPoints += 8
                bullVotes += 1
                supporting.add("MACD histogram positive with bullish momentum")
            } else if (indicators.macdHist < 0) {
                indicatorPoints += 8
                bearVotes += 1
                supporting.add("MACD histogram negative with bearish momentum")
            }
        }

        // 3. Smart Money Concepts (Max 25 pts)
        val lastBos = smc.bosList.lastOrNull()
        if (lastBos != null) {
            if (lastBos.isBullish) {
                smcPoints += 10
                bullVotes += 2
                supporting.add("Bullish Break of Structure (BOS) confirmed at $${formatPrice(lastBos.brokenLevel)}")
            } else {
                smcPoints += 10
                bearVotes += 2
                supporting.add("Bearish Break of Structure (BOS) confirmed at $${formatPrice(lastBos.brokenLevel)}")
            }
        }

        val lastChoch = smc.chochList.lastOrNull()
        if (lastChoch != null) {
            if (lastChoch.isBullish) {
                smcPoints += 5
                bullVotes += 1
                supporting.add("Bullish Change of Character (CHoCH) detected")
            } else {
                smcPoints += 5
                bearVotes += 1
                supporting.add("Bearish Change of Character (CHoCH) detected")
            }
        }

        val activeDemand = smc.activeDemandZone
        val activeSupply = smc.activeSupplyZone
        if (activeDemand != null && currentPrice <= activeDemand.topPrice * 1.01) {
            smcPoints += 8
            bullVotes += 2
            supporting.add("Price inside unmitigated Bullish Order Block Demand Zone ($${formatPrice(activeDemand.bottomPrice)} - $${formatPrice(activeDemand.topPrice)})")
        }
        if (activeSupply != null && currentPrice >= activeSupply.bottomPrice * 0.99) {
            smcPoints += 8
            bearVotes += 2
            supporting.add("Price inside unmitigated Bearish Order Block Supply Zone ($${formatPrice(activeSupply.bottomPrice)} - $${formatPrice(activeSupply.topPrice)})")
        }

        val lastSweep = smc.liquiditySweeps.lastOrNull()
        if (lastSweep != null) {
            smcPoints += 5
            if (!lastSweep.isHighSweep) {
                bullVotes += 1
                supporting.add("Recent liquidity sweep of lows confirmed (stop-hunt reversal)")
            } else {
                bearVotes += 1
                supporting.add("Recent liquidity sweep of highs confirmed (stop-hunt reversal)")
            }
        }

        // 4. Liquidity & Derivatives (Max 15 pts)
        if (indicators.volumeRatio != null && indicators.volumeRatio > 1.2) {
            liquidityPoints += 7
            supporting.add("Volume expansion: Current volume is ${(indicators.volumeRatio * 100).toInt()}% of 20-MA")
        }

        if (futuresStats != null) {
            val funding = futuresStats.fundingRate
            // Favorable funding: negative funding rate for longs, or low positive
            if (funding < 0.0) {
                liquidityPoints += 8
                bullVotes += 1
                supporting.add("Negative funding rate (${String.format(java.util.Locale.US, "%.4f", funding * 100)}%): Shorts paying longs, squeeze risk favoring longs")
            } else if (funding > 0.03 / 100.0) {
                conflicting.add("Elevated funding rate (${String.format(java.util.Locale.US, "%.4f", funding * 100)}%): Crowded long positioning")
                bearVotes += 1
            } else {
                liquidityPoints += 5
                supporting.add("Neutral funding rate (${String.format(java.util.Locale.US, "%.4f", funding * 100)}%)")
            }

            if (futuresStats.longShortRatio != null) {
                supporting.add("Long/Short Account Ratio: ${String.format(java.util.Locale.US, "%.2f", futuresStats.longShortRatio)}")
            }
        } else {
            missing.add("Derivatives metrics temporarily offline or spot fallback")
        }

        // 5. Fibonacci Confluence (Max 10 pts)
        if (fibo != null) {
            if (fibo.isPriceInGoldenPocket) {
                fiboPoints += 10
                supporting.add("Price in Fibonacci Golden Pocket (61.8% - 65% confluence zone)")
                if (fibo.isUptrendSwing) bullVotes += 2 else bearVotes += 2
            } else {
                fiboPoints += 4
                supporting.add("Key Fibo levels active: 50% ($${formatPrice(fibo.retracement500)}), 61.8% ($${formatPrice(fibo.retracement618)})")
            }
        } else {
            missing.add("Waiting for confirmed swing high/low to project Fibonacci")
        }

        val rawTotal = (trendPoints + indicatorPoints + smcPoints + liquidityPoints + fiboPoints)
            .coerceIn(0, 100)

        // Strict rule: If MTF conflict exists, reduce total and force WAIT
        val finalScore = if (mtf.hasConflict) minOf(rawTotal, 54) else rawTotal

        val status: SignalType = when {
            mtf.hasConflict -> SignalType.WAIT
            finalScore >= 65 && bullVotes > bearVotes + 1 -> SignalType.LONG
            finalScore >= 65 && bearVotes > bullVotes + 1 -> SignalType.SHORT
            else -> SignalType.WAIT
        }

        return ConfluenceScore(
            totalScore = finalScore,
            trendScore = trendPoints.coerceIn(0, 25),
            indicatorScore = indicatorPoints.coerceIn(0, 25),
            smcScore = smcPoints.coerceIn(0, 25),
            liquidityScore = liquidityPoints.coerceIn(0, 15),
            fibonacciScore = fiboPoints.coerceIn(0, 10),
            supportingFactors = supporting,
            conflictingFactors = conflicting,
            missingDataFactors = missing,
            status = status
        )
    }

    fun buildTradingPlan(
        symbol: String,
        timeframe: Timeframe,
        confluence: ConfluenceScore,
        currentPrice: Double,
        smc: SmartMoneyConcepts,
        fibo: FibonacciAnalysis?,
        indicators: TechnicalIndicators,
        accountBalance: Double = 1000.0,
        riskPercent: Double = 2.0,
        leverage: Int = 5
    ): TradingPlan {
        val signal = confluence.status
        val atr = indicators.atr14 ?: (currentPrice * 0.01)

        val entryMin: Double
        val entryMax: Double
        val stopLoss: Double
        val tp1: Double
        val tp2: Double
        val tp3: Double
        val invalidation: Double

        when (signal) {
            SignalType.LONG -> {
                val demand = smc.activeDemandZone
                val fiboPocket = fibo?.goldenPocketMin

                entryMin = if (demand != null) demand.bottomPrice else currentPrice - atr * 0.4
                entryMax = if (demand != null) demand.topPrice else currentPrice + atr * 0.2
                stopLoss = if (demand != null) demand.bottomPrice - atr * 0.5 else currentPrice - atr * 1.5
                invalidation = stopLoss

                val riskDistance = abs(entryMax - stopLoss)
                tp1 = entryMax + riskDistance * 1.5
                tp2 = if (fibo != null) fibo.extension1272 else entryMax + riskDistance * 2.5
                tp3 = if (fibo != null) fibo.extension1618 else entryMax + riskDistance * 4.0
            }
            SignalType.SHORT -> {
                val supply = smc.activeSupplyZone
                entryMin = if (supply != null) supply.bottomPrice else currentPrice - atr * 0.2
                entryMax = if (supply != null) supply.topPrice else currentPrice + atr * 0.4
                stopLoss = if (supply != null) supply.topPrice + atr * 0.5 else currentPrice + atr * 1.5
                invalidation = stopLoss

                val riskDistance = abs(stopLoss - entryMin)
                tp1 = entryMin - riskDistance * 1.5
                tp2 = if (fibo != null) fibo.extension1272 else entryMin - riskDistance * 2.5
                tp3 = if (fibo != null) fibo.extension1618 else entryMin - riskDistance * 4.0
            }
            SignalType.WAIT -> {
                entryMin = currentPrice * 0.99
                entryMax = currentPrice * 1.01
                stopLoss = currentPrice * 0.97
                tp1 = currentPrice * 1.03
                tp2 = currentPrice * 1.05
                tp3 = currentPrice * 1.08
                invalidation = stopLoss
            }
        }

        val riskDist = abs(entryMax - stopLoss)
        val rewardDist = abs(tp1 - entryMax)
        val rrRatio = if (riskDist > 0) rewardDist / riskDist else 1.5

        val posCalc = calculatePosition(
            accountBalance = accountBalance,
            riskPercent = riskPercent,
            leverage = leverage,
            currentPrice = currentPrice,
            stopLoss = stopLoss,
            isLong = signal == SignalType.LONG
        )

        val summary = when (signal) {
            SignalType.LONG -> "BULLISH SETUP: Confluence score ${confluence.totalScore}/100. Target 1:1.5 minimum R:R with invalidation below $${formatPrice(invalidation)}."
            SignalType.SHORT -> "BEARISH SETUP: Confluence score ${confluence.totalScore}/100. Target 1:1.5 minimum R:R with invalidation above $${formatPrice(invalidation)}."
            SignalType.WAIT -> "WAIT (NO TRADE): Confluence ${confluence.totalScore}/100 is below the 65 execution threshold or timeframe signals conflict. Protect capital."
        }

        return TradingPlan(
            symbol = symbol,
            timeframe = timeframe,
            signal = signal,
            currentPrice = currentPrice,
            entryMin = entryMin,
            entryMax = entryMax,
            stopLoss = stopLoss,
            tp1 = tp1,
            tp2 = tp2,
            tp3 = tp3,
            riskRewardRatio = (rrRatio * 100).toInt() / 100.0,
            invalidationLevel = invalidation,
            timestamp = System.currentTimeMillis(),
            summary = summary,
            positionCalc = posCalc
        )
    }

    fun calculatePosition(
        accountBalance: Double,
        riskPercent: Double,
        leverage: Int,
        currentPrice: Double,
        stopLoss: Double,
        isLong: Boolean
    ): PositionCalc {
        val riskAmount = accountBalance * (riskPercent / 100.0)
        val slDist = abs(currentPrice - stopLoss)
        val slDistPct = if (currentPrice > 0) (slDist / currentPrice) else 0.02

        // Position size in USD such that if SL is hit, loss = riskAmount
        val positionSizeUsd = if (slDistPct > 0) riskAmount / slDistPct else 0.0
        val coinQuantity = if (currentPrice > 0) positionSizeUsd / currentPrice else 0.0

        // Estimated fee for VIP0/taker (approx 0.05% on entry + exit = 0.1% of position size)
        val fee = positionSizeUsd * 0.001

        // Estimated liquidation price: entry * (1 - 1/leverage + maintenance_margin)
        val liqEst = if (isLong) {
            currentPrice * (1.0 - (1.0 / leverage) * 0.9)
        } else {
            currentPrice * (1.0 + (1.0 / leverage) * 0.9)
        }

        return PositionCalc(
            accountBalance = accountBalance,
            riskPercent = riskPercent,
            leverage = leverage,
            riskAmount = riskAmount,
            positionSizeUsd = positionSizeUsd,
            coinQuantity = coinQuantity,
            estimatedFee = fee,
            estimatedLiquidationPrice = liqEst,
            stopLossDistancePercent = slDistPct * 100.0
        )
    }

    private fun formatPrice(p: Double): String {
        return when {
            p >= 1000.0 -> String.format(java.util.Locale.US, "%,.2f", p)
            p >= 1.0 -> String.format(java.util.Locale.US, "%.4f", p)
            else -> String.format(java.util.Locale.US, "%.6f", p)
        }
    }
}
