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
import com.example.data.local.PaperTradeEntity
import com.example.data.local.SavedSignalEntity
import com.example.data.repository.BacktestSummary
import com.example.domain.model.UserSubscriptionState
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.GoldenPocketGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TradesAndHubContainerScreen(
    paperTrades: List<PaperTradeEntity>,
    savedSignals: List<SavedSignalEntity>,
    accountBalance: Double,
    currentLivePrice: Double,
    livePrices: Map<String, Double>,
    currentSymbol: String,
    backtestSummary: BacktestSummary?,
    subscriptionState: UserSubscriptionState,
    developerName: String,
    developerWhatsapp: String,
    isRomanUrdu: Boolean = false,
    onCloseTrade: (PaperTradeEntity) -> Unit,
    onDeleteTrade: (PaperTradeEntity) -> Unit,
    onDeleteSignal: (SavedSignalEntity) -> Unit,
    onRunBacktest: () -> Unit,
    onRefresh: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenAdminPortal: () -> Unit,
    onOpenInfluencerPortal: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLegal: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onRedeemCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Top Segmented Bar: [ Paper Trades & Backtest | VIP, Admin & Hub ]
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
            val tradesCount = paperTrades.count { it.status == "OPEN" }
            val tab0Label = if (isRomanUrdu) {
                if (tradesCount > 0) "Trades ($tradesCount) & Test" else "Paper Trades & Test"
            } else {
                if (tradesCount > 0) "Paper Trades ($tradesCount)" else "Paper Trades"
            }
            val tab1Label = if (isRomanUrdu) "Markaz & VIP Hub" else "VIP & Markaz Hub"

            // Tab 0: Paper Trades
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedSection == 0) AccentCyan else Color.Transparent)
                    .clickable { selectedSection = 0 }
                    .padding(vertical = 8.dp)
                    .testTag("tab_section_trades"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab0Label,
                    fontSize = 12.sp,
                    fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedSection == 0) Color.Black else TextSecondary
                )
            }

            // Tab 1: App Hub & Admin & Guide
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedSection == 1) GoldenPocketGold else Color.Transparent)
                    .clickable { selectedSection = 1 }
                    .padding(vertical = 8.dp)
                    .testTag("tab_section_hub"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab1Label,
                    fontSize = 12.sp,
                    fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedSection == 1) Color.Black else TextSecondary
                )
            }
        }

        // Screen Body
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedSection == 0) {
                PaperTradingScreen(
                    paperTrades = paperTrades,
                    savedSignals = savedSignals,
                    accountBalance = accountBalance,
                    currentLivePrice = currentLivePrice,
                    livePrices = livePrices,
                    currentSymbol = currentSymbol,
                    backtestSummary = backtestSummary,
                    isRomanUrdu = isRomanUrdu,
                    onCloseTrade = onCloseTrade,
                    onDeleteTrade = onDeleteTrade,
                    onDeleteSignal = onDeleteSignal,
                    onRunBacktest = onRunBacktest,
                    onRefresh = onRefresh
                )
            } else {
                AppHubScreen(
                    subscriptionState = subscriptionState,
                    developerName = developerName,
                    developerWhatsapp = developerWhatsapp,
                    isRomanUrdu = isRomanUrdu,
                    onOpenGuide = onOpenGuide,
                    onOpenSubscription = onOpenSubscription,
                    onOpenAdminPortal = onOpenAdminPortal,
                    onOpenInfluencerPortal = onOpenInfluencerPortal,
                    onOpenAbout = onOpenAbout,
                    onOpenLegal = onOpenLegal,
                    onToggleLanguage = onToggleLanguage,
                    onRedeemCode = onRedeemCode
                )
            }
        }
    }
}
