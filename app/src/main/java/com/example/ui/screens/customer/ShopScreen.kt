package com.example.ui.screens.customer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.entity.BrandEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ProductEntity
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.SortOption

/**
 * Legacy wrapper forwarding to the new [ProductCatalogScreen] with high-end LazyVerticalGrid
 * and quick-add functionality.
 */
@Composable
fun ShopScreen(
    products: List<ProductEntity>,
    brands: List<BrandEntity>,
    categories: List<CategoryEntity>,
    filterState: FilterState,
    currency: String,
    onSearchChange: (String) -> Unit,
    onBrandSelect: (String?) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onSizeSelect: (String?) -> Unit,
    onInStockToggle: (Boolean) -> Unit,
    onSaleToggle: (Boolean) -> Unit,
    onNewArrivalsToggle: (Boolean) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCart: (ProductEntity, String, String, Int) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    ProductCatalogScreen(
        products = products,
        brands = brands,
        categories = categories,
        filterState = filterState,
        currency = currency,
        onSearchChange = onSearchChange,
        onBrandSelect = onBrandSelect,
        onCategorySelect = onCategorySelect,
        onSizeSelect = onSizeSelect,
        onInStockToggle = onInStockToggle,
        onSaleToggle = onSaleToggle,
        onNewArrivalsToggle = onNewArrivalsToggle,
        onSortSelect = onSortSelect,
        onResetFilters = onResetFilters,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart,
        modifier = modifier
    )
}
