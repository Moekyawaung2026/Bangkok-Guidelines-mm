package com.example.data.model

enum class SafetyCategory(val titleMm: String, val titleTh: String, val titleEn: String) {
    ALL("အားလုံး", "ทั้งหมด", "All"),
    DOCUMENTS("စာရွက်စာတမ်း", "เอกสารประจำตัว", "Documents"),
    TAXI_SAFETY("တက္ကစီ/ဆိုင်ကယ်", "แท็กซี่/วินมอเตอร์ไซค์", "Taxi & Moto"),
    NIGHT_TRAVEL("ညဉ့်နက်ခရီး", "เดินทางกะดึก", "Night Travel"),
    LABOR_RIGHTS("အခွင့်အရေးနှင့် ဆေးကုသခွင့်", "สิทธิและรักษาพยาบาล", "Labor & Health"),
    FINANCE_SAFETY("ငွေကြေးလုံခြုံရေး", "ความปลอดภัยทางการเงิน", "Money Safety")
}

data class SafetyTipItem(
    val id: String,
    val category: SafetyCategory,
    val importance: String = "HIGH", // CRITICAL, HIGH, RECOMMENDED
    val titleMm: String,
    val titleTh: String,
    val titleEn: String,
    val summaryMm: String,
    val summaryTh: String,
    val detailMm: String,
    val detailTh: String,
    val dosMm: List<String> = emptyList(),
    val dontsMm: List<String> = emptyList(),
    val relevantHotline: String = ""
)

data class SafetyGuideData(
    val version: Int = 1,
    val lastUpdated: String = "2026-10-17",
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val safetyTips: List<SafetyTipItem> = emptyList()
)
