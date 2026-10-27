package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.PaperTradeEntity
import com.example.domain.model.UserSubscriptionState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(
    private val db: FirebaseFirestore
) {
    // Convenience constructor resolving the named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    companion object {
        private const val TAG = "FirestoreSyncRepo"
    }

    suspend fun syncUserProfile(
        user: FirebaseUser,
        subscriptionState: UserSubscriptionState
    ): Result<Unit> {
        return try {
            val userDoc = db.collection("users").document(user.uid)
            val data = hashMapOf<String, Any>(
                "id" to user.uid,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: ""),
                "subscriptionPlan" to subscriptionState.currentPlan.name,
                "subscriptionStatus" to subscriptionState.status.name,
                "userReferenceId" to subscriptionState.userReference,
                "referralCode" to (subscriptionState.referralCode ?: ""),
                "termsAccepted" to subscriptionState.termsAccepted,
                "disclaimerAccepted" to subscriptionState.disclaimerAccepted,
                "lastSyncedAt" to FieldValue.serverTimestamp()
            )
            if (subscriptionState.expiresAt != null) {
                data["expiresAt"] = subscriptionState.expiresAt
            }
            if (subscriptionState.activatedAt != null) {
                data["activatedAt"] = subscriptionState.activatedAt
            }

            userDoc.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced user profile for ${user.uid}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync user profile", e)
            Result.failure(e)
        }
    }

    suspend fun fetchUserProfile(userId: String): Map<String, Any>? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) snapshot.data else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user profile for $userId", e)
            null
        }
    }

    suspend fun syncPaperTrade(userId: String, trade: PaperTradeEntity): Result<Unit> {
        return try {
            val tradeDocId = "${userId}_${trade.id}"
            val docRef = db.collection("paperTrades").document(tradeDocId)
            val data = hashMapOf<String, Any>(
                "id" to tradeDocId,
                "userId" to userId,
                "symbol" to trade.symbol,
                "entryPrice" to trade.entryPrice,
                "stopLoss" to trade.stopLoss,
                "tp1" to trade.tp1,
                "tp2" to trade.tp2,
                "margin" to trade.margin,
                "leverage" to trade.leverage,
                "quantity" to trade.quantity,
                "side" to trade.signalType,
                "status" to trade.status,
                "pnl" to (trade.pnlUsd ?: 0.0),
                "openTime" to trade.openTime,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced trade $tradeDocId to Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync paper trade", e)
            Result.failure(e)
        }
    }

    fun observePaperTrades(userId: String?): Flow<List<Map<String, Any>>> {
        if (userId.isNullOrBlank()) return flowOf(emptyList())
        return db.collection("paperTrades")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.data }
            }
    }

    suspend fun recordActivationRequest(
        userId: String,
        userReference: String,
        plan: String,
        paymentReference: String = "",
        influencerCode: String = "",
        code: String? = null
    ): Result<Unit> {
        return try {
            val requestId = "${userId}_${System.currentTimeMillis()}"
            val docRef = db.collection("activationRequests").document(requestId)
            val data = hashMapOf<String, Any>(
                "id" to requestId,
                "userId" to userId,
                "userReferenceId" to userReference,
                "plan" to plan,
                "paymentReference" to paymentReference,
                "influencerCode" to influencerCode,
                "status" to "PENDING_VERIFICATION",
                "activationCode" to (code ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Log.d(TAG, "Recorded activation request $requestId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record activation request", e)
            Result.failure(e)
        }
    }

    fun observeActivationRequests(userId: String?): Flow<List<Map<String, Any>>> {
        if (userId.isNullOrBlank()) return flowOf(emptyList())
        return db.collection("activationRequests")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.data }
            }
    }

    suspend fun redeemActivationCodeCloud(
        user: FirebaseUser,
        code: String,
        currentUserRef: String,
        influencerCode: String?
    ): Result<String> {
        return try {
            val cleanCode = code.trim().uppercase()
            val codeDocRef = db.collection("activationCodes").document(cleanCode)
            val codeSnap = codeDocRef.get().await()

            if (!codeSnap.exists()) {
                // If not found in cloud yet (e.g. offline-generated or direct HMAC), fallback gracefully
                return Result.failure(IllegalArgumentException("Activation code not found in Cloud Registry. Please ensure the code is correct or wait for WhatsApp admin verification."))
            }

            val isRedeemed = codeSnap.getBoolean("isRedeemed") ?: false
            if (isRedeemed) {
                return Result.failure(IllegalStateException("This activation code has already been redeemed."))
            }

            val isRevoked = codeSnap.getBoolean("isRevoked") ?: false
            if (isRevoked) {
                return Result.failure(IllegalStateException("This activation code has been revoked by the administrator."))
            }

            val targetUserRef = codeSnap.getString("userReference") ?: ""
            if (targetUserRef.isNotBlank() && !targetUserRef.equals(currentUserRef, ignoreCase = true)) {
                return Result.failure(IllegalArgumentException("This activation code is bound to User Reference ($targetUserRef). It cannot be redeemed on your account."))
            }

            val planId = codeSnap.getString("plan") ?: "FREE"
            val validityDays = codeSnap.getLong("validityDays")?.toInt() ?: 30
            val now = System.currentTimeMillis()
            val expiresAt = now + (validityDays.toLong() * 24 * 60 * 60 * 1000L)

            // 1. Mark code redeemed atomically
            codeDocRef.update(
                mapOf(
                    "isRedeemed" to true,
                    "redeemedAt" to FieldValue.serverTimestamp(),
                    "redeemedByUid" to user.uid,
                    "redeemedByUserRef" to currentUserRef
                )
            ).await()

            // 2. Update user profile in Firestore
            val userDoc = db.collection("users").document(user.uid)
            val profileUpdate = hashMapOf<String, Any>(
                "subscriptionPlan" to planId,
                "subscriptionStatus" to "ACTIVE",
                "activatedAt" to now,
                "expiresAt" to expiresAt,
                "activationCode" to cleanCode,
                "lastSyncedAt" to FieldValue.serverTimestamp()
            )
            userDoc.set(profileUpdate, SetOptions.merge()).await()

            // 3. Record Influencer Commission if referral code is present
            val effectiveReferral = influencerCode ?: codeSnap.getString("referralCode")
            if (!effectiveReferral.isNullOrBlank()) {
                val commissionPkr = when (planId) {
                    "GOLD" -> 600
                    "SILV" -> 400
                    "BRNZ" -> 200
                    else -> 0
                }
                if (commissionPkr > 0) {
                    val commId = "COMM_${System.currentTimeMillis()}_${cleanCode.takeLast(4)}"
                    val commDoc = db.collection("commissions").document(commId)
                    commDoc.set(
                        mapOf(
                            "id" to commId,
                            "influencerCode" to effectiveReferral.uppercase(),
                            "userReference" to currentUserRef,
                            "plan" to planId,
                            "commissionAmountPkr" to commissionPkr,
                            "status" to "HELD",
                            "eligibleAt" to (now + (7L * 24 * 60 * 60 * 1000L)),
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            Result.success("Subscription activated successfully for plan $planId ($validityDays days)!")
        } catch (e: Exception) {
            Log.e(TAG, "Error redeeming code in cloud", e)
            Result.failure(e)
        }
    }

    suspend fun adminIssueCodeCloud(
        code: String,
        targetUserRef: String,
        plan: String,
        validityDays: Int = 30,
        paymentRef: String = "MANUAL_WHATSAPP",
        referralCode: String = ""
    ): Result<Unit> {
        return try {
            val docRef = db.collection("activationCodes").document(code.trim().uppercase())
            val data = hashMapOf<String, Any>(
                "code" to code.trim().uppercase(),
                "userReference" to targetUserRef.trim().uppercase(),
                "plan" to plan,
                "validityDays" to validityDays,
                "paymentReference" to paymentRef,
                "referralCode" to referralCode.trim().uppercase(),
                "isRedeemed" to false,
                "isRevoked" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Admin failed to issue code in cloud", e)
            Result.failure(e)
        }
    }

    fun observeInfluencerCommissions(influencerCode: String): Flow<List<Map<String, Any>>> {
        if (influencerCode.isBlank()) return flowOf(emptyList())
        return db.collection("commissions")
            .whereEqualTo("influencerCode", influencerCode.trim().uppercase())
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.data }
            }
    }
}
