package com.example

import com.example.domain.calculator.ConfluenceEngine
import com.example.domain.calculator.FibonacciCalculator
import com.example.domain.calculator.SmcDetector
import com.example.domain.calculator.TechnicalIndicatorsCalculator
import com.example.domain.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class TechnicalCalculationsTest {

    @Test
    fun testEmaCalculation() {
        val prices = listOf(10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
        val period = 5
        val emaSeries = TechnicalIndicatorsCalculator.calculateEmaSeries(prices, period)

        // Initial EMA is SMA of first 5 items: (10+11+12+13+14)/5 = 12.0
        assertEquals(12.0, emaSeries.first(), 0.001)

        // Multiplier k = 2 / (5 + 1) = 0.3333333333
        // Next EMA for 15.0: (15 - 12) * (1/3) + 12 = 13.0
        assertEquals(13.0, emaSeries[1], 0.001)
        assertTrue(emaSeries.last() > 12.0)
    }

    @Test
    fun testRsiCalculation() {
        // Monotonically increasing prices should have RSI approaching 100
        val risingPrices = (1..30).map { it.toDouble() * 2.0 }
        val rsiSeries = TechnicalIndicatorsCalculator.calculateRsiSeries(risingPrices, 14)

        assertNotNull(rsiSeries)
        assertTrue(rsiSeries.isNotEmpty())
        assertTrue("RSI should be high on rising prices", rsiSeries.last() > 70.0)

        // Monotonically falling prices should have RSI approaching 0
        val fallingPrices = (30 downTo 1).map { it.toDouble() * 2.0 }
        val fallingRsi = TechnicalIndicatorsCalculator.calculateRsiSeries(fallingPrices, 14)
        assertTrue("RSI should be low on falling prices", fallingRsi.last() < 30.0)
    }

    @Test
    fun testMacdCalculation() {
        // Generate sample sinusoidal / trend series of 50 prices
        val prices = (1..50).map { 100.0 + it * 0.5 + kotlin.math.sin(it.toDouble()) * 2.0 }
        val macd = TechnicalIndicatorsCalculator.calculateMacd(prices, fastPeriod = 12, slowPeriod = 26, signalPeriod = 9)

        assertNotNull("MACD should not be null for 50 prices", macd)
        val (macdLine, signalLine, histogram) = macd!!
        assertEquals(histogram, macdLine - signalLine, 0.0001)
    }

    @Test
    fun testFibonacciLevels() {
        val high = 100.0
        val low = 50.0
        val diff = high - low // 50.0

        val candles = listOf(
            Candle(1000L, 50.0, 52.0, 50.0, 51.0, 100.0),
            Candle(2000L, 51.0, 100.0, 51.0, 99.0, 200.0)
        )

        val fibo = FibonacciCalculator.calculateFibonacci(
            candles = candles,
            customHigh = high,
            customLow = low,
            forcedUptrend = true
        )

        assertNotNull(fibo)
        // Uptrend retracements measure pullback down from High:
        // 50% retracement: 100 - (50 * 0.5) = 75.0
        assertEquals(75.0, fibo!!.retracement500, 0.001)
        // 61.8% retracement: 100 - (50 * 0.618) = 69.1
        assertEquals(69.1, fibo.retracement618, 0.001)
        // 38.2% retracement: 100 - (50 * 0.382) = 80.9
        assertEquals(80.9, fibo.retracement382, 0.001)
        // 23.6% retracement: 100 - (50 * 0.236) = 88.2
        assertEquals(88.2, fibo.retracement236, 0.001)

        // Extensions
        // 127.2% extension: 100 + (50 * 0.272) = 113.6
        assertEquals(113.6, fibo.extension1272, 0.001)
        // 161.8% extension: 100 + (50 * 0.618) = 130.9
        assertEquals(130.9, fibo.extension1618, 0.001)
    }

    @Test
    fun testPositionSizingAndRiskReward() {
        val balance = 1000.0
        val riskPercent = 2.0 // $20.0 max risk
        val leverage = 5
        val entryPrice = 100.0
        val stopLoss = 95.0 // 5% stop loss distance
        val isLong = true

        val pos = ConfluenceEngine.calculatePosition(
            accountBalance = balance,
            riskPercent = riskPercent,
            leverage = leverage,
            currentPrice = entryPrice,
            stopLoss = stopLoss,
            isLong = isLong
        )

        assertEquals(20.0, pos.riskAmount, 0.001)
        // Position size: $20 / 0.05 = $400.0
        assertEquals(400.0, pos.positionSizeUsd, 0.01)
        // Quantity: 400 / 100 = 4.0 coins
        assertEquals(4.0, pos.coinQuantity, 0.01)
        assertEquals(5.0, pos.stopLossDistancePercent, 0.01)

        // Liquidation estimate should be below entry for long
        assertTrue(pos.estimatedLiquidationPrice < entryPrice)
    }

    @Test
    fun testSmcStructureBreak() {
        val candles = mutableListOf<Candle>()
        // Create baseline range
        for (i in 0..20) {
            candles.add(Candle(i * 1000L, 100.0, 102.0, 98.0, 100.0, 100.0))
        }
        // Swing high at candle 10: high 110.0
        candles[10] = Candle(10000L, 105.0, 110.0, 104.0, 108.0, 150.0)

        // Subsequent impulsive breakout candle at 21 with high 115.0 and close 114.0
        candles.add(Candle(21000L, 108.0, 115.0, 107.0, 114.0, 300.0))

        val smc = SmcDetector.detectSmc(candles)
        assertNotNull(smc)
    }
}
