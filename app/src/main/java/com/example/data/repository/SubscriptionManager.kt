package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.AdminAuditRecord
import com.example.domain.model.CommissionStatus
import com.example.domain.model.InfluencerCommissionRecord
import com.example.domain.model.SubscriptionPlan
import com.example.domain.model.SubscriptionStatus
import com.example.domain.model.UserSubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SubscriptionManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("stai_subscription_vault", Context.MODE_PRIVATE)

    // Internal secret key salt used for HMAC-SHA256 signature verification
    private val hmacSecret = "STAI_CRYPTO_ENTERPRISE_SECRET_2026_KEY_SA".toByteArray(StandardCharsets.UTF_8)

    private val _subscriptionState = MutableStateFlow(loadSubscriptionState())
    val subscriptionState: StateFlow<UserSubscriptionState> = _subscriptionState.asStateFlow()

    private val _issuedCodes = MutableStateFlow<List<JSONObject>>(loadIssuedCodesJson())
    private val _commissions = MutableStateFlow<List<InfluencerCommissionRecord>>(loadCommissions())
    val commissions: StateFlow<List<InfluencerCommissionRecord>> = _commissions.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminAuditRecord>>(loadAuditLogs())
    val auditLogs: StateFlow<List<AdminAuditRecord>> = _auditLogs.asStateFlow()

    init {
        // Ensure user reference exists
        getUserReference()
        // Refresh eligibility for any commissions older than 7 days
        refreshCommissionEligibility()
    }

    fun getUserReference(): String {
        var ref = prefs.getString("user_reference", null)
        if (ref.isNullOrBlank()) {
            val randomNum = 100000 + SecureRandom().nextInt(900000)
            ref = "STAI-USER-$randomNum"
            prefs.edit().putString("user_reference", ref).apply()
        }
        return ref
    }

    fun setReferralCode(code: String): Boolean {
        val clean = code.trim().uppercase()
        if (clean.length in 3..15 && clean.matches(Regex("^[A-Z0-9_-]+$"))) {
            prefs.edit().putString("referral_code", clean).apply()
            _subscriptionState.value = _subscriptionState.value.copy(referralCode = clean)
            return true
        }
        return false
    }

    fun recordLegalConsent(disclaimer: Boolean, terms: Boolean) {
        prefs.edit()
            .putBoolean("disclaimer_accepted", disclaimer)
            .putBoolean("terms_accepted", terms)
            .putLong("consent_timestamp", System.currentTimeMillis())
            .apply()
        _subscriptionState.value = _subscriptionState.value.copy(
            disclaimerAccepted = disclaimer,
            termsAccepted = terms,
            consentTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Generates a cryptographically signed, bound activation code:
     * Format: STAI-[PLAN]-[USER_REF_SUFFIX]-[VALID_DAYS]-[HMAC_8_CHARS]
     * E.g. STAI-GOLD-8492-30D-9E4B1A7F
     */
    fun generateActivationCode(
        targetUserRef: String,
        plan: SubscriptionPlan,
        validityDays: Int = 30,
        paymentRef: String = "MANUAL_VERIFIED"
    ): String {
        val cleanUserRef = targetUserRef.trim().uppercase()
        val userSuffix = if (cleanUserRef.length >= 4) cleanUserRef.takeLast(4) else "0000"
        val payload = "${plan.id}:$userSuffix:$validityDays:${cleanUserRef}"
        val signature = calculateHmacSha256(payload, hmacSecret).take(8).uppercase(Locale.US)
        val code = "STAI-${plan.id}-$userSuffix-${validityDays}D-$signature"

        // Save in ledger
        saveIssuedCodeRecord(
            code = code,
            userRef = cleanUserRef,
            plan = plan,
            validityDays = validityDays,
            paymentRef = paymentRef
        )

        addAuditLog(
            action = "CODE_ISSUED",
            userRef = cleanUserRef,
            plan = plan.id,
            details = "Code $code generated with payment ref: $paymentRef (Validity: $validityDays days)"
        )

        return code
    }

    /**
     * Validates and redeems an activation code on device.
     */
    fun redeemActivationCode(enteredCode: String): Result<String> {
        val code = enteredCode.trim().uppercase()
        val currentUserRef = getUserReference()

        val parts = code.split("-")
        if (parts.size != 5 || parts[0] != "STAI") {
            return Result.failure(IllegalArgumentException("Invalid code format. Expected format: STAI-PLAN-XXXX-30D-XXXXXXXX"))
        }

        val planId = parts[1]
        val userSuffix = parts[2]
        val validityPart = parts[3]
        val signature = parts[4]

        val plan = SubscriptionPlan.fromId(planId)
        if (plan == SubscriptionPlan.FREE_TRIAL) {
            return Result.failure(IllegalArgumentException("Invalid plan identifier in code."))
        }

        val validityDays = validityPart.removeSuffix("D").toIntOrNull() ?: 30

        // 1. Verify User Reference Binding
        val expectedSuffix = if (currentUserRef.length >= 4) currentUserRef.takeLast(4) else "0000"
        if (userSuffix != expectedSuffix) {
            return Result.failure(IllegalArgumentException("This activation code was issued for a different User Reference ID ($userSuffix). It cannot be redeemed on this account."))
        }

        // 2. Verify Cryptographic HMAC Signature
        val expectedPayload = "${plan.id}:$userSuffix:$validityDays:${currentUserRef}"
        val expectedSignature = calculateHmacSha256(expectedPayload, hmacSecret).take(8).uppercase(Locale.US)
        if (signature != expectedSignature) {
            return Result.failure(IllegalArgumentException("Invalid activation code signature. Tampering or invalid code detected."))
        }

        // 3. Verify Against Redeemed Codes Registry (Single-Use Guarantee)
        val redeemedSet = prefs.getStringSet("redeemed_codes", emptySet()) ?: emptySet()
        if (redeemedSet.contains(code)) {
            return Result.failure(IllegalStateException("This activation code has already been redeemed and cannot be used again."))
        }

        // 4. Verify Revocation Status
        val revokedSet = prefs.getStringSet("revoked_codes", emptySet()) ?: emptySet()
        if (revokedSet.contains(code)) {
            return Result.failure(IllegalStateException("This activation code has been revoked by the administrator."))
        }

        // 5. Activate Subscription
        val now = System.currentTimeMillis()
        val expiryTime = now + (validityDays.toLong() * 24 * 60 * 60 * 1000L)

        val updatedRedeemed = redeemedSet.toMutableSet().apply { add(code) }
        prefs.edit()
            .putStringSet("redeemed_codes", updatedRedeemed)
            .putString("active_plan_id", plan.id)
            .putString("subscription_status", SubscriptionStatus.ACTIVE.name)
            .putString("active_code", code)
            .putLong("activated_at", now)
            .putLong("expires_at", expiryTime)
            .apply()

        _subscriptionState.value = _subscriptionState.value.copy(
            currentPlan = plan,
            status = SubscriptionStatus.ACTIVE,
            activationCode = code,
            activatedAt = now,
            expiresAt = expiryTime
        )

        // 6. If referral code is present, record 20% commission with 7-day holding rule
        val referral = _subscriptionState.value.referralCode
        if (!referral.isNullOrBlank()) {
            recordInfluencerSale(
                influencerCode = referral,
                userRef = currentUserRef,
                plan = plan
            )
        }

        addAuditLog(
            action = "CODE_REDEEMED",
            userRef = currentUserRef,
            plan = plan.id,
            details = "Code $code successfully redeemed. Subscription active until ${java.util.Date(expiryTime)}."
        )

        return Result.success("Subscription successfully activated for ${plan.title} (Valid for $validityDays days)!")
    }

    /**
     * Records an influencer sale with 20% commission and 7-day waiting period.
     */
    private fun recordInfluencerSale(
        influencerCode: String,
        userRef: String,
        plan: SubscriptionPlan
    ) {
        val now = System.currentTimeMillis()
        val commission = InfluencerCommissionRecord(
            id = "COMM-${now}-${SecureRandom().nextInt(9999)}",
            influencerCode = influencerCode.uppercase(),
            userReference = userRef,
            plan = plan,
            saleAmountPkr = plan.pricePkr,
            commissionAmountPkr = plan.commissionPkr,
            verifiedAt = now,
            eligibleAt = now + (7L * 24 * 60 * 60 * 1000L), // Strict 7-day rule
            status = CommissionStatus.HELD,
            notes = "Initial verified sale via WhatsApp activation"
        )

        val currentList = _commissions.value.toMutableList()
        currentList.add(0, commission)
        saveCommissions(currentList)
        _commissions.value = currentList

        addAuditLog(
            action = "COMMISSION_RECORDED",
            userRef = userRef,
            plan = plan.id,
            details = "Influencer ${influencerCode.uppercase()} credited PKR ${plan.commissionPkr} (7-day hold until ${java.util.Date(commission.eligibleAt)})"
        )
    }

    fun refreshCommissionEligibility() {
        val now = System.currentTimeMillis()
        var changed = false
        val updated = _commissions.value.map { item ->
            if (item.status == CommissionStatus.HELD && now >= item.eligibleAt) {
                changed = true
                item.copy(status = CommissionStatus.ELIGIBLE)
            } else {
                item
            }
        }
        if (changed) {
            saveCommissions(updated)
            _commissions.value = updated
        }
    }

    fun requestPayout(influencerCode: String): Result<String> {
        refreshCommissionEligibility()
        val cleanCode = influencerCode.trim().uppercase()
        var eligibleCount = 0
        var totalAmount = 0

        val updated = _commissions.value.map { item ->
            if (item.influencerCode.equals(cleanCode, ignoreCase = true) && item.status == CommissionStatus.ELIGIBLE) {
                eligibleCount++
                totalAmount += item.commissionAmountPkr
                item.copy(status = CommissionStatus.PAYOUT_REQUESTED)
            } else {
                item
            }
        }

        if (eligibleCount == 0) {
            return Result.failure(IllegalStateException("No eligible commissions available for payout. (Must wait 7 full days from payment verification)."))
        }

        saveCommissions(updated)
        _commissions.value = updated

        addAuditLog(
            action = "PAYOUT_REQUESTED",
            userRef = "INFLUENCER:$cleanCode",
            plan = "ALL",
            details = "Payout requested for $eligibleCount items totaling PKR $totalAmount"
        )

        return Result.success("Payout request submitted for PKR $totalAmount ($eligibleCount sales). Administrator will review and settle manually.")
    }

    fun markPayoutPaid(recordId: String, payoutRef: String): Result<String> {
        val now = System.currentTimeMillis()
        var target: InfluencerCommissionRecord? = null

        val updated = _commissions.value.map { item ->
            if (item.id == recordId) {
                target = item
                item.copy(
                    status = CommissionStatus.PAID,
                    payoutReference = payoutRef.ifBlank { "MANUAL_TRANSFER" },
                    payoutTimestamp = now
                )
            } else {
                item
            }
        }

        if (target == null) {
            return Result.failure(IllegalArgumentException("Commission record $recordId not found."))
        }

        saveCommissions(updated)
        _commissions.value = updated

        addAuditLog(
            action = "PAYOUT_SETTLED",
            userRef = "INFLUENCER:${target?.influencerCode}",
            plan = target?.plan?.id ?: "",
            details = "Paid PKR ${target?.commissionAmountPkr} with reference: $payoutRef"
        )

        return Result.success("Commission marked as PAID with reference $payoutRef")
    }

    fun reverseOrCancelCommission(recordId: String, reason: String): Result<String> {
        var target: InfluencerCommissionRecord? = null
        val updated = _commissions.value.map { item ->
            if (item.id == recordId) {
                target = item
                item.copy(
                    status = CommissionStatus.REVERSED,
                    notes = "Reversed: $reason"
                )
            } else {
                item
            }
        }

        if (target == null) {
            return Result.failure(IllegalArgumentException("Record $recordId not found."))
        }

        saveCommissions(updated)
        _commissions.value = updated

        addAuditLog(
            action = "COMMISSION_REVERSED",
            userRef = "INFLUENCER:${target?.influencerCode}",
            plan = target?.plan?.id ?: "",
            details = "Reversed commission PKR ${target?.commissionAmountPkr}. Reason: $reason"
        )

        return Result.success("Commission successfully reversed.")
    }

    fun revokeIssuedCode(code: String): Result<String> {
        val revokedSet = prefs.getStringSet("revoked_codes", emptySet()) ?: emptySet()
        val updated = revokedSet.toMutableSet().apply { add(code.trim().uppercase()) }
        prefs.edit().putStringSet("revoked_codes", updated).apply()

        addAuditLog(
            action = "CODE_REVOKED",
            userRef = "ADMIN",
            plan = "ALL",
            details = "Code $code revoked by administrator"
        )

        return Result.success("Code $code successfully revoked.")
    }

    private fun loadSubscriptionState(): UserSubscriptionState {
        val userRef = prefs.getString("user_reference", null) ?: "STAI-USER-100001"
        val planId = prefs.getString("active_plan_id", SubscriptionPlan.FREE_TRIAL.id)
        val statusStr = prefs.getString("subscription_status", SubscriptionStatus.TRIAL.name)
        val activeCode = prefs.getString("active_code", null)
        val activatedAt = if (prefs.contains("activated_at")) prefs.getLong("activated_at", 0L) else null
        val expiresAt = if (prefs.contains("expires_at")) prefs.getLong("expires_at", 0L) else null
        val referral = prefs.getString("referral_code", null)
        val disclaimer = prefs.getBoolean("disclaimer_accepted", false)
        val terms = prefs.getBoolean("terms_accepted", false)
        val consentTime = if (prefs.contains("consent_timestamp")) prefs.getLong("consent_timestamp", 0L) else null

        val plan = SubscriptionPlan.fromId(planId)
        val status = try {
            SubscriptionStatus.valueOf(statusStr ?: SubscriptionStatus.TRIAL.name)
        } catch (_: Exception) {
            SubscriptionStatus.TRIAL
        }

        return UserSubscriptionState(
            userReference = userRef,
            currentPlan = plan,
            status = status,
            activationCode = activeCode,
            activatedAt = activatedAt,
            expiresAt = expiresAt,
            referralCode = referral,
            disclaimerAccepted = disclaimer,
            termsAccepted = terms,
            consentTimestamp = consentTime
        )
    }

    private fun saveIssuedCodeRecord(
        code: String,
        userRef: String,
        plan: SubscriptionPlan,
        validityDays: Int,
        paymentRef: String
    ) {
        val jsonArray = JSONArray(prefs.getString("issued_codes_json", "[]"))
        val obj = JSONObject().apply {
            put("code", code)
            put("userRef", userRef)
            put("plan", plan.id)
            put("validityDays", validityDays)
            put("paymentRef", paymentRef)
            put("createdAt", System.currentTimeMillis())
        }
        jsonArray.put(obj)
        prefs.edit().putString("issued_codes_json", jsonArray.toString()).apply()
        _issuedCodes.value = loadIssuedCodesJson()
    }

    fun getIssuedCodes(): List<JSONObject> = loadIssuedCodesJson()

    private fun loadIssuedCodesJson(): List<JSONObject> {
        val raw = prefs.getString("issued_codes_json", "[]") ?: "[]"
        val list = mutableListOf<JSONObject>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                list.add(arr.getJSONObject(i))
            }
        } catch (_: Exception) {}
        return list
    }

    private fun saveCommissions(list: List<InfluencerCommissionRecord>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("influencerCode", item.influencerCode)
                put("userReference", item.userReference)
                put("plan", item.plan.id)
                put("saleAmountPkr", item.saleAmountPkr)
                put("commissionAmountPkr", item.commissionAmountPkr)
                put("verifiedAt", item.verifiedAt)
                put("eligibleAt", item.eligibleAt)
                put("status", item.status.name)
                put("payoutReference", item.payoutReference ?: "")
                put("payoutTimestamp", item.payoutTimestamp ?: 0L)
                put("notes", item.notes ?: "")
            }
            arr.put(obj)
        }
        prefs.edit().putString("commissions_ledger_json", arr.toString()).apply()
    }

    private fun loadCommissions(): List<InfluencerCommissionRecord> {
        val raw = prefs.getString("commissions_ledger_json", "[]") ?: "[]"
        val list = mutableListOf<InfluencerCommissionRecord>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val status = try {
                    CommissionStatus.valueOf(obj.optString("status", CommissionStatus.HELD.name))
                } catch (_: Exception) {
                    CommissionStatus.HELD
                }
                list.add(
                    InfluencerCommissionRecord(
                        id = obj.getString("id"),
                        influencerCode = obj.getString("influencerCode"),
                        userReference = obj.getString("userReference"),
                        plan = SubscriptionPlan.fromId(obj.getString("plan")),
                        saleAmountPkr = obj.getInt("saleAmountPkr"),
                        commissionAmountPkr = obj.getInt("commissionAmountPkr"),
                        verifiedAt = obj.getLong("verifiedAt"),
                        eligibleAt = obj.getLong("eligibleAt"),
                        status = status,
                        payoutReference = obj.optString("payoutReference").ifEmpty { null },
                        payoutTimestamp = if (obj.has("payoutTimestamp") && obj.getLong("payoutTimestamp") > 0) obj.getLong("payoutTimestamp") else null,
                        notes = obj.optString("notes").ifEmpty { null }
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun addAuditLog(action: String, userRef: String, plan: String, details: String) {
        val record = AdminAuditRecord(
            id = "AUDIT-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            action = action,
            userReference = userRef,
            plan = plan,
            details = details
        )
        val current = _auditLogs.value.toMutableList()
        current.add(0, record)
        if (current.size > 200) current.removeAt(current.lastIndex)
        _auditLogs.value = current

        val arr = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("timestamp", item.timestamp)
                put("action", item.action)
                put("userReference", item.userReference)
                put("plan", item.plan)
                put("details", item.details)
            }
            arr.put(obj)
        }
        prefs.edit().putString("admin_audit_logs_json", arr.toString()).apply()
    }

    private fun loadAuditLogs(): List<AdminAuditRecord> {
        val raw = prefs.getString("admin_audit_logs_json", "[]") ?: "[]"
        val list = mutableListOf<AdminAuditRecord>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    AdminAuditRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        action = obj.getString("action"),
                        userReference = obj.getString("userReference"),
                        plan = obj.getString("plan"),
                        details = obj.getString("details")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun calculateHmacSha256(data: String, key: ByteArray): String {
        return try {
            val signingKey = SecretKeySpec(key, "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(signingKey)
            val rawHmac = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            val sb = java.lang.StringBuilder()
            for (b in rawHmac) {
                sb.append(String.format("%02x", b))
            }
            sb.toString()
        } catch (_: Exception) {
            // Fallback digest if Mac unavailable
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(data.toByteArray(StandardCharsets.UTF_8))
            val sb = java.lang.StringBuilder()
            for (b in hash) sb.append(String.format("%02x", b))
            sb.toString()
        }
    }
}
