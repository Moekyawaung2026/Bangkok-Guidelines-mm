package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MultimodalRoute
import com.example.data.model.TransitHub
import com.example.data.model.TransportType
import com.example.ui.components.HubSelectionTarget
import com.example.ui.components.TransitHubSelectionSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

/**
 * Localization data model supporting Burmese, Thai, and English
 */
enum class PlannerLanguage(val code: String, val displayName: String, val flagEmoji: String) {
    BURMESE("my", "မြန်မာ", "🇲🇲"),
    THAI("th", "ไทย", "🇹🇭"),
    ENGLISH("en", "English", "🇬🇧")
}

data class LocalizedString(
    val my: String, // Burmese
    val th: String, // Thai
    val en: String  // English
) {
    fun get(lang: PlannerLanguage): String = when (lang) {
        PlannerLanguage.BURMESE -> my
        PlannerLanguage.THAI -> th
        PlannerLanguage.ENGLISH -> en
    }
}

object RoutePlannerLocalization {
    val screenTitle = LocalizedString(
        my = "ခရီးစဉ် လမ်းကြောင်း ရှာဖွေရေး",
        th = "วางแผนการเดินทาง",
        en = "Route Planner"
    )
    val screenSubtitle = LocalizedString(
        my = "အလွယ်ဆုံး၊ အသက်သာဆုံး၊ လုံခြုံဆုံး လမ်းကြောင်းကို ရှာဖွေပါ",
        th = "ค้นหาเส้นทางที่ง่าย ประหยัด และปลอดภัยที่สุด",
        en = "Find the easiest, cheapest, and safest routes in Bangkok"
    )
    val startPointLabel = LocalizedString(
        my = "စတင်မည့်နေရာ",
        th = "จุดเริ่มต้น",
        en = "Start Point"
    )
    val startPointPlaceholder = LocalizedString(
        my = "စတင်မည့်နေရာ ရှာဖွေပါ သို့မဟုတ် ရွေးချယ်ပါ...",
        th = "ค้นหาหรือเลือกจุดเริ่มต้น...",
        en = "Search or select start point..."
    )
    val destinationLabel = LocalizedString(
        my = "သွားလိုသောနေရာ",
        th = "จุดหมายปลายทาง",
        en = "Destination"
    )
    val destinationPlaceholder = LocalizedString(
        my = "သွားလိုသောနေရာ ရှာဖွေပါ သို့မဟုတ် ရွေးချယ်ပါ...",
        th = "ค้นหาหรือเลือกจุดหมายปลายทาง...",
        en = "Search or select destination..."
    )
    val switchDirections = LocalizedString(
        my = "လမ်းကြောင်း ပြောင်းပြန်လှန်မည်",
        th = "สลับทิศทาง",
        en = "Switch Directions"
    )
    val transportModeLabel = LocalizedString(
        my = "သွားလာမည့်ပုံစံ",
        th = "รูปแบบการเดินทาง",
        en = "Transport Mode"
    )
    val selectModeDropdownPrompt = LocalizedString(
        my = "ပုံစံ ရွေးချယ်ပါ",
        th = "เลือกโหมดการเดินทาง",
        en = "Select Transport Mode"
    )
    val useCurrentLocation = LocalizedString(
        my = "လက်ရှိတည်နေရာ",
        th = "ตำแหน่งปัจจุบัน",
        en = "Current Location"
    )
    val selectTransitHub = LocalizedString(
        my = "ဘူတာစုံမှရွေးရန်",
        th = "เลือกจากศูนย์กลางขนส่ง",
        en = "Transit Hubs"
    )
    val popularHubsTitle = LocalizedString(
        my = "လူကြိုက်များသော ဘူတာနှင့် ဂိတ်ကြီးများ:",
        th = "ศูนย์กลางการเดินทางยอดนิยม:",
        en = "Popular Transit Hubs:"
    )
    val viewAll = LocalizedString(
        my = "အားလုံးကြည့်မည်",
        th = "ดูทั้งหมด",
        en = "View All"
    )
    val recommendedRoutesTitle = LocalizedString(
        my = "အကြံပြုထားသော လမ်းကြောင်းများ",
        th = "เส้นทางแนะนำ",
        en = "Recommended Routes"
    )
    val totalTimeLabel = LocalizedString(
        my = "စုစုပေါင်းအချိန်",
        th = "เวลาเดินทาง",
        en = "Total Time"
    )
    val estimatedFareLabel = LocalizedString(
        my = "ခန့်မှန်းယာဉ်ခ",
        th = "ค่าโดยสาร",
        en = "Est. Fare"
    )
    val walkingDistanceLabel = LocalizedString(
        my = "လမ်းလျှောက်ရန်",
        th = "ระยะเดิน",
        en = "Walking"
    )
    val stepByStepTitle = LocalizedString(
        my = "အဆင့်လိုက် သွားလာနည်း:",
        th = "ขั้นตอนการเดินทาง:",
        en = "Step-by-Step Directions:"
    )
    val showDriverBanner = LocalizedString(
        my = "ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား:",
        th = "ข้อความบอกคนขับ:",
        en = "Show to Driver (Thai):"
    )
    val safetyWarningTitle = LocalizedString(
        my = "သတိပြုရန်:",
        th = "ข้อควรระวัง:",
        en = "Safety Notice:"
    )
    val saveRouteButton = LocalizedString(
        my = "လမ်းကြောင်းသိမ်းမည်",
        th = "บันทึกเส้นทาง",
        en = "Save Route"
    )
    val minutesUnit = LocalizedString(
        my = "မိနစ်",
        th = "นาที",
        en = "min"
    )
    val metersUnit = LocalizedString(
        my = "မီတာ",
        th = "เมตร",
        en = "m"
    )
    val thbUnit = LocalizedString(
        my = "ဘတ်",
        th = "บาท",
        en = "THB"
    )
}

