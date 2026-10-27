package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SubscriptionManager
import com.example.domain.model.CommissionStatus
import com.example.domain.model.SubscriptionPlan
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
fun InfluencerDashboardScreen(
    subscriptionManager: SubscriptionManager,
    initialReferralCode: String? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var influencerCodeInput by remember { mutableStateOf(initialReferralCode ?: "SARFRAZ20") }
    val commissions by subscriptionManager.commissions.collectAsState()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val cleanCode = influencerCodeInput.trim().uppercase()
    val relevantRecords = commissions.filter { it.influencerCode.equals(cleanCode, ignoreCase = true) }

    val goldenCount = relevantRecords.count { it.plan == SubscriptionPlan.GOLDEN && it.status != CommissionStatus.CANCELLED && it.status != CommissionStatus.REVERSED }
    val silverCount = relevantRecords.count { it.plan == SubscriptionPlan.SILVER && it.status != CommissionStatus.CANCELLED && it.status != CommissionStatus.REVERSED }
    val bronzeCount = relevantRecords.count { it.plan == SubscriptionPlan.BRONZE && it.status != CommissionStatus.CANCELLED && it.status != CommissionStatus.REVERSED }
    val totalSales = goldenCount + silverCount + bronzeCount

    val totalHeld = relevantRecords.filter { it.status == CommissionStatus.HELD }.sumOf { it.commissionAmountPkr }
    val totalEligible = relevantRecords.filter { it.status == CommissionStatus.ELIGIBLE }.sumOf { it.commissionAmountPkr }
    val totalRequested = relevantRecords.filter { it.status == CommissionStatus.PAYOUT_REQUESTED }.sumOf { it.commissionAmountPkr }
    val totalPaid = relevantRecords.filter { it.status == CommissionStatus.PAID }.sumOf { it.commissionAmountPkr }

    val referralLink = "https://github.com/sarfrazg1/Ai-Trade-/releases/latest/download/app-debug.apk?ref=$cleanCode"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Influencer Partner Portal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Code Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Your Influencer Referral Code:", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = influencerCodeInput,
                        onValueChange = { influencerCodeInput = it },
                        placeholder = { Text("e.g. SARFRAZ20") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Shareable Link Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Shareable APK Download & Referral Link:", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = referralLink,
                    fontSize = 11.sp,
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Referral Link", referralLink)
                            clipboard.setPrimaryClip(clip)
                            statusMessage = "Referral link copied to clipboard!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Link", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Smart Trade AI Download")
                                putExtra(Intent.EXTRA_TEXT, "Download Smart Trade AI app for crypto futures signals & SMC analysis:\n$referralLink\nUse my code $cleanCode on WhatsApp for instant activation!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Commission Breakdown Grid
        Text(text = "Commission & Earnings Summary (20% Rate):", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommissionTile(
                title = "Held (7-Day Rule)",
                amount = "PKR $totalHeld",
                subtitle = "Under 7-day waiting period",
                color = WarningAmber,
                modifier = Modifier.weight(1f)
            )
            CommissionTile(
                title = "Eligible for Payout",
                amount = "PKR $totalEligible",
                subtitle = "Ready to withdraw",
                color = BullGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommissionTile(
                title = "Payout Requested",
                amount = "PKR $totalRequested",
                subtitle = "Admin reviewing",
                color = AccentCyan,
                modifier = Modifier.weight(1f)
            )
            CommissionTile(
                title = "Paid & Settled",
                amount = "PKR $totalPaid",
                subtitle = "Completed payouts",
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Verified Sales Count
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Verified Paid Sales: $totalSales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "• Golden VIP (PKR 3,000): $goldenCount sales → PKR ${goldenCount * 600} commission", fontSize = 11.sp, color = BullGreen)
                Text(text = "• Silver Pro (PKR 2,000): $silverCount sales → PKR ${silverCount * 400} commission", fontSize = 11.sp, color = AccentCyan)
                Text(text = "• Bronze Basic (PKR 1,000): $bronzeCount sales → PKR ${bronzeCount * 200} commission", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Request Payout Button
        Button(
            onClick = {
                val res = subscriptionManager.requestPayout(cleanCode)
                res.onSuccess {
                    statusMessage = it
                }.onFailure {
                    statusMessage = it.localizedMessage ?: "Failed to request payout."
                }
            },
            enabled = totalEligible > 0,
            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("request_payout_button")
        ) {
            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (totalEligible > 0) "Request Payout of PKR $totalEligible" else "No Eligible Balance (Wait 7 Days)",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        statusMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, fontSize = 12.sp, color = if (it.contains("submitted", ignoreCase = true)) BullGreen else WarningAmber)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Policy Note
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurfaceVariant),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Commission Policy: Commissions are set at 20% of eligible verified paid subscriptions. In strict compliance with financial auditing rules, each commission is subject to a 7-day waiting period from the server payment verification date before becoming eligible for withdrawal.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun CommissionTile(
    title: String,
    amount: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 9.sp, color = TextSecondary)
        }
    }
}
