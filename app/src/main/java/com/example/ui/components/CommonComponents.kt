package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ProductEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.theme.*

fun launchWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp: $phoneNumber", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun AnnouncementBar(text: String) {
    if (text.isNotBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8C6D23), GoldPrimary, Color(0xFF8C6D23))
                    )
                )
                .padding(vertical = 6.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CharcoalDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdelixTopBar(
    title: String = "Adelix Sole",
    subtitle: String? = null,
    cartItemCount: Int = 0,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onSearchClick: (() -> Unit)? = null,
    onAdminClick: (() -> Unit)? = null
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CharcoalDark,
            titleContentColor = TextPrimary
        ),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GoldDark.copy(alpha = 0.3f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SOLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.5.sp
                            ),
                            color = GoldLight
                        )
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = {
            if (onSearchClick != null) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Shoes",
                        tint = TextPrimary
                    )
                }
            }

            // Cart with badge
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_cart_button")
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = GoldPrimary,
                                contentColor = CharcoalDark
                            ) {
                                Text(
                                    text = "$cartItemCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = TextPrimary
                    )
                }
            }

            // Admin Portal entrance
            if (onAdminClick != null) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.testTag("top_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Admin Portal",
                        tint = GoldPrimary
                    )
                }
            }
        }
    )
}

@Composable
fun AdelixBottomNavBar(
    currentRoute: String,
    cartItemCount: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToBrands: () -> Unit,
    onNavigateToTrack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    NavigationBar(
        containerColor = CharcoalDark,
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            modifier = Modifier.testTag("nav_home"),
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CharcoalDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "shop" || currentRoute == "new_arrivals" || currentRoute == "sale",
            onClick = onNavigateToShop,
            modifier = Modifier.testTag("nav_shop"),
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Shop") },
            label = { Text("Shop", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CharcoalDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "brands",
            onClick = onNavigateToBrands,
            modifier = Modifier.testTag("nav_brands"),
            icon = { Icon(Icons.Default.Style, contentDescription = "Brands") },
            label = { Text("Brands", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CharcoalDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "track_order",
            onClick = onNavigateToTrack,
            modifier = Modifier.testTag("nav_track"),
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Track") },
            label = { Text("Track", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CharcoalDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(
            selected = currentRoute == "cart" || currentRoute == "checkout",
            onClick = onNavigateToCart,
            modifier = Modifier.testTag("nav_cart"),
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = GoldPrimary,
                                contentColor = CharcoalDark
                            ) {
                                Text("$cartItemCount", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = "Cart")
                }
            },
            label = { Text("Cart", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CharcoalDark,
                selectedTextColor = GoldPrimary,
                indicatorColor = GoldPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
    }
}

@Composable
fun ShoeCard(
    product: ProductEntity,
    currency: String = "$",
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onProductClick(product.id) }
            .testTag("shoe_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Shoe Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFF14161C)),
                contentAlignment = Alignment.Center
            ) {
                // If product has image
                val context = LocalContext.current
                val resId = remember(product.imageUrl) {
                    when (product.imageUrl) {
                        "hero_banner" -> context.resources.getIdentifier("hero_banner", "drawable", context.packageName)
                        "ic_adelix_logo" -> context.resources.getIdentifier("ic_adelix_logo", "drawable", context.packageName)
                        else -> context.resources.getIdentifier("ic_adelix_logo", "drawable", context.packageName)
                    }
                }

                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RollerSkating,
                        contentDescription = product.name,
                        modifier = Modifier.size(64.dp),
                        tint = GoldPrimary.copy(alpha = 0.6f)
                    )
                }

                // Badges in top left
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.isSale && product.discountPercent > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AccentRed
                        ) {
                            Text(
                                text = "-${product.discountPercent}%",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            )
                        }
                    } else if (product.isNewArrival) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary
                        ) {
                            Text(
                                text = "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = CharcoalDark
                                )
                            )
                        }
                    }

                    if (product.isCustomizable) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF6366F1)
                        ) {
                            Text(
                                text = "BESPOKE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Stock indicator in top right
                if (product.stockQuantity <= 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "SOLD OUT",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = AccentRed
                            )
                        )
                    }
                }
            }

            // Product Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    ),
                    color = GoldPrimary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val finalPrice = product.salePrice ?: product.price
                    Text(
                        text = "$currency${String.format("%.0f", finalPrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = GoldPrimary
                    )

                    if (product.salePrice != null && product.salePrice < product.price) {
                        Text(
                            text = "$currency${String.format("%.0f", product.price)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 12.sp
                            ),
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Available sizes preview
                val sizesList = product.sizes.split(",").take(4).joinToString(" · ") { it.trim() }
                Text(
                    text = "Sizes: $sizesList",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun FloatingWhatsAppButton(
    phoneNumber: String,
    message: String = "Hello Adelix Sole, I need help with my order.",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = { launchWhatsApp(context, phoneNumber, message) },
        modifier = modifier
            .testTag("floating_whatsapp_button")
            .size(56.dp),
        containerColor = WhatsAppGreen,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "WhatsApp Order Support",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun FooterSection(
    settings: StoreSettingsEntity,
    onNavigateToShop: () -> Unit,
    onNavigateToTrack: () -> Unit,
    onNavigateToContact: () -> Unit,
    onAdminLoginClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0B0E))
            .border(BorderStroke(1.dp, CardBorder))
            .padding(24.dp)
    ) {
        // Logo & Tagline
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = settings.storeName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GoldDark.copy(alpha = 0.4f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "LUXURY FOOTWEAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = GoldLight
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = settings.footerText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = CardBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Perks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PerkItem(icon = Icons.Default.Verified, title = "100% Authentic", desc = "Handcrafted precision")
            PerkItem(icon = Icons.Default.LocalShipping, title = "COD Available", desc = "Pay at doorstep")
            PerkItem(icon = Icons.Default.SupportAgent, title = "Live Support", desc = "Direct WhatsApp")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = CardBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Quick Links
        Text(
            text = "QUICK NAVIGATION",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = GoldPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onNavigateToShop) {
                Text("All Shoes", color = TextPrimary)
            }
            TextButton(onClick = onNavigateToTrack) {
                Text("Track Order", color = TextPrimary)
            }
            TextButton(onClick = onNavigateToContact) {
                Text("Contact Us", color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contact info
        Text(
            text = "Address: ${settings.address}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text = "WhatsApp: ${settings.whatsappNumber} | Email: ${settings.email}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Copyright and Admin portal entrance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "© 2026 ${settings.storeName}. All rights reserved.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted
            )

            TextButton(
                onClick = onAdminLoginClick,
                modifier = Modifier.testTag("footer_admin_login_link")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Admin Portal",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = GoldPrimary
                )
            }
        }
    }
}

@Composable
private fun PerkItem(icon: ImageVector, title: String, desc: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = TextSecondary
        )
    }
}

/**
 * High-profile, professional floating WhatsApp button displayed across customer screens.
 * Opens WhatsApp chat with the store owner (+923187174601) with pre-filled message:
 * "Assalam o Alaikum Adelix Sole, I need help regarding your products/order."
 */
@Composable
fun WhatsAppFloatingButton(
    whatsappNumber: String = "+923187174601",
    message: String = "Assalam o Alaikum Adelix Sole, I need help regarding your products/order.",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            com.example.util.OrderNotificationManager.openCustomerSupportWhatsApp(
                context = context,
                ownerPhone = whatsappNumber,
                message = message
            )
        },
        containerColor = WhatsAppGreen,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = modifier
            .size(56.dp)
            .testTag("floating_whatsapp_btn")
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "WhatsApp Concierge Support",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
