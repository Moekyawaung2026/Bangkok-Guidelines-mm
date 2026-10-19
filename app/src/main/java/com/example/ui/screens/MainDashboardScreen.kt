package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransportPlace
import com.example.data.model.TransportType
import com.example.data.model.TransportationTypeItem
import com.example.data.repository.DefaultTransportationTypeRepository
import com.example.ui.components.HomeScreenTransitGrid
import com.example.ui.components.TransportationTypeCardList
import com.example.ui.components.TransportPlaceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BangkokUiState

/**
 * Filter categories for categorized transport service navigation cards.
 */
enum class TransportCategoryFilter(val labelMm: String, val labelTh: String, val labelEn: String, val icon: ImageVector) {
    ALL("အားလုံး", "ทั้งหมด", "All Services", Icons.Default.DirectionsTransit),
    TRAIN("ရထားလိုင်းများ", "รถไฟฟ้า/รถไฟ", "Rail & Trains", Icons.Default.Train),
    BUS("ဘတ်စ်ကား", "รถเมล์ ขสมก.", "City Buses", Icons.Default.DirectionsBus),
    BOAT("စက်လှေ/သင်္ဘော", "เรือด่วน/เรือคลอง", "Boats & Ferries", Icons.Default.DirectionsBoat),
    ROAD("တက္ကစီ/ကားဂိတ်", "แท็กซี่/รถตู้", "Taxi & Vans", Icons.Default.LocalTaxi)
}

/**
 * Data structure defining each categorized transport service card on the dashboard.
 */
data class CategorizedTransportService(
    val id: String,
    val category: TransportCategoryFilter,
    val titleMm: String,
    val titleTh: String,
    val titleEn: String,
    val networkDescriptionMm: String,
    val networkDescriptionTh: String,
    val networkDescriptionEn: String,
    val fareRangeMm: String,
    val fareRangeTh: String,
    val fareRangeEn: String,
    val operatingHours: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val keyTipsMm: String,
    val keyTipsTh: String,
    val targetTab: AppTab,
    val primaryActionTextMm: String,
    val primaryActionTextTh: String,
    val secondaryActionTextMm: String,
    val secondaryActionTextTh: String,
    val badgeTextMm: String,
    val badgeTextTh: String,
    val testTag: String
)

/**
 * Main dashboard UI component in Jetpack Compose that acts as the entry point,
 * featuring categorized navigation cards for BTS, MRT, Bus, and other transport services.
 */
