package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ActivationHelper
import com.example.domain.model.SubscriptionPlan
import com.example.domain.model.UserSubscriptionState
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

@Composable
fun AppHubScreen(
    subscriptionState: UserSubscriptionState,
    developerName: String,
    developerWhatsapp: String,
    isRomanUrdu: Boolean,
    onOpenGuide: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenAdminPortal: () -> Unit,
    onOpenInfluencerPortal: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenLegal: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onRedeemCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var quickCodeInput by remember { mutableStateOf("") }
    var isRedeeming by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isRomanUrdu) "اسمارٹ ٹریڈ ہب اور مینو" else "Smart Trade Hub & Menu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = if (isRomanUrdu) "تمام ٹولز، وی آئی پی پلانز، گائیڈ اور ایڈمن ایک جگہ" else "All tools, VIP plans, guides, and admin in one place",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Language Quick Switch Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CryptoSurfaceVariant)
                    .border(1.dp, CryptoCardBorder, RoundedCornerShape(20.dp))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("hub_toggle_language"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = AccentCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isRomanUrdu) "اردو" else "English",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        // 1. VIP Plan & Quick Activation Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenPocketGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldenPocketGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "VIP Plan",
                                tint = GoldenPocketGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isRomanUrdu) "وی آئی پی پلان اور کوڈ ایکٹیویشن" else "VIP Plan & Activation Code",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            val planName = subscriptionState.currentPlan.title
                            Text(
                                text = "${if (isRomanUrdu) "موجودہ پلان: " else "Current Plan: "} $planName",
                                fontSize = 11.sp,
                                color = if (subscriptionState.isPrivilegedActive) BullGreen else AccentCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = onOpenSubscription,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenPocketGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("hub_btn_view_plans")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "پلانز" else "Plans",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Direct activation code input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickCodeInput,
                        onValueChange = { quickCodeInput = it.uppercase().trim() },
                        placeholder = { Text("STAI-XXXX-XXXX-XXXX", fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldenPocketGold,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hub_code_input")
                    )

                    Button(
                        onClick = {
                            val code = quickCodeInput.trim()
                            if (code.isBlank()) return@Button
                            isRedeeming = true
                            ActivationHelper.autoActivate(context, code) { success, msg ->
                                isRedeeming = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) {
                                    onRedeemCode(code)
                                    quickCodeInput = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isRedeeming && quickCodeInput.isNotBlank(),
                        modifier = Modifier.testTag("hub_btn_redeem")
                    ) {
                        Text(
                            text = if (isRomanUrdu) "فعال کریں" else "Redeem",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Admin Console Card (Secure Admin Access – Password Never Displayed)
        HubActionCard(
            title = if (isRomanUrdu) "ایڈمن کنسول اور کوڈ منیجر" else "Admin Console & Code Manager",
            subtitle = if (isRomanUrdu) "محفوظ ایڈمن رسائی – صرف مجاز ایڈمن کے لیے" else "Secure Admin Access – Authorized Admin Only",
            icon = Icons.Default.AdminPanelSettings,
            iconTint = BullGreen,
            onClick = onOpenAdminPortal,
            tag = "hub_admin_card"
        )

        // 3. Trader Guide & SMC Tutorial Card
        HubActionCard(
            title = if (isRomanUrdu) "ٹریڈر گائیڈ اور ایس ایم سی ٹیوٹوریلز" else "Trader Guide & SMC Tutorials",
            subtitle = if (isRomanUrdu) "آرڈر بلاکس، بی او ایس، گولڈن پاکٹ اور رسک مینجمنٹ سیکھیں" else "Learn Order Blocks, BOS, Golden Pocket, and risk management",
            icon = Icons.Default.School,
            iconTint = AccentCyan,
            onClick = onOpenGuide,
            tag = "hub_guide_card"
        )

        // 4. WhatsApp Direct Support Card
        HubActionCard(
            title = if (isRomanUrdu) "ڈیولپر واٹس ایپ براہ راست رابطہ" else "Developer WhatsApp Direct Support",
            subtitle = "$developerName ($developerWhatsapp)",
            icon = Icons.Default.Chat,
            iconTint = BullGreen,
            onClick = {
                val cleanNumber = developerWhatsapp.replace("+", "").replace(" ", "").replace("-", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$cleanNumber?text=Assalam-o-Alaikum%20Brother,%20SmartTradeAI%20help")
                }
                context.startActivity(intent)
            },
            tag = "hub_whatsapp_card"
        )

        // 5. Influencer & Referral Dashboard Card
        HubActionCard(
            title = if (isRomanUrdu) "انفلوئنسر اور ریفرل انعامات" else "Influencer & Referral Rewards",
            subtitle = if (isRomanUrdu) "اپنا ریفرل کوڈ شیئر کریں اور وی آئی پی سہولیات حاصل کریں" else "Share your referral code and unlock VIP privileges",
            icon = Icons.Default.Share,
            iconTint = WarningAmber,
            onClick = onOpenInfluencerPortal,
            tag = "hub_influencer_card"
        )

        // 6. About Developer & System Info Card
        HubActionCard(
            title = if (isRomanUrdu) "اسمارٹ ٹریڈ اے آئی اور ورژن" else "About Smart Trade AI & Version",
            subtitle = if (isRomanUrdu) "سسٹم، ورژن، ڈیولپر معلومات اور قانونی وضاحت" else "Architecture, version, developer info, and legal disclaimer",
            icon = Icons.Default.Info,
            iconTint = TextSecondary,
            onClick = onOpenAbout,
            tag = "hub_about_card"
        )

        // 7. Legal & Terms Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onOpenLegal("DISCLAIMER") },
                colors = ButtonDefaults.buttonColors(containerColor = CryptoSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "خطرات کا ڈس کلیمر" else "Risk Disclaimer",
                    fontSize = 10.sp,
                    color = WarningAmber
                )
            }

            Button(
                onClick = { onOpenLegal("TERMS") },
                colors = ButtonDefaults.buttonColors(containerColor = CryptoSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "شرائط و ضوابط" else "Terms of Service",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { onOpenLegal("PRIVACY") },
                colors = ButtonDefaults.buttonColors(containerColor = CryptoSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isRomanUrdu) "پرائیویسی پالیسی" else "Privacy Policy",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HubActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CryptoSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 2
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
