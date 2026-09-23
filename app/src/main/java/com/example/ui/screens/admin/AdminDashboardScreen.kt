package com.example.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    orders: List<OrderEntity>,
    products: List<ProductEntity>,
    settings: StoreSettingsEntity,
    customers: List<CustomerEntity> = emptyList(),
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToBrandsCategories: () -> Unit,
    onNavigateToCustomers: () -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onViewOrderDetail: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val totalSales = remember(orders) {
        orders.filter { it.status != "Cancelled" }.sumOf { it.totalAmount }
    }
    val pendingOrders = remember(orders) { orders.count { it.status.equals("Pending", ignoreCase = true) } }
    val processingOrders = remember(orders) { orders.count { it.status.equals("Processing", ignoreCase = true) } }
    val deliveredOrders = remember(orders) { orders.count { it.status.equals("Delivered", ignoreCase = true) } }
    val lowStockCount = remember(products) { products.count { it.stockQuantity in 1..3 } }
    val outOfStockCount = remember(products) { products.count { it.stockQuantity <= 0 } }

    Scaffold(
        containerColor = CharcoalDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Adelix Sole Admin", fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = GoldDark.copy(alpha = 0.3f)) {
                            Text("PORTAL", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldLight))
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = AccentRed)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "STORE PERFORMANCE METRICS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // KPI Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "Total COD Sales",
                    value = "${settings.currency}${String.format("%.0f", totalSales)}",
                    icon = Icons.Default.Payments,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Total Orders",
                    value = "${orders.size}",
                    icon = Icons.Default.ReceiptLong,
                    color = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "Pending / In Atelier",
                    value = "${pendingOrders + processingOrders}",
                    icon = Icons.Default.HourglassTop,
                    color = AccentAmber,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Low Stock Shoes",
                    value = "${lowStockCount + outOfStockCount}",
                    icon = Icons.Default.WarningAmber,
                    color = if (lowStockCount > 0) AccentRed else AccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Navigation Hub
            Text(
                text = "MANAGEMENT MODULES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminModuleTile(
                    title = "Products Management",
                    subtitle = "${products.size} footwear models cataloged · Add, edit, pricing",
                    icon = Icons.Default.ShoppingBag,
                    onClick = onNavigateToProducts,
                    testTag = "admin_nav_products"
                )
                AdminModuleTile(
                    title = "Orders & Dispatch",
                    subtitle = "${orders.size} customer orders · Color-coded badges, COD tracking, status flow",
                    icon = Icons.Default.LocalShipping,
                    onClick = onNavigateToOrders,
                    testTag = "admin_nav_orders"
                )
                AdminModuleTile(
                    title = "Inventory & Stock Control",
                    subtitle = "Monitor shoe units, set low stock alerts, quick stock restock",
                    icon = Icons.Default.Inventory,
                    onClick = onNavigateToInventory,
                    testTag = "admin_nav_inventory"
                )
                AdminModuleTile(
                    title = "Brands & Categories",
                    subtitle = "Manage shoe labels (Nike, Adidas, Puma, Adelix, etc.) and styles",
                    icon = Icons.Default.Style,
                    onClick = onNavigateToBrandsCategories,
                    testTag = "admin_nav_brands"
                )
                AdminModuleTile(
                    title = "Customer Directory",
                    subtitle = "${customers.size} registered buyers · Contact info, spend history, WhatsApp outreach",
                    icon = Icons.Default.People,
                    onClick = onNavigateToCustomers,
                    testTag = "admin_nav_customers"
                )
                AdminModuleTile(
                    title = "Store & Website Settings",
                    subtitle = "Configure WhatsApp number, delivery fees, announcements & policies",
                    icon = Icons.Default.Settings,
                    onClick = onNavigateToSettings,
                    testTag = "admin_nav_settings"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Orders Quick View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ORDERS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldPrimary
                )
                TextButton(onClick = onNavigateToOrders) {
                    Text("View All (${orders.size})", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (orders.isEmpty()) {
                Text("No orders placed yet.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            } else {
                orders.take(4).forEach { ord ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onViewOrderDetail(ord.id) },
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = ord.orderNumber,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${ord.customerName} · ${ord.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${settings.currency}${String.format("%.2f", ord.totalAmount)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                OrderStatusBadge(status = ord.status)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun AdminModuleTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldDark.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
