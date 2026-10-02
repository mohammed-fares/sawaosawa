package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.FilterPreferences
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    currentFilters: FilterPreferences,
    language: AppLanguage,
    onApply: (FilterPreferences) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    var ageRange by remember {
        mutableStateOf(currentFilters.minAge.toFloat()..currentFilters.maxAge.toFloat())
    }
    var selectedPractice by remember { mutableStateOf(currentFilters.religiousPractice) }
    var selectedDress by remember { mutableStateOf(currentFilters.islamicDress) }
    var selectedLocation by remember { mutableStateOf(currentFilters.locationCountry) }
    var selectedLanguage by remember { mutableStateOf(currentFilters.languagePreference) }
    var selectedEthnicity by remember { mutableStateOf(currentFilters.ethnicity) }
    var showOtherProfiles by remember { mutableStateOf(currentFilters.showOtherProfiles) }
    var onlyVerified by remember { mutableStateOf(currentFilters.onlyVerified) }

    var showLocationDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showEthnicityDialog by remember { mutableStateOf(false) }

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
            // Header Bar (مطابقة لصورة 8.webp: مسح الكل، مرشحات، X)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_filters_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (isArabic) "مرشحات" else "Filters",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                TextButton(
                    onClick = {
                        ageRange = 20f..35f
                        selectedPractice = "الكل"
                        selectedDress = "الكل"
                        selectedLocation = "الكل"
                        selectedLanguage = "الكل"
                        selectedEthnicity = "الكل"
                        showOtherProfiles = true
                        onlyVerified = false
                        onReset()
                    },
                    modifier = Modifier.testTag("reset_filters_button")
                ) {
                    Text(
                        text = if (isArabic) "مسح الكل" else "Clear All",
                        color = PetroleumGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scrollable Filters Body (مطابقة لصورة 8.webp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // 1. العمر (العمر: 23-45 سنة)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "العمر" else "Age",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic)
                            "${ageRange.start.toInt()}-${ageRange.endInclusive.toInt()} سنة"
                        else
                            "${ageRange.start.toInt()}-${ageRange.endInclusive.toInt()} years",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                }

                RangeSlider(
                    value = ageRange,
                    onValueChange = { ageRange = it },
                    valueRange = 18f..50f,
                    steps = 31,
                    colors = SliderDefaults.colors(
                        thumbColor = PetroleumGreen,
                        activeTrackColor = PetroleumGreen,
                        inactiveTrackColor = PetroleumGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. تحديد الموقع عبر
                FilterItemRow(
                    title = if (isArabic) "تحديد الموقع عبر" else "Limit location by",
                    value = if (selectedLocation == "الكل") (if (isArabic) "جميع الدول والمدن" else "All Countries") else selectedLocation,
                    onClick = { showLocationDialog = true }
                )

                // 3. اللغة
                FilterItemRow(
                    title = if (isArabic) "اللغة" else "Language",
                    value = if (selectedLanguage == "الكل") (if (isArabic) "جميع اللغات" else "All Languages") else selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )

                // 4. الأصل العرقي
                FilterItemRow(
                    title = if (isArabic) "الأصل العرقي" else "Ethnicity",
                    value = if (selectedEthnicity == "الكل") (if (isArabic) "جميع الأصول" else "All Ethnicities") else selectedEthnicity,
                    onClick = { showEthnicityDialog = true }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 5. حلول التصفية المتقدمة (الالتزام الديني والنشاط)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = RadiantGoldContainer
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "حلول التصفية المتقدمة" else "Advanced Filtering Solutions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadiantGoldDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isArabic) "الالتزام الديني والنشاط:" else "Religious Commitment:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreenDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val options = listOf(
                                if (isArabic) "الكل" else "All",
                                if (isArabic) "ملتزم دينياً" else "Religious",
                                if (isArabic) "معتدل" else "Moderate"
                            )
                            for (opt in options) {
                                val selected = selectedPractice == opt || (selectedPractice == "الكل" && opt == options[0])
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (selected) PetroleumGreen else Color.White)
                                        .clickable { selectedPractice = opt }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = opt,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else PetroleumGreenDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Islamic dress
                        Text(
                            text = if (isArabic) "اللباس الإسلامي:" else "Islamic Dress:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreenDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val dressOptions = listOf(
                                if (isArabic) "الكل" else "All",
                                if (isArabic) "حجاب" else "Hijab",
                                if (isArabic) "نقاب" else "Niqab",
                                if (isArabic) "محتشم" else "Modest"
                            )
                            for (d in dressOptions) {
                                val isSel = selectedDress == d || (selectedDress == "الكل" && d == dressOptions[0])
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSel) RadiantGold else Color.White)
                                        .clickable { selectedDress = d }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = d,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color(0xFF2E2204) else Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verified switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "حسابات موثقة بالسلفي فقط" else "Selfie Verified Only",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "أعضاء تم التحقق من هويتهم وصورهم الحقيقية" else "Members verified by admin team",
                                    fontSize = 10.sp,
                                    color = Color.DarkGray
                                )
                            }
                            Switch(
                                checked = onlyVerified,
                                onCheckedChange = { onlyVerified = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PetroleumGreen
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. Show other profiles toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "أظهر لي ملفات أخرى مناسبة" else "Show me other profiles",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic)
                                    "توسيع نطاق البحث في حال انتهاء التوافقات المباشرة"
                                else
                                    "Expands recommendations if direct matches finish",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = showOtherProfiles,
                            onCheckedChange = { showOtherProfiles = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PetroleumGreen
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val newPref = FilterPreferences(
                            minAge = ageRange.start.toInt(),
                            maxAge = ageRange.endInclusive.toInt(),
                            locationCountry = selectedLocation,
                            languagePreference = selectedLanguage,
                            ethnicity = selectedEthnicity,
                            religiousPractice = selectedPractice,
                            islamicDress = selectedDress,
                            onlyVerified = onlyVerified,
                            showOtherProfiles = showOtherProfiles
                        )
                        onApply(newPref)
                    },
                    modifier = Modifier
                        .testTag("apply_filters_button")
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetroleumGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isArabic) "تطبيق المرشحات والتحديث" else "Apply Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Location Selection Dialog
        if (showLocationDialog) {
            val locationOptions = if (isArabic) {
                listOf("الكل", "مصر والقاهرة", "السعودية والخليج", "دبي والإمارات", "بلاد الشام", "المغرب العربي")
            } else {
                listOf("All", "Egypt & Cairo", "Saudi & GCC", "Dubai & UAE", "Levant", "North Africa")
            }
            FilterOptionPickerModal(
                title = if (isArabic) "تحديد الموقع الجغرافي" else "Select Location",
                options = locationOptions,
                selectedOption = selectedLocation,
                onSelect = {
                    selectedLocation = it
                    showLocationDialog = false
                },
                onDismiss = { showLocationDialog = false }
            )
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            val languageOptions = if (isArabic) {
                listOf("الكل", "العربية", "الإنجليزية", "الفرنسية")
            } else {
                listOf("All", "Arabic", "English", "French")
            }
            FilterOptionPickerModal(
                title = if (isArabic) "لغات التحدث المفضلة" else "Preferred Languages",
                options = languageOptions,
                selectedOption = selectedLanguage,
                onSelect = {
                    selectedLanguage = it
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // Ethnicity Selection Dialog
        if (showEthnicityDialog) {
            val ethnicityOptions = if (isArabic) {
                listOf("الكل", "عربي", "خليجي", "شامي", "مغاربي", "مصري")
            } else {
                listOf("All", "Arab", "GCC", "Levantine", "North African", "Egyptian")
            }
            FilterOptionPickerModal(
                title = if (isArabic) "الأصل والمنبت العرقي" else "Ethnicity & Heritage",
                options = ethnicityOptions,
                selectedOption = selectedEthnicity,
                onSelect = {
                    selectedEthnicity = it
                    showEthnicityDialog = false
                },
                onDismiss = { showEthnicityDialog = false }
            )
        }
    }
}

@Composable
fun FilterOptionPickerModal(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PetroleumGreenDark)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    val isSelected = selectedOption == option || (selectedOption == "الكل" && option == options.first())
                    Surface(
                        onClick = { onSelect(option) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PetroleumGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) PetroleumGreen else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PetroleumGreenDark else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PetroleumGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = PetroleumGreen, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FilterItemRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }
        Divider(modifier = Modifier.padding(top = 10.dp), color = Color.LightGray.copy(alpha = 0.3f))
    }
}
