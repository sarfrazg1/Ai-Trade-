package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun BeginnerGuideScreen(
    developerName: String = "Rao Muhammad Sarfraz",
    developerWhatsapp: String = "+923360074976",
    isRomanUrdu: Boolean = false,
    onUpdateDeveloperInfo: (String, String) -> Unit = { _, _ -> },
    onOpenSubscription: () -> Unit = {},
    onOpenInfluencerPortal: () -> Unit = {},
    onOpenAdminPortal: () -> Unit = {},
    onOpenLegal: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        // App Developer & WhatsApp Support Card (About Developer)
        DeveloperProfileCard(
            name = developerName.ifEmpty { "Rao Muhammad Sarfraz" },
            whatsapp = developerWhatsapp.ifEmpty { "+923360074976" },
            isRomanUrdu = isRomanUrdu,
            onEditClick = { showEditDialog = true },
            onChatClick = { number ->
                val clean = number.replace(Regex("[^0-9]"), "")
                val targetNumber = if (clean.isNotEmpty()) clean else "923360074976"
                val greeting = if (isRomanUrdu) {
                    "Assalam-o-Alaikum%20Rao%20Sarfraz%2C%20Smart%20Trade%20AI%20app%20k%20bare%20me%20rabta%20kiya%20hai"
                } else {
                    "Hello%20Rao%20Muhammad%20Sarfraz%2C%20I%20am%20using%20Smart%20Trade%20AI%20app"
                }
                val url = "https://wa.me/$targetNumber?text=$greeting"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subscription & Partner Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenSubscription,
                colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "★ Plans / Upgrade" else "★ Plans / Upgrade",
                    fontSize = 11.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onOpenInfluencerPortal,
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "Influencer Portal" else "Influencer (20%)",
                    fontSize = 11.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.OutlinedButton(
                onClick = onOpenAdminPortal,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "⚙ Admin Panel" else "⚙ Admin Panel",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            androidx.compose.material3.OutlinedButton(
                onClick = { onOpenLegal("DISCLAIMER") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "📜 Legal & Disclaimer" else "📜 Legal / Disclaimer",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRomanUrdu) "Trader Guide aur Rehnumai Manual" else "Trader Guide & System Manual",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = if (isRomanUrdu) {
                "Smart Money Concepts (SMC), Confluence Rules aur Risk Management ki asaan Roman Urdu me tafseelat."
            } else {
                "Easy English guide to understand Smart Money Concepts, confluence rules, and safety."
            },
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Confluence Score
        GuideSectionCard(
            title = if (isRomanUrdu) "1. Confluence Score (0–100) Kaisy Kaam Karta Hai" else "1. How the 0–100 Confluence Score Works",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = BullGreen
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 65 se 100 = VALID SETUP (Kamyab Trade): Jab tamam ahem isharay (Trend, SMC, RSI, Funding Rate, Fibonacci) ek hi simat me mutafiq hon.")
                GuideParagraph("• 65 se Kam = WAIT (Sabar Karein): Aksar market choppy (ghair wazeh) hoti hai. Sabar se intazar karna kamyab aur professional trader ki nishani hai.")
                GuideParagraph("• Multi-Timeframe Qanoon: Agar 4H Bearish hai aur 15m thora bounce kar raha ho to engine sakhti se WAIT signal dega. Bari timeframe k mukhalif trade hargiz na lein.")
            } else {
                GuideParagraph("• 65 to 100 = VALID SETUP: When multiple independent indicators (Trend, SMC, RSI, Funding, Fibonacci) point in the same direction.")
                GuideParagraph("• Below 65 = WAIT (No Trade): Most market hours are choppy noise. Protecting your capital by waiting is the mark of a profitable trader.")
                GuideParagraph("• Multi-Timeframe Rule: If 4H is Bearish and 15m is bouncing, the engine strictly outputs WAIT. Never trade against higher-timeframe order flow.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Smart Money Concepts (SMC)
        GuideSectionCard(
            title = if (isRomanUrdu) "2. Smart Money Concepts (SMC) ki Wazaahat" else "2. Smart Money Concepts (SMC) Explained",
            icon = Icons.Default.Lightbulb,
            accentColor = AccentCyan
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• BOS (Break of Structure): Jab market pichle swing high ya low ko tor kar candle close kar de. Iska matlab trend jari rehne ki tasdeeq ho gayi hai.")
                GuideParagraph("• CHoCH (Change of Character): Trend badalne ka pehla ishara jab market pehli martaba mukhalif structure break kare.")
                GuideParagraph("• Order Block (OB): Bare institutions (Whales/Banks) ka aakhri buying ya selling zone jaha se bari raftaar se movement aayi thi. Yeh mazboot support/resistance ka kaam karta hai.")
                GuideParagraph("• FVG (Fair Value Gap): 3 candles k darmiyan price ka khala (imbalance) jisy market wapis bharne aati hai.")
                GuideParagraph("• Liquidity Sweep: Jab market pichle high/low ko wick maar kar toray ta k retail Stop Loss trigger hon aur foran wapis palat jaye.")
            } else {
                GuideParagraph("• BOS (Break of Structure): Price cleanly breaks and closes beyond the previous high (bullish) or low (bearish). Confirms the trend is continuing.")
                GuideParagraph("• CHoCH (Change of Character): The first structural shift where price breaks the opposite pivot (e.g., lower high broken upward). Signals potential trend reversal.")
                GuideParagraph("• Order Block (OB): The last opposite candle before a sharp impulsive move that broke market structure. Serves as institutional demand or supply.")
                GuideParagraph("• FVG (Fair Value Gap): A 3-candle imbalance leaving an unfilled price pocket. Market usually gravitates back to rebalance this zone.")
                GuideParagraph("• Liquidity Sweep: A quick wick piercing previous highs/lows to trigger stop orders before immediately reversing.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Fibonacci Golden Pocket
        GuideSectionCard(
            title = if (isRomanUrdu) "3. Fibonacci Golden Pocket (61.8% - 78.6%)" else "3. Fibonacci Retracement & Extensions",
            icon = Icons.Default.Info,
            accentColor = WarningAmber
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 50% aur 61.8% Retracement: Mazboot trend k doran market healthy pullback leti hai aur aksar 50% se 61.8% (Golden Pocket) se bounce karti hai.")
                GuideParagraph("• Extensions (127.2% aur 161.8%): Naye targets k liye mathematical Take Profit levels.")
                GuideParagraph("• Ehtiyat: Fibonacci levels reference zones hain, yeh 100% guarantee nahi hotay, is liye confirmation zaroori hai.")
            } else {
                GuideParagraph("• 50% & 61.8% Retracement: Healthy pullbacks in a strong trend often bounce near the 50% to 61.8% region (the 'Golden Pocket').")
                GuideParagraph("• Extensions (127.2% & 161.8%): Mathematical profit targets used for Take Profit 2 and 3 when price enters price discovery.")
                GuideParagraph("• Safety Notice: Fibonacci levels are dynamic reference zones, never guaranteed turning points.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Futures Funding Rate & OI
        GuideSectionCard(
            title = if (isRomanUrdu) "4. Futures Derivatives: Funding Rate aur Open Interest" else "4. Futures Derivatives Metrics",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = AccentCyan
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• Funding Rate: Har 8 ghante baad Longs aur Shorts k darmiyan tabadla. Agar bohot positive (>0.03%) ho to Long squeeze ka khatra hota hai. Agar negative ho to Short squeeze ka imkan hota hai.")
                GuideParagraph("• Open Interest (OI): Kul active futures contracts. Agar price barhne k sath OI barh raha ho to naya sarmaya market me enter ho raha hai.")
                GuideParagraph("• Long/Short Ratio: Net buyers aur sellers ka tanasub.")
            } else {
                GuideParagraph("• Funding Rate: Fee paid between longs and shorts every 8 hours. When heavily positive (>0.03%), longs are vulnerable to long squeezes. When negative, shorts are crowded.")
                GuideParagraph("• Open Interest (OI): Total number of active derivative contracts. Rising OI with rising price = strong fresh buying.")
                GuideParagraph("• Long/Short Ratio: Proportion of net long accounts vs short accounts.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Risk Management & Leverage
        GuideSectionCard(
            title = if (isRomanUrdu) "5. Sarmaya Bachao aur Leverage ka Khatra" else "5. Capital Preservation & Leverage",
            icon = Icons.Default.Shield,
            accentColor = BearRed
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 1% se 2% Risk Formula: Kisi bhi ek trade me apne kul balance ka 1% ya 2% se ziada daao per na lagayein.")
                GuideParagraph("• Bari Leverage (20x, 50x) ka Nuqsan: 20x leverage me agar market sirf 5% mukhalif chali jaye to apka 100% margin khatam ho jayega! Hamesha 2x se 5x leverage istemal karein.")
                GuideParagraph("• Stop Loss Lazmi Hai: Har valid signal Stop Loss faraham karta hai. Agar Stop Loss hit ho jaye to chota nuqsan qabool karein ta k bara nuqsan na ho.")
            } else {
                GuideParagraph("• The 1% - 2% Rule: Never risk more than 1% to 2% of your account balance on a single trade.")
                GuideParagraph("• Why High Leverage is Dangerous: 20x leverage means a 5% move against you will liquidate 100% of your margin! Keep leverage low (2x - 5x) for safety.")
                GuideParagraph("• Stop Loss is Mandatory: Every valid signal calculates an invalidation price. If price hits the stop-loss, accept the small loss and preserve your account.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Live Chart Paper Trading
        GuideSectionCard(
            title = if (isRomanUrdu) "6. Live Chart per Paper Trading (Binance / Gate.io Style)" else "6. Live Chart Paper Trading (Binance / Gate.io Style)",
            icon = Icons.Default.Check,
            accentColor = BullGreen
        ) {
            if (isRomanUrdu) {
                GuideParagraph("1. 'Chart' tab me ja kar live candlestick chart per 'Buy / Long' ya 'Sell / Short' button dabayein.")
                GuideParagraph("2. Chart k upar foran Cyan rang ki Entry line, Green TP line aur Red Stop Loss line ban jayegi.")
                GuideParagraph("3. Chart k nechy live PnL (Nafa/Nuqsan) USD aur % me chalta nazar aayega.")
                GuideParagraph("4. Kisi bhi waqt 1-tap 'Market Close' daba kar trade ko band kiya ja sakta hai.")
            } else {
                GuideParagraph("1. Go to 'Chart' tab and tap 'Buy / Long' or 'Sell / Short' below the candlestick chart.")
                GuideParagraph("2. Real-time Entry (Cyan), TP1 (Green dashed), and SL (Red dashed) lines appear directly on the chart canvas.")
                GuideParagraph("3. A live PnL bar below the chart continuously tracks your position profit and loss.")
                GuideParagraph("4. Use the 1-tap 'Market Close' button to exit your position at current market price anytime.")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showEditDialog) {
        var nameInput by remember { mutableStateOf(developerName) }
        var whatsappInput by remember { mutableStateOf(developerWhatsapp) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (isRomanUrdu) "Developer Rabta Details" else "Developer Contact Details", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        if (isRomanUrdu) {
                            "Developer ka name aur WhatsApp number set karein ta k app me 1-tap chat button kaam kare:"
                        } else {
                            "Set your name and WhatsApp number to display inside the app and allow 1-tap WhatsApp chat:"
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Developer Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = whatsappInput,
                        onValueChange = { whatsappInput = it },
                        label = { Text("WhatsApp Number") },
                        placeholder = { Text("+923360074976") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDeveloperInfo(nameInput, whatsappInput)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen, contentColor = Color.Black)
                ) {
                    Text(if (isRomanUrdu) "Mehfooz Karein" else "Save Details", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(if (isRomanUrdu) "Mansookh" else "Cancel", color = TextSecondary)
                }
            },
            containerColor = CryptoSurface
        )
    }
}

@Composable
private fun DeveloperProfileCard(
    name: String,
    whatsapp: String,
    isRomanUrdu: Boolean,
    onEditClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BullGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BullGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Developer",
                            tint = BullGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DEVELOPER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullGreen,
                                modifier = Modifier
                                    .background(BullGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "WhatsApp: $whatsapp",
                            fontSize = 11.sp,
                            color = AccentCyan
                        )
                    }
                }

                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Developer Info",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onChatClick(whatsapp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BullGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRomanUrdu) "WhatsApp per Rabta Karein ($whatsapp)" else "Chat on WhatsApp ($whatsapp)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun GuideSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun GuideParagraph(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 16.sp,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}
