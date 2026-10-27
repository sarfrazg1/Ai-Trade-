package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun LegalDocumentsScreen(
    initialTab: String = "DISCLAIMER",
    onClose: () -> Unit,
    onAcceptConsent: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember {
        mutableStateOf(
            when (initialTab.uppercase()) {
                "PRIVACY" -> 1
                "TERMS" -> 2
                else -> 0
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CryptoSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Legal & Compliance Documents",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = CryptoSurface,
            contentColor = BullGreen
        ) {
            Tab(
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 },
                text = { Text("Disclaimer", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedIndex == 1,
                onClick = { selectedIndex = 1 },
                text = { Text("Privacy Policy", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedIndex == 2,
                onClick = { selectedIndex = 2 },
                text = { Text("Terms of Use", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedIndex) {
                0 -> DisclaimerContent()
                1 -> PrivacyPolicyContent()
                2 -> TermsOfUseContent()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onAcceptConsent()
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("I Understand & Accept", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DisclaimerContent() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Important Risk Disclaimer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Roman Urdu Khulaasa (اردو خلاصہ):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Smart Trade AI sirf aur sirf taleemi aur tajziyati maqasid k liye banaya gaya hai. Yeh application kisi b qisam ki maali (financial) ya sarmayakari ki guarantee nahi deti. Cryptocurrency aur Futures trading me intehai zyada maali nuqsan ka khatra mojood hota hai. App k signals, SMC analysis, ya indicators waqti tor par ghalat ya takheer ka shikar ho sakte hain. Har sarmayakari ya trade ka faisla sarif (user) ki apni zati zimedari hai.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Official English Disclaimer:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "1. Educational & Analytical Purpose: Smart Trade AI provides market data analysis, mathematical indicator calculations, and Smart Money Concepts (SMC) models strictly for educational, informational, and paper trading simulations.\n\n" +
                        "2. High Financial Risk: Cryptocurrency futures and derivatives involve substantial risk of loss and are not suitable for every individual. High leverage significantly magnifies both gains and losses.\n\n" +
                        "3. No Guarantee or Financial Advice: Signals, algorithmic confluence scores, and Fibonacci targets do not guarantee future performance or price movements. The software does not provide financial, investment, or brokerage advice.\n\n" +
                        "4. User Responsibility: All trading and capital allocation decisions remain the sole, independent responsibility of the user.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun PrivacyPolicyContent() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = AccentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Privacy Policy", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Last Updated: October 2026",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Data We Process:\n" +
                        "• Anonymous User Reference ID (e.g. STAI-USER-XXXXXX): Generated locally on device to bind activation keys.\n" +
                        "• Voluntary Contact Information: When you contact our official WhatsApp Business account, we process the message you voluntarily send to facilitate plan selection and activation code delivery.\n" +
                        "• Technical App Data: Public Binance market candlestick and futures data retrieved on-demand.\n\n" +
                        "2. What We NEVER Access or Collect:\n" +
                        "• We never access your exchange private keys, passwords, or trading account credentials.\n" +
                        "• We never request or store sensitive bank passwords, debit/credit card CVV codes, or PINs.\n" +
                        "• The app operates zero background location tracking.\n\n" +
                        "3. Data Retention & Consent:\n" +
                        "Subscription entitlements and cryptographic activation logs are retained to verify legitimate license usage. By using the app or redeeming an activation key, you consent to this policy.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun TermsOfUseContent() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = BullGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Terms of Use & License", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1. Software License:\n" +
                        "Smart Trade AI grants a personal, non-exclusive, non-transferable license for personal crypto analysis.\n\n" +
                        "2. Manual Activation System:\n" +
                        "• Subscriptions are issued through manual verification on our verified WhatsApp Business channel.\n" +
                        "• Each activation code is cryptographically bound to the recipient's User Reference ID and cannot be shared or redeemed across different devices.\n" +
                        "• Codes that are forged, cracked, or tampered with will be rejected by the cryptographic HMAC engine.\n\n" +
                        "3. Influencer Commission Policy:\n" +
                        "• Influencers earn 20% on verified paid sales.\n" +
                        "• In accordance with financial audit requirements, a strict 7-day server holding period applies from payment verification before funds become eligible for payout.\n\n" +
                        "4. Prohibited Uses:\n" +
                        "Users may not reverse engineer, redistribute, resell, or claim automated profits using this software.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
