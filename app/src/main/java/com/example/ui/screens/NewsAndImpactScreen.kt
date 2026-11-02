package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AssetImpact
import com.example.domain.model.ConceptCategory
import com.example.domain.model.CryptoGlobalStats
import com.example.domain.model.EconomicCalendarEvent
import com.example.domain.model.FinancialConcept
import com.example.domain.model.MarketImpactDirection
import com.example.domain.model.MarketSentimentSummary
import com.example.domain.model.NewsArticle
import com.example.domain.model.NewsCategory
import com.example.domain.model.VerificationStatus
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.GoldenPocketGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewsAndImpactScreen(
    articles: List<NewsArticle>,
    globalStats: CryptoGlobalStats?,
    sentimentSummary: MarketSentimentSummary?,
    economicEvents: List<EconomicCalendarEvent>,
    financialConcepts: List<FinancialConcept>,
    selectedCategory: NewsCategory,
    searchQuery: String,
    selectedArticleDetail: NewsArticle?,
    activeSection: Int, // 0: News, 1: Calendar, 2: Learning Center
    selectedLearningCategory: ConceptCategory?,
    learningSearchQuery: String,
    isLoading: Boolean,
    isPaidUser: Boolean,
    isRomanUrdu: Boolean = false,
    onSelectCategory: (NewsCategory) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onSelectArticleDetail: (NewsArticle?) -> Unit,
    onSelectSection: (Int) -> Unit,
    onSelectLearningCategory: (ConceptCategory?) -> Unit,
    onUpdateLearningSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .testTag("news_and_impact_screen")
    ) {
        // ==========================================
        // 1. DEDICATED TOOLBAR
        // ==========================================
        Surface(
            color = CryptoSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AccentCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = "News",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isRomanUrdu) "News & Market Impact" else "News & Market Impact",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRomanUrdu) "Almi Khabrain & AI Market Asraat" else "Financial Intelligence & AI Analysis",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AccentCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_refresh_news")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh News",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. CHECK PAID ACCESS (FREE PREVIEW GATE)
        // ==========================================
        if (!isPaidUser) {
            FreeUserLockedNewsView(
                isRomanUrdu = isRomanUrdu,
                onOpenSubscription = onOpenSubscription
            )
            return
        }

        // ==========================================
        // 3. MAIN SECTION SEGMENT SWITCHER
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CryptoSurface)
                .border(1.dp, CryptoCardBorder, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                0 to (if (isRomanUrdu) "📰 Khabrain (News)" else "📰 News & Impact"),
                1 to (if (isRomanUrdu) "📅 Calendar" else "📅 Calendar"),
                2 to (if (isRomanUrdu) "🎓 Learning Center" else "🎓 Learning Center")
            )

            tabs.forEach { (index, title) ->
                val isSelected = activeSection == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected && index == 0 -> AccentCyan
                                isSelected && index == 1 -> BullGreen
                                isSelected && index == 2 -> GoldenPocketGold
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onSelectSection(index) }
                        .padding(vertical = 8.dp)
                        .testTag("news_tab_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // ==========================================
        // 4. BODY CONTENT ACCORDING TO ACTIVE SECTION
        // ==========================================
        when (activeSection) {
            0 -> NewsFeedView(
                articles = articles,
                globalStats = globalStats,
                sentimentSummary = sentimentSummary,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                isRomanUrdu = isRomanUrdu,
                onSelectCategory = onSelectCategory,
                onUpdateSearchQuery = onUpdateSearchQuery,
                onSelectArticle = onSelectArticleDetail
            )
            1 -> EconomicCalendarView(
                events = economicEvents,
                isRomanUrdu = isRomanUrdu
            )
            2 -> FinancialLearningCenterView(
                concepts = financialConcepts,
                selectedCategory = selectedLearningCategory,
                searchQuery = learningSearchQuery,
                isRomanUrdu = isRomanUrdu,
                onSelectCategory = onSelectLearningCategory,
                onUpdateSearch = onUpdateLearningSearch
            )
        }

        // ==========================================
        // 5. ARTICLE DETAIL DIALOG / MODAL
        // ==========================================
        selectedArticleDetail?.let { article ->
            ArticleDetailModal(
                article = article,
                isRomanUrdu = isRomanUrdu,
                onDismiss = { onSelectArticleDetail(null) },
                onOpenLink = { url ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
        }
    }
}

// =========================================================================
// SUB-VIEW 1: LATEST NEWS & MARKET IMPACT FEED
// =========================================================================

@Composable
private fun NewsFeedView(
    articles: List<NewsArticle>,
    globalStats: CryptoGlobalStats?,
    sentimentSummary: MarketSentimentSummary?,
    selectedCategory: NewsCategory,
    searchQuery: String,
    isRomanUrdu: Boolean,
    onSelectCategory: (NewsCategory) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onSelectArticle: (NewsArticle) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // A. Global Market Telemetry Strip
        item {
            globalStats?.let { stats ->
                GlobalMarketOverviewCard(stats = stats, isRomanUrdu = isRomanUrdu)
            }
        }

        // B. Market Sentiment Card
        item {
            sentimentSummary?.let { sentiment ->
                MarketSentimentCard(sentiment = sentiment, isRomanUrdu = isRomanUrdu)
            }
        }

        // C. Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onUpdateSearchQuery,
                placeholder = {
                    Text(
                        text = if (isRomanUrdu) "Khabar ya keyword talash karein (BTC, Fed, CPI...)" else "Search financial news, Fed, CPI, BTC...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onUpdateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = CryptoCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CryptoSurface,
                    unfocusedContainerColor = CryptoSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("news_search_input")
            )
        }

        // D. Category Filter Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(NewsCategory.entries.toTypedArray()) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = {
                            Text(
                                text = if (isRomanUrdu) cat.labelUrdu else cat.labelEn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentCyan.copy(alpha = 0.25f),
                            selectedLabelColor = AccentCyan,
                            containerColor = CryptoSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) AccentCyan else CryptoCardBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // E. News Article Cards
        if (articles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isRomanUrdu) "Is category me koi taza khabar nahi mili." else "No news found for this category or search.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(articles, key = { it.id }) { article ->
                NewsArticleCard(
                    article = article,
                    isRomanUrdu = isRomanUrdu,
                    onOpenDetail = { onSelectArticle(article) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GlobalMarketOverviewCard(stats: CryptoGlobalStats, isRomanUrdu: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRomanUrdu) "Almi Market Telemetry" else "Live Crypto Market Overview",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = stats.source,
                    fontSize = 10.sp,
                    color = AccentCyan
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "BTC", fontSize = 10.sp, color = TextMuted)
                    Text(text = "$${formatPrice(stats.btcPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                }
                Column {
                    Text(text = "ETH", fontSize = 10.sp, color = TextMuted)
                    Text(text = "$${formatPrice(stats.ethPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                }
                Column {
                    Text(text = if (isRomanUrdu) "Kul Market Cap" else "Total MCap", fontSize = 10.sp, color = TextMuted)
                    Text(text = "$${formatCompactNumber(stats.totalMarketCapUsd)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text(text = "BTC Dominance", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${String.format(Locale.US, "%.1f", stats.btcDominance)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldenPocketGold)
                }
            }
        }
    }
}

@Composable
private fun MarketSentimentCard(sentiment: MarketSentimentSummary, isRomanUrdu: Boolean) {
    val (color, label) = when (sentiment.overallState) {
        MarketImpactDirection.BULLISH -> BullGreen to (if (isRomanUrdu) "BULLISH (Tezi Ka Rujhan)" else "STRONGLY BULLISH")
        MarketImpactDirection.BEARISH -> BearRed to (if (isRomanUrdu) "BEARISH (Mandi Ka Dabao)" else "BEARISH OUTLOOK")
        MarketImpactDirection.UNCERTAIN -> WarningAmber to (if (isRomanUrdu) "UNCERTAIN (Mohtat)" else "UNCERTAIN SENTIMENT")
        MarketImpactDirection.NEUTRAL -> AccentCyan to (if (isRomanUrdu) "NEUTRAL (Mutawazan)" else "NEUTRAL CONSOLIDATION")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRomanUrdu) "AI Market Sentiment Jaiza" else "AI Market Sentiment Engine",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isRomanUrdu) sentiment.summaryUrdu else sentiment.summaryEnglish,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun NewsArticleCard(
    article: NewsArticle,
    isRomanUrdu: Boolean,
    onOpenDetail: () -> Unit
) {
    val (sentimentColor, sentimentText) = when (article.overallSentiment) {
        MarketImpactDirection.BULLISH -> BullGreen to "▲ BULLISH"
        MarketImpactDirection.BEARISH -> BearRed to "▼ BEARISH"
        MarketImpactDirection.UNCERTAIN -> WarningAmber to "◆ UNCERTAIN"
        MarketImpactDirection.NEUTRAL -> TextSecondary to "■ NEUTRAL"
    }

    val timeAgoStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(article.publishedAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Source & Verification Badge & Sentiment Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = article.source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $timeAgoStr",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(sentimentColor.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = sentimentText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = sentimentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Headline
            Text(
                text = article.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Roman Urdu Quick Takeaway
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CryptoSurfaceVariant)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (isRomanUrdu) article.summaryUrdu else article.summaryEnglish,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Asset Impact Mini Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssetMiniTag(asset = "BTC", dir = article.btcImpact.direction)
                AssetMiniTag(asset = "ETH", dir = article.ethImpact.direction)
                AssetMiniTag(asset = "USD", dir = article.usdImpact.direction)
                AssetMiniTag(asset = "Gold", dir = article.goldImpact.direction)

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isRomanUrdu) "Tafseelat →" else "Report →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenPocketGold
                )
            }
        }
    }
}

