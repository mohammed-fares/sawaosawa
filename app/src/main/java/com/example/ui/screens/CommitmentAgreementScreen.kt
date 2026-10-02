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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun CommitmentAgreementScreen(
    user: CurrentUserProfile,
    language: AppLanguage,
    onAcceptAgreement: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var isAgreedChecked by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PetroleumGreenContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🤝", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic) "ميثاق الأمانة والالتزام" else "Commitment Charter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreenDark
                    )
                }

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.testTag("commitment_logout_button")
                ) {
                    Text(
                        text = if (isArabic) "تسجيل الخروج" else "Log Out",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcoming Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, RadiantGold.copy(alpha = 0.8f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        PetroleumGreenContainer.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🕊️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isArabic) "أهلاً بك يا ${user.name} في سوا سوا" else "Welcome ${user.name} to Sawa Sawa",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = PetroleumGreenDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic)
                                    "نسعد بانضمامك إلى مجتمع الزواج الإسلامي الهادف. خطوتك الأولى نحو بناء بيت مسلم مبارك قائم على المودة والرحمة والتقوى."
                                else
                                    "We are honored to have you join our halal matrimony community. Your first step toward a blessed Muslim family built on love, mercy, and piety.",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Charter Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = RadiantGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تعهد الالتزام بالضوابط والأخلاق الدينية والعامة" else "Religious & Community Ethics Pledge",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreenDark
                    )
                }

                // 5 Commitments List
                CommitmentItem(
                    emoji = "🕌",
                    title = if (isArabic) "1. النية الصادقة للزواج الحلال" else "1. Sincere Halal Intention",
                    description = if (isArabic)
                        "أتعهد بأن غرضي الوحيد من التسجيل هو التعارف الجاد بنية الزواج الشرعي، وأبرأ إلى الله من أي نية للتسلية أو تضييع الأوقات."
                    else
                        "I pledge that my sole intent is serious halal courtship for Islamic marriage, free from pastime or deceit."
                )

                CommitmentItem(
                    emoji = "🧕",
                    title = if (isArabic) "2. العفة وحسن الخلق في الحوار" else "2. Modesty & Islamic Decency",
                    description = if (isArabic)
                        "أتعهد بالتزام الحياء والآداب الإسلامية الرفيعة في المحادثات، وعدم استخدام أي ألفاظ خادشة أو طلب أو مشاركة أي محتوى غير لائق."
                    else
                        "I pledge to uphold dignity and modesty in all chats, never requesting or sharing inappropriate language or images."
                )

                CommitmentItem(
                    emoji = "✨",
                    title = if (isArabic) "3. الأمانة والصدق في البيانات والصور" else "3. Truthfulness in Details & Photos",
                    description = if (isArabic)
                        "أتعهد بأن جميع المعلومات والصور المرفوعة في حسابي حقيقية وصادقة وتطابق واقعي بدقة وأمانة تامة دون تضليل."
                    else
                        "I pledge that all details and photos on my profile are authentic, honest, and accurately reflect who I am."
                )

                CommitmentItem(
                    emoji = "🔒",
                    title = if (isArabic) "4. صون الأمانة واحترام خصوصية الأعضاء" else "4. Safeguarding Privacy & Trust",
                    description = if (isArabic)
                        "أتعهد بحفظ خصوصية جميع الأعضاء وأسرارهم، وعدم تصوير الشاشة (Screenshot) أو تداول أي معلومات أو صور خارج التطبيق بأي شكل."
                    else
                        "I pledge to preserve members' confidentiality, never taking screenshots or sharing chats or photos outside the app."
                )

                CommitmentItem(
                    emoji = "👨‍👩‍👧",
                    title = if (isArabic) "5. الترحيب بالولي والجدية في الارتباط" else "5. Wali Involvement & Official Steps",
                    description = if (isArabic)
                        "أرحب بإشراك ولي الأمر وتفعيل خيار الولي في المحادثة، والمسارعة إلى الخطوات الرسمية الشرعية فور حدوث القبول والتوافق المبدئي."
                    else
                        "I welcome chaperone/Wali involvement in communication and taking official formal steps upon mutual compatibility."
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Interactive Agreement Checkbox Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isAgreedChecked = !isAgreedChecked },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAgreedChecked) PetroleumGreenContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isAgreedChecked) 2.dp else 1.dp,
                        color = if (isAgreedChecked) PetroleumGreen else Color.LightGray.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isAgreedChecked,
                            onCheckedChange = { isAgreedChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PetroleumGreen,
                                uncheckedColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("commitment_checkbox")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic)
                                    "أتعهد أمام الله بالالتزام التام بالضوابط والأخلاق الدينية والعامة، وأوافق على هذا التعهد."
                                else
                                    "I solemnly pledge before Allah to adhere to all religious and community ethics, and agree to this charter.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAgreedChecked) PetroleumGreenDark else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = if (isArabic) "الموافقة شرط أساسي لتفعيل الحساب واستخدام التطبيق" else "Agreement is required to activate and use the app",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA Button Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onAcceptAgreement,
                        enabled = isAgreedChecked,
                        modifier = Modifier
                            .testTag("commitment_submit_button")
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PetroleumGreen,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "الموافقة والمتابعة إلى التطبيق 🤝" else "Agree & Proceed to App 🤝",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isAgreedChecked) {
                        Text(
                            text = if (isArabic)
                                "يرجى وضع علامة الصح في المربع أعلاه لتأكيد موافقتك على التعهد والمتابعة"
                            else
                                "Please check the box above to confirm your agreement and proceed",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommitmentItem(
    emoji: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PetroleumGreenContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreenDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