data class TransportModeOption(
    val id: Int,
    val iconEmoji: String,
    val name: LocalizedString,
    val description: LocalizedString
)

val transportModeOptions = listOf(
    TransportModeOption(
        id = 0,
        iconEmoji = "💰",
        name = LocalizedString("အသက်သာဆုံး", "ประหยัดที่สุด", "Cheapest"),
        description = LocalizedString("ဘတ်စ်ကားနှင့် ရိုးရိုးရထားများဖြင့် ကုန်ကျစရိတ် အနည်းဆုံး", "เน้นรถเมล์และรถไฟธรรมดา ค่าใช้จ่ายต่ำสุด", "Lowest fare with BMTA buses and regular trains")
    ),
    TransportModeOption(
        id = 1,
        iconEmoji = "⚡",
        name = LocalizedString("အမြန်ဆုံး", "เร็วที่สุด", "Fastest"),
        description = LocalizedString("BTS/MRT မိုးပျံရထားနှင့် အမြန်လမ်းလိုင်းများ", "ใช้ BTS/MRT และทางด่วนเพื่อความรวดเร็ว", "Quickest travel via BTS/MRT and expressway routes")
    ),
    TransportModeOption(
        id = 2,
        iconEmoji = "🚶",
        name = LocalizedString("လမ်းလျှောက်နည်း", "เดินน้อยที่สุด", "Least Walking"),
        description = LocalizedString("လမ်းလျှောက်ရမှု အနည်းဆုံးနှင့် အနီးဆုံးမှတ်တိုင်များ", "ลดระยะทางเดินเท้า เชื่อมต่อจุดรับส่งใกล้สุด", "Minimize walking distance and foot transfers")
    ),
    TransportModeOption(
        id = 3,
        iconEmoji = "🔄",
        name = LocalizedString("အပြောင်းအရွှေ့နည်း", "ต่อน้อยที่สุด", "Fewest Transfers"),
        description = LocalizedString("ကားပြောင်းရမှု အနည်းဆုံး တိုက်ရိုက်လမ်းကြောင်းများ", "เส้นทางตรงหรือเปลี่ยนสายน้อยที่สุด", "Direct lines with fewest changes of transport")
    ),
    TransportModeOption(
        id = 4,
        iconEmoji = "♿",
        name = LocalizedString("ဘီးတပ်/သက်ကြီးအဆင်ပြေ", "ทางวีลแชร์ / ผู้สูงอายุ", "Wheelchair Accessible"),
        description = LocalizedString("ဓာတ်လှေကားနှင့် လမ်းပြေများပါဝင်သော ဘူတာများ", "สถานีที่มีลิฟต์ บันไดเลื่อน และทางลาด", "Stations with elevators, ramps, and accessible facilities")
    ),
    TransportModeOption(
        id = 5,
        iconEmoji = "🌙",
        name = LocalizedString("ညဘက်လိုင်း (၂၄ နာရီ)", "รอบดึก 24 ชม.", "Night Service (24h)"),
        description = LocalizedString("ညအချိန် ၂၄ နာရီပြေးဆွဲသော ဘတ်စ်ကားလိုင်းများ", "รถเมล์สายกลางคืนที่วิ่งตลอด 24 ชั่วโมง", "24-hour overnight bus routes and night transit")
    ),
    TransportModeOption(
        id = 6,
        iconEmoji = "🛵",
        name = LocalizedString("ဆိုင်ကယ်ကယ်ရီ + ရထား", "วินมอเตอร์ไซค์ + รถไฟฟ้า", "Motorcycle Taxi + Train"),
        description = LocalizedString("ကြားလမ်းများမှ အမြန်ရထားဘူတာသို့ ကယ်ရီဖြင့် ဆက်သွယ်ခြင်း", "นั่งวินเข้าซอยต่อรถไฟฟ้า BTS/MRT", "Quick motorcycle taxi feeder connecting to BTS/MRT")
    ),
    TransportModeOption(
        id = 7,
        iconEmoji = "🚌",
        name = LocalizedString("ဘတ်စ်ကားသီးသန့်", "รถเมล์ล้วน", "Bus Only"),
        description = LocalizedString("BMTA မြို့တွင်းဘတ်စ်ကားလိုင်းများသာ အသုံးပြုခြင်း", "ใช้เฉพาะรถโดยสารประจำทาง ขสมก.", "Economical surface routes using city buses")
    ),
    TransportModeOption(
        id = 8,
        iconEmoji = "🚆",
        name = LocalizedString("ရထားသီးသန့် (BTS/MRT)", "รถไฟฟ้าล้วน (BTS/MRT)", "Train Only"),
        description = LocalizedString("မိုးပျံရထားနှင့် မြေအောက်ရထားလိုင်းများသာ အသုံးပြုခြင်း", "ใช้เฉพาะรถไฟฟ้า BTS, MRT, ARL", "Rapid transit network avoiding road traffic")
    ),
    TransportModeOption(
        id = 9,
        iconEmoji = "🚖",
        name = LocalizedString("မီတာတက္ကစီသီးသန့်", "แท็กซี่มิเตอร์ล้วน", "Taxi Only"),
        description = LocalizedString("တရားဝင် မီတာဖွင့် တက္ကစီများဖြင့် တိုက်ရိုက်သွားလာခြင်း", "เดินทางด้วยแท็กซี่มิเตอร์ที่ถูกกฎหมาย", "Direct licensed meter taxi service")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlannerScreen(
    state: BangkokUiState,
    majorHubs: List<TransitHub> = emptyList(),
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onSwapEndpoints: () -> Unit,
    onUseCurrentLocationForFrom: () -> Unit,
    onSelectMode: (Int) -> Unit,
    onSaveRoute: (MultimodalRoute) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onSpeakThai: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current active language state with seamless toggle (Burmese, Thai, English)
    var selectedLanguage by remember(state.isBurmese) {
        mutableStateOf(if (state.isBurmese) PlannerLanguage.BURMESE else PlannerLanguage.THAI)
    }

    // Modal state for major transit hubs
    var activeHubTarget by remember { mutableStateOf<HubSelectionTarget?>(null) }

    // Dropdown expanded state
    var isModeDropdownExpanded by remember { mutableStateOf(false) }

    val currentMode = transportModeOptions.getOrNull(state.selectedRouteModeIndex) ?: transportModeOptions[0]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("route_planner_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Header with Language Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BangkokBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.AltRoute,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = RoutePlannerLocalization.screenTitle.get(selectedLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = RoutePlannerLocalization.screenSubtitle.get(selectedLanguage),
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Toggle Bar (Burmese, Thai, English)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PlannerLanguage.values().forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clickable { selectedLanguage = lang }
                                    .testTag("lang_toggle_${lang.code}"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) BangkokBlue else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${lang.flagEmoji} ${lang.displayName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Main Search Card: Start Point, Switch Directions Button, Destination, Dropdown Menu
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("route_search_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // FIELD 1: Start Point Search Input
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = RoutePlannerLocalization.startPointLabel.get(selectedLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        OutlinedTextField(
                            value = state.plannerFrom,
                            onValueChange = onFromChange,
                            placeholder = {
                                Text(
                                    text = RoutePlannerLocalization.startPointPlaceholder.get(selectedLanguage),
                                    fontSize = 13.sp,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Start Point",
                                    tint = SuccessGreen
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (state.plannerFrom.isNotEmpty()) {
                                        IconButton(
                                            onClick = { onFromChange("") },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear Start Point",
                                                tint = TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    // Use Current GPS Location
                                    IconButton(
                                        onClick = onUseCurrentLocationForFrom,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("btn_use_current_location")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = RoutePlannerLocalization.useCurrentLocation.get(selectedLanguage),
                                            tint = BangkokBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    // Choose from Transit Hubs
                                    IconButton(
                                        onClick = { activeHubTarget = HubSelectionTarget.ORIGIN },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("btn_pick_origin_hub")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = RoutePlannerLocalization.selectTransitHub.get(selectedLanguage),
                                            tint = BurmeseGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SuccessGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_start_point")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BUTTON: Switch Directions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        FilledTonalButton(
                            onClick = onSwapEndpoints,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = BangkokBlue.copy(alpha = 0.12f),
                                contentColor = BangkokBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .padding(horizontal = 8.dp)
                                .testTag("btn_switch_directions")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = RoutePlannerLocalization.switchDirections.get(selectedLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // FIELD 2: Destination Search Input
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ThaiRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = RoutePlannerLocalization.destinationLabel.get(selectedLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ThaiRed
                            )
                        }

                        OutlinedTextField(
                            value = state.plannerTo,
                            onValueChange = onToChange,
                            placeholder = {
                                Text(
                                    text = RoutePlannerLocalization.destinationPlaceholder.get(selectedLanguage),
                                    fontSize = 13.sp,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Destination",
                                    tint = ThaiRed
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (state.plannerTo.isNotEmpty()) {
                                        IconButton(
                                            onClick = { onToChange("") },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear Destination",
                                                tint = TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    // Choose from Transit Hubs
                                    IconButton(
                                        onClick = { activeHubTarget = HubSelectionTarget.DESTINATION },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("btn_pick_destination_hub")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tour,
                                            contentDescription = RoutePlannerLocalization.selectTransitHub.get(selectedLanguage),
                                            tint = ThaiRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ThaiRed,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_destination")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // COMPONENT: Transport Mode Selection Dropdown Menu
                    Column {
                        Text(
                            text = RoutePlannerLocalization.transportModeLabel.get(selectedLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isModeDropdownExpanded) BangkokBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { isModeDropdownExpanded = !isModeDropdownExpanded }
                                    .testTag("dropdown_transport_mode"),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = currentMode.iconEmoji,
                                            fontSize = 20.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = currentMode.name.get(selectedLanguage),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = currentMode.description.get(selectedLanguage),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = if (isModeDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Expand transport modes dropdown",
                                        tint = BangkokBlue
                                    )
                                }
                            }

                            // Dropdown Menu Popover
                            DropdownMenu(
                                expanded = isModeDropdownExpanded,
                                onDismissRequest = { isModeDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("menu_transport_modes")
                            ) {
                                transportModeOptions.forEach { option ->
                                    val isSelected = option.id == state.selectedRouteModeIndex
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = option.iconEmoji,
                                                    fontSize = 18.sp
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = option.name.get(selectedLanguage),
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) BangkokBlue else MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = option.description.get(selectedLanguage),
                                                        fontSize = 11.sp,
                                                        color = TextMuted,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = BangkokBlue,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            onSelectMode(option.id)
                                            isModeDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("mode_option_${option.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Transit Hub Carousel
        if (majorHubs.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = RoutePlannerLocalization.popularHubsTitle.get(selectedLanguage),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(
                            onClick = { activeHubTarget = HubSelectionTarget.DESTINATION }
                        ) {
                            Text(
                                text = RoutePlannerLocalization.viewAll.get(selectedLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BangkokBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(majorHubs.take(10)) { hub ->
                            val hubLabel = when (selectedLanguage) {
                                PlannerLanguage.BURMESE -> hub.shortNameMm
                                PlannerLanguage.THAI -> hub.nameTh
                                PlannerLanguage.ENGLISH -> hub.nameEn
                            }
                            Surface(
                                modifier = Modifier
                                    .clickable { onToChange(hub.nameMm) }
                                    .testTag("quick_hub_${hub.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (hub.type) {
                                            TransportType.BTS -> "🚆"
                                            TransportType.MRT -> "🚇"
                                            TransportType.BUS_TERMINAL -> "🚌"
                                            TransportType.AIRPORT_RAIL_LINK, TransportType.SRT -> "✈️"
                                            TransportType.COMMUNITY, TransportType.WORKPLACE -> "🏭"
                                            else -> "📍"
                                        },
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = hubLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Section Header: Recommended Routes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = RoutePlannerLocalization.recommendedRoutesTitle.get(selectedLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = BangkokBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${state.plannedRoutes.size}",
                        color = BangkokBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // 5. Planned Multimodal Route Cards
        items(state.plannedRoutes) { route ->
            RouteCard(
                route = route,
                selectedLanguage = selectedLanguage,
                isBurmese = state.isBurmese,
                onSaveClick = { onSaveRoute(route) },
                onShowDriverClick = onShowDriverClick,
                onSpeakThai = onSpeakThai
            )
        }
    }

    // Modal Sheet for choosing transit hubs
    activeHubTarget?.let { target ->
        TransitHubSelectionSheet(
            target = target,
            hubs = majorHubs,
            isBurmese = selectedLanguage == PlannerLanguage.BURMESE,
            onSelectHub = { selectedHub ->
                val formattedName = when (selectedLanguage) {
                    PlannerLanguage.BURMESE -> selectedHub.nameMm
                    PlannerLanguage.THAI -> selectedHub.nameTh
                    PlannerLanguage.ENGLISH -> selectedHub.nameEn
                }
                if (target == HubSelectionTarget.ORIGIN) {
                    onFromChange(formattedName)
                } else {
                    onToChange(formattedName)
                }
                activeHubTarget = null
            },
            onDismiss = { activeHubTarget = null }
        )
    }
}

@Composable
fun RouteCard(
    route: MultimodalRoute,
    selectedLanguage: PlannerLanguage,
    isBurmese: Boolean,
    onSaveClick: () -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onSpeakThai: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("route_card_${route.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Route Mode Badge + Save Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BurmeseGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            PlannerLanguage.BURMESE -> route.modeNameMm
                            PlannerLanguage.THAI -> route.modeNameTh
                            PlannerLanguage.ENGLISH -> route.modeNameMm
                        },
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_save_route_${route.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkAdd,
                        contentDescription = RoutePlannerLocalization.saveRouteButton.get(selectedLanguage),
                        tint = BangkokBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route Title
            Text(
                text = when (selectedLanguage) {
                    PlannerLanguage.BURMESE -> route.titleMm
                    PlannerLanguage.THAI -> route.titleTh
                    PlannerLanguage.ENGLISH -> route.titleMm
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Stats Pill (Duration, Fare, Walking distance)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⏱ ${route.totalTimeMinutes} ${RoutePlannerLocalization.minutesUnit.get(selectedLanguage)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = RoutePlannerLocalization.totalTimeLabel.get(selectedLanguage),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "💰 ${route.fareMinThb}–${route.fareMaxThb} ${RoutePlannerLocalization.thbUnit.get(selectedLanguage)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SuccessGreen
                        )
                        Text(
                            text = RoutePlannerLocalization.estimatedFareLabel.get(selectedLanguage),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🚶 ${route.walkingMeters} ${RoutePlannerLocalization.metersUnit.get(selectedLanguage)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = RoutePlannerLocalization.walkingDistanceLabel.get(selectedLanguage),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-Step Directions
            Text(
                text = RoutePlannerLocalization.stepByStepTitle.get(selectedLanguage),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            route.steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BangkokBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (selectedLanguage) {
                                PlannerLanguage.BURMESE -> step.instructionMm
                                PlannerLanguage.THAI -> step.instructionTh
                                PlannerLanguage.ENGLISH -> step.instructionMm
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                        if (step.exitNumber.isNotBlank()) {
                            Text(
                                text = "🚪 ${step.exitNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BangkokBlue
                            )
                        }

                        // Driver Thai phrase with TTS button and Show Driver dialog
                        if (step.driverPhraseTh.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = BurmeseGold.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onShowDriverClick(
                                            step.driverPhraseTh,
                                            step.instructionMm
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🚖 ${RoutePlannerLocalization.showDriverBanner.get(selectedLanguage)} “${step.driverPhraseTh}”",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { onSpeakThai(step.driverPhraseTh) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Speak Thai phrase",
                                            tint = BangkokBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Safety Warning if present
            if (route.safetyNoteMm.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = WarningOrange.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${RoutePlannerLocalization.safetyWarningTitle.get(selectedLanguage)} ${
                                if (selectedLanguage == PlannerLanguage.THAI) route.safetyNoteTh else route.safetyNoteMm
                            }",
                            fontSize = 12.sp,
                            color = TextDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
