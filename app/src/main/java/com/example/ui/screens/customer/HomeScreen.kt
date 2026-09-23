package com.example.ui.screens.customer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BrandEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.ReviewEntity
import com.example.data.entity.StoreSettingsEntity
import com.example.ui.components.FooterSection
import com.example.ui.components.ShoeCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    settings: StoreSettingsEntity,
    products: List<ProductEntity>,
    brands: List<BrandEntity>,
    reviews: List<ReviewEntity>,
    onNavigateToShop: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToBrand: (String) -> Unit,
    onNavigateToNewArrivals: () -> Unit,
    onNavigateToSale: () -> Unit,
    onNavigateToTrack: () -> Unit,
    onNavigateToContact: () -> Unit,
    onAdminLoginClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val featuredList = remember(products) { products.filter { it.isFeatured } }
    val newArrivalsList = remember(products) { products.filter { it.isNewArrival } }
    val bestSellersList = remember(products) { products.filter { it.isBestSeller } }
    val saleList = remember(products) { products.filter { it.isSale || (it.salePrice != null && it.salePrice < it.price) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(CharcoalDark)
    ) {
        // 1. Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val heroResId = remember {
                context.resources.getIdentifier("hero_banner", "drawable", context.packageName)
            }
            if (heroResId != 0) {
                Image(
                    painter = painterResource(id = heroResId),
                    contentDescription = "Adelix Sole Showcase",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                CharcoalDark.copy(alpha = 0.6f),
                                CharcoalDark
                            )
                        )
                    )
            )

            // Content on top of banner
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GoldPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "NEW 2026 COLLECTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        ),
                        color = CharcoalDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings.heroBannerTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = settings.heroBannerSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToShop,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = CharcoalDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("hero_shop_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shop Now",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Quick Category / Brand Quick Pills
        if (brands.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "EXPLORE BY BRAND",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(brands) { brand ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardBackground,
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier
                                .clickable { onNavigateToBrand(brand.name) }
                                .testTag("brand_chip_${brand.name}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = brand.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Featured Shoes
        if (featuredList.isNotEmpty()) {
            HomeSection(
                title = "Featured Shoes",
                subtitle = "Handcrafted silhouettes chosen for distinction",
                onViewAll = onNavigateToShop
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(featuredList) { product ->
                        ShoeCard(
                            product = product,
                            currency = settings.currency,
                            onProductClick = onNavigateToProduct,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. New Arrivals
        if (newArrivalsList.isNotEmpty()) {
            HomeSection(
                title = "New Arrivals",
                subtitle = "The latest drops and contemporary releases",
                onViewAll = onNavigateToNewArrivals
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(newArrivalsList) { product ->
                        ShoeCard(
                            product = product,
                            currency = settings.currency,
                            onProductClick = onNavigateToProduct,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Best Sellers
        if (bestSellersList.isNotEmpty()) {
            HomeSection(
                title = "Best Sellers",
                subtitle = "Most demanded footwear by our discerning patrons",
                onViewAll = onNavigateToShop
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(bestSellersList) { product ->
                        ShoeCard(
                            product = product,
                            currency = settings.currency,
                            onProductClick = onNavigateToProduct,
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. Sale & Discount Section
        if (saleList.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1718)),
                border = BorderStroke(1.dp, Color(0xFF4A2024))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = AccentRed)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Special Offers & Sale",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Up to 25% discount on curated designs",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        TextButton(onClick = onNavigateToSale) {
                            Text("View All", color = AccentRed, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(saleList) { product ->
                            ShoeCard(
                                product = product,
                                currency = settings.currency,
                                onProductClick = onNavigateToProduct,
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // 7. Why Choose Adelix Sole
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "WHY CHOOSE ADELIX SOLE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "The Adelix Standard of Luxury",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            ValuePropCard(
                icon = Icons.Default.Diamond,
                title = "Artisan Mastercraft",
                desc = "Every shoe is hand-cut and assembled with full-grain leathers, precision welted stitching, and memory cushioning."
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValuePropCard(
                icon = Icons.Default.Payments,
                title = "100% Cash on Delivery",
                desc = "Inspect and try your shoes at your doorstep before paying. Complete convenience and trust guaranteed."
            )
            Spacer(modifier = Modifier.height(8.dp))
            ValuePropCard(
                icon = Icons.Default.Tune,
                title = "Bespoke Customization",
                desc = "Personalize colors, leather types, or add embossed initials on eligible shoes made just for you."
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 8. Customer Reviews
        if (reviews.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "CUSTOMER TESTIMONIALS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Loved by Footwear Aficionados",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier
                                .width(260.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(review.rating) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${review.reviewText}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = TextPrimary,
                                    maxLines = 4
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = review.customerName,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        // 9. Footer
        FooterSection(
            settings = settings,
            onNavigateToShop = onNavigateToShop,
            onNavigateToTrack = onNavigateToTrack,
            onNavigateToContact = onNavigateToContact,
            onAdminLoginClick = onAdminLoginClick
        )
    }
}

@Composable
private fun HomeSection(
    title: String,
    subtitle: String,
    onViewAll: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            TextButton(onClick = onViewAll) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun ValuePropCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldDark.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
