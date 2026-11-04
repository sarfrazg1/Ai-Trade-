package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ActivationHelper
import com.example.data.repository.SubscriptionManager
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

data class AdminCodeItem(
    val key: String,
    val code: String,
    val plan: String,
    val status: String,
    val isUsed: Boolean = false,
    val usedByDevice: String = ""
)

@Composable
fun AdminScreen(
    subscriptionManager: SubscriptionManager,
    isUserAdmin: Boolean = false,
    adminEmail: String? = null,
    isRomanUrdu: Boolean = false,
    onSignInAdmin: () -> Unit = {},
    onClose: () -> Unit,
    onPublishCodeCloud: (code: String, targetUserRef: String, plan: String, validityDays: Int, paymentRef: String) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    val correctPassword = "Sarfrazg1."

    // 1. Password Verification Screen (No Google Sign-In)
    if (!isAuthenticated) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CryptoBackground),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BullGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Admin Lock",
                            tint = BullGreen,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isRomanUrdu) "ایڈمن پورٹل" else "Admin Portal",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isRomanUrdu) "فائر بیس ریئل ٹائم ڈیٹا بیس کوڈ مینیجر" else "Realtime Database Activation Code Console",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            if (passwordError != null) passwordError = null
                        },
                        label = { Text(if (isRomanUrdu) "ایڈمن پاس ورڈ" else "Admin Password", fontSize = 12.sp) },
                        placeholder = { Text(if (isRomanUrdu) "پاس ورڈ درج کریں" else "Enter password", fontSize = 12.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_password_input")
                    )

                    passwordError?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = BearRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isRomanUrdu) "واپس جائیں" else "Back to App", color = TextSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (passwordInput == correctPassword) {
                                    isAuthenticated = true
                                    passwordError = null
                                } else {
                                    passwordError = if (isRomanUrdu) "غلط پاس ورڈ! براہ کرم درست پاس ورڈ درج کریں۔" else "Wrong Password"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sign_in_admin_button")
                        ) {
                            Text(if (isRomanUrdu) "لاگ ان کریں" else "Sign In Admin", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Secondary Dialog flow if user triggered dialog specifically
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                containerColor = CryptoSurface,
                title = { Text("Sign In Admin", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter administrator password to continue:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = {
                                passwordInput = it
                                if (passwordError != null) passwordError = null
                            },
                            label = { Text("Password", fontSize = 12.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BullGreen,
                                unfocusedBorderColor = CryptoCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        passwordError?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = it, fontSize = 11.sp, color = BearRed, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (passwordInput == correctPassword) {
                                isAuthenticated = true
                                passwordError = null
                                showPasswordDialog = false
                            } else {
                                passwordError = "Wrong Password"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                    ) {
                        Text("Login", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
        return
    }

    // 2. Authenticated Admin Console (Realtime Database Connected)
    var codesList by remember { mutableStateOf<List<AdminCodeItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var rtdbError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Dialog state controllers
    var showAddCodeDialog by remember { mutableStateOf(false) }
    var showDeleteCodeDialog by remember { mutableStateOf(false) }
    var codeToDelete by remember { mutableStateOf<AdminCodeItem?>(null) }
    var showStatusChangeDialog by remember { mutableStateOf(false) }
    var codeToResetStatus by remember { mutableStateOf<AdminCodeItem?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Realtime Database connection to: activation_codes -> activationCodes
    DisposableEffect(Unit) {
        val database = ActivationHelper.getDatabase(context)
        if (database == null) {
            isLoading = false
            rtdbError = "Could not initialize Firebase Realtime Database"
            return@DisposableEffect onDispose {}
        }

        val rootRef = database.getReference("activation_codes")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading = false
                rtdbError = null
                val items = mutableListOf<AdminCodeItem>()

                val targetList = if (snapshot.hasChild("activationCodes")) {
                    snapshot.child("activationCodes")
                } else {
                    snapshot
                }

                for (child in targetList.children) {
                    val code = child.child("code").getValue(String::class.java)?.trim() ?: ""
                    val plan = child.child("plan").getValue(String::class.java)?.trim() ?: "GOLDEN"
                    val status = child.child("status").getValue(String::class.java)?.trim() ?: "unused"
                    val isUsed = child.child("isUsed").getValue(Boolean::class.java) ?: status.equals("used", ignoreCase = true)
                    val device = child.child("usedByDevice").getValue(String::class.java)?.trim() ?: ""

                    if (code.isNotEmpty() || child.childrenCount > 0) {
                        items.add(
                            AdminCodeItem(
                                key = child.key ?: "",
                                code = code,
                                plan = plan,
                                status = status,
                                isUsed = isUsed,
                                usedByDevice = device
                            )
                        )
                    }
                }
                codesList = items
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                rtdbError = "Firebase Error: ${error.message}"
            }
        }

        rootRef.addValueEventListener(listener)
        onDispose {
            rootRef.removeEventListener(listener)
        }
    }

    // Filter codes based on search query
    val filteredCodes = remember(codesList, searchQuery) {
        if (searchQuery.isBlank()) {
            codesList
        } else {
            val q = searchQuery.trim()
            codesList.filter {
                it.code.contains(q, ignoreCase = true) ||
                it.plan.contains(q, ignoreCase = true) ||
                it.status.contains(q, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CryptoSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Admin Console",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "activation_codes -> activationCodes",
                        fontSize = 10.sp,
                        color = AccentCyan
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val database = ActivationHelper.getDatabase(context)
                        database?.getReference("activation_codes")?.get()
                        feedbackMessage = "Refreshed from Firebase"
                    }
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentCyan)
                }
            }
        }

        // Stats summary bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CryptoSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalCount = codesList.size
            val usedCount = codesList.count { it.status.equals("used", ignoreCase = true) || it.isUsed }
            val unusedCount = totalCount - usedCount

            Text(
                text = "Total Codes: $totalCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Unused: $unusedCount", fontSize = 11.sp, color = BullGreen, fontWeight = FontWeight.Bold)
                Text(text = "Used: $usedCount", fontSize = 11.sp, color = BearRed, fontWeight = FontWeight.Bold)
            }
        }

        // 4 Action Buttons Bar: Add New Code, Delete Code, Search Code, Change Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Admin Actions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Button 1: Add New Code
                    Button(
                        onClick = { showAddCodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Add Code", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    // Button 2: Delete Code
                    Button(
                        onClick = { showDeleteCodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BearRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Delete", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Button 3: Search Code
                    Button(
                        onClick = { isSearchVisible = !isSearchVisible },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSearchVisible) AccentCyan else CryptoSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchVisible) Color.Black else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "Search",
                            fontSize = 10.sp,
                            color = if (isSearchVisible) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Button 4: Change Status from used to unused
                    Button(
                        onClick = { showStatusChangeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Autorenew, contentDescription = "Reset", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Reset", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Optional Search Bar
        if (isSearchVisible) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search code, plan or status...", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Feedback banner
        feedbackMessage?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = BullGreen.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = msg, fontSize = 12.sp, color = BullGreen, fontWeight = FontWeight.Medium)
                    IconButton(onClick = { feedbackMessage = null }, modifier = Modifier.size(20.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = BullGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Error Banner
        rtdbError?.let { err ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = BearRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = err,
                    fontSize = 12.sp,
                    color = BearRed,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Activation Codes List
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BullGreen, modifier = Modifier.size(36.dp))
                }
            } else if (filteredCodes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No activation codes match '$searchQuery'" else "No activation codes found in Realtime Database.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showAddCodeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                        ) {
                            Text("Add First Code", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCodes, key = { it.key + it.code }) { item ->
                        val isUsed = item.status.equals("used", ignoreCase = true) || item.isUsed

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isUsed) CryptoCardBorder else BullGreen.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Code Text & Copy
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.code.ifBlank { "[Empty Code Key ${item.key}]" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (isUsed) TextSecondary else TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Activation Code", item.code)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Code copied: ${item.code}", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy code",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Status Badge (unused / used)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isUsed) BearRed.copy(alpha = 0.2f) else BullGreen.copy(alpha = 0.2f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isUsed) "USED" else "UNUSED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUsed) BearRed else BullGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Plan label
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Plan: ", fontSize = 11.sp, color = TextMuted)
                                        Text(
                                            text = item.plan.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.plan.uppercase()) {
                                                "GOLDEN", "GOLD" -> WarningAmber
                                                "SILVER" -> AccentCyan
                                                else -> BullGreen
                                            }
                                        )
                                        Text(text = " (Key: ${item.key})", fontSize = 10.sp, color = TextMuted)
                                    }

                                    // Action buttons for this specific code
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (isUsed) {
                                            TextButton(
                                                onClick = {
                                                    codeToResetStatus = item
                                                    showStatusChangeDialog = true
                                                },
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Make Unused", fontSize = 10.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                codeToDelete = item
                                                showDeleteCodeDialog = true
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete code",
                                                tint = BearRed.copy(alpha = 0.8f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                if (isUsed && item.usedByDevice.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Device: ${item.usedByDevice}",
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOG 1: Add New Code
    // -------------------------------------------------------------
    if (showAddCodeDialog) {
        var newCodeInput by remember {
            val randomPart = (1..3).map {
                (1..4).map { "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".random() }.joinToString("")
            }.joinToString("-")
            mutableStateOf("STAI-$randomPart")
        }
        var newPlanInput by remember { mutableStateOf("GOLDEN") }
        var newStatusInput by remember { mutableStateOf("unused") }
        var isSaving by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showAddCodeDialog = false },
            containerColor = CryptoSurface,
            title = { Text("Add New Activation Code", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Enter code or keep auto-generated:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCodeInput,
                        onValueChange = { newCodeInput = it.uppercase().trim() },
                        label = { Text("Code", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BullGreen,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Plan:", fontSize = 11.sp, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("GOLDEN", "SILVER", "BRONZE").forEach { plan ->
                            val selected = newPlanInput == plan
                            Button(
                                onClick = { newPlanInput = plan },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) BullGreen else CryptoSurfaceVariant
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                Text(
                                    text = plan,
                                    fontSize = 10.sp,
                                    color = if (selected) Color.Black else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Initial Status:", fontSize = 11.sp, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("unused", "used").forEach { st ->
                            val selected = newStatusInput == st
                            Button(
                                onClick = { newStatusInput = st },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) (if (st == "unused") BullGreen else BearRed) else CryptoSurfaceVariant
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                Text(
                                    text = st.uppercase(),
                                    fontSize = 10.sp,
                                    color = if (selected) Color.Black else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newCodeInput.trim()
                        if (clean.isBlank()) return@Button
                        isSaving = true

                        val database = ActivationHelper.getDatabase(context)
                        val ref = database?.getReference("activation_codes")?.child("activationCodes")

                        // Next index calculation
                        val nextIndex = (codesList.mapNotNull { it.key.toIntOrNull() }.maxOrNull() ?: -1) + 1
                        val newKey = nextIndex.toString()

                        val data = mapOf<String, Any>(
                            "code" to clean,
                            "plan" to newPlanInput.uppercase(),
                            "status" to newStatusInput.lowercase(),
                            "isUsed" to (newStatusInput.lowercase() == "used")
                        )

                        ref?.child(newKey)?.setValue(data)?.addOnSuccessListener {
                            isSaving = false
                            showAddCodeDialog = false
                            feedbackMessage = "Code $clean added to Realtime Database!"
                        }?.addOnFailureListener {
                            isSaving = false
                            feedbackMessage = "Failed to add code: ${it.localizedMessage}"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Add to Firebase", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCodeDialog = false }, enabled = !isSaving) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG 2: Delete Code
    // -------------------------------------------------------------
    if (showDeleteCodeDialog) {
        var inputCodeToDelete by remember { mutableStateOf(codeToDelete?.code ?: "") }
        var isDeleting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteCodeDialog = false },
            containerColor = CryptoSurface,
            title = { Text("Delete Activation Code", color = BearRed, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Select or enter the code you want to permanently delete from Firebase:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputCodeToDelete,
                        onValueChange = { inputCodeToDelete = it.trim() },
                        label = { Text("Code to Delete", fontSize = 11.sp) },
                        placeholder = { Text("e.g. STAI-X436-7S7Q-LY4A", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BearRed,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = inputCodeToDelete.trim()
                        if (target.isBlank()) return@Button

                        val matched = codesList.firstOrNull { it.code.equals(target, ignoreCase = true) || it.key == target }
                        if (matched == null) {
                            feedbackMessage = "Code '$target' not found in database"
                            showDeleteCodeDialog = false
                            return@Button
                        }

                        isDeleting = true
                        val database = ActivationHelper.getDatabase(context)
                        val ref = database?.getReference("activation_codes")?.child("activationCodes")

                        ref?.child(matched.key)?.removeValue()?.addOnSuccessListener {
                            isDeleting = false
                            showDeleteCodeDialog = false
                            codeToDelete = null
                            feedbackMessage = "Code ${matched.code} deleted from Realtime Database"
                        }?.addOnFailureListener {
                            isDeleting = false
                            feedbackMessage = "Failed to delete: ${it.localizedMessage}"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BearRed),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCodeDialog = false }, enabled = !isDeleting) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG 3: Change Status from used to unused
    // -------------------------------------------------------------
    if (showStatusChangeDialog) {
        var inputCodeToReset by remember { mutableStateOf(codeToResetStatus?.code ?: "") }
        var isUpdating by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isUpdating) showStatusChangeDialog = false },
            containerColor = CryptoSurface,
            title = { Text("Reset Status to Unused", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text(
                        text = "This will update the code's status in Firebase Realtime Database from 'used' back to 'unused', enabling it to be redeemed again.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputCodeToReset,
                        onValueChange = { inputCodeToReset = it.trim() },
                        label = { Text("Code to Reset", fontSize = 11.sp) },
                        placeholder = { Text("e.g. STAI-X436-7S7Q-LY4A", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarningAmber,
                            unfocusedBorderColor = CryptoCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = inputCodeToReset.trim()
                        if (target.isBlank()) return@Button

                        val matched = codesList.firstOrNull { it.code.equals(target, ignoreCase = true) || it.key == target }
                        if (matched == null) {
                            feedbackMessage = "Code '$target' not found in database"
                            showStatusChangeDialog = false
                            return@Button
                        }

                        isUpdating = true
                        val database = ActivationHelper.getDatabase(context)
                        val ref = database?.getReference("activation_codes")?.child("activationCodes")

                        val updates = mapOf<String, Any>(
                            "status" to "unused",
                            "isUsed" to false,
                            "usedByDevice" to ""
                        )

                        ref?.child(matched.key)?.updateChildren(updates)?.addOnSuccessListener {
                            isUpdating = false
                            showStatusChangeDialog = false
                            codeToResetStatus = null
                            feedbackMessage = "Code ${matched.code} status changed from used to unused!"
                        }?.addOnFailureListener {
                            isUpdating = false
                            feedbackMessage = "Failed to update: ${it.localizedMessage}"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Reset to Unused", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusChangeDialog = false }, enabled = !isUpdating) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
