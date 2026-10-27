package com.example.ui.viewmodel

import android.app.Application
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
import com.example.domain.model.SignalType
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
    val showSubscriptionModal: Boolean = false,
    val showAdminModal: Boolean = false,
    val showInfluencerModal: Boolean = false,
    val showLegalModal: Boolean = false,
    val legalTab: String = "DISCLAIMER"
)

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("smart_trade_prefs", Application.MODE_PRIVATE)

    val subscriptionManager = com.example.data.repository.SubscriptionManager(application.applicationContext)
    val subscriptionState = subscriptionManager.subscriptionState

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
            // Default to false so emulator preview and app open immediately to interactive charts & signals
            showSubscriptionModal = false
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
        _uiState.value = _uiState.value.copy(showDeveloperDialog = show)
    }

    fun updateDeveloperInfo(name: String, whatsapp: String) {
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
        val result = subscriptionManager.redeemActivationCode(code)
        result.onSuccess { message ->
            _uiState.value = _uiState.value.copy(userNotification = message)
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(userNotification = err.localizedMessage ?: "Invalid code")
        }
    }

    fun acceptLegalConsent() {
        subscriptionManager.recordLegalConsent(disclaimer = true, terms = true)
        _uiState.value = _uiState.value.copy(userNotification = "Terms & Disclaimer accepted.")
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
