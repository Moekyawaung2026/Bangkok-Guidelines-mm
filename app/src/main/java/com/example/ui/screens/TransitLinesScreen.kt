package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun TransitLinesScreen(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit
) {
    val isBurmese = state.isBurmese
    var selectedCategoryTab by remember(state.selectedTransitLineType) {
        mutableStateOf(state.selectedTransitLineType)
    }

    val tabs = listOf(
        Pair(TransportType.BTS, "BTS မိုးပျံ"),
        Pair(TransportType.MRT, "MRT မြေအောက်"),
        Pair(TransportType.AIRPORT_RAIL_LINK, "လေဆိပ်ရထား ARL"),
        Pair(TransportType.SRT, "SRT မီးရထား"),
        Pair(TransportType.BUS_TERMINAL, "ကားဂိတ်ကြီးများ")
    )

    val currentPlaces = places.filter { it.type == selectedCategoryTab }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Line Categories Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == selectedCategoryTab }.coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BangkokBlue,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabs.forEach { (type, labelMm) ->
                val isSelected = selectedCategoryTab == type
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategoryTab = type },
                    text = {
                        Text(
                            text = if (isBurmese) labelMm else type.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) BangkokBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.testTag("tab_transit_${type.name.lowercase()}")
                )
            }
        }

        // Subheader Info
        Surface(
            color = BangkokBlue.copy(alpha = 0.08f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = BangkokBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (selectedCategoryTab) {
                        TransportType.BTS -> if (isBurmese) "ဆူခုမ်ဝစ်လိုင်း (အစိမ်းရောင်)၊ ဆီလုံလိုင်း (အစိမ်းရင့်)၊ ရွှေရောင်လိုင်း (Gold)" else "สายสุขุมวิท สายสีลม และสายสีทอง"
                        TransportType.MRT -> if (isBurmese) "အပြာရောင်လိုင်း (Blue Line)၊ ခရမ်းရောင်လိုင်း၊ အဝါရောင်၊ ပန်းရောင်လိုင်း" else "สายสีน้ำเงิน สายสีม่วง สายสีเหลือง และสายสีชมพู"
                        TransportType.AIRPORT_RAIL_LINK -> if (isBurmese) "သုဝဏ္ဏဘူမိလေဆိပ်မှ မြို့တွင်းသို့ တိုက်ရိုက်ပြေးဆွဲသော ရထား" else "เชื่อมสนามบินสุวรรณภูมิเข้าสู่ใจกลางเมือง"
                        TransportType.SRT -> if (isBurmese) "ခရုန်ထိပ်အဖိဝပ် (ဘန်းဆူး) ဗဟိုဘူတာ၊ ဟွာလမ်ဖောင်၊ ဒွန်မောင်းရထား" else "สถานีกลางกรุงเทพอภิวัฒน์ หัวลำโพง และสายสีแดงดอนเมือง"
                        TransportType.BUS_TERMINAL -> if (isBurmese) "မိုချစ် ၂ (မြောက်ပိုင်း)၊ ဧကမိုင်း (အရှေ့ပိုင်း)၊ ဆိုင်တိုင်းမိုင် (တောင်ပိုင်း)" else "หมอชิต 2 เอกมัย สายใต้ใหม่ และคิวรถตู้"
                        else -> ""
                    },
                    fontSize = 12.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Station & Terminal Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentPlaces) { place ->
                TransportPlaceCard(
                    place = place,
                    isBurmese = isBurmese,
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
