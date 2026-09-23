package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.components.*
import com.example.ui.screens.admin.*
import com.example.ui.screens.customer.*
import com.example.ui.theme.AdelixSoleTheme
import com.example.ui.theme.CharcoalDark
import com.example.ui.viewmodel.AdelixViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val app = application as AdelixApp
            val viewModel: AdelixViewModel = viewModel {
                AdelixViewModel(app.repository)
            }

            AdelixSoleTheme {
                AdelixMainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdelixMainContent(viewModel: AdelixViewModel) {
    // Navigation state
    var currentRoute by remember { mutableStateOf("home") }
    var previousRoute by remember { mutableStateOf("home") }
    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var selectedOrderId by remember { mutableStateOf<Long?>(null) }
    var lastCreatedOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var lastCreatedOrderItems by remember { mutableStateOf<List<OrderItemEntity>>(emptyList()) }

    fun navigateTo(route: String) {
        previousRoute = currentRoute
        currentRoute = route
    }

    // ViewModel State collection
    val activeProducts by viewModel.activeProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val brands by viewModel.brands.collectAsStateWithLifecycle()
    val enabledBrands by viewModel.enabledBrands.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val storeSettings by viewModel.storeSettings.collectAsStateWithLifecycle()
    val cartItems by viewModel.cart.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val cartDeliveryCharges by viewModel.cartDeliveryCharges.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val trackedOrder by viewModel.trackedOrder.collectAsStateWithLifecycle()
    val trackedOrderItems by viewModel.trackedOrderItems.collectAsStateWithLifecycle()
    val trackingError by viewModel.trackingError.collectAsStateWithLifecycle()
    val isTrackingLoading by viewModel.isTrackingLoading.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()
    val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()

    val settings = storeSettings ?: StoreSettingsEntity()
    val totalCartCount = cartItems.sumOf { it.quantity }

    val isAdminRoute = currentRoute.startsWith("admin_")
    val isProductDetail = currentRoute == "product_detail"
    val isOrderConfirmation = currentRoute == "order_confirmation"
    val isCheckout = currentRoute == "checkout"

    // Back button handling
    BackHandler(enabled = currentRoute != "home") {
        when {
            currentRoute == "admin_dashboard" -> currentRoute = "home"
            currentRoute.startsWith("admin_") && currentRoute != "admin_dashboard" && currentRoute != "admin_login" -> currentRoute = "admin_dashboard"
            currentRoute == "admin_login" -> currentRoute = "home"
            currentRoute == "order_confirmation" -> currentRoute = "home"
            currentRoute == "checkout" -> currentRoute = "cart"
            currentRoute == "product_detail" -> currentRoute = previousRoute.ifBlank { "shop" }
            else -> currentRoute = "home"
        }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            if (!isAdminRoute && !isCheckout && !isOrderConfirmation) {
                Column {
                    AnnouncementBar(text = settings.announcementText)
                    AdelixTopBar(
                        title = settings.storeName,
                        cartItemCount = totalCartCount,
                        canNavigateBack = isProductDetail,
                        onNavigateBack = { currentRoute = previousRoute.ifBlank { "shop" } },
                        onCartClick = { navigateTo("cart") },
                        onSearchClick = {
                            viewModel.resetFilters()
                            navigateTo("shop")
                        },
                        onAdminClick = {
                            if (isAdminAuthenticated) {
                                navigateTo("admin_dashboard")
                            } else {
                                navigateTo("admin_login")
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (!isAdminRoute && !isCheckout && !isOrderConfirmation && !isProductDetail) {
                AdelixBottomNavBar(
                    currentRoute = currentRoute,
                    cartItemCount = totalCartCount,
                    onNavigateToHome = { navigateTo("home") },
                    onNavigateToShop = {
                        viewModel.resetFilters()
                        navigateTo("shop")
                    },
                    onNavigateToBrands = { navigateTo("brands") },
                    onNavigateToTrack = { navigateTo("track_order") },
                    onNavigateToCart = { navigateTo("cart") }
                )
            }
        },
        floatingActionButton = {
            if (!isAdminRoute && !isCheckout && !isOrderConfirmation) {
                FloatingWhatsAppButton(
                    phoneNumber = settings.whatsappNumber.ifBlank { "+923187174601" },
                    message = "Assalam o Alaikum Adelix Sole, I need help regarding your products/order."
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CharcoalDark)
        ) {
            when (currentRoute) {
                // Customer Routes
                "home" -> {
                    HomeScreen(
                        settings = settings,
                        products = activeProducts,
                        brands = enabledBrands,
                        reviews = allReviews,
                        onNavigateToShop = {
                            viewModel.resetFilters()
                            navigateTo("shop")
                        },
                        onNavigateToProduct = { id ->
                            selectedProductId = id
                            navigateTo("product_detail")
                        },
                        onNavigateToBrand = { brandName ->
                            viewModel.resetFilters()
                            viewModel.setBrandFilter(brandName)
                            navigateTo("shop")
                        },
                        onNavigateToNewArrivals = {
                            viewModel.resetFilters()
                            viewModel.setNewArrivalsOnly(true)
                            navigateTo("shop")
                        },
                        onNavigateToSale = {
                            viewModel.resetFilters()
                            viewModel.setSaleOnly(true)
                            navigateTo("shop")
                        },
                        onNavigateToTrack = { navigateTo("track_order") },
                        onNavigateToContact = { navigateTo("contact") },
                        onAdminLoginClick = {
                            if (isAdminAuthenticated) navigateTo("admin_dashboard")
                            else navigateTo("admin_login")
                        }
                    )
                }

                "shop" -> {
                    ProductCatalogScreen(
                        products = filteredProducts,
                        brands = enabledBrands,
                        categories = categories,
                        filterState = filterState,
                        currency = settings.currency,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onBrandSelect = { viewModel.setBrandFilter(it) },
                        onCategorySelect = { viewModel.setCategoryFilter(it) },
                        onSizeSelect = { viewModel.setSizeFilter(it) },
                        onInStockToggle = { viewModel.setInStockOnly(it) },
                        onSaleToggle = { viewModel.setSaleOnly(it) },
                        onNewArrivalsToggle = { viewModel.setNewArrivalsOnly(it) },
                        onSortSelect = { viewModel.setSortOption(it) },
                        onResetFilters = { viewModel.resetFilters() },
                        onProductClick = { id ->
                            selectedProductId = id
                            navigateTo("product_detail")
                        },
                        onAddToCart = { prod, size, color, qty ->
                            viewModel.addToCart(prod, size, color, qty)
                        }
                    )
                }

                "brands" -> {
                    BrandsScreen(
                        brands = enabledBrands,
                        products = activeProducts,
                        onSelectBrand = { brandName ->
                            viewModel.resetFilters()
                            viewModel.setBrandFilter(brandName)
                            navigateTo("shop")
                        }
                    )
                }

                "product_detail" -> {
                    val currentProduct = activeProducts.firstOrNull { it.id == selectedProductId }
                        ?: allProducts.firstOrNull { it.id == selectedProductId }
                    val currentReviews = allReviews.filter { it.productId == selectedProductId }

                    ProductDetailScreen(
                        product = currentProduct,
                        settings = settings,
                        reviews = currentReviews,
                        onAddToCart = { prod, size, color, qty, customText, customNotes ->
                            viewModel.addToCart(prod, size, color, qty, customText, customNotes)
                        },
                        onBuyNow = { prod, size, color, qty, customText, customNotes ->
                            viewModel.addToCart(prod, size, color, qty, customText, customNotes)
                            navigateTo("checkout")
                        },
                        onSubmitReview = { name, rating, text ->
                            selectedProductId?.let { pid ->
                                viewModel.submitReview(pid, name, rating, text)
                            }
                        },
                        onBackClick = { currentRoute = previousRoute.ifBlank { "shop" } }
                    )
                }

                "cart" -> {
                    CartScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        deliveryCharges = cartDeliveryCharges,
                        total = cartTotal,
                        settings = settings,
                        onUpdateQuantity = { item, qty -> viewModel.updateCartQuantity(item, qty) },
                        onRemoveItem = { item -> viewModel.removeFromCart(item) },
                        onCheckoutClick = { navigateTo("checkout") },
                        onContinueShopping = {
                            viewModel.resetFilters()
                            navigateTo("shop")
                        }
                    )
                }

                "checkout" -> {
                    val context = LocalContext.current
                    CheckoutScreen(
                        cartItems = cartItems,
                        subtotal = cartSubtotal,
                        deliveryCharges = cartDeliveryCharges,
                        total = cartTotal,
                        settings = settings,
                        onPlaceOrder = { name, phone, whatsapp, province, city, area, address, notes, customNotes, onSuccess ->
                            viewModel.placeOrder(
                                name, phone, whatsapp, province, city, area, address, notes, customNotes
                            ) { created, items ->
                                lastCreatedOrder = created
                                lastCreatedOrderItems = items

                                // 1. Automatically dispatch Email Notification to store owner (ah6202429@gmail.com)
                                com.example.util.OrderNotificationManager.dispatchEmailOrderNotification(
                                    context = context,
                                    order = created,
                                    items = items,
                                    currency = settings.currency,
                                    recipientEmail = settings.email.ifBlank { com.example.util.OrderNotificationManager.STORE_OWNER_EMAIL }
                                )

                                // 2. Automatically prepare/open WhatsApp communication to store owner (+923187174601)
                                com.example.util.OrderNotificationManager.openWhatsAppOrderNotification(
                                    context = context,
                                    order = created,
                                    items = items,
                                    currency = settings.currency,
                                    ownerPhone = settings.whatsappNumber.ifBlank { com.example.util.OrderNotificationManager.STORE_OWNER_WHATSAPP }
                                )

                                onSuccess(created)
                            }
                        },
                        onOrderPlaced = { created ->
                            lastCreatedOrder = created
                            navigateTo("order_confirmation")
                        },
                        onBackClick = { navigateTo("cart") }
                    )
                }

                "order_confirmation" -> {
                    OrderConfirmationScreen(
                        order = lastCreatedOrder,
                        items = lastCreatedOrderItems,
                        settings = settings,
                        onTrackOrder = { orderNumber ->
                            viewModel.loadOrderForTracking(orderNumber)
                            navigateTo("track_order")
                        },
                        onContinueShopping = {
                            viewModel.resetFilters()
                            navigateTo("shop")
                        }
                    )
                }

                "track_order" -> {
                    OrderTrackingScreen(
                        trackedOrder = trackedOrder,
                        orderItems = trackedOrderItems,
                        errorMessage = trackingError,
                        isLoading = isTrackingLoading,
                        settings = settings,
                        onTrackOrder = { ordId, ph ->
                            viewModel.trackOrder(ordId, ph)
                        }
                    )
                }

                "contact" -> {
                    ContactScreen(settings = settings)
                }

                // Admin Routes
                "admin_login" -> {
                    AdminLoginScreen(
                        onLogin = { user, pass ->
                            viewModel.adminLogin(user, pass)
                        },
                        onLoginSuccess = {
                            navigateTo("admin_dashboard")
                        },
                        onBackToStore = {
                            navigateTo("home")
                        }
                    )
                }

                "admin_dashboard" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminDashboardScreen(
                            orders = allOrders,
                            products = allProducts,
                            settings = settings,
                            customers = allCustomers,
                            onNavigateToProducts = { navigateTo("admin_products") },
                            onNavigateToOrders = { navigateTo("admin_orders") },
                            onNavigateToInventory = { navigateTo("admin_inventory") },
                            onNavigateToBrandsCategories = { navigateTo("admin_brands_categories") },
                            onNavigateToCustomers = { navigateTo("admin_customers") },
                            onNavigateToSettings = { navigateTo("admin_settings") },
                            onViewOrderDetail = { orderId ->
                                selectedOrderId = orderId
                                navigateTo("admin_orders")
                            },
                            onLogout = {
                                viewModel.adminLogout()
                                navigateTo("home")
                            }
                        )
                    }
                }

                "admin_products" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminProductsScreen(
                            products = allProducts,
                            brands = brands,
                            categories = categories,
                            currency = settings.currency,
                            onSaveProduct = { p -> viewModel.saveProduct(p) },
                            onDeleteProduct = { p -> viewModel.deleteProduct(p) },
                            onDuplicateProduct = { p -> viewModel.duplicateProduct(p) },
                            onBackClick = { navigateTo("admin_dashboard") }
                        )
                    }
                }

                "admin_orders" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        val app = com.example.AdelixApp.instance
                        AdminOrdersScreen(
                            orders = allOrders,
                            currency = settings.currency,
                            onUpdateStatus = { ordId, status -> viewModel.updateOrderStatus(ordId, status) },
                            getOrderItems = { ordId -> app.repository.getOrderItems(ordId) },
                            onBackClick = { navigateTo("admin_dashboard") }
                        )
                    }
                }

                "admin_inventory" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminInventoryScreen(
                            products = allProducts,
                            onUpdateStock = { id, stock -> viewModel.updateProductStock(id, stock) },
                            onBackClick = { navigateTo("admin_dashboard") }
                        )
                    }
                }

                "admin_brands_categories" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminBrandsCategoriesScreen(
                            brands = brands,
                            categories = categories,
                            onSaveBrand = { b -> viewModel.saveBrand(b) },
                            onDeleteBrand = { b -> viewModel.deleteBrand(b) },
                            onSaveCategory = { c -> viewModel.saveCategory(c) },
                            onDeleteCategory = { c -> viewModel.deleteCategory(c) },
                            onBackClick = { navigateTo("admin_dashboard") }
                        )
                    }
                }

                "admin_customers" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminCustomersScreen(
                            customers = allCustomers,
                            settings = settings,
                            onAddCustomer = { c -> viewModel.insertCustomer(c) },
                            onUpdateCustomer = { c -> viewModel.updateCustomer(c) },
                            onDeleteCustomer = { c -> viewModel.deleteCustomer(c) },
                            onBack = { navigateTo("admin_dashboard") }
                        )
                    }
                }

                "admin_settings" -> {
                    if (!isAdminAuthenticated) {
                        currentRoute = "admin_login"
                    } else {
                        AdminSettingsScreen(
                            settings = settings,
                            onSaveSettings = { s -> viewModel.updateSettings(s) },
                            onBackClick = { navigateTo("admin_dashboard") }
                        )
                    }
                }
            }
        }
    }
}
