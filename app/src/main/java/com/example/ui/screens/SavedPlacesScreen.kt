package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun SavedPlacesScreen(
    state: BangkokUiState,
    onRemoveSavedPlace: (String) -> Unit,
    onRemoveSavedRoute: (String) -> Unit,
    onGoToPlace: (String) -> Unit
) {
    val isBurmese = state.isBurmese
    var selectedTab by remember { mutableStateOf("PLACES") } // PLACES or ROUTES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row (Saved Places vs Saved Routes)
        TabRow(
            selectedTabIndex = if (selectedTab == "PLACES") 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BangkokBlue
        ) {
            Tab(
                selected = selectedTab == "PLACES",
                onClick = { selectedTab = "PLACES" },
                text = {
                    Text(
                        text = if (isBurmese) "သိမ်းဆည်းထားသော နေရာများ (${state.savedPlaces.size})" else "สถานที่ที่บันทึกไว้",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_saved_places")
            )
            Tab(
                selected = selectedTab == "ROUTES",
                onClick = { selectedTab = "ROUTES" },
                text = {
                    Text(
                        text = if (isBurmese) "သိမ်းဆည်းထားသော လမ်းကြောင်းများ (${state.savedRoutes.size})" else "เส้นทางที่บันทึกไว้",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_saved_routes")
            )
        }

        // Offline Status Notice
        Surface(
            color = SuccessGreen.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBurmese) "အင်တာနက်မရှိဘဲ အော့ဖ်လိုင်း (Offline) အသုံးပြုနိုင်ပါသည်" else "สามารถดูข้อมูลได้แบบออฟไลน์โดยไม่ต้องใช้อินเทอร์เน็ต",
                    fontSize = 12.sp,
                    color = TextDark
                )
            }
        }

        if (selectedTab == "PLACES") {
            if (state.savedPlaces.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBurmese) "သိမ်းဆည်းထားသော နေရာ မရှိသေးပါ" else "ยังไม่มีสถานที่ที่บันทึกไว้",
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.savedPlaces) { place ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = place.nameMm, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = place.nameTh, fontSize = 13.sp, color = BangkokBlue)
                                    if (place.addressMm.isNotBlank()) {
                                        Text(text = place.addressMm, fontSize = 12.sp, color = TextMuted)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { onGoToPlace(place.nameMm) }) {
                                        Icon(imageVector = Icons.Default.Directions, contentDescription = "Go", tint = BangkokBlue)
                                    }
                                    IconButton(onClick = { onRemoveSavedPlace(place.id) }) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = ThaiRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (state.savedRoutes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Route, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBurmese) "သိမ်းဆည်းထားသော လမ်းကြောင်း မရှိသေးပါ" else "ยังไม่มีเส้นทางที่บันทึกไว้",
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.savedRoutes) { route ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = route.titleMm, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    IconButton(onClick = { onRemoveSavedRoute(route.id) }) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = ThaiRed)
                                    }
                                }
                                Text(text = "⏱ ${route.totalTimeMinutes} မိနစ် | 💰 ${route.fareThbText}", fontSize = 13.sp, color = BangkokBlue, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = route.stepsSummaryMm, fontSize = 12.sp, color = TextMuted, maxLines = 3)
                            }
                        }
                    }
                }
            }
        }
    }
}
