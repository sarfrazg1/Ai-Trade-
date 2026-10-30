package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.PaperTradeEntity
import com.example.data.local.SavedSignalEntity
import com.example.data.local.WatchlistEntity
import com.example.data.remote.BinanceClient
import com.example.data.repository.BacktestSummary
import com.example.data.repository.CompleteAnalysisResult
import com.example.data.repository.CryptoTradingRepository
import com.example.data.repository.DefaultMarketSnapshot
import com.example.domain.model.AppFeature
import com.example.domain.model.SignalType
import com.example.domain.model.SubscriptionPlan
import com.example.domain.model.Timeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TradingUiState(
    val currentSymbol: String = "BTCUSDT",
    val selectedTimeframe: Timeframe = Timeframe.FIFTEEN_MIN,
    val analysisResult: CompleteAnalysisResult? = DefaultMarketSnapshot.createInitialSnapshot("BTCUSDT", Timeframe.FIFTEEN_MIN),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accountBalance: Double = 1000.0,
    val riskPercent: Double = 2.0,
    val leverage: Int = 5,
    val selectedTab: Int = 0,
    // Chart Overlay toggles
    val showEma: Boolean = true,
    val showBollinger: Boolean = false,
    val showFibonacci: Boolean = true,
    val showOrderBlocks: Boolean = true,
    val showFvg: Boolean = true,
    val showBos: Boolean = true,
    val backtestSummary: BacktestSummary? = null,
    val userNotification: String? = null,
    val developerName: String = "Rao Muhammad Sarfraz",
    val developerWhatsapp: String = "+923360074976",
    val showDeveloperDialog: Boolean = false,
    val isRomanUrdu: Boolean = false,
    // Subscription & Modals
    val showOnboardingModal: Boolean = false,
    val showSubscriptionModal: Boolean = false,
    val showAboutModal: Boolean = false,
    val showAdminModal: Boolean = false,
    val showInfluencerModal: Boolean = false,
    val showLegalModal: Boolean = false,
    val legalTab: String = "DISCLAIMER",
    // Firebase Auth & Cloud Firestore
    val googleUserEmail: String? = null,
    val googleDisplayName: String? = null,
    val isGoogleSignedIn: Boolean = false,
    val isUserAdmin: Boolean = false,
    val isCloudSyncing: Boolean = false,
    val lastCloudSyncTimestamp: Long? = null
)

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val ADMIN_EMAIL = "sarfarazmech00@gmail.com"
    }

    private val prefs = application.getSharedPreferences("smart_trade_prefs", Application.MODE_PRIVATE)

    val subscriptionManager = com.example.data.repository.SubscriptionManager(application.applicationContext)
    val subscriptionState = subscriptionManager.subscriptionState

    val googleAuthManager = com.example.data.repository.GoogleAuthManager(application.applicationContext)
    val firestoreSyncRepository = com.example.data.repository.FirestoreSyncRepository(application.applicationContext)

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "smart_trade_crypto.db"
    ).fallbackToDestructiveMigration().build()

    private val client = BinanceClient()
    private val repository = CryptoTradingRepository(client, db)

    private val _uiState = MutableStateFlow(
        TradingUiState(
            developerName = prefs.getString("dev_name", "Rao Muhammad Sarfraz") ?: "Rao Muhammad Sarfraz",
            developerWhatsapp = prefs.getString("dev_whatsapp", "+923360074976") ?: "+923360074976",
            isRomanUrdu = prefs.getBoolean("is_roman_urdu", false),
            showSubscriptionModal = false,
            showOnboardingModal = !prefs.getBoolean("has_completed_onboarding", false) && !subscriptionManager.subscriptionState.value.isPrivilegedActive
        )
    )
    val uiState: StateFlow<TradingUiState> = _uiState.asStateFlow()

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedSignals: StateFlow<List<SavedSignalEntity>> = repository.savedSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paperTrades: StateFlow<List<PaperTradeEntity>> = repository.paperTrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDefaultWatchlistIfNeeded()
            loadCurrentPair()
        }
        viewModelScope.launch {
            googleAuthManager.currentUser.collect { user ->
                val isAdmin = user?.email?.equals(ADMIN_EMAIL, ignoreCase = true) == true
                _uiState.value = _uiState.value.copy(
                    googleUserEmail = user?.email,
                    googleDisplayName = user?.displayName,
                    isGoogleSignedIn = user != null,
                    isUserAdmin = isAdmin
                )
                if (user != null) {
                    firestoreSyncRepository.syncUserProfile(user, subscriptionState.value)
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun selectPair(symbol: String) {
        val clean = symbol.uppercase().trim()
        if (clean.isNotEmpty() && clean != _uiState.value.currentSymbol) {
            _uiState.value = _uiState.value.copy(currentSymbol = clean)
            loadCurrentPair()
        }
    }

    fun selectTimeframe(tf: Timeframe) {
        val hasVip = subscriptionState.value.hasAccessTo(AppFeature.VIP_SIGNALS_5M_1D, _uiState.value.isUserAdmin)
        if (!hasVip && (tf == Timeframe.FIVE_MIN || tf == Timeframe.DAILY)) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Timeframe ${tf.label} is exclusive to Golden VIP Plan. Tap Upgrade to activate."
            )
            return
        }
        if (tf != _uiState.value.selectedTimeframe) {
            _uiState.value = _uiState.value.copy(selectedTimeframe = tf)
            loadCurrentPair()
        }
    }

    fun refreshData() {
        loadCurrentPair()
    }

    fun updateRiskSettings(balance: Double, riskPct: Double, lev: Int) {
        _uiState.value = _uiState.value.copy(
            accountBalance = balance.coerceAtLeast(10.0),
            riskPercent = riskPct.coerceIn(0.1, 10.0),
            leverage = lev.coerceIn(1, 50)
        )
        // Recalculate plan with new risk settings
        val current = _uiState.value.analysisResult
        if (current != null) {
            viewModelScope.launch {
                loadCurrentPair()
            }
        }
    }

    fun toggleOverlay(overlay: String) {
        val s = _uiState.value
        val hasSmc = subscriptionState.value.hasAccessTo(AppFeature.SMC_ADVANCED_OVERLAYS, s.isUserAdmin)
        if (!hasSmc && (overlay == "ORDER_BLOCKS" || overlay == "FVG" || overlay == "BOS")) {
            _uiState.value = s.copy(
                userNotification = "Smart Money Concepts ($overlay) require Golden VIP Plan. Tap Upgrade to activate."
            )
            return
        }
        _uiState.value = when (overlay) {
            "EMA" -> s.copy(showEma = !s.showEma)
            "BOLLINGER" -> s.copy(showBollinger = !s.showBollinger)
            "FIBONACCI" -> s.copy(showFibonacci = !s.showFibonacci)
            "ORDER_BLOCKS" -> s.copy(showOrderBlocks = !s.showOrderBlocks)
            "FVG" -> s.copy(showFvg = !s.showFvg)
            "BOS" -> s.copy(showBos = !s.showBos)
            else -> s
        }
    }

    fun saveCurrentSignal() {
        val plan = _uiState.value.analysisResult?.tradingPlan ?: return
        val confluence = _uiState.value.analysisResult?.confluence ?: return
        viewModelScope.launch {
            repository.saveSignal(plan, confluence)
            _uiState.value = _uiState.value.copy(userNotification = "Signal saved to history!")
        }
    }

    fun deleteSavedSignal(signal: SavedSignalEntity) {
        viewModelScope.launch {
            repository.deleteSavedSignal(signal)
        }
    }

    fun executePaperTrade() {
        val plan = _uiState.value.analysisResult?.tradingPlan ?: return
        if (plan.signal == SignalType.WAIT) {
            _uiState.value = _uiState.value.copy(userNotification = "Cannot open paper trade on WAIT status!")
            return
        }

        viewModelScope.launch {
            val margin = _uiState.value.accountBalance * (_uiState.value.riskPercent / 100.0)
            repository.openPaperTrade(
                symbol = plan.symbol,
                signalType = plan.signal.name,
                entryPrice = plan.currentPrice,
                stopLoss = plan.stopLoss,
                tp1 = plan.tp1,
                tp2 = plan.tp2,
                margin = margin,
                leverage = _uiState.value.leverage
            )
            _uiState.value = _uiState.value.copy(userNotification = "Paper trade opened: ${plan.signal} ${plan.symbol} @ $${plan.currentPrice}")
        }
    }

    fun closePaperTrade(trade: PaperTradeEntity) {
        val currentPrice = _uiState.value.analysisResult?.futuresStats?.lastPrice ?: trade.entryPrice
        viewModelScope.launch {
            repository.closePaperTrade(trade, currentPrice, "MANUALLY_CLOSED")
            _uiState.value = _uiState.value.copy(userNotification = "Paper trade closed manually")
        }
    }

    fun deletePaperTrade(trade: PaperTradeEntity) {
        viewModelScope.launch {
            repository.deletePaperTrade(trade)
        }
    }

    fun addToWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.addToWatchlist(symbol)
            _uiState.value = _uiState.value.copy(userNotification = "Added $symbol to watchlist")
        }
    }

    fun removeFromWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.removeFromWatchlist(symbol)
            _uiState.value = _uiState.value.copy(userNotification = "Removed $symbol from watchlist")
        }
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(userNotification = null)
    }

    fun toggleDeveloperDialog(show: Boolean) {
        if (show && !_uiState.value.isUserAdmin) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Developer settings are restricted to verified administrator ($ADMIN_EMAIL)."
            )
            return
        }
        _uiState.value = _uiState.value.copy(showDeveloperDialog = show)
    }

    fun updateDeveloperInfo(name: String, whatsapp: String) {
        if (!_uiState.value.isUserAdmin) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Access Denied: Only administrator ($ADMIN_EMAIL) can modify developer details."
            )
            return
        }
        val cleanName = name.trim().ifEmpty { "Sarfaraz" }
        val cleanWhatsapp = whatsapp.trim()
        prefs.edit()
            .putString("dev_name", cleanName)
            .putString("dev_whatsapp", cleanWhatsapp)
            .apply()
        _uiState.value = _uiState.value.copy(
            developerName = cleanName,
            developerWhatsapp = cleanWhatsapp,
            userNotification = "Developer details saved successfully!"
        )
    }

    fun toggleLanguage() {
        val newLang = !_uiState.value.isRomanUrdu
        prefs.edit().putBoolean("is_roman_urdu", newLang).apply()
        _uiState.value = _uiState.value.copy(isRomanUrdu = newLang)
    }

    fun openPaperTradeInstant(isLong: Boolean) {
        val analysis = _uiState.value.analysisResult ?: return
        val currentPrice = analysis.futuresStats?.lastPrice ?: analysis.candles.lastOrNull()?.close ?: return
        val atr = analysis.indicators.atr14 ?: (currentPrice * 0.01)

        val stopLoss = if (isLong) currentPrice - atr * 1.5 else currentPrice + atr * 1.5
        val tp1 = if (isLong) currentPrice + atr * 2.25 else currentPrice - atr * 2.25
        val tp2 = if (isLong) currentPrice + atr * 3.75 else currentPrice - atr * 3.75
        val margin = _uiState.value.accountBalance * (_uiState.value.riskPercent / 100.0)

        viewModelScope.launch {
            repository.openPaperTrade(
                symbol = _uiState.value.currentSymbol,
                signalType = if (isLong) "LONG" else "SHORT",
                entryPrice = currentPrice,
                stopLoss = stopLoss,
                tp1 = tp1,
                tp2 = tp2,
                margin = margin,
                leverage = _uiState.value.leverage
            )
            val notif = if (_uiState.value.isRomanUrdu) {
                "${if (isLong) "LONG" else "SHORT"} Trade chart per open ho gaya hai @ $$currentPrice"
            } else {
                "${if (isLong) "LONG" else "SHORT"} trade opened on chart @ $$currentPrice"
            }
            _uiState.value = _uiState.value.copy(userNotification = notif)
        }
    }

    fun runBacktest() {
        val candles = _uiState.value.analysisResult?.candles ?: return
        val summary = repository.runBacktest(candles)
        _uiState.value = _uiState.value.copy(backtestSummary = summary)
    }

    fun completeOnboarding(selectedPlan: SubscriptionPlan) {
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
        _uiState.value = _uiState.value.copy(showOnboardingModal = false)
        if (selectedPlan != SubscriptionPlan.FREE_TRIAL) {
            subscriptionManager.setPendingPlanSelection(selectedPlan)
            _uiState.value = _uiState.value.copy(
                showSubscriptionModal = true,
                userNotification = "Selected ${selectedPlan.title}. Please submit payment reference or enter activation code."
            )
        } else {
            _uiState.value = _uiState.value.copy(
                userNotification = "Welcome to Smart Trade AI! Free demo mode activated."
            )
        }
    }

    fun openAboutModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAboutModal = show)
    }

    fun openSubscriptionModal(show: Boolean) {
        if (!show) {
            prefs.edit().putBoolean("has_seen_welcome_screen", true).apply()
        }
        _uiState.value = _uiState.value.copy(showSubscriptionModal = show)
    }

    fun openAdminModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAdminModal = show)
    }

    fun openInfluencerModal(show: Boolean) {
        if (show && !_uiState.value.isUserAdmin) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Access Denied: Influencer management is restricted to authorized administrator."
            )
            return
        }
        _uiState.value = _uiState.value.copy(showInfluencerModal = show)
    }

    fun openLegalModal(tab: String = "DISCLAIMER") {
        _uiState.value = _uiState.value.copy(showLegalModal = true, legalTab = tab)
    }

    fun closeLegalModal() {
        _uiState.value = _uiState.value.copy(showLegalModal = false)
    }

    fun applyReferralCode(code: String): Boolean {
        val success = subscriptionManager.setReferralCode(code)
        if (success) {
            _uiState.value = _uiState.value.copy(userNotification = "Referral code applied!")
        }
        return success
    }

    fun redeemActivationCode(code: String) {
        val user = googleAuthManager.currentUser.value
        val userRef = subscriptionManager.getUserReference()
        val referral = subscriptionState.value.referralCode

        if (user != null) {
            val nonNullUser = user
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val cloudRes = firestoreSyncRepository.redeemActivationCodeCloud(
                    user = nonNullUser,
                    code = code,
                    currentUserRef = userRef,
                    influencerCode = referral
                )
                _uiState.value = _uiState.value.copy(isLoading = false)

                if (cloudRes.isSuccess) {
                    // Update local manager for instant UI state
                    subscriptionManager.redeemActivationCode(code)
                    _uiState.value = _uiState.value.copy(
                        userNotification = cloudRes.getOrNull() ?: "Subscription activated in Firebase Cloud!"
                    )
                } else {
                    // Fall back to local HMAC verification if offline or manually signed
                    val localRes = subscriptionManager.redeemActivationCode(code)
                    localRes.onSuccess { msg ->
                        _uiState.value = _uiState.value.copy(userNotification = msg)
                        firestoreSyncRepository.syncUserProfile(nonNullUser, subscriptionState.value)
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(
                            userNotification = "Activation failed: ${err.localizedMessage ?: cloudRes.exceptionOrNull()?.localizedMessage}"
                        )
                    }
                }
            }
        } else {
            val result = subscriptionManager.redeemActivationCode(code)
            result.onSuccess { message ->
                _uiState.value = _uiState.value.copy(userNotification = message)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(userNotification = err.localizedMessage ?: "Invalid code")
            }
        }
    }

    fun submitPaymentVerification(plan: String, paymentRef: String) {
        val user = googleAuthManager.currentUser.value
        val userRef = subscriptionManager.getUserReference()
        val referral = subscriptionState.value.referralCode ?: ""

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (user != null) {
                val res = firestoreSyncRepository.recordActivationRequest(
                    userId = user.uid,
                    userReference = userRef,
                    plan = plan,
                    paymentReference = paymentRef,
                    influencerCode = referral
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userNotification = if (res.isSuccess)
                        "Payment reference recorded in Cloud! Send screenshot to WhatsApp (+923360074976) to receive activation code."
                    else "Recorded locally. Please share screenshot on WhatsApp (+923360074976)."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userNotification = "Payment recorded! Please send reference $paymentRef on WhatsApp (+923360074976)."
                )
            }
        }
    }

    fun adminPublishCodeToCloud(
        code: String,
        targetUserRef: String,
        plan: String,
        validityDays: Int = 30,
        paymentRef: String = "MANUAL_VERIFIED"
    ) {
        if (!_uiState.value.isUserAdmin) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Access Denied: Only verified admin ($ADMIN_EMAIL) can issue activation codes."
            )
            return
        }
        viewModelScope.launch {
            firestoreSyncRepository.adminIssueCodeCloud(
                code = code,
                targetUserRef = targetUserRef,
                plan = plan,
                validityDays = validityDays,
                paymentRef = paymentRef,
                referralCode = subscriptionState.value.referralCode ?: ""
            )
        }
    }

    fun acceptLegalConsent() {
        subscriptionManager.recordLegalConsent(disclaimer = true, terms = true)
        _uiState.value = _uiState.value.copy(userNotification = "Terms & Disclaimer accepted.")
        val user = googleAuthManager.currentUser.value
        if (user != null) {
            val nonNullUser = user
            viewModelScope.launch {
                firestoreSyncRepository.syncUserProfile(nonNullUser, subscriptionState.value)
            }
        }
    }

    fun signInWithGoogle(launcherContext: Context? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = googleAuthManager.signInWithGoogle(launcherContext)
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    userNotification = "Google Sign-In successful: ${user.displayName ?: user.email}"
                )
                firestoreSyncRepository.syncUserProfile(user, subscriptionState.value)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    userNotification = "Sign in cancelled or failed: ${err.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun signOutGoogle() {
        googleAuthManager.signOut()
        _uiState.value = _uiState.value.copy(
            userNotification = "Signed out of Google account."
        )
    }

    fun syncDataToFirestore() {
        val user = googleAuthManager.currentUser.value
        if (user == null) {
            _uiState.value = _uiState.value.copy(
                userNotification = "Please Sign in with Google first to sync with Firebase Cloud!"
            )
            return
        }
        val nonNullUser = user
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCloudSyncing = true)
            val res = firestoreSyncRepository.syncUserProfile(nonNullUser, subscriptionState.value)
            _uiState.value = _uiState.value.copy(
                isCloudSyncing = false,
                lastCloudSyncTimestamp = System.currentTimeMillis(),
                userNotification = if (res.isSuccess) "Data synced with Firebase Firestore!" else "Failed to sync: ${res.exceptionOrNull()?.localizedMessage}"
            )
        }
    }

    private fun loadCurrentPair() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val symbol = _uiState.value.currentSymbol
            val tf = _uiState.value.selectedTimeframe
            val balance = _uiState.value.accountBalance
            val riskPct = _uiState.value.riskPercent
            val lev = _uiState.value.leverage

            val result = repository.analyzePair(
                symbol = symbol,
                selectedTimeframe = tf,
                accountBalance = balance,
                riskPercent = riskPct,
                leverage = lev
            )

            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    analysisResult = data,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { err ->
                val fallback = _uiState.value.analysisResult
                    ?: DefaultMarketSnapshot.createInitialSnapshot(symbol, tf)
                _uiState.value = _uiState.value.copy(
                    analysisResult = fallback,
                    isLoading = false,
                    errorMessage = err.localizedMessage ?: "Using offline market snapshot"
                )
            }
        }
    }
}
