package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.ProductEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    products: List<ProductEntity>,
    onUpdateStock: (Long, Int) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var stockDialogProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var newStockInput by remember { mutableStateOf("") }

    val filtered = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.sku.contains(searchQuery, ignoreCase = true) ||
            it.brand.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = { Text("Inventory & Stock Control", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter inventory by shoe name or SKU...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(filtered, key = { it.id }) { product ->
                    val isLowStock = product.stockQuantity in 1..3
                    val isOutOfStock = product.stockQuantity <= 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("inventory_item_${product.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, if (isOutOfStock) AccentRed else if (isLowStock) AccentAmber else CardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.brand.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary
                                )
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "SKU: ${product.sku}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when {
                                        isOutOfStock -> Color(0xFF3B1519)
                                        isLowStock -> Color(0xFF3D2B11)
                                        else -> Color(0xFF133224)
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            isOutOfStock -> "OUT OF STOCK"
                                            isLowStock -> "LOW STOCK WARNING (${product.stockQuantity} pairs left)"
                                            else -> "IN STOCK (${product.stockQuantity} pairs available)"
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isOutOfStock -> AccentRed
                                                isLowStock -> AccentAmber
                                                else -> AccentGreen
                                            }
                                        )
                                    )
                                }
                            }

                            // Quick adjust buttons (+ / - / direct input)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val newStock = (product.stockQuantity - 1).coerceAtLeast(0)
                                        onUpdateStock(product.id, newStock)
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                }

                                Text(
                                    text = "${product.stockQuantity}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .clickable {
                                            stockDialogProduct = product
                                            newStockInput = product.stockQuantity.toString()
                                        }
                                )

                                IconButton(
                                    onClick = {
                                        val newStock = product.stockQuantity + 1
                                        onUpdateStock(product.id, newStock)
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Stock Quantity Dialog
    stockDialogProduct?.let { prod ->
        AlertDialog(
            onDismissRequest = { stockDialogProduct = null },
            containerColor = CardBackground,
            title = { Text("Update Stock for ${prod.name}", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    Text("Enter exact number of pairs available in warehouse:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newStockInput,
                        onValueChange = { newStockInput = it },
                        label = { Text("Units") },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = newStockInput.toIntOrNull() ?: 0
                        onUpdateStock(prod.id, num.coerceAtLeast(0))
                        stockDialogProduct = null
                        Toast.makeText(context, "Stock updated to $num", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { stockDialogProduct = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
