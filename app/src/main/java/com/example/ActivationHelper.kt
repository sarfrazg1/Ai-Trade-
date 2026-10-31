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
        val code = enteredCode.trim().uppercase()
        val deviceId = getDeviceId(context)

        if (code.isEmpty()) {
            onDone(false, "کوڈ لکھیں / Please enter activation code")
            return
        }

        val database = getDatabase(context)
        if (database == null) {
            onDone(false, "انٹرنیٹ یا فائر بیس سروس چیک کریں / Firebase service unavailable")
            return
        }

        try {
            val codeRef = database.getReference("activation_codes").child(code)

            codeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (!snapshot.exists()) {
                            onDone(false, "غلط کوڈ ہے / Invalid code")
                            return
                        }

                        val isUsed = snapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                        val usedDevice = snapshot.child("usedByDevice").getValue(String::class.java) ?: ""

                        if (isUsed && usedDevice.isNotEmpty() && usedDevice != deviceId) {
                            onDone(false, "یہ کوڈ دوسرے موبائل پر استعمال ہو چکا / Code already used on another device")
                            return
                        }

                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, 30)
                        val newExpireDate = calendar.time

                        val plan = snapshot.child("plan").getValue(String::class.java) ?: "bronze"

                        // Realtime DB updates
                        val updates = mapOf(
                            "isUsed" to true,
                            "usedByDevice" to deviceId,
                            "activatedAt" to ServerValue.TIMESTAMP,
                            "expiresAt" to newExpireDate.time,
                            "plan" to plan
                        )

                        codeRef.updateChildren(updates).addOnSuccessListener {
                            try {
                                val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
                                pref.edit().putBoolean("isActivated", true)
                                    .putString("plan", plan)
                                    .putString("active_code", code)
                                    .putLong("expireTime", newExpireDate.time).apply()
                                onDone(true, "$plan پلان 30 دن کے لیے ایکٹیو ہو گیا / $plan plan activated for 30 days")
                            } catch (e: Exception) {
                                onDone(true, "$plan پلان ایکٹیو ہو گیا")
                            }
                        }.addOnFailureListener { err ->
                            onDone(false, "انٹرنیٹ چیک کریں: ${err.localizedMessage ?: "Database update failed"}")
                        }
                    } catch (e: Exception) {
                        onDone(false, "خرابی: ${e.localizedMessage ?: "Activation processing error"}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onDone(false, "انٹرنیٹ چیک کریں / Database error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            onDone(false, "فائر بیس سروس چیک کریں / Firebase error: ${e.localizedMessage}")
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
