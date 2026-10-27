package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CompleteAnalysisResult
import com.example.domain.model.ConfluenceScore
import com.example.domain.model.MultiTimeframeAnalysis
import com.example.domain.model.SignalType
import com.example.domain.model.TradingPlan
import com.example.domain.model.TrendDirection
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedGlow
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenGlow
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberGlow

@Composable
fun PlanAndConfluenceScreen(
    analysis: CompleteAnalysisResult,
    accountBalance: Double,
    riskPercent: Double,
    leverage: Int,
    isRomanUrdu: Boolean = false,
    onUpdateRisk: (Double, Double, Int) -> Unit,
    onExecutePaperTrade: () -> Unit,
    onSaveSignal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plan = analysis.tradingPlan
    val confluence = analysis.confluence
    val mtf = analysis.mtf

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        // 1. Hero Signal Banner
        HeroSignalBanner(plan = plan, confluence = confluence, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Confluence Score Gauge Card
        ConfluenceScoreCard(confluence = confluence, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Multi-Timeframe Alignment Card
        MtfAlignmentCard(mtf = mtf, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Complete Trading Plan
        TradingPlanDetailsCard(plan = plan, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Evidence Cards (Supporting vs Conflicting)
        EvidenceCard(
            supporting = confluence.supportingFactors,
            conflicting = confluence.conflictingFactors,
            missing = confluence.missingDataFactors,
            isRomanUrdu = isRomanUrdu
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Interactive Risk & Position Sizing Calculator
        RiskCalculatorCard(
            plan = plan,
            accountBalance = accountBalance,
            riskPercent = riskPercent,
            leverage = leverage,
            isRomanUrdu = isRomanUrdu,
            onUpdateRisk = onUpdateRisk
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Action Buttons (Execute Paper Trade / Save Signal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onExecutePaperTrade,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("execute_paper_trade_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (plan.signal) {
                        SignalType.LONG -> BullGreen
                        SignalType.SHORT -> BearRed
                        SignalType.WAIT -> CryptoSurfaceVariant
                    },
                    contentColor = if (plan.signal == SignalType.WAIT) TextSecondary else Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (plan.signal == SignalType.WAIT) {
                        if (isRomanUrdu) "WAIT (Sabar Karein)" else "WAIT (No Trade)"
                    } else {
                        if (isRomanUrdu) "Paper Trade Lagayein" else "Paper Trade Setup"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = onSaveSignal,
                modifier = Modifier
                    .weight(0.8f)
                    .height(48.dp)
                    .testTag("save_signal_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CryptoCardBorder))
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = AccentCyan
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isRomanUrdu) "Mehfooz" else "Save",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HeroSignalBanner(plan: TradingPlan, confluence: ConfluenceScore, isRomanUrdu: Boolean = false) {
    val (signalColor, glowColor, signalTitle, signalSubtitle) = when (plan.signal) {
        SignalType.LONG -> Quadruple(
            BullGreen,
            BullGreenGlow,
            if (isRomanUrdu) "BUY / LONG (Kharidari Setup)" else "LONG SIGNAL DETECTED",
            if (isRomanUrdu) "Mazboot Bullish Confluence Score (${confluence.totalScore}/100)" else "High Confluence Bullish Setup (${confluence.totalScore}/100)"
        )
        SignalType.SHORT -> Quadruple(
            BearRed,
            BearRedGlow,
            if (isRomanUrdu) "SELL / SHORT (Farokht Setup)" else "SHORT SIGNAL DETECTED",
            if (isRomanUrdu) "Mazboot Bearish Confluence Score (${confluence.totalScore}/100)" else "High Confluence Bearish Setup (${confluence.totalScore}/100)"
        )
        SignalType.WAIT -> Quadruple(
            WarningAmber,
            WarningAmberGlow,
            if (isRomanUrdu) "WAIT - KOI TRADE NA LEIN" else "WAIT - NO TRADE",
            if (isRomanUrdu) "Ishare mutazaad hain ya score (${confluence.totalScore}/100) 65 se kam hai" else "Signals conflicting or score (${confluence.totalScore}/100) below 65"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CryptoSurface)
            .border(1.5.dp, signalColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(signalColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = signalTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = signalColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = signalSubtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Big Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(signalColor)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isRomanUrdu) {
                        when (plan.signal) {
                            SignalType.LONG -> "LONG ▲"
                            SignalType.SHORT -> "SHORT ▼"
                            SignalType.WAIT -> "WAIT ⏸"
                        }
                    } else plan.signal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ConfluenceScoreCard(confluence: ConfluenceScore, isRomanUrdu: Boolean = false) {
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
                Text(
                    text = if (isRomanUrdu) "Confluence Score Engine" else "Confluence Scoring Engine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${confluence.totalScore} / 100",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        confluence.totalScore >= 65 -> BullGreen
                        confluence.totalScore >= 50 -> WarningAmber
                        else -> BearRed
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { confluence.totalScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    confluence.totalScore >= 65 -> BullGreen
                    confluence.totalScore >= 50 -> WarningAmber
                    else -> BearRed
                },
                trackColor = CryptoSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-scores breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SubScoreItem(label = if (isRomanUrdu) "Trend" else "Trend (4H/1D)", score = confluence.trendScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "Indicators" else "Indicators", score = confluence.indicatorScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "SMC/BOS" else "SMC / BOS", score = confluence.smcScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "Funding" else "Derivatives", score = confluence.liquidityScore, max = 15)
                SubScoreItem(label = if (isRomanUrdu) "Fibonacci" else "Fibonacci", score = confluence.fibonacciScore, max = 10)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRomanUrdu) {
                    "Trade lene ka miyar 65+ score hai. Agar score kam ho to sabar karein ta k sarmaya mehfooz rahe."
                } else {
                    "Threshold for trade entry is 65+. Scored strictly with confirmed candle closes to prevent look-ahead bias."
                },
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SubScoreItem(label: String, score: Int, max: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$score/$max", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, fontSize = 9.sp, color = TextMuted)
    }
}

@Composable
private fun MtfAlignmentCard(mtf: MultiTimeframeAnalysis, isRomanUrdu: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isRomanUrdu) "Multi-Timeframe Structure Matrix (Bari aur Choti Timeframes)" else "Multi-Timeframe Structure Matrix",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MtfBadge(tf = "1D (Macro)", trend = mtf.dailyTrend)
                MtfBadge(tf = "4H (Trend)", trend = mtf.fourHourTrend)
                MtfBadge(tf = "1H (Structure)", trend = mtf.oneHourTrend)
                MtfBadge(tf = "15M (Entry)", trend = mtf.fifteenMinTrend)
                MtfBadge(tf = "5M (Trigger)", trend = mtf.fiveMinTrend)
            }

            if (mtf.conflictReason != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningAmber.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRomanUrdu) "Ikhtilaf: Timeframes aapas me match nahi kar rahein, intazar behtar hai." else mtf.conflictReason,
                        fontSize = 11.sp,
                        color = WarningAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun MtfBadge(tf: String, trend: TrendDirection) {
    val (color, text) = when (trend) {
        TrendDirection.BULLISH -> Pair(BullGreen, "BULL")
        TrendDirection.BEARISH -> Pair(BearRed, "BEAR")
        TrendDirection.NEUTRAL -> Pair(TextMuted, "RANGE")
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = tf, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun TradingPlanDetailsCard(plan: TradingPlan, isRomanUrdu: Boolean = false) {
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
                Text(
                    text = if (isRomanUrdu) "Mukammal Trading Plan" else "Complete Trading Plan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "R:R Ratio 1:${String.format("%.2f", plan.riskRewardRatio)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PlanMetricRow(
                label = if (isRomanUrdu) "Dakhla Zone (Suggested Entry)" else "Suggested Entry Zone",
                value = "$${formatPrice(plan.entryMin)} - $${formatPrice(plan.entryMax)}",
                color = TextPrimary
            )
            PlanMetricRow(
                label = if (isRomanUrdu) "Stop Loss (Nuqsan se Bachao)" else "Stop Loss (Invalidation)",
                value = "$${formatPrice(plan.stopLoss)}",
                color = BearRed
            )
            PlanMetricRow(
                label = if (isRomanUrdu) "Target 1 (Pehla Nafa)" else "Take Profit 1 (1:1.5)",
                value = "$${formatPrice(plan.tp1)}",
                color = BullGreen
            )
            PlanMetricRow(
                label = if (isRomanUrdu) "Target 2 (Fibo Golden Pocket)" else "Take Profit 2 (1:2.5 / Fibo)",
                value = "$${formatPrice(plan.tp2)}",
                color = BullGreen
            )
            PlanMetricRow(
                label = if (isRomanUrdu) "Target 3 (Runner Nafa)" else "Take Profit 3 (Runner)",
                value = "$${formatPrice(plan.tp3)}",
                color = BullGreen
            )
        }
    }
}

@Composable
private fun PlanMetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun EvidenceCard(
    supporting: List<String>,
    conflicting: List<String>,
    missing: List<String>,
    isRomanUrdu: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isRomanUrdu) "Technical Evidence Breakdown (Wajohat aur Ishare)" else "Technical Evidence Breakdown",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (supporting.isNotEmpty()) {
                Text(
                    text = if (isRomanUrdu) "Haq me Wajohat / Supporting Factors (${supporting.size}):" else "Supporting Factors (${supporting.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BullGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                supporting.forEach { item ->
                    val displayText = if (isRomanUrdu) translateFactorToRomanUrdu(item) else item
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BullGreen,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = displayText, fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }

            if (conflicting.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRomanUrdu) "Khatrat / Mukhalif Wajohat (${conflicting.size}):" else "Conflicting / Risk Factors (${conflicting.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BearRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                conflicting.forEach { item ->
                    val displayText = if (isRomanUrdu) translateFactorToRomanUrdu(item) else item
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = BearRed,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = displayText, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            if (missing.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRomanUrdu) "Ghair Dastyab Data (${missing.size}):" else "Missing / Unavailable Data (${missing.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                missing.forEach { item ->
                    Text(text = "• $item", fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

private fun translateFactorToRomanUrdu(factor: String): String {
    val f = factor.lowercase()
    return when {
        f.contains("higher timeframe 4h trend is bullish") -> "Bari timeframe (4H) ka trend Bullish hai (Khareedari ka rujhan)."
        f.contains("higher timeframe 4h trend is bearish") -> "Bari timeframe (4H) ka trend Bearish hai (Farokht ka rujhan)."
        f.contains("daily macro trend is bullish") -> "Daily (1D) macro trend mazboot Bullish hai."
        f.contains("daily macro trend is bearish") -> "Daily (1D) macro trend Bearish hai."
        f.contains("price above ema 20 and ema 50") -> "Price EMA 20 aur EMA 50 k upar chal rahi hai (Mazboot Uptrend)."
        f.contains("price below ema 20 and ema 50") -> "Price EMA 20 aur EMA 50 k nechy chal rahi hai (Mazboot Downtrend)."
        f.contains("rsi") && f.contains("bullish") -> "RSI momentum Bullish hai (Buyers market control kar rahy hain)."
        f.contains("rsi") && f.contains("bearish") -> "RSI momentum Bearish hai (Sellers ka dabao ziada hai)."
        f.contains("rsi") && f.contains("oversold") -> "RSI oversold zone me hai, bounce hone k imkanat hain."
        f.contains("rsi") && f.contains("overbought") -> "RSI overbought zone me hai, pull back ka khatra hai."
        f.contains("macd histogram is bullish") -> "MACD Histogram positive hai aur upar barh raha hai."
        f.contains("macd histogram is bearish") -> "MACD Histogram negative hai aur nechy ja raha hai."
        f.contains("bullish break of structure") || f.contains("bullish bos") -> "Bullish Break of Structure (BOS) confirm hua hai (Trend agay barh raha hai)."
        f.contains("bearish break of structure") || f.contains("bearish bos") -> "Bearish Break of Structure (BOS) confirm hua hai (Trend nechy toot raha hai)."
        f.contains("change of character") || f.contains("choch") -> "Change of Character (CHoCH) structural reversal ka ishara de raha hai."
        f.contains("bullish order block") -> "Bullish Order Block (Institutions ki demand zone) kareeb hai."
        f.contains("bearish order block") -> "Bearish Order Block (Institutions ki supply zone) kareeb hai."
        f.contains("fair value gap") || f.contains("fvg") -> "Fair Value Gap (FVG price pocket) retest ho chuki hai."
        f.contains("liquidity sweep") -> "Liquidity sweep ho chuki hai (Stop hunts mukammal ho gaye)."
        f.contains("fibonacci golden pocket") -> "Price Fibonacci Golden Pocket (61.8% - 78.6%) key level per hai."
        f.contains("funding rate is negative") -> "Futures Funding Rate negative hai (Short squeeze ka imkan)."
        f.contains("funding rate is positive") -> "Futures Funding Rate positive hai (Long positions active hain)."
        f.contains("open interest is rising") -> "Open Interest (OI) barh raha hai (Naya sarmaya futures market me aa raha hai)."
        f.contains("volume above 20-period average") -> "Volume 20-period average se ziada hai (Bari activity)."
        f.contains("timeframe conflict") -> "Timeframes aapas me mukhalif hain (4H aur 15m me farq hai)."
        f.contains("resistance") -> "Price ahem Resistance zone k bilkul kareeb hai."
        f.contains("support") -> "Price ahem Support zone k kareeb trade kar rahi hai."
        else -> factor
    }
}

@Composable
private fun RiskCalculatorCard(
    plan: TradingPlan,
    accountBalance: Double,
    riskPercent: Double,
    leverage: Int,
    isRomanUrdu: Boolean = false,
    onUpdateRisk: (Double, Double, Int) -> Unit
) {
    var balanceInput by remember(accountBalance) { mutableStateOf(accountBalance.toInt().toString()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Position Sizing & Risk Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calculates exact position size so stop-loss strictly limits your loss.",
                fontSize = 10.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Inputs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = balanceInput,
                    onValueChange = {
                        balanceInput = it
                        val b = it.toDoubleOrNull()
                        if (b != null && b > 0) {
                            onUpdateRisk(b, riskPercent, leverage)
                        }
                    },
                    label = { Text("Balance ($)", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Risk % Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text("Risk %: ${riskPercent}%", fontSize = 11.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1.0, 2.0, 3.0).forEach { r ->
                            val isSel = r == riskPercent
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) BullGreen else CryptoSurfaceVariant)
                                    .clickable { onUpdateRisk(accountBalance, r, leverage) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${r.toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Leverage Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text("Leverage: ${leverage}x", fontSize = 11.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(2, 5, 10).forEach { lev ->
                            val isSel = lev == leverage
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) AccentCyan else CryptoSurfaceVariant)
                                    .clickable { onUpdateRisk(accountBalance, riskPercent, lev) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${lev}x",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val pos = plan.positionCalc
            if (pos != null) {
                PlanMetricRow(
                    label = "Max Dollar Risk",
                    value = "$${formatPrice(pos.riskAmount)}",
                    color = BearRed
                )
                PlanMetricRow(
                    label = "Suggested Position Size",
                    value = "$${formatPrice(pos.positionSizeUsd)} (${String.format("%.4f", pos.coinQuantity)} coins)",
                    color = AccentCyan
                )
                PlanMetricRow(
                    label = "Required Margin (Capital)",
                    value = "$${formatPrice(pos.positionSizeUsd / leverage)}",
                    color = TextPrimary
                )
                PlanMetricRow(
                    label = "Est. Liquidation Price",
                    value = "$${formatPrice(pos.estimatedLiquidationPrice)}",
                    color = WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "⚠️ Warning: Higher leverage multiplies liquidation hazard. Never risk more than you can afford to lose.",
                fontSize = 10.sp,
                color = WarningAmber
            )
        }
    }
}


private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun formatPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("%,.2f", p)
        p >= 1.0 -> String.format("%.4f", p)
        else -> String.format("%.6f", p)
    }
}
