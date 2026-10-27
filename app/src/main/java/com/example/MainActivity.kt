package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CandlestickChart
import com.example.ui.components.TopBarHeader
import com.example.ui.screens.BeginnerGuideScreen
import com.example.ui.screens.FuturesAnalyticsScreen
import com.example.ui.screens.IndicatorsScreen
import com.example.ui.screens.PaperTradingScreen
import com.example.ui.screens.PlanAndConfluenceScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.SmartTradeTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TradingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartTradeTheme {
                SmartTradeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SmartTradeApp(viewModel: TradingViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val savedSignals by viewModel.savedSignals.collectAsState()
    val paperTrades by viewModel.paperTrades.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBarHeader(
                symbol = uiState.currentSymbol,
                stats = uiState.analysisResult?.futuresStats,
                timeframe = uiState.selectedTimeframe,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                watchlist = watchlist,
                developerName = uiState.developerName,
                onDeveloperClick = { viewModel.selectTab(5) },
                onSymbolSelected = { viewModel.selectPair(it) },
                onTimeframeSelected = { viewModel.selectTimeframe(it) },
                onRefresh = { viewModel.refreshData() },
                onAddWatchlist = { viewModel.addToWatchlist(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CryptoSurface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Signal & Plan") },
                    label = { Text("Signal", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = BullGreen,
                        indicatorColor = BullGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_signal")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Chart & SMC") },
                    label = { Text("Chart", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = AccentCyan,
                        indicatorColor = AccentCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_chart")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Indicators") },
                    label = { Text("Indicators", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = BullGreen,
                        indicatorColor = BullGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_indicators")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Futures") },
                    label = { Text("Futures", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = AccentCyan,
                        indicatorColor = AccentCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_futures")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.WorkOutline, contentDescription = "Paper Trading") },
                    label = { Text("Paper", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = BullGreen,
                        indicatorColor = BullGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_paper")
                )
                NavigationBarItem(
                    selected = uiState.selectedTab == 5,
                    onClick = { viewModel.selectTab(5) },
                    icon = { Icon(Icons.Default.School, contentDescription = "Trader Guide") },
                    label = { Text("Guide", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = AccentCyan,
                        indicatorColor = AccentCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_guide")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CryptoBackground)
        ) {
            val analysis = uiState.analysisResult

            if (analysis != null) {
                when (uiState.selectedTab) {
                    0 -> PlanAndConfluenceScreen(
                        analysis = analysis,
                        accountBalance = uiState.accountBalance,
                        riskPercent = uiState.riskPercent,
                        leverage = uiState.leverage,
                        onUpdateRisk = { b, r, l -> viewModel.updateRiskSettings(b, r, l) },
                        onExecutePaperTrade = { viewModel.executePaperTrade() },
                        onSaveSignal = { viewModel.saveCurrentSignal() }
                    )
                    1 -> CandlestickChart(
                        candles = analysis.candles,
                        indicators = analysis.indicators,
                        smc = analysis.smc,
                        fibonacci = analysis.fibonacci,
                        showEma = uiState.showEma,
                        showBollinger = uiState.showBollinger,
                        showFibonacci = uiState.showFibonacci,
                        showOrderBlocks = uiState.showOrderBlocks,
                        showFvg = uiState.showFvg,
                        showBos = uiState.showBos,
                        onToggleOverlay = { viewModel.toggleOverlay(it) }
                    )
                    2 -> IndicatorsScreen(
                        indicators = analysis.indicators,
                        timeframe = uiState.selectedTimeframe,
                        currentPrice = analysis.futuresStats?.lastPrice ?: analysis.candles.last().close
                    )
                    3 -> FuturesAnalyticsScreen(
                        stats = analysis.futuresStats,
                        smc = analysis.smc,
                        currentPrice = analysis.futuresStats?.lastPrice ?: analysis.candles.last().close
                    )
                    4 -> PaperTradingScreen(
                        paperTrades = paperTrades,
                        savedSignals = savedSignals,
                        accountBalance = uiState.accountBalance,
                        currentLivePrice = analysis.futuresStats?.lastPrice ?: analysis.candles.last().close,
                        backtestSummary = uiState.backtestSummary,
                        onCloseTrade = { viewModel.closePaperTrade(it) },
                        onDeleteTrade = { viewModel.deletePaperTrade(it) },
                        onDeleteSignal = { viewModel.deleteSavedSignal(it) },
                        onRunBacktest = { viewModel.runBacktest() }
                    )
                    5 -> BeginnerGuideScreen(
                        developerName = uiState.developerName,
                        developerWhatsapp = uiState.developerWhatsapp,
                        onUpdateDeveloperInfo = { name, wa -> viewModel.updateDeveloperInfo(name, wa) }
                    )
                }
            }
        }
    }
}
