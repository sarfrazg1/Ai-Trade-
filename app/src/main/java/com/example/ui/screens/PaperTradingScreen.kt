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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.PaperTradeEntity
import com.example.data.local.SavedSignalEntity
import com.example.data.repository.BacktestSummary
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaperTradingScreen(
    paperTrades: List<PaperTradeEntity>,
    savedSignals: List<SavedSignalEntity>,
    accountBalance: Double,
    currentLivePrice: Double,
    backtestSummary: BacktestSummary?,
    onCloseTrade: (PaperTradeEntity) -> Unit,
    onDeleteTrade: (PaperTradeEntity) -> Unit,
    onDeleteSignal: (SavedSignalEntity) -> Unit,
    onRunBacktest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val openTrades = paperTrades.filter { it.status == "OPEN" }
    val closedTrades = paperTrades.filter { it.status != "OPEN" }

    // Calculate total unrealized PnL
    var totalUnrealizedPnl = 0.0
    for (t in openTrades) {
        val isLong = t.signalType == "LONG"
        val diff = if (isLong) (currentLivePrice - t.entryPrice) else (t.entryPrice - currentLivePrice)
        if (t.entryPrice > 0) {
            totalUnrealizedPnl += (diff / t.entryPrice) * (t.margin * t.leverage)
        }
    }

    val totalRealizedPnl = closedTrades.mapNotNull { it.pnlUsd }.sum()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Portfolio Stats Header
        Card(
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Paper Trading Portfolio", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Capital Balance", fontSize = 10.sp, color = TextMuted)
                        Text("$${formatPrice(accountBalance + totalRealizedPnl)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Unrealized PnL", fontSize = 10.sp, color = TextMuted)
                        val isPos = totalUnrealizedPnl >= 0
                        Text(
                            text = "${if (isPos) "+" else ""}$${formatPrice(totalUnrealizedPnl)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPos) BullGreen else BearRed
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Realized PnL", fontSize = 10.sp, color = TextMuted)
                        val isRealPos = totalRealizedPnl >= 0
                        Text(
                            text = "${if (isRealPos) "+" else ""}$${formatPrice(totalRealizedPnl)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRealPos) BullGreen else BearRed
                        )
                    }
                }
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CryptoSurfaceVariant,
            contentColor = BullGreen
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Open (${openTrades.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("History (${closedTrades.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Saved (${savedSignals.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 3,
                onClick = { selectedSubTab = 3 },
                text = { Text("Backtester", fontSize = 12.sp) }
            )
        }

        // Content
        when (selectedSubTab) {
            0 -> OpenTradesList(openTrades = openTrades, livePrice = currentLivePrice, onClose = onCloseTrade)
            1 -> ClosedTradesList(closedTrades = closedTrades, onDelete = onDeleteTrade)
            2 -> SavedSignalsList(savedSignals = savedSignals, onDelete = onDeleteSignal)
            3 -> BacktestSection(backtestSummary = backtestSummary, onRun = onRunBacktest)
        }
    }
}

@Composable
private fun OpenTradesList(
    openTrades: List<PaperTradeEntity>,
    livePrice: Double,
    onClose: (PaperTradeEntity) -> Unit
) {
    if (openTrades.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No open paper trades. Go to 'Signal & Plan' tab to open one!", color = TextSecondary, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(openTrades) { trade ->
                val isLong = trade.signalType == "LONG"
                val diff = if (isLong) (livePrice - trade.entryPrice) else (trade.entryPrice - livePrice)
                val pnlUsd = if (trade.entryPrice > 0) (diff / trade.entryPrice) * (trade.margin * trade.leverage) else 0.0
                val pnlPct = if (trade.entryPrice > 0) (diff / trade.entryPrice) * trade.leverage * 100.0 else 0.0
                val isPos = pnlUsd >= 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = trade.signalType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLong) BullGreen else BearRed,
                                    modifier = Modifier
                                        .background(
                                            if (isLong) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${trade.symbol} (${trade.leverage}x)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Button(
                                onClick = { onClose(trade) },
                                colors = ButtonDefaults.buttonColors(containerColor = CryptoSurfaceVariant),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Close", fontSize = 10.sp, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Entry Price: $${formatPrice(trade.entryPrice)}", fontSize = 11.sp, color = TextSecondary)
                                Text("Mark Price: $${formatPrice(livePrice)}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("SL: $${formatPrice(trade.stopLoss)}", fontSize = 11.sp, color = BearRed)
                                Text("TP: $${formatPrice(trade.tp1)}", fontSize = 11.sp, color = BullGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Margin: $${formatPrice(trade.margin)}", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "PnL: ${if (isPos) "+" else ""}$${formatPrice(pnlUsd)} (${String.format("%.2f", pnlPct)}%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPos) BullGreen else BearRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClosedTradesList(
    closedTrades: List<PaperTradeEntity>,
    onDelete: (PaperTradeEntity) -> Unit
) {
    if (closedTrades.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No closed trades yet", color = TextSecondary, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(closedTrades) { trade ->
                val pnl = trade.pnlUsd ?: 0.0
                val isPos = pnl >= 0
                val dateStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(trade.closeTime ?: trade.openTime))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = trade.signalType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (trade.signalType == "LONG") BullGreen else BearRed
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${trade.symbol} • ${trade.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Text(text = "$dateStr • Entry $${formatPrice(trade.entryPrice)}", fontSize = 10.sp, color = TextMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${if (isPos) "+" else ""}$${formatPrice(pnl)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPos) BullGreen else BearRed
                            )
                            IconButton(
                                onClick = { onDelete(trade) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedSignalsList(
    savedSignals: List<SavedSignalEntity>,
    onDelete: (SavedSignalEntity) -> Unit
) {
    if (savedSignals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No saved signals yet", color = TextSecondary, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(savedSignals) { signal ->
                val dateStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(signal.timestamp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${signal.signalType} ${signal.symbol} (${signal.timeframe})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (signal.signalType == "LONG") BullGreen else BearRed
                            )
                            IconButton(onClick = { onDelete(signal) }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(text = "Score: ${signal.score}/100 • $dateStr", fontSize = 10.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Entry Zone: ${signal.entryZone} • SL: $${formatPrice(signal.stopLoss)} • TP: $${formatPrice(signal.tp1)}", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun BacktestSection(
    backtestSummary: BacktestSummary?,
    onRun: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Historical Strategy Backtester", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tests the confluence rules (RSI momentum, EMA structure, MACD, and 1:1.5 R:R target) across recent historical candles without forward bias.",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onRun,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_backtest_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Run Backtest on Current Candlesticks", fontWeight = FontWeight.Bold)
                }

                if (backtestSummary != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Backtest Results:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BacktestMetric(label = "Total Trades", value = "${backtestSummary.totalTrades}")
                        BacktestMetric(label = "Win Rate", value = "${backtestSummary.winRatePercent}%", isPositive = backtestSummary.winRatePercent >= 50)
                        BacktestMetric(label = "Profit Factor", value = "${backtestSummary.profitFactor}", isPositive = backtestSummary.profitFactor >= 1.2)
                        BacktestMetric(label = "Net PnL", value = "${backtestSummary.totalPnlPercent}%", isPositive = backtestSummary.totalPnlPercent >= 0)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Winning Trades: ${backtestSummary.winTrades}", fontSize = 11.sp, color = BullGreen)
                        Text("Losing Trades: ${backtestSummary.lossTrades}", fontSize = 11.sp, color = BearRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun BacktestMetric(label: String, value: String, isPositive: Boolean? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = when (isPositive) {
                true -> BullGreen
                false -> BearRed
                null -> TextPrimary
            }
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
