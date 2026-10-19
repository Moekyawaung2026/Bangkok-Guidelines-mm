package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun BangkokTopAppBar(
    isBurmese: Boolean = LocalIsBurmese.current,
    isDarkMode: Boolean = LocalIsDarkMode.current,
    onToggleLanguage: () -> Unit = LocalLanguageContext.current.onToggleLanguage,
    onToggleDarkMode: () -> Unit,
    onEmergencyClick: () -> Unit,
    onAiAssistantClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Brand Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BurmeseGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsTransit,
                            contentDescription = "Logo",
                            tint = TextDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBurmese) "Bangkok Go MM" else "กรุงเทพเดินทาง",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBurmese) "ဘန်ကောက်သွားလာရေးလမ်းညွှန်" else "Bangkok Go MM",
                            style = MaterialTheme.typography.bodySmall,
                            color = BurmeseGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: AI Assistant, Language Switcher, Dark Mode, Emergency SOS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dark Mode Toggle Button (48dp touch target)
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("btn_toggle_dark_mode")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkMode) BurmeseGold else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // AI Assistant Quick Button
                    IconButton(
                        onClick = onAiAssistantClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("btn_ai_assistant_top")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = BurmeseGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Language Switcher Badge Button (48dp touch target)
                    Button(
                        onClick = onToggleLanguage,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurmeseGold,
                            contentColor = TextDark
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("btn_language_switcher")
                    ) {
                        Text(
                            text = if (isBurmese) "မြန်မာ | TH" else "ไทย | MM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Emergency SOS Red Button
                    IconButton(
                        onClick = onEmergencyClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ThaiRed)
                            .testTag("btn_emergency_sos")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Emergency 191",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BangkokBottomNavigationBar(
    currentTab: AppTab,
    isBurmese: Boolean = LocalIsBurmese.current,
    onSelectTab: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val navItems = listOf(
            Triple(AppTab.HOME, Icons.Default.Home, if (isBurmese) "ပင်မ" else "หน้าหลัก"),
            Triple(AppTab.NEARBY, Icons.Default.NearMe, if (isBurmese) "အနီးနား" else "ใกล้ฉัน"),
            Triple(AppTab.PLANNER, Icons.Default.Route, if (isBurmese) "လမ်းကြောင်း" else "วางแผน"),
            Triple(AppTab.BUS, Icons.Default.DirectionsBus, if (isBurmese) "ဘတ်စ်ကား" else "รถเมล์"),
            Triple(AppTab.LINES, Icons.Default.Train, if (isBurmese) "ရထားလိုင်း" else "รถไฟฟ้า"),
            Triple(AppTab.SAFETY, Icons.Default.Shield, if (isBurmese) "လုံခြုံရေး" else "ความปลอดภัย")
        )

        navItems.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun TransportTypeBadge(type: TransportType, isBurmese: Boolean = LocalIsBurmese.current) {
    val isDark = LocalIsDarkMode.current
    val (bg, fg) = when (type) {
        TransportType.BTS -> if (isDark) Pair(Color(0xFF14532D), Color(0xFF86EFAC)) else Pair(Color(0xFFE8F5E9), Color(0xFF166534))
        TransportType.MRT -> if (isDark) Pair(Color(0xFF1E3A8A), Color(0xFF93C5FD)) else Pair(Color(0xFFE3F2FD), Color(0xFF1E40AF))
        TransportType.AIRPORT_RAIL_LINK -> if (isDark) Pair(Color(0xFF831843), Color(0xFFF9A8D4)) else Pair(Color(0xFFFCE4EC), Color(0xFF9D174D))
        TransportType.SRT -> if (isDark) Pair(Color(0xFF7C2D12), Color(0xFFFDBA74)) else Pair(Color(0xFFFFF3E0), Color(0xFFC2410C))
        TransportType.BUS, TransportType.BUS_STOP -> if (isDark) Pair(Color(0xFF581C87), Color(0xFFD8B4FE)) else Pair(Color(0xFFEDE7F6), Color(0xFF6B21A8))
        TransportType.BUS_TERMINAL -> if (isDark) Pair(Color(0xFF164E63), Color(0xFF67E8F9)) else Pair(Color(0xFFE0F7FA), Color(0xFF0E7490))
        TransportType.TAXI -> if (isDark) Pair(Color(0xFF713F12), Color(0xFFFDE047)) else Pair(Color(0xFFFFFDE7), Color(0xFFA16207))
        TransportType.MOTORCYCLE_TAXI -> if (isDark) Pair(Color(0xFF78350F), Color(0xFFFCD34D)) else Pair(Color(0xFFFFECB3), Color(0xFFB45309))
        TransportType.HOSPITAL -> if (isDark) Pair(Color(0xFF7F1D1D), Color(0xFFFCA5A5)) else Pair(Color(0xFFFFEBEE), Color(0xFFB91C1C))
        TransportType.POLICE -> if (isDark) Pair(Color(0xFF1E1B4B), Color(0xFFA5B4FC)) else Pair(Color(0xFFE8EAF6), Color(0xFF3730A3))
        TransportType.EMBASSY -> if (isDark) Pair(Color(0xFF064E3B), Color(0xFF6EE7B7)) else Pair(Color(0xFFE0F2F1), Color(0xFF047857))
        TransportType.COMMUNITY, TransportType.MARKET, TransportType.WORKPLACE -> if (isDark) Pair(Color(0xFF713F12), Color(0xFFFDE047)) else Pair(Color(0xFFFFF8E1), Color(0xFFA16207))
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = if (isBurmese) type.labelMm else type.labelTh,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatusBadge(status: ServiceStatus, isBurmese: Boolean = LocalIsBurmese.current) {
    val (color, label) = when (status) {
        ServiceStatus.NORMAL -> Pair(SuccessGreen, if (isBurmese) status.labelMm else status.labelTh)
        ServiceStatus.DELAYED -> Pair(WarningOrange, if (isBurmese) status.labelMm else status.labelTh)
        ServiceStatus.CLOSED -> Pair(ThaiRed, if (isBurmese) status.labelMm else status.labelTh)
        ServiceStatus.UNKNOWN -> Pair(TextMuted, if (isBurmese) status.labelMm else status.labelTh)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TransportPlaceCard(
    place: TransportPlace,
    isBurmese: Boolean = LocalIsBurmese.current,
    distanceMeters: Double? = null,
    onCardClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShowDriverClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("card_place_${place.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Badge + Distance + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransportTypeBadge(type = place.type, isBurmese = isBurmese)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (distanceMeters != null) {
                        val distanceText = if (distanceMeters < 1000) {
                            "${distanceMeters.toInt()} မီတာ"
                        } else {
                            String.format("%.1f ကီလို", distanceMeters / 1000)
                        }
                        Surface(
                            color = BangkokBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🚶 $distanceText",
                                fontSize = 11.sp,
                                color = BangkokBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    StatusBadge(status = place.liveStatus, isBurmese = isBurmese)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Burmese Name
            Text(
                text = place.nameMm,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Thai Name
            Text(
                text = place.nameTh,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = BangkokBlue
            )

            // English / Romanized
            if (place.nameEn.isNotBlank()) {
                Text(
                    text = place.nameEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Address and Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏰ ${place.openingTime} - ${place.closingTime}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                if (place.fareMin > 0) {
                    Text(
                        text = "💰 ${place.fareMin} - ${place.fareMax} THB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons (minimum 48dp height requirement)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Directions button
                Button(
                    onClick = onDirectionsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_directions_${place.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isBurmese) "လမ်းညွှန်" else "เส้นทาง", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Show to Driver Flashcard
                OutlinedButton(
                    onClick = onShowDriverClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_show_driver_${place.id}"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp), tint = BangkokBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isBurmese) "ဒရိုင်ဘာအားပြ" else "ให้คนขับดู", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BangkokBlue)
                }

                // Save button
                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_save_${place.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save place",
                        tint = BurmeseGold
                    )
                }
            }
        }
    }
}

@Composable
fun DriverFlashcardDialog(
    thaiText: String,
    burmeseMeaning: String,
    onSpeakThai: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Label
                Surface(
                    color = BurmeseGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "🚖 ယာဉ်မောင်း/လက်မှတ်ဖြတ်သူအား ပြပါ (Show Driver)",
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Huge High-Contrast Thai Text for Driver
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = thaiText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Burmese meaning
                Text(
                    text = "မြန်မာအဓိပ္ပာယ်: $burmeseMeaning",
                    fontSize = 15.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // TTS Play Audio Button (Height >= 48dp)
                Button(
                    onClick = onSpeakThai,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_tts_play"),
                    colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Play Audio")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ထိုင်းအသံထွက် ဖွင့်ပြရန် (Play Thai Voice)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Close Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "ပိတ်မည် (Close)", color = TextDark, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
