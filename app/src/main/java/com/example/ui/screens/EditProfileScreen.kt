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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CurrentUserProfile
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer

@Composable
fun EditProfileScreen(
    user: CurrentUserProfile,
    language: AppLanguage,
    onSave: (CurrentUserProfile) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    var name by remember { mutableStateOf(user.name) }
    var ageText by remember { mutableStateOf(user.age.toString()) }
    var city by remember { mutableStateOf(user.city) }
    var profession by remember { mutableStateOf(user.profession) }
    var education by remember { mutableStateOf(user.education) }
    var heightText by remember { mutableStateOf(user.heightCm.toString()) }
    var bio by remember { mutableStateOf(user.bio) }
    var marriageGoal by remember { mutableStateOf(user.marriageGoal) }

    // Religious details
    var religiousPractice by remember { mutableStateOf(user.religiousPractice) }
    var islamicDress by remember { mutableStateOf(user.islamicDress) }
    var prayersHabit by remember { mutableStateOf(user.prayersHabit) }
    var halalFood by remember { mutableStateOf(user.halalFood) }

    // Wali / Chaperone details
    var chaperoneName by remember { mutableStateOf(user.chaperoneName) }
    var chaperoneEmail by remember { mutableStateOf(user.chaperoneEmail) }
    var chaperonePhone by remember { mutableStateOf(user.chaperonePhone) }

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
                    onClick = onBack,
                    modifier = Modifier.testTag("edit_profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PetroleumGreen
                    )
                }

                Text(
                    text = if (isArabic) "تعديل الملف الشخصي" else "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        val updated = user.copy(
                            name = name.trim(),
                            age = ageText.toIntOrNull() ?: user.age,
                            city = city.trim(),
                            profession = profession.trim(),
                            education = education.trim(),
                            heightCm = heightText.toIntOrNull() ?: user.heightCm,
                            bio = bio.trim(),
                            marriageGoal = marriageGoal.trim(),
                            religiousPractice = religiousPractice,
                            islamicDress = islamicDress,
                            prayersHabit = prayersHabit,
                            halalFood = halalFood,
                            chaperoneName = chaperoneName.trim(),
                            chaperoneEmail = chaperoneEmail.trim(),
                            chaperonePhone = chaperonePhone.trim()
                        )
                        onSave(updated)
                    },
                    modifier = Modifier
                        .testTag("save_profile_button")
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                    shape = RoundedCornerShape(19.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "حفظ" else "Save",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo preview with edit indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, RadiantGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_sarah),
                        contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = if (isArabic) "المعلومات الأساسية" else "Basic Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )

                // Name & Age
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isArabic) "الاسم" else "Name") },
                        modifier = Modifier
                            .weight(2f)
                            .testTag("edit_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text(if (isArabic) "العمر" else "Age") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_age_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // City & Profession
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text(if (isArabic) "المدينة" else "City") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = profession,
                        onValueChange = { profession = it },
                        label = { Text(if (isArabic) "المهنة / الوظيفة" else "Profession") },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Education & Height
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = education,
                        onValueChange = { education = it },
                        label = { Text(if (isArabic) "المؤهل التعليمي" else "Education") },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text(if (isArabic) "الطول (سم)" else "Height (cm)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Divider(color = Color.LightGray.copy(alpha = 0.3f))

                // Religious & Practice
                Text(
                    text = if (isArabic) "الالتزام الديني والضوابط الشرعية 🕌" else "Religious Details 🕌",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )

                OutlinedTextField(
                    value = religiousPractice,
                    onValueChange = { religiousPractice = it },
                    label = { Text(if (isArabic) "مستوى الالتزام الديني" else "Religious Practice") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = islamicDress,
                    onValueChange = { islamicDress = it },
                    label = { Text(if (isArabic) "اللباس الإسلامي (حجاب، نقاب، محتشم)" else "Islamic Dress") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = prayersHabit,
                    onValueChange = { prayersHabit = it },
                    label = { Text(if (isArabic) "المحافظة على الصلاة" else "Prayer Habit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Divider(color = Color.LightGray.copy(alpha = 0.3f))

                // Bio & Marriage Goals
                Text(
                    text = if (isArabic) "عن نفسي وتطلعات الزواج 💍" else "About Me & Marriage Goals 💍",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(if (isArabic) "عن نفسي (اهتماماتك وشخصيتك)" else "About Me") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                OutlinedTextField(
                    value = marriageGoal,
                    onValueChange = { marriageGoal = it },
                    label = { Text(if (isArabic) "هدفي ورؤيتي للزواج" else "Marriage Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                Divider(color = Color.LightGray.copy(alpha = 0.3f))

                // Wali / Chaperone Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PetroleumGreenContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = PetroleumGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "بيانات الولي / المرافق الشرعي 🛡️" else "Wali / Guardian Details 🛡️",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreenDark
                            )
                        }

                        OutlinedTextField(
                            value = chaperoneName,
                            onValueChange = { chaperoneName = it },
                            label = { Text(if (isArabic) "اسم ولي الأمر" else "Guardian Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = chaperoneEmail,
                            onValueChange = { chaperoneEmail = it },
                            label = { Text(if (isArabic) "البريد الإلكتروني للولي" else "Guardian Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = chaperonePhone,
                            onValueChange = { chaperonePhone = it },
                            label = { Text(if (isArabic) "رقم هاتف الولي" else "Guardian Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
