package com.example.ui.components

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
import com.example.data.model.TransitHub
import com.example.data.model.TransitHubCategory
import com.example.data.model.TransportType
import com.example.ui.theme.*

enum class HubSelectionTarget {
    ORIGIN,
    DESTINATION
}

@Composable
fun RoutePlanningInputForm(
    fromValue: String,
    toValue: String,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
    onSwapEndpoints: () -> Unit,
    onUseCurrentLocationForFrom: () -> Unit,
    majorHubs: List<TransitHub>,
    isBurmese: Boolean,
    modifier: Modifier = Modifier
) {
    var activeModalTarget by remember { mutableStateOf<HubSelectionTarget?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("route_planning_input_form"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Form Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BangkokBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBurmese) "လမ်းကြောင်း ရှာဖွေရေး ဖောင်" else "แบบฟอร์มวางแผนการเดินทาง",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = BurmeseGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isBurmese) "ဘူတာစုံ ရွေးချယ်နိုင်" else "เลือกจากศูนย์กลางขนส่ง",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Starting Point Input Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBurmese) "စတင်မည့်နေရာ (Starting Point)" else "จุดเริ่มต้น (Origin)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        OutlinedTextField(
                            value = fromValue,
                            onValueChange = onFromChange,
                            placeholder = {
                                Text(
                                    text = if (isBurmese) "စတင်မည့်နေရာ ရိုက်ထည့်ပါ သို့မဟုတ် ရွေးပါ..." else "พิมพ์หรือเลือกจุดเริ่มต้น...",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("input_planner_from")
                        )
                    }

                    // Button to trigger Hub Selector for Origin
                    IconButton(
                        onClick = { activeModalTarget = HubSelectionTarget.ORIGIN },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BangkokBlue.copy(alpha = 0.12f))
                            .testTag("btn_select_origin_hub")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Select major transit hub for origin",
                            tint = BangkokBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Button for GPS location
                    IconButton(
                        onClick = onUseCurrentLocationForFrom,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BurmeseGold.copy(alpha = 0.25f))
                            .testTag("btn_use_current_location")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Use current location",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Swap Button Row in the Center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onSwapEndpoints,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = BangkokBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("btn_swap_endpoints")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap Origin and Destination",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 2. Destination Input Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(ThaiRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBurmese) "သွားလိုသောနေရာ (Destination)" else "จุดหมายปลายทาง (Destination)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThaiRed
                        )
                        OutlinedTextField(
                            value = toValue,
                            onValueChange = onToChange,
                            placeholder = {
                                Text(
                                    text = if (isBurmese) "သွားလိုသောနေရာ ရိုက်ထည့်ပါ သို့မဟုတ် ရွေးပါ..." else "พิมพ์หรือเลือกจุดหมายปลายทาง...",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("input_planner_to")
                        )
                    }

                    // Button to trigger Hub Selector for Destination
                    IconButton(
                        onClick = { activeModalTarget = HubSelectionTarget.DESTINATION },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ThaiRed.copy(alpha = 0.12f))
                            .testTag("btn_select_destination_hub")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tour,
                            contentDescription = "Select major transit hub for destination",
                            tint = ThaiRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Hub Quick-Pick Carousel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBurmese) "အဓိက အချက်အချာများမှ တိုက်ရိုက်ရွေးရန်:" else "เลือกศูนย์กลางการเดินทางยอดนิยม:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { activeModalTarget = HubSelectionTarget.DESTINATION },
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = if (isBurmese) "အားလုံးကြည့်မည်" else "ดูทั้งหมด",
                        fontSize = 12.sp,
                        color = BangkokBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(majorHubs.take(8)) { hub ->
                    Surface(
                        modifier = Modifier
                            .clickable {
                                // Default sets destination
                                onToChange(hub.nameMm)
                            }
                            .testTag("quick_hub_${hub.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                                text = hub.shortNameMm,
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

    // Modal Sheet for choosing transit hub
    activeModalTarget?.let { target ->
        TransitHubSelectionSheet(
            target = target,
            hubs = majorHubs,
            isBurmese = isBurmese,
            onSelectHub = { selectedHub ->
                val formattedName = selectedHub.nameMm
                if (target == HubSelectionTarget.ORIGIN) {
                    onFromChange(formattedName)
                } else {
                    onToChange(formattedName)
                }
                activeModalTarget = null
            },
            onDismiss = { activeModalTarget = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransitHubSelectionSheet(
    target: HubSelectionTarget,
    hubs: List<TransitHub>,
    isBurmese: Boolean,
    onSelectHub: (TransitHub) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TransitHubCategory.ALL) }

    val filteredHubs = remember(searchQuery, selectedCategory, hubs) {
        hubs.filter { hub ->
            val matchesCategory = selectedCategory == TransitHubCategory.ALL || hub.category == selectedCategory
            val cleanQuery = searchQuery.trim().lowercase()
            val matchesQuery = cleanQuery.isBlank() ||
                    hub.nameMm.lowercase().contains(cleanQuery) ||
                    hub.nameTh.lowercase().contains(cleanQuery) ||
                    hub.nameEn.lowercase().contains(cleanQuery) ||
                    hub.shortNameMm.lowercase().contains(cleanQuery) ||
                    hub.lines.any { it.lowercase().contains(cleanQuery) } ||
                    hub.descriptionMm.lowercase().contains(cleanQuery)
            matchesCategory && matchesQuery
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 18.dp)
        ) {
            // Header Target Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (target == HubSelectionTarget.ORIGIN) SuccessGreen else ThaiRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (target == HubSelectionTarget.ORIGIN) Icons.Default.LocationOn else Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (target == HubSelectionTarget.ORIGIN) {
                                if (isBurmese) "စတင်မည့်နေရာ ရွေးချယ်ရန်" else "เลือกจุดเริ่มต้น (Origin)"
                            } else {
                                if (isBurmese) "သွားလိုသောနေရာ ရွေးချယ်ရန်" else "เลือกจุดหมายปลายทาง (Destination)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBurmese) "ဘန်ကောက်မြို့ အဓိက သယ်ယူပို့ဆောင်ရေး အချက်အချာများ" else "ศูนย์กลางการเดินทางหลักในกรุงเทพฯ",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar inside Modal
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_transit_hubs"),
                placeholder = {
                    Text(
                        text = if (isBurmese) "ဘူတာ၊ ဂိတ်ကြီး သို့မဟုတ် နယ်မြေရှာရန်..." else "ค้นหา BTS, คิวรถ, สนามบิน, ตลาด...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BangkokBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TransitHubCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = if (isBurmese) category.titleMm else category.titleTh,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BangkokBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Count label
            Text(
                text = if (isBurmese) "ရွေးချယ်နိုင်သော အချက်အချာပေါင်း (${filteredHubs.size}) ခု:" else "ศูนย์กลางการเดินทาง (${filteredHubs.size}) แห่ง:",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // List of Transit Hubs
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredHubs) { hub ->
                    TransitHubCard(
                        hub = hub,
                        isBurmese = isBurmese,
                        onSelect = { onSelectHub(hub) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransitHubCard(
    hub: TransitHub,
    isBurmese: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("hub_item_${hub.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Icon + Category Label + Select Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (hub.type) {
                                    TransportType.BTS -> Color(0xFFE8F5E9)
                                    TransportType.MRT -> Color(0xFFE3F2FD)
                                    TransportType.BUS_TERMINAL -> Color(0xFFE0F7FA)
                                    TransportType.AIRPORT_RAIL_LINK -> Color(0xFFFCE4EC)
                                    TransportType.SRT -> Color(0xFFFFF3E0)
                                    TransportType.COMMUNITY, TransportType.WORKPLACE -> Color(0xFFFFF8E1)
                                    else -> Color(0xFFEDE7F6)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (hub.type) {
                                TransportType.BTS, TransportType.MRT -> Icons.Default.Train
                                TransportType.BUS_TERMINAL -> Icons.Default.DepartureBoard
                                TransportType.AIRPORT_RAIL_LINK -> Icons.Default.FlightTakeoff
                                TransportType.SRT -> Icons.Default.DirectionsRailway
                                TransportType.WORKPLACE, TransportType.COMMUNITY -> Icons.Default.Storefront
                                else -> Icons.Default.Place
                            },
                            contentDescription = null,
                            tint = when (hub.type) {
                                TransportType.BTS -> Color(0xFF2E7D32)
                                TransportType.MRT -> Color(0xFF1565C0)
                                TransportType.BUS_TERMINAL -> Color(0xFF00838F)
                                TransportType.AIRPORT_RAIL_LINK -> Color(0xFFC2185B)
                                TransportType.SRT -> Color(0xFFE65100)
                                TransportType.COMMUNITY, TransportType.WORKPLACE -> Color(0xFFF57F17)
                                else -> BangkokBlue
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isBurmese) hub.category.titleMm else hub.category.titleTh,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangkokBlueDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Select Button
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_confirm_hub_${hub.id}")
                ) {
                    Text(
                        text = if (isBurmese) "ရွေးမည်" else "เลือก",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Burmese Name (Primary)
            Text(
                text = hub.nameMm,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Thai Script Name
            Text(
                text = hub.nameTh,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = BangkokBlue
            )

            // English / Romanized
            Text(
                text = hub.nameEn,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = if (isBurmese) hub.descriptionMm else hub.descriptionTh,
                style = MaterialTheme.typography.bodySmall,
                color = TextDark,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Connecting Lines Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                hub.lines.take(3).forEach { line ->
                    Surface(
                        color = BurmeseGold.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = line,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
