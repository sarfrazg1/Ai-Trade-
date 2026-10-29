package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardBorder
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutScreen(
    developerName: String = "Rao Muhammad Sarfraz",
    developerWhatsapp: String = "+923360074976",
    isRomanUrdu: Boolean = false,
    isAdmin: Boolean = false,
    onClose: () -> Unit,
    onOpenSubscription: () -> Unit = {},
    onOpenLegal: (String) -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    onUpdateDeveloperInfo: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CryptoBackground)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRomanUrdu) "Smart Trade AI k Bare Me" else "About Smart Trade AI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Info Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BullGreen.copy(alpha = 0.15f))
                        .border(1.dp, BullGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Smart Trade AI",
                        tint = BullGreen,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Smart Trade AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Version 1.0.0 (Release)",
                    fontSize = 11.sp,
                    color = AccentCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isRomanUrdu) {
                        "Smart Trade AI crypto futures aur spot markets k liye Smart Money Concepts (SMC), confluence rules aur automated technical analysis faraham karta hai."
                    } else {
                        "Smart Trade AI is a high-precision cryptocurrency market analysis engine designed to evaluate Smart Money Concepts (SMC), multi-timeframe confluence, and risk-managed trading setups."
                    },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Navigation Links
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                AboutMenuItem(
                    icon = Icons.Default.Info,
                    label = if (isRomanUrdu) "★ Subscription Plans" else "★ Subscription Plans & Pricing",
                    onClick = onOpenSubscription
                )
                AboutMenuItem(
                    icon = Icons.Default.Shield,
                    label = if (isRomanUrdu) "📜 Risk Disclaimer" else "📜 Legal & Risk Disclaimer",
                    onClick = { onOpenLegal("DISCLAIMER") }
                )
                AboutMenuItem(
                    icon = Icons.Default.Shield,
                    label = if (isRomanUrdu) "📋 Terms of Service" else "📋 Terms of Service",
                    onClick = { onOpenLegal("TERMS") }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Subtle Developer Information near the bottom
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CryptoSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CryptoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Developer: $developerName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "WhatsApp: $developerWhatsapp",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    if (isAdmin) {
                        IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Developer Info",
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val clean = developerWhatsapp.replace(Regex("[^0-9]"), "")
                        val targetNumber = if (clean.isNotEmpty()) clean else "923360074976"
                        val greeting = if (isRomanUrdu) {
                            "Assalam-o-Alaikum%20Rao%20Sarfraz%2C%20Smart%20Trade%20AI%20app%20support%20k%20bare%20me%20rabta%20kiya%20hai"
                        } else {
                            "Hello%20Rao%20Muhammad%20Sarfraz%2C%20I%20am%20using%20Smart%20Trade%20AI%20app"
                        }
                        val url = "https://wa.me/$targetNumber?text=$greeting"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("about_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BullGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chat on WhatsApp ($developerWhatsapp)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Discreet Admin Access Link at the very bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚙ System Access (Admin Portal)",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier
                    .clickable { onOpenAdmin() }
                    .padding(8.dp)
                    .testTag("about_admin_link")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
    }

    // Developer Edit Dialog (Admin Only)
    if (showEditDialog && isAdmin) {
        var editName by remember { mutableStateOf(developerName) }
        var editWhatsapp by remember { mutableStateOf(developerWhatsapp) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (isRomanUrdu) "Developer Maloomat Tabdeel Karein" else "Edit Developer Details",
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Developer Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editWhatsapp,
                        onValueChange = { editWhatsapp = it },
                        label = { Text("WhatsApp Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDeveloperInfo(editName, editWhatsapp)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BullGreen)
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CryptoSurface
        )
    }
}

@Composable
private fun AboutMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