@Composable
private fun AssetMiniTag(asset: String, dir: MarketImpactDirection) {
    val color = when (dir) {
        MarketImpactDirection.BULLISH -> BullGreen
        MarketImpactDirection.BEARISH -> BearRed
        else -> TextMuted
    }
    val symbol = when (dir) {
        MarketImpactDirection.BULLISH -> "▲"
        MarketImpactDirection.BEARISH -> "▼"
        else -> "■"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$asset $symbol",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// =========================================================================
// SUB-VIEW 2: ECONOMIC EVENTS & MARKET CALENDAR
// =========================================================================

@Composable
private fun EconomicCalendarView(
    events: List<EconomicCalendarEvent>,
    isRomanUrdu: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BullGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRomanUrdu) "Almi Maashi Calendar (Economic Releases)" else "Global Macroeconomic Calendar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRomanUrdu) {
                            "FOMC, CPI, PPI aur NFP jese ahem maashi ailanat jo Bitcoin aur global markets par foran asar andaz hote hain."
                        } else {
                            "Verified scheduled economic data releases driving interest rates and cryptocurrency volatility."
                        },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        items(events, key = { it.id }) { event ->
            val dateStr = SimpleDateFormat("EEEE, MMM dd • HH:mm", Locale.getDefault()).format(Date(event.scheduledTime))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (event.importance == "HIGH") BearRed.copy(alpha = 0.2f) else AccentCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (event.importance == "HIGH") "★ HIGH IMPACT" else "MEDIUM IMPACT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (event.importance == "HIGH") BearRed else AccentCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = event.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Values Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CryptoSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = if (isRomanUrdu) "Pichla (Previous)" else "Previous", fontSize = 9.sp, color = TextMuted)
                            Text(text = event.previousValue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column {
                            Text(text = if (isRomanUrdu) "Tawaqqo (Forecast)" else "Forecast", fontSize = 9.sp, color = TextMuted)
                            Text(text = event.forecastValue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        }
                        Column {
                            Text(text = if (isRomanUrdu) "Asli (Actual)" else "Actual", fontSize = 9.sp, color = TextMuted)
                            Text(text = event.actualValue, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenPocketGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isRomanUrdu) event.urduExplanation else event.relevance,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =========================================================================
// SUB-VIEW 3: FINANCIAL LEARNING CENTER (NO QUIZZES, PURE ROMAN URDU)
// =========================================================================

@Composable
private fun FinancialLearningCenterView(
    concepts: List<FinancialConcept>,
    selectedCategory: ConceptCategory?,
    searchQuery: String,
    isRomanUrdu: Boolean,
    onSelectCategory: (ConceptCategory?) -> Unit,
    onUpdateSearch: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = GoldenPocketGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRomanUrdu) "Financial Learning Center (Maliyati Taleem)" else "Financial Learning Center",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRomanUrdu) {
                            "Aik aam shakhs aur bachon ki tarah asaan Roman Urdu me samjhein ke dunya ki maeeshat, banks, inflation aur crypto kaisy kaam karti hai."
                        } else {
                            "Master global financial concepts, banking, inflation, and crypto mechanics in ultra-simple plain Roman Urdu with real-life examples."
                        },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onUpdateSearch,
                placeholder = {
                    Text(
                        text = if (isRomanUrdu) "Koi bhi concept talash karein (CPI, Halving, Whale, Leverage...)" else "Search concepts (CPI, Halving, Whale, Leverage...)",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onUpdateSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldenPocketGold,
                    unfocusedBorderColor = CryptoCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CryptoSurface,
                    unfocusedContainerColor = CryptoSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text(if (isRomanUrdu) "Tamam (${concepts.size})" else "All (${concepts.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldenPocketGold.copy(alpha = 0.25f),
                        selectedLabelColor = GoldenPocketGold
                    )
                )

                FilterChip(
                    selected = selectedCategory == ConceptCategory.ECONOMIC_CONCEPTS,
                    onClick = { onSelectCategory(ConceptCategory.ECONOMIC_CONCEPTS) },
                    label = { Text(if (isRomanUrdu) "Maeeshat & Fed (22)" else "Macro (22)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BullGreen.copy(alpha = 0.25f),
                        selectedLabelColor = BullGreen
                    )
                )

                FilterChip(
                    selected = selectedCategory == ConceptCategory.CRYPTO_CONCEPTS,
                    onClick = { onSelectCategory(ConceptCategory.CRYPTO_CONCEPTS) },
                    label = { Text(if (isRomanUrdu) "Crypto & Trading (28)" else "Crypto (28)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AccentCyan
                    )
                )
            }
        }

        // Concept Cards (Expandable with Real-life Example & Impact)
        items(concepts, key = { it.id }) { concept ->
            ExpandableConceptCard(concept = concept, isRomanUrdu = isRomanUrdu)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExpandableConceptCard(
    concept: FinancialConcept,
    isRomanUrdu: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpanded) GoldenPocketGold.copy(alpha = 0.6f) else CryptoCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = concept.term,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenPocketGold
                        )
                        if (concept.fullForm.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${concept.fullForm})",
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = concept.simpleUrduMeaning,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = TextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = CryptoCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Rozmarra Zindagi Ki Misaal
                    DetailSectionBox(
                        title = if (isRomanUrdu) "🏠 Rozmarra Zindagi Ki Misaal (Everyday Example):" else "🏠 Everyday Real-Life Example:",
                        content = concept.everydayExample,
                        titleColor = AccentCyan
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Yeh Kyun Ahem Hai
                    DetailSectionBox(
                        title = if (isRomanUrdu) "💡 Yeh Kyun Zaroori Hai (Why It Matters):" else "💡 Why It Matters:",
                        content = concept.whyItMatters,
                        titleColor = GoldenPocketGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Market aur Crypto Par Asar
                    DetailSectionBox(
                        title = if (isRomanUrdu) "📈 Market aur Crypto Par Asar (Impact):" else "📈 Market & Crypto Impact:",
                        content = concept.marketImpactExplanation,
                        titleColor = BullGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Trader Ko Kya Dekhna Chahiye
                    DetailSectionBox(
                        title = if (isRomanUrdu) "👁 Trader Ko Kya Dekhna Chahiye (Watchlist):" else "👁 What A Trader Should Monitor:",
                        content = concept.whatTraderShouldWatch,
                        titleColor = WarningAmber
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailSectionBox(title: String, content: String, titleColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = content, fontSize = 11.sp, color = TextPrimary, lineHeight = 16.sp)
        }
    }
}

// =========================================================================
// SUB-VIEW 4: ARTICLE DETAIL MODAL (FULL 21-POINT REPORT)
// =========================================================================

@Composable
private fun ArticleDetailModal(
    article: NewsArticle,
    isRomanUrdu: Boolean,
    onDismiss: () -> Unit,
    onOpenLink: (String) -> Unit
) {
    val pubStr = SimpleDateFormat("EEEE, MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(article.publishedAt))
    val retStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(article.retrievedAt))

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text(if (isRomanUrdu) "Band Karein" else "Close Report", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (article.url.isNotBlank()) {
                OutlinedButton(
                    onClick = { onOpenLink(article.url) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = GoldenPocketGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRomanUrdu) "Asli Khabar Kholein" else "Original Article", color = GoldenPocketGold, fontSize = 11.sp)
                }
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRomanUrdu) "AI Market Impact Report" else "AI Market Impact Report",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Headline
                item {
                    Text(text = article.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                // 2. Meta (Source, Time, Verification Status)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Source: ${article.source}", fontSize = 10.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                            Text(text = "Published: $pubStr", fontSize = 9.sp, color = TextMuted)
                            Text(text = "Retrieved: $retStr", fontSize = 9.sp, color = TextMuted)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BullGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isRomanUrdu) article.verificationStatus.labelUrdu else article.verificationStatus.labelEn,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullGreen
                            )
                        }
                    }
                }

                // 3. Simple Roman Urdu Summary
                item {
                    DetailSectionBox(
                        title = if (isRomanUrdu) "📝 Asaan Roman Urdu Khulasa (Summary):" else "📝 Simple Summary:",
                        content = article.summaryUrdu,
                        titleColor = GoldenPocketGold
                    )
                }

                // 4. Important Facts & Figures
                if (article.importantFacts.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CryptoSurfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (isRomanUrdu) "📌 Ahem Haqaiq aur Numbers:" else "📌 Key Facts & Figures:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            article.importantFacts.forEach { fact ->
                                Text(text = "• $fact", fontSize = 11.sp, color = TextPrimary, lineHeight = 15.sp)
                            }
                        }
                    }
                }

                // 5. Economic Values (if available)
                article.economicValues?.let { econ ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CryptoSurfaceVariant)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = econ.indicatorName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Previous: ${econ.previousValue ?: "--"}", fontSize = 10.sp, color = TextMuted)
                            }
                            Column {
                                Text(text = "Forecast: ${econ.forecastValue ?: "--"}", fontSize = 10.sp, color = AccentCyan)
                                Text(text = "Actual: ${econ.actualValue ?: "--"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                            }
                        }
                    }
                }

                // 6. AI Multi-Asset Impact Assessment
                item {
                    Text(
                        text = if (isRomanUrdu) "🌐 Assets Par Mumkina Asraat (Market Impact):" else "🌐 Cross-Asset Impact Matrix:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                item {
                    AssetImpactCard(impact = article.btcImpact, isRomanUrdu = isRomanUrdu)
                }
                item {
                    AssetImpactCard(impact = article.ethImpact, isRomanUrdu = isRomanUrdu)
                }
                item {
                    AssetImpactCard(impact = article.altcoinsImpact, isRomanUrdu = isRomanUrdu)
                }
                item {
                    AssetImpactCard(impact = article.usdImpact, isRomanUrdu = isRomanUrdu)
                }
                item {
                    AssetImpactCard(impact = article.goldImpact, isRomanUrdu = isRomanUrdu)
                }

                // 7. Time Horizons (Short, Medium, Long)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CryptoSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(text = if (isRomanUrdu) "⏱ Waqti Asraat (Time Horizons):" else "⏱ Time Horizons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenPocketGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Short-Term (1-24h): ${article.shortTermImpact}", fontSize = 10.sp, color = TextPrimary)
                        Text(text = "• Medium-Term (Weeks): ${article.mediumTermImpact}", fontSize = 10.sp, color = TextPrimary)
                        Text(text = "• Long-Term (Macro): ${article.longTermImpact}", fontSize = 10.sp, color = TextPrimary)
                    }
                }

                // 8. Risk & Confidence Level
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Risk Level: ${article.riskLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                        Text(text = "Confidence: ${article.confidenceLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    }
                }

                // 9. What Traders Should Monitor Next
                item {
                    DetailSectionBox(
                        title = if (isRomanUrdu) "👁 Traders Ko Agay Kya Dekhna Chahiye:" else "👁 What Traders Should Monitor Next:",
                        content = article.whatToMonitorNext,
                        titleColor = AccentCyan
                    )
                }

                // 10. Mandatory Educational Disclaimer
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BearRed.copy(alpha = 0.12f))
                            .border(1.dp, BearRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (isRomanUrdu) {
                                "⚠️ Zaroori Wazaahat: Ye analysis educational aur informational purpose ke liye hai. Crypto aur financial markets mein risk hota hai. Kisi bhi trade se pehle apni research aur risk management zaroor karein."
                            } else {
                                "⚠️ Educational Disclaimer: This analysis is for educational and informational purposes only. Crypto and financial markets contain substantial risk. Always conduct independent research and risk management before trading."
                            },
                            fontSize = 10.sp,
                            color = TextPrimary,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = CryptoSurface
    )
}

