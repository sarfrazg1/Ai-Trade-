package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaperTradeEntity
import com.example.domain.calculator.TechnicalIndicatorsCalculator
import com.example.domain.model.Candle
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.SmartMoneyConcepts
import com.example.domain.model.TechnicalIndicators
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.FvgBull
import com.example.ui.theme.GoldenPocketGold
import com.example.ui.theme.OrderBlockBear
import com.example.ui.theme.OrderBlockBull
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CandlestickChart(
    candles: List<Candle>,
    indicators: TechnicalIndicators,
    smc: SmartMoneyConcepts,
    fibonacci: FibonacciAnalysis?,
    showEma: Boolean,
    showBollinger: Boolean,
    showFibonacci: Boolean,
    showOrderBlocks: Boolean,
    showFvg: Boolean,
    showBos: Boolean,
    onToggleOverlay: (String) -> Unit,
    activeTrades: List<PaperTradeEntity> = emptyList(),
    currentLivePrice: Double = 0.0,
    isRomanUrdu: Boolean = false,
    onOpenInstantTrade: (Boolean) -> Unit = {},
    onCloseTrade: (PaperTradeEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()

    val currentActiveTrade = activeTrades.firstOrNull { it.status == "OPEN" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Overlay Toggle FilterChips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OverlayChip(label = "EMA (20/50/200)", selected = showEma) { onToggleOverlay("EMA") }
            OverlayChip(label = "Bollinger (20,2)", selected = showBollinger) { onToggleOverlay("BOLLINGER") }
            OverlayChip(label = "Fibonacci Golden", selected = showFibonacci) { onToggleOverlay("FIBONACCI") }
            OverlayChip(label = "Order Blocks", selected = showOrderBlocks) { onToggleOverlay("ORDER_BLOCKS") }
            OverlayChip(label = "FVG Imbalance", selected = showFvg) { onToggleOverlay("FVG") }
            OverlayChip(label = "BOS / CHoCH", selected = showBos) { onToggleOverlay("BOS") }
        }

        // Selected candle info HUD
        val displayCandle = selectedCandleIndex?.let { candles.getOrNull(it) } ?: candles.lastOrNull()
        if (displayCandle != null) {
            val dateStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(displayCandle.time))
            Surface(
                color = CryptoSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = dateStr, fontSize = 11.sp, color = TextMuted)
                    Text(text = "O: ${formatShortPrice(displayCandle.open)}", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "H: ${formatShortPrice(displayCandle.high)}", fontSize = 11.sp, color = BullGreen)
                    Text(text = "L: ${formatShortPrice(displayCandle.low)}", fontSize = 11.sp, color = BearRed)
                    Text(
                        text = "C: ${formatShortPrice(displayCandle.close)}",
                        fontSize = 11.sp,
                        color = if (displayCandle.isBullish) BullGreen else BearRed
                    )
                }
            }
        }

        // Main Chart Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            if (candles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isRomanUrdu) "Candlestick data load ho raha hai..." else "No candlestick data available",
                        color = TextSecondary
                    )
                }
            } else {
                // Display latest 40 candles for clean mobile view
                val visibleCandles = candles.takeLast(40.coerceAtMost(candles.size))
                var minPrice = visibleCandles.minOf { it.low }
                var maxPrice = visibleCandles.maxOf { it.high }

                // Include active trade levels in viewable chart scale if near
                if (currentActiveTrade != null) {
                    minPrice = minOf(minPrice, currentActiveTrade.stopLoss, currentActiveTrade.entryPrice)
                    maxPrice = maxOf(maxPrice, currentActiveTrade.tp1, currentActiveTrade.entryPrice)
                }

                val pricePadding = (maxPrice - minPrice) * 0.08
                val chartMin = (minPrice - pricePadding).coerceAtLeast(0.0)
                val chartMax = maxPrice + pricePadding
                val priceRange = if (chartMax > chartMin) chartMax - chartMin else 1.0

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(visibleCandles) {
                            detectTapGestures { offset ->
                                val candleWidth = size.width / visibleCandles.size
                                val index = (offset.x / candleWidth).toInt().coerceIn(0, visibleCandles.size - 1)
                                val originalIndex = candles.size - visibleCandles.size + index
                                selectedCandleIndex = originalIndex
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val candleCount = visibleCandles.size
                    val slotWidth = canvasWidth / candleCount
                    val candleBodyWidth = (slotWidth * 0.7f).coerceIn(3f, 22f)

                    // 1. Draw Grid Lines and Price Axis
                    drawChartGrid(
                        chartMin = chartMin,
                        chartMax = chartMax,
                        textMeasurer = textMeasurer,
                        width = canvasWidth,
                        height = canvasHeight
                    )

                    // 2. Draw Fibonacci Overlays
                    if (showFibonacci && fibonacci != null) {
                        drawFibonacciOverlay(fibonacci, chartMin, chartMax, canvasWidth, canvasHeight, textMeasurer)
                    }

                    // 3. Draw SMC Overlays: Order Blocks & FVGs
                    if (showOrderBlocks) {
                        drawOrderBlocks(smc, visibleCandles, candles.size, slotWidth, chartMin, chartMax, canvasHeight, canvasWidth)
                    }

                    if (showFvg) {
                        drawFvgZones(smc, visibleCandles, candles.size, slotWidth, chartMin, chartMax, canvasHeight, canvasWidth)
                    }

                    // 4. Draw Candlesticks
                    for (i in visibleCandles.indices) {
                        val c = visibleCandles[i]
                        val x = i * slotWidth + slotWidth / 2f

                        val yHigh = (canvasHeight * (1.0 - (c.high - chartMin) / priceRange)).toFloat()
                        val yLow = (canvasHeight * (1.0 - (c.low - chartMin) / priceRange)).toFloat()
                        val yOpen = (canvasHeight * (1.0 - (c.open - chartMin) / priceRange)).toFloat()
                        val yClose = (canvasHeight * (1.0 - (c.close - chartMin) / priceRange)).toFloat()

                        val candleColor = if (c.isBullish) CandleGreen else CandleRed

                        // Draw Wick
                        drawLine(
                            color = candleColor,
                            start = Offset(x, yHigh),
                            end = Offset(x, yLow),
                            strokeWidth = 1.5f
                        )

                        // Draw Body
                        val bodyTop = minOf(yOpen, yClose)
                        val bodyHeight = maxOf(kotlin.math.abs(yClose - yOpen), 2f)

                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - candleBodyWidth / 2f, bodyTop),
                            size = Size(candleBodyWidth, bodyHeight),
                            style = Fill
                        )
                    }

                    // 5. Draw Technical Indicator Overlays: EMAs & Bollinger Bands
                    if (showEma) {
                        drawEmaLine(candles, visibleCandles, 20, chartMin, priceRange, slotWidth, canvasHeight, AccentCyan)
                        drawEmaLine(candles, visibleCandles, 50, chartMin, priceRange, slotWidth, canvasHeight, WarningAmber)
                    }

                    if (showBollinger) {
                        drawBollingerOverlay(candles, visibleCandles, chartMin, priceRange, slotWidth, canvasHeight)
                    }

                    // 6. Draw BOS / CHoCH Lines
                    if (showBos) {
                        drawBosLines(smc, visibleCandles, candles.size, slotWidth, chartMin, priceRange, canvasHeight, textMeasurer)
                    }

                    // 7. Interactive Paper Trade Lines (Gate.io / Binance Futures Style)
                    if (currentActiveTrade != null) {
                        drawPaperTradeChartOverlay(
                            trade = currentActiveTrade,
                            chartMin = chartMin,
                            chartMax = chartMax,
                            priceRange = priceRange,
                            canvasWidth = canvasWidth,
                            canvasHeight = canvasHeight,
                            textMeasurer = textMeasurer,
                            isRomanUrdu = isRomanUrdu
                        )
                    }
                }
            }
        }

        // Bottom Gate.io / Binance Futures Style Position & Quick-Action HUD
        if (currentActiveTrade != null) {
            ActivePositionBar(
                trade = currentActiveTrade,
                currentPrice = if (currentLivePrice > 0) currentLivePrice else displayCandle?.close ?: currentActiveTrade.entryPrice,
                isRomanUrdu = isRomanUrdu,
                onCloseTrade = { onCloseTrade(currentActiveTrade) }
            )
        } else {
            InstantTradeActionBar(
                currentPrice = if (currentLivePrice > 0) currentLivePrice else displayCandle?.close ?: 0.0,
                isRomanUrdu = isRomanUrdu,
                onOpenLong = { onOpenInstantTrade(true) },
                onOpenShort = { onOpenInstantTrade(false) }
            )
        }
    }
}

