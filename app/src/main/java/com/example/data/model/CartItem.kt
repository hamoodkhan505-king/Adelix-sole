package com.example.data.model

import com.example.data.entity.ProductEntity

data class CartItem(
    val product: ProductEntity,
    val selectedSize: String,
    val selectedColor: String,
    var quantity: Int,
    val customizationText: String = "",
    val customizationNotes: String = ""
) {
    val unitPrice: Double
        get() = product.salePrice ?: product.price

    val totalPrice: Double
        get() = unitPrice * quantity

    val itemKey: String
        get() = "${product.id}_${selectedSize}_${selectedColor}_${customizationText}"
}
