package com.example.data.remote

import com.example.domain.model.Candle
import com.example.domain.model.FuturesStats
import com.example.domain.model.Timeframe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class BinanceClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val userAgent = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"

    suspend fun fetchKlines(symbol: String, timeframe: Timeframe, limit: Int = 100): Result<List<Candle>> =
        withContext(Dispatchers.IO) {
            // Tier 1: Binance Futures API
            val futuresUrl = "https://fapi.binance.com/fapi/v1/klines?symbol=$symbol&interval=${timeframe.key}&limit=$limit"
            val res1 = executeBinanceKlines(futuresUrl)
            if (res1.isSuccess && res1.getOrNull()?.isNotEmpty() == true) {
                return@withContext res1
            }

            // Tier 2: Binance Vision public data mirror (Unblocked globally)
            val visionUrl = "https://data-api.binance.vision/api/v3/klines?symbol=$symbol&interval=${timeframe.key}&limit=$limit"
            val res2 = executeBinanceKlines(visionUrl)
            if (res2.isSuccess && res2.getOrNull()?.isNotEmpty() == true) {
                return@withContext res2
            }

            // Tier 3: Binance Spot API
            val spotUrl = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=${timeframe.key}&limit=$limit"
            val res3 = executeBinanceKlines(spotUrl)
            if (res3.isSuccess && res3.getOrNull()?.isNotEmpty() == true) {
                return@withContext res3
            }

            // Tier 4: Bybit Linear Perpetual Futures public API (Works unrestricted globally)
            val bybitInterval = when (timeframe) {
                Timeframe.FIVE_MIN -> "5"
                Timeframe.FIFTEEN_MIN -> "15"
                Timeframe.ONE_HOUR -> "60"
                Timeframe.FOUR_HOUR -> "240"
                Timeframe.DAILY -> "D"
            }
            val bybitUrl = "https://api.bybit.com/v5/market/kline?category=linear&symbol=$symbol&interval=$bybitInterval&limit=$limit"
            val res4 = executeBybitKlines(bybitUrl)
            if (res4.isSuccess && res4.getOrNull()?.isNotEmpty() == true) {
                return@withContext res4
            }

            Result.failure(IOException("All public market data sources failed. Please check internet connection."))
        }

    private fun executeBinanceKlines(url: String): Result<List<Candle>> {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .header("Cache-Control", "no-cache")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return Result.failure(IOException("HTTP error ${response.code}"))
            }

            val body = response.body?.string() ?: return Result.failure(IOException("Empty body"))
            val jsonArray = JSONArray(body)
            val candles = mutableListOf<Candle>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONArray(i)
                val openTime = item.getLong(0)
                val open = item.getString(1).toDoubleOrNull() ?: 0.0
                val high = item.getString(2).toDoubleOrNull() ?: 0.0
                val low = item.getString(3).toDoubleOrNull() ?: 0.0
                val close = item.getString(4).toDoubleOrNull() ?: 0.0
                val volume = item.getString(5).toDoubleOrNull() ?: 0.0

                candles.add(
                    Candle(
                        time = openTime,
                        open = open,
                        high = high,
                        low = low,
                        close = close,
                        volume = volume
                    )
                )
            }
            Result.success(candles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executeBybitKlines(url: String): Result<List<Candle>> {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return Result.failure(IOException("Bybit HTTP ${response.code}"))
            }

            val body = response.body?.string() ?: return Result.failure(IOException("Empty body"))
            val root = JSONObject(body)
            val resultObj = root.optJSONObject("result") ?: return Result.failure(IOException("No result"))
            val list = resultObj.optJSONArray("list") ?: return Result.failure(IOException("No candle list"))

            val candles = mutableListOf<Candle>()
            // Bybit returns newest first, so parse and reverse
            for (i in 0 until list.length()) {
                val item = list.getJSONArray(i)
                // Bybit format: [startTime, openPrice, highPrice, lowPrice, closePrice, volume, turnover]
                val openTime = item.getString(0).toLongOrNull() ?: 0L
                val open = item.getString(1).toDoubleOrNull() ?: 0.0
                val high = item.getString(2).toDoubleOrNull() ?: 0.0
                val low = item.getString(3).toDoubleOrNull() ?: 0.0
                val close = item.getString(4).toDoubleOrNull() ?: 0.0
                val volume = item.getString(5).toDoubleOrNull() ?: 0.0

                candles.add(
                    Candle(
                        time = openTime,
                        open = open,
                        high = high,
                        low = low,
                        close = close,
                        volume = volume
                    )
                )
            }
            // Reverse so oldest is first
            candles.reverse()
            Result.success(candles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFuturesStats(symbol: String): Result<FuturesStats> = withContext(Dispatchers.IO) {
        try {
            // Try 1: Binance Futures Ticker & Funding
            val futuresResult = tryBinanceFuturesStats(symbol)
            if (futuresResult != null) return@withContext Result.success(futuresResult)

            // Try 2: Binance Vision Ticker (Spot mirror)
            val visionResult = tryBinanceVisionStats(symbol)
            if (visionResult != null) return@withContext Result.success(visionResult)

            // Try 3: Bybit Linear Futures Ticker
            val bybitResult = tryBybitStats(symbol)
            if (bybitResult != null) return@withContext Result.success(bybitResult)

            Result.failure(IOException("Failed to retrieve price statistics"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun tryBinanceFuturesStats(symbol: String): FuturesStats? {
        return try {
            val tickerUrl = "https://fapi.binance.com/fapi/v1/ticker/24hr?symbol=$symbol"
            val req = Request.Builder().url(tickerUrl).header("User-Agent", userAgent).build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return null
            val body = res.body?.string() ?: return null
            val tickerJson = JSONObject(body)

            val lastPrice = tickerJson.optString("lastPrice", "0.0").toDoubleOrNull() ?: 0.0
            val priceChangePercent = tickerJson.optString("priceChangePercent", "0.0").toDoubleOrNull() ?: 0.0
            val high24h = tickerJson.optString("highPrice", "0.0").toDoubleOrNull() ?: 0.0
            val low24h = tickerJson.optString("lowPrice", "0.0").toDoubleOrNull() ?: 0.0
            val volume24h = tickerJson.optString("volume", "0.0").toDoubleOrNull() ?: 0.0

            var markPrice = lastPrice
            var indexPrice = lastPrice
            var fundingRate = 0.0001
            var nextFundingTime = System.currentTimeMillis() + 4 * 3600 * 1000
            var openInterest = 0.0
            var longShortRatio: Double? = null

            // Premium Index
            try {
                val pReq = Request.Builder().url("https://fapi.binance.com/fapi/v1/premiumIndex?symbol=$symbol").header("User-Agent", userAgent).build()
                val pRes = client.newCall(pReq).execute()
                if (pRes.isSuccessful) {
                    val pJson = JSONObject(pRes.body?.string() ?: "{}")
                    markPrice = pJson.optString("markPrice", "$lastPrice").toDoubleOrNull() ?: lastPrice
                    indexPrice = pJson.optString("indexPrice", "$lastPrice").toDoubleOrNull() ?: lastPrice
                    fundingRate = pJson.optString("lastFundingRate", "0.0001").toDoubleOrNull() ?: 0.0001
                    nextFundingTime = pJson.optLong("nextFundingTime", nextFundingTime)
                }
            } catch (_: Exception) {}

            // Open Interest
            try {
                val oiReq = Request.Builder().url("https://fapi.binance.com/fapi/v1/openInterest?symbol=$symbol").header("User-Agent", userAgent).build()
                val oiRes = client.newCall(oiReq).execute()
                if (oiRes.isSuccessful) {
                    val oiJson = JSONObject(oiRes.body?.string() ?: "{}")
                    openInterest = oiJson.optString("openInterest", "0.0").toDoubleOrNull() ?: 0.0
                }
            } catch (_: Exception) {}

            // Long/Short Account Ratio
            try {
                val lsReq = Request.Builder().url("https://fapi.binance.com/futures/data/globalLongShortAccountRatio?symbol=$symbol&period=15m&limit=1").header("User-Agent", userAgent).build()
                val lsRes = client.newCall(lsReq).execute()
                if (lsRes.isSuccessful) {
                    val lsArray = JSONArray(lsRes.body?.string() ?: "[]")
                    if (lsArray.length() > 0) {
                        longShortRatio = lsArray.getJSONObject(0).optString("longShortRatio", "1.0").toDoubleOrNull()
                    }
                }
            } catch (_: Exception) {}

            FuturesStats(
                symbol = symbol,
                lastPrice = lastPrice,
                markPrice = markPrice,
                indexPrice = indexPrice,
                priceChange24h = priceChangePercent,
                high24h = high24h,
                low24h = low24h,
                volume24h = volume24h,
                fundingRate = fundingRate,
                nextFundingTime = nextFundingTime,
                openInterest = openInterest,
                longShortRatio = longShortRatio,
                source = "Binance Futures (fapi)",
                lastUpdated = System.currentTimeMillis(),
                isFallback = false
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun tryBinanceVisionStats(symbol: String): FuturesStats? {
        return try {
            val tickerUrl = "https://data-api.binance.vision/api/v3/ticker/24hr?symbol=$symbol"
            val req = Request.Builder().url(tickerUrl).header("User-Agent", userAgent).build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return null
            val body = res.body?.string() ?: return null
            val tickerJson = JSONObject(body)

            val lastPrice = tickerJson.optString("lastPrice", "0.0").toDoubleOrNull() ?: 0.0
            val priceChangePercent = tickerJson.optString("priceChangePercent", "0.0").toDoubleOrNull() ?: 0.0
            val high24h = tickerJson.optString("highPrice", "0.0").toDoubleOrNull() ?: 0.0
            val low24h = tickerJson.optString("lowPrice", "0.0").toDoubleOrNull() ?: 0.0
            val volume24h = tickerJson.optString("volume", "0.0").toDoubleOrNull() ?: 0.0

            FuturesStats(
                symbol = symbol,
                lastPrice = lastPrice,
                markPrice = lastPrice,
                indexPrice = lastPrice,
                priceChange24h = priceChangePercent,
                high24h = high24h,
                low24h = low24h,
                volume24h = volume24h,
                fundingRate = 0.0001,
                nextFundingTime = System.currentTimeMillis() + 4 * 3600 * 1000,
                openInterest = 0.0,
                longShortRatio = null,
                source = "Binance Vision Mirror",
                lastUpdated = System.currentTimeMillis(),
                isFallback = true
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun tryBybitStats(symbol: String): FuturesStats? {
        return try {
            val url = "https://api.bybit.com/v5/market/tickers?category=linear&symbol=$symbol"
            val req = Request.Builder().url(url).header("User-Agent", userAgent).build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return null
            val body = res.body?.string() ?: return null
            val root = JSONObject(body)
            val resultObj = root.optJSONObject("result") ?: return null
            val list = resultObj.optJSONArray("list") ?: return null
            if (list.length() == 0) return null
            val item = list.getJSONObject(0)

            val lastPrice = item.optString("lastPrice", "0.0").toDoubleOrNull() ?: 0.0
            val markPrice = item.optString("markPrice", "$lastPrice").toDoubleOrNull() ?: lastPrice
            val indexPrice = item.optString("indexPrice", "$lastPrice").toDoubleOrNull() ?: lastPrice
            val priceChangePercent = (item.optString("price24hPcnt", "0.0").toDoubleOrNull() ?: 0.0) * 100.0
            val high24h = item.optString("highPrice24h", "0.0").toDoubleOrNull() ?: 0.0
            val low24h = item.optString("lowPrice24h", "0.0").toDoubleOrNull() ?: 0.0
            val volume24h = item.optString("volume24h", "0.0").toDoubleOrNull() ?: 0.0
            val fundingRate = item.optString("fundingRate", "0.0001").toDoubleOrNull() ?: 0.0001
            val nextFundingTime = item.optString("nextFundingTime", "0").toLongOrNull() ?: (System.currentTimeMillis() + 4 * 3600 * 1000)
            val openInterest = item.optString("openInterest", "0.0").toDoubleOrNull() ?: 0.0

            FuturesStats(
                symbol = symbol,
                lastPrice = lastPrice,
                markPrice = markPrice,
                indexPrice = indexPrice,
                priceChange24h = priceChangePercent,
                high24h = high24h,
                low24h = low24h,
                volume24h = volume24h,
                fundingRate = fundingRate,
                nextFundingTime = nextFundingTime,
                openInterest = openInterest,
                longShortRatio = null,
                source = "Bybit Linear Futures (Global)",
                lastUpdated = System.currentTimeMillis(),
                isFallback = false
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun fetchLivePrice(symbol: String): Double? = withContext(Dispatchers.IO) {
        val clean = symbol.uppercase().trim()
        val prices = fetchLivePrices(setOf(clean))
        prices[clean]
    }

    suspend fun fetchLivePrices(symbols: Set<String>): Map<String, Double> = withContext(Dispatchers.IO) {
        if (symbols.isEmpty()) return@withContext emptyMap()
        val cleanSymbols = symbols.map { it.uppercase().trim() }.filter { it.isNotEmpty() }.toSet()

        // 1. Try Binance Futures Ticker Price
        val futuresPrices = tryFetchBinanceFuturesPrices(cleanSymbols)
        if (futuresPrices.isNotEmpty()) return@withContext futuresPrices

        // 2. Try Binance Vision Ticker Price (Spot mirror, unblocked globally)
        val visionPrices = tryFetchBinanceVisionPrices(cleanSymbols)
        if (visionPrices.isNotEmpty()) return@withContext visionPrices

        // 3. Try Bybit Linear Tickers
        val bybitPrices = tryFetchBybitPrices(cleanSymbols)
        if (bybitPrices.isNotEmpty()) return@withContext bybitPrices

        emptyMap()
    }

    private fun tryFetchBinanceFuturesPrices(symbols: Set<String>): Map<String, Double> {
        return try {
            val url = if (symbols.size == 1) {
                "https://fapi.binance.com/fapi/v1/ticker/price?symbol=${symbols.first()}"
            } else {
                "https://fapi.binance.com/fapi/v1/ticker/price"
            }
            val req = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .header("Cache-Control", "no-cache")
                .build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return emptyMap()
            val body = res.body?.string() ?: return emptyMap()

            val resultMap = HashMap<String, Double>()
            if (body.trimStart().startsWith("{")) {
                val obj = JSONObject(body)
                val sym = obj.optString("symbol").uppercase()
                val price = obj.optString("price", "0.0").toDoubleOrNull() ?: 0.0
                if (price > 0.0) resultMap[sym] = price
            } else if (body.trimStart().startsWith("[")) {
                val arr = JSONArray(body)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val sym = obj.optString("symbol").uppercase()
                    if (symbols.contains(sym)) {
                        val price = obj.optString("price", "0.0").toDoubleOrNull() ?: 0.0
                        if (price > 0.0) resultMap[sym] = price
                    }
                }
            }
            resultMap
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun tryFetchBinanceVisionPrices(symbols: Set<String>): Map<String, Double> {
        return try {
            val url = if (symbols.size == 1) {
                "https://data-api.binance.vision/api/v3/ticker/price?symbol=${symbols.first()}"
            } else {
                "https://data-api.binance.vision/api/v3/ticker/price"
            }
            val req = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .header("Cache-Control", "no-cache")
                .build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return emptyMap()
            val body = res.body?.string() ?: return emptyMap()

            val resultMap = HashMap<String, Double>()
            if (body.trimStart().startsWith("{")) {
                val obj = JSONObject(body)
                val sym = obj.optString("symbol").uppercase()
                val price = obj.optString("price", "0.0").toDoubleOrNull() ?: 0.0
                if (price > 0.0) resultMap[sym] = price
            } else if (body.trimStart().startsWith("[")) {
                val arr = JSONArray(body)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val sym = obj.optString("symbol").uppercase()
                    if (symbols.contains(sym)) {
                        val price = obj.optString("price", "0.0").toDoubleOrNull() ?: 0.0
                        if (price > 0.0) resultMap[sym] = price
                    }
                }
            }
            resultMap
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun tryFetchBybitPrices(symbols: Set<String>): Map<String, Double> {
        return try {
            val url = "https://api.bybit.com/v5/market/tickers?category=linear"
            val req = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return emptyMap()
            val body = res.body?.string() ?: return emptyMap()
            val root = JSONObject(body)
            val resultObj = root.optJSONObject("result") ?: return emptyMap()
            val list = resultObj.optJSONArray("list") ?: return emptyMap()

            val resultMap = HashMap<String, Double>()
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val sym = item.optString("symbol").uppercase()
                if (symbols.contains(sym)) {
                    val price = item.optString("lastPrice", "0.0").toDoubleOrNull() ?: 0.0
                    if (price > 0.0) resultMap[sym] = price
                }
            }
            resultMap
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
