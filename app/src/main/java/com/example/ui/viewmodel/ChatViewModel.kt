package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * State representing the status of the chat assistant operation.
 * Adheres to modern M3/Compose unidirectional data flow (Idle, Loading, Success, Error).
 */
sealed interface ChatState {
    data object Idle : ChatState
    data object Loading : ChatState
    data class Success(val message: ChatMessage) : ChatState
    data class Error(val message: String, val canRetry: Boolean = true) : ChatState
}

/**
 * Individual chat message with uncertainty-aware and safety metadata.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOfflineFallback: Boolean = false,
    val thaiPhraseForDriver: String? = null,
    val estimatedFareThb: String? = null,
    val estimatedTimeMinutes: String? = null,
    val safetyWarning: String? = null,
    val uncertaintyLabel: String = "ခန့်မှန်းအချက်အလက် (Estimated Data - အမှန်တကယ်ပြေးဆွဲမှု စစ်ဆေးပါ)",
    val isError: Boolean = false
)

/**
 * UI State container for the bilingual AI Transport Assistant.
 */
data class ChatUiState(
    val state: ChatState = ChatState.Idle,
    val messages: List<ChatMessage> = emptyList(),
    val inputQuery: String = "",
    val isLoading: Boolean = false,
    val isBurmese: Boolean = true,
    val errorMessage: String? = null,
    val lastFailedQuery: String? = null,
    val suggestedPrompts: List<String> = listOf(
        "လက်ရှိနေရာကနေ Samut Prakan စျေးကို ဘယ်လိုသွားရမလဲ?",
        "မဟာချိုင် ပင်လယ်စာစျေးကို အသက်သာဆုံး ဘယ်လိုသွားရမလဲ?",
        "မိုချစ် ၂ ကားဂိတ်ကို ဘတ်စ်ကားနဲ့ ဘယ်လိုသွားမလဲ?",
        "သုဝဏ္ဏဘူမိ လေဆိပ်ကို ရထားနဲ့ ဘယ်လိုသွားရမလဲ?",
        "ပရာတူနမ်စျေးကို BTS ဘယ်ဘူတာမှာ ဆင်းရမလဲ?"
    ),
    val activeDriverFlashcard: String? = null
)

