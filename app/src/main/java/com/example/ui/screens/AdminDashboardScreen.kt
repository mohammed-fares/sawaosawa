package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AdminBroadcast
import com.example.model.AdminReport
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.MuzzPink
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@Composable
fun AdminDashboardScreen(
    candidates: List<CandidateProfile>,
    language: AppLanguage,
    onBack: () -> Unit,
    onToggleVerify: (String) -> Unit,
    onToggleGold: (String) -> Unit,
    onToggleBan: (String) -> Unit,
    onToggleBlur: (String) -> Unit,
    onAddCandidate: (CandidateProfile) -> Unit,
    onSendBroadcast: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedGenderFilter by remember { mutableStateOf("ALL") }

    // Dialogs
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    // Reports mock list
    val reports = remember {
        mutableStateListOf(
            AdminReport(
                id = "r1",
                reporterName = "سارة",
                reportedUserId = "c1",
                reportedUserName = "عدنان",
                reason = "طلب التواصل خارج التطبيق بدون علم الولي",
                timestamp = "منذ 15 دقيقة",
                status = "PENDING"
            ),
            AdminReport(
                id = "r2",
                reporterName = "نور",
                reportedUserId = "c4",
                reportedUserName = "نادية",
                reason = "اشتباه في صورة الحساب الشخصي",
                timestamp = "منذ ساعتين",
                status = "PENDING"
            )
        )
    }

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
            // Admin Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PetroleumGreenDark)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(RadiantGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = PetroleumGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "لوحة التحكم الإدارية (Admin)" else "Admin Control Panel",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { showBroadcastDialog = true },
                    modifier = Modifier.testTag("admin_broadcast_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Broadcast",
                        tint = RadiantGold
                    )
                }
            }

            // Scrollable Admin Tabs Row
            val tabs = if (isArabic) {
                listOf("📊 الإحصائيات", "👥 إدارة الأعضاء", "🛡️ الرقابة والضوابط", "💳 الاشتراكات والأسعار")
            } else {
                listOf("📊 Overview", "👥 Members", "🛡️ Moderation", "💳 Subscriptions")
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PetroleumGreenDark,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RadiantGold,
                        height = 3.dp
                    )
                },
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) RadiantGold else Color.White.copy(alpha = 0.8f)
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // TAB 1: OVERVIEW & REALTIME KPIS
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isArabic) "نظرة عامة على النشاط والإيرادات 📈" else "Platform Performance & Metrics 📈",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreen
                        )

                        // 2x2 Metrics Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricCard(
                                title = if (isArabic) "إجمالي الأعضاء" else "Total Members",
                                value = "1,428",
                                sub = if (isArabic) "+84 هذا الأسبوع" else "+84 this week",
                                color = PetroleumGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = if (isArabic) "المشتركون النشطون" else "Gold Subscribers",
                                value = "384",
                                sub = if (isArabic) "27% من إجمالي الأعضاء" else "27% of total",
                                color = RadiantGoldDark,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricCard(
                                title = if (isArabic) "إجمالي الإيرادات" else "Total Revenue",
                                value = "142,500 ر.س",
                                sub = if (isArabic) "+18% نمو شهري" else "+18% MoM growth",
                                color = PetroleumGreenDark,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = if (isArabic) "التوافقات الناجحة" else "Halal Matches",
                                value = "324",
                                sub = if (isArabic) "128 خطوبة وعقد قران" else "128 marriages",
                                color = MuzzPink,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Demographics Gender Breakdown Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isArabic) "التوزيع الديموغرافي للأعضاء" else "Gender Demographics",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Man, contentDescription = null, tint = PetroleumGreen, modifier = Modifier.size(32.dp))
                                        Text(text = if (isArabic) "الذكور: 688 (48%)" else "Males: 688 (48%)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Woman, contentDescription = null, tint = MuzzPink, modifier = Modifier.size(32.dp))
                                        Text(text = if (isArabic) "الإناث: 740 (52%)" else "Females: 740 (52%)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        // Broadcast Promo Action
                        Button(
                            onClick = { showBroadcastDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                        ) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = RadiantGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "إرسال إشعار ترويجي عام لجميع الأعضاء" else "Send Promotional Broadcast",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                1 -> {
                    // TAB 2: MEMBERS MANAGEMENT (ذكور وإناث، حظر، توثيق، ترقية)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "إدارة الأعضاء والبيانات" else "Member Directory",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = { showAddMemberDialog = true },
                                modifier = Modifier
                                    .testTag("add_member_button")
                                    .height(36.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (isArabic) "إضافة عضو" else "Add Member", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Gender Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf(
                                Pair("ALL", if (isArabic) "الكل" else "All"),
                                Pair("MALE", if (isArabic) "الذكور 👨" else "Males 👨"),
                                Pair("FEMALE", if (isArabic) "الإناث 🧕" else "Females 🧕")
                            )

                            for ((id, label) in filters) {
                                val isSel = selectedGenderFilter == id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSel) PetroleumGreen else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedGenderFilter = id }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val filteredList = candidates.filter {
                            selectedGenderFilter == "ALL" || it.gender.equals(selectedGenderFilter, ignoreCase = true)
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredList) { member ->
                                AdminMemberItem(
                                    candidate = member,
                                    isArabic = isArabic,
                                    onToggleVerify = { onToggleVerify(member.id) },
                                    onToggleGold = { onToggleGold(member.id) },
                                    onToggleBan = { onToggleBan(member.id) },
                                    onToggleBlur = { onToggleBlur(member.id) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: CONTENT MODERATION & REPORTS
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (isArabic) "بلاغات الأعضاء والرقابة الشرعية 🛡️" else "Reports & Moderation 🛡️",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreen
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (reports.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isArabic) "لا توجد بلاغات معلقة حالياً ✓" else "No pending reports ✓",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(reports) { report ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "بلاغ من: ${report.reporterName}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = report.timestamp,
                                                    fontSize = 11.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "المبلّغ عنه: ${report.reportedUserName}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PetroleumGreen
                                            )
                                            Text(
                                                text = "السبب: ${report.reason}",
                                                fontSize = 12.sp,
                                                color = Color.DarkGray,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TextButton(onClick = { reports.remove(report) }) {
                                                    Text(if (isArabic) "تجاهل" else "Dismiss", color = Color.Gray)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Button(
                                                    onClick = {
                                                        onToggleBan(report.reportedUserId)
                                                        reports.remove(report)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text(if (isArabic) "حظر العضو فوراً" else "Ban Member", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 4: SUBSCRIPTIONS & PRICING MANAGEMENT
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isArabic) "إدارة أسعار باقات سوا سوا GOLD 💳" else "Subscription Plans & Pricing 💳",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreen
                        )

                        PriceEditorCard(
                            planName = if (isArabic) "الباقة الأسبوعية" else "Weekly Plan",
                            currentPrice = "39.99 ر.س",
                            activeSubscribers = "142 مشترك"
                        )

                        PriceEditorCard(
                            planName = if (isArabic) "الباقة الشهرية (الأكثر طلباً)" else "Monthly Plan",
                            currentPrice = "99.99 ر.س",
                            activeSubscribers = "196 مشترك"
                        )

                        PriceEditorCard(
                            planName = if (isArabic) "الباقة السنوية" else "Annual Plan",
                            currentPrice = "599.99 ر.س",
                            activeSubscribers = "46 مشترك"
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = RadiantGoldContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎁", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "الحملات الترويجية والخصومات" else "Promo Campaigns",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = RadiantGoldDark
                                    )
                                    Text(
                                        text = if (isArabic) "تفعيل كوبونات الخصم وعروض الأعياد مباشرة" else "Activate discount coupons & seasonal deals",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Member Dialog
    if (showAddMemberDialog) {
        var newName by remember { mutableStateOf("") }
        var newAge by remember { mutableStateOf("25") }
        var newCity by remember { mutableStateOf("الرياض") }
        var newProfession by remember { mutableStateOf("مهندس برمجيات") }
        var newGender by remember { mutableStateOf("MALE") }

        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = {
                Text(if (isArabic) "إضافة عضو جديد للمنصة" else "Add New Member", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(if (isArabic) "الاسم" else "Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAge,
                            onValueChange = { newAge = it },
                            label = { Text(if (isArabic) "العمر" else "Age") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newCity,
                            onValueChange = { newCity = it },
                            label = { Text(if (isArabic) "المدينة" else "City") },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    OutlinedTextField(
                        value = newProfession,
                        onValueChange = { newProfession = it },
                        label = { Text(if (isArabic) "المهنة" else "Profession") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isArabic) "الجنس: " else "Gender: ", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { newGender = "MALE" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (newGender == "MALE") PetroleumGreen else Color.LightGray)
                        ) {
                            Text("ذكر", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { newGender = "FEMALE" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (newGender == "FEMALE") PetroleumGreen else Color.LightGray)
                        ) {
                            Text("أنثى", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val newCandidate = CandidateProfile(
                                id = "admin_${System.currentTimeMillis()}",
                                name = newName.trim(),
                                nameEn = newName.trim(),
                                age = newAge.toIntOrNull() ?: 25,
                                city = newCity.trim(),
                                cityEn = newCity.trim(),
                                distanceKm = 8,
                                profession = newProfession.trim(),
                                professionEn = newProfession.trim(),
                                ethnicity = "عربي",
                                ethnicityEn = "Arab",
                                religiousPractice = "ملتزم بالفرائض 🌙",
                                religiousPracticeEn = "Practising",
                                islamicDress = "محتشم",
                                islamicDressEn = "Modest",
                                prayersHabit = "أصلي دائماً",
                                prayersHabitEn = "Always",
                                halalFood = "حلال دائماً",
                                maritalStatus = "أعزب",
                                maritalStatusEn = "Single",
                                willingToRelocate = true,
                                hasChildren = false,
                                heightCm = 175,
                                education = "جامعي",
                                bio = "عضو تم إضافته عبر لوحة التحكم الإدارية.",
                                bioEn = "Member created via Admin Dashboard.",
                                marriageGoal = "بناء أسرة مسلمة سعيدة.",
                                marriageGoalEn = "Building a halal home.",
                                icebreakerQuestion = "أهم قيمة في الزواج...",
                                icebreakerAnswer = "التقوى والمودة والرحمة.",
                                photoRes = if (newGender == "MALE") R.drawable.profile_ahmed else R.drawable.profile_sarah,
                                gender = newGender,
                                isVerified = true,
                                isGoldMember = true
                            )
                            onAddCandidate(newCandidate)
                            showAddMemberDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Text(if (isArabic) "إضافة العضو" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemberDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // Broadcast Dialog
    if (showBroadcastDialog) {
        var broadcastTitle by remember { mutableStateOf("عرض حصري بمناسبة إطلاق تطبيق سوا سوا!") }
        var broadcastBody by remember { mutableStateOf("احصل على ترقية مجانية لعضوية Gold واستفد من 12 محادثة فورية مجانية اليوم.") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = PetroleumGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isArabic) "إرسال إشعار ترويجي عام" else "Broadcast Notification", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text(if (isArabic) "عنوان الإشعار" else "Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = broadcastBody,
                        onValueChange = { broadcastBody = it },
                        label = { Text(if (isArabic) "نص الإشعار الترويجي" else "Message Body") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendBroadcast(broadcastTitle, broadcastBody)
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isArabic) "إرسال للجميع الآن" else "Send Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = sub, fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun AdminMemberItem(
    candidate: CandidateProfile,
    isArabic: Boolean,
    onToggleVerify: () -> Unit,
    onToggleGold: () -> Unit,
    onToggleBan: () -> Unit,
    onToggleBlur: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (candidate.isBanned) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (candidate.isGoldMember) RadiantGold else PetroleumGreen, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = candidate.photoRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${candidate.name} (${candidate.age})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (candidate.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(14.dp))
                    }
                    if (candidate.isGoldMember) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "👑", fontSize = 11.sp)
                    }
                    if (candidate.isBanned) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "[محظور]", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "${candidate.profession} • ${candidate.city} • ${if (candidate.gender == "MALE") "ذكر" else "أنثى"}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Action Icons Row
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Verify toggle
                IconButton(
                    onClick = onToggleVerify,
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (candidate.isVerified) RadiantGoldContainer else Color.LightGray.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Verify", tint = PetroleumGreenDark, modifier = Modifier.size(16.dp))
                }

                // Ban toggle
                IconButton(
                    onClick = onToggleBan,
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (candidate.isBanned) Color.Red else Color.LightGray.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Block, contentDescription = "Ban", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PriceEditorCard(
    planName: String,
    currentPrice: String,
    activeSubscribers: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = planName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "المشتركون حالياً: $activeSubscribers", fontSize = 11.sp, color = Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentPrice,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = PetroleumGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Price", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
