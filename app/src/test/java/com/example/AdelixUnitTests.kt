package com.example

import androidx.compose.ui.graphics.Color
import com.example.data.entity.BrandEntity
import com.example.data.entity.CustomerEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.model.CartItem
import com.example.ui.screens.admin.getOrderStatusStyle
import org.junit.Assert.*
import org.junit.Test

class AdelixUnitTests {

    @Test
    fun testOrderStatusStyleDeliveredIsGreen() {
        val style = getOrderStatusStyle("Delivered")
        assertEquals("Delivered", style.label)
        assertEquals(Color(0xFF10B981), style.textColor)
        assertEquals(Color(0xFF133224), style.backgroundColor)
    }

    @Test
    fun testOrderStatusStyleProcessingIsYellow() {
        val style = getOrderStatusStyle("Processing")
        assertEquals("Processing", style.label)
        assertEquals(Color(0xFFFBBF24), style.textColor)
        assertEquals(Color(0xFF382C10), style.backgroundColor)
    }

    @Test
    fun testOrderStatusStyleCancelledIsRed() {
        val style = getOrderStatusStyle("Cancelled")
        assertEquals("Cancelled", style.label)
        assertEquals(Color(0xFFF87171), style.textColor)
        assertEquals(Color(0xFF381316), style.backgroundColor)
    }

    @Test
    fun testOrderStatusStylePendingIsOrange() {
        val style = getOrderStatusStyle("Pending")
        assertEquals("Pending", style.label)
        assertEquals(Color(0xFFFB923C), style.textColor)
    }

    @Test
    fun testCartItemTotalPriceCalculation() {
        val shoe = ProductEntity(
            id = 1L,
            name = "Adelix Monarch Oxford",
            brand = "Adelix Signature",
            category = "Formal Dress",
            description = "Hand-burnished leather",
            price = 280.0,
            salePrice = 240.0,
            sizes = "40,41,42,43,44,45",
            colors = "Cognac Brown,Onyx Black",
            stockQuantity = 10,
            sku = "AS-101"
        )

        val cartItem = CartItem(
            product = shoe,
            selectedSize = "42",
            selectedColor = "Cognac Brown",
            quantity = 2
        )

        assertEquals(240.0, cartItem.unitPrice, 0.001)
        assertEquals(480.0, cartItem.totalPrice, 0.001)
    }

    @Test
    fun testCustomerEntityCreationAndFields() {
        val customer = CustomerEntity(
            id = 10L,
            fullName = "Lord Sterling",
            phoneNumber = "+1555123456",
            whatsappNumber = "+1555123456",
            email = "sterling@luxury.com",
            province = "California",
            city = "Beverly Hills",
            area = "Rodeo",
            defaultAddress = "90210 Wilshire Blvd",
            totalOrdersCount = 3,
            totalSpent = 1250.0
        )

        assertEquals("Lord Sterling", customer.fullName)
        assertEquals("+1555123456", customer.phoneNumber)
        assertEquals("Beverly Hills", customer.city)
        assertEquals(3, customer.totalOrdersCount)
        assertEquals(1250.0, customer.totalSpent, 0.001)
    }

    @Test
    fun testBrandEntityCreation() {
        val brand = BrandEntity(
            id = 1L,
            name = "Adelix Signature",
            logoText = "AS",
            isEnabled = true,
            description = "Bespoke handcrafted atelier footwear"
        )

        assertEquals("Adelix Signature", brand.name)
        assertTrue(brand.isEnabled)
    }

    @Test
    fun testOrderEntityFormatAndTotal() {
        val order = OrderEntity(
            id = 1L,
            orderNumber = "AS-2026-00042",
            customerName = "James Bond",
            customerPhone = "+442079460912",
            customerWhatsapp = "+442079460912",
            province = "London",
            city = "Westminster",
            area = "Mayfair",
            deliveryAddress = "MI6 HQ",
            subtotal = 500.0,
            deliveryCharges = 0.0,
            totalAmount = 500.0,
            status = "Processing"
        )

        assertEquals("AS-2026-00042", order.orderNumber)
        assertEquals(500.0, order.totalAmount, 0.001)
        assertEquals("Processing", order.status)
    }

