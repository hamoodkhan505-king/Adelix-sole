package com.example.ui.screens.customer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data model representing a single step in the order tracking lifecycle.
 */
data class TrackingStep(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val activeAdvice: String? = null
)

val ORDER_TRACKING_STEPS = listOf(
    TrackingStep(
        id = "placed",
        title = "Order Placed & Registered",
        description = "Your luxury footwear order has been securely recorded in our atelier ledger.",
        icon = Icons.Default.ReceiptLong
    ),
    TrackingStep(
        id = "confirmed",
        title = "Order Confirmed",
        description = "Our sales concierge verified order specifications and Cash on Delivery eligibility.",
        icon = Icons.Default.CheckCircleOutline
    ),
    TrackingStep(
        id = "processing",
        title = "Artisan Crafting & Processing",
        description = "Leather inspection, hand-buffing, bespoke packaging in satin dust bags and custom shoe box.",
        icon = Icons.Default.Build,
        activeAdvice = "Our master craftsmen are preparing your pair. Each shoe undergoes rigorous inspection before boxing."
    ),
    TrackingStep(
        id = "shipped",
        title = "Dispatched & In Transit",
        description = "Parcel handed over to our express luxury courier network with priority air/surface routing.",
        icon = Icons.Default.LocalShipping,
        activeAdvice = "Your package has departed the central dispatch hub. In transit towards your regional delivery station."
    ),
    TrackingStep(
        id = "out_for_delivery",
        title = "Out for Doorstep Handover",
        description = "Assigned to the local courier specialist for same-day delivery to your doorstep.",
        icon = Icons.Default.DirectionsRun,
        activeAdvice = "Delivery courier is on route today. Please ensure Cash on Delivery (COD) amount is ready."
    ),
    TrackingStep(
        id = "delivered",
        title = "Delivered & Payment Settled",
        description = "Parcel inspected, accepted by recipient, and Cash on Delivery payment finalized.",
        icon = Icons.Default.Verified
    )
)

