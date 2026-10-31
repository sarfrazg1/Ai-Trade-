package com.example

import android.content.Context
import android.provider.Settings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

object ActivationHelper {
    // Realtime Database instance for activation codes
    private val db = FirebaseDatabase.getInstance(
        "https://gen-lang-client-0963937245-default-rtdb.firebaseio.com"
    )

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_DEVICE"
    }

    fun autoActivate(context: Context, enteredCode: String, onDone: (Boolean, String) -> Unit) {
        val code = enteredCode.trim().uppercase()
        val deviceId = getDeviceId(context)

        if (code.isEmpty()) {
            onDone(false, "کوڈ لکھیں / Please enter activation code")
            return
        }

        val codeRef = db.getReference("activation_codes").child(code)

        codeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                    val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
                    pref.edit().putBoolean("isActivated", true)
                        .putString("plan", plan)
                        .putString("active_code", code)
                        .putLong("expireTime", newExpireDate.time).apply()
                    onDone(true, "$plan پلان 30 دن کے لیے ایکٹیو ہو گیا / $plan plan activated for 30 days")
                }.addOnFailureListener {
                    onDone(false, "انٹرنیٹ چیک کریں / Please check internet connection")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onDone(false, "انٹرنیٹ چیک کریں / Database error: ${error.message}")
            }
        })
    }

    fun isAppActivated(context: Context): Boolean {
        val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
        return pref.getBoolean("isActivated", false) && pref.getLong("expireTime", 0) > System.currentTimeMillis()
    }

    fun getActivatedPlan(context: Context): String? {
        if (!isAppActivated(context)) return null
        val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
        return pref.getString("plan", "bronze")
    }

    fun getActivatedExpiry(context: Context): Long {
        val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
        return pref.getLong("expireTime", 0L)
    }

    fun getActivatedCode(context: Context): String? {
        val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
        return pref.getString("active_code", null)
    }
}
