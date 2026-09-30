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

            // Enhanced Spacious Sub-Tabs Row: "الأقرب إليك", "أعجب بك", "زار ملفك", "المفضلة"
            androidx.compose.material3.ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PetroleumGreen,
                edgePadding = 14.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab in tabPositions.indices) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PetroleumGreen,
                            height = 3.dp
                        )
                    }
                }
            ) {
                val tabsList = listOf(
                    Triple(if (isArabic) "الأقرب إليك" else "Nearest", "📍", null),
                    Triple(if (isArabic) "أعجب بك" else "Liked You", "💖", "64"),
                    Triple(if (isArabic) "زار ملفك" else "Visits", "👀", "18"),
                    Triple(if (isArabic) "المفضلة" else "Favorites", "⭐", "5")
                )

                tabsList.forEachIndexed { index, (label, emoji, count) ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PetroleumGreenContainer else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(text = emoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) PetroleumGreenDark else Color.DarkGray
                            )
                            if (count != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (index == 1) MuzzPink else RadiantGold)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = count,
                                        fontSize = 10.sp,
                                        color = if (index == 1) Color.White else PetroleumGreenDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
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
            .height(245.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = candidate.photoRes),
                contentDescription = candidate.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Refined smooth gradient overlay that starts near the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.88f)
                            ),
                            startY = 220f
                        )
                    )
            )

            // Top Row: Clean Distance Pill on Start, VIP / Favorite on End
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
                        .background(if (isProximityTab) PetroleumGreenDark.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.6f))
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
                                if (isArabic) "< 1 كم" else "< 1 km"
                            } else {
                                if (isArabic) "${String.format("%.1f", distanceKm)} كم" else "${String.format("%.1f", distanceKm)} km"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Top End: VIP badge if member, otherwise subtle favorite icon
                if (candidate.isVip || candidate.isGoldMember) {
                    VipBadgeOverlay(text = "VIP 👑")
                } else {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MuzzPink,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Bottom Info: Clean, well-spaced typography
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
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

                    // Online indicator
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(if (candidate.isOnlineNow) Color(0xFF4CAF50) else Color.Gray, CircleShape)
                    )
                }

                Text(
                    text = "${if (isArabic) candidate.profession else candidate.professionEn} • ${if (isArabic) candidate.city else candidate.cityEn}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.90f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
