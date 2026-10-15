package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun TaxiSafetyScreen(
    state: BangkokUiState,
    onShowDriverClick: (String, String) -> Unit,
    onSpeakThai: (String) -> Unit,
    onCallEmergency: (String) -> Unit
) {
    val isBurmese = state.isBurmese
    var selectedSubTab by remember { mutableStateOf("TAXI") } // TAXI or MOTO

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top SubTab Switcher (Taxi vs Motorcycle Taxi)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { selectedSubTab = "TAXI" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("subtab_taxi"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == "TAXI") BangkokBlue else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedSubTab == "TAXI") Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalTaxi, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isBurmese) "တက္ကစီ (Taxi)" else "แท็กซี่มิเตอร์", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { selectedSubTab = "MOTO" },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("subtab_moto"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == "MOTO") BurmeseGold else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = TextDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isBurmese) "ဆိုင်ကယ်ကယ်ရီ" else "วินมอเตอร์ไซค์", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedSubTab == "TAXI") {
            // TAXI SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PriceCheck, contentDescription = null, tint = BangkokBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) "မီတာတက္ကစီ စီးနင်းမှု စည်းမျဉ်းများ" else "กฎการนั่งแท็กซี่มิเตอร์ในกรุงเทพฯ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Golden Phrase Card: "เปิดมิเตอร์ด้วยครับ/ค่ะ"
                        Surface(
                            color = BurmeseGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "မဖြစ်မနေ ပြောရမည့် စကား (Must Say Phrase):",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "“เปิดมิเตอร์ด้วยครับ/ค่ะ”",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "အသံထွက်: 'ဖွင့်မီတာ ဒိုဝိုင် ခရတ်/ခါ့'",
                                    fontSize = 13.sp,
                                    color = BangkokBlueDark
                                )
                                Text(
                                    text = "မြန်မာအဓိပ္ပာယ်: “မီတာဖွင့်ပေးပါ။”",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ThaiRed
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onSpeakThai("เปิดมิเตอร์ด้วยครับ") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("btn_speak_meter"),
                                        colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "အသံဖွင့်ပြမည်", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onShowDriverClick(
                                                "เปิดมิเตอร์ด้วยครับ/ค่ะ ไม่กดมิเตอร์ไม่ไปครับ",
                                                "မီတာဖွင့်ပေးပါ၊ မီတာမဖွင့်ပါက မစီးပါ"
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("btn_flashcard_meter"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "စာလုံးကြီးပြမည်", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fare Rules Explanation
                        Text(
                            text = if (isBurmese) "ယာဉ်ခနှုန်းထား အချက်အလက်များ:" else "อัตราค่าโดยสาร:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBurmese)
                                "• အစပျိုး မီတာခ: ၃၅ ဘတ် (ပထမ ၁ ကီလိုမီတာ)\n" +
                                "• ယာဉ်ကြောပိတ်ဆို့မှု: တစ်မိနစ်လျှင် ၃ ဘတ်\n" +
                                "• အဝေးပြေးအမြန်လမ်း (Expressway): ခရီးသည်မှ ၅၀–၇၀ ဘတ် တိုက်ရိုက်ပေးဆောင်ရပါသည် (အမြန်လမ်းမတက်လိုပါက ကြိုတင်ပြောပါ)\n" +
                                "• လေဆိပ်အထူးကြေး: သုဝဏ္ဏဘူမိ သို့မဟုတ် ဒွန်မောင်းလေဆိပ် အငှားဂိတ်မှစီးလျှင် အပို ၅၀ ဘတ် တရားဝင် ပေးရပါသည်"
                            else
                                "• เริ่มต้น 35 บาท\n• รถติดนาทีละ 3 บาท\n• ทางด่วนผู้โดยสารเป็นผู้จ่าย\n• ค่าเซอร์วิสชาร์จสนามบิน 50 บาท",
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Complaint Button (1584)
                        Button(
                            onClick = { onCallEmergency("1584") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_call_1584"),
                            colors = ButtonDefaults.buttonColors(containerColor = ThaiRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) "မီတာမဖွင့်ပါက ၁၅၈၄ သို့ တိုင်ကြားရန် ဖုန်းခေါ်မည်" else "โทร 1584 ร้องเรียนแท็กซี่ไม่กดมิเตอร์",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // MOTORCYCLE TAXI SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TwoWheeler, contentDescription = null, tint = WarningOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) "ဆိုင်ကယ်ကယ်ရီ လုံခြုံရေး လမ်းညွှန်" else "ความปลอดภัยการนั่งวินมอเตอร์ไซค์",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Helmet Warning Banner (500 THB fine)
                        Surface(
                            color = ThaiRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ThaiRed)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ThaiRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.SportsMotorsports, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBurmese) "ဦးထုပ်မဆောင်းပါက ၅၀၀ ဘတ် ဒဏ်ငွေကျခံရပါမည်" else "ไม่สวมหมวกกันน็อก ปรับสูงสุด 500 บาท",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ThaiRed
                                    )
                                    Text(
                                        text = if (isBurmese) "ဆိုင်ကယ်မတက်မီ 'မော့ဂန်နော့' (ဦးထုပ်) အမြဲတောင်းဆောင်းပါ" else "ขอยืมหมวกกันน็อกก่อนขึ้นทุกครั้ง",
                                        fontSize = 12.sp,
                                        color = TextDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Helmet Request Phrase
                        Surface(
                            color = BurmeseGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "“ขอยืมหมวกกันน็อกด้วยครับ/ค่ะ”", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "မြန်မာလို: “ဆိုင်ကယ်ဦးထုပ် ပေးပါလား။”", fontSize = 13.sp, color = TextDark)
                                }
                                IconButton(onClick = { onSpeakThai("ขอยืมหมวกกันน็อกด้วยครับ") }) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Play", tint = BangkokBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // How to verify licensed driver
                        Text(
                            text = if (isBurmese) "တရားဝင် ယာဉ်မောင်း စစ်ဆေးနည်း:" else "วิธีสังเกตวินมอเตอร์ไซค์ที่ถูกกฎหมาย:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBurmese)
                                "၁။ လိမ္မော်ရောင်အင်္ကျီ (ဝင်းအင်္ကျီ) ဝတ်ဆင်ထားပြီး ကျောဘက်တွင် နံပါတ်နှင့် ဝင်းအမည် ပါရှိရပါမည်။\n" +
                                "၂။ ရင်ဘတ်တွင် ယာဉ်မောင်းလိုင်စင်ကတ်ပြားနှင့် ဓာတ်ပုံ ချိတ်ဆွဲထားရပါမည်။\n" +
                                "၃။ ဆိုင်ကယ်သည် အဝါရောင်လိုင်စင်ပြား (အများပြည်သူသယ်ယူပို့ဆောင်ရေး) ဖြစ်ရပါမည်။\n" +
                                "၄။ လမ်းထိပ်ရှိ ဆိုင်းဘုတ်တွင် သတ်မှတ်ထားသော ယာဉ်ခဇယားအတိုင်းသာ ပေးဆောင်ရပါမည်။ (အပိုတောင်းပါက ၁၅၈၄ သို့ တိုင်ကြားနိုင်)"
                            else
                                "1. สวมเสื้อกั๊กสีส้ม มีเบอร์และชื่อวิน\n2. ติดบัตรประจำตัวผู้ขับขี่ที่หน้าอก\n3. ป้ายทะเบียนสีเหลือง",
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rain & Night warning
                        Surface(
                            color = WarningOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Thunderstorm, contentDescription = null, tint = WarningOrange)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBurmese) "မိုးရွာချိန် သို့မဟုတ် ညဉ့်နက်ချိန်များတွင် ဆိုင်ကယ်အစား BTS သို့မဟုတ် ဘတ်စ်ကားကို ဦးစားပေးစီးပါ" else "ช่วงฝนตกหรือกลางคืน แนะนำให้ใช้ BTS หรือรถเมล์แทนเพื่อความปลอดภัย",
                                    fontSize = 12.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
