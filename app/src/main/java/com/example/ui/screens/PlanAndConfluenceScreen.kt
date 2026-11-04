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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.theme.GoldenPocketGold
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
    hasPreciseTargets: Boolean = true,
    onOpenSubscription: () -> Unit = {},
    onUpdateRisk: (Double, Double, Int) -> Unit,
    onExecutePaperTrade: () -> Unit,
    onSaveSignal: () -> Unit,
    onNavigateToSmcDetail: () -> Unit = {},
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

        Spacer(modifier = Modifier.height(10.dp))

        // Quick SMC & Fibonacci Pair Detail Navigation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToSmcDetail() }
                .testTag("btn_signal_to_smc_fib"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(GoldenPocketGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = GoldenPocketGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isRomanUrdu) "${plan.symbol} ایس ایم سی اور فبوناکی جائزہ" else "${plan.symbol} SMC & Fibonacci Tool",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRomanUrdu) "آرڈر بلاکس، FVG اور گولڈن پاکٹ لیڈر" else "Inspect Order Blocks, FVGs & Custom Golden Pocket",
                            fontSize = 11.sp,
                            color = GoldenPocketGold
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = GoldenPocketGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Confluence Score Gauge Card
        ConfluenceScoreCard(confluence = confluence, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Multi-Timeframe Alignment Card
        MtfAlignmentCard(mtf = mtf, isRomanUrdu = isRomanUrdu)

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Complete Trading Plan
        TradingPlanDetailsCard(
            plan = plan,
            isRomanUrdu = isRomanUrdu,
            hasPreciseTargets = hasPreciseTargets,
            onOpenSubscription = onOpenSubscription
        )

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
                        if (isRomanUrdu) "انتظار کریں (کوئی ٹریڈ نہ لیں)" else "WAIT (No Trade)"
                    } else {
                        if (isRomanUrdu) "پیپر ٹریڈ شروع کریں" else "Paper Trade Setup"
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
                    text = if (isRomanUrdu) "محفوظ کریں" else "Save",
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
            if (isRomanUrdu) "خریدیں / لانگ سگنل (BUY)" else "LONG SIGNAL DETECTED",
            if (isRomanUrdu) "مضبوط بلش کنفلوئنس اسکور (${confluence.totalScore}/100)" else "High Confluence Bullish Setup (${confluence.totalScore}/100)"
        )
        SignalType.SHORT -> Quadruple(
            BearRed,
            BearRedGlow,
            if (isRomanUrdu) "فروخت کریں / شارٹ سگنل (SELL)" else "SHORT SIGNAL DETECTED",
            if (isRomanUrdu) "مضبوط بیرش کنفلوئنس اسکور (${confluence.totalScore}/100)" else "High Confluence Bearish Setup (${confluence.totalScore}/100)"
        )
        SignalType.WAIT -> Quadruple(
            WarningAmber,
            WarningAmberGlow,
            if (isRomanUrdu) "انتظار کریں - کوئی ٹریڈ نہ لیں" else "WAIT - NO TRADE",
            if (isRomanUrdu) "اشارے متضاد ہیں یا اسکور (${confluence.totalScore}/100) 65 سے کم ہے" else "Signals conflicting or score (${confluence.totalScore}/100) below 65"
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
                            SignalType.LONG -> "لانگ ▲"
                            SignalType.SHORT -> "شارٹ ▼"
                            SignalType.WAIT -> "انتظار ⏸"
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
                    text = if (isRomanUrdu) "کنفلوئنس اسکورنگ انجن" else "Confluence Scoring Engine",
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
                SubScoreItem(label = if (isRomanUrdu) "ٹرینڈ" else "Trend (4H/1D)", score = confluence.trendScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "اشارے" else "Indicators", score = confluence.indicatorScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "SMC / BOS" else "SMC / BOS", score = confluence.smcScore, max = 25)
                SubScoreItem(label = if (isRomanUrdu) "ڈیریویٹوز" else "Derivatives", score = confluence.liquidityScore, max = 15)
                SubScoreItem(label = if (isRomanUrdu) "فبوناکی" else "Fibonacci", score = confluence.fibonacciScore, max = 10)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRomanUrdu) {
                    "ٹریڈ لینے کا معیار 65+ اسکور ہے۔ اس سے کم اسکور پر صبر کریں تاکہ سرمایہ محفوظ رہے۔"
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
                text = if (isRomanUrdu) "ملٹی ٹائم فریم اسٹرکچر میٹرکس" else "Multi-Timeframe Structure Matrix",
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
                        text = if (isRomanUrdu) "اختلاف: ٹائم فریمز آپس میں میچ نہیں کر رہے، انتظار بہتر ہے۔" else mtf.conflictReason,
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
private fun TradingPlanDetailsCard(
    plan: TradingPlan,
    isRomanUrdu: Boolean = false,
    hasPreciseTargets: Boolean = true,
    onOpenSubscription: () -> Unit = {}
) {
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
                    text = if (isRomanUrdu) "مکمل ٹریڈنگ پلان" else "Complete Trading Plan",
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

            if (!hasPreciseTargets) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CryptoSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isRomanUrdu) "اسٹاپ لاس اور TP اہداف مقفل ہیں" else "Precision SL & TP Targets Locked",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isRomanUrdu) {
                                "درست اسٹاپ لاس اور ٹیک پرافٹ لیولز سلور پرو یا گولڈن وی آئی پی پلان میں شامل ہیں۔"
                            } else {
                                "Exact Stop Loss and Take Profit levels require Silver Pro or Golden VIP Plan."
                            },
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenSubscription,
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (isRomanUrdu) "پلان اپ گریڈ کریں" else "Upgrade to Unlock Targets", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                PlanMetricRow(
                    label = if (isRomanUrdu) "تجویز کردہ داخلہ زون (Entry)" else "Suggested Entry Zone",
                    value = "$${formatPrice(plan.entryMin)} - $${formatPrice(plan.entryMax)}",
                    color = TextPrimary
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "اسٹاپ لاس (SL - نقصان سے بچاؤ)" else "Stop Loss (Invalidation)",
                    value = "$${formatPrice(plan.stopLoss)}",
                    color = BearRed
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "پہلا ہدف (TP1)" else "Take Profit 1 (1:1.5)",
                    value = "$${formatPrice(plan.tp1)}",
                    color = BullGreen
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "دوسرا ہدف (TP2 - فبوناکی)" else "Take Profit 2 (1:2.5 / Fibo)",
                    value = "$${formatPrice(plan.tp2)}",
                    color = BullGreen
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "تیسرا ہدف (TP3 - رنر)" else "Take Profit 3 (Runner)",
                    value = "$${formatPrice(plan.tp3)}",
                    color = BullGreen
                )
            }
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
                text = if (isRomanUrdu) "تکنیکی شواہد کی تفصیلات" else "Technical Evidence Breakdown",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (supporting.isNotEmpty()) {
                Text(
                    text = if (isRomanUrdu) "حق میں وجوہات / بلش عوامل (${supporting.size}):" else "Supporting Factors (${supporting.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BullGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                supporting.forEach { item ->
                    val displayText = if (isRomanUrdu) translateFactorToUrdu(item) else item
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
                    text = if (isRomanUrdu) "مخالف وجوہات / خطرات (${conflicting.size}):" else "Conflicting / Risk Factors (${conflicting.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BearRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                conflicting.forEach { item ->
                    val displayText = if (isRomanUrdu) translateFactorToUrdu(item) else item
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
                    text = if (isRomanUrdu) "غیر دستیاب ڈیٹا (${missing.size}):" else "Missing / Unavailable Data (${missing.size}):",
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

private fun translateFactorToUrdu(factor: String): String {
    val f = factor.lowercase()
    return when {
        f.contains("higher timeframe 4h trend is bullish") -> "بڑی ٹائم فریم (4H) کا ٹرینڈ بلش ہے (خریداری کا رجحان)۔"
        f.contains("higher timeframe 4h trend is bearish") -> "بڑی ٹائم فریم (4H) کا ٹرینڈ بیرش ہے (فروخت کا رجحان)۔"
        f.contains("daily macro trend is bullish") -> "ڈیلی (1D) میکرو ٹرینڈ مضبوط بلش ہے۔"
        f.contains("daily macro trend is bearish") -> "ڈیلی (1D) میکرو ٹرینڈ بیرش ہے۔"
        f.contains("bullish ema 20/50 alignment") || (f.contains("ema") && f.contains("above")) -> "EMA 20/50 بلش: قیمت موونگ ایوریجز سے اوپر چل رہی ہے (مضبوط اپ ٹرینڈ)۔"
        f.contains("bearish ema 20/50 alignment") || (f.contains("ema") && f.contains("below")) -> "EMA 20/50 بیرش: قیمت موونگ ایوریجز سے نیچے چل رہی ہے (مضبوط ڈاؤن ٹرینڈ)۔"
        f.contains("emas tangled") -> "EMAs نیوٹرل ہیں، قیمت فی الحال سکڑ رہی ہے۔"
        f.contains("rsi") && f.contains("overbought") -> "RSI اوور بوٹ زون میں ہے (>70)، پل بیک کا خطرہ بڑھ گیا ہے۔"
        f.contains("rsi") && f.contains("oversold") -> "RSI اوور سولڈ زون میں ہے (<30)، مارکیٹ میں باؤنس کا امکان ہے۔"
        f.contains("rsi") && f.contains("bullish") -> "RSI مومنٹم بلش ہے (خریدار مارکیٹ کنٹرول کر رہے ہیں)۔"
        f.contains("rsi") && f.contains("bearish") -> "RSI مومنٹم بیرش ہے (سیلرز کا دباؤ زیادہ ہے)۔"
        f.contains("macd histogram positive") || (f.contains("macd") && f.contains("bullish")) -> "MACD ہسٹوگرام مثبت ہے اور بلش مومنٹم بڑھ رہا ہے۔"
        f.contains("macd histogram negative") || (f.contains("macd") && f.contains("bearish")) -> "MACD ہسٹوگرام منفی ہے اور بیرش مومنٹم بڑھ رہا ہے۔"
        f.contains("bullish break of structure") || f.contains("bullish bos") -> "بلش بریک آف اسٹرکچر (BOS) کنفرم ہوا ہے (ٹرینڈ آگے بڑھ رہا ہے)۔"
        f.contains("bearish break of structure") || f.contains("bearish bos") -> "بیرش بریک آف اسٹرکچر (BOS) کنفرم ہوا ہے (ٹرینڈ نیچے ٹوٹ رہا ہے)۔"
        f.contains("change of character") || f.contains("choch") -> "چینج آف کیریکٹر (CHoCH) اسٹرکچرل ریورسل کا اشارہ دے رہا ہے۔"
        f.contains("bullish order block") -> "بلش آرڈر بلاک (ڈیمانڈ زون) قریب ہے۔"
        f.contains("bearish order block") -> "بیرش آرڈر بلاک (سپلائی زون) قریب ہے۔"
        f.contains("fair value gap") || f.contains("fvg") -> "فیئر ویلیو گیپ (FVG) ری ٹیسٹ ہو چکا ہے۔"
        f.contains("liquidity sweep") -> "لیکویڈیٹی سویپ مکمل ہو چکی ہے۔"
        f.contains("fibonacci golden pocket") -> "قیمت فبوناکی گولڈن پاکٹ (61.8% - 65%) کے اہم زون میں ہے۔"
        f.contains("key fibo levels active") -> "اہم فبوناکی لیولز (50% اور 61.8%) فعال ہیں۔"
        f.contains("funding rate negative") -> "فیوچرز فنڈنگ ریٹ منفی ہے (شارٹ اسکوئز کا امکان)۔"
        f.contains("funding rate positive") -> "فیوچرز فنڈنگ ریٹ مثبت ہے (لانگ پوزیشنز فعال ہیں)۔"
        f.contains("open interest is rising") -> "اوپن انٹرسٹ (OI) بڑھ رہا ہے (نیا سرمایہ فیوچرز مارکیٹ میں آ رہا ہے)۔"
        f.contains("long/short account ratio") -> "فیوچرز لانگ / شارٹ ریشو ڈیٹا فعال ہے۔"
        f.contains("volume above 20-period average") -> "والیوم 20 پیریڈ اوسط سے زیادہ ہے۔"
        f.contains("timeframe conflict") -> "ٹائم فریمز آپس میں متضاد ہیں (4H اور 15M میں فرق ہے)۔"
        f.contains("resistance") -> "قیمت اہم ریزسٹنس زون کے قریب ہے۔"
        f.contains("support") -> "قیمت اہم سپورٹ زون کے قریب ٹریڈ کر رہی ہے۔"
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
                text = if (isRomanUrdu) "پوزیشن سائز اور رسک مینجمنٹ" else "Position Sizing & Risk Management",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isRomanUrdu) "صحیح پوزیشن سائز کیلکولیٹ کریں تاکہ اسٹاپ لاس آپ کا نقصان سختی سے حد میں رکھے۔" else "Calculates exact position size so stop-loss strictly limits your loss.",
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
                    label = { Text(if (isRomanUrdu) "بیلنس ($)" else "Balance ($)", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                // Risk % Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isRomanUrdu) "رسک: ${riskPercent}%" else "Risk %: ${riskPercent}%", fontSize = 11.sp, color = TextSecondary)
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
                    Text(if (isRomanUrdu) "لیوریج: ${leverage}x" else "Leverage: ${leverage}x", fontSize = 11.sp, color = TextSecondary)
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
                    label = if (isRomanUrdu) "زیادہ سے زیادہ رسک کی رقم" else "Max Dollar Risk",
                    value = "$${formatPrice(pos.riskAmount)}",
                    color = BearRed
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "تجویز کردہ پوزیشن سائز" else "Suggested Position Size",
                    value = "$${formatPrice(pos.positionSizeUsd)} (${String.format("%.4f", pos.coinQuantity)} coins)",
                    color = AccentCyan
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "ضروری مارجن (آپ کا سرمایہ)" else "Required Margin (Capital)",
                    value = "$${formatPrice(pos.positionSizeUsd / leverage)}",
                    color = TextPrimary
                )
                PlanMetricRow(
                    label = if (isRomanUrdu) "اندازاً لیکویڈیشن قیمت" else "Est. Liquidation Price",
                    value = "$${formatPrice(pos.estimatedLiquidationPrice)}",
                    color = WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isRomanUrdu) "⚠️ خبردار: زیادہ لیوریج لیکویڈیشن کا خطرہ بڑھا دیتی ہے۔ ہمیشہ اسٹاپ لاس استعمال کریں۔" else "⚠️ Warning: Higher leverage multiplies liquidation hazard. Never risk more than you can afford to lose.",
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
