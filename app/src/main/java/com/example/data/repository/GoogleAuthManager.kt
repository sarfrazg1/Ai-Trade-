package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val auth: FirebaseAuth? = try {
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            val app = com.google.firebase.FirebaseApp.initializeApp(context)
            if (app == null) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:613712404065:android:7aa69d4126a01da247d420")
                    .setApiKey("AIzaSyBhZqkU9ZJWnIASKM4LSQ2Me3X_qLkTLG0")
                    .setProjectId("gen-lang-client-0963937245")
                    .setStorageBucket("gen-lang-client-0963937245.firebasestorage.app")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(context, options)
            }
        }
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.e("GoogleAuthManager", "FirebaseAuth initialization error", e)
        null
    }

    private val credentialManager: CredentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        auth?.addAuthStateListener(authStateListener)
    }

    private fun findActivity(ctx: Context): Activity? {
        var current = ctx
        while (current is ContextWrapper) {
            if (current is Activity) {
                return current
            }
            current = current.baseContext
        }
        return null
    }

    suspend fun signInWithGoogle(launcherContext: Context? = null): Result<FirebaseUser> {
        val resolvedActivity = (launcherContext as? Activity)
            ?: (launcherContext?.let { findActivity(it) })
            ?: (context as? Activity)
            ?: findActivity(context)

        val targetContext = resolvedActivity ?: launcherContext ?: context

        return try {
            val webClientId = try {
                val resId = targetContext.resources.getIdentifier("default_web_client_id", "string", targetContext.packageName)
                if (resId != 0) targetContext.getString(resId) else "613712404065-2q4tn20buj6lf6rhn4idtnpdeie9d44g.apps.googleusercontent.com"
            } catch (_: Exception) {
                "613712404065-2q4tn20buj6lf6rhn4idtnpdeie9d44g.apps.googleusercontent.com"
            }
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val cm = if (resolvedActivity != null) CredentialManager.create(resolvedActivity) else credentialManager
            val result = cm.getCredential(
                context = resolvedActivity ?: targetContext,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val activeAuth = auth ?: run {
                    if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                        com.google.firebase.FirebaseApp.initializeApp(context)
                    }
                    FirebaseAuth.getInstance()
                }
                val authResult = activeAuth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(IllegalStateException("User is null after Google sign-in"))
                }
            } else {
                Result.failure(IllegalStateException("Unrecognized credential type received"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("GoogleAuthManager", "Sign-in was cancelled by user")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuthManager", "CredentialManager exception", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleAuthManager", "Unexpected sign-in failure", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val currentAuth = auth
        if (currentAuth == null) {
            trySend(null)
            awaitClose { }
        } else {
            val listener = FirebaseAuth.AuthStateListener { fAuth ->
                trySend(fAuth.currentUser)
            }
            currentAuth.addAuthStateListener(listener)
            awaitClose { currentAuth.removeAuthStateListener(listener) }
        }
    }
}
