package com.example

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

object ActivationHelper {

    private fun getDatabase(context: Context): FirebaseDatabase? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val app = FirebaseApp.initializeApp(context)
                if (app == null) {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:613712404065:android:7aa69d4126a01da247d420")
                        .setApiKey("AIzaSyBhZqkU9ZJWnIASKM4LSQ2Me3X_qLkTLG0")
                        .setProjectId("gen-lang-client-0963937245")
                        .setStorageBucket("gen-lang-client-0963937245.firebasestorage.app")
                        .setDatabaseUrl("https://gen-lang-client-0963937245-default-rtdb.firebaseio.com")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                }
            }
            FirebaseDatabase.getInstance("https://gen-lang-client-0963937245-default-rtdb.firebaseio.com")
        } catch (e: Exception) {
            Log.e("ActivationHelper", "FirebaseDatabase custom URL initialization error", e)
            try {
                FirebaseDatabase.getInstance()
            } catch (e2: Exception) {
                Log.e("ActivationHelper", "FirebaseDatabase default initialization error", e2)
                null
            }
        }
    }

    fun getDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_DEVICE"
        } catch (e: Exception) {
            "UNKNOWN_DEVICE"
        }
    }

    fun autoActivate(context: Context, enteredCode: String, onDone: (Boolean, String) -> Unit) {
        val code = enteredCode.trim()
        val deviceId = getDeviceId(context)

        if (code.isEmpty()) {
            onDone(false, "Invalid Code")
            return
        }

        val database = getDatabase(context)
        if (database == null) {
            onDone(false, "Firebase service unavailable")
            return
        }

        try {
            // Reference activation_codes -> activationCodes
            val activationCodesRef = database.getReference("activation_codes")

            activationCodesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val candidateList: Iterable<DataSnapshot>? = when {
                            snapshot.hasChild("activationCodes") -> snapshot.child("activationCodes").children
                            snapshot.exists() && snapshot.childrenCount > 0 -> snapshot.children
                            else -> null
                        }

                        if (candidateList != null) {
                            searchAndProcessCode(context, candidateList, code, deviceId, onDone)
                        } else {
                            // Fallback check root 'activationCodes'
                            database.getReference("activationCodes").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(altSnapshot: DataSnapshot) {
                                    if (altSnapshot.exists() && altSnapshot.childrenCount > 0) {
                                        searchAndProcessCode(context, altSnapshot.children, code, deviceId, onDone)
                                    } else {
                                        onDone(false, "Invalid Code")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("ActivationHelper", "Database error: ${error.message}")
                                    onDone(false, "Invalid Code")
                                }
                            })
                        }
                    } catch (e: Exception) {
                        Log.e("ActivationHelper", "Error processing activation codes", e)
                        onDone(false, "Invalid Code")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ActivationHelper", "Database error: ${error.message}")
                    onDone(false, "Database error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("ActivationHelper", "Firebase error: ${e.localizedMessage}")
            onDone(false, "Firebase error: ${e.localizedMessage ?: "Unknown"}")
        }
    }

    private fun searchAndProcessCode(
        context: Context,
        codesList: Iterable<DataSnapshot>,
        enteredCode: String,
        deviceId: String,
        onDone: (Boolean, String) -> Unit
    ) {
        val targetCode = enteredCode.trim()
        var matchedChild: DataSnapshot? = null
        var matchedCode = ""
        var matchedPlan = "GOLDEN"
        var matchedStatus = ""

        // Loop through entire activationCodes list and search for entered code (do not search by direct child path)
        for (item in codesList) {
            val codeVal = item.child("code").getValue(String::class.java)?.trim() ?: ""
            if (codeVal.equals(targetCode, ignoreCase = true) ||
                codeVal.replace("-", "").equals(targetCode.replace("-", ""), ignoreCase = true)
            ) {
                matchedChild = item
                matchedCode = codeVal
                matchedPlan = item.child("plan").getValue(String::class.java)?.trim() ?: "GOLDEN"
                matchedStatus = item.child("status").getValue(String::class.java)?.trim()?.lowercase() ?: ""
                val isUsedBool = item.child("isUsed").getValue(Boolean::class.java) ?: false
                if (matchedStatus.isEmpty() && isUsedBool) {
                    matchedStatus = "used"
                }
                break
            }
        }

        // If not found, show Invalid Code
        if (matchedChild == null) {
            onDone(false, "Invalid Code")
            return
        }

        // If already used, show Already Used
        if (matchedStatus == "used") {
            onDone(false, "Already Used")
            return
        }

        // If found and status unused, show Valid and update status to used
        if (matchedStatus == "unused" || matchedStatus.isEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 30)
            val newExpireDate = calendar.time

            val updates = mapOf<String, Any>(
                "status" to "used",
                "isUsed" to true,
                "usedByDevice" to deviceId,
                "activatedAt" to ServerValue.TIMESTAMP,
                "expiresAt" to newExpireDate.time,
                "plan" to matchedPlan
            )

            matchedChild.ref.updateChildren(updates).addOnSuccessListener {
                try {
                    val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
                    pref.edit()
                        .putBoolean("isActivated", true)
                        .putString("plan", matchedPlan.lowercase())
                        .putString("active_code", matchedCode)
                        .putLong("expireTime", newExpireDate.time)
                        .apply()
                    onDone(true, "Valid")
                } catch (e: Exception) {
                    onDone(true, "Valid")
                }
            }.addOnFailureListener { err ->
                onDone(false, "Update failed: ${err.localizedMessage ?: "Database error"}")
            }
        } else {
            onDone(false, "Already Used")
        }
    }

    fun isAppActivated(context: Context): Boolean {
        return try {
            val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
            pref.getBoolean("isActivated", false) && pref.getLong("expireTime", 0) > System.currentTimeMillis()
        } catch (e: Exception) {
            false
        }
    }

    fun getActivatedPlan(context: Context): String? {
        return try {
            if (!isAppActivated(context)) return null
            val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
            pref.getString("plan", "bronze")
        } catch (e: Exception) {
            null
        }
    }

    fun getActivatedExpiry(context: Context): Long {
        return try {
            val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
            pref.getLong("expireTime", 0L)
        } catch (e: Exception) {
            0L
        }
    }

    fun getActivatedCode(context: Context): String? {
        return try {
            val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
            pref.getString("active_code", null)
        } catch (e: Exception) {
            null
        }
    }
}
