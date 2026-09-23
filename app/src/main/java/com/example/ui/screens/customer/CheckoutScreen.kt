package com.example.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.data.model.CartItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    subtotal: Double,
    deliveryCharges: Double,
    total: Double,
    settings: StoreSettingsEntity,
    onPlaceOrder: (
        name: String,
        phone: String,
        whatsapp: String,
        province: String,
        city: String,
        area: String,
        address: String,
        orderNotes: String,
        customizationNotes: String,
        onSuccess: (OrderEntity) -> Unit
    ) -> Unit,
    onOrderPlaced: (OrderEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerWhatsapp by remember { mutableStateOf("") }
    var sameAsPhone by remember { mutableStateOf(true) }

    var province by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var orderNotes by remember { mutableStateOf("") }
    var customizationNotes by remember { mutableStateOf("") }

    var isAgreed by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val hasCustomizableItems = remember(cartItems) {
        cartItems.any { it.product.isCustomizable || it.customizationText.isNotBlank() }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = { Text("Cash on Delivery Checkout", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalDark)
            )
        },
        bottomBar = {
            Surface(
                color = CardBackground,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Pay on Delivery (COD):",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            "${settings.currency}${String.format("%.2f", total)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (customerName.isBlank()) {
                                Toast.makeText(context, "Please enter your Full Name", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (customerPhone.isBlank() || customerPhone.length < 7) {
                                Toast.makeText(context, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (deliveryAddress.isBlank()) {
                                Toast.makeText(context, "Please enter your Complete Delivery Address", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isAgreed) {
                                Toast.makeText(context, "Please check the confirmation agreement", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            val finalWhatsapp = if (sameAsPhone) customerPhone else customerWhatsapp.ifBlank { customerPhone }

                            onPlaceOrder(
                                customerName.trim(),
                                customerPhone.trim(),
                                finalWhatsapp.trim(),
                                province.trim().ifBlank { "N/A" },
                                city.trim().ifBlank { "N/A" },
                                area.trim().ifBlank { "N/A" },
                                deliveryAddress.trim(),
                                orderNotes.trim(),
                                customizationNotes.trim()
                            ) { createdOrder ->
                                isSubmitting = false
                                onOrderPlaced(createdOrder)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_confirm_place_order"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSubmitting && cartItems.isNotEmpty()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CharcoalDark)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm & Place Order (COD)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // COD Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132B1F)),
                border = BorderStroke(1.dp, Color(0xFF225E3F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cash on Delivery (COD) Only",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "You only pay when the rider hands over your Adelix Sole luxury package.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contact Information Section
            Text(
                text = "1. RECIPIENT & CONTACT",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Full Name *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = {
                    customerPhone = it
                    if (sameAsPhone) customerWhatsapp = it
                },
                label = { Text("Mobile Phone Number *") },
                placeholder = { Text("e.g. +1 555 123 4567") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_phone"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = sameAsPhone,
                    onCheckedChange = {
                        sameAsPhone = it
                        if (it) customerWhatsapp = customerPhone
                    },
                    colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = CharcoalDark)
                )
                Text(
                    text = "WhatsApp number is same as mobile number",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }

            if (!sameAsPhone) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customerWhatsapp,
                    onValueChange = { customerWhatsapp = it },
                    label = { Text("WhatsApp Number") },
                    placeholder = { Text("For live dispatch updates") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Delivery Address Section
            Text(
                text = "2. DELIVERY DESTINATION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("Province / State") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Area / District / Neighborhood") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = { deliveryAddress = it },
                label = { Text("Complete Street & House / Apartment Address *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_address"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = orderNotes,
                onValueChange = { orderNotes = it },
                label = { Text("Delivery Instructions / Special Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 2
            )

            if (hasCustomizableItems) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customizationNotes,
                    onValueChange = { customizationNotes = it },
                    label = { Text("Bespoke Customization Notes") },
                    placeholder = { Text("Specify initials, custom color details...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Method Section
            Text(
                text = "3. PAYMENT METHOD",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_payment_method_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
                border = BorderStroke(1.5.dp, AccentGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = true,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cash on Delivery (COD)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentGreen
                            ) {
                                Text(
                                    text = "ONLY METHOD",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = CharcoalDark
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Zero advance or card payment required. Inspect your handcrafted footwear upon arrival and pay exact cash to the courier.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Order Summary Card
            Text(
                text = "4. ORDER SUMMARY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.product.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Size: ${item.selectedSize} · ${item.selectedColor}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "${settings.currency}${String.format("%.2f", item.totalPrice)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = CardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = TextSecondary)
                        Text("${settings.currency}${String.format("%.2f", subtotal)}", color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Charges", color = TextSecondary)
                        Text(if (deliveryCharges == 0.0) "FREE" else "${settings.currency}${String.format("%.2f", deliveryCharges)}", color = if (deliveryCharges == 0.0) AccentGreen else TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount (COD)", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${settings.currency}${String.format("%.2f", total)}", fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirmation Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAgreed,
                    onCheckedChange = { isAgreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = GoldPrimary, checkmarkColor = CharcoalDark),
                    modifier = Modifier.testTag("checkout_confirm_checkbox")
                )
                Text(
                    text = "I confirm that my delivery address and order details are accurate. I will pay the total amount upon delivery.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
