package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LocationHelper
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.theme.MuzzPink
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark
import com.example.ui.components.VipBadgeOverlay

@Composable
fun CommunityScreen(
    candidates: List<CandidateProfile>,
    language: AppLanguage,
    userLatitude: Double,
    userLongitude: Double,
    isGpsActive: Boolean,
    onCandidateClick: (CandidateProfile) -> Unit,
    onLocationUpdated: (Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    val context = LocalContext.current

    // Tabs: 0: Proximity (الأقرب إليك), 1: Liked You (أعجب بك), 2: Visited (قام بزيارتك), 3: Favorites (المفضلة)
    var selectedTab by remember { mutableIntStateOf(0) }
    var locationStatusText by remember {
        mutableStateOf(
            if (isGpsActive) "📍 تم تحديد موقعك الجغرافي عبر GPS بدقة"
            else "📍 انقر لتفعيل المطابقة الدقيقة حسب الأقرب لموقعك"
        )
    }

    // Permission launcher for location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            LocationHelper.fetchCurrentLocation(
                context = context,
                onSuccess = { lat, lon, _ ->
                    locationStatusText = "📍 تم التحديث: خط العرض ${String.format("%.3f", lat)}، خط الطول ${String.format("%.3f", lon)}"
                    onLocationUpdated(lat, lon, "تم التحديد عبر GPS")
                },
                onError = { err ->
                    locationStatusText = "⚠️ $err"
                }
            )
        } else {
            locationStatusText = "⚠️ تم رفض صلاحية الموقع، يُعرض موقع القاهرة الافتراضي"
        }
    }

    // Auto-fetch location if already has permission
    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            LocationHelper.fetchCurrentLocation(
                context = context,
                onSuccess = { lat, lon, _ ->
                    onLocationUpdated(lat, lon, "تم التحديد عبر GPS")
                },
                onError = {}
            )
        }
    }

    // Sort candidates by proximity if proximity tab is selected
    val displayCandidates = remember(candidates, selectedTab, userLatitude, userLongitude) {
        if (selectedTab == 0) {
            candidates.sortedBy { candidate ->
                LocationHelper.calculateDistanceKm(
                    userLatitude,
                    userLongitude,
                    candidate.latitude,
                    candidate.longitude
                )
            }
        } else {
            candidates
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isArabic) "المستكشف والمطابقة" else "Explorer & Match",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreenDark
                    )
                    Text(
                        text = if (isArabic) "المطابقة حسب الأقرب جغرافياً والأكثر توافقاً" else "Match by proximity & compatibility",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // GPS Refresh Button
                IconButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier
                        .testTag("refresh_gps_button")
                        .size(38.dp)
                        .background(if (isGpsActive) RadiantGoldContainer else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS",
                        tint = if (isGpsActive) RadiantGoldDark else PetroleumGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Proximity GPS banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGpsActive) PetroleumGreenContainer.copy(alpha = 0.4f) else RadiantGoldContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = PetroleumGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = locationStatusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PetroleumGreenDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (!isGpsActive) {
                        Text(
                            text = if (isArabic) "تفعيل GPS" else "Enable",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreen,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tabs Row: "الأقرب لك 📍" | "أعجب بك (64)" | "قام بزيارتك" | "المفضلة"
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = PetroleumGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PetroleumGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isArabic) "الأقرب لك 📍" else "Nearest 📍",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "أعجب بك" else "Liked You",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MuzzPink)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "64",
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = if (isArabic) "زارك" else "Visits",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Text(
                            text = if (isArabic) "المفضلة" else "Favorites",
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2x2 Grid of candidates with dynamic proximity distance & presence
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayCandidates) { candidate ->
                    val distKm = LocationHelper.calculateDistanceKm(
                        userLatitude,
                        userLongitude,
                        candidate.latitude,
                        candidate.longitude
                    )

                    CandidateGridCard(
                        candidate = candidate,
                        distanceKm = distKm,
                        isArabic = isArabic,
                        isProximityTab = selectedTab == 0,
                        onClick = { onCandidateClick(candidate) }
                    )
                }
            }
        }
    }
}

@Composable
fun CandidateGridCard(
    candidate: CandidateProfile,
    distanceKm: Float,
    isArabic: Boolean,
    isProximityTab: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = candidate.photoRes),
                contentDescription = candidate.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.90f)),
                            startY = 130f
                        )
                    )
            )

            // Top Badges: Distance Pill & Favorite Heart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Proximity Distance Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isProximityTab) PetroleumGreenDark.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (distanceKm < 1f) {
                                if (isArabic) "أقل من 1 كم" else "< 1 km"
                            } else {
                                if (isArabic) "${String.format("%.1f", distanceKm)} كم" else "${String.format("%.1f", distanceKm)} km"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Heart / Match badge & VIP Overlay
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (candidate.isVip || candidate.isGoldMember) {
                        VipBadgeOverlay(text = "VIP 👑")
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(MuzzPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Bottom Info: Name, Age, Profession, City & Active Hours
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${if (isArabic) candidate.name else candidate.nameEn}، ${candidate.age}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (candidate.isVerified) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "${if (isArabic) candidate.profession else candidate.professionEn} • ${if (isArabic) candidate.city else candidate.cityEn}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Active hours / Online indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (candidate.isOnlineNow) Color(0xFF4CAF50) else Color.LightGray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (candidate.isOnlineNow) {
                            if (isArabic) "متصل الآن" else "Online"
                        } else {
                            candidate.activeHours
                        },
                        fontSize = 9.sp,
                        color = if (candidate.isOnlineNow) Color(0xFFA5D6A7) else Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
