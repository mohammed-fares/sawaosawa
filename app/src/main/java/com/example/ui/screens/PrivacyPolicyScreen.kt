package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold

@Composable
fun PrivacyPolicyScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

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
                    modifier = Modifier.testTag("privacy_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PetroleumGreen
                    )
                }

                Text(
                    text = if (isArabic) "الخصوصية والشروط الشرعية" else "Privacy & Terms",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Intro Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PetroleumGreenContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(PetroleumGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "ميثاق الأمانة والخصوصية الشرعية" else "Charter of Privacy & Trust",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreenDark
                            )
                            Text(
                                text = if (isArabic)
                                    "تطبيق سوا سوا مصمم لحفظ العرض والكرامة والتعارف الحلال."
                                else
                                    "Sawa Sawa is designed for dignified halal matrimonial connections.",
                                fontSize = 11.sp,
                                color = PetroleumGreenDark.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // Section 1: الضوابط الشرعية للتعارف
                PrivacySectionCard(
                    title = if (isArabic) "1. الضوابط الشرعية للتعارف والزواج" else "1. Halal Matrimonial Guidelines",
                    icon = Icons.Default.Gavel,
                    content = if (isArabic)
                        "تطبيق سوا سوا مخصص حصراً للأشخاص الجادين في طلب الزواج الشرعي. يُحظر استخدام المنصة لأي غرض يخالف أحكام الشريعة الإسلامية أو الآداب العامة، ويتم حظر أي مستخدم يُسيء استخدام المنصة فوراً دون إنذار."
                    else
                        "Sawa Sawa is strictly dedicated to individuals seeking genuine halal marriage. Inappropriate conduct or non-matrimonial intentions result in immediate termination."
                )

                // Section 2: حماية وتشفير الصور
                PrivacySectionCard(
                    title = if (isArabic) "2. خصوصية الصور وتمويه الوجه" else "2. Photo Privacy & Face Blurring",
                    icon = Icons.Default.Lock,
                    content = if (isArabic)
                        "نمنحك التحكم التام في خصوصية صورك؛ حيث يمكنك تفعيل ميزة إخفاء الصور للعامة، ولن يتمكن أي طرف من مشاهدة صورك إلا بعد طلب الإذن وموافقتك الصريحة، كما نمنع ميزة التقاط الشاشة للحفاظ على الخصوصية."
                    else
                        "You maintain full control of your photos. You may keep photos blurred until mutual interest and explicit permission are granted."
                )

                // Section 3: ميزة الولي والمرافق
                PrivacySectionCard(
                    title = if (isArabic) "3. إشراف الولي والمرافق الشرعي (Wali)" else "3. Chaperone & Wali Oversight",
                    icon = Icons.Default.Shield,
                    content = if (isArabic)
                        "تتيح ميزة الولي إضافة البريد الإلكتروني أو رقم هاتف ولي الأمر (الأب، الأخ، أو المرافق) ليتلقى ملخصاً عن المحادثات بشفافية تامة وطمأنينة لجميع أفراد الأسرة."
                    else
                        "The Chaperone feature allows including a guardian's contact to receive transcript summaries for transparency and peace of mind."
                )

                // Section 4: حذف وتجميد الحساب
                PrivacySectionCard(
                    title = if (isArabic) "4. حق حذف الحساب والبيانات" else "4. Right to Deletion & Account Closure",
                    icon = Icons.Default.DeleteForever,
                    content = if (isArabic)
                        "لك كامل الحق في حذف حسابك وجميع بياناتك وصورك بشكل نهائي في أي وقت. بمجرد تأكيد الحذف يتم محو سجلاتك فوراً من خوادمنا."
                    else
                        "You retain the right to permanently delete your account, photos, and messages at any time without retention."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Delete Account Action
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .testTag("delete_account_button")
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "حذف الحساب نهائياً ومسح البيانات" else "Permanently Delete Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (isArabic) "تأكيد حذف الحساب نهائياً؟" else "Confirm Permanent Deletion?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "سيتم مسح جميع بياناتك وصورك ومحادثاتك نهائياً ولن تتمكن من استعادتها. هل أنت متأكد؟"
                    else
                        "All your data, photos, and messages will be permanently erased. Are you sure?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (isArabic) "نعم، احذف الحساب" else "Yes, Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text(if (isArabic) "إلغاء" else "Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun PrivacySectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PetroleumGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
