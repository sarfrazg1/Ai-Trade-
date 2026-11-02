package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BreakOfStructure
import com.example.domain.model.ChangeOfCharacter
import com.example.domain.model.FairValueGap
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.FuturesStats
import com.example.domain.model.LiquiditySweep
import com.example.domain.model.OrderBlock
import com.example.domain.model.SmartMoneyConcepts
import com.example.domain.model.Timeframe
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.GoldenPocketGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun PairDetailScreen(
    symbol: String,
    timeframe: Timeframe,
    currentPrice: Double,
    stats: FuturesStats?,
    smc: SmartMoneyConcepts?,
    fibonacci: FibonacciAnalysis?,
    isRomanUrdu: Boolean = false,
    onNavigateToChart: () -> Unit,
    onNavigateToSignal: () -> Unit,
    onExecuteInstantTrade: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    // Interactive Fibonacci Tool Custom Inputs
    var customHighText by remember(symbol, fibonacci) {
        val initialHigh = fibonacci?.swingHigh ?: (currentPrice * 1.05)
        mutableStateOf(String.format(Locale.US, if (initialHigh >= 100) "%.2f" else "%.4f", initialHigh))
    }
    var customLowText by remember(symbol, fibonacci) {
        val initialLow = fibonacci?.swingLow ?: (currentPrice * 0.95)
        mutableStateOf(String.format(Locale.US, if (initialLow >= 100) "%.2f" else "%.4f", initialLow))
    }
    var isBullishSwing by remember(fibonacci) {
        mutableStateOf(fibonacci?.isUptrendSwing ?: true)
    }

    // Active SMC Sub-Filter
    var selectedSmcFilter by remember { mutableStateOf("ALL") } // ALL, OB, FVG, BOS_CHOCH, LIQ

    val parsedHigh = customHighText.toDoubleOrNull() ?: (currentPrice * 1.05)
    val parsedLow = customLowText.toDoubleOrNull() ?: (currentPrice * 0.95)
    val swingHigh = max(parsedHigh, parsedLow)
    val swingLow = min(parsedHigh, parsedLow)
    val swingRange = max(0.00001, swingHigh - swingLow)

    // Calculate Dynamic Fibonacci Retracement Levels based on Custom Inputs
    val fibLevels = remember(swingHigh, swingLow, isBullishSwing) {
        calculateFibonacciLadder(swingHigh, swingLow, isBullishSwing)
    }

    // Determine current market structure regime
    val isBullishRegime = (smc?.bosList?.lastOrNull()?.isBullish == true) ||
            (smc?.chochList?.lastOrNull()?.isBullish == true) ||
            (stats != null && stats.priceChange24h >= 0)

    // Calculate Premium vs Discount position (0% = Swing Low, 100% = Swing High)
    val rangePercent = ((currentPrice - swingLow) / swingRange).coerceIn(0.0, 1.0)
    val isDiscountZone = rangePercent < 0.50

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(scrollState)
            .padding(14.dp)
            .testTag("pair_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HERO PAIR HEADER & 24H TELEMETRY CARD
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Row 1: Symbol & Timeframe & Regime Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isBullishRegime) BullGreen.copy(alpha = 0.2f) else BearRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isBullishRegime) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isBullishRegime) BullGreen else BearRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = symbol,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CryptoSurfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = timeframe.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentCyan
                                    )
                                }
                            }
                            Text(
                                text = if (isRomanUrdu) "Smart Money aur Fibonacci Jaiza" else "SMC & Fibonacci Structure View",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Regime Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isBullishRegime) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                if (isBullishRegime) BullGreen.copy(alpha = 0.4f) else BearRed.copy(alpha = 0.4f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBullishRegime) {
                                if (isRomanUrdu) "BULLISH DHANCHA" else "BULLISH STRUCTURE"
                            } else {
                                if (isRomanUrdu) "BEARISH DHANCHA" else "BEARISH STRUCTURE"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBullishRegime) BullGreen else BearRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 2: Live Price & 24h Change
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (isRomanUrdu) "Live Qeemat (Price)" else "Live Market Price",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = formatPrice(currentPrice),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stats != null && stats.priceChange24h >= 0) BullGreen else BearRed
                        )
                    }

                    if (stats != null) {
                        val changeSign = if (stats.priceChange24h >= 0) "+" else ""
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (stats.priceChange24h >= 0) BullGreen.copy(alpha = 0.2f) else BearRed.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$changeSign${String.format(Locale.US, "%.2f", stats.priceChange24h)}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.priceChange24h >= 0) BullGreen else BearRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CryptoCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Row 3: 24h High, Low, Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryStat(
                        label = if (isRomanUrdu) "24h Aala (High)" else "24h High",
                        value = if (stats != null) formatPrice(stats.high24h) else formatPrice(swingHigh),
                        color = BullGreen
                    )
                    TelemetryStat(
                        label = if (isRomanUrdu) "24h Kam (Low)" else "24h Low",
                        value = if (stats != null) formatPrice(stats.low24h) else formatPrice(swingLow),
                        color = BearRed
                    )
                    TelemetryStat(
                        label = if (isRomanUrdu) "24h Hajam (Vol)" else "24h Volume",
                        value = if (stats != null) formatVolume(stats.volume24h) else "--",
                        color = AccentCyan
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Premium vs Discount Zone Progress Gauge
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isRomanUrdu) "Discount Zone (Sasta - Khareedari)" else "Discount Zone (<50%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BullGreen
                        )
                        Text(
                            text = "50% EQ",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = if (isRomanUrdu) "Premium Zone (Mehenga - Farokht)" else "Premium Zone (>50%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BearRed
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Gauge Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CryptoSurfaceVariant)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(BullGreen.copy(alpha = 0.35f))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(BearRed.copy(alpha = 0.35f))
                            )
                        }
                        // Indicator dot at rangePercent
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(rangePercent.toFloat())
                                .height(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.CenterEnd)
                                    .clip(CircleShape)
                                    .background(if (isDiscountZone) BullGreen else BearRed)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDiscountZone) {
                            if (isRomanUrdu) "Qeemat Discount Zone me hai (~${String.format(Locale.US, "%.0f", rangePercent * 100)}%). Long entries ke liye mozoon hai."
                            else "Price is currently in the Discount Zone (~${String.format(Locale.US, "%.0f", rangePercent * 100)}%). Smart money favors dip buying."
                        } else {
                            if (isRomanUrdu) "Qeemat Premium Zone me hai (~${String.format(Locale.US, "%.0f", rangePercent * 100)}%). Short entries ya TP lene ka time hai."
                            else "Price is currently in the Premium Zone (~${String.format(Locale.US, "%.0f", rangePercent * 100)}%). Smart money favors selling rallies."
                        },
                        fontSize = 11.sp,
                        color = if (isDiscountZone) BullGreen else WarningAmber,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ========================================================
        // 2. FIBONACCI RETRACEMENT TOOL INPUTS & CALCULATOR
        // ========================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Golden Pocket badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldenPocketGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = GoldenPocketGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isRomanUrdu) "Fibonacci Retracement Tool" else "Fibonacci Retracement Tool",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isRomanUrdu) "Swing High / Low daal kar Golden Pocket check karein" else "Custom Swing High/Low & Golden Pocket Ladder",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Auto Fill Button
                    OutlinedButton(
                        onClick = {
                            val detectedHigh = fibonacci?.swingHigh ?: (currentPrice * 1.05)
                            val detectedLow = fibonacci?.swingLow ?: (currentPrice * 0.95)
                            customHighText = String.format(Locale.US, if (detectedHigh >= 100) "%.2f" else "%.4f", detectedHigh)
                            customLowText = String.format(Locale.US, if (detectedLow >= 100) "%.2f" else "%.4f", detectedLow)
                            isBullishSwing = fibonacci?.isUptrendSwing ?: true
                            Toast.makeText(
                                context,
                                if (isRomanUrdu) "Chart ke High/Low khudkar bhar gaye!" else "Auto-filled chart swing points!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldenPocketGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_autofill_fib")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRomanUrdu) "Auto Bharo" else "Auto Detect",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Fields: Swing High & Swing Low
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = customHighText,
                        onValueChange = { customHighText = it },
                        label = { Text(if (isRomanUrdu) "Swing High ($)" else "Swing High ($)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CryptoSurfaceVariant,
                            unfocusedContainerColor = CryptoSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_swing_high")
                    )

                    OutlinedTextField(
                        value = customLowText,
                        onValueChange = { customLowText = it },
                        label = { Text(if (isRomanUrdu) "Swing Low ($)" else "Swing Low ($)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BearRed,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CryptoSurfaceVariant,
                            unfocusedContainerColor = CryptoSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_swing_low")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Trend Direction Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isBullishSwing,
                        onClick = { isBullishSwing = true },
                        label = {
                            Text(
                                text = if (isRomanUrdu) "Bullish Swing (Dip Buy)" else "Bullish Retracement",
                                fontSize = 12.sp,
                                fontWeight = if (isBullishSwing) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BullGreen.copy(alpha = 0.25f),
                            selectedLabelColor = BullGreen,
                            containerColor = CryptoSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = !isBullishSwing,
                        onClick = { isBullishSwing = false },
                        label = {
                            Text(
                                text = if (isRomanUrdu) "Bearish Swing (Rally Short)" else "Bearish Retracement",
                                fontSize = 12.sp,
                                fontWeight = if (!isBullishSwing) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BearRed.copy(alpha = 0.25f),
                            selectedLabelColor = BearRed,
                            containerColor = CryptoSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Calculated Golden Pocket Highlight Box
                val gpMin = fibLevels.firstOrNull { it.ratio == 0.618 }?.price ?: 0.0
                val gpMax = fibLevels.firstOrNull { it.ratio == 0.650 }?.price ?: 0.0
                val isInGp = currentPrice in min(gpMin, gpMax)..max(gpMin, gpMax)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldenPocketGold.copy(alpha = 0.12f))
                        .border(1.5.dp, GoldenPocketGold, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldenPocketGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRomanUrdu) "SONE KI POCKET (0.618 - 0.650)" else "GOLDEN POCKET (0.618 - 0.650)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenPocketGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${formatPrice(min(gpMin, gpMax))} - ${formatPrice(max(gpMin, gpMax))}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isInGp) BullGreen else GoldenPocketGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isInGp) {
                                    if (isRomanUrdu) "QEEMAT ANDAR HAI!" else "PRICE IN POCKET!"
                                } else {
                                    if (isRomanUrdu) "KHAAS REVERSAL ZONE" else "KEY REVERSAL ZONE"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInGp) Color.Black else GoldenPocketGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Complete Fibonacci Ladder Table
                Text(
                    text = if (isRomanUrdu) "Mukammal Fibonacci Ladder Darja-ba-Darja" else "Fibonacci Levels & Price Distance Ladder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CryptoSurfaceVariant)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    fibLevels.forEach { level ->
                        FibonacciLadderRow(
                            level = level,
                            currentPrice = currentPrice,
                            isRomanUrdu = isRomanUrdu,
                            onCopyLevel = {
                                clipboardManager.setText(AnnotatedString(level.price.toString()))
                                Toast.makeText(
                                    context,
                                    if (isRomanUrdu) "Level ${level.label} ($${formatPrice(level.price)}) copy ho gaya!" else "Copied ${level.label}: $${formatPrice(level.price)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }

        // ========================================================
        // 3. SMART MONEY CONCEPT (SMC) MARKERS INSPECTOR
        // ========================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isRomanUrdu) "Smart Money Concept (SMC) Markers" else "Smart Money Concepts (SMC) Markers",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isRomanUrdu) "Order Blocks, Fair Value Gaps, BOS aur CHoCH" else "Order Blocks, Fair Value Gaps, BOS & Liquidity",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SMC Category Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val filters = listOf(
                        "ALL" to (if (isRomanUrdu) "Sab" else "All"),
                        "OB" to (if (isRomanUrdu) "Order Blocks" else "Order Blocks"),
                        "FVG" to (if (isRomanUrdu) "FVG Gaps" else "Fair Value"),
                        "BOS" to (if (isRomanUrdu) "BOS / CHoCH" else "BOS / CHoCH"),
                        "LIQ" to (if (isRomanUrdu) "Liquidity" else "Liquidity")
                    )

                    filters.forEach { (key, label) ->
                        val isSelected = selectedSmcFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentCyan else CryptoSurfaceVariant)
                                .clickable { selectedSmcFilter = key }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SMC 1: Active Order Blocks (Demand & Supply)
                if (selectedSmcFilter in listOf("ALL", "OB")) {
                    Text(
                        text = if (isRomanUrdu) "Active Order Blocks (Demand aur Supply)" else "Active Order Blocks (Supply & Demand)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val activeDemand = smc?.activeDemandZone
                    val activeSupply = smc?.activeSupplyZone

                    if (activeDemand == null && activeSupply == null && (smc?.orderBlocks.isNullOrEmpty())) {
                        EmptyNoticeCard(
                            text = if (isRomanUrdu) "Is timeframe par koi taaza Order Block nahi mila." else "No fresh Order Blocks on this timeframe."
                        )
                    } else {
                        // Bullish Demand Zone Card
                        if (activeDemand != null) {
                            OrderBlockDetailCard(
                                orderBlock = activeDemand,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Bearish Supply Zone Card
                        if (activeSupply != null) {
                            OrderBlockDetailCard(
                                orderBlock = activeSupply,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Other recent order blocks if available
                        val otherObs = smc?.orderBlocks?.filter { it != activeDemand && it != activeSupply } ?: emptyList()
                        otherObs.take(3).forEach { ob ->
                            OrderBlockDetailCard(
                                orderBlock = ob,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // SMC 2: Fair Value Gaps (FVG)
                if (selectedSmcFilter in listOf("ALL", "FVG")) {
                    Text(
                        text = if (isRomanUrdu) "Fair Value Gaps (FVG - Qadr Ka Khala)" else "Fair Value Gaps (FVG Imbalances)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val fvgs = smc?.fvgList ?: emptyList()
                    if (fvgs.isEmpty()) {
                        EmptyNoticeCard(
                            text = if (isRomanUrdu) "Is timeframe par koi unmitigated FVG nahi mila." else "No unmitigated Fair Value Gaps detected."
                        )
                    } else {
                        fvgs.take(4).forEach { fvg ->
                            FvgDetailCard(
                                fvg = fvg,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // SMC 3: Break of Structure (BOS) & Change of Character (CHoCH)
                if (selectedSmcFilter in listOf("ALL", "BOS")) {
                    Text(
                        text = if (isRomanUrdu) "Break of Structure (BOS) & CHoCH" else "Break of Structure (BOS) & CHoCH Reversals",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val bosList = smc?.bosList ?: emptyList()
                    val chochList = smc?.chochList ?: emptyList()

                    if (bosList.isEmpty() && chochList.isEmpty()) {
                        EmptyNoticeCard(
                            text = if (isRomanUrdu) "Abhi tak koi taaza BOS ya CHoCH trigger nahi hua." else "No active BOS or CHoCH structure shifts recorded."
                        )
                    } else {
                        bosList.takeLast(2).forEach { bos ->
                            BosDetailCard(
                                bos = bos,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        chochList.takeLast(2).forEach { choch ->
                            ChochDetailCard(
                                choch = choch,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // SMC 4: Liquidity Sweeps & Equal Highs/Lows
                if (selectedSmcFilter in listOf("ALL", "LIQ")) {
                    Text(
                        text = if (isRomanUrdu) "Liquidity Sweeps & Pools (EQH / EQL)" else "Liquidity Sweeps & Pool Levels",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val sweeps = smc?.liquiditySweeps ?: emptyList()
                    val eqh = smc?.equalHighs ?: emptyList()
                    val eql = smc?.equalLows ?: emptyList()

                    if (sweeps.isEmpty() && eqh.isEmpty() && eql.isEmpty()) {
                        EmptyNoticeCard(
                            text = if (isRomanUrdu) "Koi barri liquidity sweep ya equal level detect nahi hua." else "No major liquidity sweep or equal levels found."
                        )
                    } else {
                        // EQH (Buy-side liquidity)
                        eqh.take(2).forEach { level ->
                            LiquidityPoolCard(
                                title = if (isRomanUrdu) "Equal Highs (EQH - Buy-Side Liquidity Pool)" else "Equal Highs (EQH - Buy-side Pool)",
                                level = level,
                                currentPrice = currentPrice,
                                isHigh = true,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // EQL (Sell-side liquidity)
                        eql.take(2).forEach { level ->
                            LiquidityPoolCard(
                                title = if (isRomanUrdu) "Equal Lows (EQL - Sell-Side Liquidity Pool)" else "Equal Lows (EQL - Sell-side Pool)",
                                level = level,
                                currentPrice = currentPrice,
                                isHigh = false,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Sweeps
                        sweeps.takeLast(2).forEach { sweep ->
                            SweepDetailCard(
                                sweep = sweep,
                                currentPrice = currentPrice,
                                isRomanUrdu = isRomanUrdu
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }

        // ========================================================
        // 4. ACTION SHORTCUTS (Quick Open Chart / Signal / Trade)
        // ========================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isRomanUrdu) "Fori Karwai (Quick Actions)" else "Instant Trade & Navigation Actions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToChart,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_to_chart")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRomanUrdu) "Chart Kholein" else "Open Chart",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onNavigateToSignal,
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_to_signal")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRomanUrdu) "Signal Dekhein" else "View Signal",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onExecuteInstantTrade(true) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BullGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BullGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_instant_long")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "Fori BUY (Long)" else "Instant LONG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { onExecuteInstantTrade(false) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BearRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BearRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_detail_instant_short")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "Fori SELL (Short)" else "Instant SHORT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

// ==========================================
// FIBONACCI ROW & HELPER MODELS
// ==========================================

data class FibLevelItem(
    val ratio: Double,
    val label: String,
    val price: Double,
    val isGoldenPocket: Boolean = false,
    val isExtension: Boolean = false
)

private fun calculateFibonacciLadder(
    high: Double,
    low: Double,
    isBullishSwing: Boolean
): List<FibLevelItem> {
    val diff = high - low
    val ratios = listOf(
        0.000 to "0.0%",
        0.236 to "23.6%",
        0.382 to "38.2%",
        0.500 to "50.0% (EQ)",
        0.618 to "61.8%",
        0.650 to "65.0%",
        0.786 to "78.6%",
        1.000 to "100.0%",
        1.272 to "127.2% Ext",
        1.618 to "161.8% Ext"
    )

    return ratios.map { (ratio, label) ->
        val price = if (isBullishSwing) {
            // Uptrend retracement: 0% is High, 100% is Low
            if (ratio <= 1.0) high - (diff * ratio) else high + (diff * (ratio - 1.0))
        } else {
            // Downtrend retracement: 0% is Low, 100% is High
            if (ratio <= 1.0) low + (diff * ratio) else low - (diff * (ratio - 1.0))
        }
        FibLevelItem(
            ratio = ratio,
            label = label,
            price = max(0.000001, price),
            isGoldenPocket = ratio == 0.618 || ratio == 0.650,
            isExtension = ratio > 1.0
        )
    }
}

@Composable
private fun FibonacciLadderRow(
    level: FibLevelItem,
    currentPrice: Double,
    isRomanUrdu: Boolean,
    onCopyLevel: () -> Unit
) {
    val diff = level.price - currentPrice
    val diffPercent = if (currentPrice > 0) (diff / currentPrice) * 100 else 0.0
    val isAbove = diff > 0
    val isClose = abs(diffPercent) < 0.3

    val rowBg = when {
        level.isGoldenPocket -> GoldenPocketGold.copy(alpha = 0.15f)
        isClose -> AccentCyan.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onCopyLevel() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            level.isGoldenPocket -> GoldenPocketGold.copy(alpha = 0.3f)
                            level.isExtension -> AccentCyan.copy(alpha = 0.25f)
                            else -> CryptoSurface
                        }
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = level.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        level.isGoldenPocket -> GoldenPocketGold
                        level.isExtension -> AccentCyan
                        else -> TextPrimary
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$${formatPrice(level.price)}",
                fontSize = 13.sp,
                fontWeight = if (level.isGoldenPocket) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (level.isGoldenPocket) GoldenPocketGold else TextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val distLabel = when {
                isClose -> if (isRomanUrdu) "Testing!" else "Testing!"
                isAbove -> "+${String.format(Locale.US, "%.2f", diffPercent)}%"
                else -> "${String.format(Locale.US, "%.2f", diffPercent)}%"
            }
            Text(
                text = distLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    isClose -> GoldenPocketGold
                    isAbove -> BullGreen
                    else -> BearRed
                }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = TextMuted,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

// ==========================================
// SMC CARDS & SUB-COMPONENTS
// ==========================================

@Composable
private fun OrderBlockDetailCard(
    orderBlock: OrderBlock,
    currentPrice: Double,
    isRomanUrdu: Boolean
) {
    val isBullish = orderBlock.isBullish
    val color = if (isBullish) BullGreen else BearRed
    val typeTitle = if (isBullish) {
        if (isRomanUrdu) "Demand Zone (Bullish OB)" else "Demand Zone (Bullish OB)"
    } else {
        if (isRomanUrdu) "Supply Zone (Bearish OB)" else "Supply Zone (Bearish OB)"
    }
    val distPercent = if (currentPrice > 0) ((orderBlock.topPrice - currentPrice) / currentPrice) * 100 else 0.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CryptoSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = typeTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (orderBlock.mitigated) TextMuted.copy(alpha = 0.2f) else color.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (orderBlock.mitigated) {
                            if (isRomanUrdu) "Mitigated (Test Shuda)" else "Mitigated"
                        } else {
                            if (isRomanUrdu) "FRESH ZONE" else "FRESH ZONE"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (orderBlock.mitigated) TextMuted else color
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${formatPrice(orderBlock.bottomPrice)} - ${formatPrice(orderBlock.topPrice)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = if (abs(distPercent) < 0.2) {
                        if (isRomanUrdu) "Qeemat Zone me hai!" else "Inside Zone!"
                    } else {
                        "${String.format(Locale.US, "%+.2f", distPercent)}%"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (abs(distPercent) < 0.2) GoldenPocketGold else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun FvgDetailCard(
    fvg: FairValueGap,
    currentPrice: Double,
    isRomanUrdu: Boolean
) {
    val isBullish = fvg.isBullish
    val color = if (isBullish) BullGreen else BearRed
    val title = if (isBullish) {
        if (isRomanUrdu) "Bullish FVG (Dip Imbalance)" else "Bullish FVG (Discount Gap)"
    } else {
        if (isRomanUrdu) "Bearish FVG (Rally Imbalance)" else "Bearish FVG (Premium Gap)"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "${formatPrice(fvg.bottomPrice)} - ${formatPrice(fvg.topPrice)}",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (fvg.mitigated) TextMuted.copy(alpha = 0.2f) else AccentCyan.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (fvg.mitigated) {
                        if (isRomanUrdu) "Filled" else "Filled"
                    } else {
                        if (isRomanUrdu) "Unfilled Magnet" else "Open Magnet"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (fvg.mitigated) TextMuted else AccentCyan
                )
            }
        }
    }
}

@Composable
private fun BosDetailCard(
    bos: BreakOfStructure,
    currentPrice: Double,
    isRomanUrdu: Boolean
) {
    val isBullish = bos.isBullish
    val color = if (isBullish) BullGreen else BearRed

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBullish) {
                        if (isRomanUrdu) "Bullish Break of Structure (BOS)" else "Bullish Break of Structure (BOS)"
                    } else {
                        if (isRomanUrdu) "Bearish Break of Structure (BOS)" else "Bearish Break of Structure (BOS)"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "${if (isRomanUrdu) "Tota Level: $" else "Broken Level: $"}${formatPrice(bos.brokenLevel)}",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (bos.confirmed) (if (isRomanUrdu) "Tasdeeq Shuda" else "Confirmed") else (if (isRomanUrdu) "Zair-e-Ghaur" else "Pending"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun ChochDetailCard(
    choch: ChangeOfCharacter,
    currentPrice: Double,
    isRomanUrdu: Boolean
) {
    val isBullish = choch.isBullish
    val color = if (isBullish) BullGreen else BearRed

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .border(1.dp, GoldenPocketGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBullish) {
                            if (isRomanUrdu) "CHoCH (Bullish Rujhaan Tabdeeli)" else "CHoCH (Bullish Trend Shift)"
                        } else {
                            if (isRomanUrdu) "CHoCH (Bearish Rujhaan Tabdeeli)" else "CHoCH (Bearish Trend Shift)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Text(
                    text = "${if (isRomanUrdu) "Pivot Level: $" else "Pivot Level: $"}${formatPrice(choch.brokenLevel)}",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GoldenPocketGold.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isRomanUrdu) "Tabdeeli Reversal" else "Reversal Shift",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenPocketGold
                )
            }
        }
    }
}

@Composable
private fun LiquidityPoolCard(
    title: String,
    level: Double,
    currentPrice: Double,
    isHigh: Boolean,
    isRomanUrdu: Boolean
) {
    val color = if (isHigh) BullGreen else BearRed
    val diffPercent = if (currentPrice > 0) ((level - currentPrice) / currentPrice) * 100 else 0.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = "$${formatPrice(level)}", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = "${String.format(Locale.US, "%+.2f", diffPercent)}%",
                fontSize = 11.sp,
                color = if (abs(diffPercent) < 0.5) GoldenPocketGold else TextSecondary
            )
        }
    }
}

@Composable
private fun SweepDetailCard(
    sweep: LiquiditySweep,
    currentPrice: Double,
    isRomanUrdu: Boolean
) {
    val isHigh = sweep.isHighSweep
    val color = if (isHigh) BearRed else BullGreen
    val title = if (isHigh) {
        if (isRomanUrdu) "High Sweep (Kharidaaron Ka Trap - Bearish)" else "High Sweep (Buyers Trapped - Bearish)"
    } else {
        if (isRomanUrdu) "Low Sweep (Farokht-kunandgan Trap - Bullish)" else "Low Sweep (Sellers Trapped - Bullish)"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = "${if (isRomanUrdu) "Swept Level: $" else "Swept Level: $"}${formatPrice(sweep.sweptLevel)}", fontSize = 12.sp, color = TextPrimary)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = if (isRomanUrdu) "Trap Sweep" else "Trap Purged", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
private fun EmptyNoticeCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TelemetryStat(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000.0 -> String.format(Locale.US, "%,.2f", price)
        price >= 1.0 -> String.format(Locale.US, "%.3f", price)
        price >= 0.001 -> String.format(Locale.US, "%.5f", price)
        else -> String.format(Locale.US, "%.7f", price)
    }
}

private fun formatVolume(volume: Double): String {
    return when {
        volume >= 1_000_000_000 -> String.format(Locale.US, "%.2fB", volume / 1_000_000_000)
        volume >= 1_000_000 -> String.format(Locale.US, "%.2fM", volume / 1_000_000)
        volume >= 1_000 -> String.format(Locale.US, "%.1fK", volume / 1_000)
        else -> String.format(Locale.US, "%.0f", volume)
    }
}