@Composable
fun MainDashboardScreen(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onNavigateTab: (AppTab) -> Unit,
    onGpsClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickDestinationSelect: (String) -> Unit,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onAiAssistantClick: () -> Unit,
    onSelectTransitType: (TransportType) -> Unit = {},
    onSelectTransportationType: (TransportationTypeItem) -> Unit = {},
    onVoiceSearchClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isBurmese = state.isBurmese
    var selectedCategoryFilter by remember { mutableStateOf(TransportCategoryFilter.ALL) }
    var expandedCardId by remember { mutableStateOf<String?>(null) }

    // Categorized transport service navigation data
    val categorizedServices = remember {
        listOf(
            // 1. BTS Skytrain Card
            CategorizedTransportService(
                id = "bts",
                category = TransportCategoryFilter.TRAIN,
                titleMm = "BTS မိုးပျံရထား (Skytrain)",
                titleTh = "รถไฟฟ้าบีทีเอส (BTS Skytrain)",
                titleEn = "BTS Skytrain",
                networkDescriptionMm = "ဆူခုမ်ဝစ်လိုင်း (စိမ်းဖျော့) + ဆီလုံလိုင်း (စိမ်းရင့်) + ရွှေရောင်လိုင်း (ICONSIAM)",
                networkDescriptionTh = "สายสุขุมวิท (สีเขียวอ่อน) + สายสีลม (สีเขียวเข้ม) + สายสีทอง",
                networkDescriptionEn = "Sukhumvit (Light Green) + Silom (Dark Green) + Gold Line",
                fareRangeMm = "၁၇ မှ ၆၂ ဘတ် (ခရီးအကွာအဝေးအလိုက်)",
                fareRangeTh = "17 - 62 บาท (ตามระยะทาง)",
                fareRangeEn = "17 - 62 THB (By distance)",
                operatingHours = "05:30 - 24:00",
                primaryColor = Color(0xFF009A44), // BTS Green
                secondaryColor = Color(0xFFE8F5E9),
                icon = Icons.Default.Train,
                keyTipsMm = "လက်မှတ်အရောင်းစက်တွင် ဒင်္ဂါးပြား သို့မဟုတ် Rabbit ကတ် သုံးနိုင်ပါသည်။ ဆယမ် (Siam) ဘူတာတွင် လိုင်းလဲလှယ်နိုင်ပါသည်။",
                keyTipsTh = "ซื้อเหรียญโดยสารที่ตู้หรือแตะบัตร Rabbit เปลี่ยนสายได้ที่สถานีสยาม",
                targetTab = AppTab.LINES,
                primaryActionTextMm = "BTS ဘူတာများ ကြည့်မည်",
                primaryActionTextTh = "ดูสถานี BTS ทั้งหมด",
                secondaryActionTextMm = "လမ်းကြောင်းရှာမည်",
                secondaryActionTextTh = "วางแผนเดินทาง",
                badgeTextMm = "မြန်ဆန်/အဲကွန်း",
                badgeTextTh = "รวดเร็ว ปลอดภัย",
                testTag = "card_nav_bts"
            ),

            // 2. MRT Subway & Monorail Card
            CategorizedTransportService(
                id = "mrt",
                category = TransportCategoryFilter.TRAIN,
                titleMm = "MRT မြေအောက်ရထားနှင့် မိုနိုရိုင်း",
                titleTh = "รถไฟฟ้ามหานคร (MRT Subway)",
                titleEn = "MRT Subway & Monorails",
                networkDescriptionMm = "အပြာရောင်လိုင်း (ပတ်လမ်း) + ခရမ်းရောင်လိုင်း + အဝါရောင်နှင့် ပန်းရောင် မိုနိုရိုင်းများ",
                networkDescriptionTh = "สายสีน้ำเงิน (วงกลม) + สายสีม่วง + สายสีเหลือง และสายสีชมพู",
                networkDescriptionEn = "Blue Line (Loop) + Purple Line + Yellow & Pink Monorails",
                fareRangeMm = "၁၇ မှ ၄၅ ဘတ် (Visa/Mastercard သုံးနိုင်)",
                fareRangeTh = "17 - 45 บาท (แตะบัตร EMV ได้)",
                fareRangeEn = "17 - 45 THB (EMV Contactless / Token)",
                operatingHours = "06:00 - 24:00",
                primaryColor = Color(0xFF003399), // MRT Blue
                secondaryColor = Color(0xFFE3F2FD),
                icon = Icons.Default.Subway,
                keyTipsMm = "အနက်ရောင် တိုကင်ပြား သို့မဟုတ် Visa/Mastercard Contactless ကတ်ဖြင့် ကောင်တာမလိုဘဲ တိုက်ရိုက်ဖြတ်စီးနိုင်ပါသည်။",
                keyTipsTh = "แตะบัตรเครดิต/เดบิต EMV เข้าเกทได้ทันที ไม่ต้องต่อคิวซื้อเหรียญ",
                targetTab = AppTab.LINES,
                primaryActionTextMm = "MRT ဘူတာများ ကြည့်မည်",
                primaryActionTextTh = "ดูสถานี MRT ทั้งหมด",
                secondaryActionTextMm = "ဘန်းဆူးဗဟိုဘူတာ",
                secondaryActionTextTh = "สถานีกลางบางซื่อ",
                badgeTextMm = "ယာဉ်ကြောပိတ်ဆို့မှု ကင်းလွတ်",
                badgeTextTh = "เลี่ยงรถติด 100%",
                testTag = "card_nav_mrt"
            ),

            // 3. BMTA City Bus Card
            CategorizedTransportService(
                id = "bus",
                category = TransportCategoryFilter.BUS,
                titleMm = "ဘတ်စ်ကားလိုင်းများ (BMTA ขสมก.)",
                titleTh = "รถเมล์ ขสมก. และรถร่วมบริการ",
                titleEn = "Bangkok City Bus (BMTA)",
                networkDescriptionMm = "အနီရောင်ရိုးရိုးကား (၈ ဘတ်) + အပြာ/လိမ္မော် အဲကွန်းကား (၁၂-၂၅ ဘတ်) + ညလုံးပေါက်ကားများ",
                networkDescriptionTh = "รถร้อนครีมแดง (8 บาท) + รถแอร์ (12-25 บาท) + รถกะสว่าง 24 ชม.",
                networkDescriptionEn = "Regular Red (8 THB) + AC Euro II (12-25 THB) + Night Buses",
                fareRangeMm = "၈ မှ ၂၅ ဘတ် (အသက်သာဆုံး သယ်ယူပို့ဆောင်ရေး)",
                fareRangeTh = "8 - 25 บาท (ประหยัดที่สุด)",
                fareRangeEn = "8 - 25 THB (Most Economical)",
                operatingHours = "04:30 - 23:00 (ညလုံးပေါက်လိုင်းများရှိ)",
                primaryColor = Color(0xFFC62828), // BMTA Crimson Red
                secondaryColor = Color(0xFFFFEBEE),
                icon = Icons.Default.DirectionsBus,
                keyTipsMm = "ယာဉ်ပေါ်တက်ပြီး စပယ်ယာထံ ငွေသားပေးချေကာ လက်မှတ်ဖြတ်ပိုင်းကို ဆင်းသည်အထိ သိမ်းထားပါ။ မှတ်တိုင်မရောက်မီ ဘဲလ်တီးပါ။",
                keyTipsTh = "เตรียมเศษเหรียญ จ่ายกระเป๋ารถเมล์ และเก็บตั๋วไว้จนลงจากรถ กดกริ่งก่อนถึงป้าย",
                targetTab = AppTab.BUS,
                primaryActionTextMm = "ဘတ်စ်ကားလိုင်းများ ရှာမည်",
                primaryActionTextTh = "ค้นหาสายรถเมล์",
                secondaryActionTextMm = "အနီးဆုံးမှတ်တိုင်များ",
                secondaryActionTextTh = "ป้ายรถเมล์ใกล้ฉัน",
                badgeTextMm = "အသက်သာဆုံး/လမ်းကြောင်းစုံ",
                badgeTextTh = "ประหยัดที่สุด ครอบคลุม",
                testTag = "card_nav_bus"
            ),

            // 4. SRT Commuter Train & Airport Rail Link Card
            CategorizedTransportService(
                id = "srt",
                category = TransportCategoryFilter.TRAIN,
                titleMm = "SRT မီးရထားနှင့် လေဆိပ်ရထား (ARL)",
                titleTh = "รถไฟชานเมือง รฟท. และแอร์พอร์ต เรล ลิงก์",
                titleEn = "SRT Commuter & Airport Rail Link",
                networkDescriptionMm = "သုဝဏ္ဏဘူမိလေဆိပ်ရထား (ARL) + အနီရောင်လိုင်း (ဒွန်မောင်းလေဆိပ်/ရန်းစစ်) + နယ်ဝေးရိုးရိုးရထား",
                networkDescriptionTh = "แอร์พอร์ตลิงก์สุวรรณภูมิ + รถไฟสายสีแดงดอนเมือง + รถไฟชานเมือง",
                networkDescriptionEn = "Airport Rail Link (Suvarnabhumi) + SRT Red Line + Provincial",
                fareRangeMm = "၂ မှ ၄၅ ဘတ် (လေဆိပ် ၁၅-၄၅ ဘတ်၊ နယ်ဝေး ၂-၂၀ ဘတ်)",
                fareRangeTh = "2 - 45 บาท (ARL 15-45 บ. / รถไฟธรรมดา 2-20 บ.)",
                fareRangeEn = "2 - 45 THB (ARL 15-45 / Rail 2-20)",
                operatingHours = "05:30 - 24:00",
                primaryColor = Color(0xFF6A1B9A), // Purple/Deep Wine
                secondaryColor = Color(0xFFF3E5F5),
                icon = Icons.Default.DirectionsRailway,
                keyTipsMm = "ဖရာယားထိုင်းမှ သုဝဏ္ဏဘူမိသို့ မိနစ် ၃၀ ဖြင့် ရောက်ရှိ။ ဘန်းဆူးဗဟိုဘူတာမှ အယုဒ္ဓယ၊ မဟာချိုင်ဘက်သို့ စျေးသက်သာစွာ စီးနိုင်ပါသည်။",
                keyTipsTh = "เดินทางไปสนามบินสุวรรณภูมิและดอนเมืองสะดวกรวดเร็ว เชื่อมต่อจังหวัดรอบนอก",
                targetTab = AppTab.LINES,
                primaryActionTextMm = "မီးရထားလိုင်းများ ကြည့်မည်",
                primaryActionTextTh = "ดูเส้นทางรถไฟ",
                secondaryActionTextMm = "လေဆိပ်သွားလမ်း",
                secondaryActionTextTh = "เดินทางไปสนามบิน",
                badgeTextMm = "လေဆိပ်နှင့် ပြည်နယ်ဆက်သွယ်ရေး",
                badgeTextTh = "เชื่อมสนามบินและชานเมือง",
                testTag = "card_nav_srt"
            ),

            // 5. Chao Phraya Express & Canal Boats Card
            CategorizedTransportService(
                id = "boat",
                category = TransportCategoryFilter.BOAT,
                titleMm = "သင်္ဘောနှင့် တူးမြောင်းစက်လှေလိုင်းများ",
                titleTh = "เรือด่วนเจ้าพระยา และเรือคลองแสนแสบ",
                titleEn = "Chao Phraya Express & Canal Boats",
                networkDescriptionMm = "ကျောက်ဖရားအမြန်သင်္ဘော (လိမ္မော်ရောင်အလံ ၁၆ ဘတ်) + ဆန်ဆပ်တူးမြောင်းလှေ (ပရာတူနမ်-ဘန်းကပီ)",
                networkDescriptionTh = "เรือด่วนเจ้าพระยาธงส้ม (16 บาทตลอดสาย) + เรือคลองแสนแสบ (ประตูน้ำ-บางกะปิ)",
                networkDescriptionEn = "Chao Phraya Express (Orange Flag 16 THB) + Khlong Saen Saep Canal",
                fareRangeMm = "၁၂ မှ ၂၂ ဘတ် (လမ်းပိတ်ဆို့မှု လုံးဝမရှိ)",
                fareRangeTh = "12 - 22 บาท (เลี่ยงรถติด)",
                fareRangeEn = "12 - 22 THB (Bypass Traffic)",
                operatingHours = "06:00 - 19:30",
                primaryColor = Color(0xFF00838F), // Maritime Cyan/Teal
                secondaryColor = Color(0xFFE0F7FA),
                icon = Icons.Default.DirectionsBoat,
                keyTipsMm = "သာထွန်း (Sathorn Pier) ဆိပ်ကမ်းသည် BTS Saphan Taksin နှင့် ချိတ်ဆက်ထားပါသည်။ လှေအတက်အဆင်းတွင် ကြိုးကို သေချာကိုင်ပါ။",
                keyTipsTh = "ท่าเรือสาทรเชื่อมต่อ BTS สะพานตากสิน ขึ้นลงเรือด้วยความระมัดระวัง",
                targetTab = AppTab.NEARBY,
                primaryActionTextMm = "ဆိပ်ကမ်းများ ရှာမည်",
                primaryActionTextTh = "ดูท่าเรือและเส้นทางน้ำ",
                secondaryActionTextMm = "စကားပြန်ကတ်",
                secondaryActionTextTh = "ข้อความบอกคนเรือ",
                badgeTextMm = "လမ်းပိတ်ရှောင်ကွင်း ရေလမ်း",
                badgeTextTh = "เดินทางทางน้ำรวดเร็ว",
                testTag = "card_nav_boat"
            ),

            // 6. Taxi & Motorcycle Win Card
            CategorizedTransportService(
                id = "taxi",
                category = TransportCategoryFilter.ROAD,
                titleMm = "တက္ကစီနှင့် ဆိုင်ကယ်ကယ်ရီ (Win)",
                titleTh = "แท็กซี่มิเตอร์ และ วินมอเตอร์ไซค์",
                titleEn = "Metered Taxi & Motorcycle Taxi",
                networkDescriptionMm = "တက္ကစီမီတာ (အခြေခံ ၃၅ ဘတ်) + လမ်းကြိုလမ်းကြား ဆိုင်ကယ်ဂိတ်များ (လိမ္မော်ရောင်အင်္ကျီ)",
                networkDescriptionTh = "แท็กซี่มิเตอร์ (เริ่มต้น 35 บาท) + วินมอเตอร์ไซค์เสื้อกั๊กส้มตามปากซอย",
                networkDescriptionEn = "Metered Taxi (From 35 THB) + Regulated Motorcycle Win",
                fareRangeMm = "မီတာ ၃၅ ဘတ် စတင် / ဆိုင်ကယ် ၁၅ မှ ၆၀ ဘတ်",
                fareRangeTh = "มิเตอร์เริ่ม 35 บาท / วิน 15 - 60 บาท",
                fareRangeEn = "Meter Starts 35 THB / Win 15 - 60 THB",
                operatingHours = "၂၄ နာရီ ဝန်ဆောင်မှု (24/7)",
                primaryColor = Color(0xFFF57F17), // Taxi Yellow/Amber
                secondaryColor = Color(0xFFFFFDE7),
                icon = Icons.Default.LocalTaxi,
                keyTipsMm = "တက္ကစီစီးတိုင်း 'ကရုဏာပြု၍ မီတာဖွင့်ပါ (ခိုဖွစ်မီတာခရပ်)' ဟု အမြဲတောင်းဆိုပါ။ ဆိုင်ကယ်စီးလျှင် ဦးထုပ်အမြဲဆောင်းပါ။ မတရားခေါင်းပုံဖြတ်ပါက ၁၅၈၄ သို့ တိုင်ကြားနိုင်ပါသည်။",
                keyTipsTh = "ขึ้นแท็กซี่ต้องขอให้กดมิเตอร์เสมอ ซ้อนท้ายวินมอเตอร์ไซค์ต้องสวมหมวกกันน็อกทุกครั้ง",
                targetTab = AppTab.TAXI,
                primaryActionTextMm = "ယာဉ်စီးလုံခြုံရေးနှင့် မီတာတွက်ချက်",
                primaryActionTextTh = "ความปลอดภัยและคำนวณมิเตอร์",
                secondaryActionTextMm = "ယာဉ်မောင်းပြကတ်",
                secondaryActionTextTh = "บัตรข้อความให้คนขับ",
                badgeTextMm = "တံခါးဝအရောက် ၂၄ နာရီ",
                badgeTextTh = "บริการ 24 ชั่วโมง",
                testTag = "card_nav_taxi"
            ),

            // 7. Intercity Vans & Bus Terminals Card
            CategorizedTransportService(
                id = "terminal",
                category = TransportCategoryFilter.ROAD,
                titleMm = "အဝေးပြေးကားဂိတ်ကြီးများနှင့် ကားလိုင်းများ",
                titleTh = "สถานีขนส่งผู้โดยสาร และคิวรถตู้ชานเมือง",
                titleEn = "Intercity Terminals & Commuter Vans",
                networkDescriptionMm = "မိုချစ် ၂ (မြောက်ပိုင်း/အရှေ့မြောက်) + စိုင်းထိုင်းမိုင် (တောင်ပိုင်း) + ဧကမိုင်း + မဟာချိုင်/စမွတ်ပရာကန်",
                networkDescriptionTh = "หมอชิต 2 (สายเหนือ/อีสาน) + สายใต้ใหม่ + เอกมัย + มหาชัย/สมุทรปราการ",
                networkDescriptionEn = "Mo Chit 2 + Sai Tai Mai + Ekkamai + Mahachai / Samut Prakan",
                fareRangeMm = "၃၀ မှ ၁၅၀ ဘတ် (စက်ရုံဇုန်များနှင့် နယ်စပ်ကားများ)",
                fareRangeTh = "30 - 150 บาท (เชื่อมโยงนิคมอุตสาหกรรม)",
                fareRangeEn = "30 - 150 THB (Factory & Border Vans)",
                operatingHours = "05:00 - 21:00",
                primaryColor = Color(0xFF37474F), // Slate/Grey
                secondaryColor = Color(0xFFECEFF1),
                icon = Icons.Default.DepartureBoard,
                keyTipsMm = "အလုပ်သမားများ အများဆုံးသွားသော မဟာချိုင် (ပင်လယ်စာစက်ရုံများ)၊ စမွတ်ပရာကန်နှင့် မြဝတီနယ်စပ် မဲဆောက်သို့ ကားဂိတ်များမှ တိုက်ရိုက်သွားနိုင်ပါသည်။",
                keyTipsTh = "สถานีศูนย์กลางเดินทางไปยังสมุทรสาคร (มหาชัย), สมุทรปราการ และแม่สอด",
                targetTab = AppTab.LINES,
                primaryActionTextMm = "ကားဂိတ်ကြီးများ ကြည့်မည်",
                primaryActionTextTh = "ดูสถานีขนส่ง",
                secondaryActionTextMm = "မဟာချိုင်သို့ သွားမည်",
                secondaryActionTextTh = "ไปมหาชัย",
                badgeTextMm = "စက်ရုံဇုန်/နယ်စပ် ချိတ်ဆက်",
                badgeTextTh = "เชื่อมต่อนิคมอุตสาหกรรม",
                testTag = "card_nav_van"
            )
        )
    }

    // Filter services according to the active category filter pill
    val filteredServices = remember(selectedCategoryFilter) {
        if (selectedCategoryFilter == TransportCategoryFilter.ALL) {
            categorizedServices
        } else {
            categorizedServices.filter { it.category == selectedCategoryFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("main_dashboard_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Entry Point Hero Header / Core Mission Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BangkokBlue, BangkokBlueDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
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
                                            imageVector = Icons.Default.DirectionsTransit,
                                            contentDescription = null,
                                            tint = TextDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = if (isBurmese) "ဘန်ကောက် သွားလာရေး ပင်မလမ်းညွှန်" else "ศูนย์กลางการเดินทางกรุงเทพฯ (Bangkok Go MM)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = BurmeseGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBurmese) "မြန်မာရွှေ့ပြောင်းလုပ်သားများအတွက် အခမဲ့လမ်းညွှန်" else "คู่มือการเดินทางสำหรับแรงงานเมียนมาในไทย",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            // 100% Offline Database verified pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Offline Ready",
                                        tint = BurmeseGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Offline 100%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBurmese)
                                "“သင်ရောက်ရှိနေသောနေရာမှ BTS၊ MRT၊ ဘတ်စ်ကား၊ သင်္ဘောနှင့် တက္ကစီများကို မြန်မာဘာသာဖြင့် အလွယ်ကူဆုံး ရှာဖွေစီးနင်းပါ”"
                            else
                                "“เดินทางในกรุงเทพฯ ด้วย BTS, MRT, รถเมล์, เรือ และแท็กซี่ ง่าย สะดวก และปลอดภัย เป็นภาษาเมียนมา”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // 2. Large High-Visibility GPS Detection Button
        item {
            Button(
                onClick = onGpsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("btn_large_gps_search"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurmeseGold,
                    contentColor = TextDark
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "GPS Detection",
                    modifier = Modifier.size(28.dp),
                    tint = TextDark
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = if (isBurmese) "ကျွန်ုပ်ရှိနေသောနေရာအနီး ရှာမည်" else "ค้นหาสถานีและป้ายใกล้ตำแหน่งฉัน",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = if (isBurmese) "GPS တည်နေရာဖြင့် အနီးဆုံးမှတ်တိုင်နှင့် ရထားဘူတာများ" else "ตรวจจับพิกัดปัจจุบันและแสดงป้ายรถเมล์ สถานี BTS/MRT",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // 3. Search Field: Where to go? with Voice / AI Assistant Trigger
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .testTag("input_search_destination"),
                shape = RoundedCornerShape(14.dp),
                placeholder = {
                    Text(
                        text = if (isBurmese) "ဘယ်ကိုသွားမလဲ? (စျေး၊ ရထား၊ ဆေးရုံ၊ စက်ရုံ...)" else "จะไปที่ไหน? (ตลาด, สถานี, โรงพยาบาล, นิคมฯ...)",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BangkokBlue
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Search",
                                    tint = TextMuted
                                )
                            }
                        }
                        IconButton(
                            onClick = { onVoiceSearchClick?.invoke() ?: onAiAssistantClick() },
                            modifier = Modifier.testTag("btn_voice_ai_search")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search / AI Assistant",
                                tint = BangkokBlue
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BangkokBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
        }

        // 4. Popular Destination Quick Chips
        item {
            Column {
                Text(
                    text = if (isBurmese) "လူသွားများသော နေရာများ:" else "จุดหมายปลายทางยอดนิยม:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickDestinations = listOf(
                        "ဆယမ် (Siam)",
                        "မဟာချိုင် (Mahachai)",
                        "အောင်ဆန်းရတနာ (Victory Monument)",
                        "စမွတ်ပရာကန် (Samut Prakan)",
                        "မိုချစ် ၂ ကားဂိတ် (Mo Chit 2)",
                        "ပရာတူနမ်စျေး (Pratunam)",
                        "သုဝဏ္ဏဘူမိလေဆိပ်",
                        "ဒွန်မောင်းလေဆိပ်",
                        "ဘန်းဆူးဗဟိုဘူတာ"
                    )
                    items(quickDestinations) { dest ->
                        SuggestionChip(
                            onClick = { onQuickDestinationSelect(dest) },
                            label = { Text(text = dest, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.height(38.dp)
                        )
                    }
                }
            }
        }

        // 5. Home Screen Transit Grid Layout (BTS, MRT, Bus, SRT Quick-Access Cards)
        item {
            HomeScreenTransitGrid(
                isBurmese = isBurmese,
                onSelectTransitType = onSelectTransitType,
                onSelectBusService = { onNavigateTab(AppTab.BUS) }
            )
        }

        // 5.1 Primary Transportation Types List (BTS, MRT, Bus, SRT, Taxi Clickable Cards)
        item {
            val allTypes = remember(state.transportationTypes) {
                if (state.transportationTypes.isNotEmpty()) state.transportationTypes
                else DefaultTransportationTypeRepository().getAllTransportationTypes()
            }
            TransportationTypeCardList(
                transportationTypes = allTypes,
                isBurmese = isBurmese,
                onSelectTransportationType = { item ->
                    onSelectTransportationType(item)
                    when (item.type) {
                        TransportType.BTS -> onSelectTransitType(TransportType.BTS)
                        TransportType.MRT -> onSelectTransitType(TransportType.MRT)
                        TransportType.SRT -> onSelectTransitType(TransportType.SRT)
                        TransportType.BUS, TransportType.BUS_STOP, TransportType.BUS_TERMINAL -> onNavigateTab(AppTab.BUS)
                        TransportType.TAXI, TransportType.MOTORCYCLE_TAXI -> onNavigateTab(AppTab.TAXI)
                        else -> onNavigateTab(AppTab.LINES)
                    }
                }
            )
        }

        // 6. Categorized Navigation Section Header & Category Filter Pills
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBurmese) "ယာဉ်လိုင်းနှင့် ဝန်ဆောင်မှုကဏ္ဍများ" else "บริการขนส่งสาธารณะตามหมวดหมู่",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isBurmese) "BTS၊ MRT၊ ဘတ်စ်ကား၊ သင်္ဘောနှင့် တက္ကစီလမ်းညွှန်" else "BTS, MRT, รถเมล์, เรือ และแท็กซี่",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    // Count of services
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "${filteredServices.size} Services",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Pills Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TransportCategoryFilter.values()) { filter ->
                        val isSelected = selectedCategoryFilter == filter
                        val filterTag = when (filter) {
                            TransportCategoryFilter.ALL -> "category_filter_all"
                            TransportCategoryFilter.TRAIN -> "category_filter_train"
                            TransportCategoryFilter.BUS -> "category_filter_bus"
                            TransportCategoryFilter.BOAT -> "category_filter_boat"
                            TransportCategoryFilter.ROAD -> "category_filter_taxi"
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = filter },
                            label = {
                                Text(
                                    text = if (isBurmese) filter.labelMm else filter.labelTh,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = filter.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BangkokBlue,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag(filterTag)
                        )
                    }
                }
            }
        }

        // 6. Categorized Navigation Cards (BTS, MRT, Bus, SRT, Boats, Taxi, Terminals)
        items(filteredServices, key = { it.id }) { service ->
            val isExpanded = expandedCardId == service.id

            CategorizedTransportServiceCard(
                service = service,
                isBurmese = isBurmese,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedCardId = if (isExpanded) null else service.id
                },
                onPrimaryAction = {
                    onNavigateTab(service.targetTab)
                },
                onSecondaryAction = {
                    when (service.id) {
                        "bts", "mrt", "srt" -> {
                            onQuickDestinationSelect(if (service.id == "srt") "သုဝဏ္ဏဘူမိလေဆိပ်" else "ဆယမ် (Siam)")
                        }
                        "bus" -> onNavigateTab(AppTab.NEARBY)
                        "boat" -> onShowDriverClick("ไปท่าเรือครับ", "ဆိပ်ကမ်းသို့ သွားပါ")
                        "taxi" -> onShowDriverClick("กรุณาเปิดมิเตอร์ด้วยครับ", "ကျေးဇူးပြု၍ မီတာဖွင့်ပါ")
                        "terminal" -> onQuickDestinationSelect("မဟာချိုင် (Mahachai)")
                    }
                }
            )
        }

        // 7. Safety Emergency Shortcut Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(AppTab.SAFETY) }
                    .testTag("dashboard_safety_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ThaiRed.copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(ThaiRed.copy(alpha = 0.4f), ThaiRed.copy(alpha = 0.1f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ThaiRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safety Guide",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isBurmese) "မြန်မာလုပ်သား လုံခြုံရေး လမ်းညွှန်" else "คู่มือความปลอดภัยแรงงานเมียนมา",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ThaiRed
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ThaiRed.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "SOS 191",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ThaiRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBurmese) "တက္ကစီမီတာဖွင့်စီးရန်၊ ဆိုင်ကယ်ဦးထုပ်ဆောင်းရန်၊ သံရုံးနှင့် ၁၉၁ အရေးပေါ်ဖုန်းများ" else "สายด่วนตำรวจ 191, ท่องเที่ยว 1155, สถานทูตเมียนมา และสิทธิแรงงาน",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ThaiRed
                    )
                }
            }
        }

        // 8. Important Transit Hubs Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBurmese) "အဓိက ပို့ဆောင်ရေးဗဟိုဌာနများ" else "ศูนย์กลางการเดินทางที่สำคัญ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isBurmese) "ရထားဘူတာကြီးများနှင့် အဓိကကားဂိတ်များ" else "สถานีเชื่อมต่อสำคัญในกรุงเทพฯ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                TextButton(onClick = { onNavigateTab(AppTab.NEARBY) }) {
                    Text(
                        text = if (isBurmese) "အားလုံးကြည့်မည်" else "ดูทั้งหมด",
                        color = BangkokBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 9. Featured Major Transport Hubs Cards
        val displayPlaces = if (state.searchQuery.isNotBlank()) state.searchResults else places.take(4)
        items(displayPlaces) { place ->
            TransportPlaceCard(
                place = place,
                isBurmese = isBurmese,
                onCardClick = { onPlaceClick(place) },
                onDirectionsClick = { onDirectionsClick(place) },
                onSaveClick = { onSavePlace(place) },
                onShowDriverClick = {
                    onShowDriverClick(
                        place.driverPhraseTh.ifBlank { "ไป ${place.nameTh} ครับ" },
                        place.nameMm
                    )
                }
            )
        }
    }
}

