package com.example.data.datasource

import android.content.Context
import com.example.R
import com.example.data.model.EmergencyContact
import com.example.data.model.SafetyCategory
import com.example.data.model.SafetyGuideData
import com.example.data.model.SafetyTipItem
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Loads the static safety tips guide and emergency contacts specifically tailored
 * for Myanmar migrant workers in Bangkok from the local Android raw resource file.
 */
class SafetyGuideResourceLoader(private val context: Context) {

    @Volatile
    private var cachedData: SafetyGuideData? = null

    /**
     * Loads the SafetyGuideData from res/raw/myanmar_worker_safety_guide.json.
     * Caches the result in memory for instant subsequent access.
     */
    fun loadSafetyGuide(): SafetyGuideData {
        cachedData?.let { return it }

        return try {
            val inputStream = context.resources.openRawResource(R.raw.myanmar_worker_safety_guide)
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            val root = JSONObject(jsonString)

            val version = root.optInt("version", 1)
            val lastUpdated = root.optString("last_updated", "2026-10-17")

            // Parse emergency contacts
            val contactsList = mutableListOf<EmergencyContact>()
            val contactsArray = root.optJSONArray("emergency_contacts")
            if (contactsArray != null) {
                for (i in 0 until contactsArray.length()) {
                    val obj = contactsArray.getJSONObject(i)
                    contactsList.add(
                        EmergencyContact(
                            id = obj.optString("id", "contact_$i"),
                            titleMm = obj.optString("title_mm"),
                            titleTh = obj.optString("title_th"),
                            titleEn = obj.optString("title_en"),
                            number = obj.optString("number"),
                            descriptionMm = obj.optString("description_mm"),
                            descriptionTh = obj.optString("description_th"),
                            iconType = obj.optString("icon_type", "police")
                        )
                    )
                }
            }

            // Parse safety tips
            val tipsList = mutableListOf<SafetyTipItem>()
            val tipsArray = root.optJSONArray("safety_tips")
            if (tipsArray != null) {
                for (i in 0 until tipsArray.length()) {
                    val obj = tipsArray.getJSONObject(i)
                    val catStr = obj.optString("category", "DOCUMENTS")
                    val category = runCatching { SafetyCategory.valueOf(catStr) }.getOrDefault(SafetyCategory.DOCUMENTS)

                    val dosList = mutableListOf<String>()
                    val dosArray = obj.optJSONArray("dos_mm")
                    if (dosArray != null) {
                        for (d in 0 until dosArray.length()) {
                            dosList.add(dosArray.getString(d))
                        }
                    }

                    val dontsList = mutableListOf<String>()
                    val dontsArray = obj.optJSONArray("donts_mm")
                    if (dontsArray != null) {
                        for (d in 0 until dontsArray.length()) {
                            dontsList.add(dontsArray.getString(d))
                        }
                    }

                    tipsList.add(
                        SafetyTipItem(
                            id = obj.optString("id", "tip_$i"),
                            category = category,
                            importance = obj.optString("importance", "HIGH"),
                            titleMm = obj.optString("title_mm"),
                            titleTh = obj.optString("title_th"),
                            titleEn = obj.optString("title_en"),
                            summaryMm = obj.optString("summary_mm"),
                            summaryTh = obj.optString("summary_th"),
                            detailMm = obj.optString("detail_mm"),
                            detailTh = obj.optString("detail_th"),
                            dosMm = dosList,
                            dontsMm = dontsList,
                            relevantHotline = obj.optString("relevant_hotline", "")
                        )
                    )
                }
            }

            val result = SafetyGuideData(
                version = version,
                lastUpdated = lastUpdated,
                emergencyContacts = contactsList,
                safetyTips = tipsList
            )
            cachedData = result
            result
        } catch (e: Exception) {
            // Fallback gracefully to default static data
            val fallback = createFallbackSafetyGuide()
            cachedData = fallback
            fallback
        }
    }