@Composable
private fun ActivePositionBar(
    trade: PaperTradeEntity,
    currentPrice: Double,
    isRomanUrdu: Boolean,
    onCloseTrade: () -> Unit
) {
    val isLong = trade.signalType == "LONG"
    val diff = if (isLong) (currentPrice - trade.entryPrice) else (trade.entryPrice - currentPrice)
    val pnlUsd = if (trade.entryPrice > 0) (diff / trade.entryPrice) * (trade.margin * trade.leverage) else 0.0
    val pnlPct = if (trade.entryPrice > 0) (diff / trade.entryPrice) * trade.leverage * 100.0 else 0.0
    val isPos = pnlUsd >= 0

    Surface(
        color = CryptoSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isLong) BullGreen.copy(alpha = 0.5f) else BearRed.copy(alpha = 0.5f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Position badge & Symbol
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isLong) BullGreen else BearRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${trade.signalType} ${trade.leverage}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trade.symbol,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Close Button
                Button(
                    onClick = onCloseTrade,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BearRed.copy(alpha = 0.2f),
                        contentColor = BearRed
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp).testTag("close_paper_trade_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRomanUrdu) "Market Close" else "Market Close",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Middle: Metrics Row (Entry, Mark, Live PnL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isRomanUrdu) "Dakhla (Entry)" else "Entry Price",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "$${formatShortPrice(trade.entryPrice)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRomanUrdu) "Live Qeemat" else "Mark Price",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "$${formatShortPrice(currentPrice)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isRomanUrdu) "Nafa/Nuqsan (PnL)" else "Unrealized PnL",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${if (isPos) "+" else ""}$${String.format("%.2f", pnlUsd)} (${String.format("%.2f", pnlPct)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPos) BullGreen else BearRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // SL & TP Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SL: $${formatShortPrice(trade.stopLoss)}",
                    fontSize = 10.sp,
                    color = BearRed
                )
                Text(
                    text = "TP1: $${formatShortPrice(trade.tp1)}",
                    fontSize = 10.sp,
                    color = BullGreen
                )
                Text(
                    text = "Margin: $${String.format("%.1f", trade.margin)}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun InstantTradeActionBar(
    currentPrice: Double,
    isRomanUrdu: Boolean,
    onOpenLong: () -> Unit,
    onOpenShort: () -> Unit
) {
    Surface(
        color = CryptoSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CryptoCardBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRomanUrdu) "Live Chart Paper Trade (5x Lev • 2% Risk)" else "Instant Paper Trade (5x Lev • 2% Risk)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = if (currentPrice > 0) "$${formatShortPrice(currentPrice)}" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenLong,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BullGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("instant_long_button")
                ) {
                    Text(
                        text = if (isRomanUrdu) "▲ BUY / LONG (Kharidari)" else "▲ BUY / LONG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onOpenShort,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BearRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("instant_short_button")
                ) {
                    Text(
                        text = if (isRomanUrdu) "▼ SELL / SHORT (Farokht)" else "▼ SELL / SHORT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPaperTradeChartOverlay(
    trade: PaperTradeEntity,
    chartMin: Double,
    chartMax: Double,
    priceRange: Double,
    canvasWidth: Float,
    canvasHeight: Float,
    textMeasurer: TextMeasurer,
    isRomanUrdu: Boolean
) {
    fun yOf(price: Double): Float = (canvasHeight * (1.0 - (price - chartMin) / priceRange)).toFloat()

    val isLong = trade.signalType == "LONG"

    // 1. Draw Entry Price Line (Cyan solid with glow effect)
    if (trade.entryPrice in chartMin..chartMax) {
        val yEntry = yOf(trade.entryPrice)
        drawLine(
            color = AccentCyan,
            start = Offset(0f, yEntry),
            end = Offset(canvasWidth, yEntry),
            strokeWidth = 2.2f
        )

        val entryLabel = if (isRomanUrdu) {
            "ENTRY [${trade.signalType} ${trade.leverage}x] $${formatShortPrice(trade.entryPrice)}"
        } else {
            "ENTRY [${trade.signalType} ${trade.leverage}x] $${formatShortPrice(trade.entryPrice)}"
        }
        val layout = textMeasurer.measure(
            text = entryLabel,
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        // Background badge
        drawRect(
            color = AccentCyan,
            topLeft = Offset(8f, yEntry - layout.size.height - 2f),
            size = Size(layout.size.width + 12f, layout.size.height + 4f)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(14f, yEntry - layout.size.height)
        )
    }

    // 2. Draw Take Profit Line (Green dashed)
    if (trade.tp1 in chartMin..chartMax) {
        val yTp = yOf(trade.tp1)
        drawLine(
            color = BullGreen,
            start = Offset(0f, yTp),
            end = Offset(canvasWidth, yTp),
            strokeWidth = 1.8f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )

        val tpLabel = if (isRomanUrdu) "TP1 (Nafa): $${formatShortPrice(trade.tp1)}" else "TP1 (Target): $${formatShortPrice(trade.tp1)}"
        val layout = textMeasurer.measure(
            text = tpLabel,
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        drawRect(
            color = BullGreen,
            topLeft = Offset(8f, yTp - layout.size.height - 2f),
            size = Size(layout.size.width + 12f, layout.size.height + 4f)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(14f, yTp - layout.size.height)
        )
    }

    // 3. Draw Stop Loss Line (Red dashed)
    if (trade.stopLoss in chartMin..chartMax) {
        val ySl = yOf(trade.stopLoss)
        drawLine(
            color = BearRed,
            start = Offset(0f, ySl),
            end = Offset(canvasWidth, ySl),
            strokeWidth = 1.8f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )

        val slLabel = if (isRomanUrdu) "SL (Hifazat): $${formatShortPrice(trade.stopLoss)}" else "SL (Stop Loss): $${formatShortPrice(trade.stopLoss)}"
        val layout = textMeasurer.measure(
            text = slLabel,
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        )
        drawRect(
            color = BearRed,
            topLeft = Offset(8f, ySl - layout.size.height - 2f),
            size = Size(layout.size.width + 12f, layout.size.height + 4f)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(14f, ySl - layout.size.height)
        )
    }
}

@Composable
private fun OverlayChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp, color = if (selected) Color.Black else TextSecondary) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BullGreen,
            containerColor = CryptoSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = CryptoCardBorder,
            selectedBorderColor = BullGreen
        )
    )
}

private fun DrawScope.drawChartGrid(
    chartMin: Double,
    chartMax: Double,
    textMeasurer: TextMeasurer,
    width: Float,
    height: Float
) {
    val steps = 4
    val priceStep = (chartMax - chartMin) / steps

    for (i in 0..steps) {
        val y = height * (i.toFloat() / steps)
        val price = chartMax - i * priceStep

        drawLine(
            color = CryptoCardBorder.copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 0.8f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        )

        // Draw Price Label on right
        val textLayoutResult = textMeasurer.measure(
            text = formatShortPrice(price),
            style = TextStyle(fontSize = 9.sp, color = TextMuted)
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(width - textLayoutResult.size.width - 6f, y - textLayoutResult.size.height / 2f)
        )
    }
}

private fun DrawScope.drawFibonacciOverlay(
    fibo: FibonacciAnalysis,
    chartMin: Double,
    chartMax: Double,
    width: Float,
    height: Float,
    textMeasurer: TextMeasurer
) {
    val range = chartMax - chartMin
    if (range <= 0) return

    fun yOf(price: Double): Float = (height * (1.0 - (price - chartMin) / range)).toFloat()

    val levels = listOf(
        Pair("100% (${formatShortPrice(fibo.swingHigh)})", fibo.swingHigh),
        Pair("78.6% (${formatShortPrice(fibo.retracement786)})", fibo.retracement786),
        Pair("61.8% Golden (${formatShortPrice(fibo.retracement618)})", fibo.retracement618),
        Pair("50% (${formatShortPrice(fibo.retracement500)})", fibo.retracement500),
        Pair("38.2% (${formatShortPrice(fibo.retracement382)})", fibo.retracement382),
        Pair("0% (${formatShortPrice(fibo.swingLow)})", fibo.swingLow)
    )

    // Golden pocket zone highlight
    val gpTopY = yOf(fibo.goldenPocketMax)
    val gpBottomY = yOf(fibo.goldenPocketMin)
    drawRect(
        color = GoldenPocketGold.copy(alpha = 0.12f),
        topLeft = Offset(0f, minOf(gpTopY, gpBottomY)),
        size = Size(width, kotlin.math.abs(gpBottomY - gpTopY))
    )

    for ((label, price) in levels) {
        if (price in chartMin..chartMax) {
            val y = yOf(price)
            val isGolden = label.contains("61.8")
            drawLine(
                color = if (isGolden) GoldenPocketGold else TextMuted.copy(alpha = 0.6f),
                start = Offset(0f, y),
                end = Offset(width - 60f, y),
                strokeWidth = if (isGolden) 1.5f else 0.8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            )
            val layout = textMeasurer.measure(
                text = label,
                style = TextStyle(fontSize = 8.sp, color = if (isGolden) GoldenPocketGold else TextMuted)
            )
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(8f, y - layout.size.height)
            )
        }
    }
}

