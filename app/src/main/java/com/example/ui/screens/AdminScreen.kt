package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SubscriptionManager
import com.example.domain.model.CommissionStatus
import com.example.domain.model.InfluencerCommissionRecord
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    subscriptionManager: SubscriptionManager,
    isUserAdmin: Boolean = false,
    adminEmail: String? = null,
    onSignInAdmin: () -> Unit = {},
    onClose: () -> Unit,
    onPublishCodeCloud: (code: String, targetUserRef: String, plan: String, validityDays: Int, paymentRef: String) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    val correctPin = "74976" // Default secure PIN

    // Gate 1: Check verified administrator identity
    if (!isUserAdmin) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CryptoBackground),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BearRed.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Access Restricted",
                        tint = BearRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Access Restricted",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This Administrative Console is restricted exclusively to the verified application owner account:\nsarfarazmech00@gmail.com",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CryptoSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (!adminEmail.isNullOrBlank()) {
                                "Currently signed in as:\n$adminEmail (Not Administrator)"
                            } else {
                                "Currently: Not signed in"
                            },
                            fontSize = 11.sp,
                            color = WarningAmber,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(containerColor = CryptoSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back to App", color = TextPrimary)
                        }

                        Button(
                            onClick = onSignInAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sign In Admin", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        return
    }

    // Gate 2: Secondary PIN verification for authorized administrator
    if (!isAuthenticated) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CryptoBackground),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Admin Lock",
                        tint = BullGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Admin Portal Access",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Verified Admin: $adminEmail\nEnter secondary PIN to unlock management tools.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("Enter Admin PIN", fontSize = 12.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )

                    pinError?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = it, fontSize = 11.sp, color = BearRed)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = onClose) {
                            Text("Back to App", color = TextMuted)
                        }
                        Button(
                            onClick = {
                                if (pinInput == correctPin) {
                                    isAuthenticated = true
                                    pinError = null
                                } else {
                                    pinError = "Incorrect PIN."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                            modifier = Modifier.testTag("admin_login_button")
                        ) {
                            Text("Unlock", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    // Authenticated View
    val commissions by subscriptionManager.commissions.collectAsState()
    val auditLogs by subscriptionManager.auditLogs.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CryptoSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Admin Management Console",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            IconButton(onClick = { subscriptionManager.refreshCommissionEligibility() }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentCyan)
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CryptoSurface,
            contentColor = BullGreen
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Issue Code", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Influencer Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Issued Codes", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Audit Log", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> IssueCodeTab(subscriptionManager = subscriptionManager, onPublishCodeCloud = onPublishCodeCloud)
            1 -> InfluencerLedgerTab(commissions = commissions, subscriptionManager = subscriptionManager)
            2 -> IssuedCodesTab(subscriptionManager = subscriptionManager)
            3 -> AuditLogTab(auditLogs = auditLogs)
        }
    }
}

@Composable
private fun IssueCodeTab(
    subscriptionManager: SubscriptionManager,
    onPublishCodeCloud: (code: String, targetUserRef: String, plan: String, validityDays: Int, paymentRef: String) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    var userRefInput by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.GOLDEN) }
    var paymentRefInput by remember { mutableStateOf("") }
    var validityDaysInput by remember { mutableStateOf("30") }
    var generatedCode by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Verify Payment & Issue Activation Key",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userRefInput,
                        onValueChange = { userRefInput = it },
                        label = { Text("Target User Reference ID", fontSize = 11.sp) },
                        placeholder = { Text("e.g. STAI-USER-892415", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_user_ref_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Select Plan:", fontSize = 12.sp, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(SubscriptionPlan.GOLDEN, SubscriptionPlan.SILVER, SubscriptionPlan.BRONZE).forEach { plan ->
                            Button(
                                onClick = { selectedPlan = plan },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedPlan == plan) BullGreen else CryptoSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${plan.id} (${plan.pricePkr})",
                                    fontSize = 10.sp,
                                    color = if (selectedPlan == plan) Color.Black else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = paymentRefInput,
                        onValueChange = { paymentRefInput = it },
                        label = { Text("Payment Verification TID / Reference", fontSize = 11.sp) },
                        placeholder = { Text("e.g. JazzCash / EasyPaisa TID 9841829", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = validityDaysInput,
                        onValueChange = { validityDaysInput = it },
                        label = { Text("Validity Days", fontSize = 11.sp) },
                        placeholder = { Text("30", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (userRefInput.isBlank()) {
                                feedbackMessage = "Please enter the target User Reference ID."
                                return@Button
                            }
                            val days = validityDaysInput.toIntOrNull() ?: 30
                            val paymentRef = paymentRefInput.ifBlank { "MANUAL_VERIFIED" }
                            val code = subscriptionManager.generateActivationCode(
                                targetUserRef = userRefInput,
                                plan = selectedPlan,
                                validityDays = days,
                                paymentRef = paymentRef
                            )
                            generatedCode = code
                            onPublishCodeCloud(code, userRefInput, selectedPlan.id, days, paymentRef)
                            feedbackMessage = "Code successfully generated & saved to Cloud Vault & Local Ledger!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_generate_code_button")
                    ) {
                        Text("Approve Payment & Generate Key", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    feedbackMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, fontSize = 12.sp, color = BullGreen)
                    }

                    generatedCode?.let { code ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CryptoSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BullGreen)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Generated Activation Key:", fontSize = 11.sp, color = TextMuted)
                                Text(text = code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Activation Code", code)
                                        clipboard.setPrimaryClip(clip)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy Code for WhatsApp", color = Color.Black, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfluencerLedgerTab(
    commissions: List<InfluencerCommissionRecord>,
    subscriptionManager: SubscriptionManager
) {
    var selectedRecordForPayout by remember { mutableStateOf<InfluencerCommissionRecord?>(null) }
    var selectedRecordForReversal by remember { mutableStateOf<InfluencerCommissionRecord?>(null) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Influencer Commission Ledger (20% & 7-Day Rule)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Golden: PKR 600 • Silver: PKR 400 • Bronze: PKR 200. Enforces server-side 7 complete days waiting period before payout eligibility.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (commissions.isEmpty()) {
            item {
                Text(
                    text = "No influencer sales records found yet.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        items(commissions) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Code: ${record.influencerCode} (${record.plan.id})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Text(
                            text = "PKR ${record.commissionAmountPkr}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BullGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "User Ref: ${record.userReference} | Sale: PKR ${record.saleAmountPkr}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Verified: ${dateFormat.format(Date(record.verifiedAt))}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Eligible Release: ${dateFormat.format(Date(record.eligibleAt))}",
                        fontSize = 10.sp,
                        color = if (record.isSevenDaysPassed) BullGreen else WarningAmber
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    when (record.status) {
                                        CommissionStatus.PAID -> BullGreen.copy(alpha = 0.2f)
                                        CommissionStatus.ELIGIBLE -> AccentCyan.copy(alpha = 0.2f)
                                        CommissionStatus.HELD -> WarningAmber.copy(alpha = 0.2f)
                                        CommissionStatus.REVERSED, CommissionStatus.CANCELLED -> BearRed.copy(alpha = 0.2f)
                                        else -> CryptoSurfaceVariant
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${record.status.label} ${if (record.status == CommissionStatus.HELD) "(${record.daysUntilEligible}d left)" else ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (record.status) {
                                    CommissionStatus.PAID -> BullGreen
                                    CommissionStatus.ELIGIBLE -> AccentCyan
                                    CommissionStatus.HELD -> WarningAmber
                                    CommissionStatus.REVERSED, CommissionStatus.CANCELLED -> BearRed
                                    else -> TextSecondary
                                }
                            )
                        }

                        Row {
                            if (record.status == CommissionStatus.ELIGIBLE || record.status == CommissionStatus.PAYOUT_REQUESTED) {
                                TextButton(onClick = { selectedRecordForPayout = record }) {
                                    Text("Mark Paid", fontSize = 11.sp, color = BullGreen)
                                }
                            }
                            if (record.status != CommissionStatus.PAID && record.status != CommissionStatus.REVERSED) {
                                TextButton(onClick = { selectedRecordForReversal = record }) {
                                    Text("Reverse", fontSize = 11.sp, color = BearRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Mark Paid Dialog
    selectedRecordForPayout?.let { record ->
        var payoutRef by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { selectedRecordForPayout = null },
            containerColor = CryptoSurface,
            title = { Text("Confirm Influencer Payout", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Record manual settlement of PKR ${record.commissionAmountPkr} to influencer ${record.influencerCode}.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = payoutRef,
                        onValueChange = { payoutRef = it },
                        label = { Text("Transfer Reference / TID", fontSize = 11.sp) },
                        placeholder = { Text("e.g. JazzCash 8419401", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        subscriptionManager.markPayoutPaid(record.id, payoutRef)
                        selectedRecordForPayout = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                ) {
                    Text("Confirm Paid", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecordForPayout = null }) { Text("Cancel", color = TextMuted) }
            }
        )
    }

    // Reverse Dialog
    selectedRecordForReversal?.let { record ->
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { selectedRecordForReversal = null },
            containerColor = CryptoSurface,
            title = { Text("Reverse Commission Record", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Reverse commission for record ${record.id} (Influencer: ${record.influencerCode}). Original record will be preserved for audit.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reversal Reason", fontSize = 11.sp) },
                        placeholder = { Text("e.g. User chargeback or dispute", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BearRed,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        subscriptionManager.reverseOrCancelCommission(record.id, reason)
                        selectedRecordForReversal = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BearRed)
                ) {
                    Text("Confirm Reversal", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecordForReversal = null }) { Text("Cancel", color = TextMuted) }
            }
        )
    }
}

@Composable
private fun IssuedCodesTab(subscriptionManager: SubscriptionManager) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val codes = remember { subscriptionManager.getIssuedCodes() }
    val rtdbCodes = remember { subscriptionManager.getPresetRtdbCodes() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    var filterText by remember { mutableStateOf("") }

    val filteredRtdb = remember(filterText) {
        if (filterText.isBlank()) rtdbCodes
        else rtdbCodes.filter { it.first.contains(filterText, ignoreCase = true) || it.second.contains(filterText, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Text(
                text = "⚡ Realtime Database Codes (${rtdbCodes.size} Available)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentCyan
            )
            Text(
                text = "Firebase RTDB: gen-lang-client-0963937245-default-rtdb",
                fontSize = 10.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                label = { Text("Filter codes (e.g. GOLD, SILVER, BRONZE)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = CryptoCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (filteredRtdb.isEmpty()) {
            item {
                Text("No matching Realtime Database codes.", fontSize = 12.sp, color = TextMuted)
            }
        }

        items(filteredRtdb.take(50)) { item ->
            val code = item.first
            val plan = item.second
            val price = item.third
            val planColor = when (plan.lowercase()) {
                "golden", "gold" -> Color(0xFFFFD700)
                "silver" -> Color(0xFFC0C0C0)
                else -> Color(0xFFCD7F32)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = code, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = planColor)
                        Text(
                            text = "Plan: ${plan.uppercase()} | Price: PKR $price (30 Days)",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Activation Code", code))
                            android.widget.Toast.makeText(context, "Copied: $code", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = AccentCyan)
                    }
                }
            }
        }

        if (filteredRtdb.size > 50) {
            item {
                Text(
                    text = "... and ${filteredRtdb.size - 50} more codes. Use filter above to search.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Custom Issued Codes (${codes.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BullGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (codes.isEmpty()) {
            item {
                Text("No custom issued activation keys found.", fontSize = 11.sp, color = TextMuted)
            }
        }

        items(codes) { obj ->
            val code = obj.optString("code")
            val userRef = obj.optString("userRef")
            val plan = obj.optString("plan")
            val paymentRef = obj.optString("paymentRef")
            val createdAt = obj.optLong("createdAt")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = code, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Target User: $userRef | Plan: $plan", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "Payment Ref: $paymentRef", fontSize = 10.sp, color = TextMuted)
                    Text(text = "Issued: ${dateFormat.format(Date(createdAt))}", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Activation Code", code))
                                android.widget.Toast.makeText(context, "Copied: $code", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Copy", fontSize = 10.sp, color = Color.Black)
                        }
                        Button(
                            onClick = { subscriptionManager.revokeIssuedCode(code) },
                            colors = ButtonDefaults.buttonColors(containerColor = BearRed),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Revoke Key", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogTab(auditLogs: List<com.example.domain.model.AdminAuditRecord>) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        if (auditLogs.isEmpty()) {
            item {
                Text("No administrative audit entries logged yet.", fontSize = 12.sp, color = TextMuted)
            }
        }

        items(auditLogs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.action, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                        Text(text = dateFormat.format(Date(log.timestamp)), fontSize = 9.sp, color = TextMuted)
                    }
                    Text(text = "Target: ${log.userReference} (${log.plan})", fontSize = 10.sp, color = TextSecondary)
                    Text(text = log.details, fontSize = 10.sp, color = TextMuted)
                }
            }
        }
    }
}
