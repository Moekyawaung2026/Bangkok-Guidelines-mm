package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportType
import com.example.ui.theme.LocalIsDarkMode
import com.example.ui.theme.LocalIsBurmese

/**
 * Data representation for each transit service quick-access card.
 */
data class TransitQuickAccessItem(
    val id: String,
    val type: TransportType,
    val titleMm: String,
    val titleTh: String,
    val titleEn: String,
    val subtitleMm: String,
    val subtitleTh: String,
    val fareMm: String,
    val fareTh: String,
    val badgeMm: String,
    val badgeTh: String,
    val icon: ImageVector,
    val lightBrandColor: Color,
    val darkBrandColor: Color,
    val lightBgColor: Color,
    val darkBgColor: Color,
    val testTag: String
)

/**
 * Home screen grid layout displaying quick-access cards for BTS, MRT, Bus, and SRT services,
 * using Material 3 icons for intuitive navigation.
 */
@Composable
fun HomeScreenTransitGrid(
    isBurmese: Boolean = LocalIsBurmese.current,
    onSelectTransitType: (TransportType) -> Unit,
    onSelectBusService: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkMode.current

    val items = listOf(
        // 1. BTS Skytrain Card
        TransitQuickAccessItem(
            id = "bts",
            type = TransportType.BTS,
            titleMm = "BTS မိုးပျံရထား",
            titleTh = "รถไฟฟ้า BTS",
            titleEn = "BTS Skytrain",
            subtitleMm = "ဆူခုမ်ဝစ် / ဆီလုံလိုင်း",
            subtitleTh = "สุขุมวิท • สีลม • สีทอง",
            fareMm = "၁၇ - ၆၂ ฿",
            fareTh = "17 - 62 บาท",
            badgeMm = "Rabbit Card",
            badgeTh = "บัตร Rabbit",
            icon = Icons.Default.Train,
            lightBrandColor = Color(0xFF00823B),
            darkBrandColor = Color(0xFF4ADE80),
            lightBgColor = Color(0xFFE8F5E9),
            darkBgColor = Color(0xFF14532D),
            testTag = "card_quick_bts"
        ),

        // 2. MRT Subway & Monorail Card
        TransitQuickAccessItem(
            id = "mrt",
            type = TransportType.MRT,
            titleMm = "MRT မြေအောက်ရထား",
            titleTh = "รถไฟฟ้า MRT",
            titleEn = "MRT Subway",
            subtitleMm = "အပြာ / ခရမ်း / မိုနိုရိုင်း",
            subtitleTh = "น้ำเงิน • ม่วง • เหลือง • ชมพู",
            fareMm = "၁၇ - ၄၅ ฿",
            fareTh = "17 - 45 บาท",
            badgeMm = "EMV တိုက်ရိုက်ကတ်",
            badgeTh = "แตะบัตร EMV",
            icon = Icons.Default.Subway,
            lightBrandColor = Color(0xFF003399),
            darkBrandColor = Color(0xFF60A5FA),
            lightBgColor = Color(0xFFE3F2FD),
            darkBgColor = Color(0xFF1E3A8A),
            testTag = "card_quick_mrt"
        ),

        // 3. BMTA City Bus Card
        TransitQuickAccessItem(
            id = "bus",
            type = TransportType.BUS,
            titleMm = "BMTA ဘတ်စ်ကား",
            titleTh = "รถเมล์ ขสมก.",
            titleEn = "BMTA City Bus",
            subtitleMm = "အနီရိုးရိုး / အဲကွန်းကား",
            subtitleTh = "รถร้อน 8฿ • รถแอร์ 12-25฿",
            fareMm = "၈ - ၂၅ ฿",
            fareTh = "8 - 25 บาท",
            badgeMm = "ညလုံးပေါက် ၂၄ နာရီ",
            badgeTh = "วิ่ง 24 ชั่วโมง",
            icon = Icons.Default.DirectionsBus,
            lightBrandColor = Color(0xFFC2410C),
            darkBrandColor = Color(0xFFFB923C),
            lightBgColor = Color(0xFFFFEDE7),
            darkBgColor = Color(0xFF431407),
            testTag = "card_quick_bus"
        ),

        // 4. SRT Commuter & Rail Card
        TransitQuickAccessItem(
            id = "srt",
            type = TransportType.SRT,
            titleMm = "SRT ရထားနီလိုင်း",
            titleTh = "รถไฟชานเมือง SRT",
            titleEn = "SRT Red Line",
            subtitleMm = "ဒွန်မောင်းလေဆိပ် / ဘန်းဆူး",
            subtitleTh = "ดอนเมือง • รังสิต • ตลิ่งชัน",
            fareMm = "၂၀ ฿ အမြင့်ဆုံး",
            fareTh = "สูงสุด 20 บาท",
            badgeMm = "လေဆိပ်နှင့် မီးရထား",
            badgeTh = "เชื่อมสนามบิน",
            icon = Icons.Default.Commute,
            lightBrandColor = Color(0xFF9F1239),
            darkBrandColor = Color(0xFFFB7185),
            lightBgColor = Color(0xFFFFECEE),
            darkBgColor = Color(0xFF4C0519),
            testTag = "card_quick_srt"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("grid_transit_quick_access")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBurmese) "အမြန်သွား ပို့ဆောင်ရေးကတ်များ" else "บริการขนส่งหลัก เข้าถึงด่วน",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isBurmese) "BTS၊ MRT၊ ဘတ်စ်ကားနှင့် SRT မီးရထားလိုင်းများ" else "BTS, MRT, รถเมล์ ขสมก. และรถไฟชานเมือง SRT",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = if (isBurmese) "အဓိက ၄ မျိုး" else "4 บริการหลัก",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid Layout using pairs of Rows with equal weight distribution
        // Row 1: BTS & MRT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                item = items[0],
                isBurmese = isBurmese,
                isDark = isDark,
                onClick = { onSelectTransitType(TransportType.BTS) },
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                item = items[1],
                isBurmese = isBurmese,
                isDark = isDark,
                onClick = { onSelectTransitType(TransportType.MRT) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Bus & SRT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                item = items[2],
                isBurmese = isBurmese,
                isDark = isDark,
                onClick = onSelectBusService,
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                item = items[3],
                isBurmese = isBurmese,
                isDark = isDark,
                onClick = { onSelectTransitType(TransportType.SRT) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Single Quick-Access Transit Card featuring Material 3 Iconography,
 * clear typography, fare indicator, and distinctive brand accents.
 */
@Composable
fun QuickAccessCard(
    item: TransitQuickAccessItem,
    isBurmese: Boolean = LocalIsBurmese.current,
    isDark: Boolean = LocalIsDarkMode.current,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandColor = if (isDark) item.darkBrandColor else item.lightBrandColor
    val iconBgColor = if (isDark) item.darkBgColor else item.lightBgColor

    OutlinedCard(
        modifier = modifier
            .heightIn(min = 150.dp)
            .testTag(item.testTag)
            .clickable(
                role = Role.Button,
                onClickLabel = if (isBurmese) "${item.titleMm} သို့ သွားမည်" else "เปิด ${item.titleTh}",
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.outlineVariant
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Brand Icon + Navigation Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular/Squircle Material 3 Icon Container (min 44dp touch clarity)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = if (isBurmese) item.titleMm else item.titleTh,
                        tint = brandColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Action arrow indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(brandColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Navigate",
                        tint = brandColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle: Titles
            Column {
                Text(
                    text = if (isBurmese) item.titleMm else item.titleTh,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBurmese) item.subtitleMm else item.subtitleTh,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom: Fare Pill & Quick Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fare Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = brandColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isBurmese) item.fareMm else item.fareTh,
                        color = brandColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Feature Badge
                Text(
                    text = if (isBurmese) item.badgeMm else item.badgeTh,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
