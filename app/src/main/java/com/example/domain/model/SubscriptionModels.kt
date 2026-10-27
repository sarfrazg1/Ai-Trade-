package com.example.domain.model

enum class SubscriptionPlan(
    val id: String,
    val title: String,
    val pricePkr: Int,
    val commissionPkr: Int,
    val durationDays: Int,
    val benefits: List<String>
) {
    GOLDEN(
        id = "GOLD",
        title = "Golden VIP Plan",
        pricePkr = 3000,
        commissionPkr = 600, // 20%
        durationDays = 30,
        benefits = listOf(
            "VIP 5m, 15m, 1H, 4H & 1D Real-Time Signals",
            "Smart Money Concepts (SMC) + Confluence Engine",
            "Automated Order Blocks, FVG & Fibonacci Retracement",
            "Direct WhatsApp VIP Priority Analysis Support"
        )
    ),
    SILVER(
        id = "SILV",
        title = "Silver Pro Plan",
        pricePkr = 2000,
        commissionPkr = 400, // 20%
        durationDays = 30,
        benefits = listOf(
            "Standard 15m, 1H & 4H Trading Setups",
            "Technical Indicators Breakdown (RSI, MACD, EMA)",
            "Automated Paper Trading Execution & Tracking",
            "Roman Urdu & English Guide Access"
        )
    ),
    BRONZE(
        id = "BRNZ",
        title = "Bronze Basic Plan",
        pricePkr = 1000,
        commissionPkr = 200, // 20%
        durationDays = 30,
        benefits = listOf(
            "Daily Futures Market Overview & Funding Rates",
            "Basic Swing High / Low Fibonacci Levels",
            "Educational Guide & Risk Management System"
        )
    ),
    FREE_TRIAL(
        id = "FREE",
        title = "Free Demo Mode",
        pricePkr = 0,
        commissionPkr = 0,
        durationDays = 7,
        benefits = listOf(
            "Explore Full Chart & Public Binance Live Data",
            "Test Paper Trading with Simulated $1,000 Balance",
            "Preview Confluence Score & Educational Material"
        )
    );

    companion object {
        fun fromId(id: String?): SubscriptionPlan {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FREE_TRIAL
        }
    }
}

enum class SubscriptionStatus {
    INACTIVE,
    TRIAL,
    ACTIVE,
    EXPIRED,
    REVOKED
}

enum class CommissionStatus(val label: String) {
    PENDING("Pending Verification"),
    HELD("Held (7-Day Holding Rule)"),
    ELIGIBLE("Eligible for Payout"),
    PAYOUT_REQUESTED("Payout Requested"),
    PAID("Paid & Settled"),
    CANCELLED("Cancelled"),
    REVERSED("Reversed / Refunded")
}

data class UserSubscriptionState(
    val userReference: String,
    val currentPlan: SubscriptionPlan = SubscriptionPlan.FREE_TRIAL,
    val status: SubscriptionStatus = SubscriptionStatus.TRIAL,
    val activationCode: String? = null,
    val activatedAt: Long? = null,
    val expiresAt: Long? = null,
    val referralCode: String? = null,
    val termsAccepted: Boolean = false,
    val disclaimerAccepted: Boolean = false,
    val consentTimestamp: Long? = null
) {
    val isPrivilegedActive: Boolean
        get() = (status == SubscriptionStatus.ACTIVE) && (expiresAt == null || expiresAt > System.currentTimeMillis())

    val daysRemaining: Int
        get() {
            val exp = expiresAt ?: return 0
            val diff = exp - System.currentTimeMillis()
            return if (diff > 0) (diff / (1000L * 60 * 60 * 24)).toInt() else 0
        }
}

data class ActivationCodeRecord(
    val code: String,
    val userReference: String,
    val plan: SubscriptionPlan,
    val paymentReference: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L),
    val isRedeemed: Boolean = false,
    val redeemedAt: Long? = null,
    val isRevoked: Boolean = false,
    val revokedAt: Long? = null
)

data class InfluencerCommissionRecord(
    val id: String,
    val influencerCode: String,
    val userReference: String,
    val plan: SubscriptionPlan,
    val saleAmountPkr: Int,
    val commissionAmountPkr: Int,
    val verifiedAt: Long = System.currentTimeMillis(),
    val eligibleAt: Long = verifiedAt + (7L * 24 * 60 * 60 * 1000L), // Strict 7-day rule
    val status: CommissionStatus = CommissionStatus.HELD,
    val payoutReference: String? = null,
    val payoutTimestamp: Long? = null,
    val notes: String? = null
) {
    val isSevenDaysPassed: Boolean
        get() = System.currentTimeMillis() >= eligibleAt

    val daysUntilEligible: Int
        get() {
            val diff = eligibleAt - System.currentTimeMillis()
            return if (diff > 0) ((diff / (1000L * 60 * 60 * 24)) + 1).toInt() else 0
        }
}

data class AdminAuditRecord(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val userReference: String,
    val plan: String,
    val details: String
)
