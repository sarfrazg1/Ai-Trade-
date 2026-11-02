package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaperTradeEntity
import com.example.domain.calculator.TechnicalIndicatorsCalculator
import com.example.domain.model.Candle
import com.example.domain.model.FibonacciAnalysis
import com.example.domain.model.FuturesStats
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
    stats: FuturesStats? = null,
    symbol: String = "BNBUSDT",
    isRomanUrdu: Boolean = false,
    onOpenInstantTrade: (Boolean) -> Unit = {},
    onCloseTrade: (PaperTradeEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var visibleCount by remember { mutableStateOf(28) }
    var panOffset by remember { mutableStateOf(0) }
    var isCrosshairActive by remember { mutableStateOf(false) }
    var crosshairOffset by remember { mutableStateOf<Offset?>(null) }
    var isTradeBarExpanded by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    val currentActiveTrade = activeTrades.firstOrNull { it.status == "OPEN" }

    // Task 1: Top Info Header Metrics
    val effectiveLivePrice = if (currentLivePrice > 0) currentLivePrice else stats?.lastPrice ?: candles.lastOrNull()?.close ?: 0.0
    val baseAsset = symbol.removeSuffix("USDT").ifEmpty { "BNB" }
    val changePct = stats?.priceChange24h ?: run {
        val prevClose = candles.dropLast(1).lastOrNull()?.close ?: effectiveLivePrice
        if (prevClose > 0) ((effectiveLivePrice - prevClose) / prevClose) * 100.0 else 0.0
    }
    val isPriceUp = changePct >= 0
    val markPrice = stats?.markPrice ?: (effectiveLivePrice * 0.9998)
    val spotPrice = stats?.indexPrice ?: (effectiveLivePrice * 1.0001)
    val high24h = stats?.high24h ?: (candles.maxOfOrNull { it.high } ?: effectiveLivePrice)
    val low24h = stats?.low24h ?: (candles.minOfOrNull { it.low } ?: effectiveLivePrice)
    val vol24h = stats?.volume24h ?: candles.sumOf { it.volume }
    val turnover24h = vol24h * effectiveLivePrice

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // TASK 1: Professional Top Info Header (Exchange Style)
        Surface(
            color = Color(0xFF090C12),
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(0.6.dp, CryptoCardBorder))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Big Bold Last Price + Mark, Spot & %
                Column(modifier = Modifier.weight(1.15f)) {
                    Text(
                        text = formatHeaderPrice(effectiveLivePrice),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isPriceUp) BullGreen else BearRed,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Mark ${formatShortPrice(markPrice)}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Spot ${baseAsset}/USDT ${formatShortPrice(spotPrice)}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${if (isPriceUp) "+" else ""}${String.format(Locale.US, "%.2f", changePct)}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPriceUp) BullGreen else BearRed
                        )
                    }
                }

                // Right Column: 4 compact rows: 24h High, 24h Low, 24h Vol, 24h Turnover
                Column(
                    modifier = Modifier.weight(0.95f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(1.5.dp)
                ) {
                    HeaderStatRow("24h High", formatShortPrice(high24h))
                    HeaderStatRow("24h Low", formatShortPrice(low24h))
                    HeaderStatRow("24h Vol ($baseAsset)", formatCompactNumber(vol24h))
                    HeaderStatRow("24h Turnover (USDT)", formatCompactNumber(turnover24h))
                }
            }
        }

        // Ultra-compact sleek overlay controls & candle density toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Overlay indicator toggle chips (compact 24dp)
            OverlayChip(label = "EMA (20/50)", selected = showEma) { onToggleOverlay("EMA") }
            OverlayChip(label = "Bollinger (20,2)", selected = showBollinger) { onToggleOverlay("BOLLINGER") }
            OverlayChip(label = "Fibonacci", selected = showFibonacci) { onToggleOverlay("FIBONACCI") }
            OverlayChip(label = "Order Blocks", selected = showOrderBlocks) { onToggleOverlay("ORDER_BLOCKS") }
            OverlayChip(label = "FVG", selected = showFvg) { onToggleOverlay("FVG") }
            OverlayChip(label = "BOS / CHoCH", selected = showBos) { onToggleOverlay("BOS") }

            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(14.dp)
                    .background(CryptoCardBorder)
            )
            Spacer(modifier = Modifier.width(3.dp))

            // Candle Density / Zoom Preset Selector (20C, 28C, 40C)
            listOf(20, 28, 40).forEach { count ->
                val isSel = visibleCount == count
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSel) BullGreen.copy(alpha = 0.25f) else CryptoSurfaceVariant)
                        .clickable { visibleCount = count }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${count}C",
                        fontSize = 10.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) BullGreen else TextSecondary
                    )
                }
            }
        }

        // Main Chart Canvas with Floating HUD inside BoxWithConstraints (0dp layout displacement)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            if (candles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isRomanUrdu) "Candlestick data load ho raha hai..." else "No candlestick data available",
                        color = TextSecondary
                    )
                }
            } else {
                // TASK 3: Zoom and Pan slice computation
                val totalCandles = candles.size
                val maxPan = (totalCandles - visibleCount).coerceAtLeast(0)
                val effectivePan = panOffset.coerceIn(0, maxPan)
                val endIndex = (totalCandles - effectivePan).coerceIn(visibleCount.coerceAtMost(totalCandles), totalCandles)
                val startIndex = (endIndex - visibleCount).coerceAtLeast(0)
                val visibleCandles = candles.subList(startIndex, endIndex)

                var minPrice = visibleCandles.minOfOrNull { it.low } ?: 0.0
                var maxPrice = visibleCandles.maxOfOrNull { it.high } ?: 1.0

                if (effectiveLivePrice > 0) {
                    minPrice = minOf(minPrice, effectiveLivePrice)
                    maxPrice = maxOf(maxPrice, effectiveLivePrice)
                }

                if (currentActiveTrade != null) {
                    val tradeLevels = listOfNotNull(
                        currentActiveTrade.stopLoss.takeIf { it > 0 },
                        currentActiveTrade.entryPrice.takeIf { it > 0 },
                        currentActiveTrade.tp1.takeIf { it > 0 },
                        currentActiveTrade.tp2.takeIf { it > 0 },
                        currentActiveTrade.tp3.takeIf { it > 0 }
                    )
                    if (tradeLevels.isNotEmpty()) {
                        minPrice = minOf(minPrice, tradeLevels.minOrNull() ?: minPrice)
                        maxPrice = maxOf(maxPrice, tradeLevels.maxOrNull() ?: maxPrice)
                    }
                }

                val pricePadding = (maxPrice - minPrice) * 0.10
                val chartMin = (minPrice - pricePadding).coerceAtLeast(0.0)
                val chartMax = maxPrice + pricePadding
                val priceRange = if (chartMax > chartMin) chartMax - chartMin else 1.0

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(candles.size) {
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val downTime = System.currentTimeMillis()
                                    val downPos = down.position
                                    var isLongPressTriggered = false
                                    var isTransforming = false
                                    var zoomAcc = 1f
                                    var panAcc = 0f

                                    do {
                                        val event = awaitPointerEvent()
                                        val pointers = event.changes
                                        val pointerCount = pointers.size

                                        if (pointerCount >= 2) {
                                            // Two fingers: Pinch to zoom in / zoom out
                                            isTransforming = true
                                            isLongPressTriggered = false
                                            isCrosshairActive = false

                                            val p1Prev = pointers[0].previousPosition
                                            val p2Prev = pointers[1].previousPosition
                                            val p1Curr = pointers[0].position
                                            val p2Curr = pointers[1].position

                                            val prevSpan = (p1Prev - p2Prev).getDistance()
                                            val currSpan = (p1Curr - p2Curr).getDistance()

                                            if (prevSpan > 10f && currSpan > 10f) {
                                                val factor = currSpan / prevSpan
                                                zoomAcc *= factor
                                                if (zoomAcc > 1.10f) {
                                                    visibleCount = (visibleCount - 3).coerceAtLeast(8)
                                                    zoomAcc = 1f
                                                } else if (zoomAcc < 0.90f) {
                                                    visibleCount = (visibleCount + 3).coerceAtMost(candles.size.coerceAtLeast(8))
                                                    zoomAcc = 1f
                                                }
                                            }
                                            pointers.forEach { it.consume() }
                                        } else if (pointerCount == 1) {
                                            val p = pointers.first()
                                            val elapsed = System.currentTimeMillis() - downTime
                                            val moveDist = (p.position - downPos).getDistance()

                                            if (!isTransforming && elapsed > 350 && moveDist < 25f) {
                                                // Long press detected -> activate crosshair
                                                isLongPressTriggered = true
                                                isCrosshairActive = true
                                                crosshairOffset = p.position
                                                p.consume()
                                            } else if (isLongPressTriggered) {
                                                // Dragging crosshair
                                                crosshairOffset = p.position
                                                p.consume()
                                            } else if (moveDist > 12f) {
                                                // Single finger dragging -> Pan left/right
                                                isTransforming = true
                                                val dx = p.position.x - p.previousPosition.x
                                                panAcc += dx
                                                val slotW = size.width / visibleCandles.size.coerceAtLeast(1)
                                                if (slotW > 0f) {
                                                    val candleShift = (panAcc / slotW).toInt()
                                                    if (candleShift != 0) {
                                                        val maxP = (candles.size - visibleCount).coerceAtLeast(0)
                                                        panOffset = (panOffset + candleShift).coerceIn(0, maxP)
                                                        panAcc -= candleShift * slotW
                                                    }
                                                }
                                                p.consume()
                                            }
                                        }
                                    } while (pointers.any { it.pressed })

                                    // Tap handling
                                    val totalDuration = System.currentTimeMillis() - downTime
                                    val totalMove = (downPos - (crosshairOffset ?: downPos)).getDistance()
                                    if (!isTransforming && !isLongPressTriggered && totalDuration < 300 && totalMove < 20f) {
                                        if (isCrosshairActive) {
                                            isCrosshairActive = false
                                            crosshairOffset = null
                                        } else {
                                            isCrosshairActive = true
                                            crosshairOffset = downPos
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val slotWidth = canvasWidth / visibleCandles.size.coerceAtLeast(1)
                    val candleBodyWidth = (slotWidth * 0.72f).coerceIn(4f, 26f)

                    // 1. Draw Grid Lines and Price Axis
                    drawChartGrid(
                        chartMin = chartMin,
                        chartMax = chartMax,
                        textMeasurer = textMeasurer,
                        width = canvasWidth,
                        height = canvasHeight
                    )

                    // 2. Draw Fibonacci Overlays with 61.8% Golden Zone
                    if (showFibonacci && fibonacci != null) {
                        drawFibonacciOverlay(fibonacci, chartMin, chartMax, canvasWidth, canvasHeight, textMeasurer)
                    }

                    // 3. Draw SMC Overlays: Order Blocks & FVGs
                    if (showOrderBlocks) {
                        drawOrderBlocks(smc, visibleCandles, startIndex, slotWidth, chartMin, chartMax, canvasHeight, canvasWidth)
                    }

                    if (showFvg) {
                        drawFvgZones(smc, visibleCandles, startIndex, slotWidth, chartMin, chartMax, canvasHeight, canvasWidth)
                    }

                    // 4. Draw Volume Bars at bottom 14% of canvas
                    val maxVol = visibleCandles.maxOfOrNull { it.volume }?.coerceAtLeast(1.0) ?: 1.0
                    val volMaxH = canvasHeight * 0.14f
                    for (i in visibleCandles.indices) {
                        val c = visibleCandles[i]
                        val x = i * slotWidth + slotWidth / 2f
                        val vH = (c.volume / maxVol * volMaxH).toFloat().coerceIn(2f, volMaxH)
                        drawRect(
                            color = (if (c.isBullish) BullGreen else BearRed).copy(alpha = 0.20f),
                            topLeft = Offset(x - candleBodyWidth / 2f, canvasHeight - vH),
                            size = Size(candleBodyWidth, vH)
                        )
                    }

                    // 5. Draw Candlesticks with prominent bodies and crisp wicks
                    for (i in visibleCandles.indices) {
                        val c = visibleCandles[i]
                        val x = i * slotWidth + slotWidth / 2f

                        val yHigh = (canvasHeight * (1.0 - (c.high - chartMin) / priceRange)).toFloat()
                        val yLow = (canvasHeight * (1.0 - (c.low - chartMin) / priceRange)).toFloat()
                        val yOpen = (canvasHeight * (1.0 - (c.open - chartMin) / priceRange)).toFloat()
                        val yClose = (canvasHeight * (1.0 - (c.close - chartMin) / priceRange)).toFloat()

                        val candleColor = if (c.isBullish) BullGreen else BearRed

                        // Draw Wick (2.0f stroke)
                        drawLine(
                            color = candleColor,
                            start = Offset(x, yHigh),
                            end = Offset(x, yLow),
                            strokeWidth = 2.0f
                        )

                        // Draw Body
                        val bodyTop = minOf(yOpen, yClose)
                        val bodyHeight = maxOf(kotlin.math.abs(yClose - yOpen), 2.5f)

                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - candleBodyWidth / 2f, bodyTop),
                            size = Size(candleBodyWidth, bodyHeight),
                            style = Fill
                        )
                    }

                    // 6. Draw Technical Indicator Overlays: EMAs & Bollinger Bands
                    if (showEma) {
                        drawEmaLine(candles, visibleCandles, startIndex, 20, chartMin, priceRange, slotWidth, canvasHeight, AccentCyan)
                        drawEmaLine(candles, visibleCandles, startIndex, 50, chartMin, priceRange, slotWidth, canvasHeight, WarningAmber)
                    }

                    if (showBollinger) {
                        drawBollingerOverlay(candles, visibleCandles, startIndex, chartMin, priceRange, slotWidth, canvasHeight)
                    }

                    // 7. Draw BOS / CHoCH Lines
                    if (showBos) {
                        drawBosLines(smc, visibleCandles, startIndex, slotWidth, chartMin, priceRange, canvasHeight, textMeasurer)
                    }

                    // 8. TASK 2: Current Price Floating Label on the right edge (always visible, moving up/down)
                    if (effectiveLivePrice > 0.0) {
                        val yLive = (canvasHeight * (1.0 - (effectiveLivePrice - chartMin) / priceRange)).toFloat().coerceIn(14f, canvasHeight - 14f)
                        val isPriceBullish = effectiveLivePrice >= (visibleCandles.dropLast(1).lastOrNull()?.close ?: effectiveLivePrice)
                        val priceColor = if (isPriceBullish) BullGreen else BearRed

                        // Horizontal dashed line across the chart
                        drawLine(
                            color = priceColor.copy(alpha = 0.85f),
                            start = Offset(0f, yLive),
                            end = Offset(canvasWidth - 62f, yLive),
                            strokeWidth = 1.6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                        )

                        // Floating Label Box on right edge
                        val liveText = formatShortPrice(effectiveLivePrice)
                        val liveLayout = textMeasurer.measure(
                            text = liveText,
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        )
                        val badgeWidth = liveLayout.size.width + 12f
                        val badgeHeight = liveLayout.size.height + 6f
                        val badgeLeft = canvasWidth - badgeWidth - 2f
                        val badgeTop = yLive - badgeHeight / 2f

                        // Outer border shadow
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.6f),
                            topLeft = Offset(badgeLeft - 1f, badgeTop - 1f),
                            size = Size(badgeWidth + 2f, badgeHeight + 2f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        // Floating badge body
                        drawRoundRect(
                            color = priceColor,
                            topLeft = Offset(badgeLeft, badgeTop),
                            size = Size(badgeWidth, badgeHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        // Label text inside badge
                        drawText(
                            textLayoutResult = liveLayout,
                            topLeft = Offset(badgeLeft + 6f, badgeTop + 3f)
                        )
                    }

                    // 9. Crosshair Lines on Long Press / Tap
                    if (isCrosshairActive && crosshairOffset != null && visibleCandles.isNotEmpty()) {
                        val activeIndex = ((crosshairOffset!!.x) / slotWidth).toInt().coerceIn(0, visibleCandles.size - 1)
                        val selC = visibleCandles[activeIndex]
                        val xCross = activeIndex * slotWidth + slotWidth / 2f
                        val yCross = crosshairOffset!!.y.coerceIn(0f, canvasHeight)
                        val crosshairPrice = chartMax - (yCross / canvasHeight) * priceRange

                        // Vertical crosshair
                        drawLine(
                            color = AccentCyan.copy(alpha = 0.85f),
                            start = Offset(xCross, 0f),
                            end = Offset(xCross, canvasHeight),
                            strokeWidth = 1.2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))
                        )
                        // Horizontal crosshair
                        drawLine(
                            color = AccentCyan.copy(alpha = 0.85f),
                            start = Offset(0f, yCross),
                            end = Offset(canvasWidth, yCross),
                            strokeWidth = 1.2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))
                        )

                        // Floating price badge at crosshair on right axis
                        val crosshairPriceText = formatShortPrice(crosshairPrice)
                        val priceLayout = textMeasurer.measure(
                            text = crosshairPriceText,
                            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        val cpBadgeW = priceLayout.size.width + 10f
                        val cpBadgeH = priceLayout.size.height + 4f
                        drawRoundRect(
                            color = Color(0xFF2A2E39),
                            topLeft = Offset(canvasWidth - cpBadgeW - 2f, yCross - cpBadgeH / 2f),
                            size = Size(cpBadgeW, cpBadgeH),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                        drawText(
                            textLayoutResult = priceLayout,
                            topLeft = Offset(canvasWidth - cpBadgeW + 3f, yCross - cpBadgeH / 2f + 2f)
                        )

                        // Floating time badge at crosshair on bottom
                        val timeStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(selC.time))
                        val timeLayout = textMeasurer.measure(
                            text = timeStr,
                            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        val ctBadgeW = timeLayout.size.width + 10f
                        val ctBadgeH = timeLayout.size.height + 4f
                        drawRoundRect(
                            color = Color(0xFF2A2E39),
                            topLeft = Offset((xCross - ctBadgeW / 2f).coerceIn(4f, canvasWidth - ctBadgeW - 4f), canvasHeight - ctBadgeH - 2f),
                            size = Size(ctBadgeW, ctBadgeH),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                        drawText(
                            textLayoutResult = timeLayout,
                            topLeft = Offset((xCross - ctBadgeW / 2f).coerceIn(4f, canvasWidth - ctBadgeW - 4f) + 5f, canvasHeight - ctBadgeH)
                        )
                    }

                    // 10. Interactive Paper Trade Lines
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

                // Floating Candle HUD / OHLC Banner (shows inspected candle on long-press or tap, or latest candle)
                val displayCandle = if (isCrosshairActive && crosshairOffset != null && visibleCandles.isNotEmpty()) {
                    val slotW = constraints.maxWidth.toFloat() / visibleCandles.size.coerceAtLeast(1)
                    val idx = ((crosshairOffset!!.x) / slotW).toInt().coerceIn(0, visibleCandles.size - 1)
                    visibleCandles[idx]
                } else {
                    visibleCandles.lastOrNull()
                }

                if (displayCandle != null) {
                    val dateStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(displayCandle.time))
                    val diff = displayCandle.close - displayCandle.open
                    val pct = if (displayCandle.open > 0) (diff / displayCandle.open) * 100.0 else 0.0
                    val isBull = displayCandle.isBullish

                    Surface(
                        color = CryptoSurface.copy(alpha = 0.90f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.6.dp, CryptoCardBorder),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 6.dp, top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = dateStr, fontSize = 10.sp, color = TextMuted)
                            Text(text = "O: ${formatShortPrice(displayCandle.open)}", fontSize = 10.sp, color = TextSecondary)
                            Text(text = "H: ${formatShortPrice(displayCandle.high)}", fontSize = 10.sp, color = BullGreen)
                            Text(text = "L: ${formatShortPrice(displayCandle.low)}", fontSize = 10.sp, color = BearRed)
                            Text(
                                text = "C: ${formatShortPrice(displayCandle.close)} (${if (diff >= 0) "+" else ""}${String.format("%.2f", pct)}%)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBull) BullGreen else BearRed
                            )
                            if (isCrosshairActive) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear crosshair",
                                    tint = TextMuted,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            isCrosshairActive = false
                                            crosshairOffset = null
                                        }
                                )
                            }
                        }
                    }
                }

                // TASK 3: Floating Zoom Buttons (+ and -) and Reset Live Pill
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 56.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (panOffset > 0) {
                        Surface(
                            color = BullGreen.copy(alpha = 0.22f),
                            border = BorderStroke(0.8.dp, BullGreen),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .clickable { panOffset = 0 }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Jump to live candle",
                                    tint = BullGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullGreen
                                )
                            }
                        }
                    }

                    // Zoom Controls Pill (+ and - buttons)
                    Surface(
                        color = CryptoSurface.copy(alpha = 0.90f),
                        border = BorderStroke(0.8.dp, CryptoCardBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            // Zoom In Button (+)
                            IconButton(
                                onClick = {
                                    visibleCount = (visibleCount - 4).coerceAtLeast(8)
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("zoom_in_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Zoom In",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(16.dp)
                                    .background(CryptoCardBorder)
                            )

                            // Zoom Out Button (-)
                            IconButton(
                                onClick = {
                                    visibleCount = (visibleCount + 4).coerceAtMost(candles.size.coerceAtLeast(8))
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("zoom_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Zoom Out",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Gate.io / Binance Futures Style Position & Quick-Action HUD
        if (currentActiveTrade != null) {
            ActivePositionBar(
                trade = currentActiveTrade,
                currentPrice = if (currentLivePrice > 0) currentLivePrice else candles.lastOrNull()?.close ?: currentActiveTrade.entryPrice,
                isRomanUrdu = isRomanUrdu,
                onCloseTrade = { onCloseTrade(currentActiveTrade) }
            )
        } else {
            InstantTradeActionBar(
                currentPrice = if (currentLivePrice > 0) currentLivePrice else candles.lastOrNull()?.close ?: 0.0,
                isRomanUrdu = isRomanUrdu,
                isExpanded = isTradeBarExpanded,
                onToggleExpand = { isTradeBarExpanded = !isTradeBarExpanded },
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
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenLong: () -> Unit,
    onOpenShort: () -> Unit
) {
    Surface(
        color = CryptoSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CryptoCardBorder, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = if (isExpanded) 6.dp else 4.dp)
        ) {
            // Header row with collapse/expand toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRomanUrdu) "Paper Trade (5x Lev)" else "Paper Trade (5x Lev)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (currentPrice > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$${formatShortPrice(currentPrice)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isExpanded) {
                        // Quick mini action buttons in collapsed mode!
                        Button(
                            onClick = onOpenLong,
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp).testTag("mini_long_button")
                        ) {
                            Text("▲ LONG", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = onOpenShort,
                            colors = ButtonDefaults.buttonColors(containerColor = BearRed, contentColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp).testTag("mini_short_button")
                        ) {
                            Text("▼ SHORT", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse trade bar" else "Expand trade bar",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (isExpanded) {
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
                            .height(36.dp)
                            .testTag("instant_long_button")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "▲ BUY / LONG (5x)" else "▲ BUY / LONG (5x)",
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
                            .height(36.dp)
                            .testTag("instant_short_button")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "▼ SELL / SHORT (5x)" else "▼ SELL / SHORT (5x)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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

    // 2. Draw Take Profit Lines (Green dashed)
    if (trade.tp1 in chartMin..chartMax) {
        val yTp = yOf(trade.tp1)
        drawLine(
            color = BullGreen,
            start = Offset(0f, yTp),
            end = Offset(canvasWidth, yTp),
            strokeWidth = 1.8f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )

        val tpLabel = if (isRomanUrdu) "TP1: $${formatShortPrice(trade.tp1)}" else "TP1: $${formatShortPrice(trade.tp1)}"
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

    if (trade.tp2 in chartMin..chartMax) {
        val yTp2 = yOf(trade.tp2)
        drawLine(
            color = BullGreen.copy(alpha = 0.85f),
            start = Offset(0f, yTp2),
            end = Offset(canvasWidth, yTp2),
            strokeWidth = 1.6f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))
        )

        val tp2Label = if (isRomanUrdu) "TP2: $${formatShortPrice(trade.tp2)}" else "TP2: $${formatShortPrice(trade.tp2)}"
        val layout = textMeasurer.measure(
            text = tp2Label,
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        drawRect(
            color = BullGreen.copy(alpha = 0.85f),
            topLeft = Offset(8f, yTp2 - layout.size.height - 2f),
            size = Size(layout.size.width + 12f, layout.size.height + 4f)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(14f, yTp2 - layout.size.height)
        )
    }

    if (trade.tp3 in chartMin..chartMax) {
        val yTp3 = yOf(trade.tp3)
        drawLine(
            color = GoldenPocketGold,
            start = Offset(0f, yTp3),
            end = Offset(canvasWidth, yTp3),
            strokeWidth = 1.6f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
        )

        val tp3Label = if (isRomanUrdu) "TP3 (Max): $${formatShortPrice(trade.tp3)}" else "TP3 (Max): $${formatShortPrice(trade.tp3)}"
        val layout = textMeasurer.measure(
            text = tp3Label,
            style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        drawRect(
            color = GoldenPocketGold,
            topLeft = Offset(8f, yTp3 - layout.size.height - 2f),
            size = Size(layout.size.width + 12f, layout.size.height + 4f)
        )
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(14f, yTp3 - layout.size.height)
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) BullGreen else CryptoSurface)
            .border(0.8.dp, if (selected) BullGreen else CryptoCardBorder, RoundedCornerShape(5.dp))
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else TextSecondary
        )
    }
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

        // Draw Price Label on right with dark background badge
        val textLayoutResult = textMeasurer.measure(
            text = formatShortPrice(price),
            style = TextStyle(fontSize = 9.sp, color = TextMuted)
        )
        drawRoundRect(
            color = CryptoSurface.copy(alpha = 0.85f),
            topLeft = Offset(width - textLayoutResult.size.width - 10f, y - textLayoutResult.size.height / 2f - 1f),
            size = Size(textLayoutResult.size.width + 8f, textLayoutResult.size.height + 2f),
            cornerRadius = CornerRadius(3f, 3f)
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
    startIndex: Int,
    slotWidth: Float,
    chartMin: Double,
    chartMax: Double,
    canvasHeight: Float,
    canvasWidth: Float
) {
    val range = chartMax - chartMin
    if (range <= 0) return

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
    startIndex: Int,
    slotWidth: Float,
    chartMin: Double,
    chartMax: Double,
    canvasHeight: Float,
    canvasWidth: Float
) {
    val range = chartMax - chartMin
    if (range <= 0) return

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
    startIndex: Int,
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
    startIndex: Int,
    chartMin: Double,
    priceRange: Double,
    slotWidth: Float,
    canvasHeight: Float
) {
    val closes = allCandles.map { it.close }
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
    startIndex: Int,
    slotWidth: Float,
    chartMin: Double,
    priceRange: Double,
    canvasHeight: Float,
    textMeasurer: TextMeasurer
) {
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

private fun formatHeaderPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format(Locale.US, "%,.2f", p)
        p >= 1.0 -> String.format(Locale.US, "%.2f", p)
        p >= 0.001 -> String.format(Locale.US, "%.4f", p)
        else -> String.format(Locale.US, "%.8f", p)
    }
}

private fun formatShortPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format(Locale.US, "%,.1f", p)
        p >= 1.0 -> String.format(Locale.US, "%.2f", p)
        p >= 0.001 -> String.format(Locale.US, "%.4f", p)
        else -> String.format(Locale.US, "%.8f", p)
    }
}

@Composable
private fun HeaderStatRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextMuted
        )
        Text(
            text = value,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary
        )
    }
}

private fun formatCompactNumber(number: Double): String {
    return when {
        number >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", number / 1_000_000_000.0)
        number >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", number / 1_000_000.0)
        number >= 1_000.0 -> String.format(Locale.US, "%.2fK", number / 1_000.0)
        else -> String.format(Locale.US, "%.2f", number)
    }
}

