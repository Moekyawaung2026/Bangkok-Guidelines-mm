package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.EmergencyContact
import com.example.data.model.SafetyCategory
import com.example.data.model.SafetyTipItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyEmergencyScreen(
    state: BangkokUiState,
    contacts: List<EmergencyContact>,
    onDialEmergencyNumber: (String) -> Unit,
    onSelectCategory: (SafetyCategory) -> Unit = {},
    onSearchChange: (String) -> Unit = {}
) {
    val isBurmese = state.isBurmese

    // Filter tips based on search query and category
    val currentCategory = state.selectedSafetyCategory
    val searchQuery = state.safetySearchQuery

    val displayedTips = remember(state.safetyTips, currentCategory, searchQuery) {
        val catFiltered = if (currentCategory == SafetyCategory.ALL) {
            state.safetyTips
        } else {
            state.safetyTips.filter { it.category == currentCategory }
        }

        if (searchQuery.isBlank()) {
            catFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            catFiltered.filter { tip ->
                tip.titleMm.lowercase().contains(q) ||
                tip.titleTh.lowercase().contains(q) ||
                tip.titleEn.lowercase().contains(q) ||
                tip.summaryMm.lowercase().contains(q) ||
                tip.summaryTh.lowercase().contains(q) ||
                tip.detailMm.lowercase().contains(q) ||
                tip.relevantHotline.contains(q) ||
                tip.dosMm.any { it.lowercase().contains(q) } ||
                tip.dontsMm.any { it.lowercase().contains(q) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Static Guide Badge & Emergency Top Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_emergency_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ThaiRed)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Sos, contentDescription = null, tint = ThaiRed)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBurmese) "အရေးပေါ် ဖုန်းနံပါတ်များ (၂၄ နာရီ)" else "เบอร์โทรฉุกเฉิน 24 ชั่วโมง",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Offline Local Resource Badge
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Offline Resource",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isBurmese)
                            "အရေးပေါ်အခြေအနေများတွင် အောက်ပါခလုတ်များကို နှိပ်၍ တိုက်ရိုက်ဖုန်းခေါ်ဆိုနိုင်ပါသည် (ထိုင်းတစ်နိုင်ငံလုံး အခမဲ့)"
                        else
                            "แตะปุ่มเพื่อโทรออกสายด่วนฉุกเฉินได้ทันที (โทรฟรีตลอด 24 ชม. ทั่วประเทศ)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. Emergency Contacts List (1-Tap Dialing)
        item {
            Text(
                text = if (isBurmese) "အရေးပေါ် ဆက်သွယ်ရန် ဌာနများ" else "หน่วยงานช่วยเหลือฉุกเฉิน",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(contacts) { contact ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_emergency_${contact.number}"),
                shape = RoundedCornerShape(14.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = ThaiRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "☎️ ${contact.number}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ThaiRed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = contact.titleEn,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBurmese) contact.titleMm else contact.titleTh,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBurmese) contact.descriptionMm else contact.descriptionTh,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Call Button (Height >= 48dp)
                    Button(
                        onClick = { onDialEmergencyNumber(contact.number) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_dial_${contact.number}"),
                        colors = ButtonDefaults.buttonColors(containerColor = ThaiRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isBurmese) "ခေါ်မည်" else "โทร", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Static Safety Tips Guide Section Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BangkokBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBurmese) "မြန်မာအလုပ်သမားများအတွက် လုံခြုံရေး လမ်းညွှန်" else "คู่มือความปลอดภัยแรงงานเมียนมา",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Text(
                text = if (isBurmese)
                    "ဖုန်းထဲတွင် တိုက်ရိုက်သိမ်းဆည်းထားသော အော့ဖ်လိုင်း လမ်းညွှန် (စာရွက်စာတမ်း၊ တက္ကစီ၊ ဆိုင်ကယ်၊ ညဆိုင်း၊ အလုပ်သမားအခွင့်အရေး)"
                else
                    "คู่มือออฟไลน์ในเครื่อง (เอกสารประจำตัว, แท็กซี่, มอเตอร์ไซค์, กะดึก, สิทธิแรงงาน)",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Search Bar for Safety Guide
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = if (isBurmese) "လုံခြုံရေး အကြံပြုချက်များ ရှာဖွေပါ (ဥပမာ- ပတ်စ်ပို့၊ ဦးထုပ်၊ မီတာ)..." else "ค้นหาคู่มือความปลอดภัย (พาสปอร์ต, หมวกกันน็อก, มิเตอร์)...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = BangkokBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_safety_search"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BangkokBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // 5. Category Chips Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SafetyCategory.values()) { category ->
                    val isSelected = category == currentCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(category) },
                        label = {
                            Text(
                                text = if (isBurmese) category.titleMm else category.titleTh,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BangkokBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_safety_cat_${category.name}")
                    )
                }
            }
        }

        // 6. Detailed Safety Tips List
        if (displayedTips.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isBurmese) "ရှာဖွေမှုနှင့် ကိုက်ညီသော လုံခြုံရေး အချက်အလက် မရှိပါ" else "ไม่พบข้อมูลความปลอดภัยที่ตรงกับการค้นหา",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(displayedTips) { tip ->
                SafetyTipCard(
                    tip = tip,
                    isBurmese = isBurmese,
                    onDialHotline = onDialEmergencyNumber
                )
            }
        }
    }
}

@Composable
fun SafetyTipCard(
    tip: SafetyTipItem,
    isBurmese: Boolean,
    onDialHotline: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_safety_tip_${tip.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge & Importance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = BangkokBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isBurmese) tip.category.titleMm else tip.category.titleTh,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BangkokBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (tip.importance == "CRITICAL") {
                    Surface(
                        color = ThaiRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ThaiRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBurmese) "အလွန်အရေးကြီး" else "สำคัญมาก",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ThaiRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = if (isBurmese) tip.titleMm else tip.titleTh,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isBurmese) tip.summaryMm else tip.summaryTh,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detailed Explanation
            Text(
                text = if (isBurmese) tip.detailMm else tip.detailTh,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            // Recommended actions (Dos)
            if (tip.dosMm.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isBurmese) "✅ လိုက်နာဆောင်ရွက်ရန်များ:" else "✅ สิ่งที่ควรปฏิบัติ:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                tip.dosMm.forEach { itemText ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = itemText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Prohibited / Warning actions (Don'ts)
            if (tip.dontsMm.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isBurmese) "❌ လုံးဝရှောင်ကြဉ်ရန်များ:" else "❌ สิ่งที่ควรหลีกเลี่ยง:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThaiRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                tip.dontsMm.forEach { itemText ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = ThaiRed,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = itemText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Direct Call to Relevant Hotline if available
            if (tip.relevantHotline.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onDialHotline(tip.relevantHotline) },
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_tip_hotline_${tip.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ThaiRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call Hotline",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBurmese) "အကူအညီဖုန်း ${tip.relevantHotline} ခေါ်ဆိုမည်" else "โทรสายด่วน ${tip.relevantHotline}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
