package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun AdminVerificationScreen(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onSubmitCommunityReport: (String, String, String) -> Unit
) {
    val isBurmese = state.isBurmese
    var reportPlaceName by remember { mutableStateOf("") }
    var reportIssueType by remember { mutableStateOf("ယာဉ်ခ ပြောင်းလဲမှု") }
    var reportDetails by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin & Verification Policy Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBurmese) "သယ်ယူပို့ဆောင်ရေး အချက်အလက် စစ်ဆေးမှုစနစ်" else "ระบบตรวจสอบความถูกต้องของข้อมูล (Data Verification)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBurmese)
                            "Bangkok Go MM သည် ထိုင်းနိုင်ငံ BMTA၊ BTS၊ MRT၊ SRT တို့၏ တရားဝင်အချက်အလက်များကိုသာ စိစစ်ဖော်ပြပါသည်။ အတည်မပြုနိုင်သော သို့မဟုတ် မှားယွင်းနိုင်သော အချိန်ဇယားများကို တိုက်ရိုက်ဖော်ပြခြင်းမပြုပါ။"
                        else
                            "ระบบใช้ข้อมูลที่ตรวจสอบแล้วจาก ขสมก. BTS MRT และ รฟท. ไม่แสดงข้อมูลที่ไม่ได้รับการยืนยัน",
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "စစ်ဆေးပြီးနေရာပေါင်း: ${places.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Confidence: 98%+", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Community Feedback / Report Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = WarningOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBurmese) "အချက်အလက် အမှားပြင်ဆင်ချက် တင်ပြရန်" else "รายงานข้อมูลคลาดเคลื่อน",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reportPlaceName,
                        onValueChange = { reportPlaceName = it },
                        label = { Text(text = if (isBurmese) "နေရာ သို့မဟုတ် ကားနံပါတ်" else "ชื่อสถานที่หรือสายรถ") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_report_place"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        label = { Text(text = if (isBurmese) "အသေးစိတ် (ဥပမာ- မှတ်တိုင်ရွှေ့သွားခြင်း၊ ယာဉ်ခ ပြောင်းခြင်း)" else "รายละเอียด") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_report_details"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (reportPlaceName.isNotBlank() && reportDetails.isNotBlank()) {
                                onSubmitCommunityReport(reportPlaceName, reportIssueType, reportDetails)
                                reportPlaceName = ""
                                reportDetails = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_report"),
                        colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isBurmese) "ပြင်ဆင်ချက် ပေးပို့မည်" else "ส่งข้อมูลตรวจสอบ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Verified Data Records List
        item {
            Text(
                text = if (isBurmese) "စိစစ်ပြီး အချက်အလက် မှတ်တမ်းများ (Verification Logs):" else "รายการที่ได้รับการตรวจสอบแล้ว:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(places) { place ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = place.nameMm, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "အတည်ပြုပြီး (${place.confidenceScore}%)",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = place.nameTh, fontSize = 12.sp, color = BangkokBlue)

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "နောက်ဆုံးစစ်ဆေး: ${place.lastVerifiedAt}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "အရင်းအမြစ်: ${place.source}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
