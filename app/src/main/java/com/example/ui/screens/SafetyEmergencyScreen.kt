package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun SafetyEmergencyScreen(
    state: BangkokUiState,
    contacts: List<EmergencyContact>,
    onDialEmergencyNumber: (String) -> Unit
) {
    val isBurmese = state.isBurmese

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Emergency Top Banner (Police 191 & Tourist Police 1155)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ThaiRed)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isBurmese)
                            "အရေးပေါ်အခြေအနေများတွင် အောက်ပါခလုတ်များကို နှိပ်၍ တိုက်ရိုက်ဖုန်းခေါ်ဆိုနိုင်ပါသည် (အခမဲ့)"
                        else
                            "แตะปุ่มเพื่อโทรออกสายด่วนฉุกเฉินได้ทันที (โทรฟรีตลอด 24 ชม.)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. Emergency Contacts List (1-Tap Dialing)
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
                            color = TextDark,
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
                        Text(text = if (isBurmese) "ခေါ်ဆိုမည်" else "โทร", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Migrant Worker Safety Checklist Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBurmese) "မြန်မာအလုပ်သမားများအတွက် လုံခြုံရေးစည်းမျဉ်း (၁၀) ချက်" else "10 ข้อควรระวังเพื่อความปลอดภัยในการเดินทาง",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val safetyRules = listOf(
                        "၁။ ဆိုင်ကယ်ကယ်ရီ စီးနင်းရာတွင် ဦးထုပ်အမြဲတောင်းဆောင်းပါ။ (ဒဏ်ငွေ ၅၀၀ ဘတ် မထိစေရန်)",
                        "၂။ မီတာမဖွင့်ဘဲ စျေးဖြတ်တောင်းသော တက္ကစီများကို လုံးဝမစီးပါနှင့်။",
                        "၃။ အငှားယာဉ်တက်စီးလျှင် ယာဉ်နံပါတ်ပြားနှင့် ယာဉ်မောင်းပုံကို မိသားစု သို့မဟုတ် မိတ်ဆွေထံ မျှဝေထားပါ။",
                        "၄။ မူရင်း Passport သို့မဟုတ် စာရွက်စာတမ်း အစစ်အမှန်များကို မည်သည့်ယာဉ်မောင်းကိုမျှ ပေးမထားပါနှင့်။ (မိတ္တူသာ ပြပါ)",
                        "၅။ တရားမဝင် လိုင်စင်မဲ့အငှားကားများနှင့် အနက်ရောင်လိုင်စင်ပြားကားများကို ရှောင်ကြဉ်ပါ။",
                        "၆။ ညဉ့်နက်ချိန်တွင် လူရှင်းသော အမှောင်ထု မှတ်တိုင်များတွင် တစ်ဦးတည်း မစောင့်ပါနှင့်။",
                        "၇။ ယာဉ်ပေါ်မှ မဆင်းမီ မိမိအိတ်၊ ပိုက်ဆံအိတ်နှင့် ဖုန်း ကျန်မကျန် အမြဲစစ်ဆေးပါ။",
                        "၈။ ယာဉ်ခအပိုတောင်းခြင်း၊ မတရားပြုခံရခြင်းရှိပါက ၁၅၈၄ ကုန်းလမ်းပို့ဆောင်ရေးသို့ တိုင်ကြားပါ။",
                        "၉။ မှုခင်း၊ တိုက်ခိုက်ခံရမှုများရှိပါက ရဲတပ်ဖွဲ့ ၁၉၁ သို့ ချက်ချင်းခေါ်ဆိုပါ။",
                        "၁၀။ သံရုံးအကူအညီလိုအပ်ပါက မြန်မာသံရုံး +66 2 233 2237 သို့ ဆက်သွယ်ပါ။"
                    )

                    safetyRules.forEach { rule ->
                        Text(
                            text = rule,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = TextDark,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
