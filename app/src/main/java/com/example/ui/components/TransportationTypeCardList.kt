package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportType
import com.example.data.model.TransportationTypeItem
import com.example.ui.theme.*

/**
 * Returns appropriate Material 3 icon for each transportation type.
 */
fun getTransportationTypeIcon(type: TransportType): ImageVector {
    return when (type) {
        TransportType.BTS -> Icons.Default.Train
        TransportType.MRT -> Icons.Default.Subway
        TransportType.BUS, TransportType.BUS_STOP, TransportType.BUS_TERMINAL -> Icons.Default.DirectionsBus
        TransportType.SRT, TransportType.AIRPORT_RAIL_LINK -> Icons.Default.Tram
        TransportType.TAXI, TransportType.MOTORCYCLE_TAXI -> Icons.Default.LocalTaxi
        else -> Icons.Default.DirectionsTransit
    }
}

/**
 * Interactive list of clickable cards displaying Bangkok's primary transportation types
 * (BTS, MRT, Bus, SRT, Taxi) on the Home screen.
 *
 * Design and create by Gemini, Development by Moekyawaung, GitHub -dev-moe-kyaw-aung
 */
@Composable
fun TransportationTypeCardList(
    transportationTypes: List<TransportationTypeItem>,
    isBurmese: Boolean = LocalIsBurmese.current,
    onSelectTransportationType: (TransportationTypeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("section_transportation_type_card_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = BurmeseGold,
                    shape = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.AltRoute,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = if (isBurmese) "အဓိက သယ်ယူပို့ဆောင်ရေး ယာဉ်လိုင်းများ" else "ระบบขนส่งสาธารณะหลัก (5 รูปแบบ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isBurmese) "BTS • MRT • Bus • SRT • Taxi လမ်းညွှန်" else "คู่มือการเดินทาง: รถไฟฟ้า รถเมล์ รถไฟ แท็กซี่",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    text = "${transportationTypes.size} Types",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // List of Transportation Type Clickable Cards
        transportationTypes.forEach { item ->
            val isExpanded = expandedItemId == item.id
            TransportationTypeCardItem(
                item = item,
                isBurmese = isBurmese,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedItemId = if (isExpanded) null else item.id
                },
                onClick = { onSelectTransportationType(item) }
            )
        }

        // Creator & Project Attribution Footer Card
        CreatorAttributionFooterCard(
            isBurmese = isBurmese,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Individual high-contrast, Material 3 card representing a transportation type.
 */
@Composable
fun TransportationTypeCardItem(
    item: TransportationTypeItem,
    isBurmese: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandColor = Color(item.primaryColorHex)
    val brandLightColor = Color(item.secondaryColorHex)
    val isDark = LocalIsDarkMode.current

    val surfaceColor = if (isDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface
    }

    val cardBorderColor = if (isDark) {
        brandColor.copy(alpha = 0.35f)
    } else {
        brandColor.copy(alpha = 0.25f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("transport_type_card_${item.id.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(cardBorderColor, cardBorderColor.copy(alpha = 0.1f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Branded Icon + Title & Subtitle + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Branded Transit Icon Container
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) brandColor.copy(alpha = 0.2f) else brandLightColor,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getTransportationTypeIcon(item.type),
                            contentDescription = item.nameEn,
                            tint = if (isDark) brandColor else brandColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBurmese) item.nameMm else item.nameTh,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (isBurmese) item.subtitleMm else item.subtitleTh,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status / Highlight Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) brandColor.copy(alpha = 0.25f) else brandLightColor,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = if (isBurmese) item.badgeMm else item.badgeTh,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) brandColor else brandColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Network overview / description
            Text(
                text = if (isBurmese) item.descriptionMm else item.descriptionTh,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                lineHeight = 20.sp,
                maxLines = if (isExpanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Chips Row: Hours & Fare Range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hours Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Hours",
                            tint = brandColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.operatingHours,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Fare Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Fare",
                            tint = brandColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBurmese) item.fareRangeMm else item.fareRangeTh,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Expandable section with features & tips
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Key Stations
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isBurmese) "အဓိက ဆုံရပ် ဘူတာကြီးများ:" else "สถานีศูนย์กลางหลัก:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBurmese) item.keyStationsMm else item.keyStationsTh,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Worker Travel Tips
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BurmeseGold.copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(BurmeseGold.copy(alpha = 0.4f), BurmeseGold.copy(alpha = 0.1f))
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.TipsAndUpdates,
                                contentDescription = null,
                                tint = TextDark,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBurmese) "အကြံပြုချက်နှင့် သိကောင်းစရာ:" else "ข้อแนะนำสำหรับผู้เดินทาง:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBurmese) item.travelTipsMm else item.travelTipsTh,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: Click to navigate & Expand/Collapse Tips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_navigate_${item.id.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isBurmese) "${item.nameMm} လမ်းကြောင်းကြည့်မည်" else "ดูเส้นทาง ${item.nameTh}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Navigate",
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .testTag("btn_expand_${item.id.lowercase()}")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle info",
                        tint = brandColor
                    )
                }
            }
        }
    }
}

/**
 * Creator Attribution Footer Card displaying Gemini design and development credits.
 * Design and create by Gemini, Development by Moekyawaung, GitHub -dev-moe-kyaw-aung
 */
@Composable
fun CreatorAttributionFooterCard(
    isBurmese: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("creator_attribution_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(BangkokBlue.copy(alpha = 0.3f), BurmeseGold.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = BurmeseGold,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini",
                            tint = TextDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "Design and create by Gemini",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BangkokBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Development by Moekyawaung",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BangkokBlue
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    modifier = Modifier.testTag("author_github_link")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "GitHub",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GitHub: dev-moe-kyaw-aung",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBurmese)
                    "မြန်မာရွှေ့ပြောင်းလုပ်သားများနှင့် ခရီးသွားများ ဘန်ကောက်တွင် ဘေးကင်းလုံခြုံစွာ သွားလာနိုင်ရန် အခမဲ့ လမ်းညွှန်"
                else
                    "คู่มือการเดินทางฟรี เพื่อความปลอดภัยของแรงงานและผู้เดินทางในกรุงเทพฯ",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
