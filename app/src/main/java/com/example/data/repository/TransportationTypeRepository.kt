package com.example.data.repository

import com.example.data.model.TransportType
import com.example.data.model.TransportationTypeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repository interface for Bangkok transportation types.
 * Provides access to comprehensive transit metadata, routes, fares, and navigation targets.
 *
 * Design and create by Gemini, Development by Moekyawaung, GitHub -dev-moe-kyaw-aung
 */
interface TransportationTypeRepository {
    fun getAllTransportationTypes(): List<TransportationTypeItem>
    fun getTransportationTypeById(id: String): TransportationTypeItem?
    fun getTransportationTypeByType(type: TransportType): TransportationTypeItem?
    fun observeTransportationTypes(): Flow<List<TransportationTypeItem>>
}

/**
 * Default implementation of TransportationTypeRepository with curated transit data for
 * BTS, MRT, Bus, SRT, and Taxi.
 */
class DefaultTransportationTypeRepository : TransportationTypeRepository {

    private val transportationTypes: List<TransportationTypeItem> = listOf(
        TransportationTypeItem(
            id = "BTS",
            type = TransportType.BTS,
            nameMm = "BTS မိုးပျံရထား",
            nameTh = "รถไฟฟ้าบีทีเอส (BTS)",
            nameEn = "BTS Skytrain",
            subtitleMm = "မိုးပျံရထားလိုင်းများ (အစိမ်းလိုင်း၊ ရွှေလိုင်း၊ သီလုံလိုင်း)",
            subtitleTh = "รถไฟฟ้าสายสีเขียวเข้ม/อ่อน และสายสีทอง",
            subtitleEn = "Sukhumvit, Silom & Gold Lines",
            descriptionMm = "ဘန်ကောက်မြို့တွင်း ယာဉ်ကြောပိတ်ဆို့မှုကင်းစွာ လျင်မြန်စွာ သွားလာနိုင်သော အဓိက မိုးပျံရထားစနစ်။ ဆယမ်၊ မိုချစ်၊ အာဆုတ်နှင့် စမွတ်ပရာကန် စက်ရုံဇုန်များသို့ တိုက်ရိုက်ရောက်ရှိနိုင်ပါသည်။",
            descriptionTh = "ระบบขนส่งมวลชนลอยฟ้าสายหลักของกรุงเทพฯ เชื่อมต่อสยาม หมอชิต อโศก และสมุทรปราการ",
            descriptionEn = "Bangkok's premier elevated railway connecting central business districts, markets, and industrial suburbs.",
            operatingHours = "06:00 - 24:00",
            fareRangeMm = "၁၇ - ၆၂ ဘတ် (ဘူတာအကွာအဝေးအလိုက်)",
            fareRangeTh = "17 - 62 บาท (ตามระยะทาง)",
            fareRangeEn = "17 - 62 THB (By Distance)",
            keyStationsMm = "ဆယမ် (Siam - ဗဟိုဘူတာ)၊ မိုချစ် (Mo Chit)၊ အာဆုတ် (Asok)၊ သာကဆင် (Saphan Taksin)၊ အောင်ဆန်းရတနာ (Victory Monument)",
            keyStationsTh = "สยาม (Siam), หมอชิต (Mo Chit), อโศก (Asok), สะพานตากสิน (Saphan Taksin), อนุสาวรีย์ชัยฯ",
            primaryColorHex = 0xFF00823B, // BTS Sukhumvit Green
            secondaryColorHex = 0xFFE8F5E9,
            badgeMm = "လူစီးအများဆုံး • မြန်ဆန်",
            badgeTh = "ยอดนิยม • รวดเร็ว",
            travelTipsMm = "လက်မှတ်အရောင်းစက်များတွင် အကြွေစေ့ သို့မဟုတ် Rabbit Card / PromptPay QR ဖြင့် ဝယ်ယူနိုင်ပါသည်။ ရထားပေါ်တွင် အစားအသောက်စားသောက်ခြင်း တားမြစ်ထားပါသည်။",
            travelTipsTh = "ซื้อตั๋วที่ตู้หยอดเหรียญ หรือใช้บัตร Rabbit Card / QR Code ห้ามรับประทานอาหารบนขบวน",
            highlightFeaturesMm = listOf("ဆယမ်တွင် လိုင်းလဲလှယ်နိုင်", "စမွတ်ပရာကန်သို့ တိုက်ရိုက်ရောက်", "အဲယားကွန်းအပြည့်ပါရှိ"),
            highlightFeaturesTh = listOf("เปลี่ยนสายที่สยามสะดวก", "เชื่อมตรงสู่สมุทรปราการ", "ติดแอร์เย็นสบายตลอดทาง"),
            isPopular = true
        ),
        TransportationTypeItem(
            id = "MRT",
            type = TransportType.MRT,
            nameMm = "MRT မြေအောက်ရထား",
            nameTh = "รถไฟฟ้ามหานคร (MRT)",
            nameEn = "MRT Subway",
            subtitleMm = "အပြာရောင်လိုင်း၊ ခရမ်းရောင်လိုင်း၊ အဝါရောင်နှင့် ပန်းရောင်လိုင်း",
            subtitleTh = "สายสีน้ำเงิน สีม่วง สีเหลือง และสีชมพู",
            subtitleEn = "Blue, Purple, Yellow & Pink Lines",
            descriptionMm = "ဘန်ကောက်မြို့အနှံ့ လေအေးပေးစက်ဖြင့် သက်တောင့်သက်သာ သွားလာနိုင်သော မြေအောက်နှင့် မိုးပျံရထားကွန်ရက်။ ဘန်စူး (Bang Sue) ဘူတာကြီးနှင့် တရုတ်တန်း (Yaowarat) သို့ ရောက်ရှိနိုင်ပါသည်။",
            descriptionTh = "เครือข่ายรถไฟฟ้าใต้ดินและโมโนเรล ครอบคลุมกรุงเทพฯ เชื่อมต่อสถานีกลางกรุงเทพอภิวัฒน์ และเยาวราช",
            descriptionEn = "Comprehensive underground subway and monorail network connecting Bang Sue Grand Station, Chinatown, and northern suburbs.",
            operatingHours = "06:00 - 24:00",
            fareRangeMm = "၁၇ - ၄၃ ဘတ် (အပြာလိုင်း) / စုစုပေါင်း ၇၀ ဘတ်အထိ",
            fareRangeTh = "17 - 43 บาท (สายสีน้ำเงิน) / สูงสุด 70 บาท",
            fareRangeEn = "17 - 43 THB (Blue Line) / Up to 70 THB",
            keyStationsMm = "ဆူခုမ်ဝစ် (Sukhumvit)၊ ဆီလုံ (Silom)၊ ချာတူချတ် (Chatuchak)၊ ဘန်စူး (Krung Thep Aphiwat)၊ ဝပ်မန်ဂွန်း (Wat Mangkon - တရုတ်တန်း)",
            keyStationsTh = "สุขุมวิท (Sukhumvit), สีลม (Silom), สวนจตุจักร (Chatuchak), กรุงเทพอภิวัฒน์, วัดมังกร (เยาวราช)",
            primaryColorHex = 0xFF003399, // MRT Deep Blue
            secondaryColorHex = 0xFFE3F2FD,
            badgeMm = "ခရီးဝေးချိတ်ဆက် • အဲယားကွန်း",
            badgeTh = "เชื่อมต่อสะดวก • รถไฟใต้ดิน",
            travelTipsMm = "အကြွေစေ့ပုံစံ အမည်းရောင် Token သို့မဟုတ် Visa / Mastercard Contactless (EMV) ကတ်ဖြင့် ဂိတ်တံခါးကို တိုက်ရိုက်တို့ထိဖြတ်သန်းနိုင်ပါသည်။",
            travelTipsTh = "ใช้เหรียญ Token หรือบัตรเครดิต/เดบิต Contactless (EMV) แตะเข้าออกได้ทันทีไม่ต้องซื้อตั๋วใหม่",
            highlightFeaturesMm = listOf("Visa/Mastercard ကတ်ဖြင့် တိုက်ရိုက်တို့ထိနိုင်", "ဘန်စူး အဝေးပြေးဘူတာသို့ ရောက်", "မိုးတွင်း သွားလာရ အဆင်ပြေ"),
            highlightFeaturesTh = listOf("แตะบัตร Contactless ได้", "เชื่อมต่อสถานีกลางกรุงเทพอภิวัฒน์", "เดินทางสะดวกแม้ฝนตก"),
            isPopular = true
        ),
        TransportationTypeItem(
            id = "BUS",
            type = TransportType.BUS,
            nameMm = "ဘန်ကောက် ဘတ်စ်ကား",
            nameTh = "รถโดยสารประจำทาง (ขสมก. / TSB)",
            nameEn = "Bangkok City Bus (BMTA)",
            subtitleMm = "BMTA အစိုးရကား (နီဖြူ/အပြာ) နှင့် TSB လျှပ်စစ် EV ကားများ",
            subtitleTh = "รถเมล์ ขสมก. (ครีมแดง/แอร์) และ Thai Smile Bus EV",
            subtitleEn = "Public BMTA & Electric EV Buses",
            descriptionMm = "စျေးအသက်သာဆုံး သယ်ယူပို့ဆောင်ရေးစနစ်ဖြစ်ပြီး ဘန်ကောက်မြို့အနှံ့ လမ်းကြိုလမ်းကြားနှင့် စက်ရုံဇုန်များအထိ ရောက်ရှိနိုင်သည်။ အလုပ်သမားများ နေ့စဉ် အဓိကအားထားရသော ယာဉ်လိုင်း။",
            descriptionTh = "ระบบขนส่งที่ประหยัดที่สุด ครอบคลุมทุกเส้นทาง ซอย และย่านโรงงานอุตสาหกรรมในราคาประหยัด",
            descriptionEn = "Most economical transit option covering extensive street networks, factory districts, and suburban communities.",
            operatingHours = "04:30 - 23:00 (အချို့လိုင်းများ ၂၄ နာရီ)",
            fareRangeMm = "၈ - ၂၅ ဘတ် (နီဖြူရိုးရိုး ၈ ဘတ်၊ အဲယားကွန်း ၁၂-၂၅ ဘတ်)",
            fareRangeTh = "8 - 25 บาท (รถธรรมดา 8 บาท, รถแอร์ 12-25 บาท)",
            fareRangeEn = "8 - 25 THB (Regular 8 THB, AC 12-25 THB)",
            keyStationsMm = "အောင်ဆန်းရတနာ (Victory Monument)၊ မိုချစ် ၂ (Mo Chit 2)၊ စိုင်းထိုင်းမိုင် (Sai Tai Mai)၊ ပင်လယ်စာစျေး မဟာချိုင် (Mahachai)",
            keyStationsTh = "อนุสาวรีย์ชัยฯ, อู่หมอชิต 2, สายใต้ใหม่, ตลาดมหาชัย",
            primaryColorHex = 0xFFC2410C, // BMTA Red/Orange
            secondaryColorHex = 0xFFFFEDE7,
            badgeMm = "စျေးအသက်သာဆုံး • နေရာအနှံ့",
            badgeTh = "ประหยัดที่สุด • ครอบคลุม",
            travelTipsMm = "ကားစီးခပေးရာတွင် လက်မှတ်ရောင်းသူ (ကားစပယ်ယာ) အား ငွေစက္ကူအကြွေ ၂၀ ဘတ် သို့မဟုတ် အကြွေစေ့ပေးပါ။ ဆင်းလိုပါက ဘဲလ်ကြိုတီးပါ။",
            travelTipsTh = "เตรียมเหรียญหรือธนบัตร 20 บาทให้กระเป๋ารถเมล์ กดกริ่งก่อนถึงป้ายเพื่อลง",
            highlightFeaturesMm = listOf("ကားခ ၈ ဘတ်မှ စတင်", "မဟာချိုင်နှင့် စက်ရုံများသို့ ရောက်", "ကားလိုင်းပေါင်း ၁၀၀ ကျော်"),
            highlightFeaturesTh = listOf("ค่าโดยสารเริ่มต้นเพียง 8 บาท", "วิ่งตรงถึงมหาชัยและนิคมฯ", "ครอบคลุมกว่า 100 สาย"),
            isPopular = true
        ),
        TransportationTypeItem(
            id = "SRT",
            type = TransportType.SRT,
            nameMm = "နိုင်ငံတော် မီးရထား (SRT)",
            nameTh = "การรถไฟแห่งประเทศไทย (รฟท.) & สายสีแดง",
            nameEn = "State Railway of Thailand (SRT)",
            subtitleMm = "SRT အနီရောင်လိုင်း (Red Line) နှင့် ဝမ်ဝီယန်ရိုင်-မဟာချိုင် ရထား",
            subtitleTh = "รถไฟชานเมืองสายสีแดง และสายวงเวียนใหญ่-มหาชัย",
            subtitleEn = "SRT Red Line & Mahachai Commuter Trains",
            descriptionMm = "ဘန်ကောက်မြို့အစွန်နှင့် နယ်ဝေးများ၊ စက်ရုံဇုန်များ၊ ဒွန်မောင်းလေဆိပ်နှင့် မဟာချိုင်သို့ အလွယ်တကူ ဆက်သွယ်ပေးသော မီးရထားကွန်ရက်။ ပုံမှန်ခရီးသွားများနှင့် ရွှေ့ပြောင်းလုပ်သားများအတွက် အလွန်အသုံးဝင်သည်။",
            descriptionTh = "เส้นทางเชื่อมต่อชานเมือง กรุงเทพอภิวัฒน์ สนามบินดอนเมือง และสายวงเวียนใหญ่-มหาชัย",
            descriptionEn = "Commuter rail system linking outer provinces, Don Mueang Airport, and vital industrial hubs like Mahachai.",
            operatingHours = "05:00 - 24:00",
            fareRangeMm = "၁၂ - ၄၂ ဘတ် (အနီရောင်လိုင်း အမြင့်ဆုံး ၂၀ ဘတ် ပရိုမိုးရှင်း)",
            fareRangeTh = "12 - 42 บาท (สายสีแดง นโยบาย 20 บาทตลอดสาย)",
            fareRangeEn = "12 - 42 THB (Red Line 20 THB Flat Promo)",
            keyStationsMm = "ဘန်စူး (Krung Thep Aphiwat)၊ ဒွန်မောင်း (Don Mueang)၊ ရန်ဆစ် (Rangsit)၊ ဝမ်ဝီယန်ရိုင် (Wongwian Yai)၊ မဟာချိုင် (Mahachai)",
            keyStationsTh = "กรุงเทพอภิวัฒน์, ดอนเมือง, รังสิต, วงเวียนใหญ่, มหาชัย",
            primaryColorHex = 0xFF881337, // SRT Crimson
            secondaryColorHex = 0xFFFFE4E6,
            badgeMm = "စက်ရုံဇုန်/လေဆိပ် • မဟာချိုင်",
            badgeTh = "สายนิคมฯ • รถไฟชานเมือง",
            travelTipsMm = "ဝမ်ဝီယန်ရိုင်မှ မဟာချိုင်သို့ ပြေးဆွဲသော ရထားသည် ပင်လယ်စာစက်ရုံအလုပ်သမားများအတွက် စရိတ်သက်သာပြီး နေ့စဉ် ပုံမှန်စီးနင်းကြပါသည်။",
            travelTipsTh = "รถไฟสายวงเวียนใหญ่-มหาชัย เป็นเส้นทางยอดนิยมของแรงงานในโรงงานอาหารทะเล ค่าโดยสารประหยัดมาก",
            highlightFeaturesMm = listOf("ဒွန်မောင်းလေဆိပ်သို့ တိုက်ရိုက်ရောက်", "မဟာချိုင် အလုပ်သမားရထား", "၂၀ ဘတ် တစ်ပြေးညီ နှုန်းထား"),
            highlightFeaturesTh = listOf("เชื่อมตรงสนามบินดอนเมือง", "ขวัญใจแรงงานสายมหาชัย", "โปรโมชั่น 20 บาทตลอดสาย"),
            isPopular = true
        ),
        TransportationTypeItem(
            id = "TAXI",
            type = TransportType.TAXI,
            nameMm = "မီတာတက္ကစီနှင့် အငှားယာဉ်",
            nameTh = "รถแท็กซี่มิเตอร์ และรถรับจ้าง",
            nameEn = "Metered Taxi & Rideshare",
            subtitleMm = "မီတာတက္ကစီ (အဝါစိမ်း/ပန်းရောင်)၊ Grab၊ Bolt အငှားယာဉ်များ",
            subtitleTh = "แท็กซี่มิเตอร์ สีเขียวเหลือง/ชมพู และแอปเรียกรถ",
            subtitleEn = "Metered Taxis, Grab & Bolt",
            descriptionMm = "အိတ်ကြီးများပါချိန်၊ အရေးပေါ်ကိစ္စများနှင့် ညဉ့်နက်ချိန်များတွင် တစ်နေရာမှ တစ်နေရာ တိုက်ရိုက်သွားနိုင်သော ၂၄ နာရီ ဝန်ဆောင်မှု။ ယာဉ်မောင်းအား ပြသရန် ထိုင်းစာကတ်များ ပါဝင်သည်။",
            descriptionTh = "บริการรับส่งตลอด 24 ชั่วโมง เหมาะสำหรับการเดินทางที่มีสัมภาระ หรือกรณีฉุกเฉินยามค่ำคืน",
            descriptionEn = "Door-to-door 24/7 service ideal for heavy luggage, emergency trips, or late-night travel across Bangkok.",
            operatingHours = "၂၄ နာရီ ဝန်ဆောင်မှု (24 Hours)",
            fareRangeMm = "အစ ၃၅ ဘတ် (မီတာဖြင့်သာ စီးပါ)",
            fareRangeTh = "เริ่มต้น 35 บาท (คิดตามมิเตอร์)",
            fareRangeEn = "Base 35 THB (Always Request Meter)",
            keyStationsMm = "လေဆိပ်များ၊ ဆေးရုံကြီးများ၊ ကားဂိတ်ကြီးများ၊ ဟိုတယ်နှင့် ကွန်ဒိုများ",
            keyStationsTh = "สนามบินสุวรรณภูมิ/ดอนเมือง, โรงพยาบาล, คอนโด, สถานีขนส่ง",
            primaryColorHex = 0xFF0D9488, // Teal/Emerald
            secondaryColorHex = 0xFFCCFBF1,
            badgeMm = "၂၄ နာရီ • အရေးပေါ် အသင့်",
            badgeTh = "24 ชั่วโมง • สะดวก ปลอดภัย",
            travelTipsMm = "ကားပေါ်တက်သည်နှင့် \"မီတာဖွင့်ပေးပါ (ကရုဏာ ပေ့တ် မီတာ ခရပ်/ခါ)\" ဟု အမြဲတောင်းဆိုပါ။ လမ်းတွင် ပြဿနာကြုံပါက တက္ကစီတိုင်ကြားရေး ၁၅၈၄ သို့ ခေါ်ဆိုနိုင်ပါသည်။",
            travelTipsTh = "ขึ้นรถแล้วบอกคนขับ \"เปิดมิเตอร์ด้วยครับ/ค่ะ\" หากพบปัญหาโทรสายด่วน 1584",
            highlightFeaturesMm = listOf("၂၄ နာရီ ဝန်ဆောင်မှု", "ယာဉ်မောင်းပြ ထိုင်းစာကတ်ပါရှိ", "တိုင်ကြားရေး ၁၅၈၄ အသင့်"),
            highlightFeaturesTh = listOf("บริการตลอด 24 ชม.", "การ์ดข้อความบอกคนขับ", "สายด่วนร้องเรียน 1584"),
            isPopular = true
        )
    )

    override fun getAllTransportationTypes(): List<TransportationTypeItem> = transportationTypes

    override fun getTransportationTypeById(id: String): TransportationTypeItem? {
        return transportationTypes.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    override fun getTransportationTypeByType(type: TransportType): TransportationTypeItem? {
        return transportationTypes.firstOrNull { it.type == type }
    }

    override fun observeTransportationTypes(): Flow<List<TransportationTypeItem>> {
        return flowOf(transportationTypes)
    }
}
