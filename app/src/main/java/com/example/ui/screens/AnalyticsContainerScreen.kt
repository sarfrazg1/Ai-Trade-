package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.domain.model.FuturesStats
import com.example.domain.model.SmartMoneyConcepts
import com.example.domain.model.TechnicalIndicators
import com.example.domain.model.Timeframe
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyticsContainerScreen(
    indicators: TechnicalIndicators,
    stats: FuturesStats?,
    smc: SmartMoneyConcepts,
    timeframe: Timeframe,
    currentPrice: Double,
    isRomanUrdu: Boolean = false,
    hasFuturesAccess: Boolean = true,
    onOpenSubscription: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedAnalyticsTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Top Segmented Switch: [ Technical Indicators | Futures & Derivatives ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CryptoSurface)
                .border(1.dp, CryptoCardBorder, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tab0Label = if (isRomanUrdu) "Technical Ishare" else "Technical Indicators"
            val tab1Label = if (isRomanUrdu) "Futures & Derivatives" else "Futures Analytics"

            // Tab 0: Indicators
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedAnalyticsTab == 0) BullGreen else Color.Transparent)
                    .clickable { selectedAnalyticsTab = 0 }
                    .padding(vertical = 8.dp)
                    .testTag("seg_tab_indicators"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab0Label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedAnalyticsTab == 0) Color.Black else TextSecondary
                )
            }

            // Tab 1: Futures
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedAnalyticsTab == 1) AccentCyan else Color.Transparent)
                    .clickable { selectedAnalyticsTab = 1 }
                    .padding(vertical = 8.dp)
                    .testTag("seg_tab_futures"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab1Label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedAnalyticsTab == 1) Color.Black else TextSecondary
                )
            }
        }

        // Body
        if (selectedAnalyticsTab == 0) {
            IndicatorsScreen(
                indicators = indicators,
                timeframe = timeframe,
                currentPrice = currentPrice,
                isRomanUrdu = isRomanUrdu
            )
        } else {
            FuturesAnalyticsScreen(
                stats = stats,
                smc = smc,
                currentPrice = currentPrice,
                hasFuturesAccess = hasFuturesAccess,
                isRomanUrdu = isRomanUrdu,
                onOpenSubscription = onOpenSubscription
            )
        }
    }
}
