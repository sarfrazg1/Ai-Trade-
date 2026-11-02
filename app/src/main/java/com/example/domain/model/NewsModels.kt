package com.example.domain.model

enum class NewsCategory(val id: String, val labelEn: String, val labelUrdu: String) {
    ALL("all", "All News", "Tamam Khabrain"),
    GLOBAL_ECONOMY("global_economy", "Global Economy", "Almi Maeeshat"),
    FED_FOMC("fed_fomc", "Federal Reserve / FOMC", "Fed aur FOMC"),
    INFLATION_CPI("inflation_cpi", "CPI / Inflation", "Mehngai (CPI/PPI)"),
    INTEREST_RATES("interest_rates", "Interest Rates", "Soodi Sharah"),
    EMPLOYMENT_NFP("employment_nfp", "Employment / NFP", "Mulazmat aur NFP"),
    GDP("gdp", "GDP Growth", "GDP Taraqqi"),
    BITCOIN("bitcoin", "Bitcoin / BTC", "Bitcoin (BTC)"),
    ETHEREUM("ethereum", "Ethereum / ETH", "Ethereum (ETH)"),
    ALTCOINS("altcoins", "Altcoins", "Altcoins"),
    CRYPTO_ETFS("crypto_etfs", "Crypto ETFs", "Crypto ETFs"),
    REGULATION("regulation", "Regulation", "Qawaneen / SEC"),
    INSTITUTIONAL("institutional", "Institutional", "Idarayati Sarmayakari"),
    STABLECOINS("stablecoins", "Stablecoins", "Stablecoins"),
    GEOPOLITICAL("geopolitical", "Geopolitics", "Geopolitics / Jang"),
    GOLD_USD("gold_usd", "Gold & USD", "Sona aur Dollar"),
    SENTIMENT("sentiment", "Market Sentiment", "Market Ka Rujhan")
}

enum class VerificationStatus(val labelEn: String, val labelUrdu: String) {
    VERIFIED("Verified by Reliable Source", "Tasdeeq Shuda (Bawisaq Zariya)"),
    DEVELOPING("Developing Story", "Taza Tareen / Jaari Khabar"),
    UNVERIFIED("Unverified Report", "Ghair Tasdeeq Shuda"),
    CONFLICTING("Conflicting Reports", "Mutazaad Bayaanat")
}

enum class MarketImpactDirection(val labelEn: String, val labelUrdu: String) {
    BULLISH("Bullish", "Tezi (Bullish)"),
    BEARISH("Bearish", "Mandi (Bearish)"),
    NEUTRAL("Neutral", "Ghair Janibdar (Neutral)"),
    UNCERTAIN("Uncertain", "Ghair Wazeh (Uncertain)")
}

data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val url: String,
    val publishedAt: Long,
    val retrievedAt: Long = System.currentTimeMillis(),
    val category: NewsCategory,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val summaryEnglish: String,
    val summaryUrdu: String,
    val importantFacts: List<String> = emptyList(),
    val economicValues: EconomicValueComparison? = null,
    val overallSentiment: MarketImpactDirection = MarketImpactDirection.NEUTRAL,
    val btcImpact: AssetImpact,
    val ethImpact: AssetImpact,
    val altcoinsImpact: AssetImpact,
    val usdImpact: AssetImpact,
    val goldImpact: AssetImpact,
    val broaderMarketsImpact: String = "",
    val shortTermImpact: String = "",
    val mediumTermImpact: String = "",
    val longTermImpact: String = "",
    val riskLevel: String = "Medium", // Low, Medium, High
    val confidenceLevel: String = "Moderate", // High, Moderate, Low
    val whatToMonitorNext: String = "",
    val aiAnalysisText: String = "",
    val isAiGenerated: Boolean = false,
    val isAiUnavailable: Boolean = false
)

data class EconomicValueComparison(
    val indicatorName: String,
    val actualValue: String?,
    val forecastValue: String?,
    val previousValue: String?
)

data class AssetImpact(
    val assetName: String,
    val direction: MarketImpactDirection,
    val reasonEnglish: String,
    val reasonUrdu: String
)

data class EconomicCalendarEvent(
    val id: String,
    val name: String,
    val scheduledTime: Long,
    val country: String = "United States (US)",
    val importance: String = "HIGH", // HIGH, MEDIUM, LOW
    val previousValue: String = "",
    val forecastValue: String = "",
    val actualValue: String = "",
    val relevance: String = "",
    val urduExplanation: String = ""
)

data class CryptoGlobalStats(
    val btcPrice: Double,
    val ethPrice: Double,
    val totalMarketCapUsd: Double = 0.0,
    val volume24hUsd: Double = 0.0,
    val btcDominance: Double = 0.0,
    val marketCapChange24h: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isDelayed: Boolean = false,
    val source: String = "CoinGecko & Binance Public APIs"
)

data class MarketSentimentSummary(
    val overallState: MarketImpactDirection,
    val scorePercent: Int, // 0 - 100
    val bullishFactors: List<String>,
    val bearishFactors: List<String>,
    val summaryUrdu: String,
    val summaryEnglish: String
)

enum class ConceptCategory(val titleEn: String, val titleUrdu: String) {
    ECONOMIC_CONCEPTS("Macroeconomic Concepts", "Maeeshat aur Banking ke Bunyadi Usool"),
    CRYPTO_CONCEPTS("Crypto Market Concepts", "Crypto aur Trading ke Bunyadi Usool")
}

data class FinancialConcept(
    val id: String,
    val category: ConceptCategory,
    val term: String,
    val fullForm: String = "",
    val simpleUrduMeaning: String,
    val everydayExample: String,
    val whyItMatters: String,
    val marketImpactExplanation: String,
    val whatTraderShouldWatch: String
)
