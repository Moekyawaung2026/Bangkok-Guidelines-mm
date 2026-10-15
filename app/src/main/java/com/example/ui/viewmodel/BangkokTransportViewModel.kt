package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiTransportAssistant
import com.example.data.local.AppDatabase
import com.example.data.local.SavedPlaceEntity
import com.example.data.local.SavedRouteEntity
import com.example.data.local.UserReportEntity
import com.example.data.model.*
import com.example.data.repository.TransportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppTab {
    HOME,
    NEARBY,
    PLANNER,
    BUS,
    LINES,
    TAXI,
    SAFETY,
    PHRASEBOOK,
    AI_ASSISTANT,
    SAVED,
    ADMIN
}

data class BangkokUiState(
    val isBurmese: Boolean = true,
    val isDarkMode: Boolean = false,
    val activeTab: AppTab = AppTab.HOME,
    val searchQuery: String = "",
    val searchResults: List<TransportPlace> = emptyList(),
    // Nearby GPS State
    val userLatitude: Double = 13.7628, // Default Victory Monument, Bangkok
    val userLongitude: Double = 100.5372,
    val hasGpsPermission: Boolean = false,
    val selectedRadiusMeters: Double = 3000.0, // 500m, 1000m, 3000m, 5000m
    val selectedCategory: TransportType? = null,
    val nearbyPlaces: List<Pair<TransportPlace, Double>> = emptyList(),
    // Route Planner State
    val plannerFrom: String = "အောင်ဆန်းရတနာ (Victory Monument)",
    val plannerTo: String = "ဆယမ် (Siam)",
    val selectedRouteModeIndex: Int = 0,
    val plannedRoutes: List<MultimodalRoute> = emptyList(),
    // Active Place Detail
    val selectedPlace: TransportPlace? = null,
    val driverFlashcardText: String? = null,
    // Bus Guide
    val selectedBusNumber: String = "12",
    val busSearchQuery: String = "",
    // Phrasebook
    val selectedPhraseCategory: String = "လမ်းမေးမြန်းခြင်း",
    // AI Assistant
    val aiMessages: List<Pair<Boolean, String>> = listOf(
        Pair(false, "မင်္ဂလာပါ! ဘန်ကောက်မြို့တွင်း မည်သည့်နေရာသို့ သွားလိုပါသလဲ? အသက်သာဆုံး၊ အမြန်ဆုံး ဘတ်စ်ကားနှင့် ရထားလမ်းကြောင်းများကို မြန်မာလို အကြံပြုပေးပါမည်။\n(ဥပမာ- 'မဟာချိုင်စျေး ဘယ်လိုသွားရမလဲ' သို့မဟုတ် 'စမွတ်ပရာကန် စက်ရုံဇုန်')")
    ),
    val aiInputText: String = "",
    val isAiLoading: Boolean = false,
    // Saved Items
    val savedPlaces: List<SavedPlaceEntity> = emptyList(),
    val savedRoutes: List<SavedRouteEntity> = emptyList(),
    val userReports: List<UserReportEntity> = emptyList(),
    // Toast / Feedback message
    val snackbarMessage: String? = null
)

class BangkokTransportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransportRepository
    private val geminiAssistant = GeminiTransportAssistant()
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _uiState = MutableStateFlow(BangkokUiState())
    val uiState: StateFlow<BangkokUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransportRepository(database)

        // Initialize TTS for Thai pronunciation playback
        initTts(application)

        // Initial setup
        refreshNearbyPlaces()
        calculateRoutes()

        // Observe Room DB flows
        viewModelScope.launch {
            repository.savedPlaces.collect { list ->
                _uiState.update { it.copy(savedPlaces = list) }
            }
        }
        viewModelScope.launch {
            repository.savedRoutes.collect { list ->
                _uiState.update { it.copy(savedRoutes = list) }
            }
        }
        viewModelScope.launch {
            repository.userReports.collect { list ->
                _uiState.update { it.copy(userReports = list) }
            }
        }
    }

    private fun initTts(application: Application) {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("th", "TH"))
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isBurmese = !it.isBurmese) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(activeTab = tab, selectedPlace = null) }
    }

    fun onSearchQueryChanged(query: String) {
        val results = repository.searchPlaces(query)
        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun updateLocation(lat: Double, lng: Double, isGranted: Boolean) {
        _uiState.update {
            it.copy(userLatitude = lat, userLongitude = lng, hasGpsPermission = isGranted)
        }
        refreshNearbyPlaces()
    }

    fun setRadius(radiusMeters: Double) {
        _uiState.update { it.copy(selectedRadiusMeters = radiusMeters) }
        refreshNearbyPlaces()
    }

    fun setCategoryFilter(category: TransportType?) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshNearbyPlaces()
    }

    fun refreshNearbyPlaces() {
        val state = _uiState.value
        val nearby = repository.getNearbyPlaces(
            userLat = state.userLatitude,
            userLng = state.userLongitude,
            radiusMeters = state.selectedRadiusMeters,
            selectedType = state.selectedCategory
        )
        _uiState.update { it.copy(nearbyPlaces = nearby) }
    }

    fun setPlannerFrom(from: String) {
        _uiState.update { it.copy(plannerFrom = from) }
        calculateRoutes()
    }

    fun setPlannerTo(to: String) {
        _uiState.update { it.copy(plannerTo = to) }
        calculateRoutes()
    }

    fun setRouteMode(modeIndex: Int) {
        _uiState.update { it.copy(selectedRouteModeIndex = modeIndex) }
        calculateRoutes()
    }

    fun swapPlannerEndpoints() {
        _uiState.update {
            it.copy(plannerFrom = it.plannerTo, plannerTo = it.plannerFrom)
        }
        calculateRoutes()
    }

    fun calculateRoutes() {
        val state = _uiState.value
        val routes = repository.planRoutes(state.plannerFrom, state.plannerTo, state.selectedRouteModeIndex)
        _uiState.update { it.copy(plannedRoutes = routes) }
    }

    fun selectPlace(place: TransportPlace?) {
        _uiState.update { it.copy(selectedPlace = place) }
    }

    fun showDriverFlashcard(phraseTh: String) {
        _uiState.update { it.copy(driverFlashcardText = phraseTh) }
    }

    fun hideDriverFlashcard() {
        _uiState.update { it.copy(driverFlashcardText = null) }
    }

    fun setSelectedBusNumber(busNum: String) {
        _uiState.update { it.copy(selectedBusNumber = busNum) }
    }

    fun setSelectedPhraseCategory(cat: String) {
        _uiState.update { it.copy(selectedPhraseCategory = cat) }
    }

    fun savePlace(place: TransportPlace) {
        viewModelScope.launch {
            repository.savePlace(place)
            _uiState.update {
                it.copy(snackbarMessage = if (it.isBurmese) "နေရာကို သိမ်းဆည်းပြီးပါပြီ" else "บันทึกสถานที่แล้ว")
            }
        }
    }

    fun removeSavedPlace(placeId: String) {
        viewModelScope.launch {
            repository.removeSavedPlace(placeId)
        }
    }

    fun saveRoute(route: MultimodalRoute) {
        viewModelScope.launch {
            repository.saveRoute(route)
            _uiState.update {
                it.copy(snackbarMessage = if (it.isBurmese) "လမ်းကြောင်းကို အော့ဖ်လိုင်းအတွက် သိမ်းဆည်းပြီးပါပြီ" else "บันทึกเส้นทางแบบออฟไลน์แล้ว")
            }
        }
    }

    fun removeSavedRoute(routeId: String) {
        viewModelScope.launch {
            repository.removeSavedRoute(routeId)
        }
    }

    fun submitReport(placeOrRoute: String, issueType: String, details: String) {
        viewModelScope.launch {
            repository.submitReport(placeOrRoute, issueType, details)
            _uiState.update {
                it.copy(snackbarMessage = if (it.isBurmese) "တင်ပြချက်ကို လက်ခံရရှိပါသည်၊ စစ်ဆေးအတည်ပြုပါမည်" else "ส่งรายงานแล้ว เจ้าหน้าที่จะทำการตรวจสอบ")
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun speakThaiText(thaiText: String) {
        if (isTtsReady && tts != null) {
            tts?.speak(thaiText, TextToSpeech.QUEUE_FLUSH, null, "ThaiTtsId")
        }
    }

    fun updateAiInput(input: String) {
        _uiState.update { it.copy(aiInputText = input) }
    }

    fun sendAiMessage(message: String) {
        if (message.isBlank()) return
        val currentMessages = _uiState.value.aiMessages.toMutableList()
        currentMessages.add(Pair(true, message))

        _uiState.update {
            it.copy(
                aiMessages = currentMessages,
                aiInputText = "",
                isAiLoading = true
            )
        }

        viewModelScope.launch {
            val response = geminiAssistant.askAssistant(message, _uiState.value.isBurmese)
            val updated = _uiState.value.aiMessages.toMutableList()
            updated.add(Pair(false, response))
            _uiState.update {
                it.copy(
                    aiMessages = updated,
                    isAiLoading = false
                )
            }
        }
    }

    fun getAllPlaces(): List<TransportPlace> = repository.getAllPlaces()
    fun getBusRoutes(): List<BusRouteInfo> = repository.getBusRoutes()
    fun getEmergencyContacts(): List<EmergencyContact> = repository.getEmergencyContacts()
    fun getPhrasebook(): List<PhrasebookItem> = repository.getPhrasebook()
    fun getMajorTransitHubs(): List<TransitHub> = repository.getMajorTransitHubs()
    fun searchTransitHubs(query: String, category: TransitHubCategory = TransitHubCategory.ALL): List<TransitHub> =
        repository.searchTransitHubs(query, category)

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
