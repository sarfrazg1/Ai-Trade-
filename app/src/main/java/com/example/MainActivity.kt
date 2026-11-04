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
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CandlestickChart
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.UrduStrings
import com.example.ui.screens.AnalyticsContainerScreen
import com.example.ui.screens.AppHubScreen
import com.example.ui.screens.BeginnerGuideScreen
import com.example.ui.screens.FuturesAnalyticsScreen
import com.example.ui.screens.IndicatorsScreen
import com.example.ui.screens.NewsAndImpactScreen
import com.example.ui.screens.PairDetailScreen
import com.example.ui.screens.PaperTradingScreen
import com.example.ui.screens.PlanAndConfluenceScreen
import com.example.ui.screens.TradesAndHubContainerScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.GoldenPocketGold
import com.example.ui.theme.SmartTradeTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.TradingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.attachActivity(this)
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                val app = com.google.firebase.FirebaseApp.initializeApp(this)
                if (app == null) {
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setApplicationId("1:613712404065:android:7aa69d4126a01da247d420")
                        .setApiKey("AIzaSyBhZqkU9ZJWnIASKM4LSQ2Me3X_qLkTLG0")
                        .setProjectId("gen-lang-client-0963937245")
                        .setStorageBucket("gen-lang-client-0963937245.firebasestorage.app")
                        .setDatabaseUrl("https://gen-lang-client-0963937245-default-rtdb.firebaseio.com")
                        .build()
                    com.google.firebase.FirebaseApp.initializeApp(this, options)
                }
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

    override fun onResume() {
        super.onResume()
        viewModel.attachActivity(this)
        viewModel.onAppResumed()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPaused()
    }
}

