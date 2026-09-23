package com.example.data.db

import com.example.data.entity.*

object DatabaseInitializer {

    suspend fun populateInitialData(database: AppDatabase) {
        val brandDao = database.brandDao()
        val categoryDao = database.categoryDao()
        val productDao = database.productDao()
        val customerDao = database.customerDao()
        val storeSettingsDao = database.storeSettingsDao()
        val orderDao = database.orderDao()
        val reviewDao = database.reviewDao()

        // 1. Settings
        storeSettingsDao.insertOrUpdate(
            StoreSettingsEntity(
                id = 1,
                storeName = "Adelix Sole",
                whatsappNumber = "+923187174601",
                phoneNumber = "+923187174601",
                email = "ah6202429@gmail.com",
                address = "Adelix Sole Flagship Studio, Downtown Galleria, Suite 400",
                deliveryCharges = 15.0,
                freeDeliveryThreshold = 150.0,
                currency = "$",
                codEnabled = true,
                announcementText = "Complimentary nationwide COD shipping on orders over $150 | Exclusive Autumn Edition",
                footerText = "Adelix Sole — Premium Footwear. Built for Elegance, Handcrafted for Distinction.",
                heroBannerTitle = "The Art of Modern Footwear",
                heroBannerSubtitle = "Discover hand-stitched silhouettes, world-class leathers, and precision cushioning."
            )
        )

        // 2. Brands
        val initialBrands = listOf(
            BrandEntity(name = "Adelix Signature", logoText = "AS", isEnabled = true, description = "Our in-house master artisans' bespoke luxury creations."),
            BrandEntity(name = "Nike", logoText = "NIKE", isEnabled = true, description = "Global leader in iconic lifestyle and athletic innovation."),
            BrandEntity(name = "Adidas", logoText = "ADIDAS", isEnabled = true, description = "Timeless European craftsmanship fused with urban edge."),
            BrandEntity(name = "Puma", logoText = "PUMA", isEnabled = true, description = "Fast, sleek lifestyle footwear tailored for modern streets."),
            BrandEntity(name = "New Balance", logoText = "NB", isEnabled = true, description = "Heritage premium runners built with unmatched comfort."),
            BrandEntity(name = "HR", logoText = "HR", isEnabled = true, description = "Refined formal classics and Goodyear-welted oxfords.")
        )
        brandDao.insertAllBrands(initialBrands)

        // 3. Categories
        val initialCategories = listOf(
            CategoryEntity(name = "Luxury Sneakers"),
            CategoryEntity(name = "Formal Oxford"),
            CategoryEntity(name = "Chelsea Boots"),
            CategoryEntity(name = "Performance Running"),
            CategoryEntity(name = "Casual Loafers")
        )
        categoryDao.insertAllCategories(initialCategories)

        // 4. Products
        val initialProducts = listOf(
            ProductEntity(
                name = "Adelix Monarch Gold Sneaker",
                brand = "Adelix Signature",
                category = "Luxury Sneakers",
                description = "Handcrafted in calfskin leather with polished 24K electroplated gold heel accents. Featuring memory-foam orthotic insoles and durable vulcanized Italian rubber outsoles.",
                price = 280.0,
                salePrice = 238.0,
                discountPercent = 15,
                sizes = "40,41,42,43,44,45",
                colors = "Onyx Black,Pure Snow,Cognac Brown",
                stockQuantity = 18,
                sku = "AS-MNK-001",
                isNewArrival = true,
                isFeatured = true,
                isBestSeller = true,
                isSale = true,
                isCustomizable = true,
                customizationTimeDays = "3–5 Working Days",
                status = "Active",
                imageUrl = "ic_adelix_logo",
                specifications = "Upper: Full-Grain Italian Calfskin | Lining: Breathable Nappa Leather | Sole: Vulcanized Rubber | Closure: Waxed Cotton Laces"
            ),
            ProductEntity(
                name = "Nike Air Pegasus Lux Runner",
                brand = "Nike",
                category = "Performance Running",
                description = "Engineered mesh combined with brushed suede overlays. Responsive Zoom Air cushioning provides featherweight responsiveness on pavement or trail.",
                price = 195.0,
                salePrice = null,
                discountPercent = 0,
                sizes = "40,41,42,43,44",
                colors = "Phantom Grey,Midnight Navy,Triple White",
                stockQuantity = 24,
                sku = "NK-PEG-204",
                isNewArrival = true,
                isFeatured = true,
                isBestSeller = true,
                isSale = false,
                isCustomizable = false,
                status = "Active",
                imageUrl = "hero_banner",
                specifications = "Upper: Breathable Engineered Mesh | Sole: Zoom Air + High-abrasion Rubber | Weight: 285g | Arch: Neutral"
            ),
            ProductEntity(
                name = "Adidas UltraBoost Heritage 90",
                brand = "Adidas",
                category = "Luxury Sneakers",
                description = "Revolutionary Boost foam midsole meets Primeknit woven textile. Seamless sock-like fit with torsion spring stabilization for all-day luxury wear.",
                price = 220.0,
                salePrice = 176.0,
                discountPercent = 20,
                sizes = "41,42,43,44,45",
                colors = "Core Black,Chalk White,Solar Red",
                stockQuantity = 14,
                sku = "AD-UB-901",
                isNewArrival = false,
                isFeatured = false,
                isBestSeller = true,
                isSale = true,
                isCustomizable = false,
                status = "Active",
                imageUrl = "ic_adelix_logo",
                specifications = "Upper: Primeknit Recycled Textile | Midsole: Full-length BOOST | Outsole: Continental™ Rubber Traction"
            ),
            ProductEntity(
                name = "HR Royale Wholecut Oxford",
                brand = "HR",
                category = "Formal Oxford",
                description = "Cut from a single seamless piece of vegetable-tanned French calf leather. Hand-burnished toe medallion, closed channel Goodyear-welted leather sole.",
                price = 340.0,
                salePrice = null,
                discountPercent = 0,
                sizes = "40,41,42,43,44",
                colors = "Dark Walnut,Espresso,Jet Black",
                stockQuantity = 8,
                sku = "HR-OX-771",
                isNewArrival = true,
                isFeatured = true,
                isBestSeller = false,
                isSale = false,
                isCustomizable = true,
                customizationTimeDays = "5–7 Working Days",
                status = "Active",
                imageUrl = "ic_adelix_logo",
                specifications = "Construction: Goodyear Welted | Leather: French Box Calf | Sole: Oak Bark Tanned Leather | Heel: Dovetail Rubber Insert"
            ),
            ProductEntity(
                name = "New Balance 990v6 Regal Grey",
                brand = "New Balance",
                category = "Luxury Sneakers",
                description = "The pinnacle of dad-shoe chic perfected. Premium pigskin suede, FuelCell high-rebound cushioning, and iconic reflective 3M branding accents.",
                price = 260.0,
                salePrice = null,
                discountPercent = 0,
                sizes = "40,41,42,43,44,45",
                colors = "Heritage Grey,Castlerock,Navy",
                stockQuantity = 12,
                sku = "NB-990-V6",
                isNewArrival = false,
                isFeatured = true,
                isBestSeller = true,
                isSale = false,
                isCustomizable = false,
                status = "Active",
                imageUrl = "hero_banner",
                specifications = "Upper: Pigskin Suede & Synthetic Mesh | Midsole: FuelCell Foam + ENCAP | Made In: USA Craftsmanship"
            ),
            ProductEntity(
                name = "Puma Palermo Sovereign Loafer",
                brand = "Puma",
                category = "Casual Loafers",
                description = "80s terrace icon reinvented in buttery soft Italian suede. Features retro gum sole, gold foil lettering, and distinctive T-toe construction.",
                price = 140.0,
                salePrice = 112.0,
                discountPercent = 20,
                sizes = "41,42,43,44",
                colors = "Forest Green,Cream Tan,Cobalt Blue",
                stockQuantity = 5,
                sku = "PM-PLM-402",
                isNewArrival = false,
                isFeatured = false,
                isBestSeller = false,
                isSale = true,
                isCustomizable = false,
                status = "Active",
                imageUrl = "ic_adelix_logo",
                specifications = "Upper: Velour Suede | Outsole: Textured Gum Rubber | Insole: Cushioned Textile"
            ),
            ProductEntity(
                name = "Adelix Kensington Chelsea Boot",
                brand = "Adelix Signature",
                category = "Chelsea Boots",
                description = "Sleek tapered silhouette sculpted from water-resistant waxed suede. Twin elasticated side gussets and woven pull tabs for effortless slip-on elegance.",
                price = 310.0,
                salePrice = null,
                discountPercent = 0,
                sizes = "41,42,43,44,45",
                colors = "Tobacco Suede,Charcoal Nubuck,Rich Mahogany",
                stockQuantity = 10,
                sku = "AS-KNS-889",
                isNewArrival = true,
                isFeatured = true,
                isBestSeller = false,
                isSale = false,
                isCustomizable = true,
                customizationTimeDays = "4–6 Working Days",
                status = "Active",
                imageUrl = "hero_banner",
                specifications = "Upper: Waxed Hydrophobic Suede | Lining: Calf Leather | Sole: Commando Lugged Rubber | Shaft Height: 6.5 inches"
            )
        )
        productDao.insertAllProducts(initialProducts)

        // 5. Initial Sample Order so user can immediately test tracking!
        val sampleOrderId = orderDao.insertOrder(
            OrderEntity(
                orderNumber = "AS-2026-00001",
                customerName = "David Sterling",
                customerPhone = "+1 (555) 234-8901",
                customerWhatsapp = "+1 (555) 234-8901",
                province = "California",
                city = "San Francisco",
                area = "Marina District",
                deliveryAddress = "742 Evergreen Terrace, Apt 4B",
                orderNotes = "Please ring doorbell twice upon arrival.",
                customizationNotes = "Emboss initials 'D.S.' on left heel.",
                subtotal = 238.0,
                deliveryCharges = 0.0,
                totalAmount = 238.0,
                paymentMethod = "Cash on Delivery",
                status = "Shipped",
                createdAtTimestamp = System.currentTimeMillis() - 86400000L * 2
            )
        )

        orderDao.insertOrderItems(
            listOf(
                OrderItemEntity(
                    orderId = sampleOrderId,
                    productId = 1,
                    productName = "Adelix Monarch Gold Sneaker",
                    brand = "Adelix Signature",
                    price = 238.0,
                    size = "43",
                    color = "Onyx Black",
                    quantity = 1,
                    imageUrl = "ic_adelix_logo"
                )
            )
        )

        // 6. Reviews
        reviewDao.insertAllReviews(
            listOf(
                ReviewEntity(
                    productId = 1,
                    customerName = "Alexander Vance",
                    rating = 5,
                    reviewText = "Hands down the best shoe craftsmanship I've worn in years. The gold heel badge is subtle and gorgeous. Packaging was top tier!",
                    isApproved = true
                ),
                ReviewEntity(
                    productId = 1,
                    customerName = "Marcus Bennett",
                    rating = 5,
                    reviewText = "Exceptional fit. The customized embossed initials on the heel made it feel like a bespoke $800 bespoke shoe.",
                    isApproved = true
                ),
                ReviewEntity(
                    productId = 2,
                    customerName = "Ryan Cole",
                    rating = 5,
                    reviewText = "Feather-light cushioning for my daily commutes. Adelix delivered via COD promptly without any hassle.",
                    isApproved = true
                )
            )
        )
        // 7. Customers
        val initialCustomers = listOf(
            CustomerEntity(
                fullName = "David Sterling",
                phoneNumber = "+1 (555) 234-8901",
                whatsappNumber = "+1 (555) 234-8901",
                email = "david.sterling@example.com",
                province = "California",
                city = "San Francisco",
                area = "Marina District",
                defaultAddress = "742 Evergreen Terrace, Apt 4B",
                totalOrdersCount = 1,
                totalSpent = 238.0,
                lastOrderTimestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            CustomerEntity(
                fullName = "Alexander Vance",
                phoneNumber = "+1 (555) 789-1234",
                whatsappNumber = "+1 (555) 789-1234",
                email = "alexander.vance@example.com",
                province = "New York",
                city = "New York City",
                area = "Manhattan",
                defaultAddress = "350 5th Avenue, Suite 1200",
                totalOrdersCount = 2,
                totalSpent = 540.0,
                lastOrderTimestamp = System.currentTimeMillis() - 86400000L * 5
            ),
            CustomerEntity(
                fullName = "Sophia Laurent",
                phoneNumber = "+1 (555) 456-7890",
                whatsappNumber = "+1 (555) 456-7890",
                email = "sophia.laurent@example.com",
                province = "Illinois",
                city = "Chicago",
                area = "Lincoln Park",
                defaultAddress = "1200 N Lake Shore Dr",
                totalOrdersCount = 1,
                totalSpent = 310.0,
                lastOrderTimestamp = System.currentTimeMillis() - 86400000L * 7
            )
        )
        customerDao.insertAllCustomers(initialCustomers)

        // 8. Single Authorized Website Owner Account (PBKDF2 Salted Hash)
        val adminSecurityService = com.example.data.security.AdminSecurityService(database.adminAuthDao())
        adminSecurityService.ensureOwnerAccountInitialized()
    }
}
