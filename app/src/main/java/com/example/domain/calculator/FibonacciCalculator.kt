package com.example.domain.calculator

import com.example.domain.model.Candle
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.SwingPoint

object FibonacciCalculator {

    /**
     * Detects fractal swing highs and lows with a window radius (default 3 candles each side).
     * Strictly avoids look-ahead bias: requires confirmed candles on both sides.
     */
    fun findSwingPoints(candles: List<Candle>, radius: Int = 3): Pair<List<SwingPoint>, List<SwingPoint>> {
        if (candles.size < radius * 2 + 1) return Pair(emptyList(), emptyList())

        val swingHighs = mutableListOf<SwingPoint>()
        val swingLows = mutableListOf<SwingPoint>()

        for (i in radius until (candles.size - radius)) {
            val center = candles[i]

            // Check Swing High
            var isHigh = true
            for (j in 1..radius) {
                if (candles[i - j].high >= center.high || candles[i + j].high >= center.high) {
                    isHigh = false
                    break
                }
            }
            if (isHigh) {
                swingHighs.add(SwingPoint(i, center.time, center.high, true))
            }

            // Check Swing Low
            var isLow = true
            for (j in 1..radius) {
                if (candles[i - j].low <= center.low || candles[i + j].low <= center.low) {
                    isLow = false
                    break
                }
            }
            if (isLow) {
                swingLows.add(SwingPoint(i, center.time, center.low, false))
            }
        }

        return Pair(swingHighs, swingLows)
    }

    /**
     * Calculates Fibonacci Retracements & Extensions from recent confirmed swings.
     */
    fun calculateFibonacci(
        candles: List<Candle>,
        customHigh: Double? = null,
        customLow: Double? = null,
        forcedUptrend: Boolean? = null
    ): FibonacciAnalysis? {
        if (candles.isEmpty()) return null

        val (swingHighs, swingLows) = findSwingPoints(candles, radius = 3)
        val currentPrice = candles.last().close

        val high: Double
        val low: Double
        val isUptrend: Boolean

        if (customHigh != null && customLow != null && customHigh > customLow) {
            high = customHigh
            low = customLow
            isUptrend = forcedUptrend ?: (currentPrice >= (high + low) / 2)
        } else {
            val lastHigh = swingHighs.lastOrNull()
            val lastLow = swingLows.lastOrNull()

            if (lastHigh == null || lastLow == null || lastHigh.price <= lastLow.price) {
                // Fallback to recent 50-candle absolute high & low
                val recent = candles.takeLast(50.coerceAtMost(candles.size))
                high = recent.maxOf { it.high }
                low = recent.minOf { it.low }
                val maxIdx = recent.indexOfFirst { it.high == high }
                val minIdx = recent.indexOfFirst { it.low == low }
                isUptrend = minIdx < maxIdx
            } else {
                high = lastHigh.price
                low = lastLow.price
                isUptrend = lastLow.index < lastHigh.index
            }
        }

        val diff = high - low
        if (diff <= 0.0) return null

        // In an uptrend swing (Low to High), retracements measure pullback downwards from High:
        // Level = High - (diff * ratio)
        // In a downtrend swing (High to Low), retracements measure pullback upwards from Low:
        // Level = Low + (diff * ratio)
        val r236 = if (isUptrend) high - diff * 0.236 else low + diff * 0.236
        val r382 = if (isUptrend) high - diff * 0.382 else low + diff * 0.382
        val r500 = if (isUptrend) high - diff * 0.500 else low + diff * 0.500
        val r618 = if (isUptrend) high - diff * 0.618 else low + diff * 0.618
        val r786 = if (isUptrend) high - diff * 0.786 else low + diff * 0.786

        // Golden pocket zone: between 0.618 and 0.65
        val r650 = if (isUptrend) high - diff * 0.650 else low + diff * 0.650
        val goldenPocketMin = minOf(r618, r650)
        val goldenPocketMax = maxOf(r618, r650)
        val isPriceInGoldenPocket = currentPrice in (goldenPocketMin * 0.998)..(goldenPocketMax * 1.002)

        // Extensions
        val ext1272 = if (isUptrend) high + diff * 0.272 else low - diff * 0.272
        val ext1618 = if (isUptrend) high + diff * 0.618 else low - diff * 0.618
        val ext2000 = if (isUptrend) high + diff * 1.000 else low - diff * 1.000
        val ext2618 = if (isUptrend) high + diff * 1.618 else low - diff * 1.618

        return FibonacciAnalysis(
            swingHigh = high,
            swingLow = low,
            isUptrendSwing = isUptrend,
            retracement236 = r236,
            retracement382 = r382,
            retracement500 = r500,
            retracement618 = r618,
            retracement786 = r786,
            extension1272 = ext1272,
            extension1618 = ext1618,
            extension2000 = ext2000,
            extension2618 = ext2618,
            goldenPocketMin = goldenPocketMin,
            goldenPocketMax = goldenPocketMax,
            isPriceInGoldenPocket = isPriceInGoldenPocket
        )
    }
}
