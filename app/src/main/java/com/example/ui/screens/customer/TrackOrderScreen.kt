package com.example.ui.screens.customer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.StoreSettingsEntity

/**
 * Legacy wrapper forwarding to the new high-fidelity [OrderTrackingScreen] with Vertical Stepper.
 */
@Composable
fun TrackOrderScreen(
    trackedOrder: OrderEntity?,
    orderItems: List<OrderItemEntity>,
    errorMessage: String?,
    isLoading: Boolean,
    settings: StoreSettingsEntity,
    onTrackOrder: (orderId: String, phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    OrderTrackingScreen(
        trackedOrder = trackedOrder,
        orderItems = orderItems,
        errorMessage = errorMessage,
        isLoading = isLoading,
        settings = settings,
        onTrackOrder = onTrackOrder,
        modifier = modifier
    )
}
