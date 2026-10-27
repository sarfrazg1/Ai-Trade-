package com.example.domain.calculator

import com.example.domain.model.Candle
import com.example.domain.model.TechnicalIndicators
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object TechnicalIndicatorsCalculator {

    fun calculateIndicators(candles: List<Candle>): TechnicalIndicators {
        if (candles.isEmpty()) {
            return TechnicalIndicators(
                ema9 = null, ema20 = null, ema50 = null, ema200 = null,
                sma20 = null, sma50 = null, sma200 = null,
                rsi14 = null, macdLine = null, macdSignal = null, macdHist = null,
                bbUpper = null, bbMiddle = null, bbLower = null, bbBandwidth = null, bbPercentB = null,
                atr14 = null, vwap = null, stochK = null, stochD = null,
                volumeMa20 = null, currentVolume = 0.0, volumeRatio = null
            )
        }

        val closes = candles.map { it.close }
        val currentPrice = closes.last()
        val currentVolume = candles.last().volume

        val ema9Series = calculateEmaSeries(closes, 9)
        val ema20Series = calculateEmaSeries(closes, 20)
        val ema50Series = calculateEmaSeries(closes, 50)
        val ema200Series = calculateEmaSeries(closes, 200)

        val sma20 = calculateSma(closes, 20)
        val sma50 = calculateSma(closes, 50)
        val sma200 = calculateSma(closes, 200)

        val rsiSeries = calculateRsiSeries(closes, 14)
        val currentRsi = rsiSeries.lastOrNull()

        val macd = calculateMacd(closes)
        val bb = calculateBollingerBands(closes, 20, 2.0)
        val atr = calculateAtr(candles, 14)
        val vwap = calculateVwap(candles)
        val stochRsi = calculateStochRsi(rsiSeries, 14, 3, 3)
        val volumeMa20 = calculateVolumeMa(candles, 20)
        val volumeRatio = if (volumeMa20 != null && volumeMa20 > 0.0) currentVolume / volumeMa20 else null

        return TechnicalIndicators(
            ema9 = ema9Series.lastOrNull(),
            ema20 = ema20Series.lastOrNull(),
            ema50 = ema50Series.lastOrNull(),
            ema200 = ema200Series.lastOrNull(),
            sma20 = sma20,
            sma50 = sma50,
            sma200 = sma200,
            rsi14 = currentRsi,
            macdLine = macd?.first,
            macdSignal = macd?.second,
            macdHist = macd?.third,
            bbUpper = bb?.first,
            bbMiddle = bb?.second,
            bbLower = bb?.third,
            bbBandwidth = bb?.let { (u, m, l) -> if (m > 0) (u - l) / m else 0.0 },
            bbPercentB = bb?.let { (u, _, l) -> if (u - l > 0) (currentPrice - l) / (u - l) else 0.5 },
            atr14 = atr,
            vwap = vwap,
            stochK = stochRsi?.first,
            stochD = stochRsi?.second,
            volumeMa20 = volumeMa20,
            currentVolume = currentVolume,
            volumeRatio = volumeRatio
        )
    }

    fun calculateSma(data: List<Double>, period: Int): Double? {
        if (data.size < period || period <= 0) return null
        val sub = data.takeLast(period)
        return sub.sum() / period
    }

    fun calculateEmaSeries(data: List<Double>, period: Int): List<Double> {
        if (data.size < period || period <= 0) return emptyList()
        val result = mutableListOf<Double>()
        val k = 2.0 / (period + 1)

        // Seed with SMA
        var currentEma = data.take(period).sum() / period
        result.add(currentEma)

        for (i in period until data.size) {
            currentEma = (data[i] - currentEma) * k + currentEma
            result.add(currentEma)
        }
        return result
    }

    fun calculateRsiSeries(data: List<Double>, period: Int = 14): List<Double> {
        if (data.size <= period) return emptyList()
        val rsiList = mutableListOf<Double>()

        var gainSum = 0.0
        var lossSum = 0.0

        for (i in 1..period) {
            val change = data[i] - data[i - 1]
            if (change > 0) gainSum += change else lossSum += abs(change)
        }

        var avgGain = gainSum / period
        var avgLoss = lossSum / period

        val initialRs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
        rsiList.add(100.0 - (100.0 / (1.0 + initialRs)))

        for (i in (period + 1) until data.size) {
            val change = data[i] - data[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period

            val rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
            val rsi = 100.0 - (100.0 / (1.0 + rs))
            rsiList.add(rsi.coerceIn(0.0, 100.0))
        }

        return rsiList
    }

    fun calculateMacd(
        data: List<Double>,
        fastPeriod: Int = 12,
        slowPeriod: Int = 26,
        signalPeriod: Int = 9
    ): Triple<Double, Double, Double>? {
        if (data.size < slowPeriod + signalPeriod) return null

        val fastEma = calculateEmaSeries(data, fastPeriod)
        val slowEma = calculateEmaSeries(data, slowPeriod)

        // Align series lengths
        val offset = data.size - slowEma.size
        val fastAligned = fastEma.takeLast(slowEma.size)

        val macdLineSeries = slowEma.indices.map { i -> fastAligned[i] - slowEma[i] }
        val signalLineSeries = calculateEmaSeries(macdLineSeries, signalPeriod)

        if (signalLineSeries.isEmpty()) return null

        val currentMacd = macdLineSeries.last()
        val currentSignal = signalLineSeries.last()
        val currentHist = currentMacd - currentSignal

        return Triple(currentMacd, currentSignal, currentHist)
    }

    fun calculateBollingerBands(
        data: List<Double>,
        period: Int = 20,
        numStdDev: Double = 2.0
    ): Triple<Double, Double, Double>? {
        if (data.size < period) return null
        val sub = data.takeLast(period)
        val mean = sub.sum() / period

        val variance = sub.map { (it - mean) * (it - mean) }.sum() / period
        val stdDev = sqrt(variance)

        val upper = mean + numStdDev * stdDev
        val lower = mean - numStdDev * stdDev

        return Triple(upper, mean, lower)
    }

    fun calculateAtr(candles: List<Candle>, period: Int = 14): Double? {
        if (candles.size <= period) return null

        val trList = mutableListOf<Double>()
        for (i in 1 until candles.size) {
            val curr = candles[i]
            val prev = candles[i - 1]
            val tr = max(
                curr.high - curr.low,
                max(abs(curr.high - prev.close), abs(curr.low - prev.close))
            )
            trList.add(tr)
        }

        if (trList.size < period) return null

        // Wilder's smoothing
        var atr = trList.take(period).sum() / period
        for (i in period until trList.size) {
            atr = (atr * (period - 1) + trList[i]) / period
        }
        return atr
    }

    fun calculateVwap(candles: List<Candle>): Double? {
        if (candles.isEmpty()) return null
        var cumulativeVolPrice = 0.0
        var cumulativeVol = 0.0

        for (c in candles) {
            val typicalPrice = (c.high + c.low + c.close) / 3.0
            cumulativeVolPrice += typicalPrice * c.volume
            cumulativeVol += c.volume
        }

        return if (cumulativeVol > 0) cumulativeVolPrice / cumulativeVol else null
    }

    fun calculateStochRsi(
        rsiSeries: List<Double>,
        stochPeriod: Int = 14,
        kSmooth: Int = 3,
        dSmooth: Int = 3
    ): Pair<Double, Double>? {
        if (rsiSeries.size < stochPeriod + kSmooth + dSmooth) return null

        val rawStochList = mutableListOf<Double>()
        for (i in stochPeriod..rsiSeries.size) {
            val window = rsiSeries.subList(i - stochPeriod, i)
            val minRsi = window.minOrNull() ?: 0.0
            val maxRsi = window.maxOrNull() ?: 100.0
            val currRsi = window.last()
            val denom = maxRsi - minRsi
            val stoch = if (denom > 0) ((currRsi - minRsi) / denom) * 100.0 else 50.0
            rawStochList.add(stoch)
        }

        val kList = mutableListOf<Double>()
        for (i in kSmooth..rawStochList.size) {
            val sub = rawStochList.subList(i - kSmooth, i)
            kList.add(sub.sum() / kSmooth)
        }

        if (kList.size < dSmooth) return null

        val currentK = kList.last()
        val currentD = kList.takeLast(dSmooth).sum() / dSmooth

        return Pair(currentK, currentD)
    }

    fun calculateVolumeMa(candles: List<Candle>, period: Int = 20): Double? {
        if (candles.size < period) return null
        val vols = candles.takeLast(period).map { it.volume }
        return vols.sum() / period
    }
}