@Composable
private fun AssetImpactCard(impact: AssetImpact, isRomanUrdu: Boolean) {
    val (color, badge) = when (impact.direction) {
        MarketImpactDirection.BULLISH -> BullGreen to "BULLISH ▲"
        MarketImpactDirection.BEARISH -> BearRed to "BEARISH ▼"
        MarketImpactDirection.UNCERTAIN -> WarningAmber to "UNCERTAIN ◆"
        MarketImpactDirection.NEUTRAL -> TextMuted to "NEUTRAL ■"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CryptoSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = impact.assetName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (isRomanUrdu) impact.reasonUrdu else impact.reasonEnglish,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

// =========================================================================
// SUB-VIEW 5: FREE USER LOCKED PREVIEW
// =========================================================================

@Composable
private fun FreeUserLockedNewsView(
    isRomanUrdu: Boolean,
    onOpenSubscription: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(GoldenPocketGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked Feature",
                tint = GoldenPocketGold,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRomanUrdu) "Available for Paid Members Only" else "Available for Paid Members Only",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isRomanUrdu) {
                "News & Market Impact Analyzer aur Financial Learning Center sirf Golden, Silver aur Bronze VIP members ke liye makhsoos hai."
            } else {
                "The News & Market Impact Analyzer and Financial Learning Center are exclusively available for Golden, Silver, and Bronze subscribers."
            },
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Feature Highlight Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isRomanUrdu) "VIP Membership Me Kya Shamil Hai:" else "What You Unlock with VIP Access:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenPocketGold
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeatureBullet(text = "Almi Financial Khabrain (GDELT 2.0 Real-Time Intelligence)")
                FeatureBullet(text = "AI Market Impact Analyzer (BTC, ETH, Altcoins, USD, Sona)")
                FeatureBullet(text = "Upcoming Macro Economic Calendar (CPI, FOMC, NFP, GDP)")
                FeatureBullet(text = "Financial Learning Center (50 Bunyadi Maashi Usool Asaan Roman Urdu Me)")
                FeatureBullet(text = "Zero Quizzes — Pure Reading & Everyday Examples")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenSubscription,
            colors = ButtonDefaults.buttonColors(containerColor = GoldenPocketGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_upgrade_news_access")
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRomanUrdu) "VIP Plan Upgrade Karein" else "Upgrade to Access News",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BullGreen, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 11.sp, color = TextPrimary)
    }
}

private fun formatPrice(p: Double): String {
    return when {
        p >= 1000.0 -> String.format(Locale.US, "%,.2f", p)
        p >= 1.0 -> String.format(Locale.US, "%.2f", p)
        else -> String.format(Locale.US, "%.4f", p)
    }
}

private fun formatCompactNumber(number: Double): String {
    return when {
        number >= 1_000_000_000_000.0 -> String.format(Locale.US, "%.2fT", number / 1_000_000_000_000.0)
        number >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", number / 1_000_000_000.0)
        number >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", number / 1_000_000.0)
        else -> String.format(Locale.US, "%.0f", number)
    }
}