    @Test
    fun testOrderTrackingStepsSequence() {
        val steps = com.example.ui.screens.customer.ORDER_TRACKING_STEPS
        assertEquals(6, steps.size)
        assertEquals("placed", steps[0].id)
        assertEquals("confirmed", steps[1].id)
        assertEquals("processing", steps[2].id)
        assertEquals("shipped", steps[3].id)
        assertEquals("out_for_delivery", steps[4].id)
        assertEquals("delivered", steps[5].id)

        // Verify titles are present and descriptive
        assertTrue(steps[2].title.contains("Processing") || steps[2].title.contains("Crafting"))
        assertTrue(steps[5].title.contains("Delivered"))
        assertNotNull(steps[2].activeAdvice)
    }

    @Test
    fun testStoreOwnerContactConstants() {
        assertEquals("+923187174601", com.example.util.OrderNotificationManager.STORE_OWNER_WHATSAPP)
        assertEquals("ah6202429@gmail.com", com.example.util.OrderNotificationManager.STORE_OWNER_EMAIL)
    }

    @Test
    fun testWhatsAppMessageFormattingContainsAllRequiredFields() {
        val order = OrderEntity(
            id = 99L,
            orderNumber = "AS-2026-99999",
            customerName = "Ali Khan",
            customerPhone = "+923001234567",
            customerWhatsapp = "+923007654321",
            province = "Punjab",
            city = "Lahore",
            area = "Gulberg III",
            deliveryAddress = "House 12, Street 4, Block B",
            subtotal = 350.0,
            deliveryCharges = 15.0,
            totalAmount = 365.0,
            orderNotes = "Please call before arriving",
            customizationNotes = "Engrave initials 'AK' on inner heel",
            status = "Pending",
            paymentMethod = "Cash on Delivery",
            createdAtTimestamp = 1774350000000L
        )

        val item = com.example.data.entity.OrderItemEntity(
            id = 1L,
            orderId = 99L,
            productId = 5L,
            productName = "Adelix Imperial Monkstrap",
            brand = "Adelix Signature",
            price = 350.0,
            size = "43",
            color = "Midnight Black",
            quantity = 1,
            imageUrl = ""
        )

        val message = com.example.util.OrderNotificationManager.buildWhatsAppOrderMessage(
            order = order,
            items = listOf(item),
            currency = "$"
        )

        // Verify all 19 required data points appear in WhatsApp message
        assertTrue(message.contains("AS-2026-99999")) // Order ID
        assertTrue(message.contains("Ali Khan")) // Full name
        assertTrue(message.contains("+923001234567")) // Phone number
        assertTrue(message.contains("+923007654321")) // WhatsApp number
        assertTrue(message.contains("Punjab")) // Province
        assertTrue(message.contains("Lahore")) // City
        assertTrue(message.contains("Gulberg III")) // Area
        assertTrue(message.contains("House 12, Street 4, Block B")) // Complete address
        assertTrue(message.contains("Adelix Imperial Monkstrap")) // Product name
        assertTrue(message.contains("Adelix Signature")) // Brand
        assertTrue(message.contains("43")) // Size
        assertTrue(message.contains("Midnight Black")) // Color
        assertTrue(message.contains("1")) // Quantity
        assertTrue(message.contains("350.00")) // Product price
        assertTrue(message.contains("Engrave initials 'AK' on inner heel")) // Customization
        assertTrue(message.contains("15.00")) // Delivery charges
        assertTrue(message.contains("365.00")) // Total order amount
        assertTrue(message.contains("Cash on Delivery")) // Payment method
        assertTrue(message.contains("Please call before arriving")) // Customer notes
    }

    @Test
    fun testEmailSubjectAndBodyFormatting() {
        val order = OrderEntity(
            id = 88L,
            orderNumber = "AS-2026-88888",
            customerName = "Sara Ahmed",
            customerPhone = "+923219876543",
            customerWhatsapp = "+923219876543",
            province = "Sindh",
            city = "Karachi",
            area = "Clifton",
            deliveryAddress = "Sea Breeze Apts, 5th Floor",
            subtotal = 200.0,
            deliveryCharges = 0.0,
            totalAmount = 200.0,
            status = "Confirmed",
            createdAtTimestamp = 1774350000000L
        )

        val subject = com.example.util.OrderNotificationManager.buildEmailOrderSubject(order)
        assertEquals("New Adelix Sole Order — [AS-2026-88888]", subject)

        val body = com.example.util.OrderNotificationManager.buildEmailOrderBody(order, emptyList(), "$")
        assertTrue(body.contains("AS-2026-88888"))
        assertTrue(body.contains("Sara Ahmed"))
        assertTrue(body.contains("Cash on Delivery (COD ONLY)"))
        assertTrue(body.contains("Karachi"))
    }
}
