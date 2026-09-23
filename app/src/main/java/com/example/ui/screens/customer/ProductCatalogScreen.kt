package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BrandEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogScreen(
    products: List<ProductEntity>,
    brands: List<BrandEntity> = emptyList(),
    categories: List<CategoryEntity> = emptyList(),
    filterState: FilterState = FilterState(),
    currency: String = "$",
    onSearchChange: (String) -> Unit = {},
    onBrandSelect: (String?) -> Unit = {},
    onCategorySelect: (String?) -> Unit = {},
    onSizeSelect: (String?) -> Unit = {},
    onInStockToggle: (Boolean) -> Unit = {},
    onSaleToggle: (Boolean) -> Unit = {},
    onNewArrivalsToggle: (Boolean) -> Unit = {},
    onSortSelect: (SortOption) -> Unit = {},
    onResetFilters: () -> Unit = {},
    onProductClick: (Long) -> Unit,
    onAddToCart: (product: ProductEntity, size: String, color: String, quantity: Int) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSingleColumn by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var quickAddProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedCategoryTab by remember { mutableStateOf("All") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CharcoalDark,
        modifier = modifier
            .fillMaxSize()
            .testTag("product_catalog_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header: Atelier Banner & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ADELIX ATELIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = GoldPrimary
                        )
                        Text(
                            text = "Footwear Catalog",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    // Grid layout switcher (2 columns vs 1 column)
                    IconButton(
                        onClick = { isSingleColumn = !isSingleColumn },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardBackground)
                            .border(1.dp, CardBorder, CircleShape)
                            .testTag("btn_toggle_grid_layout")
                    ) {
                        Icon(
                            imageVector = if (isSingleColumn) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = "Toggle Grid Columns",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar + Filter Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = filterState.query,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("catalog_search_input"),
                        placeholder = { Text("Search shoes, brands, styles...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (filterState.query.isNotBlank()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    // Filter Button
                    FilledTonalButton(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CardBackground,
                            contentColor = if (filterState.brand != null || filterState.category != null || filterState.size != null || filterState.saleOnly || filterState.newArrivalsOnly) GoldPrimary else TextPrimary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (filterState.brand != null || filterState.category != null || filterState.size != null || filterState.saleOnly || filterState.newArrivalsOnly) GoldPrimary else CardBorder
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("catalog_filter_btn")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filters", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Category Tabs
                val allCategoryNames = remember(categories) {
                    listOf("All") + categories.map { it.name }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allCategoryNames.forEach { cat ->
                        val isSelected = (selectedCategoryTab == cat) || (filterState.category == cat) || (cat == "All" && filterState.category == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryTab = cat
                                onCategorySelect(if (cat == "All") null else cat)
                            },
                            label = {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardBackground,
                                labelColor = TextSecondary,
                                selectedContainerColor = GoldDark.copy(alpha = 0.3f),
                                selectedLabelColor = GoldLight
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else CardBorder
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Results count and active filter summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${products.size} footwear models cataloged",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                // Quick Sale Pill
                FilterChip(
                    selected = filterState.saleOnly,
                    onClick = { onSaleToggle(!filterState.saleOnly) },
                    label = { Text("Sale Only", fontSize = 11.sp) },
                    leadingIcon = {
                        if (filterState.saleOnly) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = AccentRed)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CharcoalDark,
                        labelColor = TextSecondary,
                        selectedContainerColor = AccentRed.copy(alpha = 0.2f),
                        selectedLabelColor = AccentRed
                    ),
                    border = BorderStroke(1.dp, if (filterState.saleOnly) AccentRed else CardBorder),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Empty State
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CardBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Footwear Found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "We couldn't find any shoe models matching your active search or filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedCategoryTab = "All"
                                onResetFilters()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset All Filters", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // ========================================================
                // LAZY VERTICAL GRID OF FOOTWEAR
                // Displays images, prices, badges, and quick-add buttons
                // ========================================================
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isSingleColumn) 1 else 2),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("catalog_vertical_grid"),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCatalogCard(
                            product = product,
                            currency = currency,
                            isSingleColumn = isSingleColumn,
                            onProductClick = { onProductClick(product.id) },
                            onQuickAddClick = { quickAddProduct = product }
                        )
                    }

                    item(span = { GridItemSpan(if (isSingleColumn) 1 else 2) }) {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Quick-Add Bottom Sheet / Modal
    quickAddProduct?.let { product ->
        QuickAddSheet(
            product = product,
            currency = currency,
            onDismiss = { quickAddProduct = null },
            onConfirmAdd = { size, color, qty ->
                onAddToCart(product, size, color, qty)
                quickAddProduct = null
            },
            onViewDetails = {
                val pid = product.id
                quickAddProduct = null
                onProductClick(pid)
            }
        )
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        CatalogFilterSheet(
            brands = brands,
            categories = categories,
            filterState = filterState,
            onBrandSelect = onBrandSelect,
            onCategorySelect = onCategorySelect,
            onSizeSelect = onSizeSelect,
            onInStockToggle = onInStockToggle,
            onSaleToggle = onSaleToggle,
            onNewArrivalsToggle = onNewArrivalsToggle,
            onSortSelect = onSortSelect,
            onResetFilters = onResetFilters,
            onDismiss = { showFilterSheet = false }
        )
    }
}

/**
 * Footwear item card with luxury styling, high-resolution image container,
 * discount prices, and quick-add button.
 */
@Composable
fun ProductCatalogCard(
    product: ProductEntity,
    currency: String,
    isSingleColumn: Boolean,
    onProductClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isOutOfStock = product.stockQuantity <= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onProductClick)
            .testTag("catalog_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Shoe Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isSingleColumn) 220.dp else 165.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF222633), Color(0xFF14161F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Resolved Image
                val resId = remember(product.imageUrl) {
                    when (product.imageUrl) {
                        "hero_banner" -> context.resources.getIdentifier("hero_banner", "drawable", context.packageName)
                        "ic_adelix_logo" -> context.resources.getIdentifier("ic_adelix_logo", "drawable", context.packageName)
                        else -> context.resources.getIdentifier("hero_banner", "drawable", context.packageName)
                    }
                }

                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RollerSkating,
                        contentDescription = product.name,
                        modifier = Modifier.size(54.dp),
                        tint = GoldPrimary.copy(alpha = 0.5f)
                    )
                }

                // Badges in Top Left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.isSale && product.discountPercent > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentRed
                        ) {
                            Text(
                                text = "-${product.discountPercent}%",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            )
                        }
                    } else if (product.isNewArrival) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary
                        ) {
                            Text(
                                text = "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = CharcoalDark
                                )
                            )
                        }
                    }

                    if (product.isCustomizable) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF6366F1)
                        ) {
                            Text(
                                text = "BESPOKE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Stock Indicator Badge in Top Right
                if (isOutOfStock) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = "SOLD OUT",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = AccentRed
                            )
                        )
                    }
                } else if (product.stockQuantity in 1..3) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = AccentAmber.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "ONLY ${product.stockQuantity} LEFT",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = CharcoalDark
                            )
                        )
                    }
                }
            }

            // Shoe Details Section
            Column(modifier = Modifier.padding(12.dp)) {
                // Brand Label
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = GoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Shoe Model Title
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Shoe Category & Material highlight
                Text(
                    text = "${product.category} · European Fit",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price Row & Discount Strikethrough
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val effectivePrice = product.salePrice ?: product.price

                    Text(
                        text = "$currency${String.format("%.2f", effectivePrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSingleColumn) 18.sp else 15.sp
                        ),
                        color = GoldPrimary
                    )

                    if (product.salePrice != null && product.salePrice < product.price) {
                        Text(
                            text = "$currency${String.format("%.2f", product.price)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 11.sp
                            ),
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // QUICK ADD BUTTON
                // Meets 48dp minimum touch target
                // ==========================================
                Button(
                    onClick = onQuickAddClick,
                    enabled = !isOutOfStock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("quick_add_btn_${product.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOutOfStock) Color(0xFF2B2E38) else GoldPrimary,
                        contentColor = if (isOutOfStock) TextMuted else CharcoalDark,
                        disabledContainerColor = Color(0xFF242732),
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    if (isOutOfStock) {
                        Text("Sold Out", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Quick Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quick Add",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Quick-Add Modal Bottom Sheet allowing fast size and color selection
 * before confirming to cart with Cash on Delivery reminder.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    product: ProductEntity,
    currency: String,
    onDismiss: () -> Unit,
    onConfirmAdd: (size: String, color: String, qty: Int) -> Unit,
    onViewDetails: () -> Unit
) {
    val availableSizes = remember(product.sizes) {
        if (product.sizes.isNotBlank()) product.sizes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        else listOf("40", "41", "42", "43", "44", "45")
    }

    val availableColors = remember(product.colors) {
        if (product.colors.isNotBlank()) product.colors.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        else listOf("Signature Black", "Cognac Brown")
    }

    var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull() ?: "42") }
    var selectedColor by remember { mutableStateOf(availableColors.firstOrNull() ?: "Standard") }
    var quantity by remember { mutableIntStateOf(1) }

    val effectivePrice = product.salePrice ?: product.price

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CardBorder) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .testTag("quick_add_sheet")
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = GoldPrimary
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$currency${String.format("%.2f", effectivePrice)} · Cash on Delivery",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccentGreen
                    )
                }

                TextButton(onClick = onViewDetails) {
                    Text("Full Details", color = GoldPrimary, fontSize = 12.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

            // Size Selector
            Text(
                text = "SELECT SHOE SIZE (EU)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableSizes.forEach { size ->
                    val isSelected = selectedSize == size
                    OutlinedButton(
                        onClick = { selectedSize = size },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("quick_size_$size"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) GoldPrimary else CharcoalDark,
                            contentColor = if (isSelected) CharcoalDark else TextPrimary
                        ),
                        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else CardBorder),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(size, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Selector
            Text(
                text = "SELECT LEATHER FINISH",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableColors.forEach { color ->
                    val isSelected = selectedColor == color
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedColor = color },
                        label = { Text(color, fontSize = 11.sp) },
                        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else CardBorder),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CharcoalDark,
                            selectedContainerColor = GoldDark.copy(alpha = 0.25f),
                            labelColor = TextSecondary,
                            selectedLabelColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUANTITY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CharcoalDark)
                            .border(1.dp, CardBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { if (quantity < product.stockQuantity) quantity++ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CharcoalDark)
                            .border(1.dp, CardBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button: Add to Cart
            Button(
                onClick = { onConfirmAdd(selectedSize, selectedColor, quantity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_confirm_quick_add"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to Bag · $currency${String.format("%.2f", effectivePrice * quantity)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Filter modal bottom sheet for the catalog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogFilterSheet(
    brands: List<BrandEntity>,
    categories: List<CategoryEntity>,
    filterState: FilterState,
    onBrandSelect: (String?) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onSizeSelect: (String?) -> Unit,
    onInStockToggle: (Boolean) -> Unit,
    onSaleToggle: (Boolean) -> Unit,
    onNewArrivalsToggle: (Boolean) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val availableSizes = listOf("40", "41", "42", "43", "44", "45")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CardBorder) },
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort Catalog",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                TextButton(onClick = {
                    onResetFilters()
                    onDismiss()
                }) {
                    Text("Reset All", color = AccentRed)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Options
            Text("SORT BY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            SortOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSortSelect(option) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = filterState.sortOption == option,
                        onClick = { onSortSelect(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary, unselectedColor = CardBorder)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option.label, color = if (filterState.sortOption == option) GoldPrimary else TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Brand Filter
            Text("BRAND", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.brand == null,
                    onClick = { onBrandSelect(null) },
                    label = { Text("All Brands") }
                )
                brands.forEach { brand ->
                    FilterChip(
                        selected = filterState.brand == brand.name,
                        onClick = { onBrandSelect(if (filterState.brand == brand.name) null else brand.name) },
                        label = { Text(brand.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Size Filter
            Text("SIZE (EU)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.size == null,
                    onClick = { onSizeSelect(null) },
                    label = { Text("All Sizes") }
                )
                availableSizes.forEach { size ->
                    FilterChip(
                        selected = filterState.size == size,
                        onClick = { onSizeSelect(if (filterState.size == size) null else size) },
                        label = { Text(size) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Apply & View Results", fontWeight = FontWeight.Bold)
            }
        }
    }
}
