package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CurrentUserProfile
import com.example.model.RegistrationFlowStep
import com.example.ui.theme.PetroleumBrush
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationWizardScreen(
    language: AppLanguage,
    currentStep: RegistrationFlowStep,
    userProfile: CurrentUserProfile,
    onStepCompleted: (RegistrationFlowStep, CurrentUserProfile) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    val context = LocalContext.current

    // Internal Form States
    var selectedLanguage by remember { mutableStateOf(if (isArabic) "ar" else "en") }
    var selectedCountry by remember { mutableStateOf("مصر 🇪🇬") }
    var phoneNumber by remember { mutableStateOf(userProfile.phoneNumber.ifBlank { "+20 " }) }
    var otpCode by remember { mutableStateOf("123456") }
    var name by remember { mutableStateOf(userProfile.name) }
    var password by remember { mutableStateOf("Sawa@2026") }
    var gender by remember { mutableStateOf(userProfile.gender) }
    var age by remember { mutableIntStateOf(userProfile.age) }
    var city by remember { mutableStateOf(userProfile.city) }
    var profession by remember { mutableStateOf(userProfile.profession) }
    var education by remember { mutableStateOf(userProfile.education) }
    var religiousPractice by remember { mutableStateOf(userProfile.religiousPractice) }
    var islamicDress by remember { mutableStateOf(userProfile.islamicDress) }
    var marriageGoal by remember { mutableStateOf(userProfile.marriageGoal) }
    var bio by remember { mutableStateOf(userProfile.bio) }
    var desireForChildren by remember { mutableStateOf(userProfile.desireForChildren) }
    var commitmentAgreementChecked by remember { mutableStateOf(userProfile.hasAcceptedCommitmentAgreement) }

    // Activity Result Launchers for Permissions UX
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Continue regardless of grant to allow graceful degraded experience
        onStepCompleted(
            RegistrationFlowStep.NOTIFICATIONS_PERMISSION,
            userProfile.copy(isGpsEnabled = true)
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onStepCompleted(
            RegistrationFlowStep.COMPLETED,
            userProfile.copy(
                name = name,
                gender = gender,
                age = age,
                city = city,
                profession = profession,
                education = education,
                religiousPractice = religiousPractice,
                islamicDress = islamicDress,
                marriageGoal = marriageGoal,
                bio = bio,
                desireForChildren = desireForChildren,
                hasAcceptedCommitmentAgreement = true,
                registrationStep = "COMPLETED"
            )
        )
    }

    val steps = RegistrationFlowStep.values()
    val currentStepIndex = steps.indexOf(currentStep).coerceAtLeast(0)
    val progress = (currentStepIndex + 1).toFloat() / steps.size.toFloat()

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
            // Header with Back Button and Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = PetroleumGreen
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) "تسجيل حساب جديد - سوا سوا" else "Create Sawa Sawa Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PetroleumGreenDark
                    )
                    Text(
                        text = if (isArabic) "خطوة ${currentStepIndex + 1} من ${steps.size}" else "Step ${currentStepIndex + 1} of ${steps.size}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    color = RadiantGoldDark,
                    fontSize = 14.sp
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = RadiantGold,
                trackColor = Color(0xFFE0E0E0)
            )

            // Step Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "RegistrationStepAnimation"
                ) { step ->
                    when (step) {
                        RegistrationFlowStep.WELCOME -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(RadiantGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💍", fontSize = 40.sp)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isArabic) "أهلاً بك في منصة سوا سوا" else "Welcome to Sawa Sawa",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isArabic) "حيث يلتقي المسلمون للزواج الحلال" else "Where Muslims Meet To Marry",
                                    fontSize = 14.sp,
                                    color = RadiantGoldDark,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Text(
                                            text = if (isArabic)
                                                "منصة آمنة، شرعية، وموثقة تضمن لك الخصوصية الكاملة وإشراك الولي عند الرغبة مع معايير أخلاقية إسلامية رفيعة."
                                            else
                                                "A safe, halal, and verified platform ensuring complete privacy and guardian involvement with Islamic values.",
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.LANGUAGE, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("wizard_start_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "ابدأ التسجيل الآن" else "Get Started",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        RegistrationFlowStep.LANGUAGE -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "اختر لغة التطبيق المفضلة" else "Choose Preferred Language",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedLanguage = "ar" }
                                            .border(
                                                width = if (selectedLanguage == "ar") 2.dp else 1.dp,
                                                color = if (selectedLanguage == "ar") RadiantGold else Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        colors = CardDefaults.cardColors(containerColor = if (selectedLanguage == "ar") RadiantGoldContainer else Color.White)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("العربية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("اللغة العربية (RTL)", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedLanguage = "en" }
                                            .border(
                                                width = if (selectedLanguage == "en") 2.dp else 1.dp,
                                                color = if (selectedLanguage == "en") RadiantGold else Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        colors = CardDefaults.cardColors(containerColor = if (selectedLanguage == "en") RadiantGoldContainer else Color.White)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("English", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("English (LTR)", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.COUNTRY, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "التالي" else "Next",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        RegistrationFlowStep.COUNTRY -> {
                            val countries = listOf("مصر 🇪🇬", "السعودية 🇸🇦", "الإمارات 🇦🇪", "الكويت 🇰🇼", "قطر 🇶🇦", "الأردن 🇯🇴", "دولة أخرى 🌍")
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "مكان الإقامة والدولة" else "Select Country of Residence",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    countries.forEach { c ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedCountry = c }
                                                .border(
                                                    width = if (selectedCountry == c) 2.dp else 1.dp,
                                                    color = if (selectedCountry == c) PetroleumGreen else Color(0xFFE0E0E0),
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (selectedCountry == c) Color(0xFFEBF3F0) else Color.White
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(c, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                                if (selectedCountry == c) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PetroleumGreen)
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.PHONE_INPUT, userProfile.copy(country = selectedCountry)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "تأكيد الدولة والمتابعة" else "Confirm & Continue", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.PHONE_INPUT -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "رقم الهاتف للتحقق الآمن" else "Phone Number for Verification",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "سنرسل رمز OTP عبر SMS للتحقق من هويتك ومنع الحسابات الوهمية" else "We will send an OTP via SMS to verify your identity",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text(if (isArabic) "رقم الهاتف مع رمز الدولة" else "Phone number with country code") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PetroleumGreen) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.OTP_VERIFY, userProfile.copy(phoneNumber = phoneNumber)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "إرسال رمز التحقق OTP" else "Send OTP Code", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.OTP_VERIFY -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "أدخل رمز التحقق (OTP)" else "Enter OTP Code",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "تم إرسال رمز التحقق المكون من 6 أرقام إلى $phoneNumber" else "Verification code sent to $phoneNumber",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { if (it.length <= 6) otpCode = it },
                                    label = { Text("6-Digit Code") },
                                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = RadiantGold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isArabic) "رمز تجريبي سريع: 123456 ✓" else "Sample OTP: 123456 ✓",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.ACCOUNT_CREATION, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "تأكيد الرمز وإنشاء الحساب" else "Verify Code & Create Account", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.ACCOUNT_CREATION -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "إنشاء الحساب والبيانات الأساسية" else "Account Details",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text(if (isArabic) "الاسم الكامل أو المستعار اللطيف" else "Full or Display Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PetroleumGreen) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(if (isArabic) "كلمة المرور الآمنة" else "Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PetroleumGreen) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { gender = "FEMALE" },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (gender == "FEMALE") RadiantGoldContainer else Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(if (isArabic) "أنثى (عروس) 🧕" else "Female 🧕", fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { gender = "MALE" },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (gender == "MALE") Color(0xFFEBF3F0) else Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(if (isArabic) "ذكر (عريس) 🤵" else "Male 🤵", fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        onStepCompleted(
                                            RegistrationFlowStep.COMMITMENT_AGREEMENT,
                                            userProfile.copy(name = name, gender = gender)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "متابعة لتعهد الالتزام الشرعي" else "Continue to Commitment", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.COMMITMENT_AGREEMENT -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(RadiantGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📜", fontSize = 30.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isArabic) "ميثاق وتعهد الالتزام بالضوابط الشرعية" else "Commitment & Ethical Pledge",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F0)),
                                    border = CardDefaults.outlinedCardBorder(),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = if (isArabic)
                                                "أتعهد أمام الله تعالى بأن هدفي من استخدام منصة (سوا سوا) هو الزواج الشرعي الحلال، وألتزم بالأخلاق الإسلامية والصدق في المعلومات، وحسن التعامل، وعدم الإساءة لأي طرف."
                                            else
                                                "I pledge before Allah that my sole intention on Sawa Sawa is halal Islamic marriage, upholding honesty and respectful communication.",
                                            fontSize = 13.sp,
                                            lineHeight = 22.sp,
                                            color = PetroleumGreenDark
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { commitmentAgreementChecked = !commitmentAgreementChecked }
                                ) {
                                    Checkbox(
                                        checked = commitmentAgreementChecked,
                                        onCheckedChange = { commitmentAgreementChecked = it },
                                        colors = CheckboxDefaults.colors(checkedColor = RadiantGoldDark)
                                    )
                                    Text(
                                        text = if (isArabic) "أوافق وأتعهد بالالتزام الكامل بالميثاق" else "I agree and solemnly commit to the pledge",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = PetroleumGreenDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        if (commitmentAgreementChecked) {
                                            onStepCompleted(
                                                RegistrationFlowStep.PROFILE_SETUP,
                                                userProfile.copy(hasAcceptedCommitmentAgreement = true)
                                            )
                                        }
                                    },
                                    enabled = commitmentAgreementChecked,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "تأكيد التعهد والمتابعة" else "Confirm Pledge & Continue", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.PROFILE_SETUP -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "تفاصيل الملف الشخصي" else "Profile Details",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text(if (isArabic) "المدينة" else "City") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = profession,
                                    onValueChange = { profession = it },
                                    label = { Text(if (isArabic) "المهنة / العمل" else "Profession") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = education,
                                    onValueChange = { education = it },
                                    label = { Text(if (isArabic) "المؤهل التعليمي" else "Education") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = marriageGoal,
                                    onValueChange = { marriageGoal = it },
                                    label = { Text(if (isArabic) "هدفي من الزواج وتطلعاتي" else "Marriage Goal") },
                                    maxLines = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = {
                                        onStepCompleted(
                                            RegistrationFlowStep.PHOTO_UPLOAD,
                                            userProfile.copy(
                                                city = city,
                                                profession = profession,
                                                education = education,
                                                marriageGoal = marriageGoal
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "حفظ ومتابعة إلى الصور" else "Save & Add Photos", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.PHOTO_UPLOAD -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "صور الملف الشخصي والخصوصية" else "Profile Photos & Privacy",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "يمكنك طمس وتغبيش صورك ولا تظهر للطرف الآخر إلا بإذنك الصريح" else "You can blur your photos so only approved candidates can view them",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEBF3F0))
                                        .border(2.dp, RadiantGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = PetroleumGreen,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isArabic) "تم ضبط الصورة الافتراضية المحتشمة بنجاح ✓" else "Default modest profile photo set ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.SELFIE_VERIFY, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "متابعة إلى التوثيق بالسيلفي" else "Continue to Selfie Verification", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.SELFIE_VERIFY -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(RadiantGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = RadiantGoldDark, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isArabic) "توثيق الهوية بالسيلفي (شارة التوثيق)" else "Selfie Identity Verification",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic)
                                        "صورة السيلفي مخصصة لمشرفي النظام فقط للتحقق من أن الحساب حقيقي، ولن تظهر نهائياً لأي مستخدم آخر."
                                    else
                                        "The selfie is strictly for admin identity review and will never be shown to other users.",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.PREFERENCES, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "بدء فحص السيلفي والمتابعة" else "Start Selfie Check & Next", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.PREFERENCES -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isArabic) "تفضيلات شريك الحياة" else "Partner Preferences",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isArabic)
                                        "نظام المطابقة الذكي يحسب نسبة التوافق الشرعي بناءً على أولوياتك في التدين، العمر، والمدينة."
                                    else
                                        "Matching engine uses your preferences to calculate spiritual and lifestyle compatibility.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.LOCATION_PERMISSION, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "متابعة لصلاحيات الموقع" else "Continue to Location", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.LOCATION_PERMISSION -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEBF3F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = PetroleumGreen, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isArabic) "صلاحية الموقع الجغرافي التقريبي" else "Location Permission",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic)
                                        "الموقع يُستخدم فقط لحساب المسافة وعرض من هم الأقرب إليك في مدينتك. لن يتم إظهار موقعك الدقيق لأي شخص على الإطلاق."
                                    else
                                        "Location is only used to compute proximity and nearest candidates. Exact coordinates are never revealed.",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "منح إذن الموقع والمتابعة" else "Grant Location & Continue", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                TextButton(
                                    onClick = { onStepCompleted(RegistrationFlowStep.NOTIFICATIONS_PERMISSION, userProfile) }
                                ) {
                                    Text(if (isArabic) "تخطي الآن والمتابعة يدوياً" else "Skip for now", color = Color.Gray)
                                }
                            }
                        }

                        RegistrationFlowStep.NOTIFICATIONS_PERMISSION -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(RadiantGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = RadiantGoldDark, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isArabic) "إشعارات التوافق والرسائل الجديدة" else "Notifications Permission",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic)
                                        "لتصلك تنبيهات فورية عندما يُعجب بك شخص متوافق، أو عند استلام رسائل وباقات الورد والمحادثات الشرعية."
                                    else
                                        "Get timely alerts when compatible candidates like your profile, or when you receive roses and messages.",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Button(
                                    onClick = {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "تفعيل الإشعارات وإنهاء التسجيل" else "Enable Notifications & Finish", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RegistrationFlowStep.COMPLETED -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(40.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isArabic) "تم إكمال تسجيلك بنجاح!" else "Registration Completed!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "مرحباً بك في مجتمع سوا سوا، نسأل الله لك التوفيق والبركة في رحلة الزواج الصالح." else "Welcome to Sawa Sawa. May Allah bless your journey to a pious marriage.",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(28.dp))
                                Button(
                                    onClick = { onStepCompleted(RegistrationFlowStep.COMPLETED, userProfile) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isArabic) "الدخول إلى المستكشف الآن 💍" else "Explore Matches Now 💍", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
