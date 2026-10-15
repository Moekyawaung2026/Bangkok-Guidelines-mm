package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusRouteInfo
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun BusGuideScreen(
    state: BangkokUiState,
    busRoutes: List<BusRouteInfo>,
    onSelectBusNumber: (String) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onSpeakThai: (String) -> Unit
) {
    val isBurmese = state.isBurmese
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, AC, EXPRESS, NIGHT

    val filteredRoutes = busRoutes.filter { route ->
        val matchesQuery = searchQuery.isBlank() ||
                route.busNumber.contains(searchQuery.trim()) ||
                route.routeNameMm.contains(searchQuery.trim()) ||
                route.routeNameTh.contains(searchQuery.trim())

        val matchesFilter = when (selectedFilter) {
            "AC" -> route.isAirConditioned
            "EXPRESS" -> route.isExpressway
            "NIGHT" -> route.isNightBus
            else -> true
        }
        matchesQuery && matchesFilter
    }

    val selectedRoute = busRoutes.find { it.busNumber == state.selectedBusNumber } ?: busRoutes.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Bus Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_bus_search"),
                shape = RoundedCornerShape(14.dp),
                placeholder = {
                    Text(
                        text = if (isBurmese) "ဘတ်စ်ကားနံပါတ် သို့မဟုတ် နေရာ ရှာရန် (ဥပမာ- 12, 140, 511)" else "ค้นหาสายรถเมล์ (เช่น 12, 140, 511)",
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DirectionsBus, contentDescription = null, tint = BangkokBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
        }

        // 2. Bus Type Filter Chips (Normal, AC, Expressway, Night)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Pair("ALL", if (isBurmese) "အားလုံး" else "ทั้งหมด"),
                    Pair("AC", if (isBurmese) "လေအေးပေးစက် (AC)" else "รถปรับอากาศ"),
                    Pair("EXPRESS", if (isBurmese) "အမြန်လမ်း (Express)" else "ขึ้นทางด่วน"),
                    Pair("NIGHT", if (isBurmese) "ညဘက်လိုင်း (Night)" else "รถกะดึก 24 ชม.")
                )
                items(filters) { (code, label) ->
                    FilterChip(
                        selected = selectedFilter == code,
                        onClick = { selectedFilter = code },
                        label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BangkokBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }

        // 3. Quick Bus Number Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRoutes) { route ->
                    val isSelected = route.busNumber == selectedRoute?.busNumber
                    Surface(
                        modifier = Modifier
                            .clickable { onSelectBusNumber(route.busNumber) }
                            .testTag("chip_bus_${route.busNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BurmeseGold else MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bus ${route.busNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }

        // 4. Selected Bus Route Detail Card
        if (selectedRoute != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_bus_detail"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Bus Number & Route Type Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BangkokBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedRoute.busNumber,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Bus ${selectedRoute.busNumber}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = selectedRoute.busTypeMm,
                                        fontSize = 12.sp,
                                        color = BangkokBlueDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Fare pill
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = selectedRoute.fareThb,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Route Names (Burmese & Thai)
                        Text(
                            text = selectedRoute.routeNameMm,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedRoute.routeNameTh,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BangkokBlue
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Operating Hours
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ပြေးဆွဲချိန်: ${selectedRoute.operatingHours}",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // CRITICAL: “ဘယ်ဘက်က မှတ်တိုင်မှာ စောင့်ရမလဲ” (Which side bus stop to wait)
                        Surface(
                            color = BurmeseGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Signpost,
                                        contentDescription = null,
                                        tint = TextDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBurmese) "ဘယ်ဘက်က မှတ်တိုင်မှာ စောင့်ရမလဲ?" else "ต้องรอรถที่ป้ายฝั่งไหน?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBurmese) selectedRoute.waitingSideMm else selectedRoute.waitingSideTh,
                                    fontSize = 13.sp,
                                    color = TextDark,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Inbound & Outbound directions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⬆️ အသွား: ${selectedRoute.outboundDirectionMm}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "⬇️ အပြန်: ${selectedRoute.inboundDirectionMm}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Stops List
                        Text(
                            text = if (isBurmese) "အဓိကမှတ်တိုင်များ (Key Stops):" else "ป้ายจอดสำคัญ:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        selectedRoute.keyStopsMm.forEachIndexed { idx, stopMm ->
                            val stopTh = selectedRoute.keyStopsTh.getOrNull(idx) ?: ""
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BangkokBlue)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stopMm,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (stopTh.isNotBlank()) {
                                        Text(
                                            text = stopTh,
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Flashcard for Bus Conductor
                        Button(
                            onClick = {
                                onShowDriverClick(
                                    "ไป ${selectedRoute.routeNameTh} รถเมล์สาย ${selectedRoute.busNumber} ครับ",
                                    "ဘတ်စ်ကား ${selectedRoute.busNumber} ဖြင့် သွားလိုပါသည်"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_bus_conductor_flashcard"),
                            colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) "လက်မှတ်ဖြတ်သူ/ယာဉ်မောင်းအား ပြရန်" else "ให้กระเป๋ารถเมล์ดู",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