@Composable
fun SmartTradeApp(viewModel: TradingViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val savedSignals by viewModel.savedSignals.collectAsState()
    val paperTrades by viewModel.paperTrades.collectAsState()
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    val newsArticles by viewModel.newsArticles.collectAsState()
    val globalStats by viewModel.globalStats.collectAsState()
    val sentimentSummary by viewModel.sentimentSummary.collectAsState()
    val economicEvents by viewModel.economicEvents.collectAsState()
    val isNewsLoading by viewModel.isNewsLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showGuideModal by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    val isUrdu = uiState.isRomanUrdu
    val layoutDirection = if (isUrdu) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
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
                    isChartTab = uiState.selectedTab == 2
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CryptoSurface,
                    tonalElevation = 6.dp
                ) {
                    // Tab 0: Signal
                    NavigationBarItem(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = { Icon(Icons.Default.Bolt, contentDescription = "Signal") },
                        label = { Text(UrduStrings.tabSignal(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullGreen,
                            indicatorColor = BullGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_signal")
                    )

                    // Tab 1: Pair Detail (SMC & Fibonacci Retracement Tool)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "SMC & Fib") },
                        label = { Text(UrduStrings.tabSmcFib(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldenPocketGold,
                            indicatorColor = GoldenPocketGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_smc_fib")
                    )

                    // Tab 2: Candlestick Chart
                    NavigationBarItem(
                        selected = uiState.selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Chart") },
                        label = { Text(UrduStrings.tabChart(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = AccentCyan,
                            indicatorColor = AccentCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_chart")
                    )

                    // Tab 3: News & Market Impact (MANDATORY DEDICATED PAGE)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = { Icon(Icons.Default.Newspaper, contentDescription = "News") },
                        label = { Text(UrduStrings.tabNews(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = AccentCyan,
                            indicatorColor = AccentCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_news")
                    )

                    // Tab 4: Analytics (Indicators & Futures)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = { Icon(Icons.Default.Speed, contentDescription = "Analytics") },
                        label = { Text(UrduStrings.tabAnalytics(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullGreen,
                            indicatorColor = BullGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_analytics")
                    )

                    // Tab 5: Trades & Hub (Paper Trades, Backtest, VIP Codes, Admin, WhatsApp, Settings)
                    NavigationBarItem(
                        selected = uiState.selectedTab == 5,
                        onClick = { viewModel.selectTab(5) },
                        icon = { Icon(Icons.Default.Widgets, contentDescription = "Trades & Hub") },
                        label = { Text(UrduStrings.tabHub(isUrdu), fontSize = 9.sp, maxLines = 1, softWrap = false, fontWeight = if (uiState.selectedTab == 5) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldenPocketGold,
                            indicatorColor = GoldenPocketGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("tab_trades_hub")
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
                        onSaveSignal = { viewModel.saveCurrentSignal() },
                        onNavigateToSmcDetail = { viewModel.selectTab(1) }
                    )
                    1 -> PairDetailScreen(
                        symbol = uiState.currentSymbol,
                        timeframe = uiState.selectedTimeframe,
                        currentPrice = livePrice,
                        stats = analysis.futuresStats,
                        smc = analysis.smc,
                        fibonacci = analysis.fibonacci,
                        isRomanUrdu = uiState.isRomanUrdu,
                        onNavigateToChart = { viewModel.selectTab(2) },
                        onNavigateToSignal = { viewModel.selectTab(0) },
                        onExecuteInstantTrade = { isLong -> viewModel.openPaperTradeInstant(isLong) }
                    )
                    2 -> CandlestickChart(
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
                        stats = analysis.futuresStats,
                        symbol = uiState.currentSymbol,
                        isRomanUrdu = uiState.isRomanUrdu,
                        onOpenInstantTrade = { isLong -> viewModel.openPaperTradeInstant(isLong) },
                        onCloseTrade = { trade -> viewModel.closePaperTrade(trade) }
                    )
                    3 -> NewsAndImpactScreen(
                        articles = newsArticles,
                        globalStats = globalStats,
                        sentimentSummary = sentimentSummary,
                        economicEvents = economicEvents,
                        financialConcepts = viewModel.getFilteredFinancialConcepts(),
                        selectedCategory = uiState.selectedNewsCategory,
                        searchQuery = uiState.newsSearchQuery,
                        selectedArticleDetail = uiState.selectedArticleDetail,
                        activeSection = uiState.newsActiveSection,
                        selectedLearningCategory = uiState.selectedLearningCategory,
                        learningSearchQuery = uiState.learningSearchQuery,
                        isLoading = isNewsLoading,
                        isPaidUser = subscriptionState.isPrivilegedActive || uiState.isUserAdmin,
                        isRomanUrdu = uiState.isRomanUrdu,
                        onSelectCategory = { viewModel.selectNewsCategory(it) },
                        onUpdateSearchQuery = { viewModel.updateNewsSearchQuery(it) },
                        onSelectArticleDetail = { viewModel.selectArticleDetail(it) },
                        onSelectSection = { viewModel.selectNewsSection(it) },
                        onSelectLearningCategory = { viewModel.selectLearningCategory(it) },
                        onUpdateLearningSearch = { viewModel.updateLearningSearchQuery(it) },
                        onRefresh = { viewModel.refreshNewsData() },
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) }
                    )
                    4 -> AnalyticsContainerScreen(
                        indicators = analysis.indicators,
                        stats = analysis.futuresStats,
                        smc = analysis.smc,
                        timeframe = uiState.selectedTimeframe,
                        currentPrice = livePrice,
                        isRomanUrdu = uiState.isRomanUrdu,
                        hasFuturesAccess = subscriptionState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW, uiState.isUserAdmin),
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) }
                    )
                    5 -> TradesAndHubContainerScreen(
                        paperTrades = paperTrades,
                        savedSignals = savedSignals,
                        accountBalance = uiState.accountBalance,
                        currentLivePrice = livePrice,
                        livePrices = uiState.livePrices,
                        currentSymbol = uiState.currentSymbol,
                        backtestSummary = uiState.backtestSummary,
                        subscriptionState = subscriptionState,
                        developerName = uiState.developerName,
                        developerWhatsapp = uiState.developerWhatsapp,
                        isRomanUrdu = uiState.isRomanUrdu,
                        onCloseTrade = { viewModel.closePaperTrade(it) },
                        onDeleteTrade = { viewModel.deletePaperTrade(it) },
                        onDeleteSignal = { viewModel.deleteSavedSignal(it) },
                        onRunBacktest = { viewModel.runBacktest() },
                        onRefresh = { viewModel.refreshData() },
                        onOpenGuide = { showGuideModal = true },
                        onOpenSubscription = { viewModel.openSubscriptionModal(true) },
                        onOpenAdminPortal = { viewModel.openAdminModal(true) },
                        onOpenInfluencerPortal = { viewModel.openInfluencerModal(true) },
                        onOpenAbout = { viewModel.openAboutModal(true) },
                        onOpenLegal = { viewModel.openLegalModal(it) },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onRedeemCode = { viewModel.redeemActivationCode(it) }
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
                            text = UrduStrings.loadingData(uiState.isRomanUrdu),
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
                                text = UrduStrings.retryConnection(uiState.isRomanUrdu),
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
                    onSignInGoogle = { viewModel.signInWithGoogle(context) },
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
                    isRomanUrdu = uiState.isRomanUrdu,
                    onSignInAdmin = { viewModel.signInWithGoogle(context) },
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
            } else if (showGuideModal) {
                BackHandler { showGuideModal = false }
                com.example.ui.screens.BeginnerGuideScreen(
                    isRomanUrdu = uiState.isRomanUrdu,
                    isAdmin = uiState.isUserAdmin,
                    onOpenSubscription = {
                        showGuideModal = false
                        viewModel.openSubscriptionModal(true)
                    },
                    onOpenInfluencerPortal = {
                        showGuideModal = false
                        viewModel.openInfluencerModal(true)
                    },
                    onOpenAdminPortal = {
                        showGuideModal = false
                        viewModel.openAdminModal(true)
                    },
                    onOpenLegal = {
                        showGuideModal = false
                        viewModel.openLegalModal(it)
                    },
                    onOpenAbout = {
                        showGuideModal = false
                        viewModel.openAboutModal(true)
                    }
                )
            } else if (uiState.selectedArticleDetail != null) {
                BackHandler { viewModel.selectArticleDetail(null) }
            } else if (uiState.selectedTab != 0) {
                BackHandler { viewModel.selectTab(0) }
            }
        }
    }
}
}
