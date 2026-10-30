package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
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
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Firebase initialization check in onCreate", e)
        }
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
    val subscriptionState by viewModel.subscriptionState.collectAsState()

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
                isRomanUrdu = uiState.isRomanUrdu,
                subscriptionPlan = subscriptionState.currentPlan,
                isSubscribed = subscriptionState.isPrivilegedActive,
                onSubscriptionClick = { viewModel.openSubscriptionModal(true) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onDeveloperClick = { viewModel.openAboutModal(true) },
                onSymbolSelected = { viewModel.selectPair(it) },
                onTimeframeSelected = { viewModel.selectTimeframe(it) },
                onRefresh = { viewModel.refreshData() },
                onAddWatchlist = { viewModel.addToWatchlist(it) },
                isChartTab = uiState.selectedTab == 1
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
                    label = { Text(if (uiState.isRomanUrdu) "Signal" else "Signal", fontSize = 10.sp) },
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
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Chart & SMC") },
                    label = { Text(if (uiState.isRomanUrdu) "Chart" else "Chart", fontSize = 10.sp) },
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
                    label = { Text(if (uiState.isRomanUrdu) "Ishare" else "Indicators", fontSize = 10.sp) },
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
                    label = { Text(if (uiState.isRomanUrdu) "Trades" else "Paper", fontSize = 10.sp) },
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
                    label = { Text(if (uiState.isRomanUrdu) "Guide" else "Guide", fontSize = 10.sp) },
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
                val livePrice = analysis.futuresStats?.lastPrice ?: analysis.candles.lastOrNull()?.close ?: 0.0

                when (uiState.selectedTab) {
                    0 -> PlanAndConfluenceScreen(
                        analysis = analysis,
                        accountBalance = uiState.accountBalance,
                        riskPercent = uiState.riskPercent,
                        leverage = uiState.leverage,
                        isRomanUrdu = uiState.isRomanUrdu,
                        hasPreciseTargets = subscriptionState.hasAccessTo(com.example.domain.model.AppFeature.PRECISE_TP_SL_TARGETS, uiState.isUserAdmin),
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) },
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
                        onToggleOverlay = { viewModel.toggleOverlay(it) },
                        activeTrades = paperTrades.filter { it.symbol.equals(uiState.currentSymbol, ignoreCase = true) },
                        currentLivePrice = livePrice,
                        isRomanUrdu = uiState.isRomanUrdu,
                        onOpenInstantTrade = { isLong -> viewModel.openPaperTradeInstant(isLong) },
                        onCloseTrade = { trade -> viewModel.closePaperTrade(trade) }
                    )
                    2 -> IndicatorsScreen(
                        indicators = analysis.indicators,
                        timeframe = uiState.selectedTimeframe,
                        currentPrice = livePrice
                    )
                    3 -> FuturesAnalyticsScreen(
                        stats = analysis.futuresStats,
                        smc = analysis.smc,
                        currentPrice = livePrice,
                        hasFuturesAccess = subscriptionState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW, uiState.isUserAdmin),
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) }
                    )
                    4 -> PaperTradingScreen(
                        paperTrades = paperTrades,
                        savedSignals = savedSignals,
                        accountBalance = uiState.accountBalance,
                        currentLivePrice = livePrice,
                        backtestSummary = uiState.backtestSummary,
                        onCloseTrade = { viewModel.closePaperTrade(it) },
                        onDeleteTrade = { viewModel.deletePaperTrade(it) },
                        onDeleteSignal = { viewModel.deleteSavedSignal(it) },
                        onRunBacktest = { viewModel.runBacktest() }
                    )
                    5 -> BeginnerGuideScreen(
                        isRomanUrdu = uiState.isRomanUrdu,
                        isAdmin = uiState.isUserAdmin,
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) },
                        onOpenInfluencerPortal = { viewModel.openInfluencerModal(true) },
                        onOpenAdminPortal = { viewModel.openAdminModal(true) },
                        onOpenLegal = { viewModel.openLegalModal(it) },
                        onOpenAbout = { viewModel.openAboutModal(true) }
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BullGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (uiState.isRomanUrdu) "Smart Trade AI data load ho raha hai..." else "Loading Smart Trade AI analysis...",
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.refreshData() },
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                        ) {
                            Text(
                                text = if (uiState.isRomanUrdu) "Dobara Koshish Karein" else "Retry Connection",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 1. First-Launch Welcome Onboarding Modal
            if (uiState.showOnboardingModal) {
                com.example.ui.screens.WelcomeOnboardingScreen(
                    onSelectPlanAndContinue = { selectedPlan ->
                        viewModel.completeOnboarding(selectedPlan)
                    },
                    onOpenLegal = { viewModel.openLegalModal(it) }
                )
            } else if (uiState.showAboutModal) {
                BackHandler { viewModel.openAboutModal(false) }
                com.example.ui.screens.AboutScreen(
                    developerName = uiState.developerName,
                    developerWhatsapp = uiState.developerWhatsapp,
                    isRomanUrdu = uiState.isRomanUrdu,
                    isAdmin = uiState.isUserAdmin,
                    onClose = { viewModel.openAboutModal(false) },
                    onOpenSubscription = {
                        viewModel.openAboutModal(false)
                        viewModel.openSubscriptionModal(true)
                    },
                    onOpenLegal = { viewModel.openLegalModal(it) },
                    onOpenAdmin = {
                        viewModel.openAboutModal(false)
                        viewModel.openAdminModal(true)
                    },
                    onUpdateDeveloperInfo = { name, wa -> viewModel.updateDeveloperInfo(name, wa) }
                )
            } else if (uiState.showSubscriptionModal) {
                BackHandler { viewModel.openSubscriptionModal(false) }
                com.example.ui.screens.SubscriptionScreen(
                    subscriptionState = subscriptionState,
                    developerWhatsapp = uiState.developerWhatsapp,
                    isRomanUrdu = uiState.isRomanUrdu,
                    googleUserEmail = uiState.googleUserEmail,
                    isGoogleSignedIn = uiState.isGoogleSignedIn,
                    onSignInGoogle = { viewModel.signInWithGoogle() },
                    onSignOutGoogle = { viewModel.signOutGoogle() },
                    onSyncCloud = { viewModel.syncDataToFirestore() },
                    onApplyReferralCode = { viewModel.applyReferralCode(it) },
                    onRedeemCode = { viewModel.redeemActivationCode(it) },
                    onSubmitPaymentProof = { plan, trx -> viewModel.submitPaymentVerification(plan, trx) },
                    onClose = { viewModel.openSubscriptionModal(false) },
                    onOpenLegalPage = { viewModel.openLegalModal(it) },
                    onOpenAdminPortal = { viewModel.openAdminModal(true) },
                    isAdmin = uiState.isUserAdmin
                )
            } else if (uiState.showAdminModal) {
                BackHandler { viewModel.openAdminModal(false) }
                com.example.ui.screens.AdminScreen(
                    subscriptionManager = viewModel.subscriptionManager,
                    isUserAdmin = uiState.isUserAdmin,
                    adminEmail = uiState.googleUserEmail,
                    onSignInAdmin = { viewModel.signInWithGoogle() },
                    onPublishCodeCloud = { code, userRef, plan, days, paymentRef ->
                        viewModel.adminPublishCodeToCloud(code, userRef, plan, days, paymentRef)
                    },
                    onClose = { viewModel.openAdminModal(false) }
                )
            } else if (uiState.showInfluencerModal) {
                BackHandler { viewModel.openInfluencerModal(false) }
                com.example.ui.screens.InfluencerDashboardScreen(
                    subscriptionManager = viewModel.subscriptionManager,
                    initialReferralCode = subscriptionState.referralCode,
                    onClose = { viewModel.openInfluencerModal(false) }
                )
            } else if (uiState.showLegalModal) {
                BackHandler { viewModel.closeLegalModal() }
                com.example.ui.screens.LegalDocumentsScreen(
                    initialTab = uiState.legalTab,
                    onClose = { viewModel.closeLegalModal() },
                    onAcceptConsent = { viewModel.acceptLegalConsent() }
                )
            } else if (uiState.selectedTab != 0) {
                BackHandler { viewModel.selectTab(0) }
            }
        }
    }
}
