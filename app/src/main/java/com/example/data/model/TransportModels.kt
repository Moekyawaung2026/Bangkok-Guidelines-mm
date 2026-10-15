package com.example.data.model

enum class TransportType(val labelMm: String, val labelTh: String, val labelEn: String) {
    BUS("ဘတ်စ်ကား", "รถเมล์", "Bus"),
    BUS_STOP("ဘတ်စ်ကားမှတ်တိုင်", "ป้ายรถเมล์", "Bus Stop"),
    BUS_TERMINAL("ကားဂိတ်ကြီး", "สถานีขนส่ง", "Bus Terminal"),
    BTS("BTS မိုးပျံရထား", "รถไฟฟ้า BTS", "BTS Skytrain"),
    MRT("MRT မြေအောက်ရထား", "รถไฟฟ้า MRT", "MRT Subway"),
    AIRPORT_RAIL_LINK("လေဆိပ်ရထား", "แอร์พอร์ต เรล ลิงก်", "Airport Rail Link"),
    SRT("မီးရထား", "รถไฟ รฟท.", "SRT Railway"),
    TAXI("တက္ကစီ", "แท็กซี่", "Taxi Stand"),
    MOTORCYCLE_TAXI("ဆိုင်ကယ်ကယ်ရီ", "วินมอเตอร์ไซค์", "Motorcycle Taxi"),
    HOSPITAL("ဆေးရုံ", "โรงพยาบาล", "Hospital"),
    POLICE("ရဲစခန်း", "สถานีตำรวจ", "Police Station"),
    EMBASSY("သံရုံး", "สถานทูต", "Embassy"),
    MARKET("စျေး", "ตลาด", "Market"),
    WORKPLACE("အလုပ်ရုံ / စက်ရုံ", "โรงงาน / ที่ทำงาน", "Workplace / Factory"),
    COMMUNITY("မြန်မာအသိုက်အဝန်း", "ชุมชนเมียนมา", "Myanmar Community")
}

enum class ServiceStatus(val labelMm: String, val labelTh: String, val labelEn: String) {
    NORMAL("ပုံမှန်ပြေးဆွဲနေပါသည်", "ให้บริการปกติ", "Normal Service"),
    DELAYED("ကြန့်ကြာနေပါသည်", "ล่าช้า", "Delayed"),
    CLOSED("ပိတ်ထားပါသည်", "ปิดให้บริการ", "Closed"),
    UNKNOWN("စစ်ဆေးနေဆဲ", "ไม่ระบุ", "Status Unknown")
}

enum class VerificationStatus(val labelMm: String, val labelTh: String, val labelEn: String) {
    VERIFIED("အတည်ပြုပြီး", "ตรวจสอบแล้ว", "Verified"),
    NEEDS_REVIEW("ပြန်လည်စစ်ဆေးရန်", "รอการตรวจสอบ", "Needs Review"),
    TEMPORARILY_UNAVAILABLE("ယာယီမရနိုင်ပါ", "ระงับชั่วคราว", "Temporarily Unavailable"),
    USER_REPORTED("အသုံးပြုသူတင်ပြချက်", "รายงานโดยผู้ใช้", "User Reported")
}

data class AccessibilityInfo(
    val wheelchair: Boolean = true,
    val elevator: Boolean = true,
    val escalator: Boolean = true
)

data class TransportPlace(
    val id: String,
    val type: TransportType,
    val nameMm: String,
    val nameTh: String,
    val nameEn: String,
    val romanized: String = "",
    val latitude: Double,
    val longitude: Double,
    val addressMm: String = "",
    val addressTh: String = "",
    val openingTime: String = "05:00",
    val closingTime: String = "24:00",
    val fareMin: Int = 8,
    val fareMax: Int = 45,
    val phone: String = "",
    val liveStatus: ServiceStatus = ServiceStatus.NORMAL,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
    val accessibility: AccessibilityInfo = AccessibilityInfo(),
    val source: String = "BMTA / BTS / SRT Official",
    val lastVerifiedAt: String = "2026-10-10",
    val confidenceScore: Int = 98,
    val exitDetailsMm: String = "",
    val exitDetailsTh: String = "",
    val driverPhraseTh: String = ""
)

data class RouteStep(
    val stepNumber: Int,
    val transportType: TransportType,
    val instructionMm: String,
    val instructionTh: String,
    val instructionEn: String,
    val durationMinutes: Int,
    val distanceMeters: Int,
    val boardingPlace: String,
    val alightingPlace: String,
    val exitNumber: String = "",
    val driverPhraseTh: String = ""
)

data class MultimodalRoute(
    val id: String,
    val titleMm: String,
    val titleTh: String,
    val modeNameMm: String,
    val modeNameTh: String,
    val totalTimeMinutes: Int,
    val fareMinThb: Int,
    val fareMaxThb: Int,
    val walkingMeters: Int,
    val transfersCount: Int,
    val transportTypes: List<TransportType>,
    val steps: List<RouteStep>,
    val safetyNoteMm: String = "",
    val safetyNoteTh: String = "",
    val confidenceScore: Int = 96,
    val lastVerified: String = "2026-10-12"
)

data class BusRouteInfo(
    val busNumber: String,
    val routeNameMm: String,
    val routeNameTh: String,
    val busTypeMm: String,
    val isAirConditioned: Boolean,
    val isExpressway: Boolean,
    val isNightBus: Boolean,
    val operatingHours: String,
    val fareThb: String,
    val waitingSideMm: String,
    val waitingSideTh: String,
    val keyStopsMm: List<String>,
    val keyStopsTh: List<String>,
    val inboundDirectionMm: String,
    val outboundDirectionMm: String
)

data class PhrasebookItem(
    val id: String,
    val categoryMm: String,
    val categoryTh: String,
    val categoryEn: String,
    val burmeseText: String,
    val thaiText: String,
    val romanizedPronunciation: String,
    val audioSpeedText: String = "normal",
    val usageTipMm: String = ""
)

data class EmergencyContact(
    val id: String,
    val titleMm: String,
    val titleTh: String,
    val titleEn: String,
    val number: String,
    val descriptionMm: String,
    val descriptionTh: String,
    val iconType: String = "police"
)

data class UserReport(
    val id: String,
    val placeOrRouteName: String,
    val issueTypeMm: String,
    val descriptionMm: String,
    val timestamp: Long,
    val status: String = "Pending Verification"
)

enum class TransitHubCategory(val titleMm: String, val titleTh: String, val titleEn: String) {
    ALL("အားလုံး", "ทั้งหมด", "All"),
    INTERCHANGE("ရထားအချက်အချာ (BTS/MRT)", "สถานีเชื่อมต่อ BTS/MRT", "BTS/MRT Interchanges"),
    TERMINAL("ကားဂိတ်ကြီးများ", "สถานีขนส่งผู้โดยสาร", "Bus Terminals"),
    AIRPORT_RAIL("လေဆိပ်နှင့် မီးရထား", "สนามบินและรถไฟ", "Airports & Trains"),
    COMMUNITY_WORK("အလုပ်သမားနယ်မြေ / စျေး", "ย่านแรงงานและตลาด", "Worker Hubs & Markets")
}

data class TransitHub(
    val id: String,
    val nameMm: String,
    val nameTh: String,
    val nameEn: String,
    val shortNameMm: String,
    val category: TransitHubCategory,
    val type: TransportType,
    val lines: List<String>,
    val descriptionMm: String,
    val descriptionTh: String,
    val latitude: Double,
    val longitude: Double
)