private fun DrawScope.drawOrderBlocks(
    smc: SmartMoneyConcepts,
    visibleCandles: List<Candle>,
    totalCandles: Int,
    slotWidth: Float,
    chartMin: Double,
    chartMax: Double,
    canvasHeight: Float,
    canvasWidth: Float
) {
    val range = chartMax - chartMin
    if (range <= 0) return
    val startIndex = totalCandles - visibleCandles.size

    for (ob in smc.orderBlocks) {
        if (!ob.mitigated && (ob.topPrice in chartMin..chartMax || ob.bottomPrice in chartMin..chartMax)) {
            val yTop = (canvasHeight * (1.0 - (ob.topPrice - chartMin) / range)).toFloat()
            val yBottom = (canvasHeight * (1.0 - (ob.bottomPrice - chartMin) / range)).toFloat()
            val obLocalIndex = (ob.candleIndex - startIndex).coerceAtLeast(0)
            val left = obLocalIndex * slotWidth
            val h = maxOf(kotlin.math.abs(yBottom - yTop), 3f)

            drawRect(
                color = if (ob.isBullish) OrderBlockBull else OrderBlockBear,
                topLeft = Offset(left, minOf(yTop, yBottom)),
                size = Size(canvasWidth - left, h)
            )
        }
    }
}

