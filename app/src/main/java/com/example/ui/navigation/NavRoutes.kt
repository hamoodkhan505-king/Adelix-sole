package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Shop : Screen("shop")
    object Brands : Screen("brands")
    object NewArrivals : Screen("new_arrivals")
    object Sale : Screen("sale")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: Long) = "order_confirmation/$orderId"
    }
    object TrackOrder : Screen("track_order")
    object Contact : Screen("contact")

    // Admin Screens
    object AdminLogin : Screen("admin_login")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProducts : Screen("admin_products")
    object AdminProductEdit : Screen("admin_product_edit/{productId}") {
        fun createRoute(productId: Long?) = "admin_product_edit/${productId ?: -1L}"
    }
    object AdminOrders : Screen("admin_orders")
    object AdminOrderDetail : Screen("admin_order_detail/{orderId}") {
        fun createRoute(orderId: Long) = "admin_order_detail/$orderId"
    }
    object AdminInventory : Screen("admin_inventory")
    object AdminBrandsCategories : Screen("admin_brands_categories")
    object AdminSettings : Screen("admin_settings")
}
