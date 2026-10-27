package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TechnicalIndicators
import com.example.domain.model.Timeframe
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun IndicatorsScreen(
    indicators: TechnicalIndicators,
    timeframe: Timeframe,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Text(
            text = "Technical Indicators Dashboard (${timeframe.label})",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Real-time mathematical calculations computed directly from verified candle data.",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. RSI 14 Card
        RsiCard(rsi = indicators.rsi14)

        Spacer(modifier = Modifier.height(12.dp))

        // 2. MACD (12, 26, 9) Card
        MacdCard(
            macdLine = indicators.macdLine,
            signalLine = indicators.macdSignal,
            histogram = indicators.macdHist
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Exponential Moving Averages (EMA 9, 20, 50, 200)
        MovingAveragesCard(
            title = "Exponential Moving Averages (EMA)",
            currentPrice = currentPrice,
            ma9 = indicators.ema9,
            ma20 = indicators.ema20,
            ma50 = indicators.ema50,
            ma200 = indicators.ema200
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Simple Moving Averages (SMA 20, 50, 200)
        MovingAveragesCard(
            title = "Simple Moving Averages (SMA)",
            currentPrice = currentPrice,
            ma9 = null,
            ma20 = indicators.sma20,
            ma50 = indicators.sma50,
            ma200 = indicators.sma200
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Bollinger Bands (20, 2)
        BollingerBandsCard(
            upper = indicators.bbUpper,
            middle = indicators.bbMiddle,
            lower = indicators.bbLower,
            bandwidth = indicators.bbBandwidth,
            percentB = indicators.bbPercentB,
            currentPrice = currentPrice
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Stochastic RSI (14, 14, 3, 3)
        StochRsiCard(k = indicators.stochK, d = indicators.stochD)

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Volatility & Volume: ATR, VWAP, Volume MA
        VolumeAndAtrCard(
            atr = indicators.atr14,
            vwap = indicators.vwap,
            volume = indicators.currentVolume,
            volumeMa20 = indicators.volumeMa20,
            volumeRatio = indicators.volumeRatio,
            currentPrice = currentPrice
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RsiCard(rsi: Double?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "RSI 14 (Relative Strength Index)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (rsi != null) {
                    val (statusColor, statusText) = when {
                        rsi >= 70.0 -> Pair(BearRed, "OVERBOUGHT (>70)")
                        rsi <= 30.0 -> Pair(BullGreen, "OVERSOLD (<30)")
                        rsi in 45.0..55.0 -> Pair(TextSecondary, "NEUTRAL")
                        rsi > 55.0 -> Pair(BullGreen, "BULLISH MOMENTUM")
                        else -> Pair(BearRed, "BEARISH MOMENTUM")
                    }
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (rsi != null) {
                Text(
                    text = String.format("%.2f", rsi),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (rsi / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        rsi >= 70.0 -> BearRed
                        rsi <= 30.0 -> BullGreen
                        else -> AccentCyan
                    },
                    trackColor = CryptoSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0 (Oversold)", fontSize = 9.sp, color = TextMuted)
                    Text("30", fontSize = 9.sp, color = BullGreen)
                    Text("50", fontSize = 9.sp, color = TextMuted)
                    Text("70", fontSize = 9.sp, color = BearRed)
                    Text("100 (Overbought)", fontSize = 9.sp, color = TextMuted)
                }
            } else {
                Text("Need 14+ candle history to calculate RSI", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun MacdCard(macdLine: Double?, signalLine: Double?, histogram: Double?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "MACD (12, 26, 9)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (histogram != null) {
                    val isBull = histogram >= 0
                    Text(
                        text = if (isBull) "BULLISH HISTOGRAM" else "BEARISH HISTOGRAM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBull) BullGreen else BearRed,
                        modifier = Modifier
                            .background(
                                if (isBull) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (macdLine != null && signalLine != null && histogram != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricCol(label = "MACD Line", value = formatShortPrice(macdLine), color = AccentCyan)
                    MetricCol(label = "Signal Line", value = formatShortPrice(signalLine), color = WarningAmber)
                    MetricCol(
                        label = "Histogram",
                        value = formatShortPrice(histogram),
                        color = if (histogram >= 0) BullGreen else BearRed
                    )
                }
            } else {
                Text("Need 35+ candles to compute MACD", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun MovingAveragesCard(
    title: String,
    currentPrice: Double,
    ma9: Double?,
    ma20: Double?,
    ma50: Double?,
    ma200: Double?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            if (ma9 != null) {
                MaRow(label = "9 Period", value = ma9, currentPrice = currentPrice)
            }
            if (ma20 != null) {
                MaRow(label = "20 Period", value = ma20, currentPrice = currentPrice)
            } else {
                Text("20 MA: Need 20 candles", fontSize = 11.sp, color = TextMuted)
            }
            if (ma50 != null) {
                MaRow(label = "50 Period", value = ma50, currentPrice = currentPrice)
            } else {
                Text("50 MA: Need 50 candles", fontSize = 11.sp, color = TextMuted)
            }
            if (ma200 != null) {
                MaRow(label = "200 Period (Trend Anchor)", value = ma200, currentPrice = currentPrice)
            } else {
                Text("200 MA: Insufficient history (need 200 candles)", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun MaRow(label: String, value: Double, currentPrice: Double) {
    val isAbove = currentPrice >= value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = formatPrice(value), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isAbove) "BULL" else "BEAR",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAbove) BullGreen else BearRed
            )
        }
    }
}

@Composable
private fun BollingerBandsCard(
    upper: Double?,
    middle: Double?,
    lower: Double?,
    bandwidth: Double?,
    percentB: Double?,
    currentPrice: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Bollinger Bands (20, 2)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            if (upper != null && middle != null && lower != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricCol(label = "Upper Band", value = formatPrice(upper), color = BearRed)
                    MetricCol(label = "Middle (20 SMA)", value = formatPrice(middle), color = AccentPurple)
                    MetricCol(label = "Lower Band", value = formatPrice(lower), color = BullGreen)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bandwidth: ${String.format("%.2f", (bandwidth ?: 0.0) * 100)}%",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "%B Location: ${String.format("%.2f", (percentB ?: 0.5) * 100)}%",
                        fontSize = 11.sp,
                        color = when {
                            (percentB ?: 0.5) > 1.0 -> BearRed
                            (percentB ?: 0.5) < 0.0 -> BullGreen
                            else -> TextSecondary
                        }
                    )
                }
            } else {
                Text("Need 20+ candles for Bollinger Bands", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun StochRsiCard(k: Double?, d: Double?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Stochastic RSI (14, 14, 3, 3)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (k != null && d != null) {
                    val isBullCross = k > d
                    Text(
                        text = if (isBullCross) "%K > %D (BULLISH)" else "%K < %D (BEARISH)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBullCross) BullGreen else BearRed
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (k != null && d != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricCol(label = "%K Line", value = String.format("%.2f", k), color = AccentCyan)
                    MetricCol(label = "%D Signal", value = String.format("%.2f", d), color = WarningAmber)
                }
            } else {
                Text("Need 30+ candles for Stoch RSI", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun VolumeAndAtrCard(
    atr: Double?,
    vwap: Double?,
    volume: Double,
    volumeMa20: Double?,
    volumeRatio: Double?,
    currentPrice: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Volume, VWAP & Volatility (ATR)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            if (atr != null) {
                val atrPct = if (currentPrice > 0) (atr / currentPrice) * 100 else 0.0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ATR 14 (Avg Range)", fontSize = 12.sp, color = TextSecondary)
                    Text("$${formatPrice(atr)} (${String.format("%.2f", atrPct)}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            if (vwap != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val isAboveVwap = currentPrice >= vwap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Session VWAP", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        "$${formatPrice(vwap)} (${if (isAboveVwap) "Price Above" else "Price Below"})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAboveVwap) BullGreen else BearRed
                    )
                }
            }

            if (volumeRatio != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val isSpike = volumeRatio > 1.2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Volume vs 20-MA", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        "${String.format("%.1f", volumeRatio)}x ${if (isSpike) "⚡ VOLUME SPIKE" else "Normal"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSpike) BullGreen else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCol(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

private fun formatPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("%,.2f", p)
        p >= 1.0 -> String.format("%.4f", p)
        else -> String.format("%.6f", p)
    }
}

private fun formatShortPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("%,.1f", p)
        p >= 1.0 -> String.format("%.2f", p)
        else -> String.format("%.4f", p)
    }
}
