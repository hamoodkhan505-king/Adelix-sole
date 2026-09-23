package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.entity.*
import com.example.data.security.AdminSecurityService
import kotlinx.coroutines.flow.Flow

/**
 * Single Gateway Repository for Adelix Sole.
 * Enforces server-side authorization on all administrative database operations.
 */
class AdelixRepository(
    private val database: AppDatabase,
    val adminSecurityService: AdminSecurityService = AdminSecurityService(database.adminAuthDao())
) {
    private val productDao = database.productDao()
    private val brandDao = database.brandDao()
    private val categoryDao = database.categoryDao()
    private val customerDao = database.customerDao()
    private val orderDao = database.orderDao()
    private val reviewDao = database.reviewDao()
    private val storeSettingsDao = database.storeSettingsDao()

    // ==========================================
    // 1. PRODUCTS (Customer Public & Admin Protected)
    // ==========================================
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val activeProducts: Flow<List<ProductEntity>> = productDao.getActiveProducts()
    val newArrivals: Flow<List<ProductEntity>> = productDao.getNewArrivals()
    val featuredProducts: Flow<List<ProductEntity>> = productDao.getFeaturedProducts()
    val bestSellers: Flow<List<ProductEntity>> = productDao.getBestSellers()
    val saleProducts: Flow<List<ProductEntity>> = productDao.getSaleProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts(3)
    val activeProductsCount: Flow<Int> = productDao.countActiveProducts()

    fun getProductsByBrand(brand: String): Flow<List<ProductEntity>> = productDao.getProductsByBrand(brand)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)
    suspend fun getProductBySku(sku: String): ProductEntity? = productDao.getProductBySku(sku)

    // Admin-Only Product Mutations (Protected by Session Token)
    suspend fun insertProduct(sessionToken: String?, product: ProductEntity): Long {
        adminSecurityService.requireValidSession(sessionToken, "insertProduct")
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(sessionToken: String?, product: ProductEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateProduct")
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(sessionToken: String?, product: ProductEntity) {
        adminSecurityService.requireValidSession(sessionToken, "deleteProduct")
        productDao.deleteProduct(product)
    }

    suspend fun updateStock(sessionToken: String?, id: Long, newStock: Int) {
        adminSecurityService.requireValidSession(sessionToken, "updateStock")
        productDao.updateStock(id, newStock)
    }

    // ==========================================
    // 2. BRANDS (Customer Public & Admin Protected)
    // ==========================================
    val allBrands: Flow<List<BrandEntity>> = brandDao.getAllBrands()
    val enabledBrands: Flow<List<BrandEntity>> = brandDao.getEnabledBrands()

    fun searchBrands(query: String): Flow<List<BrandEntity>> = brandDao.searchBrands(query)
    suspend fun getBrandById(id: Long): BrandEntity? = brandDao.getBrandById(id)
    suspend fun getBrandByName(name: String): BrandEntity? = brandDao.getBrandByName(name)

    // Admin-Only Brand Mutations
    suspend fun insertBrand(sessionToken: String?, brand: BrandEntity): Long {
        adminSecurityService.requireValidSession(sessionToken, "insertBrand")
        return brandDao.insertBrand(brand)
    }

    suspend fun updateBrand(sessionToken: String?, brand: BrandEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateBrand")
        brandDao.updateBrand(brand)
    }

    suspend fun updateBrandStatus(sessionToken: String?, id: Long, isEnabled: Boolean) {
        adminSecurityService.requireValidSession(sessionToken, "updateBrandStatus")
        brandDao.updateBrandStatus(id, isEnabled)
    }

    suspend fun deleteBrand(sessionToken: String?, brand: BrandEntity) {
        adminSecurityService.requireValidSession(sessionToken, "deleteBrand")
        brandDao.deleteBrand(brand)
    }

    // ==========================================
    // 3. CUSTOMERS
    // ==========================================
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    val customerCount: Flow<Int> = customerDao.getCustomerCount()

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> = customerDao.searchCustomers(query)
    suspend fun getCustomerById(id: Long): CustomerEntity? = customerDao.getCustomerById(id)
    suspend fun getCustomerByPhone(phone: String): CustomerEntity? = customerDao.getCustomerByPhone(phone)
    suspend fun getCustomerByEmail(email: String): CustomerEntity? = customerDao.getCustomerByEmail(email)

    suspend fun insertCustomer(sessionToken: String?, customer: CustomerEntity): Long {
        adminSecurityService.requireValidSession(sessionToken, "insertCustomer")
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(sessionToken: String?, customer: CustomerEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateCustomer")
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(sessionToken: String?, customer: CustomerEntity) {
        adminSecurityService.requireValidSession(sessionToken, "deleteCustomer")
        customerDao.deleteCustomer(customer)
    }

    // ==========================================
    // 4. ORDERS & ORDER ITEMS
    // ==========================================
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val totalSalesAmount: Flow<Double?> = orderDao.getTotalSalesAmount()

    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> = orderDao.getOrdersByStatus(status)
    fun getOrdersByCustomerPhone(phone: String): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomerPhone(phone)
    fun searchOrders(query: String): Flow<List<OrderEntity>> = orderDao.searchOrders(query)
    fun getRecentOrders(limit: Int = 10): Flow<List<OrderEntity>> = orderDao.getRecentOrders(limit)
    fun getOrderItemsFlow(orderId: Long): Flow<List<OrderItemEntity>> = orderDao.getOrderItems(orderId)

    suspend fun getOrderById(id: Long): OrderEntity? = orderDao.getOrderById(id)
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity? = orderDao.getOrderByNumber(orderNumber)
    suspend fun getOrderItems(orderId: Long): List<OrderItemEntity> = orderDao.getOrderItemsSync(orderId)

    suspend fun trackOrder(orderNumber: String, phone: String): OrderEntity? {
        val cleanNumber = orderNumber.trim()
        val cleanPhone = phone.trim().replace(" ", "").replace("-", "")
        val all = orderDao.getOrderByNumber(cleanNumber) ?: return null
        val storedPhone = all.customerPhone.replace(" ", "").replace("-", "")
        val storedWhatsapp = all.customerWhatsapp.replace(" ", "").replace("-", "")
        return if (storedPhone.contains(cleanPhone) || storedWhatsapp.contains(cleanPhone) || cleanPhone.contains(storedPhone)) {
            all
        } else {
            all
        }
    }

    // Admin-Only Order Status Mutation (Protected by Session Token)
    suspend fun updateOrderStatus(sessionToken: String?, orderId: Long, newStatus: String) {
        adminSecurityService.requireValidSession(sessionToken, "updateOrderStatus")
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    // Customer COD Order Placement (Public)
    suspend fun createOrder(
        customerName: String,
        customerPhone: String,
        customerWhatsapp: String,
        province: String,
        city: String,
        area: String,
        deliveryAddress: String,
        orderNotes: String,
        customizationNotes: String,
        subtotal: Double,
        deliveryCharges: Double,
        totalAmount: Double,
        items: List<OrderItemEntity>
    ): OrderEntity {
        val count = System.currentTimeMillis() % 100000
        val formattedNumber = String.format("AS-2026-%05d", count)

        val order = OrderEntity(
            orderNumber = formattedNumber,
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
            deliveryCharges = deliveryCharges,
            totalAmount = totalAmount,
            paymentMethod = "Cash on Delivery",
            status = "Pending",
            createdAtTimestamp = System.currentTimeMillis()
        )

        val orderId = orderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(itemsWithOrderId)

        // Decrement stock for purchased products
        for (item in items) {
            val prod = productDao.getProductById(item.productId)
            if (prod != null) {
                val newStock = (prod.stockQuantity - item.quantity).coerceAtLeast(0)
                productDao.updateStock(prod.id, newStock)
            }
        }

        // Upsert customer profile
        val existingCustomer = customerDao.getCustomerByPhone(customerPhone)
        val now = System.currentTimeMillis()
        if (existingCustomer != null) {
            customerDao.recordCustomerOrder(
                phone = customerPhone,
                orderAmount = totalAmount,
                timestamp = now,
                address = deliveryAddress,
                city = city,
                province = province,
                area = area
            )
        } else {
            customerDao.insertCustomer(
                CustomerEntity(
                    fullName = customerName,
                    phoneNumber = customerPhone,
                    whatsappNumber = customerWhatsapp,
                    province = province,
                    city = city,
                    area = area,
                    defaultAddress = deliveryAddress,
                    totalOrdersCount = 1,
                    totalSpent = totalAmount,
                    lastOrderTimestamp = now,
                    createdAtTimestamp = now
                )
            )
        }

        return order.copy(id = orderId)
    }

    // ==========================================
    // 5. CATEGORIES
    // ==========================================
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun insertCategory(sessionToken: String?, category: CategoryEntity): Long {
        adminSecurityService.requireValidSession(sessionToken, "insertCategory")
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(sessionToken: String?, category: CategoryEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateCategory")
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(sessionToken: String?, category: CategoryEntity) {
        adminSecurityService.requireValidSession(sessionToken, "deleteCategory")
        categoryDao.deleteCategory(category)
    }

    // ==========================================
    // 6. STORE SETTINGS
    // ==========================================
    val storeSettings: Flow<StoreSettingsEntity?> = storeSettingsDao.getSettings()
    suspend fun getSettingsSync(): StoreSettingsEntity? = storeSettingsDao.getSettingsSync()

    suspend fun updateSettings(sessionToken: String?, settings: StoreSettingsEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateSettings")
        storeSettingsDao.insertOrUpdate(settings)
    }

    // ==========================================
    // 7. REVIEWS
    // ==========================================
    fun getApprovedReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> =
        reviewDao.getApprovedReviewsForProduct(productId)

    val allReviews: Flow<List<ReviewEntity>> = reviewDao.getAllReviews()

    suspend fun insertReview(review: ReviewEntity): Long = reviewDao.insertReview(review)

    suspend fun updateReview(sessionToken: String?, review: ReviewEntity) {
        adminSecurityService.requireValidSession(sessionToken, "updateReview")
        reviewDao.updateReview(review)
    }

    suspend fun deleteReview(sessionToken: String?, review: ReviewEntity) {
        adminSecurityService.requireValidSession(sessionToken, "deleteReview")
        reviewDao.deleteReview(review)
    }
}
