package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'Active' ORDER BY id DESC")
    fun getActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): ProductEntity?

    @Query("SELECT * FROM products WHERE isNewArrival = 1 AND status = 'Active' ORDER BY id DESC")
    fun getNewArrivals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND status = 'Active' ORDER BY id DESC")
    fun getFeaturedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isBestSeller = 1 AND status = 'Active' ORDER BY id DESC")
    fun getBestSellers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isSale = 1 AND status = 'Active' ORDER BY id DESC")
    fun getSaleProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE brand = :brandName AND status = 'Active' ORDER BY id DESC")
    fun getProductsByBrand(brandName: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category AND status = 'Active' ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%') AND status = 'Active' ORDER BY id DESC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stockQuantity <= :threshold ORDER BY stockQuantity ASC")
    fun getLowStockProducts(threshold: Int = 3): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE status = 'Active'")
    fun countActiveProducts(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = :newStock, status = CASE WHEN :newStock <= 0 THEN 'Out of Stock' ELSE status END WHERE id = :id")
    suspend fun updateStock(id: Long, newStock: Int)
}

@Dao
interface BrandDao {
    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledBrands(): Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands WHERE id = :id LIMIT 1")
    suspend fun getBrandById(id: Long): BrandEntity?

    @Query("SELECT * FROM brands WHERE name = :name LIMIT 1")
    suspend fun getBrandByName(name: String): BrandEntity?

    @Query("SELECT * FROM brands WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchBrands(query: String): Flow<List<BrandEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBrands(brands: List<BrandEntity>)

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Delete
    suspend fun deleteBrand(brand: BrandEntity)

    @Query("UPDATE brands SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateBrandStatus(id: Long, isEnabled: Boolean)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY lastOrderTimestamp DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): CustomerEntity?

    @Query("SELECT * FROM customers WHERE phoneNumber = :phone OR whatsappNumber = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE email = :email LIMIT 1")
    suspend fun getCustomerByEmail(email: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE fullName LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' ORDER BY totalSpent DESC")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Query("SELECT COUNT(*) FROM customers")
    fun getCustomerCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCustomers(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("UPDATE customers SET totalOrdersCount = totalOrdersCount + 1, totalSpent = totalSpent + :orderAmount, lastOrderTimestamp = :timestamp, defaultAddress = :address, city = :city, province = :province, area = :area WHERE phoneNumber = :phone")
    suspend fun recordCustomerOrder(
        phone: String,
        orderAmount: Double,
        timestamp: Long,
        address: String,
        city: String,
        province: String,
        area: String
    )
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAtTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAtTimestamp DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerPhone = :phone OR customerWhatsapp = :phone ORDER BY createdAtTimestamp DESC")
    fun getOrdersByCustomerPhone(phone: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber AND (customerPhone = :phone OR customerWhatsapp = :phone) LIMIT 1")
    suspend fun trackOrder(orderNumber: String, phone: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' OR customerPhone LIKE '%' || :query || '%' ORDER BY createdAtTimestamp DESC")
    fun searchOrders(query: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAtTimestamp DESC LIMIT :limit")
    fun getRecentOrders(limit: Int = 10): Flow<List<OrderEntity>>

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status != 'Cancelled'")
    fun getTotalSalesAmount(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    fun getOrderCountByStatus(status: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: Long): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsSync(orderId: Long): List<OrderItemEntity>
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId AND isApproved = 1 ORDER BY createdAtTimestamp DESC")
    fun getApprovedReviewsForProduct(productId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAtTimestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviews(reviews: List<ReviewEntity>)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)
}

@Dao
interface StoreSettingsDao {
    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<StoreSettingsEntity?>

    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): StoreSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: StoreSettingsEntity)
}

/**
 * Server-Side DAO for Single Authorized Store Owner Credentials.
 * Public sign-up is impossible: queries strictly operate on owner account (id = 1).
 */
@Dao
interface AdminAuthDao {
    @Query("SELECT * FROM admin_auth WHERE id = 1 LIMIT 1")
    suspend fun getOwnerAccount(): AdminAuthEntity?

    @Query("SELECT * FROM admin_auth WHERE id = 1 LIMIT 1")
    fun getOwnerAccountFlow(): Flow<AdminAuthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(adminAuth: AdminAuthEntity)

    @Query("UPDATE admin_auth SET lastLoginTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastLogin(timestamp: Long)

    @Query("UPDATE admin_auth SET username = :username, passwordHash = :hash, saltHex = :salt, updatedAtTimestamp = :timestamp WHERE id = 1")
    suspend fun updateCredentials(username: String, hash: String, salt: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM admin_auth")
    suspend fun getAdminCount(): Int
}
