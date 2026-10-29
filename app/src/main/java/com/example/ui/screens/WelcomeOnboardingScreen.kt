package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SubscriptionPlan
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WelcomeOnboardingScreen(
    onSelectPlanAndContinue: (SubscriptionPlan) -> Unit,
    onOpenLegal: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Prevent skipping onboarding through the back button
    BackHandler(enabled = true) {
        // Enforce plan selection before entering application
    }

    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.FREE_TRIAL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Branding Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BullGreen.copy(alpha = 0.15f))
                .border(1.5.dp, BullGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = "Smart Trade AI",
                tint = BullGreen,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Welcome to Smart Trade AI",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select your trading plan to get started. Free demo mode is immediately available, or upgrade to professional signals.",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Plan 1: Free Demo Mode (PKR 0)
        PlanChoiceCard(
            plan = SubscriptionPlan.FREE_TRIAL,
            isSelected = selectedPlan == SubscriptionPlan.FREE_TRIAL,
            badge = "FREE",
            badgeColor = AccentCyan,
            onClick = { selectedPlan = SubscriptionPlan.FREE_TRIAL }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 2: Bronze Basic (PKR 1,000)
        PlanChoiceCard(
            plan = SubscriptionPlan.BRONZE,
            isSelected = selectedPlan == SubscriptionPlan.BRONZE,
            badge = "BASIC",
            badgeColor = Color(0xFFCD7F32),
            onClick = { selectedPlan = SubscriptionPlan.BRONZE }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 3: Silver Pro (PKR 2,000)
        PlanChoiceCard(
            plan = SubscriptionPlan.SILVER,
            isSelected = selectedPlan == SubscriptionPlan.SILVER,
            badge = "POPULAR",
            badgeColor = Color(0xFFC0C0C0),
            onClick = { selectedPlan = SubscriptionPlan.SILVER }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Plan 4: Golden VIP (PKR 3,000)
        PlanChoiceCard(
            plan = SubscriptionPlan.GOLDEN,
            isSelected = selectedPlan == SubscriptionPlan.GOLDEN,
            badge = "VIP COMPLETE",
            badgeColor = BullGreen,
            onClick = { selectedPlan = SubscriptionPlan.GOLDEN }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Confirm Selection Button
        Button(
            onClick = { onSelectPlanAndContinue(selectedPlan) },
            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("onboarding_confirm_button")
        ) {
            Text(
                text = if (selectedPlan == SubscriptionPlan.FREE_TRIAL) {
                    "Continue with Free Demo"
                } else {
                    "Select ${selectedPlan.title} (PKR ${selectedPlan.pricePkr})"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Legal Disclaimer",
                fontSize = 11.sp,
                color = AccentCyan,
                modifier = Modifier
                    .clickable { onOpenLegal("DISCLAIMER") }
                    .padding(6.dp)
            )
            Text(text = "•", color = TextMuted, fontSize = 11.sp)
            Text(
                text = "Terms of Service",
                fontSize = 11.sp,
                color = AccentCyan,
                modifier = Modifier
                    .clickable { onOpenLegal("TERMS") }
                    .padding(6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crypto futures trading carries high risk. Paid plans remain pending until verified via WhatsApp or activation code.",
            fontSize = 10.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PlanChoiceCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BullGreen else CryptoCardBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val bgColor = if (isSelected) CryptoSurfaceVariant else CryptoSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("plan_card_${plan.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(borderWidth, borderColor)
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
                            .size(20.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, if (isSelected) BullGreen else TextMuted, CircleShape)
                            .background(if (isSelected) BullGreen else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = plan.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (plan.pricePkr == 0) "PKR 0 (Free Demo)" else "PKR ${plan.pricePkr} / 30 Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (plan.pricePkr == 0) AccentCyan else BullGreen
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bulleted feature items
            plan.benefits.forEach { benefit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isSelected) BullGreen else TextMuted,
                        modifier = Modifier
                            .size(13.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = benefit,
                        fontSize = 11.sp,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
