package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class SmartTradeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebaseSafely()
    }

    private fun initFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Log.d("SmartTradeApplication", "FirebaseApp initialized automatically: ${app?.name}")
            }
        } catch (e: Exception) {
            Log.w("SmartTradeApplication", "Automatic FirebaseApp init failed, attempting programmatic fallback", e)
            try {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:613712404065:android:7aa69d4126a01da247d420")
                    .setApiKey("AIzaSyBhZqkU9ZJWnIASKM4LSQ2Me3X_qLkTLG0")
                    .setProjectId("gen-lang-client-0963937245")
                    .setStorageBucket("gen-lang-client-0963937245.firebasestorage.app")
                    .setDatabaseUrl("https://gen-lang-client-0963937245-default-rtdb.firebaseio.com")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("SmartTradeApplication", "FirebaseApp initialized with fallback FirebaseOptions")
            } catch (fallbackError: Exception) {
                Log.e("SmartTradeApplication", "Firebase fallback initialization also failed (running in offline fallback mode)", fallbackError)
            }
        }
    }
}
