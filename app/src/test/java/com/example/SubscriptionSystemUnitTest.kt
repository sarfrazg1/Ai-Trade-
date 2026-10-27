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
}
