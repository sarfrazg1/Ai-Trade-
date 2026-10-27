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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun BeginnerGuideScreen(
    developerName: String = "Sarfaraz",
    developerWhatsapp: String = "",
    onUpdateDeveloperInfo: (String, String) -> Unit = { _, _ -> },
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
        // App Developer & WhatsApp Support Card
        DeveloperProfileCard(
            name = developerName,
            whatsapp = developerWhatsapp,
            onEditClick = { showEditDialog = true },
            onChatClick = { number ->
                val clean = number.replace(Regex("[^0-9]"), "")
                val url = if (clean.isNotEmpty()) {
                    "https://wa.me/$clean?text=Hello%20$developerName%2C%20I%20am%20using%20Smart%20Trade%20AI%20app!"
                } else {
                    "https://wa.me/?text=Hello%20$developerName%2C%20I%20am%20using%20Smart%20Trade%20AI%20app!"
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trader Guide & System Manual",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Easy English guide to understand Smart Money Concepts, confluence rules, and safety.",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. How the Confluence Score Works
        GuideSectionCard(
            title = "1. How the 0–100 Confluence Score Works",
            icon = Icons.Default.TrendingUp,
            accentColor = BullGreen
        ) {
            GuideParagraph("• 65 to 100 = VALID SETUP: When multiple independent indicators (Trend, SMC, RSI, Funding, Fibonacci) point in the same direction.")
            GuideParagraph("• Below 65 = WAIT (No Trade): Most market hours are choppy noise. Protecting your capital by waiting is the mark of a profitable trader.")
            GuideParagraph("• Multi-Timeframe Rule: If 4H is Bearish and 15m is bouncing, the engine strictly outputs WAIT. Never trade against higher-timeframe order flow.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Smart Money Concepts (SMC) in Plain English
        GuideSectionCard(
            title = "2. Smart Money Concepts (SMC) Explained",
            icon = Icons.Default.Lightbulb,
            accentColor = AccentCyan
        ) {
            GuideParagraph("• BOS (Break of Structure): Price cleanly breaks and closes beyond the previous high (bullish) or low (bearish). Confirms the trend is continuing.")
            GuideParagraph("• CHoCH (Change of Character): The first structural shift where price breaks the opposite pivot (e.g., lower high broken upward). Signals potential trend reversal.")
            GuideParagraph("• Order Block (OB): The last opposite candle before a sharp impulsive move that broke market structure. Serves as high-probability institutional demand or supply.")
            GuideParagraph("• FVG (Fair Value Gap): A 3-candle imbalance leaving an unfilled price pocket. Market usually gravitates back to rebalance this zone.")
            GuideParagraph("• Liquidity Sweep: A quick wick piercing previous highs/lows to trigger stop orders before immediately reversing.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Fibonacci Golden Pocket
        GuideSectionCard(
            title = "3. Fibonacci Retracement & Extensions",
            icon = Icons.Default.Info,
            accentColor = WarningAmber
        ) {
            GuideParagraph("• 50% & 61.8% Retracement: Healthy pullbacks in a strong trend often bounce near the 50% to 61.8% region (the 'Golden Pocket').")
            GuideParagraph("• Extensions (127.2% & 161.8%): Mathematical profit targets used for Take Profit 2 and 3 when price enters price discovery.")
            GuideParagraph("• Safety Notice: Fibonacci levels are dynamic reference zones, never guaranteed turning points.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Funding Rate & Open Interest
        GuideSectionCard(
            title = "4. Futures Derivatives Metrics",
            icon = Icons.Default.TrendingUp,
            accentColor = AccentCyan
        ) {
            GuideParagraph("• Funding Rate: Fee paid between longs and shorts every 8 hours. When heavily positive (>0.03%), longs are crowded and vulnerable to long squeezes. When negative, shorts are crowded and short squeezes are common.")
            GuideParagraph("• Open Interest (OI): Total number of active derivative contracts. Rising OI with rising price = strong fresh buying.")
            GuideParagraph("• Long/Short Ratio: Proportion of net long accounts vs short accounts.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Risk Management & Leverage Dangers
        GuideSectionCard(
            title = "5. Capital Preservation & Leverage",
            icon = Icons.Default.Shield,
            accentColor = BearRed
        ) {
            GuideParagraph("• The 1% - 2% Rule: Never risk more than 1% to 2% of your account balance on a single trade.")
            GuideParagraph("• Why High Leverage is Dangerous: 20x leverage means a 5% move against you will liquidate 100% of your margin! Keep leverage low (2x - 5x) for safety.")
            GuideParagraph("• Stop Loss is Mandatory: Every valid signal calculates an invalidation price. If price hits the stop-loss, accept the small loss and preserve your account.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. How to verify data is updating
        GuideSectionCard(
            title = "6. Verifying Live Market Data",
            icon = Icons.Default.Check,
            accentColor = BullGreen
        ) {
            GuideParagraph("1. Check the timestamp in the top right header (e.g. 19:35:12) and connection badge.")
            GuideParagraph("2. Tap the Refresh button in the top right at any time to pull fresh candlestick and ticker data.")
            GuideParagraph("3. Compare the live price on the dashboard with Binance or Bybit in real time.")
            GuideParagraph("4. All market data is 100% free and fetched from public endpoints with multi-tier failover.")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showEditDialog) {
        var nameInput by remember { mutableStateOf(developerName) }
        var whatsappInput by remember { mutableStateOf(developerWhatsapp) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Developer Contact Details", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        "Set your name and WhatsApp number to display inside the app and allow 1-tap WhatsApp chat:",
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
                        label = { Text("WhatsApp Number (with Country Code)") },
                        placeholder = { Text("+92 300 1234567") },
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
                    Text("Save Details", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
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
                            text = if (whatsapp.isNotEmpty()) "WhatsApp: $whatsapp" else "WhatsApp not configured",
                            fontSize = 11.sp,
                            color = if (whatsapp.isNotEmpty()) AccentCyan else TextMuted
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
                    text = if (whatsapp.isNotEmpty()) "Chat on WhatsApp ($whatsapp)" else "Connect on WhatsApp",
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
    accentColor: androidx.compose.ui.graphics.Color,
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
