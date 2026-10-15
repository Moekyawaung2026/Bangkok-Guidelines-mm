package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.TransportPlace
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailSheet(
    place: TransportPlace,
    isBurmese: Boolean,
    onDismiss: () -> Unit,
    onDirectionsClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onSpeakThai: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransportTypeBadge(type = place.type, isBurmese = isBurmese)
                StatusBadge(status = place.liveStatus, isBurmese = isBurmese)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Names
            Text(
                text = place.nameMm,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = place.nameTh,
                style = MaterialTheme.typography.titleMedium,
                color = BangkokBlue,
                fontWeight = FontWeight.SemiBold
            )
            if (place.nameEn.isNotBlank()) {
                Text(
                    text = place.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Matrix: Operating Hours, Fare, Confidence
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⏰ ဖွင့်ချိန်", fontSize = 11.sp, color = TextMuted)
                        Text(text = "${place.openingTime} - ${place.closingTime}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💰 ယာဉ်ခ", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = if (place.fareMin > 0) "${place.fareMin}-${place.fareMax} THB" else "အခမဲ့/ပုံသေ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SuccessGreen
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛡 စစ်ဆေးပြီး", fontSize = 11.sp, color = TextMuted)
                        Text(text = "${place.confidenceScore}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BangkokBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Address
            if (place.addressMm.isNotBlank()) {
                Text(
                    text = if (isBurmese) "တည်နေရာ လိပ်စာ:" else "ที่อยู่:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = place.addressMm, fontSize = 13.sp, color = TextDark)
                Text(text = place.addressTh, fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Exits and Connections
            if (place.exitDetailsMm.isNotBlank()) {
                Text(
                    text = if (isBurmese) "ထွက်ပေါက်များနှင့် အချိတ်အဆက်:" else "ทางออกและการเชื่อมต่อ:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = BurmeseGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBurmese) place.exitDetailsMm else place.exitDetailsTh,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Accessibility Features
            Text(
                text = if (isBurmese) "အဆောက်အအုံ အထောက်အကူပြုမှု:" else "สิ่งอำนวยความสะดวก:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("♿ ဘီးတပ်ကုလားထိုင်", fontSize = 11.sp) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("🛗 ဓာတ်လှေကား (Elevator)", fontSize = 11.sp) }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("🪜 စက်လှေကား", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verification Source & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "အရင်းအမြစ်: ${place.source}", fontSize = 11.sp, color = TextMuted)
                Text(text = "စစ်ဆေးချိန်: ${place.lastVerifiedAt}", fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons (Minimum 48dp height)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDirectionsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_sheet_directions"),
                    colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBurmese) "လမ်းညွှန်" else "เส้นทาง", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val phrase = place.driverPhraseTh.ifBlank { "ไป ${place.nameTh} ครับ" }
                        onShowDriverClick(phrase, place.nameMm)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_sheet_driver"),
                    colors = ButtonDefaults.buttonColors(containerColor = BurmeseGold, contentColor = TextDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBurmese) "ဒရိုင်ဘာအားပြ" else "ให้คนขับดู", fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "Save", tint = BangkokBlue)
                }
            }
        }
    }
}