@Composable
fun OrderTrackingScreen(
    trackedOrder: OrderEntity?,
    orderItems: List<OrderItemEntity>,
    errorMessage: String?,
    isLoading: Boolean,
    settings: StoreSettingsEntity,
    onTrackOrder: (orderId: String, phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var orderNumberInput by remember { mutableStateOf(trackedOrder?.orderNumber ?: "") }
    var phoneInput by remember { mutableStateOf(trackedOrder?.customerPhone ?: "") }

    LaunchedEffect(trackedOrder) {
        if (trackedOrder != null) {
            orderNumberInput = trackedOrder.orderNumber
            phoneInput = trackedOrder.customerPhone
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("order_tracking_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIVE CONCIERGE TRACKING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = GoldPrimary
                )
                Text(
                    text = "Track Footwear Order",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GoldDark.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Follow every stage of your handcrafted footwear from artisan processing to doorstep Cash on Delivery handover.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Order Query Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SEARCH YOUR SHIPMENT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = orderNumberInput,
                    onValueChange = { orderNumberInput = it },
                    label = { Text("Order ID / Number *") },
                    placeholder = { Text("e.g. AS-2026-00001") },
                    leadingIcon = {
                        Icon(Icons.Default.Tag, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (orderNumberInput.isNotEmpty()) {
                            IconButton(onClick = { orderNumberInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_order_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Registered Phone / WhatsApp (Optional)") },
                    placeholder = { Text("e.g. +1 (555) 234-8901") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick sample test tags for instant demo
                Text(
                    text = "Quick Demo Samples:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val demoOrders = listOf("AS-2026-00001", "AS-2026-00002", "AS-2026-00003")
                    demoOrders.forEach { num ->
                        AssistChip(
                            onClick = {
                                orderNumberInput = num
                                onTrackOrder(num, "")
                            },
                            label = { Text(num, fontSize = 11.sp, color = GoldLight) },
                            border = BorderStroke(1.dp, CardBorder),
                            colors = AssistChipDefaults.assistChipColors(containerColor = CharcoalDark),
                            modifier = Modifier.testTag("quick_demo_$num")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (orderNumberInput.isNotBlank()) {
                            onTrackOrder(orderNumberInput.trim(), phoneInput.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_search_track_order"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading && orderNumberInput.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CharcoalDark, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Order Status", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Error Feedback
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF331518)),
                border = BorderStroke(1.dp, Color(0xFF7A2027)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AccentRed, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Order Not Found", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(errorMessage, color = Color(0xFFFCA5A5), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Tracked Order Details & Vertical Stepper
        if (trackedOrder != null) {
            Spacer(modifier = Modifier.height(20.dp))

            // Order Summary Header Card
            OrderSummaryHeaderCard(
                order = trackedOrder,
                currency = settings.currency,
                onCopyOrderNumber = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Order Number", trackedOrder.orderNumber)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Order ID copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ========================================================
            // VERTICAL STEPPER COMPONENT
            // Visualizing status from processing through to delivery
            // ========================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vertical_stepper_container"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PURCHASE PROGRESS TIMELINE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = GoldPrimary
                            )
                            Text(
                                text = "Dispatch & Delivery Journey",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        // Live status indicator badge
                        OrderStatusBadge(status = trackedOrder.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time updates as your artisan footwear transitions through verification, crafting, dispatch, and final Cash on Delivery handover.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = CardBorder)
                    Spacer(modifier = Modifier.height(18.dp))

                    // Handle special statuses like Cancelled or Returned
                    val isSpecialStatus = trackedOrder.status in listOf("Cancelled", "Returned")

                    if (isSpecialStatus) {
                        SpecialStatusAlert(status = trackedOrder.status)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Render Vertical Stepper
                    VerticalStepper(
                        steps = ORDER_TRACKING_STEPS,
                        currentStatus = trackedOrder.status,
                        createdAtTimestamp = trackedOrder.createdAtTimestamp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Package Contents Preview Card
            PackageContentsCard(
                orderItems = orderItems,
                currency = settings.currency,
                subtotal = trackedOrder.subtotal,
                deliveryCharges = trackedOrder.deliveryCharges,
                totalAmount = trackedOrder.totalAmount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Destination Card
            DeliveryDestinationCard(order = trackedOrder)

            Spacer(modifier = Modifier.height(16.dp))

            // Customer Support CTA via WhatsApp
            OutlinedButton(
                onClick = {
                    val message = "Hello Adelix Sole Concierge, I am inquiring about my Order #${trackedOrder.orderNumber} (Status: ${trackedOrder.status}). Could you provide additional delivery details?"
                    launchWhatsApp(context, settings.whatsappNumber, message)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_whatsapp_inquire"),
                border = BorderStroke(1.dp, WhatsAppGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Chat with Atelier Concierge on WhatsApp", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

/**
 * Vertical Stepper Component implementing progressive step visualization
 * from processing to delivery with node states, connecting lines, and step advice.
 */
@Composable
fun VerticalStepper(
    steps: List<TrackingStep>,
    currentStatus: String,
    createdAtTimestamp: Long,
    modifier: Modifier = Modifier
) {
    val activeIndex = remember(currentStatus) {
        when (currentStatus.lowercase()) {
            "pending" -> 0
            "confirmed" -> 1
            "processing" -> 2
            "shipped" -> 3
            "in transit" -> 3
            "out for delivery" -> 4
            "delivered" -> 5
            "cancelled", "returned" -> 1
            else -> 0
        }
    }

    val isCancelledOrReturned = currentStatus in listOf("Cancelled", "Returned")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vertical_stepper_body")
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < activeIndex && !isCancelledOrReturned
            val isCurrent = index == activeIndex && !isCancelledOrReturned
            val isUpcoming = index > activeIndex || isCancelledOrReturned
            val isLast = index == steps.lastIndex

            VerticalStepperStep(
                stepIndex = index + 1,
                step = step,
                isCompleted = isCompleted,
                isCurrent = isCurrent,
                isUpcoming = isUpcoming,
                isLast = isLast,
                createdAtTimestamp = createdAtTimestamp
            )
        }
    }
}

/**
 * Individual step item with node circle, vertical connecting line, and content details.
 */
@Composable
fun VerticalStepperStep(
    stepIndex: Int,
    step: TrackingStep,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isUpcoming: Boolean,
    isLast: Boolean,
    createdAtTimestamp: Long
) {
    // Pulse animation for the active step
    val infiniteTransition = rememberInfiniteTransition(label = "stepper_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stepper_step_$stepIndex")
    ) {
        // Left Column: Node circle + Vertical connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            // Node circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .then(
                        if (isCurrent) Modifier.scale(pulseScale) else Modifier
                    )
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> GoldPrimary
                            isCurrent -> GoldDark.copy(alpha = 0.25f)
                            else -> CardBorder
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = when {
                            isCompleted -> GoldPrimary
                            isCurrent -> GoldPrimary
                            else -> Color(0xFF3A3F50)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = CharcoalDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    isCurrent -> {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = "Current Step",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = "$stepIndex",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Connecting vertical line to next node
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (isCurrent && step.activeAdvice != null) 90.dp else 52.dp)
                        .background(
                            brush = when {
                                isCompleted -> Brush.verticalGradient(
                                    listOf(GoldPrimary, if (isCurrent) GoldPrimary else CardBorder)
                                )
                                else -> Brush.verticalGradient(
                                    listOf(CardBorder, CardBorder)
                                )
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Right Column: Step titles, status pill, advice callout
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = when {
                        isCurrent -> GoldPrimary
                        isCompleted -> TextPrimary
                        else -> TextSecondary
                    }
                )

                // Status chip
                val (chipText, chipColor, chipBg) = when {
                    isCompleted -> Triple("COMPLETED", AccentGreen, Color(0xFF133224))
                    isCurrent -> Triple("IN PROGRESS", GoldPrimary, GoldDark.copy(alpha = 0.2f))
                    else -> Triple("UPCOMING", TextMuted, Color(0xFF222631))
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = chipBg
                ) {
                    Text(
                        text = chipText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = chipColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (isUpcoming) TextMuted else TextSecondary
            )

            // Step timestamp approximation
            val stepDateText = remember(createdAtTimestamp, stepIndex) {
                val stepOffsetMillis = (stepIndex - 1) * 86400000L / 2
                val date = Date(createdAtTimestamp + stepOffsetMillis)
                val format = SimpleDateFormat("MMM d, yyyy · hh:mm a", Locale.getDefault())
                format.format(date)
            }

            if (isCompleted || isCurrent) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isCompleted) "Logged: $stepDateText" else "Current Stage",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isCurrent) GoldDark else TextMuted
                )
            }

            // In-Progress Advice Callout
            if (isCurrent && step.activeAdvice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262014)),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = step.activeAdvice,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = GoldLight
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top card displaying Order ID, Cash on Delivery banner, and copy button.
 */
@Composable
fun OrderSummaryHeaderCard(
    order: OrderEntity,
    currency: String,
    onCopyOrderNumber: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER ID",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = order.orderNumber,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(onClick = onCopyOrderNumber, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                OrderStatusBadge(status = order.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Recipient", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(order.customerName, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Payment Method", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("Cash on Delivery (COD)", fontWeight = FontWeight.Bold, color = AccentGreen)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Due", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("$currency${String.format("%.2f", order.totalAmount)}", fontWeight = FontWeight.Bold, color = GoldPrimary)
                }
            }
        }
    }
}

/**
 * Status badge with distinct colors.
 */
@Composable
fun OrderStatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "delivered" -> Pair(Color(0xFF133224), AccentGreen)
        "out for delivery" -> Pair(Color(0xFF2C2411), Color(0xFFFBBF24))
        "shipped", "in transit" -> Pair(Color(0xFF172944), Color(0xFF60A5FA))
        "processing" -> Pair(Color(0xFF382310), Color(0xFFFB923C))
        "confirmed" -> Pair(Color(0xFF2A2038), Color(0xFFC084FC))
        "cancelled", "returned" -> Pair(Color(0xFF381316), AccentRed)
        else -> Pair(Color(0xFF262A36), TextSecondary)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun SpecialStatusAlert(status: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF331518)),
        border = BorderStroke(1.dp, Color(0xFF7A2027)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = AccentRed)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Order is currently: $status",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Please reach out to our concierge support team for any replacement, cancellation, or refund queries.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFCA5A5)
                )
            }
        }
    }
}

/**
 * Package contents itemized card.
 */
@Composable
fun PackageContentsCard(
    orderItems: List<OrderItemEntity>,
    currency: String,
    subtotal: Double,
    deliveryCharges: Double,
    totalAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "PACKAGE CONTENTS & COD INVOICE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            orderItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Size: ${item.size} · Color: ${item.color} · Qty: ${item.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = "$currency${String.format("%.2f", item.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("$currency${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Luxury Courier Delivery", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    if (deliveryCharges <= 0.0) "Complimentary" else "$currency${String.format("%.2f", deliveryCharges)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (deliveryCharges <= 0.0) AccentGreen else TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Cash on Delivery Amount", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("$currency${String.format("%.2f", totalAmount)}", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
            }
        }
    }
}

/**
 * Delivery address card.
 */
@Composable
fun DeliveryDestinationCard(order: OrderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "DELIVERY DESTINATION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = order.deliveryAddress,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${order.area.ifBlank { "" }} ${order.city}, ${order.province}".trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contact: ${order.customerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    if (order.orderNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Courier Notes: ${order.orderNotes}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = GoldLight
                        )
                    }
                }
            }
        }
    }
}
