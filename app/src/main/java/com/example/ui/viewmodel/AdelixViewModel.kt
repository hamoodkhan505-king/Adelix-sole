package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.*
import com.example.data.model.CartItem
import com.example.data.repository.AdelixRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    NEWEST("Newest Arrivals"),
    BEST_SELLER("Best Sellers")
}

data class FilterState(
    val query: String = "",
    val brand: String? = null,
    val category: String? = null,
    val size: String? = null,
    val color: String? = null,
    val inStockOnly: Boolean = false,
    val saleOnly: Boolean = false,
    val newArrivalsOnly: Boolean = false,
    val sortOption: SortOption = SortOption.FEATURED
)

class AdelixViewModel(
    private val repository: AdelixRepository
) : ViewModel() {

    // Repository Flows
    val allProducts = repository.allProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeProducts = repository.activeProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val brands = repository.allBrands.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val enabledBrands = repository.enabledBrands.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val categories = repository.allCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val storeSettings = repository.storeSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        StoreSettingsEntity()
    )
    val allOrders = repository.allOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allCustomers = repository.allCustomers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allReviews = repository.allReviews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Filter & Search State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Admin Auth State
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Order Tracking State
    private val _trackedOrder = MutableStateFlow<OrderEntity?>(null)
    val trackedOrder: StateFlow<OrderEntity?> = _trackedOrder.asStateFlow()

    private val _trackedOrderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val trackedOrderItems: StateFlow<List<OrderItemEntity>> = _trackedOrderItems.asStateFlow()

    private val _trackingError = MutableStateFlow<String?>(null)
    val trackingError: StateFlow<String?> = _trackingError.asStateFlow()

    private val _isTrackingLoading = MutableStateFlow(false)
    val isTrackingLoading: StateFlow<Boolean> = _isTrackingLoading.asStateFlow()

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = _cart.map { items ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartDeliveryCharges: StateFlow<Double> = combine(_cart, storeSettings) { items, settings ->
        val subtotal = items.sumOf { it.totalPrice }
        val threshold = settings?.freeDeliveryThreshold ?: 150.0
        val baseCharge = settings?.deliveryCharges ?: 15.0
        if (subtotal <= 0.0) 0.0 else if (subtotal >= threshold) 0.0 else baseCharge
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, cartDeliveryCharges) { sub, del ->
        sub + del
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Filtered Products for Shop
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        activeProducts,
        _filterState
    ) { products, filter ->
        var list = products

        // Search query
        if (filter.query.isNotBlank()) {
            val q = filter.query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
        }

        // Brand filter
        filter.brand?.let { b ->
            if (b.isNotBlank() && b != "All Brands") {
                list = list.filter { it.brand.equals(b, ignoreCase = true) }
            }
        }

        // Category filter
        filter.category?.let { c ->
            if (c.isNotBlank() && c != "All") {
                list = list.filter { it.category.equals(c, ignoreCase = true) }
            }
        }

        // Size filter
        filter.size?.let { s ->
            if (s.isNotBlank() && s != "All") {
                list = list.filter { it.sizes.split(",").map { sz -> sz.trim() }.contains(s) }
            }
        }

        // In Stock Only
        if (filter.inStockOnly) {
            list = list.filter { it.stockQuantity > 0 && it.status == "Active" }
        }

        // Sale Only
        if (filter.saleOnly) {
            list = list.filter { it.isSale || (it.salePrice != null && it.salePrice < it.price) }
        }

        // New Arrivals Only
        if (filter.newArrivalsOnly) {
            list = list.filter { it.isNewArrival }
        }

        // Sorting
        when (filter.sortOption) {
            SortOption.FEATURED -> list.sortedByDescending { if (it.isFeatured) 1 else 0 }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.salePrice ?: it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.salePrice ?: it.price }
            SortOption.NEWEST -> list.sortedByDescending { if (it.isNewArrival) 1 else 0 }
            SortOption.BEST_SELLER -> list.sortedByDescending { if (it.isBestSeller) 1 else 0 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Operations
    fun addToCart(
        product: ProductEntity,
        size: String,
        color: String,
        quantity: Int = 1,
        customizationText: String = "",
        customizationNotes: String = ""
    ) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst {
            it.product.id == product.id &&
            it.selectedSize == size &&
            it.selectedColor == color &&
            it.customizationText == customizationText
        }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(
                CartItem(
                    product = product,
                    selectedSize = size,
                    selectedColor = color,
                    quantity = quantity,
                    customizationText = customizationText,
                    customizationNotes = customizationNotes
                )
            )
        }
        _cart.value = current
    }

    fun updateCartQuantity(cartItem: CartItem, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(cartItem)
            return
        }
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.itemKey == cartItem.itemKey }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = newQty)
            _cart.value = current
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        _cart.value = _cart.value.filterNot { it.itemKey == cartItem.itemKey }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Filter mutations
    fun setSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun setBrandFilter(brand: String?) {
        _filterState.value = _filterState.value.copy(brand = brand)
    }

    fun setCategoryFilter(category: String?) {
        _filterState.value = _filterState.value.copy(category = category)
    }

    fun setSizeFilter(size: String?) {
        _filterState.value = _filterState.value.copy(size = size)
    }

    fun setInStockOnly(inStock: Boolean) {
        _filterState.value = _filterState.value.copy(inStockOnly = inStock)
    }

    fun setSaleOnly(saleOnly: Boolean) {
        _filterState.value = _filterState.value.copy(saleOnly = saleOnly)
    }

    fun setNewArrivalsOnly(newArrivalsOnly: Boolean) {
        _filterState.value = _filterState.value.copy(newArrivalsOnly = newArrivalsOnly)
    }

    fun setSortOption(sortOption: SortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sortOption)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // Order Placement (Cash on Delivery)
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerWhatsapp: String,
        province: String,
        city: String,
        area: String,
        deliveryAddress: String,
        orderNotes: String,
        customizationNotes: String,
        onSuccessWithItems: (OrderEntity, List<OrderItemEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val items = _cart.value.map { cartItem ->
                OrderItemEntity(
                    orderId = 0,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    brand = cartItem.product.brand,
                    price = cartItem.unitPrice,
                    size = cartItem.selectedSize,
                    color = cartItem.selectedColor,
                    quantity = cartItem.quantity,
                    imageUrl = cartItem.product.imageUrl
                )
            }

            val subtotal = cartSubtotal.value
            val delivery = cartDeliveryCharges.value
            val total = cartTotal.value

            val order = repository.createOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                customerWhatsapp = customerWhatsapp,
                province = province,
                city = city,
                area = area,
                deliveryAddress = deliveryAddress,
                orderNotes = orderNotes,
                customizationNotes = customizationNotes,
                subtotal = subtotal,
                deliveryCharges = delivery,
                totalAmount = total,
                items = items
            )

            clearCart()
            onSuccessWithItems(order, items)
        }
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerWhatsapp: String,
        province: String,
        city: String,
        area: String,
        deliveryAddress: String,
        orderNotes: String,
        customizationNotes: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        placeOrder(
            customerName, customerPhone, customerWhatsapp, province, city, area,
            deliveryAddress, orderNotes, customizationNotes
        ) { order, _ ->
            onSuccess(order)
        }
    }

    // Track Order
    fun trackOrder(orderNumber: String, phone: String) {
        _isTrackingLoading.value = true
        _trackingError.value = null
        viewModelScope.launch {
            val order = repository.trackOrder(orderNumber, phone)
            if (order != null) {
                val items = repository.getOrderItems(order.id)
                _trackedOrder.value = order
                _trackedOrderItems.value = items
                _trackingError.value = null
            } else {
                _trackedOrder.value = null
                _trackedOrderItems.value = emptyList()
                _trackingError.value = "No order found matching \"$orderNumber\". Please verify your Order ID and Phone Number."
            }
            _isTrackingLoading.value = false
        }
    }

    fun loadOrderForTracking(orderNumber: String) {
        viewModelScope.launch {
            val order = repository.getOrderByNumber(orderNumber)
            if (order != null) {
                val items = repository.getOrderItems(order.id)
                _trackedOrder.value = order
                _trackedOrderItems.value = items
            }
        }
    }

    // Admin Auth
    fun adminLogin(emailOrUser: String, pass: String): Boolean {
        val trimmedUser = emailOrUser.trim()
        val trimmedPass = pass.trim()
        // Secure demo credentials for Adelix Sole Admin
        val isValid = (trimmedUser.equals("admin", ignoreCase = true) || trimmedUser.equals("admin@adelixsole.com", ignoreCase = true)) &&
                trimmedPass == "admin123"
        _isAdminAuthenticated.value = isValid
        return isValid
    }

    fun adminLogout() {
        _isAdminAuthenticated.value = false
    }

    // Admin Order Management
    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            // If currently tracking this order, refresh it
            if (_trackedOrder.value?.id == orderId) {
                _trackedOrder.value = _trackedOrder.value?.copy(status = newStatus)
            }
        }
    }

    // Admin Product Management
    fun saveProduct(product: ProductEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
            onDone()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun duplicateProduct(product: ProductEntity) {
        viewModelScope.launch {
            val copy = product.copy(
                id = 0L,
                name = "${product.name} (Copy)",
                sku = "${product.sku}-CPY"
            )
            repository.insertProduct(copy)
        }
    }

    fun updateProductStock(productId: Long, newStock: Int) {
        viewModelScope.launch {
            repository.updateStock(productId, newStock)
        }
    }

    // Admin Brand Management
    fun saveBrand(brand: BrandEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            if (brand.id == 0L) {
                repository.insertBrand(brand)
            } else {
                repository.updateBrand(brand)
            }
            onDone()
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.deleteBrand(brand)
        }
    }

    // Admin Category Management
    fun saveCategory(category: CategoryEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            if (category.id == 0L) {
                repository.insertCategory(category)
            } else {
                repository.updateCategory(category)
            }
            onDone()
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Admin Settings
    fun updateSettings(settings: StoreSettingsEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            onDone()
        }
    }

    // Reviews
    fun getProductReviews(productId: Long): Flow<List<ReviewEntity>> {
        return repository.getApprovedReviewsForProduct(productId)
    }

    fun submitReview(productId: Long, name: String, rating: Int, text: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertReview(
                ReviewEntity(
                    productId = productId,
                    customerName = if (name.isBlank()) "Verified Buyer" else name.trim(),
                    rating = rating,
                    reviewText = text.trim(),
                    isApproved = true // Auto-approved for delightful user flow, can be moderated in admin
                )
            )
            onDone()
        }
    }

    fun approveReview(review: ReviewEntity) {
        viewModelScope.launch {
            repository.updateReview(review.copy(isApproved = true))
        }
    }

    fun deleteReview(review: ReviewEntity) {
        viewModelScope.launch {
            repository.deleteReview(review)
        }
    }

    // Customers
    fun insertCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.insertCustomer(customer)
        }
    }

    fun updateCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }
}
