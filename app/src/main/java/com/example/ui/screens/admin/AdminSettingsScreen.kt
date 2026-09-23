package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    settings: StoreSettingsEntity,
    onSaveSettings: (StoreSettingsEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var storeName by remember { mutableStateOf(settings.storeName) }
    var whatsappNumber by remember { mutableStateOf(settings.whatsappNumber) }
    var phoneNumber by remember { mutableStateOf(settings.phoneNumber) }
    var email by remember { mutableStateOf(settings.email) }
    var address by remember { mutableStateOf(settings.address) }
    var deliveryChargesStr by remember { mutableStateOf(settings.deliveryCharges.toString()) }
    var freeThresholdStr by remember { mutableStateOf(settings.freeDeliveryThreshold.toString()) }
    var currency by remember { mutableStateOf(settings.currency) }
    var announcementText by remember { mutableStateOf(settings.announcementText) }
    var footerText by remember { mutableStateOf(settings.footerText) }
    var heroTitle by remember { mutableStateOf(settings.heroBannerTitle) }
    var heroSubtitle by remember { mutableStateOf(settings.heroBannerSubtitle) }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = { Text("Store & Website Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val delivery = deliveryChargesStr.toDoubleOrNull() ?: 15.0
                            val threshold = freeThresholdStr.toDoubleOrNull() ?: 150.0

                            onSaveSettings(
                                settings.copy(
                                    storeName = storeName.trim(),
                                    whatsappNumber = whatsappNumber.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    email = email.trim(),
                                    address = address.trim(),
                                    deliveryCharges = delivery,
                                    freeDeliveryThreshold = threshold,
                                    currency = currency.trim(),
                                    announcementText = announcementText.trim(),
                                    footerText = footerText.trim(),
                                    heroBannerTitle = heroTitle.trim(),
                                    heroBannerSubtitle = heroSubtitle.trim()
                                )
                            )
                            Toast.makeText(context, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("admin_save_settings_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "BRAND IDENTITY & CONTACT",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("Store Brand Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = whatsappNumber,
                onValueChange = { whatsappNumber = it },
                label = { Text("WhatsApp Concierge Number *") },
                placeholder = { Text("e.g. +18005557653") },
                modifier = Modifier.fillMaxWidth().testTag("settings_whatsapp_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Support Phone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Support Email") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Studio Flagship Address") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "COD & DELIVERY CHARGES",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = deliveryChargesStr,
                    onValueChange = { deliveryChargesStr = it },
                    label = { Text("Standard Delivery ($)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                OutlinedTextField(
                    value = freeThresholdStr,
                    onValueChange = { freeThresholdStr = it },
                    label = { Text("Free Delivery Threshold ($)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency") },
                    modifier = Modifier.width(80.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ANNOUNCEMENTS & HOMEPAGE TEXT",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = announcementText,
                onValueChange = { announcementText = it },
                label = { Text("Top Announcement Bar Text") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = heroTitle,
                onValueChange = { heroTitle = it },
                label = { Text("Hero Banner Headline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = heroSubtitle,
                onValueChange = { heroSubtitle = it },
                label = { Text("Hero Banner Subtitle") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = footerText,
                onValueChange = { footerText = it },
                label = { Text("Footer Tagline") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
