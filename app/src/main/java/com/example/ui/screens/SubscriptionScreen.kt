package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SubscriptionPlan
import com.example.domain.model.SubscriptionStatus
import com.example.domain.model.UserSubscriptionState
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SubscriptionScreen(
    subscriptionState: UserSubscriptionState,
    developerWhatsapp: String = "+923360074976",
    isRomanUrdu: Boolean = false,
    onApplyReferralCode: (String) -> Boolean,
    onRedeemCode: (String) -> Unit,
    onClose: () -> Unit,
    onOpenLegalPage: (String) -> Unit,
    onOpenAdminPortal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.GOLDEN) }
    var referralInput by remember { mutableStateOf(subscriptionState.referralCode ?: "") }
    var referralMessage by remember { mutableStateOf<String?>(null) }
    var showRedeemDialog by remember { mutableStateOf(false) }
    var showFallbackDialog by remember { mutableStateOf(false) }
    var lastPrefilledMessage by remember { mutableStateOf("") }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRomanUrdu) "Smart Trade AI Plans" else "Subscription Plans",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (subscriptionState.isPrivilegedActive) BullGreen.copy(alpha = 0.2f)
                        else WarningAmber.copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (subscriptionState.isPrivilegedActive) "ACTIVE: ${subscriptionState.currentPlan.id}"
                    else "FREE DEMO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (subscriptionState.isPrivilegedActive) BullGreen else WarningAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Branding & Educational Purpose Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BullGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Smart Trade AI",
                            tint = BullGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Smart Trade AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRomanUrdu) "Taleemi aur Market Tajziya Platform" else "Cryptocurrency Education & Market Analysis",
                            fontSize = 11.sp,
                            color = AccentCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isRomanUrdu) {
                        "Smart Trade AI ek taleemi platform hai jo Binance public data par mabni technical indicators, Smart Money Concepts (SMC) aur risk management rules faraham karta hai. Hum automated payment gateway k bajaye WhatsApp Business par asaan manual rabtay k zariye unique activation code jari karte hain."
                    } else {
                        "Smart Trade AI is a dedicated educational and analytical tool designed to simplify futures market data, Smart Money Concepts (SMC), and disciplined risk management. To keep setup simple and accessible in Pakistan, subscriptions are manually managed and activated via WhatsApp Business."
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // User Reference ID & Referral Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isRomanUrdu) "Aapka User Reference ID:" else "Your User Reference ID:",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = subscriptionState.userReference,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("User Reference", subscriptionState.userReference)
                            clipboard.setPrimaryClip(clip)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy reference ID",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Referral Code Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = referralInput,
                        onValueChange = { referralInput = it },
                        label = { Text(if (isRomanUrdu) "Influencer Referral Code (Ikhtiyari)" else "Influencer Referral Code (Optional)", fontSize = 11.sp) },
                        placeholder = { Text("e.g. SARFRAZ20", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("referral_code_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val success = onApplyReferralCode(referralInput)
                            referralMessage = if (success) {
                                if (isRomanUrdu) "Referral code shamil ho gaya!" else "Referral code applied!"
                            } else {
                                if (isRomanUrdu) "Ghair durust referral code" else "Invalid referral format"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("apply_referral_button")
                    ) {
                        Text(if (isRomanUrdu) "Apply" else "Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                referralMessage?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, fontSize = 11.sp, color = BullGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Plans Header
        Text(
            text = if (isRomanUrdu) "Dastyab Plans Muntakhab Karein:" else "Select Subscription Plan:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Plan 1: Golden
        PlanOptionCard(
            plan = SubscriptionPlan.GOLDEN,
            isSelected = selectedPlan == SubscriptionPlan.GOLDEN,
            isRomanUrdu = isRomanUrdu,
            onSelect = { selectedPlan = SubscriptionPlan.GOLDEN }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Plan 2: Silver
        PlanOptionCard(
            plan = SubscriptionPlan.SILVER,
            isSelected = selectedPlan == SubscriptionPlan.SILVER,
            isRomanUrdu = isRomanUrdu,
            onSelect = { selectedPlan = SubscriptionPlan.SILVER }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Plan 3: Bronze
        PlanOptionCard(
            plan = SubscriptionPlan.BRONZE,
            isSelected = selectedPlan == SubscriptionPlan.BRONZE,
            isRomanUrdu = isRomanUrdu,
            onSelect = { selectedPlan = SubscriptionPlan.BRONZE }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Primary Action: Contact on WhatsApp
        Button(
            onClick = {
                val cleanNumber = developerWhatsapp.replace(Regex("[^0-9]"), "").ifEmpty { "923360074976" }
                val referralText = if (!subscriptionState.referralCode.isNullOrBlank()) subscriptionState.referralCode else "NONE"
                val planText = "${selectedPlan.title} (PKR ${selectedPlan.pricePkr})"
                val userRef = subscriptionState.userReference

                val message = if (isRomanUrdu) {
                    "Assalam-o-Alaikum, mein Smart Trade AI app k mutaliq rabta kar raha hun. Mera muntakhab plan $planText hai. Mera user reference $userRef hai. Mera influencer referral code $referralText hai. Baraye meharbani subscription aur payment ki hidayat faraham karein."
                } else {
                    "Hello, I am contacting you about Smart Trade AI. My selected plan is $planText. My user reference is $userRef. My influencer referral code is $referralText. Please provide the subscription and payment instructions."
                }

                lastPrefilledMessage = message

                try {
                    val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
                    val waUri = Uri.parse("https://wa.me/$cleanNumber?text=$encoded")
                    val intent = Intent(Intent.ACTION_VIEW, waUri)
                    intent.setPackage("com.whatsapp")
                    context.startActivity(intent)
                } catch (_: Exception) {
                    // Try WhatsApp Business or browser fallback
                    try {
                        val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
                        val waUri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encoded")
                        val intent = Intent(Intent.ACTION_VIEW, waUri)
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        showFallbackDialog = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("contact_whatsapp_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRomanUrdu) "WhatsApp par Rabta Karein" else "Contact on WhatsApp",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Action: Enter Activation Code
        OutlinedButton(
            onClick = { showRedeemDialog = true },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("enter_activation_code_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Key, contentDescription = "Activation Code", tint = AccentCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRomanUrdu) "Activation Code Darj Karein" else "Enter Activation Code",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Free Demo / Explore App Option
        TextButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (isRomanUrdu) "Free Demo Mode me App Dekhein →" else "Explore App in Free Demo Mode →",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal & Policy Links
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRomanUrdu) "Disclaimer (Urdu/ENG)" else "Disclaimer",
                fontSize = 11.sp,
                color = AccentCyan,
                modifier = Modifier
                    .clickable { onOpenLegalPage("DISCLAIMER") }
                    .padding(4.dp)
            )
            Text(text = " • ", fontSize = 11.sp, color = TextMuted)
            Text(
                text = if (isRomanUrdu) "Privacy Policy" else "Privacy Policy",
                fontSize = 11.sp,
                color = AccentCyan,
                modifier = Modifier
                    .clickable { onOpenLegalPage("PRIVACY") }
                    .padding(4.dp)
            )
            Text(text = " • ", fontSize = 11.sp, color = TextMuted)
            Text(
                text = if (isRomanUrdu) "Terms of Use" else "Terms of Use",
                fontSize = 11.sp,
                color = AccentCyan,
                modifier = Modifier
                    .clickable { onOpenLegalPage("TERMS") }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Admin Access link
        Text(
            text = "⚙ Admin Management (PIN Protected)",
            fontSize = 10.sp,
            color = TextMuted,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onOpenAdminPortal() }
                .padding(4.dp)
        )
    }

    // Activation Code Dialog
    if (showRedeemDialog) {
        var inputCode by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showRedeemDialog = false },
            containerColor = CryptoSurface,
            title = {
                Text(
                    text = if (isRomanUrdu) "Activation Code Darj Karein" else "Enter Activation Code",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isRomanUrdu) {
                            "WhatsApp par administrator se mila hua unique activation code yahan darj karein. Yeh code aapke user reference (${subscriptionState.userReference}) k sath munsalik hai."
                        } else {
                            "Enter the unique activation code provided by the administrator on WhatsApp. This code is bound to your user reference (${subscriptionState.userReference})."
                        },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        label = { Text("Activation Code", fontSize = 11.sp) },
                        placeholder = { Text("e.g. STAI-GOLD-XXXX-30D-XXXXXXXX", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("code_input_field")
                    )

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = it, fontSize = 11.sp, color = BearRed)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCode.isBlank()) {
                            errorMessage = "Please enter an activation code"
                            return@Button
                        }
                        onRedeemCode(inputCode)
                        showRedeemDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                    modifier = Modifier.testTag("confirm_redeem_button")
                ) {
                    Text(if (isRomanUrdu) "Activate Karein" else "Activate Plan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRedeemDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Fallback WhatsApp Dialog
    if (showFallbackDialog) {
        AlertDialog(
            onDismissRequest = { showFallbackDialog = false },
            containerColor = CryptoSurface,
            title = {
                Text("WhatsApp Not Detected", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "WhatsApp could not be opened directly. Please contact our official WhatsApp Business number manually:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "WhatsApp: $developerWhatsapp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Prefilled message to copy:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = lastPrefilledMessage,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier
                            .background(CryptoSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("WhatsApp Message", lastPrefilledMessage)
                        clipboard.setPrimaryClip(clip)
                        showFallbackDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                ) {
                    Text("Copy Message", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFallbackDialog = false }) {
                    Text("Close", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun PlanOptionCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    isRomanUrdu: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CryptoSurfaceVariant else CryptoSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BullGreen else CryptoCardBorder
        )
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
                            .size(18.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) BullGreen else TextMuted, CircleShape)
                            .background(if (isSelected) BullGreen else Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = plan.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "PKR ${plan.pricePkr}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) BullGreen else AccentCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            plan.benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Included",
                        tint = BullGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = benefit,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