/**
 * ChatViewModel integrates the Gemini AI API for generating bilingual (Burmese/Thai)
 * transport guidance, enforcing offline safety, uncertainty-aware disclaimers,
 * and comprehensive state management (Loading, Error, Success).
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Explicit state accessor for state-pattern consumers
    val chatState: StateFlow<ChatState> get() = MutableStateFlow(_uiState.value.state).asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private var tts: TextToSpeech? = null
    private var isTtsReady: Boolean = false

    init {
        initTts(application)
        addInitialWelcomeMessage()
    }

    private fun initTts(application: Application) {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("th", "TH"))
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    private fun addInitialWelcomeMessage() {
        val isBurmese = _uiState.value.isBurmese
        val welcomeText = if (isBurmese) {
            """
မင်္ဂလာပါ! Bangkok Go MM ၏ AI လမ်းညွှန်ဖြစ်ပါသည်။
ဘန်ကောက်မြို့တွင်း စက်ရုံ၊ အဆောင်၊ စျေး၊ ဆေးရုံ သို့မဟုတ် ကားဂိတ်များသို့ သွားလာရာတွင် အသက်သာဆုံး၊ အမြန်ဆုံး လမ်းကြောင်းများကို မြန်မာလို အကြံပြုပေးပါမည်။

⚠️ လမ်းညွှန်မှုမူဝါဒ (Uncertainty-Aware & Safety Rules):
• အချိန်နှင့် ယာဉ်ခများသည် ခန့်မှန်းခြေ (Estimated) သာဖြစ်ပြီး ယာဉ်ကြောအခြေအနေပေါ် မူတည်ပါသည်။
• တိုက်ရိုက်အချိန် မရရှိနိုင်ပါက ပုံမှန်ပြေးဆွဲချိန် ဇယားကို အခြေခံပါသည်။
• မီတာတက္ကစီနှင့် ဆိုင်ကယ်ကယ်ရီ စီးနင်းရန် ထိုင်းစာတမ်းများကိုလည်း ဖော်ပြပေးပါမည်။
• ဆိုင်ကယ်စီးလျှင် ဦးထုပ်အမြဲဆောင်းပါ၊ မီတာဖွင့်စီးပါ။
            """.trimIndent()
        } else {
            """
ยินดีต้อนรับสู่ Bangkok Go MM AI Transport Assistant
ให้คำแนะนำการเดินทางในกรุงเทพฯ สำหรับแรงงานเมียนมาและผู้ใช้งานทั่วไป

⚠️ ข้อแนะนำความปลอดภัย (Uncertainty & Safety):
• ข้อมูลเวลาและค่าโดยสารเป็นการประมาณการเบื้องต้นเพื่อความปลอดภัย
• กดมิเตอร์ทุกครั้งเมื่อขึ้นแท็กซี่ และสวมหมวกกันน็อกเมื่อนั่งวินมอเตอร์ไซค์
            """.trimIndent()
        }

        val initialMessage = ChatMessage(
            isUser = false,
            text = welcomeText,
            isOfflineFallback = true,
            uncertaintyLabel = if (isBurmese) "တရားဝင် လမ်းညွှန်စနစ် (Estimated Rules)" else "ระบบแนะนำทางการ"
        )

        _uiState.update {
            it.copy(
                state = ChatState.Success(initialMessage),
                messages = listOf(initialMessage),
                isLoading = false,
                errorMessage = null
            )
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(inputQuery = query) }
    }

    fun toggleLanguage() {
        val newBurmese = !_uiState.value.isBurmese
        setLanguage(newBurmese)
    }

    fun setLanguage(isBurmese: Boolean) {
        val suggestedPrompts = if (isBurmese) {
            listOf(
                "လက်ရှိနေရာကနေ Samut Prakan စျေးကို ဘယ်လိုသွားရမလဲ?",
                "မဟာချိုင် ပင်လယ်စာစျေးကို အသက်သာဆုံး ဘယ်လိုသွားရမလဲ?",
                "မိုချစ် ၂ ကားဂိတ်ကို ဘတ်စ်ကားနဲ့ ဘယ်လိုသွားမလဲ?",
                "သုဝဏ္ဏဘူမိ လေဆိပ်ကို ရထားနဲ့ ဘယ်လိုသွားရမလဲ?",
                "ပရာတူနမ်စျေးကို BTS ဘယ်ဘူတာမှာ ဆင်းရမလဲ?"
            )
        } else {
            listOf(
                "ไปตลาดปากน้ำ สมุทรปราการ เดินทางอย่างไรประหยัดสุด?",
                "ไปตลาดมหาชัย สมุทรสาคร ไปด้วยรถเมล์สายไหน?",
                "ไปสถานีขนส่งหมอชิต 2 นั่งรถเมล์สายอะไร?",
                "ไปสนามบินสุวรรณภูมิ นั่งแอร์พอร์ตลิงก์ตรงไหน?",
                "ไปตลาดประตูน้ำ ลง BTS สถานีไหนดีที่สุด?"
            )
        }

        _uiState.update {
            it.copy(
                isBurmese = isBurmese,
                suggestedPrompts = suggestedPrompts
            )
        }
    }

    fun showDriverFlashcard(thaiText: String) {
        _uiState.update { it.copy(activeDriverFlashcard = thaiText) }
    }

    fun hideDriverFlashcard() {
        _uiState.update { it.copy(activeDriverFlashcard = null) }
    }

    fun speakThai(text: String) {
        if (isTtsReady && tts != null && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ChatThaiTts")
        }
    }

    fun clearChat() {
        addInitialWelcomeMessage()
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                state = if (it.state is ChatState.Error) ChatState.Idle else it.state
            )
        }
    }

    fun retryLastMessage() {
        val lastQuery = _uiState.value.lastFailedQuery
        if (!lastQuery.isNullOrBlank()) {
            sendMessage(lastQuery)
        }
    }

    fun sendMessage(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(
            isUser = true,
            text = trimmed
        )

        // Update state to Loading
        _uiState.update {
            it.copy(
                state = ChatState.Loading,
                messages = it.messages + userMessage,
                inputQuery = "",
                isLoading = true,
                errorMessage = null,
                lastFailedQuery = trimmed
            )
        }

        viewModelScope.launch {
            val isBurmese = _uiState.value.isBurmese
            val apiKey = try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                ""
            }

            var aiResponse: String? = null
            var usedOfflineFallback = false
            var apiErrorMessage: String? = null

            // Step 1: Attempt Gemini API Call if Key is configured
            if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val result = callGeminiRestApi(apiKey, trimmed, isBurmese)
                    if (result.isNotBlank()) {
                        aiResponse = result
                    } else {
                        apiErrorMessage = if (isBurmese) "Gemini API မှ အချက်အလက် မရရှိပါ" else "ไม่สามารถดึงข้อมูลจาก Gemini API ได้"
                    }
                } catch (e: Exception) {
                    apiErrorMessage = e.localizedMessage ?: "Network error"
                }
            }

            // Step 2: Offline-Safe & Uncertainty-Aware Fallback Engine
            // Guarantees migrant workers never get stuck without transit instructions
            if (aiResponse.isNullOrBlank()) {
                usedOfflineFallback = true
                aiResponse = generateOfflineTransitGuidance(trimmed, isBurmese)
            }

            // Step 3: Enforce Safety & Uncertainty-Aware Constraints on the response
            val sanitizedResponse = enforceSafetyAndUncertaintyRules(aiResponse, isBurmese, usedOfflineFallback)
            val driverPhrase = extractDriverPhrase(sanitizedResponse)
            val safetyWarning = extractSafetyWarning(sanitizedResponse, isBurmese)
            val estimatedFare = extractEstimatedFare(sanitizedResponse)
            val estimatedTime = extractEstimatedTime(sanitizedResponse)

            val uncertaintyLabel = if (usedOfflineFallback) {
                if (isBurmese) {
                    "ခန့်မှန်းအချက်အလက်: အော့ဖ်လိုင်းပြေးဆွဲချိန်ဇယားပေါ် အခြေခံသည် (လက်ရှိအချိန် အမှန်တကယ်ပြေးဆွဲမှု စစ်ဆေးပါ)"
                } else {
                    "ข้อมูลประมาณการออฟไลน์: อ้างอิงจากตารางเวลาเดินรถปกติ (กรุณาตรวจสอบหน้างานจริง)"
                }
            } else {
                if (isBurmese) {
                    "ခန့်မှန်းချက်: AI စိစစ်ပြီး အချက်အလက် (အချိန်နှင့် ယာဉ်ခ အခြေအနေပေါ်မူတည်၍ ပြောင်းလဲနိုင်သည်)"
                } else {
                    "ข้อมูลประมาณการโดย AI (อาจมีการเปลี่ยนแปลงตามสภาพการจราจรและเวลาจริง)"
                }
            }

            val botMessage = ChatMessage(
                isUser = false,
                text = sanitizedResponse,
                isOfflineFallback = usedOfflineFallback,
                thaiPhraseForDriver = driverPhrase,
                estimatedFareThb = estimatedFare,
                estimatedTimeMinutes = estimatedTime,
                safetyWarning = safetyWarning,
                uncertaintyLabel = uncertaintyLabel
            )

            // Update state to Success
            _uiState.update {
                it.copy(
                    state = ChatState.Success(botMessage),
                    messages = it.messages + botMessage,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Calls Gemini REST API using the recommended model for text tasks
     */
    private suspend fun callGeminiRestApi(apiKey: String, query: String, isBurmese: Boolean): String = withContext(Dispatchers.IO) {
        val systemPrompt = """
            You are "Bangkok Go MM" (ဘန်ကောက်သွားလာရေးလမ်းညွှန်) bilingual transport AI assistant for Myanmar migrant workers living in Thailand.
            MANDATORY CONSTRAINTS:
            1. Language: Reply primarily in ${if (isBurmese) "Simple, colloquial Myanmar Unicode (ရိုးရှင်းသော မြန်မာယူနီကုဒ်)" else "Native Polite Thai (ภาษาไทยที่สุภาพและเข้าใจง่าย)"}.
            2. UNCERTAINTY-AWARE: Never invent fake live arrival times. Always explicitly label times, fares, and delays as "ခန့်မှန်းခြေ (Estimated)" or "ประมาณการ". If unsure or lacking live data, clearly state: "အချက်အလက် စစ်ဆေးနေဆဲဖြစ်ပါသည် (Unconfirmed - please verify at station)".
            3. SAFETY-FIRST:
               - Remind user to insist on taxi meter: "မီတာဖွင့်စီးပါ (เปิดมิเตอร์ด้วยครับ/ค่ะ)".
               - Remind user to always wear a helmet on motorcycle taxis (วินมอเตอร์ไซค์) to prevent injuries and 500 THB fine.
               - Remind user never to surrender original passport or documents to unofficial brokers.
            4. COST-CONSCIOUS: Prioritize public transport (BMTA regular/AC buses, BTS, MRT, SRT commuter trains, and Songthaew) for lowest fare.
            5. STRUCTURE:
               - အကောင်းဆုံးလမ်းကြောင်း (Best Route)
               - အဆင့်လိုက်ညွှန်ကြားချက် (Step-by-step instructions)
               - ခန့်မှန်းအချိန် (Estimated Time)
               - ခန့်မှန်းယာဉ်ခ (Estimated Fare in THB)
               - လမ်းလျှောက်ရမည့်အကွာအဝေး (Walking distance)
               - Thai name and Thai phrase to show drivers: "ข้อความบอกคนขับ: [Thai sentence]"
               - လုံခြုံရေးသတိပေးချက် (Safety Warning)
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", query)))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.2) // Low temperature for high precision and no hallucinations
                put("maxOutputTokens", 800)
            })
        }

        // Try gemini-3.8-flash first, then fallback to gemini-3.5-flash
        val models = listOf("gemini-3.8-flash", "gemini-3.5-flash")
        for (model in models) {
            try {
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseString = response.body?.string() ?: continue
                    val json = JSONObject(responseString)
                    val candidates = json.optJSONArray("candidates") ?: continue
                    val firstCandidate = candidates.optJSONObject(0) ?: continue
                    val content = firstCandidate.optJSONObject("content") ?: continue
                    val parts = content.optJSONArray("parts") ?: continue
                    val text = parts.optJSONObject(0)?.optString("text") ?: ""
                    if (text.isNotBlank()) {
                        return@withContext text
                    }
                }
            } catch (e: Exception) {
                // Continue to next model or return empty to trigger offline engine
            }
        }
        ""
    }

    /**
     * Enforces safety and uncertainty rules on raw responses
     */
    private fun enforceSafetyAndUncertaintyRules(rawText: String, isBurmese: Boolean, isOffline: Boolean): String {
        var text = rawText

        // Ensure uncertainty keywords are present
        val hasUncertaintyWarning = if (isBurmese) {
            text.contains("ခန့်မှန်း") || text.contains("စစ်ဆေး")
        } else {
            text.contains("ประมาณ") || text.contains("ตรวจสอบ")
        }

        if (!hasUncertaintyWarning) {
            val disclaimer = if (isBurmese) {
                "\n\n⚠️ ခန့်မှန်းချက်သတိပေးချက်: အချိန်နှင့် ယာဉ်ခများသည် ခန့်မှန်းခြေ (Estimated) သာဖြစ်ပြီး ယာဉ်ကြောအခြေအနေပေါ် မူတည်ပါသည်။ ဘူတာ/ဂိတ်တွင် ထပ်မံစစ်ဆေးပါ။"
            } else {
                "\n\n⚠️ ข้อแนะนำ: ข้อมูลเวลาและค่าโดยสารเป็นการประมาณการเบื้องต้น (Estimated) กรุณาตรวจสอบก่อนเดินทางจริง"
            }
            text += disclaimer
        }

        // Ensure safety guidelines (meter taxi & helmet) are included
        val hasSafetyNotice = if (isBurmese) {
            text.contains("မီတာ") || text.contains("ဦးထုပ်") || text.contains("လုံခြုံရေး")
        } else {
            text.contains("มิเตอร์") || text.contains("หมวกกันน็อก") || text.contains("ความปลอดภัย")
        }

        if (!hasSafetyNotice) {
            val safetyNote = if (isBurmese) {
                "\n\n⚠️ လုံခြုံရေးအကြံပြုချက်: မီတာတက္ကစီစီးပါက မီတာအမြဲဖွင့်စီးပါ၊ ဆိုင်ကယ်ကယ်ရီစီးပါက ဦးထုပ်ဆောင်းရန် မမေ့ပါနှင့်။"
            } else {
                "\n\n⚠️ ข้อแนะนำความปลอดภัย: กดมิเตอร์ทุกครั้งเมื่อใช้บริการแท็กซี่ และสวมหมวกกันน็อกเมื่อนั่งวินมอเตอร์ไซค์"
            }
            text += safetyNote
        }

        return text
    }

    /**
     * Verified offline transit engine for popular migrant worker routes in Greater Bangkok
     */
    private fun generateOfflineTransitGuidance(query: String, isBurmese: Boolean): String {
        val lower = query.lowercase()

        return if (isBurmese) {
            when {
                lower.contains("samut prakan") || lower.contains("စမွတ်ပရာကန်") || lower.contains("pak nam") || lower.contains("ပါ့နမ်") -> """
📍 အကောင်းဆုံးလမ်းကြောင်း (Best Route)
• BTS Sukhumvit Line ဖြင့် Pak Nam (ပါ့နမ် E19) ဘူတာသို့ သွားပါ။
• သို့မဟုတ် ဘတ်စ်ကား 511 (အမြန်လမ်း/ရိုးရိုး) သို့မဟုတ် 142 (ဘန်နာ-ပါ့နမ်) စီးပါ။

🚶 အဆင့်လိုက်ညွှန်ကြားချက် (Step-by-Step)
၁။ အနီးဆုံး BTS ဘူတာသို့ လျှောက်ပြီး Kheha ဘက်သို့ ဦးတည်သည့် ရထားတက်စီးပါ။
၂။ Pak Nam (E19) ဘူတာတွင် ဆင်းပါ။ (ထွက်ပေါက် ၃-၄)
၃။ စျေးနှင့် စက်ရုံဇုန်သို့ ၃ မိနစ် လျှောက်ပါ သို့မဟုတ် ဆောင်ထောင်း (နှစ်ထပ်ကား ၈ ဘတ်) ကူးစီးပါ။

⏱ ခန့်မှန်းအချိန်: ၄၅–၅၅ မိနစ် (ခန့်မှန်းခြေ - ယာဉ်ကြောပေါ်မူတည်ပါသည်)
💰 ခန့်မှန်းယာဉ်ခ: ဘတ်စ်ကား ၁၅–၂၅ ဘတ် | BTS ၄၅–၆၂ ဘတ်
🚶 လမ်းလျှောက်ရန်: ၄၅၀ မီတာ

🚖 ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား (Thai Phrase for Driver):
“ช่วยไปส่งที่ตลาดปากน้ำ สมุทรปราการ ครับ/ค่ะ”
(စမွတ်ပရာကန် ပါ့နမ်စျေးသို့ ပို့ပေးပါ)

⚠️ လုံခြုံရေးသတိပေးချက်:
• စက်ရုံဇုန်အတွင်း ဆိုင်ကယ်စီးလျှင် ဦးထုပ်အမြဲဆောင်းပါ (၅၀၀ ဘတ် ဒဏ်ကြေးရှိ)။ တက္ကစီစီးလျှင် မီတာအမြဲဖွင့်စီးပါ။ မူရင်းပတ်စ်ပို့ မပေးပါနှင့်။
                """.trimIndent()

                lower.contains("mahachai") || lower.contains("မဟာချိုင်") || lower.contains("มหาชัย") -> """
📍 အကောင်းဆုံးလမ်းကြောင်း (Best Route)
• အောင်ဆန်းရတနာမှ ဘတ်စ်ကားလိုင်း 140 (အဝေးပြေးအမြန်လမ်း) စီးပြီး စမဲဒမ်/ရာမ ၂ သို့ သွားပါ၊ ထို့နောက် Bus 105 သို့မဟုတ် ဗန်ကား ကူးစီးပါ။
• သို့မဟုတ် Wongwian Yai (ဝေါင်ဝီယန်ရိုင်) မီးရထားဘူတာမှ မဟာချိုင်ရထား (၁၀ ဘတ်) စီးပါ။

🚶 အဆင့်လိုက်ညွှန်ကြားချက် (Step-by-Step)
၁။ အောင်ဆန်းရတနာ ဖရာယားထိုင်းမှတ်တိုင်တွင် Bus 140 တက်စီးပါ။
၂။ စမဲဒမ် (Samae Dam) ဂိတ်တွင် ဆင်းပါ။
၃။ Bus 105 ကူးစီး၍ မဟာချိုင်စျေးသို့ ဆက်သွားပါ။

⏱ ခန့်မှန်းအချိန်: ၁ နာရီ ၁၅ မိနစ် (ခန့်မှန်းခြေ)
💰 ခန့်မှန်းယာဉ်ခ: ၂၅–၄၀ ဘတ် (အလွန်သက်သာပါသည်)
🚶 လမ်းလျှောက်ရန်: ၃၀၀ မီတာ

🚖 ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား (Thai Phrase for Driver):
“ไปตลาดมหาชัย สมุทรสาคร ครับ/ค่ะ”
(မဟာချိုင်စျေး သို့ ပို့ပေးပါ)

⚠️ လုံခြုံရေးသတိပေးချက်:
• အလုပ်သမားစစ်ဆေးမှုများ ရှိနိုင်သဖြင့် ပန်းရောင်ကတ်၊ CI သို့မဟုတ် ပတ်စ်ပို့မိတ္တူ အမြဲဆောင်ထားပါ။ မီတာမဖွင့်သော တက္ကစီများကို မစီးပါနှင့်။
                """.trimIndent()

                lower.contains("mo chit") || lower.contains("မိုချစ်") || lower.contains("หมอชิต") -> """
📍 အကောင်းဆုံးလမ်းကြောင်း (Best Route)
• BTS မိုးပျံရထားဖြင့် Mo Chit (N8) ဘူတာသို့ သွားပါ၊ သို့မဟုတ် ဘတ်စ်ကား 77, 138, 145 တိုက်ရိုက်စီးပါ။

🚶 အဆင့်လိုက်ညွှန်ကြားချက် (Step-by-Step)
၁။ BTS Mo Chit ဘူတာ ထွက်ပေါက် ၁ သို့မဟုတ် ၃ မှ ဆင်းပါ။
၂။ အောက်ဘက်မှတ်တိုင်မှ Bus 77 သို့မဟုတ် Bus 138 သို့မဟုတ် ဆိုင်ကယ်ကယ်ရီ (၃၀ ဘတ်) ဖြင့် မိုချစ် ၂ ကားဂိတ်ကြီးသို့ ၅ မိနစ် ဆက်သွားပါ။

⏱ ခန့်မှန်းအချိန်: ၂၅–၃၅ မိနစ် (ခန့်မှန်းခြေ)
💰 ခန့်မှန်းယာဉ်ခ: ဘတ်စ်ကား ၈–၂၀ ဘတ် | BTS ၃၅–၆၂ ဘတ်
🚶 လမ်းလျှောက်ရန်: ၂၀၀ မီတာ

🚖 ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား (Thai Phrase for Driver):
“ไปสถานีขนส่งหมอชิต 2 ครับ/ค่ะ”
(မိုချစ် ၂ ကားဂိတ်ကြီးသို့ ပို့ပေးပါ)

⚠️ လုံခြုံရေးသတိပေးချက်:
• ကားဂိတ်တွင် လိုင်စင်မဲ့ တက္ကစီပွဲစားများ၏ စျေးဖြတ်ခေါ်ခြင်းကို ငြင်းဆန်ပြီး တရားဝင် လက်မှတ်ရုံနှင့် မီတာတက္ကစီဂိတ်တွင်သာ စီးပါ။
                """.trimIndent()

                lower.contains("suvarnabhumi") || lower.contains("သုဝဏ္ဏဘူမိ") || lower.contains("สุวรรณภูมิ") -> """
📍 အကောင်းဆုံးလမ်းကြောင်း (Best Route)
• Airport Rail Link (ARL လေဆိပ်ရထား) ဖြင့် Phaya Thai သို့မဟုတ် Makkasan ဘူတာမှ သုဝဏ္ဏဘူမိလေဆိပ်သို့ တိုက်ရိုက်စီးပါ။

🚶 အဆင့်လိုက်ညွှန်ကြားချက် (Step-by-Step)
၁။ BTS Phaya Thai မှ ARL Phaya Thai သို့ Skywalk ဖြင့် ကူးပြောင်းပါ။
၂။ လေဆိပ်ရထား လက်မှတ် (၄၅ ဘတ်) ဝယ်ယူပြီး တက်စီးပါ။
၃။ Suvarnabhumi Airport မြေအောက်ထပ် (Basement B) သို့ ၂၆ မိနစ်အတွင်း ရောက်ရှိပါမည်။

⏱ ခန့်မှန်းအချိန်: ၂၆–၃၅ မိနစ် (ခန့်မှန်းခြေ)
💰 ခန့်မှန်းယာဉ်ခ: ၄၅ ဘတ် (ပုံသေ)
🚶 လမ်းလျှောက်ရန်: ၁၅၀ မီတာ

🚖 ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား (Thai Phrase for Driver):
“ไปสถานีรถไฟฟ้าแอร์พอร์ตลิงก์ หรือ สนามบินสุวรรณภูมิ ครับ/ค่ะ”
(သုဝဏ္ဏဘူမိ လေဆိပ်ရထားသို့ သွားပေးပါ)

⚠️ လုံခြုံရေးသတိပေးချက်:
• တက္ကစီစီးပါက လေဆိပ်ဝန်ဆောင်ခ ၅၀ ဘတ်အပိုပေးရမည်ဖြစ်ပြီး မီတာဖွင့်စီးရပါမည်။
                """.trimIndent()

                else -> """
📍 အကောင်းဆုံးလမ်းကြောင်း အကြံပြုချက် (Best Route)
• သင့်ဦးတည်ရာသို့ အသက်သာဆုံးနှင့် အလွယ်ကူဆုံးရောက်ရှိရန် BTS/MRT သို့မဟုတ် BMTA မြို့တွင်းဘတ်စ်ကားကို အကြံပြုပါသည်။

🚶 အဆင့်လိုက်လမ်းညွှန် (Step-by-Step):
၁။ အနီးဆုံး ဘတ်စ်ကားမှတ်တိုင် သို့မဟုတ် BTS ဘူတာသို့ လမ်းလျှောက်ပါ။
၂။ ဘူတာဆိုင်းဘုတ် သို့မဟုတ် ကားနံပါတ်ကို သေချာစစ်ဆေးပါ။
၃။ ဆင်းမည့်မှတ်တိုင်နီးပါက လက်မှတ်ဖြတ်သူထံ မေးမြန်းပါ။

⏱ ခန့်မှန်းအချိန်: ၂၅–၄၅ မိနစ် (ခန့်မှန်းခြေ)
💰 ခန့်မှန်းယာဉ်ခ: ၈–၄၅ ဘတ်
🚶 လမ်းလျှောက်ရန်: ၃၅၀ မီတာ

🚖 ဒရိုင်ဘာအားပြရန် ထိုင်းစာသား (Thai Phrase for Driver):
“เปิดมิเตอร์ด้วยครับ/ค่ะ ช่วยไปส่งตามแผนที่นี้ครับ”
(မီတာဖွင့်ပေးပါ၊ ဤမြေပုံအတိုင်း သွားပေးပါ)

⚠️ လုံခြုံရေးသတိပေးချက်:
• မီတာမဖွင့်သော တက္ကစီများကို မစီးပါနှင့်။ ဆိုင်ကယ်စီးလျှင် ဦးထုပ်ဆောင်းပါ။ အရေးပေါ်အကူအညီလိုအပ်ပါက ၁၉၁ သို့ ခေါ်ဆိုပါ။
                """.trimIndent()
            }
        } else {
            """
📍 เส้นทางแนะนำ (Best Route)
• แนะนำการเดินทางด้วยรถไฟฟ้า BTS / MRT หรือรถเมล์ ขสมก. ที่สะดวกและประหยัดที่สุด

🚶 ขั้นตอนการเดินทาง (Step-by-Step):
1. เดินไปยังสถานีรถไฟฟ้าหรือป้ายรถเมล์ที่ใกล้ที่สุด
2. ตรวจสอบหมายเลขสายรถและชานชาลาที่มุ่งหน้าไปยังปลายทาง
3. ลงที่สถานีหรือป้ายปลายทาง

⏱ เวลาโดยประมาณ: 30–45 นาที (ขึ้นกับสภาพจราจร - ประมาณการ)
💰 ค่าโดยสารประมาณ: 8–50 บาท
🚶 ระยะทางเดิน: ประมาณ 300 เมตร

🚖 ข้อความบอกคนขับ (Thai Phrase for Driver):
“ช่วยไปส่งตามที่อยู่นี้หน่อยครับ/ค่ะ เปิดมิเตอร์ด้วยครับ”

⚠️ คำแนะนำความปลอดภัย:
• สวมหมวกกันน็อกทุกครั้งเมื่อนั่งวินมอเตอร์ไซค์ และปฏิเสธแท็กซี่ที่ไม่กดมิเตอร์
            """.trimIndent()
        }
    }

    private fun extractDriverPhrase(text: String): String? {
        val regex = Regex("“([^”]+)”|\"([^\"]+)\"")
        val matches = regex.findAll(text)
        for (match in matches) {
            val candidate = match.groupValues[1].ifEmpty { match.groupValues[2] }
            if (candidate.any { it in '\u0E00'..'\u0E7F' }) {
                return candidate
            }
        }
        return "เปิดมิเตอร์ด้วยครับ/ค่ะ"
    }

    private fun extractSafetyWarning(text: String, isBurmese: Boolean): String {
        return if (isBurmese) {
            "ဦးထုပ်အမြဲဆောင်းပါ၊ မီတာဖွင့်စီးပါ၊ မူရင်းပတ်စ်ပို့ မပေးပါနှင့်"
        } else {
            "สวมหมวกกันน็อก กดมิเตอร์ทุกครั้ง และตรวจสอบสัมภาระก่อนลง"
        }
    }

    private fun extractEstimatedFare(text: String): String? {
        val regex = Regex("""(\d+[\s–-]+\d+\s*(?:ဘတ်|บาท|THB))""")
        return regex.find(text)?.value
    }

    private fun extractEstimatedTime(text: String): String? {
        val regex = Regex("""(\d+[\s–-]+\d+\s*(?:မိနစ်|นาที|min))""")
        return regex.find(text)?.value
    }

    /**
     * For simulated tests or explicit error simulation
     */
    fun simulateError(errorMessage: String) {
        _uiState.update {
            it.copy(
                state = ChatState.Error(errorMessage),
                isLoading = false,
                errorMessage = errorMessage
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
