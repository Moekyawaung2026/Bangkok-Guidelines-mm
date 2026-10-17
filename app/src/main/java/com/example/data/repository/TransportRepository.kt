package com.example.data.repository

import android.content.Context
import com.example.data.datasource.BangkokStaticData
import com.example.data.datasource.SafetyGuideResourceLoader
import com.example.data.local.AppDatabase
import com.example.data.local.SavedPlaceEntity
import com.example.data.local.SavedRouteEntity
import com.example.data.local.UserReportEntity
import com.example.data.location.LocationService
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.*

class TransportRepository(
    private val database: AppDatabase,
    private val context: Context? = null
) {

    private val places: List<TransportPlace> = BangkokStaticData.transportPlaces
    private val busRoutes: List<BusRouteInfo> = BangkokStaticData.busRoutes
    private val phrasebook: List<PhrasebookItem> = BangkokStaticData.phrasebook
    private val transitHubs: List<TransitHub> = BangkokStaticData.majorTransitHubs

    private val safetyResourceLoader: SafetyGuideResourceLoader? = context?.let { SafetyGuideResourceLoader(it) }
    val locationService: LocationService? = context?.let { LocationService(it) }

    fun getAllPlaces(): List<TransportPlace> = places

    fun getBusRoutes(): List<BusRouteInfo> = busRoutes

    fun getSafetyGuide(): SafetyGuideData {
        return safetyResourceLoader?.loadSafetyGuide() ?: SafetyGuideData(
            version = 1,
            lastUpdated = "2026-10-17",
            emergencyContacts = BangkokStaticData.emergencyContacts,
            safetyTips = emptyList()
        )
    }

    fun getSafetyTips(): List<SafetyTipItem> {
        return getSafetyGuide().safetyTips
    }

    fun getEmergencyContacts(): List<EmergencyContact> {
        val resourceContacts = safetyResourceLoader?.loadSafetyGuide()?.emergencyContacts
        return if (!resourceContacts.isNullOrEmpty()) resourceContacts else BangkokStaticData.emergencyContacts
    }

    fun getPhrasebook(): List<PhrasebookItem> = phrasebook

    fun getMajorTransitHubs(): List<TransitHub> = transitHubs

    fun searchTransitHubs(query: String, category: TransitHubCategory = TransitHubCategory.ALL): List<TransitHub> {
        val filtered = if (category == TransitHubCategory.ALL) {
            transitHubs
        } else {
            transitHubs.filter { it.category == category }
        }
        if (query.isBlank()) return filtered
        val clean = query.trim().lowercase()
        return filtered.filter {
            it.nameMm.lowercase().contains(clean) ||
            it.nameTh.lowercase().contains(clean) ||
            it.nameEn.lowercase().contains(clean) ||
            it.shortNameMm.lowercase().contains(clean) ||
            it.descriptionMm.lowercase().contains(clean) ||
            it.lines.any { line -> line.lowercase().contains(clean) }
        }
    }

    fun searchPlaces(query: String): List<TransportPlace> {
        if (query.isBlank()) return places
        val cleanQuery = query.trim().lowercase()
        return places.filter {
            it.nameMm.lowercase().contains(cleanQuery) ||
            it.nameTh.lowercase().contains(cleanQuery) ||
            it.nameEn.lowercase().contains(cleanQuery) ||
            it.romanized.lowercase().contains(cleanQuery) ||
            it.addressMm.lowercase().contains(cleanQuery) ||
            it.addressTh.lowercase().contains(cleanQuery)
        }
    }

    fun getNearbyPlaces(
        userLat: Double,
        userLng: Double,
        radiusMeters: Double,
        selectedType: TransportType? = null
    ): List<Pair<TransportPlace, Double>> {
        val service = locationService
        return if (service != null) {
            service.filterNearbyPlaces(places, userLat, userLng, radiusMeters, selectedType)
        } else {
            places
                .filter { selectedType == null || it.type == selectedType }
                .map { place ->
                    val distance = calculateDistanceMeters(userLat, userLng, place.latitude, place.longitude)
                    Pair(place, distance)
                }
                .filter { (_, distance) -> distance <= radiusMeters }
                .sortedBy { it.second }
        }
    }

    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val service = locationService
        if (service != null) {
            return service.calculateDistanceMeters(lat1, lon1, lat2, lon2)
        }
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun planRoutes(originQuery: String, destinationQuery: String, selectedModeIndex: Int): List<MultimodalRoute> {
        val fromNameMm = if (originQuery.isBlank()) "အောင်ဆန်းရတနာ" else originQuery.trim()
        val toNameMm = if (destinationQuery.isBlank()) "ဆယမ် (Siam)" else destinationQuery.trim()

        val isAirport = toNameMm.contains("လေဆိပ်") || toNameMm.contains("Suvarnabhumi") || toNameMm.contains("สนามบิน")
        val isMahachai = toNameMm.contains("မဟာချိုင်") || toNameMm.contains("Mahachai") || toNameMm.contains("มหาชัย")
        val isSamutPrakan = toNameMm.contains("စမွတ်ပရာကန်") || toNameMm.contains("Samut Prakan") || toNameMm.contains("สมุทรปราการ")

        val generatedRoutes = mutableListOf<MultimodalRoute>()

        // 1. Cheapest Route (အသက်သာဆုံးလမ်းကြောင်း)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_cheapest",
                titleMm = "အသက်သာဆုံး လမ်းကြောင်း (ဘတ်စ်ကားအဓိက)",
                titleTh = "เส้นทางประหยัดที่สุด (เน้นรถเมล์)",
                modeNameMm = "အသက်သာဆုံး (Cheapest)",
                modeNameTh = "ประหยัดที่สุด",
                totalTimeMinutes = if (isMahachai) 65 else if (isSamutPrakan) 50 else 38,
                fareMinThb = if (isMahachai) 20 else 8,
                fareMaxThb = if (isMahachai) 27 else 20,
                walkingMeters = 350,
                transfersCount = 1,
                transportTypes = listOf(TransportType.BUS, TransportType.BUS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BUS_STOP,
                        instructionMm = "သင်ရှိရာနေရာမှ အနီးဆုံးမှတ်တိုင်သို့ ၄ မိနစ် လမ်းလျှောက်ပါ",
                        instructionTh = "เดิน 4 นาทีไปยังป้ายรถเมล์ที่ใกล้ที่สุด",
                        instructionEn = "Walk 4 minutes to the nearest bus stop",
                        durationMinutes = 4,
                        distanceMeters = 250,
                        boardingPlace = fromNameMm,
                        alightingPlace = "အနီးဆုံးမှတ်တိုင်"
                    ),
                    RouteStep(
                        stepNumber = 2,
                        transportType = TransportType.BUS,
                        instructionMm = if (isMahachai) "ဘတ်စ်ကားလိုင်း 140 ကို တက်စီးပါ (စမဲဒမ်/မဟာချိုင်ဘက်)" else "ဘတ်စ်ကားလိုင်း 12 သို့မဟုတ် 77 ကို တက်စီးပါ",
                        instructionTh = "ขึ้นรถเมล์สายที่กำหนด",
                        instructionEn = "Board the local bus route",
                        durationMinutes = if (isMahachai) 45 else 25,
                        distanceMeters = 6500,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm,
                        driverPhraseTh = "ถึงแล้วช่วยบอกด้วยนะครับ/ค่ะ"
                    ),
                    RouteStep(
                        stepNumber = 3,
                        transportType = TransportType.BUS_STOP,
                        instructionMm = "$toNameMm တွင် ဆင်းပြီး ဦးတည်ရာသို့ ၂ မိနစ် လျှောက်ပါ",
                        instructionTh = "ลงที่ปลายทางแล้วเดินต่อ 2 นาที",
                        instructionEn = "Alight at destination and walk 2 minutes",
                        durationMinutes = 2,
                        distanceMeters = 100,
                        boardingPlace = toNameMm,
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "ဘတ်စ်ကားလက်မှတ်ဖြတ်သူထံ ယာဉ်ခပေးချေပြီး လက်မှတ်ကို ကားပေါ်မှမဆင်းမချင်း သိမ်းထားပါ",
                safetyNoteTh = "เก็บตั๋วรถเมล์ไว้ตลอดการเดินทางจนกว่าจะลงจากรถ"
            )
        )

        // 2. Fastest Route (အမြန်ဆုံးလမ်းကြောင်း)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_fastest",
                titleMm = "အမြန်ဆုံး လမ်းကြောင်း (BTS / MRT လျှပ်စစ်ရထား)",
                titleTh = "เส้นทางเร็วที่สุด (BTS / MRT)",
                modeNameMm = "အမြန်ဆုံး (Fastest)",
                modeNameTh = "เร็วที่สุด",
                totalTimeMinutes = if (isAirport) 35 else 22,
                fareMinThb = 35,
                fareMaxThb = 62,
                walkingMeters = 400,
                transfersCount = 0,
                transportTypes = listOf(TransportType.BTS, TransportType.MRT),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BTS,
                        instructionMm = "အနီးဆုံး BTS ဘူတာသို့ လျှောက်ပြီး လက်မှတ်ဝယ်ပါ",
                        instructionTh = "เดินเข้าสถานี BTS และซื้อบัตรโดยสาร",
                        instructionEn = "Walk to BTS station and get a token/ticket",
                        durationMinutes = 5,
                        distanceMeters = 300,
                        boardingPlace = fromNameMm,
                        alightingPlace = "BTS ဘူတာရုံ"
                    ),
                    RouteStep(
                        stepNumber = 2,
                        transportType = TransportType.BTS,
                        instructionMm = "ရထားတက်စီးပါ။ $toNameMm သို့ ရထားပြတင်းပေါက် စာတန်းကြည့်၍ သွားပါ",
                        instructionTh = "ขึ้นรถไฟฟ้ามุ่งหน้าสู่สถานีปลายทาง",
                        instructionEn = "Board the Skytrain directly to destination",
                        durationMinutes = 15,
                        distanceMeters = 8000,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm,
                        exitNumber = "ထွက်ပေါက် ၁-၂ (Exit 1-2)",
                        driverPhraseTh = "ไปสถานีปลายทางครับ"
                    )
                ),
                safetyNoteMm = "ဘူတာရုံစင်္ကြံတွင် အဝါရောင်လုံခြုံရေးစည်းနောက်မှ စောင့်ဆိုင်းပါ",
                safetyNoteTh = "ยืนรอหลังเส้นสีเหลืองบนชานชาลาเสมอ"
            )
        )

        // 3. Least Walking Route (လမ်းလျှောက်အနည်းဆုံး)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_least_walking",
                titleMm = "လမ်းလျှောက်အနည်းဆုံး (ဆိုင်ကယ်ကယ်ရီ + တိုက်ရိုက်ကား)",
                titleTh = "เดินน้อยที่สุด (วินมอเตอร์ไซค์ + รถต่อเดียว)",
                modeNameMm = "လမ်းလျှောက်အနည်းဆုံး (Least Walking)",
                modeNameTh = "เดินน้อยที่สุด",
                totalTimeMinutes = 20,
                fareMinThb = 40,
                fareMaxThb = 80,
                walkingMeters = 80,
                transfersCount = 1,
                transportTypes = listOf(TransportType.MOTORCYCLE_TAXI, TransportType.BTS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.MOTORCYCLE_TAXI,
                        instructionMm = "အနီးရှိ လိမ္မော်ရောင်အင်္ကျီဝတ် ဆိုင်ကယ်ကယ်ရီဂိတ်မှ ဘူတာထိပ်ထိ စီးပါ (ဦးထုပ်ဆောင်းပါ)",
                        instructionTh = "นั่งวินมอเตอร์ไซค์เสื้อกั๊กส้มไปส่งหน้าสถานี",
                        instructionEn = "Take orange-vest motorcycle taxi to station entrance",
                        durationMinutes = 4,
                        distanceMeters = 800,
                        boardingPlace = fromNameMm,
                        alightingPlace = "ဘူတာအဝ",
                        driverPhraseTh = "ไปส่งหน้าสถานีรถไฟฟ้าครับ ขอยืมหมวกกันน็อกด้วยครับ"
                    ),
                    RouteStep(
                        stepNumber = 2,
                        transportType = TransportType.BTS,
                        instructionMm = "ဓာတ်လှေကား အသုံးပြု၍ ရထားပေါ်တက်ပါ",
                        instructionTh = "ใช้ลิฟต์ขึ้นชานชาลารถไฟฟ้า",
                        instructionEn = "Use elevator to platform and ride to destination",
                        durationMinutes = 14,
                        distanceMeters = 5000,
                        boardingPlace = "ဘူတာအဝ",
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "ဆိုင်ကယ်စီးလျှင် ဦးထုပ်အမြဲတောင်းဆောင်းပါ၊ ယာဉ်ခကို ကြိုတင်မေးမြန်းသဘောတူပါ",
                safetyNoteTh = "สวมหมวกกันน็อกทุกครั้ง ตกลงราคาล่วงหน้า"
            )
        )

        // 4. Fewest Transfers Route (အပြောင်းအရွှေ့အနည်းဆုံး)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_fewest_transfers",
                titleMm = "အပြောင်းအရွှေ့အနည်းဆုံး (တိုက်ရိုက်လိုင်း)",
                titleTh = "เปลี่ยนสายต่อเดียว/ตรง",
                modeNameMm = "အပြောင်းအရွှေ့နည်း (Direct)",
                modeNameTh = "ต่อเดียวน้อยที่สุด",
                totalTimeMinutes = 30,
                fareMinThb = 25,
                fareMaxThb = 50,
                walkingMeters = 300,
                transfersCount = 0,
                transportTypes = listOf(TransportType.BUS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BUS,
                        instructionMm = "တိုက်ရိုက်ပြေးဆွဲသော ဘတ်စ်ကား သို့မဟုတ် ရထားလိုင်းကို စီးပါ",
                        instructionTh = "ขึ้นสายตรงไม่เปลี่ยนขบวน",
                        instructionEn = "Direct transit with 0 transfers",
                        durationMinutes = 28,
                        distanceMeters = 7000,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "ကားမဆင်းမီ မိမိပစ္စည်းများ မကျန်ခဲ့စေရန် သတိပြုပါ",
                safetyNoteTh = "ตรวจสัมภาระก่อนลงจากรถ"
            )
        )

        // 5. Accessible Route (ဘီးတပ်ကုလားထိုင်/ဓာတ်လှေကား လမ်းကြောင်း)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_accessible",
                titleMm = "သက်ကြီးရွယ်အိုနှင့် ဘီးတပ်ကုလားထိုင် အဆင်ပြေသော လမ်းကြောင်း",
                titleTh = "เส้นทางผู้สูงอายุและวีลแชร์ (มีลิฟต์ 100%)",
                modeNameMm = "အဆင်ပြေလုံခြုံ (Accessible)",
                modeNameTh = "เพื่อทุกคน วีลแชร์",
                totalTimeMinutes = 35,
                fareMinThb = 30,
                fareMaxThb = 60,
                walkingMeters = 200,
                transfersCount = 0,
                transportTypes = listOf(TransportType.BTS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BTS,
                        instructionMm = "ဘူတာရုံ၏ မြေပြင်အဆင့် ဓာတ်လှေကားမှ တိုက်ရိုက်တက်ပါ",
                        instructionTh = "ขึ้นลิฟต์จากระดับถนนสู่ชานชาลา",
                        instructionEn = "Take street-level elevator directly to platform",
                        durationMinutes = 5,
                        distanceMeters = 100,
                        boardingPlace = fromNameMm,
                        alightingPlace = "ဓာတ်လှေကား"
                    ),
                    RouteStep(
                        stepNumber = 2,
                        transportType = TransportType.BTS,
                        instructionMm = "ဘီးတပ်ကုလားထိုင်နေရာ သီးသန့်ပါသော ရထားတွဲတွင် စီးပါ",
                        instructionTh = "โดยสารในโบกี้ที่มีพื้นที่สำหรับรถวีลแชร์",
                        instructionEn = "Ride in accessible train carriage",
                        durationMinutes = 20,
                        distanceMeters = 6000,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm,
                        exitNumber = "ဓာတ်လှေကားထွက်ပေါက်"
                    )
                ),
                safetyNoteMm = "ဘူတာရုံဝန်ထမ်းများထံမှ အခမဲ့ အထူးကူညီစောင့်ရှောက်မှု တောင်းခံနိုင်ပါသည်",
                safetyNoteTh = "ขอความช่วยเหลือจากเจ้าหน้าที่ประจำสถานีได้ตลอดเวลา"
            )
        )

        // 6. Night Route (ညဘက်လိုင်း)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_night",
                titleMm = "ညဘက် ၂၄ နာရီ လမ်းကြောင်း (ညဆိုင်းဆင်းချိန်အတွက်)",
                titleTh = "เส้นทางกะดึก 24 ชั่วโมง",
                modeNameMm = "ညဘက်လိုင်း (Night Route)",
                modeNameTh = "บริการรอบดึก",
                totalTimeMinutes = 40,
                fareMinThb = 15,
                fareMaxThb = 35,
                walkingMeters = 250,
                transfersCount = 1,
                transportTypes = listOf(TransportType.BUS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BUS,
                        instructionMm = "ညဘက်ပြေးဆွဲသော ၂၄ နာရီ ဘတ်စ်ကား 511 သို့မဟုတ် 29 ကို စောင့်စီးပါ",
                        instructionTh = "รอขึ้นรถเมล์รอบดึก 24 ชั่วโมง สาย 511 หรือสายที่มีป้ายตลอดคืน",
                        instructionEn = "Board 24-hour night bus",
                        durationMinutes = 35,
                        distanceMeters = 8500,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "ညဘက်တွင် လူရှင်းသော မှောင်မိုက်သည့် မှတ်တိုင်များတွင် တစ်ဦးတည်း မစောင့်ပါနှင့်၊ မီးရောင်ရှိရာတွင် စောင့်ပါ",
                safetyNoteTh = "รอในที่สว่าง ไม่ยืนคนเดียวในที่เปลี่ยว"
            )
        )

        // 7. Motorcycle Taxi + Train (ဆိုင်ကယ်ကယ်ရီ + ရထား)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_moto_train",
                titleMm = "ဆိုင်ကယ်ကယ်ရီ + လျှပ်စစ်ရထား ပေါင်းစပ်လမ်းကြောင်း",
                titleTh = "วินมอเตอร์ไซค์ + รถไฟฟ้า",
                modeNameMm = "ဆိုင်ကယ် + ရထား (Moto + Train)",
                modeNameTh = "วิน + รถไฟฟ้า",
                totalTimeMinutes = 18,
                fareMinThb = 45,
                fareMaxThb = 85,
                walkingMeters = 120,
                transfersCount = 1,
                transportTypes = listOf(TransportType.MOTORCYCLE_TAXI, TransportType.BTS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.MOTORCYCLE_TAXI,
                        instructionMm = "အဆောင် သို့မဟုတ် စက်ရုံရှေ့မှ ဆိုင်ကယ်ကယ်ရီဖြင့် ဘူတာသို့ ၅ မိနစ်စီးပါ",
                        instructionTh = "นั่งมอเตอร์ไซค์รับจ้างจากที่พัก/โรงงาน ไปสถานีรถไฟฟ้า",
                        instructionEn = "Motorcycle taxi to nearest station",
                        durationMinutes = 5,
                        distanceMeters = 1200,
                        boardingPlace = fromNameMm,
                        alightingPlace = "BTS ဘူတာ"
                    ),
                    RouteStep(
                        stepNumber = 2,
                        transportType = TransportType.BTS,
                        instructionMm = "ရထားစီး၍ $toNameMm သို့ ၁၂ မိနစ်အတွင်း သွားပါ",
                        instructionTh = "นั่งรถไฟฟ้าต่อเดียวถึงจุดหมาย",
                        instructionEn = "Ride train to final destination",
                        durationMinutes = 12,
                        distanceMeters = 5500,
                        boardingPlace = "BTS ဘူတာ",
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "မိုးရွာချိန် သို့မဟုတ် ညဉ့်နက်ချိန်တွင် ဆိုင်ကယ်အမြန်မမောင်းရန် ဒရိုင်ဘာအား သတိပေးပါ",
                safetyNoteTh = "ฝนตกถนนลื่น เตือนคนขับให้ขับช้าๆ ปลอดภัย"
            )
        )

        // 8. Bus Only (ဘတ်စ်ကားသီးသန့်)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_bus_only",
                titleMm = "ဘတ်စ်ကားသီးသန့် လမ်းကြောင်း (ကုန်ကျစရိတ် အနည်းဆုံး)",
                titleTh = "รถเมล์ล้วน ประหยัดเงิน",
                modeNameMm = "ဘတ်စ်ကားသီးသန့် (Bus Only)",
                modeNameTh = "เฉพาะรถเมล์",
                totalTimeMinutes = 45,
                fareMinThb = 8,
                fareMaxThb = 20,
                walkingMeters = 400,
                transfersCount = 0,
                transportTypes = listOf(TransportType.BUS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BUS,
                        instructionMm = "ဘတ်စ်ကားမှတ်တိုင်တွင် ကားနံပါတ် 12, 140, 77, 511 ထဲမှ သင့်တော်ရာ စောင့်စီးပါ",
                        instructionTh = "ขึ้นรถเมล์ตรงสาย ค่าโดยสาร 8-20 บาท",
                        instructionEn = "Take direct bus to your destination",
                        durationMinutes = 40,
                        distanceMeters = 7000,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm,
                        driverPhraseTh = "ไป $toNameMm ครับ"
                    )
                ),
                safetyNoteMm = "အကြွေစေ့နှင့် ၁၀ ဘတ်၊ ၂၀ ဘတ်တန် ငွေစက္ကူငယ်များ ကြိုတင်ဆောင်ထားပါ",
                safetyNoteTh = "เตรียมเหรียญและธนบัตรย่อย 20 บาทไว้จ่ายค่าโดยสาร"
            )
        )

        // 9. BTS / MRT Only (ရထားလိုင်းသီးသန့်)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_train_only",
                titleMm = "BTS / MRT ရထားလိုင်းသီးသန့် (ယာဉ်ကြောပိတ်ဆို့မှု ကင်းလွတ်)",
                titleTh = "รถไฟฟ้าล้วน BTS / MRT ไม่ติดขัด",
                modeNameMm = "ရထားသီးသန့် (Train Only)",
                modeNameTh = "เฉพาะรถไฟฟ้า",
                totalTimeMinutes = 20,
                fareMinThb = 30,
                fareMaxThb = 60,
                walkingMeters = 300,
                transfersCount = 0,
                transportTypes = listOf(TransportType.BTS),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.BTS,
                        instructionMm = "ဘူတာရုံသို့ လမ်းလျှောက်ပြီး ကောင်တာတွင် ဘူတာအမည်ပြော၍ လက်မှတ်ဝယ်ပါ",
                        instructionTh = "ซื้อตั๋วที่สถานีรถไฟฟ้า",
                        instructionEn = "Get train ticket and board",
                        durationMinutes = 18,
                        distanceMeters = 6000,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm
                    )
                ),
                safetyNoteMm = "ရထားတံခါးပိတ်သံမြည်ပါက အတင်းတိုးမဝင်ပါနှင့်၊ နောက်တစ်စီးကို စောင့်ပါ",
                safetyNoteTh = "เมื่อสัญญาณเตือนประตูปิดดัง ห้ามดันตัวเข้าไป"
            )
        )

        // 10. Taxi Only (တက္ကစီသီးသန့်)
        generatedRoutes.add(
            MultimodalRoute(
                id = "route_taxi_only",
                titleMm = "မီတာတက္ကစီ တိုက်ရိုက်လမ်းကြောင်း (ပစ္စည်းများသူများအတွက်)",
                titleTh = "แท็กซี่มิเตอร์ สะดวกสบายมีสัมภาระ",
                modeNameMm = "တက္ကစီသီးသန့် (Taxi Only)",
                modeNameTh = "เฉพาะแท็กซี่",
                totalTimeMinutes = 25,
                fareMinThb = 75,
                fareMaxThb = 160,
                walkingMeters = 20,
                transfersCount = 0,
                transportTypes = listOf(TransportType.TAXI),
                steps = listOf(
                    RouteStep(
                        stepNumber = 1,
                        transportType = TransportType.TAXI,
                        instructionMm = "အစိမ်း-အဝါ သို့မဟုတ် ပန်းရောင် တက္ကစီကို လက်တားပါ။ တက်တက်ချင်း 'ဖွင့်မီတာ' ဟု ပြောပါ",
                        instructionTh = "โบกรถแท็กซี่ถูกกฎหมายและขอให้เปิดมิเตอร์ทันที",
                        instructionEn = "Hail licensed meter taxi and insist on meter",
                        durationMinutes = 25,
                        distanceMeters = 7500,
                        boardingPlace = fromNameMm,
                        alightingPlace = toNameMm,
                        driverPhraseTh = "ช่วยไปที่ $toNameMm หน่อยครับ เปิดมิเตอร์ด้วยครับ"
                    )
                ),
                safetyNoteMm = "မီတာမဖွင့်ဘဲ စျေးဖြတ်တောင်းသော တက္ကစီများကို လုံးဝမစီးပါနှင့်။ ကားနံပါတ်ကို ဓာတ်ပုံရိုက်ထားပါ",
                safetyNoteTh = "ห้ามขึ้นแท็กซี่ที่ไม่ยอมกดมิเตอร์เด็ดขาด จำทะเบียนรถไว้เสมอ"
            )
        )

        // Return the selected mode first if valid index, otherwise return all
        return if (selectedModeIndex in generatedRoutes.indices) {
            val primary = generatedRoutes[selectedModeIndex]
            val others = generatedRoutes.filterIndexed { index, _ -> index != selectedModeIndex }
            listOf(primary) + others
        } else {
            generatedRoutes
        }
    }

    // Room Database Operations
    val savedPlaces: Flow<List<SavedPlaceEntity>> = database.bangkokDao().getAllSavedPlaces()
    val savedRoutes: Flow<List<SavedRouteEntity>> = database.bangkokDao().getAllSavedRoutes()
    val userReports: Flow<List<UserReportEntity>> = database.bangkokDao().getAllReports()

    suspend fun savePlace(place: TransportPlace) {
        database.bangkokDao().savePlace(
            SavedPlaceEntity(
                id = place.id,
                type = place.type.name,
                nameMm = place.nameMm,
                nameTh = place.nameTh,
                nameEn = place.nameEn,
                latitude = place.latitude,
                longitude = place.longitude,
                addressMm = place.addressMm
            )
        )
    }

    suspend fun removeSavedPlace(placeId: String) {
        database.bangkokDao().deletePlace(placeId)
    }

    suspend fun isPlaceSaved(placeId: String): Boolean {
        return database.bangkokDao().isPlaceSaved(placeId)
    }

    suspend fun saveRoute(route: MultimodalRoute) {
        database.bangkokDao().saveRoute(
            SavedRouteEntity(
                id = route.id,
                titleMm = route.titleMm,
                titleTh = route.titleTh,
                totalTimeMinutes = route.totalTimeMinutes,
                fareThbText = "${route.fareMinThb} - ${route.fareMaxThb} THB",
                stepsSummaryMm = route.steps.joinToString(" ➔ ") { it.instructionMm }
            )
        )
    }

    suspend fun removeSavedRoute(routeId: String) {
        database.bangkokDao().deleteRoute(routeId)
    }

    suspend fun submitReport(placeOrRouteName: String, issueType: String, details: String) {
        database.bangkokDao().insertReport(
            UserReportEntity(
                placeOrRouteName = placeOrRouteName,
                issueType = issueType,
                details = details
            )
        )
    }
}
