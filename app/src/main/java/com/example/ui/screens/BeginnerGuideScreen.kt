package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    isRomanUrdu: Boolean = false,
    isAdmin: Boolean = false,
    onOpenSubscription: () -> Unit = {},
    onOpenInfluencerPortal: () -> Unit = {},
    onOpenAdminPortal: () -> Unit = {},
    onOpenLegal: (String) -> Unit = {},
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        // Quick Action Buttons
        if (isAdmin) {
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
                        text = if (isRomanUrdu) "★ پلانز / اپ گریڈ" else "★ Plans / Upgrade",
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
                        text = if (isRomanUrdu) "انفلوئنسر پورٹل" else "Influencer (20%)",
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
                        text = if (isRomanUrdu) "⚙ ایڈمن پینل" else "⚙ Admin Panel",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = onOpenAbout,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isRomanUrdu) "ⓘ ایپ معلومات" else "ⓘ About App",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = { onOpenLegal("DISCLAIMER") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRomanUrdu) "📜 قانونی اور ڈس کلیمر" else "📜 Legal / Disclaimer",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        } else {
            // Ordinary users: ONLY public features (Plans / Upgrade, About App, and Legal / Disclaimer)
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
                        text = if (isRomanUrdu) "★ پلانز / اپ گریڈ" else "★ Plans / Upgrade",
                        fontSize = 11.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = onOpenAbout,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isRomanUrdu) "ⓘ ایپ معلومات" else "ⓘ About App",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = { onOpenLegal("DISCLAIMER") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRomanUrdu) "📜 قانونی اور ڈس کلیمر" else "📜 Legal / Disclaimer",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRomanUrdu) "ٹریڈر گائیڈ اور سسٹم مینوئل" else "Trader Guide & System Manual",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = if (isRomanUrdu) {
                "اسمارٹ منی کنسیپٹس (SMC)، کنفلوئنس کے اصول اور رسک مینجمنٹ کی آسان اردو میں تفصیلات۔"
            } else {
                "Easy English guide to understand Smart Money Concepts, confluence rules, and safety."
            },
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Confluence Score
        GuideSectionCard(
            title = if (isRomanUrdu) "1. کنفلوئنس اسکور (0–100) کیسے کام کرتا ہے" else "1. How the 0–100 Confluence Score Works",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = BullGreen
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 65 سے 100 = درست سیٹ اپ (کامیاب ٹریڈ): جب تمام اہم اشارے (ٹرینڈ، SMC، RSI، فنڈنگ ریٹ، فبوناکی) ایک ہی سمت میں متفق ہوں۔")
                GuideParagraph("• 65 سے کم = انتظار کریں (صبر کریں): مارکیٹ اکثر غیر واضح ہوتی ہے۔ صبر سے انتظار کرنا ایک پیشہ ور ٹریڈر کی نشانی ہے۔")
                GuideParagraph("• ملٹی ٹائم فریم اصول: اگر 4H بیرش ہے اور 15M پر باؤنس ہو رہا ہو تو انجن سختی سے انتظار کا سگنل دے گا۔ بڑی ٹائم فریم کے مخالف ٹریڈ ہرگز نہ لیں۔")
            } else {
                GuideParagraph("• 65 to 100 = VALID SETUP: When multiple independent indicators (Trend, SMC, RSI, Funding, Fibonacci) point in the same direction.")
                GuideParagraph("• Below 65 = WAIT (No Trade): Most market hours are choppy noise. Protecting your capital by waiting is the mark of a profitable trader.")
                GuideParagraph("• Multi-Timeframe Rule: If 4H is Bearish and 15m is bouncing, the engine strictly outputs WAIT. Never trade against higher-timeframe order flow.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Smart Money Concepts (SMC)
        GuideSectionCard(
            title = if (isRomanUrdu) "2. اسمارٹ منی کنسیپٹس (SMC) کی وضاحت" else "2. Smart Money Concepts (SMC) Explained",
            icon = Icons.Default.Lightbulb,
            accentColor = AccentCyan
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• BOS (بریک آف اسٹرکچر): جب مارکیٹ پچھلے سوئنگ ہائی یا لو کو توڑ کر کینڈل کلوز کر دے۔ اس کا مطلب ٹرینڈ جاری رہنے کی تصدیق ہو گئی ہے۔")
                GuideParagraph("• CHoCH (چینج آف کیریکٹر): ٹرینڈ بدلنے کا پہلا اشارہ جب مارکیٹ پہلی مرتبہ مخالف اسٹرکچر بریک کرے۔")
                GuideParagraph("• آرڈر بلاک (Order Block): بڑے اداروں (وہیلز / بینکس) کا آخری بائنگ یا سیلنگ زون جہاں سے بڑی رفتار سے حرکت آئی تھی۔ یہ مضبوط سپورٹ / ریزسٹنس کا کام کرتا ہے۔")
                GuideParagraph("• FVG (فیئر ویلیو گیپ): 3 کینڈلز کے درمیان قیمت کا عدم توازن (خلا) جسے مارکیٹ واپس بھرنے آتی ہے۔")
                GuideParagraph("• لیکویڈیٹی سویپ (Liquidity Sweep): جب مارکیٹ پچھلے ہائی / لو کی وک مار کر ریٹیل اسٹاپ لاسز کو ٹریگر کرے اور فوراً پلٹ جائے۔")
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
            title = if (isRomanUrdu) "3. فبوناکی گولڈن پاکٹ (61.8% - 78.6%)" else "3. Fibonacci Retracement & Extensions",
            icon = Icons.Default.Info,
            accentColor = WarningAmber
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 50% اور 61.8% ریٹریسمنٹ: مضبوط ٹرینڈ کے دوران مارکیٹ صحت مند پل بیک لیتی ہے اور اکثر 50% سے 61.8% (گولڈن پاکٹ) سے باؤنس کرتی ہے۔")
                GuideParagraph("• ایکسٹینشنز (127.2% اور 161.8%): نئے اہداف کے لیے ریاضیاتی ٹیک پرافٹ لیولز۔")
                GuideParagraph("• احتیاط: فبوناکی لیولز ریفرنس زونز ہیں، یہ 100% گارنٹی نہیں ہوتے اس لیے کنفرمیشن ضروری ہے۔")
            } else {
                GuideParagraph("• 50% & 61.8% Retracement: Healthy pullbacks in a strong trend often bounce near the 50% to 61.8% region (the 'Golden Pocket').")
                GuideParagraph("• Extensions (127.2% & 161.8%): Mathematical profit targets used for Take Profit 2 and 3 when price enters price discovery.")
                GuideParagraph("• Safety Notice: Fibonacci levels are dynamic reference zones, never guaranteed turning points.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Futures Funding Rate & OI
        GuideSectionCard(
            title = if (isRomanUrdu) "4. فیوچرز ڈیریویٹوز: فنڈنگ ریٹ اور اوپن انٹرسٹ" else "4. Futures Derivatives Metrics",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            accentColor = AccentCyan
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• فنڈنگ ریٹ: ہر 8 گھنٹے بعد لانگز اور شارٹس کے درمیان تبادلہ۔ اگر بہت زیادہ مثبت (>0.03%) ہو تو لانگ اسکوئز کا خطرہ ہوتا ہے۔ اگر منفی ہو تو شارٹ اسکوئز کا امکان ہوتا ہے۔")
                GuideParagraph("• اوپن انٹرسٹ (OI): کل ایکٹو فیوچرز کنٹریکٹس۔ اگر قیمت بڑھنے کے ساتھ OI بڑھ رہا ہو تو نیا سرمایہ مارکیٹ میں داخل ہو رہا ہے۔")
                GuideParagraph("• لانگ / شارٹ ریشو: نیٹ بائرز اور سیلرز کا تناسب۔")
            } else {
                GuideParagraph("• Funding Rate: Fee paid between longs and shorts every 8 hours. When heavily positive (>0.03%), longs are vulnerable to long squeezes. When negative, shorts are crowded.")
                GuideParagraph("• Open Interest (OI): Total number of active derivative contracts. Rising OI with rising price = strong fresh buying.")
                GuideParagraph("• Long/Short Ratio: Proportion of net long accounts vs short accounts.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Risk Management & Leverage
        GuideSectionCard(
            title = if (isRomanUrdu) "5. سرمایہ بچاؤ اور لیوریج کا خطرہ" else "5. Capital Preservation & Leverage",
            icon = Icons.Default.Shield,
            accentColor = BearRed
        ) {
            if (isRomanUrdu) {
                GuideParagraph("• 1% سے 2% رسک فارمولا: کسی بھی ایک ٹریڈ میں اپنے کل بیلنس کا 1% یا 2% سے زیادہ داؤ پر نہ لگائیں۔")
                GuideParagraph("• بڑی لیوریج (20x, 50x) کا نقصان: 20x لیوریج میں اگر مارکیٹ صرف 5% مخالف چلی جائے تو آپ کا 100% مارجن ختم ہو جائے گا! ہمیشہ 2x سے 5x لیوریج استعمال کریں۔")
                GuideParagraph("• اسٹاپ لاس لازمی ہے: ہر ویلڈ سگنل اسٹاپ لاس فراہم کرتا ہے۔ اگر اسٹاپ لاس ہٹ ہو جائے تو چھوٹا نقصان قبول کریں تاکہ بڑا نقصان نہ ہو۔")
            } else {
                GuideParagraph("• The 1% - 2% Rule: Never risk more than 1% to 2% of your account balance on a single trade.")
                GuideParagraph("• Why High Leverage is Dangerous: 20x leverage means a 5% move against you will liquidate 100% of your margin! Keep leverage low (2x - 5x) for safety.")
                GuideParagraph("• Stop Loss is Mandatory: Every valid signal calculates an invalidation price. If price hits the stop-loss, accept the small loss and preserve your account.")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Live Chart Paper Trading
        GuideSectionCard(
            title = if (isRomanUrdu) "6. لائیو چارٹ پر پیپر ٹریڈنگ" else "6. Live Chart Paper Trading (Binance / Gate.io Style)",
            icon = Icons.Default.Check,
            accentColor = BullGreen
        ) {
            if (isRomanUrdu) {
                GuideParagraph("1. 'چارٹ' ٹیب میں جا کر لائیو کینڈل اسٹک چارٹ پر 'خریدیں / لانگ' یا 'بیچیں / شارٹ' بٹن دبائیں۔")
                GuideParagraph("2. چارٹ کے اوپر فوری انٹری لائن، ٹیک پرافٹ اور اسٹاپ لاس لائن بن جائے گی۔")
                GuideParagraph("3. چارٹ کے نیچے لائیو PnL (نفع / نقصان) ڈالر اور فیصد میں چلتا نظر آئے گا۔")
                GuideParagraph("4. کسی بھی وقت 1-ٹیپ مارکیٹ کلوز دبا کر پوزیشن بند کی جا سکتی ہے۔")
            } else {
                GuideParagraph("1. Go to 'Chart' tab and tap 'Buy / Long' or 'Sell / Short' below the candlestick chart.")
                GuideParagraph("2. Real-time Entry (Cyan), TP1 (Green dashed), and SL (Red dashed) lines appear directly on the chart canvas.")
                GuideParagraph("3. A live PnL bar below the chart continuously tracks your position profit and loss.")
                GuideParagraph("4. Use the 1-tap 'Market Close' button to exit your position at current market price anytime.")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subtle About link near the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenAbout() }
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRomanUrdu) "اسمارٹ ٹریڈ اے آئی v1.0.0 • ایپ معلومات اور ڈیولپر رابطہ" else "Smart Trade AI v1.0.0 • View About & Developer Support",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
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