private fun DrawScope.drawFvgZones(
    smc: SmartMoneyConcepts,
    visibleCandles: List<Candle>,
    totalCandles: Int,
    slotWidth: Float,
    chartMin: Double,
    chartMax: Double,
    canvasHeight: Float,
    canvasWidth: Float
) {
    val range = chartMax - chartMin
    if (range <= 0) return
    val startIndex = totalCandles - visibleCandles.size

    for (fvg in smc.fvgList.takeLast(4)) {
        if (!fvg.mitigated && fvg.topPrice in chartMin..chartMax) {
            val yTop = (canvasHeight * (1.0 - (fvg.topPrice - chartMin) / range)).toFloat()
            val yBottom = (canvasHeight * (1.0 - (fvg.bottomPrice - chartMin) / range)).toFloat()
            val localIdx = (fvg.candleIndex - startIndex).coerceAtLeast(0)
            val left = localIdx * slotWidth
            val h = maxOf(kotlin.math.abs(yBottom - yTop), 2f)

            drawRect(
                color = if (fvg.isBullish) FvgBull else Color(0x26FF9100),
                topLeft = Offset(left, minOf(yTop, yBottom)),
                size = Size(canvasWidth - left, h)
            )
        }
    }
}

private fun DrawScope.drawEmaLine(
    allCandles: List<Candle>,
    visibleCandles: List<Candle>,
    period: Int,
    chartMin: Double,
    priceRange: Double,
    slotWidth: Float,
    canvasHeight: Float,
    color: Color
) {
    val closes = allCandles.map { it.close }
    val emaSeries = TechnicalIndicatorsCalculator.calculateEmaSeries(closes, period)
    if (emaSeries.isEmpty()) return

    val startIndex = allCandles.size - visibleCandles.size
    val path = Path()
    var started = false

    for (i in visibleCandles.indices) {
        val fullIdx = startIndex + i
        val emaVal = emaSeries.getOrNull(fullIdx - period + 1) ?: continue
        val x = i * slotWidth + slotWidth / 2f
        val y = (canvasHeight * (1.0 - (emaVal - chartMin) / priceRange)).toFloat()

        if (!started) {
            path.moveTo(x, y)
            started = true
        } else {
            path.lineTo(x, y)
        }
    }

    if (started) {
        drawPath(path = path, color = color, style = Stroke(width = 1.8f))
    }
}