/**
 * Individual categorized navigation card for a transport service (BTS, MRT, Bus, etc.)
 */
@Composable
private fun CategorizedTransportServiceCard(
    service: CategorizedTransportService,
    isBurmese: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(service.testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Icon + Titles + Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Vibrant Service Brand Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(service.secondaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = service.icon,
                        contentDescription = null,
                        tint = service.primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Title & Network Description
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBurmese) service.titleMm else service.titleTh,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (isBurmese) service.networkDescriptionMm else service.networkDescriptionTh,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpanded) 3 else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Service Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = service.primaryColor.copy(alpha = 0.12f),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = if (isBurmese) service.badgeTextMm else service.badgeTextTh,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = service.primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Row: Fares & Operating Hours
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fare Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Fare",
                        tint = service.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBurmese) service.fareRangeMm else service.fareRangeTh,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Hours Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Operating Hours",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = service.operatingHours,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Expandable Key Tips & Instructions
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = service.secondaryColor.copy(alpha = 0.7f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(service.primaryColor.copy(alpha = 0.3f), service.primaryColor.copy(alpha = 0.1f))
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.TipsAndUpdates,
                                contentDescription = "Tips",
                                tint = service.primaryColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) service.keyTipsMm else service.keyTipsTh,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row (Interactive Navigation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Action Button (Filled, brand colored)
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_${service.id}_primary"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = service.primaryColor,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Text(
                        text = if (isBurmese) service.primaryActionTextMm else service.primaryActionTextTh,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Secondary Action Button (Outlined)
                OutlinedButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_${service.id}_secondary"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = service.primaryColor
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(
                            listOf(service.primaryColor.copy(alpha = 0.5f), service.primaryColor.copy(alpha = 0.3f))
                        )
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Text(
                        text = if (isBurmese) service.secondaryActionTextMm else service.secondaryActionTextTh,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Toggle tips button
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("btn_${service.id}_expand")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand Info",
                        tint = service.primaryColor
                    )
                }
            }
        }
    }
}

/**
 * Main dashboard UI component alias for entry point integration.
 */
@Composable
fun MainDashboard(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onNavigateTab: (AppTab) -> Unit,
    onGpsClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickDestinationSelect: (String) -> Unit,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onAiAssistantClick: () -> Unit,
    onSelectTransitType: (TransportType) -> Unit = {},
    onSelectTransportationType: (TransportationTypeItem) -> Unit = {},
    onVoiceSearchClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    MainDashboardScreen(
        state = state,
        places = places,
        onNavigateTab = onNavigateTab,
        onGpsClick = onGpsClick,
        onSearchChange = onSearchChange,
        onQuickDestinationSelect = onQuickDestinationSelect,
        onPlaceClick = onPlaceClick,
        onDirectionsClick = onDirectionsClick,
        onSavePlace = onSavePlace,
        onShowDriverClick = onShowDriverClick,
        onAiAssistantClick = onAiAssistantClick,
        onSelectTransitType = onSelectTransitType,
        onSelectTransportationType = onSelectTransportationType,
        onVoiceSearchClick = onVoiceSearchClick,
        modifier = modifier
    )
}
