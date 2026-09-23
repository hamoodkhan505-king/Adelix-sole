package com.example.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.entity.CustomerEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCustomersScreen(
    customers: List<CustomerEntity>,
    settings: StoreSettingsEntity,
    onAddCustomer: (CustomerEntity) -> Unit,
    onUpdateCustomer: (CustomerEntity) -> Unit,
    onDeleteCustomer: (CustomerEntity) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<CustomerEntity?>(null) }
    var customerToDelete by remember { mutableStateOf<CustomerEntity?>(null) }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else {
            val q = searchQuery.trim().lowercase()
            customers.filter {
                it.fullName.lowercase().contains(q) ||
                it.phoneNumber.contains(q) ||
                it.city.lowercase().contains(q) ||
                it.email.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Customer Directory", fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text("${filteredCustomers.size} customers recorded", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_customers_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            customerToEdit = null
                            showDialog = true
                        },
                        modifier = Modifier.testTag("admin_add_customer_btn")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer", tint = GoldPrimary)
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
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_customer_search_input"),
                placeholder = { Text("Search by name, phone, email, or city...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No customers found", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Customers will appear automatically when orders are placed, or you can add one manually.", color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_card_${customer.id}"),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(GoldDark.copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = customer.fullName.take(2).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = GoldPrimary,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = customer.fullName,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = customer.phoneNumber,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Row {
                                        if (customer.whatsappNumber.isNotBlank()) {
                                            IconButton(
                                                onClick = {
                                                    launchWhatsApp(
                                                        context = context,
                                                        phoneNumber = customer.whatsappNumber,
                                                        message = "Hello ${customer.fullName}, greeting from Adelix Sole luxury footwear support!"
                                                    )
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                customerToEdit = customer
                                                showDialog = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary)
                                        }

                                        IconButton(
                                            onClick = { customerToDelete = customer },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed)
                                        }
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Orders Placed", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text("${customer.totalOrdersCount} orders", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Spend", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text("${settings.currency}${String.format("%.2f", customer.totalSpent)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Location", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        Text(
                                            text = if (customer.city.isNotBlank()) "${customer.city}, ${customer.province}" else "Online",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                if (customer.defaultAddress.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Address: ${customer.defaultAddress}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Customer Add / Edit Dialog
    if (showDialog) {
        var name by remember { mutableStateOf(customerToEdit?.fullName ?: "") }
        var phone by remember { mutableStateOf(customerToEdit?.phoneNumber ?: "") }
        var whatsapp by remember { mutableStateOf(customerToEdit?.whatsappNumber ?: "") }
        var email by remember { mutableStateOf(customerToEdit?.email ?: "") }
        var province by remember { mutableStateOf(customerToEdit?.province ?: "") }
        var city by remember { mutableStateOf(customerToEdit?.city ?: "") }
        var area by remember { mutableStateOf(customerToEdit?.area ?: "") }
        var address by remember { mutableStateOf(customerToEdit?.defaultAddress ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (customerToEdit == null) "Add Customer" else "Edit Customer",
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("cust_input_name")
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.fillMaxWidth().testTag("cust_input_phone")
                    )
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = province,
                            onValueChange = { province = it },
                            label = { Text("Province") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Default Delivery Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            if (customerToEdit == null) {
                                onAddCustomer(
                                    CustomerEntity(
                                        fullName = name.trim(),
                                        phoneNumber = phone.trim(),
                                        whatsappNumber = if (whatsapp.isBlank()) phone.trim() else whatsapp.trim(),
                                        email = email.trim(),
                                        province = province.trim(),
                                        city = city.trim(),
                                        area = area.trim(),
                                        defaultAddress = address.trim(),
                                        createdAtTimestamp = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                onUpdateCustomer(
                                    customerToEdit!!.copy(
                                        fullName = name.trim(),
                                        phoneNumber = phone.trim(),
                                        whatsappNumber = if (whatsapp.isBlank()) phone.trim() else whatsapp.trim(),
                                        email = email.trim(),
                                        province = province.trim(),
                                        city = city.trim(),
                                        area = area.trim(),
                                        defaultAddress = address.trim()
                                    )
                                )
                            }
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                    modifier = Modifier.testTag("cust_dialog_save_btn")
                ) {
                    Text("Save Customer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(14.dp)
        )
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer Record?", fontWeight = FontWeight.Bold, color = AccentRed) },
            text = { Text("Are you sure you want to remove '${cust.fullName}' from your customer list?", color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomer(cust)
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardBackground
        )
    }
}
