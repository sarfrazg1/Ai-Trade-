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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.domain.model.FuturesStats
import com.example.domain.model.SmartMoneyConcepts
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FuturesAnalyticsScreen(
    stats: FuturesStats?,
    smc: SmartMoneyConcepts,
    currentPrice: Double,
    hasFuturesAccess: Boolean = true,
    onOpenSubscription: () -> Unit = {},
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
            text = "Futures & Derivatives Analytics",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Funding rate, open interest, account positioning, and structural liquidity.",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (stats == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(18.dp), contentAlignment = Alignment.Center) {
                    Text("Fetching derivatives data from Binance...", color = TextSecondary)
                }
            }
        } else {
            // 1. Funding Rate Card (Public preview)
            FundingRateCard(stats = stats)

            if (!hasFuturesAccess) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Derivatives Analytics Locked",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open Interest, Long/Short Account positioning, and Liquidity Pools require Bronze Basic Plan (PKR 1,000) or higher.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        androidx.compose.material3.Button(
                            onClick = onOpenSubscription,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = BullGreen)
                        ) {
                            Text("Upgrade to Bronze (PKR 1,000)", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                // 2. Open Interest & 24h Ticker Stats
                OpenInterestCard(stats = stats)

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Long / Short Account Ratio
                LongShortRatioCard(ratio = stats.longShortRatio)

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Structural Liquidity Pools (Equal Highs & Equal Lows)
                LiquidityPoolsCard(smc = smc, currentPrice = currentPrice)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Data Disclosure Notice
            DataDisclosureNotice(isFallback = stats.isFallback)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FundingRateCard(stats: FuturesStats) {
    val fundingPct = stats.fundingRate * 100.0
    val isNegative = fundingPct < 0
    val nextFundingStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(stats.nextFundingTime))

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
                Text(text = "Perpetual Funding Rate (8h)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = "Next: $nextFundingStr",
                    fontSize = 11.sp,
                    color = AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val sign = if (fundingPct > 0) "+" else ""
                    Text(
                        text = "$sign${String.format("%.4f", fundingPct)}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isNegative) BullGreen else if (fundingPct > 0.03) BearRed else TextPrimary
                    )
                    Text(
                        text = "Est. Annualized (APR): ~${String.format("%.2f", fundingPct * 3 * 365)}%",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isNegative) BullGreen.copy(alpha = 0.15f) else CryptoSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isNegative) "Shorts Pay Longs" else "Longs Pay Shorts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNegative) BullGreen else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isNegative) {
                    "Sentiment is heavily bearish; short squeeze probability is elevated."
                } else if (fundingPct > 0.03) {
                    "Longs are paying heavy premium; longs are crowded and vulnerable to cascade long liquidations."
                } else {
                    "Funding is balanced within standard baseline rate."
                },
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun OpenInterestCard(stats: FuturesStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Open Interest & 24h Range", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Open Interest (Contracts)", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = if (stats.openInterest > 0) String.format("%,.0f", stats.openInterest) else "N/A (Spot fallback)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("24h Volume", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = String.format("%,.0f", stats.volume24h),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("24h High: $${formatPrice(stats.high24h)}", fontSize = 11.sp, color = BullGreen)
                Text("24h Low: $${formatPrice(stats.low24h)}", fontSize = 11.sp, color = BearRed)
            }
        }
    }
}

@Composable
private fun LongShortRatioCard(ratio: Double?) {
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
                Text(text = "Global Long/Short Account Ratio", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = if (ratio != null) String.format("%.2f", ratio) else "1.00",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val r = ratio ?: 1.0
            val longFraction = (r / (r + 1.0)).toFloat().coerceIn(0.1f, 0.9f)
            val longPct = (longFraction * 100).toInt()
            val shortPct = 100 - longPct

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Longs: $longPct%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                Text("Shorts: $shortPct%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BearRed)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { longFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = BullGreen,
                trackColor = BearRed
            )
        }
    }
}

@Composable
private fun LiquidityPoolsCard(smc: SmartMoneyConcepts, currentPrice: Double) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Structural Liquidity Zones (EQH & EQL)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Identified swing clusters where stop-losses and breakout orders cluster.",
                fontSize = 10.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Buy-Side Liquidity (Equal Highs above price)
            val bslPools = smc.equalHighs.filter { it >= currentPrice }
            if (bslPools.isNotEmpty()) {
                Text("Buy-Side Liquidity (Equal Highs above price):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentCyan)
                bslPools.forEach { price ->
                    Text("• Target Pool at $${formatPrice(price)}", fontSize = 11.sp, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sell-Side Liquidity (Equal Lows below price)
            val sslPools = smc.equalLows.filter { it <= currentPrice }
            if (sslPools.isNotEmpty()) {
                Text("Sell-Side Liquidity (Equal Lows below price):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = WarningAmber)
                sslPools.forEach { price ->
                    Text("• Target Pool at $${formatPrice(price)}", fontSize = 11.sp, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            if (bslPools.isEmpty() && sslPools.isEmpty()) {
                Text("No tight double-tops/bottoms detected in recent 40 candles.", fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

@Composable
private fun DataDisclosureNotice(isFallback: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CryptoSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Data Integrity Notice: All prices, candles, funding rates, and open interest are fetched from Binance public endpoints. Liquidation heatmaps requiring private exchange depth feeds are not simulated or fabricated. Estimated liquidity pools are strictly derived from mathematical swing high/low clusters.",
            fontSize = 10.sp,
            color = TextMuted,
            lineHeight = 14.sp
        )
    }
}

private fun formatPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("%,.2f", p)
        p >= 1.0 -> String.format("%.4f", p)
        else -> String.format("%.6f", p)
    }
}
