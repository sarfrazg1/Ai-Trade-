package com.yourappname

import android.content.Context
import android.provider.Settings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object ActivationHelper {
    private val db = FirebaseFirestore.getInstance()

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun autoActivate(context: Context, enteredCode: String, onDone: (Boolean, String) -> Unit) {
        val code = enteredCode.trim().uppercase()
        val deviceId = getDeviceId(context)
        
        db.collection("activation_codes").whereEqualTo("code", code).get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onDone(false, "غلط کوڈ ہے")
                    return@addOnSuccessListener
                }
                val doc = result.documents[0]
                val isUsed = doc.getBoolean("isUsed") ?: false
                val usedDevice = doc.getString("usedByDevice") ?: ""
                
                if (isUsed && usedDevice.isNotEmpty() && usedDevice != deviceId) {
                    onDone(false, "یہ کوڈ دوسرے موبائل پر استعمال ہو چکا")
                    return@addOnSuccessListener
                }

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                val newExpireDate = calendar.time

                doc.reference.update(
                    mapOf(
                        "isUsed" to true,
                        "usedByDevice" to deviceId,
                        "activatedAt" to FieldValue.serverTimestamp(),
                        "expiresAt" to newExpireDate
                    )
                ).addOnSuccessListener {
                    val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
                    pref.edit().putBoolean("isActivated", true)
                        .putString("plan", doc.getString("plan") ?: "bronze")
                        .putLong("expireTime", newExpireDate.time).apply()
                    onDone(true, "${doc.getString("plan")} پلان 30 دن کے لیے ایکٹیو ہو گیا")
                }
            }
            .addOnFailureListener { onDone(false, "انٹرنیٹ چیک کریں") }
    }

    fun isAppActivated(context: Context): Boolean {
        val pref = context.getSharedPreferences("app_activation", Context.MODE_PRIVATE)
        return pref.getBoolean("isActivated", false) && pref.getLong("expireTime", 0) > System.currentTimeMillis()
    }
}
