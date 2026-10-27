package com.example.domain.calculator

import com.example.domain.model.BreakOfStructure
import com.example.domain.model.Candle
import com.example.domain.model.ChangeOfCharacter
import com.example.domain.model.FairValueGap
import com.example.domain.model.LiquiditySweep
import com.example.domain.model.OrderBlock
import com.example.domain.model.SmartMoneyConcepts
import kotlin.math.abs

object SmcDetector {

    fun detectSmc(candles: List<Candle>): SmartMoneyConcepts {
        if (candles.size < 15) {
            return SmartMoneyConcepts(
                swingHighs = emptyList(),
                swingLows = emptyList(),
                bosList = emptyList(),
                chochList = emptyList(),
                orderBlocks = emptyList(),
                fvgList = emptyList(),
                liquiditySweeps = emptyList(),
                equalHighs = emptyList(),
                equalLows = emptyList(),
                activeDemandZone = null,
                activeSupplyZone = null
            )
        }

        val (swingHighs, swingLows) = FibonacciCalculator.findSwingPoints(candles, radius = 2)

        val bosList = mutableListOf<BreakOfStructure>()
        val chochList = mutableListOf<ChangeOfCharacter>()
        val orderBlocks = mutableListOf<OrderBlock>()
        val fvgList = mutableListOf<FairValueGap>()
        val liquiditySweeps = mutableListOf<LiquiditySweep>()
        val equalHighs = mutableListOf<Double>()
        val equalLows = mutableListOf<Double>()

        // 1. Detect Equal Highs (EQH) and Equal Lows (EQL) (Liquidity Pools)
        for (i in 0 until swingHighs.size - 1) {
            val h1 = swingHighs[i]
            val h2 = swingHighs[i + 1]
            if (abs(h1.price - h2.price) / h1.price < 0.0015) { // within 0.15%
                equalHighs.add((h1.price + h2.price) / 2.0)
            }
        }
        for (i in 0 until swingLows.size - 1) {
            val l1 = swingLows[i]
            val l2 = swingLows[i + 1]
            if (abs(l1.price - l2.price) / l1.price < 0.0015) {
                equalLows.add((l1.price + l2.price) / 2.0)
            }
        }

        // 2. Detect Liquidity Sweeps
        for (sh in swingHighs) {
            for (k in (sh.index + 1) until candles.size) {
                val c = candles[k]
                if (c.high > sh.price && c.close < sh.price) {
                    liquiditySweeps.add(
                        LiquiditySweep(
                            candleIndex = k,
                            time = c.time,
                            sweptLevel = sh.price,
                            isHighSweep = true,
                            confirmed = true
                        )
                    )
                    break
                }
            }
        }
        for (sl in swingLows) {
            for (k in (sl.index + 1) until candles.size) {
                val c = candles[k]
                if (c.low < sl.price && c.close > sl.price) {
                    liquiditySweeps.add(
                        LiquiditySweep(
                            candleIndex = k,
                            time = c.time,
                            sweptLevel = sl.price,
                            isHighSweep = false,
                            confirmed = true
                        )
                    )
                    break
                }
            }
        }

        // 3. Detect BOS & CHoCH
        var isUptrend = true
        var prevHighPrice: Double? = null
        var prevLowPrice: Double? = null

        val allSwings = (swingHighs + swingLows).sortedBy { it.index }
        for (swing in allSwings) {
            if (swing.isHigh) {
                if (prevHighPrice != null) {
                    if (swing.price > prevHighPrice) {
                        if (isUptrend) {
                            bosList.add(BreakOfStructure(swing.index, swing.time, prevHighPrice, isBullish = true, confirmed = true))
                        } else {
                            chochList.add(ChangeOfCharacter(swing.index, swing.time, prevHighPrice, isBullish = true, confirmed = true))
                            isUptrend = true
                        }
                    }
                }
                prevHighPrice = swing.price
            } else {
                if (prevLowPrice != null) {
                    if (swing.price < prevLowPrice) {
                        if (!isUptrend) {
                            bosList.add(BreakOfStructure(swing.index, swing.time, prevLowPrice, isBullish = false, confirmed = true))
                        } else {
                            chochList.add(ChangeOfCharacter(swing.index, swing.time, prevLowPrice, isBullish = false, confirmed = true))
                            isUptrend = false
                        }
                    }
                }
                prevLowPrice = swing.price
            }
        }

        // 4. Fair Value Gaps (FVG)
        for (i in 2 until candles.size) {
            val c1 = candles[i - 2]
            val c2 = candles[i - 1]
            val c3 = candles[i]

            // Bullish FVG: c1.high < c3.low with strong displacement
            if (c3.low > c1.high && c2.isBullish) {
                val top = c3.low
                val bottom = c1.high

                // Check mitigation by subsequent candles
                var mitigated = false
                for (j in (i + 1) until candles.size) {
                    if (candles[j].low <= top) {
                        mitigated = true
                        break
                    }
                }

                fvgList.add(
                    FairValueGap(
                        candleIndex = i - 1,
                        time = c2.time,
                        topPrice = top,
                        bottomPrice = bottom,
                        isBullish = true,
                        mitigated = mitigated
                    )
                )
            }

            // Bearish FVG: c1.low > c3.high with strong displacement
            if (c1.low > c3.high && !c2.isBullish) {
                val top = c1.low
                val bottom = c3.high

                var mitigated = false
                for (j in (i + 1) until candles.size) {
                    if (candles[j].high >= bottom) {
                        mitigated = true
                        break
                    }
                }

                fvgList.add(
                    FairValueGap(
                        candleIndex = i - 1,
                        time = c2.time,
                        topPrice = top,
                        bottomPrice = bottom,
                        isBullish = false,
                        mitigated = mitigated
                    )
                )
            }
        }

        // 5. Order Blocks (OB)
        // Bullish OB: last down candle before an impulsive move breaking structure upward
        // Bearish OB: last up candle before an impulsive move breaking structure downward
        for (i in 2 until (candles.size - 2)) {
            val c = candles[i]
            val nextC1 = candles[i + 1]
            val nextC2 = candles[i + 2]

            // Check Bullish OB
            if (!c.isBullish && nextC1.isBullish && nextC2.isBullish) {
                val displacement = (nextC2.close - c.close)
                if (displacement > c.bodySize * 1.5 && c.range > 0) {
                    // Check if subsequent candles mitigated it
                    var mitigated = false
                    for (k in (i + 3) until candles.size) {
                        if (candles[k].low <= c.bodyTop) {
                            mitigated = true
                            break
                        }
                    }
                    orderBlocks.add(
                        OrderBlock(
                            candleIndex = i,
                            time = c.time,
                            topPrice = c.high,
                            bottomPrice = c.low,
                            isBullish = true,
                            mitigated = mitigated,
                            confirmed = true
                        )
                    )
                }
            }

            // Check Bearish OB
            if (c.isBullish && !nextC1.isBullish && !nextC2.isBullish) {
                val displacement = (c.close - nextC2.close)
                if (displacement > c.bodySize * 1.5 && c.range > 0) {
                    var mitigated = false
                    for (k in (i + 3) until candles.size) {
                        if (candles[k].high >= c.bodyBottom) {
                            mitigated = true
                            break
                        }
                    }
                    orderBlocks.add(
                        OrderBlock(
                            candleIndex = i,
                            time = c.time,
                            topPrice = c.high,
                            bottomPrice = c.low,
                            isBullish = false,
                            mitigated = mitigated,
                            confirmed = true
                        )
                    )
                }
            }
        }

        val currentPrice = candles.last().close
        val activeDemand = orderBlocks
            .filter { it.isBullish && !it.mitigated && it.topPrice <= currentPrice * 1.02 }
            .maxByOrNull { it.candleIndex }

        val activeSupply = orderBlocks
            .filter { !it.isBullish && !it.mitigated && it.bottomPrice >= currentPrice * 0.98 }
            .maxByOrNull { it.candleIndex }

        return SmartMoneyConcepts(
            swingHighs = swingHighs.takeLast(10),
            swingLows = swingLows.takeLast(10),
            bosList = bosList.takeLast(10),
            chochList = chochList.takeLast(10),
            orderBlocks = orderBlocks.takeLast(10),
            fvgList = fvgList.takeLast(10),
            liquiditySweeps = liquiditySweeps.takeLast(10),
            equalHighs = equalHighs.takeLast(5),
            equalLows = equalLows.takeLast(5),
            activeDemandZone = activeDemand,
            activeSupplyZone = activeSupply
        )
    }
}
