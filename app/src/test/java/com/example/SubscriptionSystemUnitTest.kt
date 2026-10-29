package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SubscriptionManager
import com.example.domain.model.CommissionStatus
import com.example.domain.model.SubscriptionPlan
import com.example.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SubscriptionSystemUnitTest {

    private lateinit var context: Context
    private lateinit var subscriptionManager: SubscriptionManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("stai_subscription_vault", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        subscriptionManager = SubscriptionManager(context)
    }

    @Test
    fun testUserReferenceCreatedAndPersistent() {
        val ref1 = subscriptionManager.getUserReference()
        assertTrue("User reference should start with STAI-USER-", ref1.startsWith("STAI-USER-"))
        val ref2 = subscriptionManager.getUserReference()
        assertEquals("User reference should be persistent", ref1, ref2)
    }

    @Test
    fun testActivationCodeGenerationAndRedemption() {
        val userRef = subscriptionManager.getUserReference()
        val code = subscriptionManager.generateActivationCode(
            targetUserRef = userRef,
            plan = SubscriptionPlan.GOLDEN,
            validityDays = 30,
            paymentRef = "JAZZCASH_TID_9921"
        )

        assertTrue(code.startsWith("STAI-GOLD-"))
        assertTrue(code.contains("-30D-"))

        // Redeem on target user
        val result = subscriptionManager.redeemActivationCode(code)
        assertTrue("Code should be redeemed successfully", result.isSuccess)

        val state = subscriptionManager.subscriptionState.value
        assertEquals(SubscriptionStatus.ACTIVE, state.status)
        assertEquals(SubscriptionPlan.GOLDEN, state.currentPlan)
        assertTrue(state.isPrivilegedActive)

        // Try redeeming the same code again (Double redemption check)
        val secondResult = subscriptionManager.redeemActivationCode(code)
        assertFalse("Same code cannot be redeemed twice", secondResult.isSuccess)
    }

    @Test
    fun testActivationCodeRejectedOnDifferentUserReference() {
        val foreignUserRef = "STAI-USER-999999"
        val codeForForeignUser = subscriptionManager.generateActivationCode(
            targetUserRef = foreignUserRef,
            plan = SubscriptionPlan.SILVER,
            validityDays = 30
        )

        // Current device user reference is different
        val result = subscriptionManager.redeemActivationCode(codeForForeignUser)
        assertFalse("Code issued for another user reference must be rejected", result.isSuccess)
    }

    @Test
    fun testTamperedActivationCodeRejected() {
        val userRef = subscriptionManager.getUserReference()
        val validCode = subscriptionManager.generateActivationCode(
            targetUserRef = userRef,
            plan = SubscriptionPlan.BRONZE,
            validityDays = 30
        )

        // Tamper with code
        val tamperedCode = validCode.dropLast(1) + "X"
        val result = subscriptionManager.redeemActivationCode(tamperedCode)
        assertFalse("Tampered code signature must fail validation", result.isSuccess)
    }

    @Test
    fun testInfluencerAttributionAndCommissionCalculation() {
        subscriptionManager.setReferralCode("CRYPTO_PRO_20")
        val userRef = subscriptionManager.getUserReference()

        val code = subscriptionManager.generateActivationCode(
            targetUserRef = userRef,
            plan = SubscriptionPlan.GOLDEN, // PKR 3000 -> 20% = PKR 600
            validityDays = 30
        )

        subscriptionManager.redeemActivationCode(code)

        val commissions = subscriptionManager.commissions.value
        assertEquals(1, commissions.size)

        val record = commissions[0]
        assertEquals("CRYPTO_PRO_20", record.influencerCode)
        assertEquals(3000, record.saleAmountPkr)
        assertEquals(600, record.commissionAmountPkr)
        assertEquals(CommissionStatus.HELD, record.status)
        assertFalse("Newly verified commission must be held during 7-day rule", record.isSevenDaysPassed)
    }

    @Test
    fun testAuditableReversalsAndPayouts() {
        subscriptionManager.setReferralCode("PARTNER_99")
        val userRef = subscriptionManager.getUserReference()

        val code = subscriptionManager.generateActivationCode(
            targetUserRef = userRef,
            plan = SubscriptionPlan.SILVER, // PKR 2000 -> 20% = PKR 400
            validityDays = 30
        )
        subscriptionManager.redeemActivationCode(code)

        val recordId = subscriptionManager.commissions.value[0].id

        val revResult = subscriptionManager.reverseOrCancelCommission(recordId, "Customer refund request")
        assertTrue("Reversal should succeed", revResult.isSuccess)

        val updated = subscriptionManager.commissions.value[0]
        assertEquals(CommissionStatus.REVERSED, updated.status)
        assertTrue(updated.notes?.contains("Customer refund request") == true)
    }

    @Test
    fun testDirectPreferencesTamperingRevertedToFree() {
        val prefs = context.getSharedPreferences("stai_subscription_vault", Context.MODE_PRIVATE)
        // Simulate malicious user directly altering local preferences to forge a GOLDEN subscription
        prefs.edit()
            .putString("active_plan_id", "GOLD")
            .putString("subscription_status", "ACTIVE")
            .putString("active_code", "FAKE_FORGED_CODE_123")
            .commit()

        val secureManager = SubscriptionManager(context)
        val state = secureManager.subscriptionState.value
        assertEquals("Locally forged paid plan without valid HMAC signature must be reverted to Free Trial", SubscriptionPlan.FREE_TRIAL, state.currentPlan)
        assertEquals(SubscriptionStatus.TRIAL, state.status)
    }

    @Test
    fun testAdminRoleEmailVerification() {
        val adminEmail = com.example.ui.viewmodel.TradingViewModel.ADMIN_EMAIL
        assertEquals("sarfarazmech00@gmail.com", adminEmail)

        val isOwnerAdmin = "sarfarazmech00@gmail.com".equals(adminEmail, ignoreCase = true)
        val isOrdinaryUserAdmin = "randomuser@gmail.com".equals(adminEmail, ignoreCase = true)

        assertTrue("sarfarazmech00@gmail.com must be recognized as verified administrator", isOwnerAdmin)
        assertFalse("Ordinary user emails must NOT be recognized as administrator", isOrdinaryUserAdmin)
    }

    @Test
    fun testPendingPaidPlanRemainsInactiveUntilVerified() {
        val userRef = subscriptionManager.getUserReference()
        // User selects Golden in onboarding
        subscriptionManager.setPendingPlanSelection(SubscriptionPlan.GOLDEN)
        val pending = subscriptionManager.getPendingPlanSelection()
        assertEquals(SubscriptionPlan.GOLDEN, pending)

        // But actual subscription status must remain FREE/TRIAL and unactivated
        val state = subscriptionManager.subscriptionState.value
        assertFalse("Selecting a paid plan must NOT grant immediate active privileges", state.isPrivilegedActive)
        assertEquals(SubscriptionPlan.FREE_TRIAL, state.currentPlan)
        assertFalse(state.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS))
    }

    @Test
    fun testPlanFeatureAccessMatrix() {
        val userRef = subscriptionManager.getUserReference()

        // 1. Free Trial State
        val freeState = subscriptionManager.subscriptionState.value
        assertFalse(freeState.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS))
        assertFalse(freeState.hasAccessTo(com.example.domain.model.AppFeature.VIP_SIGNALS_5M_1D))
        assertFalse(freeState.hasAccessTo(com.example.domain.model.AppFeature.PRECISE_TP_SL_TARGETS))
        assertFalse(freeState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW))

        // 2. Activate Bronze
        val bronzeCode = subscriptionManager.generateActivationCode(userRef, SubscriptionPlan.BRONZE, 30)
        subscriptionManager.redeemActivationCode(bronzeCode)
        val bronzeState = subscriptionManager.subscriptionState.value
        assertTrue(bronzeState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW))
        assertFalse(bronzeState.hasAccessTo(com.example.domain.model.AppFeature.PRECISE_TP_SL_TARGETS))
        assertFalse(bronzeState.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS))

        // 3. Clear and Activate Silver
        context.getSharedPreferences("stai_subscription_vault", Context.MODE_PRIVATE).edit().clear().commit()
        val silverManager = SubscriptionManager(context)
        val sUserRef = silverManager.getUserReference()
        val silverCode = silverManager.generateActivationCode(sUserRef, SubscriptionPlan.SILVER, 30)
        silverManager.redeemActivationCode(silverCode)
        val silverState = silverManager.subscriptionState.value
        assertTrue(silverState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW))
        assertTrue(silverState.hasAccessTo(com.example.domain.model.AppFeature.PRECISE_TP_SL_TARGETS))
        assertTrue(silverState.hasAccessTo(com.example.domain.model.AppFeature.PAPER_TRADING_FULL))
        assertFalse(silverState.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS))
        assertFalse(silverState.hasAccessTo(com.example.domain.model.AppFeature.VIP_SIGNALS_5M_1D))

        // 4. Clear and Activate Golden
        context.getSharedPreferences("stai_subscription_vault", Context.MODE_PRIVATE).edit().clear().commit()
        val goldenManager = SubscriptionManager(context)
        val gUserRef = goldenManager.getUserReference()
        val goldenCode = goldenManager.generateActivationCode(gUserRef, SubscriptionPlan.GOLDEN, 30)
        goldenManager.redeemActivationCode(goldenCode)
        val goldenState = goldenManager.subscriptionState.value
        assertTrue(goldenState.hasAccessTo(com.example.domain.model.AppFeature.FUTURES_ANALYTICS_OVERVIEW))
        assertTrue(goldenState.hasAccessTo(com.example.domain.model.AppFeature.PRECISE_TP_SL_TARGETS))
        assertTrue(goldenState.hasAccessTo(com.example.domain.model.AppFeature.PAPER_TRADING_FULL))
        assertTrue(goldenState.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS))
        assertTrue(goldenState.hasAccessTo(com.example.domain.model.AppFeature.VIP_SIGNALS_5M_1D))

        // 5. Admin bypass test
        assertTrue("Admin must have test access to all features", freeState.hasAccessTo(com.example.domain.model.AppFeature.SMC_ADVANCED_OVERLAYS, isAdmin = true))
    }
}
