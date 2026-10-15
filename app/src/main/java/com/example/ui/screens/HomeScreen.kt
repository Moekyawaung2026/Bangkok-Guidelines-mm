package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportPlace
import com.example.data.model.TransportType
import com.example.ui.components.TransportPlaceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun HomeScreen(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onNavigateTab: (AppTab) -> Unit,
    onGpsClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickDestinationSelect: (String) -> Unit,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onAiAssistantClick: () -> Unit
) {
    val isBurmese = state.isBurmese

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Core Promise Card / Hero Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BangkokBlue)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = BurmeseGold,
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsTransit,
                                    contentDescription = null,
                                    tint = TextDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isBurmese) "ဘန်ကောက်သွားလာရေးလမ်းညွှန်" else "คู่มือการเดินทางกรุงเทพฯ สำหรับแรงงานเมียนมา",
                            style = MaterialTheme.typography.titleSmall,
                            color = BurmeseGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isBurmese)
                            "“သင်ရှိနေသောနေရာမှ သွားလိုသောနေရာသို့ အလွယ်ဆုံး၊ အသက်သာဆုံး၊ လုံခြုံဆုံး လမ်းကြောင်းကို မြန်မာလို ရှာဖွေပါ။”"
                        else
                            "“ค้นหาเส้นทางที่ง่ายที่สุด ประหยัดที่สุด และปลอดภัยที่สุดจากจุดที่คุณอยู่เป็นภาษาเมียนมา”",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // 2. Large GPS Button (Minimum 56dp height, high contrast, pulsing icon)
        item {
            Button(
                onClick = onGpsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("btn_large_gps_search"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurmeseGold,
                    contentColor = TextDark
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "GPS Detection",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = if (isBurmese) "ကျွန်ုပ်ရှိနေသောနေရာအနီး ရှာမည်" else "ค้นหาสถานที่ใกล้ตำแหน่งของฉัน",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = if (isBurmese) "GPS တည်နေရာဖြင့် အနီးဆုံးမှတ်တိုင်များရှာရန်" else "แตะเพื่อตรวจจับพิกัดปัจจุบัน",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // 3. Search Field: “ဘယ်ကိုသွားမလဲ?” (Where to go?)
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .testTag("input_search_destination"),
                shape = RoundedCornerShape(14.dp),
                placeholder = {
                    Text(
                        text = if (isBurmese) "ဘယ်ကိုသွားမလဲ? (စျေး၊ ရထား၊ မှတ်တိုင်...)" else "จะไปที่ไหน? (ตลาด, สถานี, ป้ายรถ...)",
                        fontSize = 15.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BangkokBlue
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = onAiAssistantClick,
                        modifier = Modifier.testTag("btn_voice_ai_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Search / AI Assistant",
                            tint = BangkokBlue
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BangkokBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
        }

        // 4. Quick Destination Chips
        item {
            Column {
                Text(
                    text = if (isBurmese) "လူသွားများသော နေရာများ:" else "สถานที่ยอดนิยม:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickDestinations = listOf(
                        "ဆယမ် (Siam)",
                        "မဟာချိုင် (Mahachai)",
                        "အောင်ဆန်းရတနာ (Victory Monument)",
                        "စမွတ်ပရာကန် (Samut Prakan)",
                        "မိုချစ် ၂ ကားဂိတ် (Mo Chit 2)",
                        "ပရာတူနမ်စျေး (Pratunam)",
                        "သုဝဏ္ဏဘူမိလေဆိပ်",
                        "ဒွန်မောင်းလေဆိပ်",
                        "ဘန်းဆူးဗဟိုဘူတာ"
                    )
                    items(quickDestinations) { dest ->
                        SuggestionChip(
                            onClick = { onQuickDestinationSelect(dest) },
                            label = { Text(text = dest, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.height(38.dp)
                        )
                    }
                }
            }
        }

        // 5. Transport Quick Actions Grid (10 categories with Icon + Text, height >= 48dp)
        item {
            Text(
                text = if (isBurmese) "ယာဉ်လိုင်းနှင့် နေရာကဏ္ဍများ" else "หมวดหมู่การเดินทางและสถานที่",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            val actionItems = listOf(
                QuickActionItem(
                    titleMm = "ဘတ်စ်ကား",
                    titleTh = "รถเมล์",
                    icon = Icons.Default.DirectionsBus,
                    bg = Color(0xFFEDE7F6),
                    color = Color(0xFF512DA8),
                    onClick = { onNavigateTab(AppTab.BUS) }
                ),
                QuickActionItem(
                    titleMm = "BTS မိုးပျံရထား",
                    titleTh = "BTS",
                    icon = Icons.Default.Train,
                    bg = Color(0xFFE8F5E9),
                    color = Color(0xFF2E7D32),
                    onClick = { onNavigateTab(AppTab.LINES) }
                ),
                QuickActionItem(
                    titleMm = "MRT မြေအောက်",
                    titleTh = "MRT",
                    icon = Icons.Default.Subway,
                    bg = Color(0xFFE3F2FD),
                    color = Color(0xFF1565C0),
                    onClick = { onNavigateTab(AppTab.LINES) }
                ),
                QuickActionItem(
                    titleMm = "မီးရထား (SRT)",
                    titleTh = "รถไฟ",
                    icon = Icons.Default.DirectionsRailway,
                    bg = Color(0xFFFFF3E0),
                    color = Color(0xFFE65100),
                    onClick = { onNavigateTab(AppTab.LINES) }
                ),
                QuickActionItem(
                    titleMm = "တက္ကစီ",
                    titleTh = "แท็กซี่",
                    icon = Icons.Default.LocalTaxi,
                    bg = Color(0xFFFFFDE7),
                    color = Color(0xFFF57F17),
                    onClick = { onNavigateTab(AppTab.TAXI) }
                ),
                QuickActionItem(
                    titleMm = "ဆိုင်ကယ်ကယ်ရီ",
                    titleTh = "วินมอไซค์",
                    icon = Icons.Default.TwoWheeler,
                    bg = Color(0xFFFFECB3),
                    color = Color(0xFFFF6F00),
                    onClick = { onNavigateTab(AppTab.TAXI) }
                ),
                QuickActionItem(
                    titleMm = "ကားဂိတ်ကြီး",
                    titleTh = "สถานีขนส่ง",
                    icon = Icons.Default.DepartureBoard,
                    bg = Color(0xFFE0F7FA),
                    color = Color(0xFF00838F),
                    onClick = { onNavigateTab(AppTab.LINES) }
                ),
                QuickActionItem(
                    titleMm = "မှတ်တိုင်များ",
                    titleTh = "ป้ายรถเมล์",
                    icon = Icons.Default.DirectionsBusFilled,
                    bg = Color(0xFFF3E5F5),
                    color = Color(0xFF7B1FA2),
                    onClick = { onNavigateTab(AppTab.NEARBY) }
                ),
                QuickActionItem(
                    titleMm = "ဆေးရုံ",
                    titleTh = "โรงพยาบาล",
                    icon = Icons.Default.LocalHospital,
                    bg = Color(0xFFFFEBEE),
                    color = Color(0xFFC62828),
                    onClick = { onNavigateTab(AppTab.NEARBY) }
                ),
                QuickActionItem(
                    titleMm = "အလုပ်ရုံ / စက်ရုံ",
                    titleTh = "โรงงาน / ที่ทำงาน",
                    icon = Icons.Default.Factory,
                    bg = Color(0xFFECEFF1),
                    color = Color(0xFF37474F),
                    onClick = { onNavigateTab(AppTab.NEARBY) }
                )
            )

            // Render in 2 columns
            for (i in actionItems.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val item1 = actionItems[i]
                    QuickActionButton(
                        item = item1,
                        isBurmese = isBurmese,
                        modifier = Modifier.weight(1f)
                    )

                    if (i + 1 < actionItems.size) {
                        val item2 = actionItems[i + 1]
                        QuickActionButton(
                            item = item2,
                            isBurmese = isBurmese,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 6. Safety Alert Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(AppTab.SAFETY) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ThaiRed.copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ThaiRed.copy(alpha = 0.3f)))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ThaiRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBurmese) "အလုပ်သမား လုံခြုံရေးနှင့် အရေးပေါ် 191" else "ความปลอดภัยและสายด่วนฉุกเฉิน 191",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ThaiRed
                        )
                        Text(
                            text = if (isBurmese) "ဆိုင်ကယ်ဦးထုပ်ဆောင်းရန်၊ မီတာဖွင့်စီးရန်၊ ပတ်စ်ပို့မူရင်း မပေးရန် အကြံပြုချက်များ" else "ข้อควรระวัง สวมหมวกกันน็อก กดมิเตอร์ และสายด่วนสถานทูต",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark
                        )
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = ThaiRed)
                }
            }
        }

        // 7. Important Transport Locations Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBurmese) "အရေးကြီးသော ပို့ဆောင်ရေးဗဟိုဌာနများ" else "ศูนย์กลางการเดินทางสำคัญ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { onNavigateTab(AppTab.NEARBY) }) {
                    Text(text = if (isBurmese) "အားလုံးကြည့်မည်" else "ดูทั้งหมด", color = BangkokBlue)
                }
            }
        }

        val displayPlaces = if (state.searchQuery.isNotBlank()) state.searchResults else places.take(5)
        items(displayPlaces) { place ->
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

private data class QuickActionItem(
    val titleMm: String,
    val titleTh: String,
    val icon: ImageVector,
    val bg: Color,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionButton(
    item: QuickActionItem,
    isBurmese: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = item.onClick)
            .testTag("action_${item.titleTh.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isBurmese) item.titleMm else item.titleTh,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
