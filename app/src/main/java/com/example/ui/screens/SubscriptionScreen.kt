package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CurrentUserProfile
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.PetroleumBrush
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

data class PlanOption(
    val id: String,
    val title: String,
    val titleEn: String,
    val priceEgp: String,
    val priceUsd: String,
    val originalPriceEgp: String?,
    val originalPriceUsd: String?,
    val badge: String?,
    val isPopular: Boolean
)

@Composable
fun SubscriptionScreen(
    user: CurrentUserProfile = CurrentUserProfile(),
    language: AppLanguage = AppLanguage.ARABIC,
    onPlanPurchased: (String, String) -> Unit,
    onRenewWithRoses: () -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    // Location-based currency: Default to EGP for Egypt location, USD outside Egypt
    val isEgyptDefault = user.locationCity.contains("مصر") ||
            user.locationCity.contains("Egypt") ||
            user.locationCity.contains("القاهرة") ||
            (user.latitude in 22.0..32.0 && user.longitude in 24.0..37.0)

    var isEgyptLocation by remember { mutableStateOf(isEgyptDefault) }
    var userRosesBalance by remember { mutableIntStateOf(user.rosesBalance) }

    val plans = listOf(
        PlanOption(
            id = "WEEKLY",
            title = "اشتراك أسبوعي مرن",
            titleEn = "Flexible Weekly Plan",
            priceEgp = "79 ج.م / أسبوع",
            priceUsd = "$4.99 / week",
            originalPriceEgp = null,
            originalPriceUsd = null,
            badge = null,
            isPopular = false
        ),
        PlanOption(
            id = "MONTHLY",
            title = "اشتراك شهري مميز",
            titleEn = "Premium Monthly Plan",
            priceEgp = "199 ج.م / شهر",
            priceUsd = "$12.99 / month",
            originalPriceEgp = "349 ج.م",
            originalPriceUsd = "$19.99",
            badge = "الأكثر طلباً بمصر • وفر 45%",
            isPopular = true
        ),
        PlanOption(
            id = "ANNUAL",
            title = "اشتراك سنوي كامل (حتى الزواج)",
            titleEn = "Full Annual Plan",
            priceEgp = "899 ج.م / سنة",
            priceUsd = "$59.99 / year",
            originalPriceEgp = "1799 ج.م",
            originalPriceUsd = "$119.99",
            badge = "أفضل قيمة توفير • وفر 65%",
            isPopular = false
        )
    )

    var selectedPlanIndex by remember { mutableIntStateOf(1) }
    var selectedPaymentMethod by remember {
        mutableStateOf(if (isEgyptLocation) "VODAFONE_CASH" else "APPLE_PAY")
    }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showRoseSuccessDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .testTag("close_subscriptions_button")
                        .size(40.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = PetroleumGreen)
                }

                Text(
                    text = if (isArabic) "متجر الباقات الذهبية والورد 👑" else "VIP Store & Virtual Roses 👑",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreenDark
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RadiantGoldContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isEgyptLocation) "🇪🇬 ج.م EGP" else "🌐 $ USD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF382902)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Location & Currency Dynamic Switcher
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = PetroleumGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تسعير الموقع الجغرافي:" else "Regional Pricing:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isEgyptLocation) PetroleumGreen else Color.White)
                                    .clickable { isEgyptLocation = true; selectedPaymentMethod = "VODAFONE_CASH" }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "🇪🇬 داخل مصر (ج.م)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEgyptLocation) Color.White else Color.DarkGray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isEgyptLocation) PetroleumGreen else Color.White)
                                    .clickable { isEgyptLocation = false; selectedPaymentMethod = "APPLE_PAY" }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "🌍 خارج مصر (USD)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isEgyptLocation) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(brush = GoldShineBrush)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "👑", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) "باقة سوا سوا VIP الملكية" else "Sawa Sawa VIP Royal Plan",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2B2003)
                            )
                            Text(
                                text = if (isArabic)
                                    "ارفع فرص التوافق للزواج الشرعي بنسبة 350% مع تعزيزات مجانية"
                                else
                                    "Boost your halal marriage matching by 350%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF574106)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION: Virtual Rose Renewal (خاص بحسابات البنات)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Color(0xFFE91E63).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌹", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "تجديد العضوية بباقات الورد" else "Renew VIP with Roses",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF880E4F)
                                    )
                                    Text(
                                        text = if (isArabic) "ميزة خاصة بحسابات الأخوات والبنات" else "Exclusive feature for female accounts",
                                        fontSize = 10.sp,
                                        color = Color(0xFFAD1457)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE91E63))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$userRosesBalance / 50 🌹",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabic)
                                "هذا البوكيه والورد تحصل عليه الأخت من حسابات الشباب تقديراً وإعجاباً، وعند تجميع 50 باقة ورد يمكنكِ تجديد عضويتكِ الذهبية VIP مجاناً بهذه النقاط بدلاً من الدفع النقدي!"
                            else
                                "Female members receiving roses from male profiles can renew VIP membership for free with 50 roses instead of paying!",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF4A148C)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        val progress = (userRosesBalance.toFloat() / 50f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFE91E63),
                            trackColor = Color.LightGray.copy(alpha = 0.4f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (userRosesBalance >= 50) {
                                        userRosesBalance -= 50
                                        onRenewWithRoses()
                                        showRoseSuccessDialog = true
                                    } else {
                                        // Demo preview: add roses for easy testing
                                        userRosesBalance = (userRosesBalance + 15).coerceAtMost(60)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (userRosesBalance >= 50) Color(0xFFE91E63) else Color(0xFFC2185B)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (userRosesBalance >= 50)
                                        (if (isArabic) "تجديد العضوية بـ 50 باقة ورد 🌹" else "Renew VIP (50 Roses)")
                                    else
                                        (if (isArabic) "جمعي باقي الورد أو جربي (+15 🌹)" else "Add roses demo (+15 🌹)"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Perks List
                Text(
                    text = if (isArabic) "جميع مزايا العضوية الذهبية الحصرية:" else "Exclusive Gold Member Perks:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                val perks = if (isArabic) listOf(
                    "إرسال واستقبال باقات الورد الافتراضية VIP 🌹",
                    "عدد غير محدود من الإعجابات اليومية دون أي قيود",
                    "12 محادثة فورية شهرياً لكسر الجليد مع أي مرشح",
                    "3 تعزيزات مجانية أسبوعياً للظهور بالمركز الأول 🚀",
                    "تراجع غير محدود عن التمريرات واستعادة أي ملف",
                    "كشف كامل لمن زار ملفك وتصفحه ومن أعجب بك",
                    "شارة التميز الملكية الذهبية 👑 الموثقة",
                    "تصفح بخصوصية تامة وبدون أي إعلانات"
                ) else listOf(
                    "Send & receive VIP virtual roses 🌹",
                    "Unlimited daily likes without restrictions",
                    "12 instant icebreaker messages each month",
                    "3 weekly profile boosts for #1 top ranking 🚀",
                    "Unlimited rewinds on passed profiles",
                    "See who visited and liked your profile",
                    "Regal verified Gold Crown profile badge 👑",
                    "Full privacy and ad-free experience"
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (perk in perks) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(PetroleumGreenContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PetroleumGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = perk,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Plan Cards Selector
                Text(
                    text = if (isArabic) "اختر خطة الاشتراك المناسبة لك:" else "Select Your Subscription Plan:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    plans.forEachIndexed { index, plan ->
                        val isSelected = selectedPlanIndex == index
                        val currentPrice = if (isEgyptLocation) plan.priceEgp else plan.priceUsd
                        val currentOrig = if (isEgyptLocation) plan.originalPriceEgp else plan.originalPriceUsd

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) RadiantGold else Color.LightGray.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedPlanIndex = index },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) RadiantGoldContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (plan.badge != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PetroleumGreen)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = plan.badge,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isArabic) plan.title else plan.titleEn,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (currentOrig != null) {
                                            Text(
                                                text = currentOrig,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                    }

                                    Text(
                                        text = currentPrice,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) PetroleumGreenDark else Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Payment Methods Options
                Text(
                    text = if (isArabic)
                        "طرق الدفع المتوفرة والمحلية المعتمدة 💳"
                    else
                        "Available Payment Methods 💳",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val methods = if (isEgyptLocation) {
                    listOf(
                        Pair("VODAFONE_CASH", "فودافون كاش 📱"),
                        Pair("INSTAPAY", "انستاباي InstaPay ⚡"),
                        Pair("FAWRY", "فوري Fawry 🏪"),
                        Pair("CARD", "Visa / Master 💳"),
                        Pair("APPLE_PAY", "Apple Pay 🍏")
                    )
                } else {
                    listOf(
                        Pair("APPLE_PAY", "Apple Pay 🍏"),
                        Pair("GOOGLE_PAY", "Google Pay 🌐"),
                        Pair("CARD", "Visa / Master 💳"),
                        Pair("MADA", "مدى mada 🇸🇦")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    methods.chunked(2).forEach { rowMethods ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMethods.forEach { (id, label) ->
                                val isSelected = selectedPaymentMethod == id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) PetroleumGreen else Color.LightGray.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .background(if (isSelected) PetroleumGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedPaymentMethod = id }
                                        .padding(vertical = 11.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PetroleumGreenDark else Color.Black
                                    )
                                }
                            }
                            if (rowMethods.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Checkout CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                val selectedPlan = plans[selectedPlanIndex]
                val priceText = if (isEgyptLocation) selectedPlan.priceEgp else selectedPlan.priceUsd

                Button(
                    onClick = {
                        onPlanPurchased(selectedPlan.id, priceText)
                        showSuccessDialog = true
                    },
                    modifier = Modifier
                        .testTag("subscribe_checkout_button")
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetroleumGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = RadiantGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "اشترك بأمان ($priceText)" else "Subscribe Securely ($priceText)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    if (showRoseSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showRoseSuccessDialog = false
                onClose()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌹", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "مبروك! تجديد العضوية بالورد" else "Renewed with Roses!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Text(
                    text = if (isArabic)
                        "تم خصم 50 باقة ورد وتجديد عضويتك الذهبية VIP بنجاح! شكراً لتفاعلك الراقي ومشاركتك في المنصة."
                    else
                        "50 roses deducted and your VIP membership successfully renewed!",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRoseSuccessDialog = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text(if (isArabic) "رائع، شكراً لكِ" else "Awesome")
                }
            }
        )
    }

    if (showSuccessDialog) {
        val selectedPlan = plans[selectedPlanIndex]
        val priceText = if (isEgyptLocation) selectedPlan.priceEgp else selectedPlan.priceUsd

        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onClose()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👑", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "مبروك! تم تفعيل الاشتراك بنجاح" else "Subscription Activated!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (isArabic)
                            "تم ترقية حسابك إلى باقة ${selectedPlan.title} ($priceText). تم تفعيل جميع المزايا وشارة VIP الملكية."
                        else
                            "Upgraded to ${selectedPlan.titleEn} ($priceText). VIP perks activated.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Text(if (isArabic) "ابدأ الاستخدام" else "Start Using")
                }
            }
        )
    }
}
