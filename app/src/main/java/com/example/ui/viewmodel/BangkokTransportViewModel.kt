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
import com.example.data.location.LocationService
import com.example.data.model.*
import com.example.data.repository.TransportRepository
import com.example.data.repository.TransportationTypeRepository
import com.example.data.repository.DefaultTransportationTypeRepository
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
    val selectedTransitLineType: TransportType = TransportType.BTS,
    val transportationTypes: List<TransportationTypeItem> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<TransportPlace> = emptyList(),
    // Nearby GPS State handled by LocationService
    val userLatitude: Double = 13.7628, // Default Victory Monument, Bangkok
    val userLongitude: Double = 100.5372,
    val hasGpsPermission: Boolean = false,
    val isGpsDetecting: Boolean = false,
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
    // Static Safety Tips Guide specifically for Myanmar Migrant Workers
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val safetyTips: List<SafetyTipItem> = emptyList(),
    val selectedSafetyCategory: SafetyCategory = SafetyCategory.ALL,
    val safetySearchQuery: String = "",
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
    val transportationTypeRepository: TransportationTypeRepository = DefaultTransportationTypeRepository()
    val locationService: LocationService = LocationService(application)
    private val geminiAssistant = GeminiTransportAssistant()
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _uiState = MutableStateFlow(BangkokUiState())
    val uiState: StateFlow<BangkokUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransportRepository(database, application)

        // Initialize TTS for Thai pronunciation playback
        initTts(application)

        // Load static safety tips guide and emergency contacts from local resources
        val safetyGuide = repository.getSafetyGuide()
        val allTypes = transportationTypeRepository.getAllTransportationTypes()
        _uiState.update {
            it.copy(
                emergencyContacts = safetyGuide.emergencyContacts,
                safetyTips = safetyGuide.safetyTips,
                transportationTypes = allTypes
            )
        }

        // Initial setup
        refreshNearbyPlaces()
        calculateRoutes()

        // Warm up and ensure Room offline cache is primed with transport places
        viewModelScope.launch {
            if (repository.getCachedPlacesCount() == 0) {
                repository.cachePlaces()
            }
        }

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

    fun selectTransitLineType(type: TransportType) {
        _uiState.update { it.copy(selectedTransitLineType = type, activeTab = AppTab.LINES, selectedPlace = null) }
    }

    fun getTransportationTypes(): List<TransportationTypeItem> =
        transportationTypeRepository.getAllTransportationTypes()

    fun selectTransportationType(item: TransportationTypeItem) {
        when (item.type) {
            TransportType.BTS -> selectTransitLineType(TransportType.BTS)
            TransportType.MRT -> selectTransitLineType(TransportType.MRT)
            TransportType.SRT -> selectTransitLineType(TransportType.SRT)
            TransportType.BUS, TransportType.BUS_STOP, TransportType.BUS_TERMINAL -> setTab(AppTab.BUS)
            TransportType.TAXI, TransportType.MOTORCYCLE_TAXI -> setTab(AppTab.TAXI)
            else -> setTab(AppTab.LINES)
        }
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

    fun getSafetyGuide(): SafetyGuideData = repository.getSafetyGuide()
    fun getSafetyTips(): List<SafetyTipItem> = repository.getSafetyTips()

    fun selectSafetyCategory(category: SafetyCategory) {
        _uiState.update { it.copy(selectedSafetyCategory = category) }
    }

    fun setSafetySearchQuery(query: String) {
        _uiState.update { it.copy(safetySearchQuery = query) }
    }

    fun getFilteredSafetyTips(): List<SafetyTipItem> {
        val state = _uiState.value
        val categoryFiltered = if (state.selectedSafetyCategory == SafetyCategory.ALL) {
            state.safetyTips
        } else {
            state.safetyTips.filter { it.category == state.selectedSafetyCategory }
        }

        if (state.safetySearchQuery.isBlank()) {
            return categoryFiltered
        }

        val q = state.safetySearchQuery.trim().lowercase()
        return categoryFiltered.filter { tip ->
            tip.titleMm.lowercase().contains(q) ||
            tip.titleTh.lowercase().contains(q) ||
            tip.titleEn.lowercase().contains(q) ||
            tip.summaryMm.lowercase().contains(q) ||
            tip.summaryTh.lowercase().contains(q) ||
            tip.detailMm.lowercase().contains(q) ||
            tip.relevantHotline.contains(q) ||
            tip.dosMm.any { it.lowercase().contains(q) } ||
            tip.dontsMm.any { it.lowercase().contains(q) }
        }
    }

    fun detectCurrentLocationWithService(onComplete: ((Boolean) -> Unit)? = null) {
        if (!locationService.hasLocationPermission()) {
            _uiState.update { it.copy(hasGpsPermission = false) }
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGpsDetecting = true) }
            val location = locationService.getCurrentLocation(highAccuracy = true)
                ?: locationService.getLastKnownLocation()

            if (location != null) {
                updateLocation(location.latitude, location.longitude, isGranted = true)
                onComplete?.invoke(true)
            } else {
                _uiState.update { it.copy(hasGpsPermission = true) }
                onComplete?.invoke(false)
            }
            _uiState.update { it.copy(isGpsDetecting = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
