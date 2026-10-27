package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.WatchlistEntity
import com.example.domain.model.FuturesStats
import com.example.domain.model.Timeframe
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
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
fun TopBarHeader(
    symbol: String,
    stats: FuturesStats?,
    timeframe: Timeframe,
    isLoading: Boolean,
    errorMessage: String?,
    watchlist: List<WatchlistEntity>,
    developerName: String = "Sarfaraz",
    onDeveloperClick: () -> Unit = {},
    onSymbolSelected: (String) -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onRefresh: () -> Unit,
    onAddWatchlist: (String) -> Unit
) {
    var showPairDialog by remember { mutableStateOf(false) }
    var customSymbolInput by remember { mutableStateOf("") }

    Surface(
        color = CryptoSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Row 1: App Brand, Live Status, Refresh
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
                            .background(
                                when {
                                    isLoading -> WarningAmber
                                    errorMessage != null -> BearRed
                                    stats?.isFallback == true -> AccentCyan
                                    else -> BullGreen
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Trade AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BullGreen.copy(alpha = 0.15f))
                            .clickable { onDeveloperClick() }
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Dev: $developerName",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BullGreen
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val timeStr = stats?.let {
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(it.lastUpdated))
                    } ?: "Offline"

                    Text(
                        text = timeStr,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("refresh_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = BullGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh market data",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Selected Pair Selector & Live Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pair Selector Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CryptoSurfaceVariant)
                        .clickable { showPairDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("pair_selector_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = symbol,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select trading pair",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Live Price and 24h Change
                if (stats != null) {
                    val isPositive = stats.priceChange24h >= 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatPrice(stats.lastPrice),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val changeSign = if (isPositive) "+" else ""
                        Text(
                            text = "$changeSign${String.format("%.2f", stats.priceChange24h)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) BullGreen else BearRed,
                            modifier = Modifier
                                .background(
                                    if (isPositive) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Watchlist quick-pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(watchlist) { item ->
                    val isSelected = item.symbol.equals(symbol, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) BullGreen.copy(alpha = 0.2f) else CryptoSurfaceVariant)
                            .clickable { onSymbolSelected(item.symbol) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.symbol.replace("USDT", ""),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BullGreen else TextSecondary
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CryptoSurfaceVariant)
                            .clickable { showPairDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add pair",
                                tint = AccentCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "Add", fontSize = 11.sp, color = AccentCyan)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 4: Timeframe selection buttons (5m, 15m, 1h, 4h, 1d)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Timeframe.entries.forEach { tf ->
                    val isSelected = tf == timeframe
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) BullGreen else CryptoSurfaceVariant)
                            .clickable { onTimeframeSelected(tf) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tf.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }

            // Connection source info banner
            if (stats != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Source: ${stats.source}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    if (stats.fundingRate != 0.0) {
                        val fundingPct = stats.fundingRate * 100.0
                        Text(
                            text = "Funding (8h): ${String.format("%.4f", fundingPct)}%",
                            fontSize = 10.sp,
                            color = if (fundingPct < 0) BullGreen else TextSecondary
                        )
                    }
                }
            }
        }
    }

    // Pair Selector Dialog
    if (showPairDialog) {
        AlertDialog(
            onDismissRequest = { showPairDialog = false },
            title = { Text("Select Cryptocurrency Pair", color = TextPrimary, fontSize = 18.sp) },
            text = {
                Column {
                    Text(
                        "Search or enter any USDT perpetual contract:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customSymbolInput,
                        onValueChange = { customSymbolInput = it.uppercase() },
                        placeholder = { Text("e.g. BTCUSDT, SOLUSDT, AVAXUSDT", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Popular Pairs:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val popular = listOf("BTCUSDT", "ETHUSDT", "SOLUSDT", "BNBUSDT", "DOGEUSDT", "XRPUSDT", "AVAXUSDT", "LINKUSDT", "ADAUSDT", "NEARUSDT")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        popular.take(4).forEach { p ->
                            Button(
                                onClick = {
                                    onSymbolSelected(p)
                                    showPairDialog = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(p.replace("USDT", ""), fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sym = customSymbolInput.trim()
                        if (sym.isNotEmpty()) {
                            val cleanSym = if (!sym.endsWith("USDT")) "${sym}USDT" else sym
                            onSymbolSelected(cleanSym)
                            onAddWatchlist(cleanSym)
                        }
                        showPairDialog = false
                    }
                ) {
                    Text("Select Pair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPairDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CryptoSurface
        )
    }
}

private fun formatPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format("$%,.2f", p)
        p >= 1.0 -> String.format("$%.4f", p)
        else -> String.format("$%.6f", p)
    }
}
