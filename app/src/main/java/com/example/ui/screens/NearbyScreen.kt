package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportPlace
import com.example.data.model.TransportType
import com.example.ui.components.TransportPlaceCard
import com.example.ui.theme.BangkokBlue
import com.example.ui.theme.BurmeseGold
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun NearbyScreen(
    state: BangkokUiState,
    onRequestLocationPermission: () -> Unit,
    onSelectRadius: (Double) -> Unit,
    onSelectCategory: (TransportType?) -> Unit,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit
) {
    val isBurmese = state.isBurmese

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Filter Bar: GPS Status & Radius Buttons
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // GPS Status & Location Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BangkokBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.hasGpsPermission)
                                (if (isBurmese) "GPS တည်နေရာ: လက်ရှိနေရာ" else "พิกัด: ตำแหน่งปัจจุบัน")
                            else
                                (if (isBurmese) "ဗဟိုနေရာ: အောင်ဆန်းရတနာ" else "ตำแหน่ง: อนุสาวรีย์ชัยสมรภูมิ"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!state.hasGpsPermission) {
                        TextButton(
                            onClick = onRequestLocationPermission,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (isBurmese) "GPS ခွင့်ပြုမည်" else "เปิด GPS",
                                fontSize = 12.sp,
                                color = BangkokBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Radius Selector (500m, 1km, 3km, 5km)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val radiusOptions = listOf(
                        Pair(500.0, "500 m"),
                        Pair(1000.0, "1 km"),
                        Pair(3000.0, "3 km"),
                        Pair(5000.0, "5 km")
                    )

                    radiusOptions.forEach { (radius, label) ->
                        val isSelected = state.selectedRadiusMeters == radius
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectRadius(radius) },
                            label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BangkokBlue,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("radius_chip_${label.replace(" ", "_")}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedCategory == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text(text = if (isBurmese) "အားလုံး" else "ทั้งหมด", fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BurmeseGold,
                                selectedLabelColor = TextDark
                            )
                        )
                    }

                    val categories = listOf(
                        Pair(TransportType.BUS_STOP, if (isBurmese) "မှတ်တိုင်များ" else "ป้ายรถเมล์"),
                        Pair(TransportType.BTS, "BTS"),
                        Pair(TransportType.MRT, "MRT"),
                        Pair(TransportType.SRT, if (isBurmese) "မီးရထား" else "รถไฟ"),
                        Pair(TransportType.AIRPORT_RAIL_LINK, if (isBurmese) "လေဆိပ်ရထား" else "แอร์พอร์ตลิงก์"),
                        Pair(TransportType.BUS_TERMINAL, if (isBurmese) "ကားဂိတ်ကြီး" else "สถานีขนส่ง"),
                        Pair(TransportType.TAXI, if (isBurmese) "တက္ကစီ" else "แท็กซี่"),
                        Pair(TransportType.MOTORCYCLE_TAXI, if (isBurmese) "ဆိုင်ကယ်" else "วินมอเตอร์ไซค์"),
                        Pair(TransportType.HOSPITAL, if (isBurmese) "ဆေးရုံ" else "โรงพยาบาล"),
                        Pair(TransportType.COMMUNITY, if (isBurmese) "မြန်မာအသိုက်အဝန်း" else "ชุมชนเมียนมา")
                    )

                    items(categories) { (cat, label) ->
                        FilterChip(
                            selected = state.selectedCategory == cat,
                            onClick = { onSelectCategory(cat) },
                            label = { Text(text = label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BangkokBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // List of Nearby Places
        if (state.nearbyPlaces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isBurmese) "ဤအကွာအဝေးအတွင်း မှတ်တိုင်မတွေ့ပါ" else "ไม่พบสถานที่ในระยะที่เลือก",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBurmese) "ရှာဖွေမှု အကွာအဝေး (၃ ကီလို သို့မဟုတ် ၅ ကီလို) ကို တိုးမြှင့်ကြည့်ပါ" else "ลองขยายระยะค้นหาเป็น 3 กม. หรือ 5 กม.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (isBurmese)
                            "အနီးရှိ မှတ်တိုင်နှင့် နေရာပေါင်း (${state.nearbyPlaces.size}) ခု တွေ့ရှိပါသည်:"
                        else
                            "พบสถานที่ใกล้เคียงทั้งหมด (${state.nearbyPlaces.size}) แห่ง:",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted
                    )
                }

                items(state.nearbyPlaces) { (place, distanceMeters) ->
                    TransportPlaceCard(
                        place = place,
                        isBurmese = isBurmese,
                        distanceMeters = distanceMeters,
                        onCardClick = { onPlaceClick(place) },
                        onDirectionsClick = { onDirectionsClick(place) },
                        onSaveClick = { onSavePlace(place) },
                        onShowDriverClick = {
                            onShowDriverClick(
                                place.driverPhraseTh.ifBlank { "ไป ${place.nameTh} ครับ" },
                                place.nameMm
                            )
                        }
                    )
                }
            }
        }
    }
}
