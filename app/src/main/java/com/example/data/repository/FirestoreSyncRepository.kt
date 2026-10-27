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
                "status" to "REQUESTED",
                "activationCode" to (code ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to record activation request", e)
            Result.failure(e)
        }
    }
}