    private fun createFallbackSafetyGuide(): SafetyGuideData {
        val contacts = listOf(
            EmergencyContact("191", "အရေးပေါ် ရဲတပ်ဖွဲ့", "ตำรวจ 191", "Emergency Police", "191", "အရေးပေါ် အကူအညီ ၂၄ နာရီ", "เหตุด่วนเหตุร้าย", "police"),
            EmergencyContact("1155", "ခရီးသွားရဲတပ်ဖွဲ့", "ตำรวจท่องเที่ยว 1155", "Tourist Police", "1155", "ဘာသာစကားအကူအညီရဲတပ်ဖွဲ့", "ช่วยเหลือนักท่องเที่ยว", "police"),
            EmergencyContact("1669", "အရေးပေါ် လူနာတင်ယာဉ်", "สถาบันการแพทย์ฉุกเฉิน 1669", "Ambulance", "1669", "ဆေးဘက်အရေးပေါ် ၂၄ နာရီ", "สายด่วนอุบัติเหตุ", "ambulance"),
            EmergencyContact("1584", "ကုန်းလမ်းပို့ဆောင်ရေး", "กรมการขนส่งทางบก 1584", "DLT Transport Hotline", "1584", "တက္ကစီမီတာတိုင်ကြားရန်", "ร้องเรียนรถสาธารณะ", "taxi"),
            EmergencyContact("1506", "အလုပ်သမားဝန်ကြီးဌာန (ခလုတ် ၅)", "สายด่วนแรงงาน 1506 กด 5", "Labour Hotline", "1506", "မြန်မာဘာသာဖြင့် တိုင်ကြားရန်", "คุ้มครองแรงงาน", "labour"),
            EmergencyContact("embassy", "မြန်မာသံရုံး ဗန်ကောက်", "สถานทูตเมียนมา", "Myanmar Embassy", "+6622332237", "သံရုံးအကူအညီနှင့် စာရွက်စာတမ်း", "สถานทูต ถนนสาทร", "embassy")
        )

        val tips = listOf(
            SafetyTipItem(
                id = "tip_doc",
                category = SafetyCategory.DOCUMENTS,
                importance = "CRITICAL",
                titleMm = "စာရွက်စာတမ်း မူရင်းနှင့် မိတ္တူ လုံခြုံစွာ ကိုင်တွယ်ခြင်း",
                titleTh = "การจัดการเอกสารตัวจริงและสำเนาอย่างปลอดภัย",
                titleEn = "Document Safety",
                summaryMm = "မူရင်း Passport ကို အိမ်တွင် လုံခြုံစွာသိမ်းပြီး မိတ္တူသာ ဆောင်ထားပါ။",
                summaryTh = "พกสำเนาแทนตัวจริงเพื่อความปลอดภัย",
                detailMm = "မူရင်း Passport ကို အပြင်သွားရာတွင် နေ့စဉ် မသယ်ဆောင်ပါနှင့်။ ဖုန်းထဲတွင် ရှင်းလင်းသော ဓာတ်ပုံနှင့် ရောင်စုံမိတ္တူကို ပလတ်စတစ်လောင်း၍ ဆောင်ထားပါ။",
                detailTh = "เก็บพาสปอร์ตตัวจริงไว้ที่พัก และพกสำเนาพร้อมถ่ายรูปในมือถือ",
                dosMm = listOf("ရောင်စုံမိတ္တူဆောင်ထားပါ", "ဖုန်းထဲတွင် ဓာတ်ပုံသိမ်းထားပါ"),
                dontsMm = listOf("မူရင်းစာအုပ်ကို မည်သည့်ယာဉ်မောင်းကိုမျှ မပေးပါနှင့်"),
                relevantHotline = "1506"
            ),
            SafetyTipItem(
                id = "tip_taxi",
                category = SafetyCategory.TAXI_SAFETY,
                importance = "CRITICAL",
                titleMm = "မီတာတက္ကစီ စီးနင်းရာတွင် လိုက်နာရမည့် အချက်များ",
                titleTh = "การขึ้นแท็กซี่มิเตอร์อย่างปลอดภัย",
                titleEn = "Taxi Safety",
                summaryMm = "မီတာမဖွင့်ဘဲ စျေးဖြတ်တောင်းသော ကားများကို မစီးပါနှင့်။ အဝါရောင်နံပါတ်ပြား ဓာတ်ပုံရိုက်ထားပါ။",
                summaryTh = "ไม่ขึ้นรถเหมา ถ่ายรูปป้ายเหลืองส่งให้เพื่อน",
                detailMm = "တက္ကစီတိုင်း မီတာဖွင့်စီးရပါမည်။ မီတာမဖွင့်ပါက ချက်ချင်းဆင်းပါ။ ကားနံပါတ်ပြားနှင့် ယာဉ်မောင်းပုံကို မိသားစုထံ ပို့ထားပါ။",
                detailTh = "คนขับต้องกดมิเตอร์เสมอ หากไม่กดให้ลงทันทีและโทร 1584",
                dosMm = listOf("မီတာဖွင့်ပေးရန် အရင်ပြောပါ", "ကားနံပါတ် ဓာတ်ပုံရိုက်ထားပါ"),
                dontsMm = listOf("တရားမဝင် အနက်ရောင်လိုင်စင်ပြားကားများကို မစီးပါနှင့်"),
                relevantHotline = "1584"
            )
        )

        return SafetyGuideData(1, "2026-10-17", contacts, tips)
    }
}
