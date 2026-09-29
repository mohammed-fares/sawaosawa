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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.MuzzPink
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
    val price: String,
    val originalPrice: String?,
    val badge: String?,
    val isPopular: Boolean
)

@Composable
fun SubscriptionScreen(
    language: AppLanguage,
    onPlanPurchased: (String, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    val plans = listOf(
        PlanOption(
            id = "WEEKLY",
            title = "اشتراك أسبوعي",
            titleEn = "Weekly Plan",
            price = "39.99 ر.س / أسبوع",
            originalPrice = null,
            badge = null,
            isPopular = false
        ),
        PlanOption(
            id = "MONTHLY",
            title = "اشتراك شهري",
            titleEn = "Monthly Plan",
            price = "99.99 ر.س / شهر",
            originalPrice = "159.99 ر.س",
            badge = "الأكثر شعبية • وفر 35%",
            isPopular = true
        ),
        PlanOption(
            id = "ANNUAL",
            title = "اشتراك سنوي كامل",
            titleEn = "Annual Plan",
            price = "599.99 ر.س / سنة",
            originalPrice = "1199.99 ر.س",
            badge = "أفضل قيمة • وفر 60%",
            isPopular = false
        )
    )

    var selectedPlanIndex by remember { mutableIntStateOf(1) }
    var selectedPaymentMethod by remember { mutableStateOf("APPLE_PAY") }
    var showSuccessDialog by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("subscription_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (isArabic) "عضوية سوا سوا GOLD 👑" else "Sawa Sawa GOLD VIP 👑",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crown Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GoldShineBrush)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "👑", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic) "سوا سوا GOLD" else "SAWA SAWA GOLD",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF261D05),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isArabic)
                                    "ارفع فرص التوافق للزواج الشرعي بنسبة 350%"
                                else
                                    "Boost your halal marriage matching by 350%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF574106)
                            )
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
                    "عدد غير محدود من الإعجابات اليومية دون أي قيود",
                    "12 محادثة فورية شهرياً لكسر الجليد مع أي مرشح",
                    "3 تعزيزات مجانية أسبوعياً للظهور بالمركز الأول 🚀",
                    "تراجع غير محدود عن التمريرات السابقة واستعادة أي ملف",
                    "كشف كامل لمن زار ملفك وتصفحه ومن أعجب بك",
                    "شارة التميز الملكية الذهبية 👑 الموثقة",
                    "تصفح بخصوصية تامة وبدون أي إعلانات"
                ) else listOf(
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
                                    .size(22.dp)
                                    .background(PetroleumGreenContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PetroleumGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
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
                                        if (plan.originalPrice != null) {
                                            Text(
                                                text = plan.originalPrice,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        }
                                    }

                                    Text(
                                        text = plan.price,
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

                // Payment Methods Options (Apple Pay, Mada, Credit, STC Pay)
                Text(
                    text = if (isArabic) "طرق الدفع الآمنة المعتمدة 💳" else "Secure Payment Methods 💳",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val methods = listOf(
                        Pair("APPLE_PAY", "Apple Pay"),
                        Pair("MADA", "مدى mada"),
                        Pair("CARD", "Visa / Master"),
                        Pair("STC_PAY", "stc pay")
                    )

                    for ((id, label) in methods) {
                        val isSelected = selectedPaymentMethod == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PetroleumGreen else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedPaymentMethod = id }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
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
                Button(
                    onClick = {
                        onPlanPurchased(selectedPlan.id, selectedPlan.price)
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
                        text = if (isArabic) "اشترك الآن بأمان (${selectedPlan.price})" else "Subscribe Securely (${selectedPlan.price})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    if (showSuccessDialog) {
        val selectedPlan = plans[selectedPlanIndex]
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
                            "تم ترقية حسابك إلى باقة ${selectedPlan.title} (${selectedPlan.price}). تم منحك 12 محادثة فورية و3 تعزيزات والشارة الذهبية الملكية."
                        else
                            "Upgraded to ${selectedPlan.titleEn}. You have received 12 instant chats, 3 boosts, and the royal Gold VIP badge.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isArabic) "نسأل الله أن يبارك لك ويوفقك في إيجاد الشريك الصالح." else "May Allah bless your search for a righteous partner.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PetroleumGreen
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
