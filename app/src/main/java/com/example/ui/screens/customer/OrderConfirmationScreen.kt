package com.example.ui.screens.customer

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.theme.*
import com.example.util.OrderNotificationManager

@Composable
fun OrderConfirmationScreen(
    order: OrderEntity?,
    items: List<OrderItemEntity> = emptyList(),
    settings: StoreSettingsEntity,
    onTrackOrder: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    val context = LocalContext.current

    if (order == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CharcoalDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GoldPrimary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("order_confirmation_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Success Icon Graphic
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GoldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CharcoalDark,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "Order Confirmed"
        Text(
            text = "Order Confirmed",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("order_confirmed_title")
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your luxury footwear order has been successfully placed in our system.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Notifications Dispatch Summary Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
            border = BorderStroke(1.dp, Color(0xFF225E3F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Store Notifications Dispatched",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Store Owner WhatsApp: +923187174601\n• Store Owner Email: ah6202429@gmail.com",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFFA7F3D0)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Details Card
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
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = GoldPrimary
                        )
                        Text(
                            text = order.orderNumber,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.testTag("confirmed_order_id")
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1B3828)
                    ) {
                        Text(
                            text = "COD CONFIRMED",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                DetailRow("Customer Name:", order.customerName)
                DetailRow("Phone Number:", order.customerPhone)
                if (order.customerWhatsapp.isNotBlank()) {
                    DetailRow("WhatsApp Number:", order.customerWhatsapp)
                }
                DetailRow("Destination:", "${order.deliveryAddress}, ${order.area}, ${order.city}, ${order.province}")
                DetailRow("Payment Method:", "Cash on Delivery (COD ONLY)")

                if (order.customizationNotes.isNotBlank()) {
                    DetailRow("Customization:", order.customizationNotes)
                }
                if (order.orderNotes.isNotBlank()) {
                    DetailRow("Special Notes:", order.orderNotes)
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Order Total (COD):",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        "${settings.currency}${String.format("%.2f", order.totalAmount)}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary,
                        modifier = Modifier.testTag("confirmed_order_total")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expected Delivery Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Expected Delivery Information",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Estimated delivery time: 2 to 4 business days nationwide.\n• Courier will contact you at ${order.customerPhone} prior to delivery.\n• Payment: Please have exact cash of ${settings.currency}${String.format("%.2f", order.totalAmount)} ready for the delivery rider upon arrival.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Order Tracking Button
        Button(
            onClick = { onTrackOrder(order.orderNumber) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_track_this_order"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Order Progress", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // WhatsApp Support Button (+923187174601)
        Button(
            onClick = {
                OrderNotificationManager.openCustomerSupportWhatsApp(
                    context = context,
                    ownerPhone = OrderNotificationManager.STORE_OWNER_WHATSAPP,
                    message = "Assalam o Alaikum Adelix Sole, I need help regarding my order #${order.orderNumber}."
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_whatsapp_support"),
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("WhatsApp Support (+923187174601)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Resend/View WhatsApp Order Notification to Owner Button
        OutlinedButton(
            onClick = {
                OrderNotificationManager.openWhatsAppOrderNotification(
                    context = context,
                    order = order,
                    items = items,
                    currency = settings.currency,
                    ownerPhone = OrderNotificationManager.STORE_OWNER_WHATSAPP
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_resend_whatsapp_order"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
            border = BorderStroke(1.dp, GoldPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = GoldPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send/Review WhatsApp Order Message", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onContinueShopping,
            modifier = Modifier.testTag("btn_continue_shopping")
        ) {
            Text("Continue Shopping", color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false).padding(start = 16.dp)
        )
    }
}
