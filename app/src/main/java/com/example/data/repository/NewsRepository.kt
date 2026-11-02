package com.example.data.repository

import com.example.data.remote.NewsApiClient
import com.example.domain.model.ConceptCategory
import com.example.domain.model.CryptoGlobalStats
import com.example.domain.model.EconomicCalendarEvent
import com.example.domain.model.FinancialConcept
import com.example.domain.model.MarketImpactDirection
import com.example.domain.model.MarketSentimentSummary
import com.example.domain.model.NewsArticle
import com.example.domain.model.NewsCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class NewsRepository(
    private val newsApiClient: NewsApiClient = NewsApiClient()
) {
    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles.asStateFlow()

    private val _globalStats = MutableStateFlow<CryptoGlobalStats?>(null)
    val globalStats: StateFlow<CryptoGlobalStats?> = _globalStats.asStateFlow()

    private val _sentimentSummary = MutableStateFlow<MarketSentimentSummary?>(null)
    val sentimentSummary: StateFlow<MarketSentimentSummary?> = _sentimentSummary.asStateFlow()

    private val _economicEvents = MutableStateFlow<List<EconomicCalendarEvent>>(emptyList())
    val economicEvents: StateFlow<List<EconomicCalendarEvent>> = _economicEvents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    suspend fun loadNewsAndMarketData(
        category: NewsCategory = NewsCategory.ALL,
        searchQuery: String = "",
        binanceBtcPrice: Double = 0.0,
        binanceEthPrice: Double = 0.0,
        forceRefresh: Boolean = false
    ) = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _errorMessage.value = null

        try {
            // 1. Fetch News Articles
            val newsResult = newsApiClient.fetchLatestNews(category, searchQuery)
            val articles = newsResult.getOrDefault(emptyList())

            // Filter in-memory if needed
            val filtered = if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase().trim()
                articles.filter {
                    it.title.lowercase().contains(q) ||
                            it.summaryEnglish.lowercase().contains(q) ||
                            it.summaryUrdu.lowercase().contains(q) ||
                            it.source.lowercase().contains(q)
                }
            } else if (category != NewsCategory.ALL) {
                articles.filter { it.category == category }
            } else {
                articles
            }

            _newsArticles.value = filtered

            // 2. Fetch Global Market Telemetry
            val stats = newsApiClient.fetchGlobalMarketStats(binanceBtcPrice, binanceEthPrice)
            _globalStats.value = stats

            // 3. Compute Sentiment Summary
            val sentiment = computeMarketSentiment(articles, stats)
            _sentimentSummary.value = sentiment

            // 4. Load Economic Events
            _economicEvents.value = newsApiClient.getUpcomingEconomicEvents()

        } catch (e: Exception) {
            _errorMessage.value = "Market intelligence service temporarily unavailable: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private fun computeMarketSentiment(
        articles: List<NewsArticle>,
        stats: CryptoGlobalStats
    ): MarketSentimentSummary {
        val total = articles.size.coerceAtLeast(1)
        val bullishCount = articles.count { it.overallSentiment == MarketImpactDirection.BULLISH }
        val bearishCount = articles.count { it.overallSentiment == MarketImpactDirection.BEARISH }
        val neutralCount = articles.count { it.overallSentiment == MarketImpactDirection.NEUTRAL || it.overallSentiment == MarketImpactDirection.UNCERTAIN }

        val bullishPct = (bullishCount * 100) / total
        val bearishPct = (bearishCount * 100) / total

        val overallState = when {
            bullishPct >= 65 -> MarketImpactDirection.BULLISH
            bearishPct >= 60 -> MarketImpactDirection.BEARISH
            bullishPct >= 45 -> MarketImpactDirection.BULLISH
            else -> MarketImpactDirection.NEUTRAL
        }

        val score = (bullishPct * 0.7 + (if (stats.marketCapChange24h > 0) 30 else 10)).toInt().coerceIn(15, 95)

        val bullishFactors = mutableListOf<String>()
        val bearishFactors = mutableListOf<String>()

        if (bullishCount > bearishCount) {
            bullishFactors.add("News flow indicates positive institutional and macro liquidity sentiment ($bullishPct% Bullish).")
        } else {
            bearishFactors.add("Elevated cautious/bearish news coverage observed ($bearishPct% Bearish).")
        }

        if (stats.marketCapChange24h >= 0) {
            bullishFactors.add("Global crypto market cap expanded +${String.format("%.2f", stats.marketCapChange24h)}% in past 24 hours.")
        } else {
            bearishFactors.add("Global crypto market cap contracted ${String.format("%.2f", stats.marketCapChange24h)}% in past 24 hours.")
        }

        if (stats.btcDominance >= 55.0) {
            bullishFactors.add("Strong Bitcoin Dominance (${String.format("%.1f", stats.btcDominance)}%) signals steady capital flight into core asset.")
        }

        val summaryUrdu = when (overallState) {
            MarketImpactDirection.BULLISH -> "Mojooda market sentiment pur-umeed (Bullish) hai. Khabron mein institutional sarmayakari aur mehngai mein kami ke asraat numayan hain."
            MarketImpactDirection.BEARISH -> "Mojooda market sentiment dabao aur khauf (Bearish) me hai. Traders ko sakht risk management aur Stop Loss ka istemal karna chahiye."
            else -> "Market is waqt mutawazan aur ghair-janibdar (Neutral) halat me hai. Breakout ya wazeh news catalyst ka intezar behtar hai."
        }

        val summaryEn = when (overallState) {
            MarketImpactDirection.BULLISH -> "Overall market tone is constructive with strong institutional accumulation underlying major support floors."
            MarketImpactDirection.BEARISH -> "Cautious sentiment prevails amid selective regulatory and macroeconomic uncertainties."
            else -> "Balanced consolidation regime with selective rotational opportunities across high-conviction assets."
        }

        return MarketSentimentSummary(
            overallState = overallState,
            scorePercent = score,
            bullishFactors = bullishFactors,
            bearishFactors = bearishFactors,
            summaryUrdu = summaryUrdu,
            summaryEnglish = summaryEn
        )
    }

    /**
     * Search and filter Financial Learning Center concepts
     */
    fun getFinancialConcepts(
        category: ConceptCategory? = null,
        searchQuery: String = ""
    ): List<FinancialConcept> {
        var list = when (category) {
            ConceptCategory.ECONOMIC_CONCEPTS -> FinancialLearningCenterData.economicConcepts
            ConceptCategory.CRYPTO_CONCEPTS -> FinancialLearningCenterData.cryptoConcepts
            null -> FinancialLearningCenterData.allConcepts
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase().trim()
            list = list.filter {
                it.term.lowercase().contains(q) ||
                        it.fullForm.lowercase().contains(q) ||
                        it.simpleUrduMeaning.lowercase().contains(q) ||
                        it.everydayExample.lowercase().contains(q) ||
                        it.whyItMatters.lowercase().contains(q) ||
                        it.marketImpactExplanation.lowercase().contains(q)
            }
        }
        return list
    }
}
