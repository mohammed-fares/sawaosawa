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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldLight

@Composable
fun OnboardingScreen(
    language: AppLanguage,
    onContinue: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    Box(modifier = modifier.fillMaxSize()) {
        // Hero Background Image with couple
        Image(
            painter = painterResource(id = R.drawable.hero_couple),
            contentDescription = "سوا سوا - زواج إسلامي",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for smooth readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.95f)
                        ),
                        startY = 0f
                    )
                )
        )

        // Top Language & Help Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language selector button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "English" else "العربية",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Help",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Center / Bottom Brand & Login actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Infinity ring emblem
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(PetroleumGreen, RadiantGold)),
                        shape = CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Brand Title "سوا سوا" / "SAWA SAWA"
            Text(
                text = if (isArabic) "سوا سوا" else "SAWA SAWA",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = if (isArabic) 0.sp else 3.sp
            )

            // Tagline: "حيث يلتقي المسلمون للزواج" / "Where Muslims Meet To Marry"
            Text(
                text = if (isArabic) "حيث يلتقي المسلمون للزواج الحلال" else "Where Muslims Meet To Marry",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = RadiantGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 26.dp)
            )

            // Social & Auth Action Buttons matching reference image
            // 1. Continue with Apple
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .testTag("continue_apple_button")
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (isArabic) "المتابعة باستخدام Apple" else "Continue with Apple",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Continue with Google
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .testTag("continue_google_button")
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F)
                )
            ) {
                Text(
                    text = if (isArabic) "المتابعة باستخدام Google" else "Continue with Google",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Continue with Email / Phone
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier
                    .testTag("continue_email_button")
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(RadiantGold, PetroleumGreen))),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = RadiantGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "المتابعة بالبريد أو رقم الجوال" else "Continue with email",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Privacy and Terms disclaimer
            Text(
                text = if (isArabic)
                    "بالمتابعة فإنك توافق على شروط الخدمة وسياسة الخصوصية لتطبيق سوا سوا الإسلامي"
                else
                    "By continuing you agree to our Terms and Privacy Policy",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
