package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class OrderStatusStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: ImageVector,
    val label: String
)

fun getOrderStatusStyle(status: String): OrderStatusStyle {
    return when (status.lowercase()) {
        "delivered" -> OrderStatusStyle(
            backgroundColor = Color(0xFF133224),
            textColor = Color(0xFF10B981), // Emerald Green
            borderColor = Color(0xFF225E3F),
            icon = Icons.Default.CheckCircle,
            label = "Delivered"
        )
        "processing" -> OrderStatusStyle(
            backgroundColor = Color(0xFF382C10),
            textColor = Color(0xFFFBBF24), // Vibrant Yellow
            borderColor = Color(0xFF6B5115),
            icon = Icons.Default.Build,
            label = "Processing"
        )
        "pending" -> OrderStatusStyle(
            backgroundColor = Color(0xFF3D2311),
            textColor = Color(0xFFFB923C), // Warm Orange
            borderColor = Color(0xFF7C3913),
            icon = Icons.Default.HourglassTop,
            label = "Pending"
        )
        "confirmed" -> OrderStatusStyle(
            backgroundColor = Color(0xFF14253D),
            textColor = Color(0xFF60A5FA), // Blue
            borderColor = Color(0xFF1E3A8A),
            icon = Icons.Default.ThumbUp,
            label = "Confirmed"
        )
        "shipped" -> OrderStatusStyle(
            backgroundColor = Color(0xFF261B42),
            textColor = Color(0xFFA78BFA), // Purple / Indigo
            borderColor = Color(0xFF5B21B6),
            icon = Icons.Default.LocalShipping,
            label = "Shipped"
        )
        "out for delivery" -> OrderStatusStyle(
            backgroundColor = Color(0xFF112E2E),
            textColor = Color(0xFF2DD4BF), // Cyan / Teal
            borderColor = Color(0xFF0F766E),
            icon = Icons.Default.DeliveryDining,
            label = "Out for Delivery"
        )
        "cancelled" -> OrderStatusStyle(
            backgroundColor = Color(0xFF381316),
            textColor = Color(0xFFF87171), // Red
            borderColor = Color(0xFF7F1D1D),
            icon = Icons.Default.Cancel,
            label = "Cancelled"
        )
        "returned" -> OrderStatusStyle(
            backgroundColor = Color(0xFF381316),
            textColor = Color(0xFFF87171), // Red
            borderColor = Color(0xFF7F1D1D),
            icon = Icons.Default.AssignmentReturn,
            label = "Returned"
        )
        else -> OrderStatusStyle(
            backgroundColor = Color(0xFF1E212B),
            textColor = Color(0xFFD1D5DB),
            borderColor = Color(0xFF374151),
            icon = Icons.Default.Info,
            label = status
        )
    }
}

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val style = getOrderStatusStyle(status)
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = style.backgroundColor,
        border = BorderStroke(1.dp, style.borderColor),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = style.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = style.textColor
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    orders: List<OrderEntity>,
    currency: String,
    onUpdateStatus: (Long, String) -> Unit,
    getOrderItems: suspend (Long) -> List<OrderItemEntity>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var selectedOrderItems by remember { mutableStateOf<List<OrderItemEntity>>(emptyList()) }

    val filteredOrders = remember(orders, selectedStatusFilter) {
        if (selectedStatusFilter == "All") orders
        else orders.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
    }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = { Text("Orders & COD Dispatch (${orders.size})", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CharcoalDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Status Legend & Quick Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS LEGEND:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 9.sp
                    ),
                    color = GoldPrimary
                )
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF10B981)))
                Text("Delivered", fontSize = 10.sp, color = TextSecondary)
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFFBBF24)))
                Text("Processing", fontSize = 10.sp, color = TextSecondary)
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF87171)))
                Text("Cancelled", fontSize = 10.sp, color = TextSecondary)
            }

            // Status Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statuses = listOf("All", "Pending", "Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled", "Returned")
                statuses.forEach { st ->
                    val count = if (st == "All") orders.size else orders.count { it.status.equals(st, ignoreCase = true) }
                    FilterChip(
                        selected = selectedStatusFilter == st,
                        onClick = { selectedStatusFilter = st },
                        label = { Text("$st ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = CharcoalDark,
                            containerColor = CardBackground,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No orders match status \"$selectedStatusFilter\".", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedOrderForDetail = order }
                                .testTag("admin_order_card_${order.id}"),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = order.orderNumber,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        val dateStr = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())
                                            .format(Date(order.createdAtTimestamp))
                                        Text(
                                            text = dateStr,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = TextMuted
                                        )
                                    }

                                    // Color-coded status badge
                                    OrderStatusBadge(status = order.status)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Customer: ${order.customerName} (${order.customerPhone})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Destination: ${order.deliveryAddress}, ${order.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = AccentGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Cash on Delivery",
                                            style = MaterialTheme.typography.labelSmall.copy(color = AccentGreen, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Text(
                                        text = "$currency${String.format("%.2f", order.totalAmount)}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Order Detail & Status Transition Modal
    selectedOrderForDetail?.let { order ->
        LaunchedEffect(order.id) {
            selectedOrderItems = getOrderItems(order.id)
        }

        AlertDialog(
            onDismissRequest = { selectedOrderForDetail = null },
            containerColor = CardBackground,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, color = TextPrimary)
                    OrderStatusBadge(status = order.status)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Customer Information", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    Text("Name: ${order.customerName}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text("Mobile: ${order.customerPhone}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text("WhatsApp: ${order.customerWhatsapp}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text("Address: ${order.deliveryAddress}, ${order.area}, ${order.city}, ${order.province}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                    if (order.orderNotes.isNotBlank()) {
                        Text("Notes: ${order.orderNotes}", style = MaterialTheme.typography.bodySmall, color = AccentAmber)
                    }
                    if (order.customizationNotes.isNotBlank()) {
                        Text("Bespoke Customization: ${order.customizationNotes}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFA5B4FC), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = CardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Ordered Footwear Items", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    selectedOrderItems.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.quantity}x ${item.productName} (Sz ${item.size}, ${item.color})", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            Text("$currency${String.format("%.2f", item.price * item.quantity)}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = CardBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("$currency${String.format("%.2f", order.subtotal)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Charges", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(if (order.deliveryCharges == 0.0) "FREE" else "$currency${String.format("%.2f", order.deliveryCharges)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total COD Amount", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text("$currency${String.format("%.2f", order.totalAmount)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact Customer on WhatsApp
                    OutlinedButton(
                        onClick = {
                            val msg = "Hello ${order.customerName}, this is Adelix Sole regarding your order ${order.orderNumber}. We are updating your status."
                            launchWhatsApp(context, order.customerWhatsapp, msg)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, WhatsAppGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Message Customer on WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Update Order Status:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Status Transition Buttons with corresponding color indicators
                    val nextStatuses = listOf("Pending", "Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled", "Returned")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        nextStatuses.chunked(2).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { st ->
                                    val isCurrent = order.status.equals(st, ignoreCase = true)
                                    val targetStyle = getOrderStatusStyle(st)
                                    Button(
                                        onClick = {
                                            onUpdateStatus(order.id, st)
                                            selectedOrderForDetail = order.copy(status = st)
                                            Toast.makeText(context, "Status set to $st", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCurrent) targetStyle.borderColor else Color(0xFF282B38),
                                            contentColor = if (isCurrent) targetStyle.textColor else TextPrimary
                                        ),
                                        border = if (isCurrent) BorderStroke(1.dp, targetStyle.textColor) else null,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(targetStyle.icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (isCurrent) targetStyle.textColor else TextSecondary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(st, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedOrderForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