private fun DrawScope.drawBollingerOverlay(
    allCandles: List<Candle>,
    visibleCandles: List<Candle>,
    chartMin: Double,
    priceRange: Double,
    slotWidth: Float,
    canvasHeight: Float
) {
    val closes = allCandles.map { it.close }
    val startIndex = allCandles.size - visibleCandles.size
    val upperPath = Path()
    val lowerPath = Path()
    var started = false

    for (i in visibleCandles.indices) {
        val fullIdx = startIndex + i
        if (fullIdx < 20) continue
        val window = closes.subList(0, fullIdx + 1)
        val bb = TechnicalIndicatorsCalculator.calculateBollingerBands(window, 20, 2.0) ?: continue

        val x = i * slotWidth + slotWidth / 2f
        val yUpper = (canvasHeight * (1.0 - (bb.first - chartMin) / priceRange)).toFloat()
        val yLower = (canvasHeight * (1.0 - (bb.third - chartMin) / priceRange)).toFloat()

        if (!started) {
            upperPath.moveTo(x, yUpper)
            lowerPath.moveTo(x, yLower)
            started = true
        } else {
            upperPath.lineTo(x, yUpper)
            lowerPath.lineTo(x, yLower)
        }
    }

    if (started) {
        drawPath(path = upperPath, color = AccentPurple.copy(alpha = 0.7f), style = Stroke(width = 1.2f))
        drawPath(path = lowerPath, color = AccentPurple.copy(alpha = 0.7f), style = Stroke(width = 1.2f))
    }
}

