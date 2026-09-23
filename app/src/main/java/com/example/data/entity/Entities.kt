package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["brand"]),
        Index(value = ["category"]),
        Index(value = ["status"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,
    val description: String,
    val price: Double,
    val salePrice: Double? = null,
    val discountPercent: Int = 0,
    val sizes: String, // Comma-separated: "40,41,42,43,44,45"
    val colors: String, // Comma-separated: "Onyx Black,Pure White,Cognac Brown"
    val stockQuantity: Int = 10,
    val sku: String,
    val isNewArrival: Boolean = false,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isSale: Boolean = false,
    val isCustomizable: Boolean = false,
    val customizationTimeDays: String = "3–5 Working Days",
    val status: String = "Active", // "Active", "Draft", "Out of Stock"
    val imageUrl: String = "",
    val specifications: String = "Upper: Full-Grain Leather | Sole: Vibram Rubber | Insole: Memory Foam | Origin: Handcrafted"
)

@Entity(
    tableName = "brands",
    indices = [Index(value = ["name"], unique = true)]
)
data class BrandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val logoText: String = "",
    val isEnabled: Boolean = true,
    val description: String = ""
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isEnabled: Boolean = true
)

@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["phoneNumber"], unique = true),
        Index(value = ["email"])
    ]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phoneNumber: String,
    val whatsappNumber: String = "",
    val email: String = "",
    val province: String = "",
    val city: String = "",
    val area: String = "",
    val defaultAddress: String = "",
    val totalOrdersCount: Int = 0,
    val totalSpent: Double = 0.0,
    val lastOrderTimestamp: Long = System.currentTimeMillis(),
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["orderNumber"], unique = true),
        Index(value = ["customerPhone"]),
        Index(value = ["status"]),
        Index(value = ["createdAtTimestamp"])
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. "AS-2026-00001"
    val customerName: String,
    val customerPhone: String,
    val customerWhatsapp: String,
    val province: String,
    val city: String,
    val area: String,
    val deliveryAddress: String,
    val orderNotes: String = "",
    val customizationNotes: String = "",
    val subtotal: Double,
    val deliveryCharges: Double,
    val totalAmount: Double,
    val paymentMethod: String = "Cash on Delivery",
    val status: String = "Pending", // "Pending", "Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled", "Returned"
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "order_items",
    indices = [Index(value = ["orderId"]), Index(value = ["productId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val brand: String,
    val price: Double,
    val size: String,
    val color: String,
    val quantity: Int,
    val imageUrl: String = ""
)

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["productId"])]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val customerName: String,
    val rating: Int, // 1 to 5
    val reviewText: String,
    val isApproved: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val storeName: String = "Adelix Sole",
    val whatsappNumber: String = "+923187174601",
    val phoneNumber: String = "+923187174601",
    val email: String = "ah6202429@gmail.com",
    val address: String = "Adelix Sole Flagship Studio, Downtown Galleria, Suite 400",
    val deliveryCharges: Double = 15.0,
    val freeDeliveryThreshold: Double = 150.0,
    val currency: String = "$",
    val codEnabled: Boolean = true,
    val announcementText: String = "Complimentary shipping on orders over $150 | Exclusive Autumn Footwear Collection",
    val footerText: String = "Adelix Sole — Handcrafted Luxury Footwear. Built for Elegance, Engineered for Comfort.",
    val heroBannerTitle: String = "Walk With Distinction",
    val heroBannerSubtitle: String = "Experience pinnacle artisan craftsmanship and ultra-premium modern comfort."
)

/**
 * Single Authorized Website Owner Account Entity.
 * Public sign-up is completely prohibited. Only 1 owner entity exists in the system (id = 1).
 * Credentials are protected using PBKDF2WithHmacSHA256 salted hashing.
 */
@Entity(
    tableName = "admin_auth",
    indices = [Index(value = ["username"], unique = true)]
)
data class AdminAuthEntity(
    @PrimaryKey
    val id: Int = 1, // Only 1 authorized owner account
    val username: String, // Store Owner Email or Username
    val passwordHash: String, // PBKDF2 salted hash (hex)
    val saltHex: String, // 16-byte random salt (hex)
    val ownerFullName: String = "Adelix Sole Store Owner",
    val role: String = "ROLE_OWNER_ADMIN",
    val lastLoginTimestamp: Long? = null,
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)
