package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.entity.BrandEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ProductEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    products: List<ProductEntity>,
    brands: List<BrandEntity>,
    categories: List<CategoryEntity>,
    currency: String,
    onSaveProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onDuplicateProduct: (ProductEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    val filtered = remember(products, searchQuery, selectedStatusFilter) {
        products.filter { p ->
            val matchQuery = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.brand.contains(searchQuery, ignoreCase = true) ||
                    p.sku.contains(searchQuery, ignoreCase = true)
            val matchStatus = selectedStatusFilter == "All" || p.status.equals(selectedStatusFilter, ignoreCase = true)
            matchQuery && matchStatus
        }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = { Text("Products Catalog (${products.size})", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = { isCreatingNew = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("admin_add_product_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Shoe", fontWeight = FontWeight.Bold)
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
            // Search & Status filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search shoes or SKU...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Active", "Draft", "Out of Stock").forEach { st ->
                    FilterChip(
                        selected = selectedStatusFilter == st,
                        onClick = { selectedStatusFilter = st },
                        label = { Text(st, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = CharcoalDark,
                            containerColor = CardBackground,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Products List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(filtered, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_product_item_${product.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = product.brand.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GoldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (product.status) {
                                                "Active" -> Color(0xFF133224)
                                                "Draft" -> Color(0xFF282B38)
                                                else -> Color(0xFF3B1519)
                                            }
                                        ) {
                                            Text(
                                                text = product.status,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (product.status) {
                                                        "Active" -> AccentGreen
                                                        "Draft" -> TextSecondary
                                                        else -> AccentRed
                                                    }
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )

                                    Text(
                                        text = "SKU: ${product.sku} · Category: ${product.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "$currency${String.format("%.0f", product.salePrice ?: product.price)}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "Stock: ${product.stockQuantity} pairs",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (product.stockQuantity <= 3) AccentRed else AccentGreen
                                            )
                                        )
                                    }
                                }

                                // Quick Actions: Edit, Duplicate, Delete
                                Row {
                                    IconButton(
                                        onClick = { onDuplicateProduct(product) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { editingProduct = product },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            onDeleteProduct(product)
                                            Toast.makeText(context, "Deleted ${product.name}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Product Add/Edit Dialog
    if (isCreatingNew || editingProduct != null) {
        val initial = editingProduct ?: ProductEntity(
            name = "",
            brand = brands.firstOrNull()?.name ?: "Adelix Signature",
            category = categories.firstOrNull()?.name ?: "Luxury Sneakers",
            description = "Handcrafted with premium materials and precision stitching.",
            price = 220.0,
            salePrice = null,
            discountPercent = 0,
            sizes = "40,41,42,43,44,45",
            colors = "Onyx Black,Pure White,Cognac Brown",
            stockQuantity = 15,
            sku = "AS-${System.currentTimeMillis() % 10000}",
            isNewArrival = true,
            isFeatured = true,
            isBestSeller = false,
            isSale = false,
            isCustomizable = true,
            customizationTimeDays = "3–5 Working Days",
            status = "Active",
            imageUrl = "ic_adelix_logo"
        )

        ProductFormDialog(
            initial = initial,
            isNew = isCreatingNew,
            brands = brands,
            categories = categories,
            onDismiss = {
                isCreatingNew = false
                editingProduct = null
            },
            onSave = { saved ->
                onSaveProduct(saved)
                isCreatingNew = false
                editingProduct = null
                Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormDialog(
    initial: ProductEntity,
    isNew: Boolean,
    brands: List<BrandEntity>,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var brand by remember { mutableStateOf(initial.brand) }
    var category by remember { mutableStateOf(initial.category) }
    var description by remember { mutableStateOf(initial.description) }
    var priceStr by remember { mutableStateOf(initial.price.toString()) }
    var salePriceStr by remember { mutableStateOf(initial.salePrice?.toString() ?: "") }
    var discountPercentStr by remember { mutableStateOf(initial.discountPercent.toString()) }
    var sizes by remember { mutableStateOf(initial.sizes) }
    var colors by remember { mutableStateOf(initial.colors) }
    var stockStr by remember { mutableStateOf(initial.stockQuantity.toString()) }
    var sku by remember { mutableStateOf(initial.sku) }

    var isNewArrival by remember { mutableStateOf(initial.isNewArrival) }
    var isFeatured by remember { mutableStateOf(initial.isFeatured) }
    var isBestSeller by remember { mutableStateOf(initial.isBestSeller) }
    var isSale by remember { mutableStateOf(initial.isSale) }
    var isCustomizable by remember { mutableStateOf(initial.isCustomizable) }
    var customizationDays by remember { mutableStateOf(initial.customizationTimeDays) }
    var status by remember { mutableStateOf(initial.status) }
    var imageUrl by remember { mutableStateOf(initial.imageUrl) }
    var specifications by remember { mutableStateOf(initial.specifications) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(if (isNew) "Add New Shoe Model" else "Edit Shoe Model", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shoe Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand *") },
                        modifier = Modifier.weight(1f).testTag("product_brand_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Regular Price ($) *") },
                        modifier = Modifier.weight(1f).testTag("product_price_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = salePriceStr,
                        onValueChange = { salePriceStr = it },
                        label = { Text("Sale Price ($)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("Stock Quantity *") },
                        modifier = Modifier.weight(1f).testTag("product_stock_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sizes,
                    onValueChange = { sizes = it },
                    label = { Text("Sizes (comma-separated)") },
                    placeholder = { Text("40,41,42,43,44,45") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = colors,
                    onValueChange = { colors = it },
                    label = { Text("Colors (comma-separated)") },
                    placeholder = { Text("Onyx Black,Pure White,Brown") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = specifications,
                    onValueChange = { specifications = it },
                    label = { Text("Specifications (pipe separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Flags
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("New Arrival", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(16.dp))
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("Featured", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isBestSeller, onCheckedChange = { isBestSeller = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("Best Seller", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(16.dp))
                    Checkbox(checked = isSale, onCheckedChange = { isSale = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("On Sale", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCustomizable, onCheckedChange = { isCustomizable = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("Customizable Footwear", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                }

                if (isCustomizable) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customizationDays,
                        onValueChange = { customizationDays = it },
                        label = { Text("Customization Lead Time") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val salePrice = salePriceStr.toDoubleOrNull()
                    val stock = stockStr.toIntOrNull() ?: 0
                    val discount = discountPercentStr.toIntOrNull() ?: 0

                    if (name.isNotBlank() && brand.isNotBlank()) {
                        onSave(
                            initial.copy(
                                name = name.trim(),
                                brand = brand.trim(),
                                category = category.trim(),
                                description = description.trim(),
                                price = price,
                                salePrice = if (salePrice != null && salePrice > 0) salePrice else null,
                                discountPercent = discount,
                                sizes = sizes.trim(),
                                colors = colors.trim(),
                                stockQuantity = stock,
                                sku = sku.trim(),
                                isNewArrival = isNewArrival,
                                isFeatured = isFeatured,
                                isBestSeller = isBestSeller,
                                isSale = isSale,
                                isCustomizable = isCustomizable,
                                customizationTimeDays = customizationDays.trim(),
                                status = if (stock <= 0) "Out of Stock" else status,
                                imageUrl = if (imageUrl.isNotBlank()) imageUrl else "ic_adelix_logo",
                                specifications = specifications.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                modifier = Modifier.testTag("admin_save_product_btn")
            ) {
                Text("Save Shoe", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