private fun DrawScope.drawBosLines(
    smc: SmartMoneyConcepts,
    visibleCandles: List<Candle>,
    totalCandles: Int,
    slotWidth: Float,
    chartMin: Double,
    priceRange: Double,
    canvasHeight: Float,
    textMeasurer: TextMeasurer
) {
    val startIndex = totalCandles - visibleCandles.size
    for (bos in smc.bosList.takeLast(3)) {
        val localIdx = bos.candleIndex - startIndex
        if (localIdx in visibleCandles.indices) {
            val x = localIdx * slotWidth + slotWidth / 2f
            val y = (canvasHeight * (1.0 - (bos.brokenLevel - chartMin) / priceRange)).toFloat()
            val color = if (bos.isBullish) BullGreen else BearRed

            drawLine(
                color = color,
                start = Offset(x - 20f, y),
                end = Offset(x + 50f, y),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
            )

            val tag = if (bos.isBullish) "BOS ▲" else "BOS ▼"
            val layout = textMeasurer.measure(
                text = tag,
                style = TextStyle(fontSize = 8.sp, color = color)
            )
            drawText(textLayoutResult = layout, topLeft = Offset(x, y - layout.size.height))
        }
    }
}

private fun formatShortPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("%,.1f", p)
        p >= 1.0 -> String.format("%.2f", p)
        else -> String.format("%.4f", p)
    }
}
