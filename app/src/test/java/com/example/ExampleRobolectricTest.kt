package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.BangkokStaticData
import com.example.data.local.AppDatabase
import com.example.data.local.toDomain
import com.example.data.local.toEntity
import com.example.data.model.TransportType
import com.example.data.repository.TransportRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BangkokTransportAppTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: TransportRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TransportRepository(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testAppNameStringResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bangkok Go MM", appName)
    }

    @Test
    fun testStaticDataIntegrity() {
        val places = repository.getAllPlaces()
        assertTrue("Places list should not be empty", places.isNotEmpty())

        val bts = places.filter { it.type == TransportType.BTS }
        assertTrue("BTS stations should exist", bts.isNotEmpty())

        val busRoutes = repository.getBusRoutes()
        assertTrue("Bus routes should not be empty", busRoutes.isNotEmpty())

        val emergencyContacts = repository.getEmergencyContacts()
        assertTrue("Emergency contacts must include police 191", emergencyContacts.any { it.number == "191" })
        assertTrue("Emergency contacts must include 1584", emergencyContacts.any { it.number == "1584" })

        val phrasebook = repository.getPhrasebook()
        assertTrue("Phrasebook should contain meter taxi phrase", phrasebook.any { it.thaiText.contains("เปิดมิเตอร์") })
    }

    @Test
    fun testHaversineDistanceCalculation() {
        // Distance between Victory Monument (13.7628, 100.5372) and Siam (13.7456, 100.5342)
        val distance = repository.calculateDistanceMeters(13.7628, 100.5372, 13.7456, 100.5342)
        // Approximate distance ~1.9 - 2.1 km
        assertTrue("Distance should be around 2km", distance in 1500.0..2500.0)
    }

    @Test
    fun testSearchFunctionality() {
        val resultsMm = repository.searchPlaces("အောင်ဆန်း")
        assertTrue("Search by Burmese should return Victory Monument", resultsMm.isNotEmpty())

        val resultsTh = repository.searchPlaces("สยาม")
        assertTrue("Search by Thai should return Siam", resultsTh.isNotEmpty())
    }

    @Test
    fun testMultimodalRoutePlanner10Modes() {
        val routes = repository.planRoutes("အောင်ဆန်းရတနာ", "ဆယမ်", 0)
        assertTrue("Route planner should generate 10 multimodal modes", routes.size >= 10)

        val cheapest = routes.find { it.id == "route_cheapest" }
        assertNotNull(cheapest)
        assertTrue("Cheapest route should have low fare", cheapest!!.fareMinThb <= 20)

        val fastest = routes.find { it.id == "route_fastest" }
        assertNotNull(fastest)
        assertTrue("Fastest route should have swift travel time", fastest!!.totalTimeMinutes <= 35)
    }

    @Test
    fun testSavedPlaceDatabaseFlow() = runBlocking {
        val place = BangkokStaticData.transportPlaces.first()
        repository.savePlace(place)

        val isSaved = repository.isPlaceSaved(place.id)
        assertTrue("Place should be saved in Room DB", isSaved)

        repository.removeSavedPlace(place.id)
        val isStillSaved = repository.isPlaceSaved(place.id)
        assertFalse("Place should be deleted from Room DB", isStillSaved)
    }

    @Test
    fun testChatViewModelOfflineSafeGuidance() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val chatViewModel = com.example.ui.viewmodel.ChatViewModel(app)

        // Initial welcome message should contain uncertainty warning
        val initialMessages = chatViewModel.uiState.value.messages
        assertTrue("Chat should have initial welcome message", initialMessages.isNotEmpty())
        assertTrue("Welcome message should mention uncertainty", initialMessages.first().text.contains("ခန့်မှန်းခြေ"))

        // Send query
        chatViewModel.sendMessage("လက်ရှိနေရာကနေ Samut Prakan စျေးကို ဘယ်လိုသွားရမလဲ?")
        // Wait or let coroutine settle
        kotlinx.coroutines.delay(200)

        val updatedMessages = chatViewModel.uiState.value.messages
        assertTrue("Message history should include user query and AI response", updatedMessages.size >= 2)

        val lastMessage = updatedMessages.last()
        assertFalse("Last message should be from assistant", lastMessage.isUser)
        assertTrue("Should include Pak Nam / Samut Prakan transit info", lastMessage.text.contains("Pak Nam") || lastMessage.text.contains("စမွတ်ပရာကန်"))
        assertNotNull("Should provide Thai sentence for driver", lastMessage.thaiPhraseForDriver)
        assertTrue("Must be uncertainty-aware", lastMessage.uncertaintyLabel.isNotBlank())
    }

    @Test
    fun testChatViewModelBilingualToggle() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val chatViewModel = com.example.ui.viewmodel.ChatViewModel(app)

        assertTrue("Default language should be Burmese", chatViewModel.uiState.value.isBurmese)
        chatViewModel.toggleLanguage()
        assertFalse("Language should toggle to Thai", chatViewModel.uiState.value.isBurmese)
        chatViewModel.toggleLanguage()
        assertTrue("Language should toggle back to Burmese", chatViewModel.uiState.value.isBurmese)
    }

    @Test
    fun testChatViewModelStateManagementAndSafetyEnforcement() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val chatViewModel = com.example.ui.viewmodel.ChatViewModel(app)

        // Verify initial state is ChatState.Success
        val initialUiState = chatViewModel.uiState.value
        assertTrue("Initial state should be Success with welcome message", initialUiState.state is com.example.ui.viewmodel.ChatState.Success)
        assertFalse("Loading should be false initially", initialUiState.isLoading)
        assertNull("Error message should be null initially", initialUiState.errorMessage)

        // Send a query about Mahachai market
        chatViewModel.sendMessage("မဟာချိုင် ပင်လယ်စာစျေးကို အသက်သာဆုံး ဘယ်လိုသွားရမလဲ?")
        kotlinx.coroutines.delay(200)

        val updatedState = chatViewModel.uiState.value
        assertTrue("State should return to Success after response generation", updatedState.state is com.example.ui.viewmodel.ChatState.Success)
        assertFalse("isLoading should be false", updatedState.isLoading)
        val lastMsg = updatedState.messages.last()
        assertFalse("Last message should be assistant", lastMsg.isUser)

        // Enforce safety constraints
        assertTrue("Must include safety warning for passport, meter or helmet",
            lastMsg.text.contains("ဦးထုပ်") || lastMsg.text.contains("မီတာ") || lastMsg.text.contains("လုံခြုံရေး") || lastMsg.safetyWarning != null
        )

        // Enforce uncertainty constraints
        assertTrue("Must include uncertainty labeling",
            lastMsg.uncertaintyLabel.contains("ခန့်မှန်း") || lastMsg.text.contains("ခန့်မှန်း")
        )

        // Test Error state handling
        chatViewModel.simulateError("API Rate Limit Reached")
        val errorState = chatViewModel.uiState.value
        assertTrue("State should transition to Error", errorState.state is com.example.ui.viewmodel.ChatState.Error)
        assertEquals("API Rate Limit Reached", errorState.errorMessage)

        // Clear error
        chatViewModel.clearError()
        assertNull("ErrorMessage should be cleared", chatViewModel.uiState.value.errorMessage)
    }

    @Test
    fun testRoutePlannerLocalizationPlaceholders() {
        // Verify Burmese, Thai, and English localized strings
        val myStart = com.example.ui.screens.RoutePlannerLocalization.startPointLabel.get(com.example.ui.screens.PlannerLanguage.BURMESE)
        val thStart = com.example.ui.screens.RoutePlannerLocalization.startPointLabel.get(com.example.ui.screens.PlannerLanguage.THAI)
        val enStart = com.example.ui.screens.RoutePlannerLocalization.startPointLabel.get(com.example.ui.screens.PlannerLanguage.ENGLISH)
        assertEquals("စတင်မည့်နေရာ", myStart)
        assertEquals("จุดเริ่มต้น", thStart)
        assertEquals("Start Point", enStart)

        val myDest = com.example.ui.screens.RoutePlannerLocalization.destinationLabel.get(com.example.ui.screens.PlannerLanguage.BURMESE)
        val thDest = com.example.ui.screens.RoutePlannerLocalization.destinationLabel.get(com.example.ui.screens.PlannerLanguage.THAI)
        val enDest = com.example.ui.screens.RoutePlannerLocalization.destinationLabel.get(com.example.ui.screens.PlannerLanguage.ENGLISH)
        assertEquals("သွားလိုသောနေရာ", myDest)
        assertEquals("จุดหมายปลายทาง", thDest)
        assertEquals("Destination", enDest)

        val mySwitch = com.example.ui.screens.RoutePlannerLocalization.switchDirections.get(com.example.ui.screens.PlannerLanguage.BURMESE)
        val thSwitch = com.example.ui.screens.RoutePlannerLocalization.switchDirections.get(com.example.ui.screens.PlannerLanguage.THAI)
        val enSwitch = com.example.ui.screens.RoutePlannerLocalization.switchDirections.get(com.example.ui.screens.PlannerLanguage.ENGLISH)
        assertEquals("လမ်းကြောင်း ပြောင်းပြန်လှန်မည်", mySwitch)
        assertEquals("สลับทิศทาง", thSwitch)
        assertEquals("Switch Directions", enSwitch)
    }

    @Test
    fun testTransportModeDropdownOptions() {
        val modes = com.example.ui.screens.transportModeOptions
        assertTrue("Transport modes should include 10 options", modes.size >= 10)

        val cheapest = modes.find { it.id == 0 }
        assertNotNull(cheapest)
        assertEquals("အသက်သာဆုံး", cheapest!!.name.get(com.example.ui.screens.PlannerLanguage.BURMESE))
        assertEquals("ประหยัดที่สุด", cheapest.name.get(com.example.ui.screens.PlannerLanguage.THAI))
        assertEquals("Cheapest", cheapest.name.get(com.example.ui.screens.PlannerLanguage.ENGLISH))

        val fastest = modes.find { it.id == 1 }
        assertNotNull(fastest)
        assertEquals("အမြန်ဆုံး", fastest!!.name.get(com.example.ui.screens.PlannerLanguage.BURMESE))
        assertEquals("เร็วที่สุด", fastest.name.get(com.example.ui.screens.PlannerLanguage.THAI))
        assertEquals("Fastest", fastest.name.get(com.example.ui.screens.PlannerLanguage.ENGLISH))

        val leastWalking = modes.find { it.id == 2 }
        assertNotNull(leastWalking)
        assertEquals("လမ်းလျှောက်နည်း", leastWalking!!.name.get(com.example.ui.screens.PlannerLanguage.BURMESE))
        assertEquals("เดินน้อยที่สุด", leastWalking.name.get(com.example.ui.screens.PlannerLanguage.THAI))
        assertEquals("Least Walking", leastWalking.name.get(com.example.ui.screens.PlannerLanguage.ENGLISH))
    }

    @Test
    fun testSwapPlannerEndpoints() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        viewModel.setPlannerFrom("Point A")
        viewModel.setPlannerTo("Point B")
        assertEquals("Point A", viewModel.uiState.value.plannerFrom)
        assertEquals("Point B", viewModel.uiState.value.plannerTo)

        viewModel.swapPlannerEndpoints()
        assertEquals("Point B", viewModel.uiState.value.plannerFrom)
        assertEquals("Point A", viewModel.uiState.value.plannerTo)
    }

    @Test
    fun testLocationServiceCalculationsAndNearbyFiltering() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationService = com.example.data.location.LocationService(context)

        // Victory Monument to Siam Paragon ~ 3.2 km
        val victoryLat = 13.7628
        val victoryLng = 100.5372
        val siamLat = 13.7460
        val siamLng = 100.5347

        val distance = locationService.calculateDistanceMeters(victoryLat, victoryLng, siamLat, siamLng)
        assertTrue("Distance should be approximately 1.5 - 2.5 km", distance in 1500.0..3000.0)

        val formattedDist = locationService.formatDistance(distance)
        assertTrue("Formatted distance should contain 'km'", formattedDist.contains("km"))

        // Radius check: Siam is within 3000m of Victory Monument, but not within 500m
        assertTrue(locationService.isWithinRadius(victoryLat, victoryLng, siamLat, siamLng, 3000.0))
        assertFalse(locationService.isWithinRadius(victoryLat, victoryLng, siamLat, siamLng, 500.0))

        // Test filtering nearby places
        val places = repository.getAllPlaces()
        val nearby3km = locationService.filterNearbyPlaces(places, victoryLat, victoryLng, 3000.0)
        assertTrue("Nearby 3km places should not be empty", nearby3km.isNotEmpty())

        // First item should be the closest place to Victory Monument
        val closest = nearby3km.first()
        assertTrue("Closest place should be within 1000m of Victory Monument", closest.second < 1000.0)

        // All filtered places must be <= 3000m
        assertTrue("All returned places must be within defined radius", nearby3km.all { it.second <= 3000.0 })

        // Check sorted by distance ascending
        for (i in 0 until nearby3km.size - 1) {
            assertTrue("Places must be sorted ascending by distance", nearby3km[i].second <= nearby3km[i+1].second)
        }
    }

    @Test
    fun testStaticSafetyTipsGuideInLocalResources() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val loader = com.example.data.datasource.SafetyGuideResourceLoader(context)
        val guide = loader.loadSafetyGuide()

        // Verify version and non-empty contents
        assertTrue("Safety guide version should be >= 1", guide.version >= 1)
        assertTrue("Emergency contacts must not be empty", guide.emergencyContacts.isNotEmpty())
        assertTrue("Safety tips list must not be empty", guide.safetyTips.isNotEmpty())

        // Verify key emergency hotlines for Myanmar migrant workers
        val contacts = guide.emergencyContacts
        assertTrue("Police 191 must exist", contacts.any { it.number == "191" })
        assertTrue("Tourist Police 1155 must exist", contacts.any { it.number == "1155" })
        assertTrue("Medical Ambulance 1669 must exist", contacts.any { it.number == "1669" })
        assertTrue("Transport Complaint 1584 must exist", contacts.any { it.number == "1584" })
        assertTrue("Labour Hotline 1506 must exist", contacts.any { it.number == "1506" })
        assertTrue("Myanmar Embassy or Labour Attache must exist", contacts.any { it.id.contains("embassy") || it.id.contains("labour") })

        // Verify key safety tip topics
        val tips = guide.safetyTips
        val docTip = tips.find { it.category == com.example.data.model.SafetyCategory.DOCUMENTS }
        assertNotNull("Document safety tip must exist", docTip)
        assertTrue("Document tip must advise keeping copy/photo", docTip!!.dosMm.isNotEmpty())

        val taxiTip = tips.find { it.id == "tip_taxi_meter" }
        assertNotNull("Taxi meter safety tip must exist", taxiTip)
        assertEquals("1584", taxiTip!!.relevantHotline)

        val helmetTip = tips.find { it.id == "tip_moto_helmet" }
        assertNotNull("Motorcycle helmet safety tip must exist", helmetTip)
        assertTrue("Helmet tip should mention helmet requirement", helmetTip!!.summaryMm.contains("ဦးထုပ်") || helmetTip.titleMm.contains("ဦးထုပ်"))

        // Verify localized string resources
        val guideTitle = context.getString(R.string.safety_guide_title)
        assertFalse("Safety guide title string resource should be resolved", guideTitle.isBlank())
    }

    @Test
    fun testSafetyCategoryFilterAndSearch() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        val allTips = viewModel.getFilteredSafetyTips()
        assertTrue("All tips should not be empty", allTips.isNotEmpty())

        // Filter by DOCUMENTS category
        viewModel.selectSafetyCategory(com.example.data.model.SafetyCategory.DOCUMENTS)
        val docTips = viewModel.getFilteredSafetyTips()
        assertTrue("Documents tips should exist", docTips.isNotEmpty())
        assertTrue("All filtered tips should belong to DOCUMENTS category",
            docTips.all { it.category == com.example.data.model.SafetyCategory.DOCUMENTS })

        // Search for specific keyword "1584"
        viewModel.selectSafetyCategory(com.example.data.model.SafetyCategory.ALL)
        viewModel.setSafetySearchQuery("1584")
        val searchResults = viewModel.getFilteredSafetyTips()
        assertTrue("Search for '1584' should match taxi tip", searchResults.isNotEmpty())
        assertTrue("Matched tip must contain 1584", searchResults.any { it.relevantHotline == "1584" })
    }

    @Test
    fun testMainDashboardCategorizedNavigationServices() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        // Verify entry point initial state
        assertEquals("Initial tab should be HOME", com.example.ui.viewmodel.AppTab.HOME, viewModel.uiState.value.activeTab)

        // Verify TransportCategoryFilter enum
        val filters = com.example.ui.screens.TransportCategoryFilter.values()
        assertTrue("Category filters should include ALL, TRAIN, BUS, BOAT, ROAD",
            filters.size >= 5)
        assertTrue(filters.any { it == com.example.ui.screens.TransportCategoryFilter.TRAIN })
        assertTrue(filters.any { it == com.example.ui.screens.TransportCategoryFilter.BUS })
        assertTrue(filters.any { it == com.example.ui.screens.TransportCategoryFilter.BOAT })
        assertTrue(filters.any { it == com.example.ui.screens.TransportCategoryFilter.ROAD })

        // Verify that BTS places exist for BTS navigation card
        val allPlaces = viewModel.getAllPlaces()
        val btsStations = allPlaces.filter { it.type == com.example.data.model.TransportType.BTS }
        assertTrue("BTS stations should be available on dashboard", btsStations.isNotEmpty())

        // Verify that MRT places exist for MRT navigation card
        val mrtStations = allPlaces.filter { it.type == com.example.data.model.TransportType.MRT }
        assertTrue("MRT stations should be available on dashboard", mrtStations.isNotEmpty())

        // Verify that Bus routes exist for Bus navigation card
        val busRoutes = viewModel.getBusRoutes()
        assertTrue("Bus routes should be available for bus navigation card", busRoutes.isNotEmpty())

        // Verify tab switching from Dashboard
        viewModel.setTab(com.example.ui.viewmodel.AppTab.LINES)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)

        viewModel.setTab(com.example.ui.viewmodel.AppTab.BUS)
        assertEquals(com.example.ui.viewmodel.AppTab.BUS, viewModel.uiState.value.activeTab)

        viewModel.setTab(com.example.ui.viewmodel.AppTab.TAXI)
        assertEquals(com.example.ui.viewmodel.AppTab.TAXI, viewModel.uiState.value.activeTab)

        viewModel.setTab(com.example.ui.viewmodel.AppTab.HOME)
        assertEquals(com.example.ui.viewmodel.AppTab.HOME, viewModel.uiState.value.activeTab)
    }

    @Test
    fun testDarkModeToggleState() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        // Verify initial state defaults to Light Mode (or system default)
        val initialMode = viewModel.uiState.value.isDarkMode

        // Toggle Dark Mode
        viewModel.toggleDarkMode()
        assertEquals(!initialMode, viewModel.uiState.value.isDarkMode)

        // Toggle back
        viewModel.toggleDarkMode()
        assertEquals(initialMode, viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun testColorSchemeAndTypographyHighContrastAccessibility() {
        // Verify Dark & Light Color Schemes
        val darkScheme = com.example.ui.theme.HighContrastDarkColorScheme
        val lightScheme = com.example.ui.theme.HighContrastLightColorScheme

        assertNotNull(darkScheme)
        assertNotNull(lightScheme)

        // Verify distinct high-contrast surface/background levels
        assertNotEquals(darkScheme.background, lightScheme.background)
        assertNotEquals(darkScheme.surface, lightScheme.surface)
        assertNotEquals(darkScheme.onBackground, lightScheme.onBackground)

        // Verify dark mode high-contrast text is bright / near white
        val darkOnBg = darkScheme.onBackground
        assertTrue("Dark mode onBackground must be bright for readability", darkOnBg.red > 0.8f && darkOnBg.green > 0.8f && darkOnBg.blue > 0.8f)

        // Verify light mode high-contrast text is dark / near black
        val lightOnBg = lightScheme.onBackground
        assertTrue("Light mode onBackground must be dark for readability", lightOnBg.red < 0.2f && lightOnBg.green < 0.2f && lightOnBg.blue < 0.2f)

        // Verify Typography configuration scale and line height accessibility (diacritic clipping prevention)
        val type = com.example.ui.theme.Typography
        assertTrue("DisplayLarge should be larger than HeadlineLarge", type.displayLarge.fontSize.value > type.headlineLarge.fontSize.value)
        assertTrue("HeadlineLarge should be larger than BodyLarge", type.headlineLarge.fontSize.value > type.bodyLarge.fontSize.value)
        assertTrue("BodyLarge should be larger than BodySmall", type.bodyLarge.fontSize.value > type.bodySmall.fontSize.value)

        // Multilingual diacritic protection: line heights must exceed font size
        assertTrue("BodyLarge line height must exceed font size", type.bodyLarge.lineHeight.value > type.bodyLarge.fontSize.value)
        assertTrue("BodyMedium line height must exceed font size", type.bodyMedium.lineHeight.value > type.bodyMedium.fontSize.value)
        assertTrue("HeadlineMedium line height must exceed font size", type.headlineMedium.lineHeight.value > type.headlineMedium.fontSize.value)
    }

    @Test
    fun testHomeScreenTransitGridLayoutQuickAccessCards() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        // Verify initial state
        assertEquals(com.example.data.model.TransportType.BTS, viewModel.uiState.value.selectedTransitLineType)

        // Test BTS selection from quick-access grid
        viewModel.selectTransitLineType(com.example.data.model.TransportType.BTS)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.BTS, viewModel.uiState.value.selectedTransitLineType)

        // Test MRT selection from quick-access grid
        viewModel.selectTransitLineType(com.example.data.model.TransportType.MRT)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.MRT, viewModel.uiState.value.selectedTransitLineType)

        // Test SRT selection from quick-access grid
        viewModel.selectTransitLineType(com.example.data.model.TransportType.SRT)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.SRT, viewModel.uiState.value.selectedTransitLineType)

        // Test Bus selection from quick-access grid
        viewModel.setTab(com.example.ui.viewmodel.AppTab.BUS)
        assertEquals(com.example.ui.viewmodel.AppTab.BUS, viewModel.uiState.value.activeTab)

        // Verify that SRT places exist in the database for SRT quick access
        val srtPlaces = viewModel.getAllPlaces().filter { it.type == com.example.data.model.TransportType.SRT }
        assertTrue("SRT places should exist in repository for quick access navigation", srtPlaces.isNotEmpty())
    }

    @Test
    fun testGlobalLanguageStateManagementCompositionLocal() {
        val mmLanguage = com.example.ui.theme.AppLanguage.BURMESE
        val thLanguage = com.example.ui.theme.AppLanguage.THAI

        // Verify enum codes and localization properties
        assertEquals("my", mmLanguage.code)
        assertEquals("th", thLanguage.code)
        assertTrue("BURMESE should have isBurmese = true", mmLanguage.isBurmese)
        assertFalse("BURMESE should have isThai = false", mmLanguage.isThai)
        assertTrue("THAI should have isThai = true", thLanguage.isThai)
        assertFalse("THAI should have isBurmese = false", thLanguage.isBurmese)

        // Verify LanguageContext localization resolver helper
        var toggled = false
        val mmContext = com.example.ui.theme.LanguageContext(
            language = mmLanguage,
            onToggleLanguage = { toggled = true }
        )
        assertEquals("မင်္ဂလာပါ", mmContext.localized("မင်္ဂလာပါ", "สวัสดี"))

        val thContext = com.example.ui.theme.LanguageContext(
            language = thLanguage,
            onToggleLanguage = { toggled = true }
        )
        assertEquals("สวัสดี", thContext.localized("မင်္ဂလာပါ", "สวัสดี"))

        // Test toggle callback invocation
        mmContext.onToggleLanguage()
        assertTrue("Language toggle callback should execute", toggled)
    }

    @Test
    fun testLanguageToggleStateTransitions() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        // Default initial language is Burmese
        assertTrue("Initial state must be Burmese", viewModel.uiState.value.isBurmese)

        // Toggle to Thai
        viewModel.toggleLanguage()
        assertFalse("State after toggle must be Thai (isBurmese = false)", viewModel.uiState.value.isBurmese)

        // Toggle back to Burmese
        viewModel.toggleLanguage()
        assertTrue("State after second toggle must return to Burmese", viewModel.uiState.value.isBurmese)
    }

    @Test
    fun testTransportPlaceEntityAndPlaceDaoOfflineCaching() = runBlocking {
        val placeDao = database.placeDao()
        assertNotNull("PlaceDao should be accessible from database", placeDao)

        // Clear any leftover data
        placeDao.clearAllPlaces()
        assertEquals(0, placeDao.getPlacesCount())

        // Create sample domain places
        val samplePlace1 = com.example.data.model.TransportPlace(
            id = "test_bts_siam",
            type = com.example.data.model.TransportType.BTS,
            nameMm = "ဆယမ် (Siam)",
            nameTh = "สยาม",
            nameEn = "Siam",
            romanized = "Siam",
            latitude = 13.7456,
            longitude = 100.5342,
            addressMm = "ဆယမ်စကွဲယား အနီး",
            addressTh = "สยามสแควร์"
        )
        val samplePlace2 = com.example.data.model.TransportPlace(
            id = "test_mrt_sukhumvit",
            type = com.example.data.model.TransportType.MRT,
            nameMm = "ဆူခုမ်ဝစ် MRT",
            nameTh = "สุขุมวิท",
            nameEn = "Sukhumvit",
            romanized = "Sukhumvit",
            latitude = 13.7371,
            longitude = 100.5604,
            addressMm = "အာဆုတ် BTS နှင့် လဲလှယ်ရာ",
            addressTh = "เชื่อมต่อ BTS อโศก"
        )

        // Test toEntity and insert
        val entity1 = samplePlace1.toEntity()
        val entity2 = samplePlace2.toEntity()

        placeDao.insertPlaces(listOf(entity1, entity2))
        assertEquals(2, placeDao.getPlacesCount())

        // Test getPlaceById
        val retrieved = placeDao.getPlaceById("test_bts_siam")
        assertNotNull(retrieved)
        assertEquals("test_bts_siam", retrieved?.id)
        assertEquals("BTS", retrieved?.type)
        assertEquals("ဆယမ် (Siam)", retrieved?.nameMm)

        // Test toDomain mapper
        val domainModel = retrieved!!.toDomain()
        assertEquals(com.example.data.model.TransportType.BTS, domainModel.type)
        assertEquals(13.7456, domainModel.latitude, 0.0001)
        assertEquals(100.5342, domainModel.longitude, 0.0001)

        // Delete test
        placeDao.deletePlaceById("test_bts_siam")
        assertEquals(1, placeDao.getPlacesCount())
        assertNull(placeDao.getPlaceById("test_bts_siam"))

        // Clear all
        placeDao.clearAllPlaces()
        assertEquals(0, placeDao.getPlacesCount())
    }

    @Test
    fun testPlaceDaoCoordinateRangeQuerying() = runBlocking {
        val placeDao = database.placeDao()
        placeDao.clearAllPlaces()

        // Victory Monument coordinates: ~13.7628, 100.5372
        val nearPlaceBts = com.example.data.local.TransportPlaceEntity(
            id = "p_vic_mon_bts",
            type = "BTS",
            nameMm = "အောင်ဆန်းရတနာ BTS",
            nameTh = "อนุสาวรีย์ชัยสมรภูมิ",
            nameEn = "Victory Monument BTS",
            latitude = 13.7628,
            longitude = 100.5372
        )
        val nearPlaceBus = com.example.data.local.TransportPlaceEntity(
            id = "p_vic_mon_bus",
            type = "BUS",
            nameMm = "အောင်ဆန်းရတနာ ကျွန်း ၄ ကျွန်း ကားမှတ်တိုင်",
            nameTh = "เกาะพญาไท อนุสาวรีย์ชัยฯ",
            nameEn = "Victory Monument Bus Island",
            latitude = 13.7635,
            longitude = 100.5380
        )
        // Far away place: Mahachai (~35km southwest: lat 13.5475, lng 100.2744)
        val farPlaceMahachai = com.example.data.local.TransportPlaceEntity(
            id = "p_mahachai_market",
            type = "MARKET",
            nameMm = "မဟာချိုင် ပင်လယ်စာစျေးကြီး",
            nameTh = "ตลาดมหาชัย",
            nameEn = "Mahachai Seafood Market",
            latitude = 13.5475,
            longitude = 100.2744
        )

        placeDao.insertPlaces(listOf(nearPlaceBts, nearPlaceBus, farPlaceMahachai))
        assertEquals(3, placeDao.getPlacesCount())

        // Query coordinate range bounding Victory Monument (~2km box: 13.75 to 13.78, 100.52 to 100.55)
        val resultsInBox = placeDao.getPlacesInCoordinateRangeList(
            minLat = 13.75,
            maxLat = 13.78,
            minLng = 100.52,
            maxLng = 100.55
        )

        // Should return the 2 nearby Victory Monument places, excluding Mahachai
        assertEquals(2, resultsInBox.size)
        assertTrue(resultsInBox.any { it.id == "p_vic_mon_bts" })
        assertTrue(resultsInBox.any { it.id == "p_vic_mon_bus" })
        assertFalse(resultsInBox.any { it.id == "p_mahachai_market" })

        // Test coordinate range query with type filter (BTS only)
        val btsOnlyInBox = placeDao.getPlacesByTypeInCoordinateRangeList(
            type = "BTS",
            minLat = 13.75,
            maxLat = 13.78,
            minLng = 100.52,
            maxLng = 100.55
        )
        assertEquals(1, btsOnlyInBox.size)
        assertEquals("p_vic_mon_bts", btsOnlyInBox[0].id)

        // Clean up
        placeDao.clearAllPlaces()
    }

    @Test
    fun testAppThemeAndCustomColorConstants() {
        // Verify custom color constants specified in project guidelines
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0B5ED7), com.example.ui.theme.PrimaryBlue)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFF4B400), com.example.ui.theme.BurmeseGold)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFD62828), com.example.ui.theme.ThaiRed)

        // Verify AppColors holder object
        assertEquals(com.example.ui.theme.PrimaryBlue, com.example.ui.theme.AppColors.PrimaryBlue)
        assertEquals(com.example.ui.theme.BurmeseGold, com.example.ui.theme.AppColors.BurmeseGold)
        assertEquals(com.example.ui.theme.ThaiRed, com.example.ui.theme.AppColors.ThaiRed)

        // Verify BangkokBlue alias consistency
        assertEquals(com.example.ui.theme.PrimaryBlue, com.example.ui.theme.BangkokBlue)

        // Verify Light and Dark color schemes for Material 3 AppTheme
        val darkScheme = com.example.ui.theme.HighContrastDarkColorScheme
        val lightScheme = com.example.ui.theme.HighContrastLightColorScheme

        assertNotNull(darkScheme)
        assertNotNull(lightScheme)

        // Verify distinct light/dark background and surface
        assertNotEquals(darkScheme.background, lightScheme.background)
        assertNotEquals(darkScheme.surface, lightScheme.surface)
    }

    @Test
    fun testVoiceSearchFloatingActionButtonAndSearchFlow() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)

        // Initial search query should be empty
        assertEquals("", viewModel.uiState.value.searchQuery)

        // Simulate voice-to-text recognition result updating the search query
        val voiceInputMyanmar = "ဆယမ်"
        viewModel.onSearchQueryChanged(voiceInputMyanmar)
        assertEquals(voiceInputMyanmar, viewModel.uiState.value.searchQuery)

        // Verify that places matching the recognized query can be retrieved
        val filtered = viewModel.getAllPlaces().filter {
            it.nameMm.contains(voiceInputMyanmar, ignoreCase = true) ||
            it.nameTh.contains(voiceInputMyanmar, ignoreCase = true) ||
            it.nameEn.contains(voiceInputMyanmar, ignoreCase = true)
        }
        assertTrue("Voice search should find matching stations like Siam", filtered.isNotEmpty())

        // Clear query
        viewModel.onSearchQueryChanged("")
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun testTransportationTypeDataClassAndRepository() {
        val repository = com.example.data.repository.DefaultTransportationTypeRepository()
        val types = repository.getAllTransportationTypes()

        // Verify exactly the 5 requested transportation types
        assertEquals(5, types.size)
        val ids = types.map { it.id }
        assertTrue(ids.contains("BTS"))
        assertTrue(ids.contains("MRT"))
        assertTrue(ids.contains("BUS"))
        assertTrue(ids.contains("SRT"))
        assertTrue(ids.contains("TAXI"))

        // Verify all fields are properly populated for BTS
        val bts = repository.getTransportationTypeById("BTS")
        assertNotNull(bts)
        assertEquals(com.example.data.model.TransportType.BTS, bts?.type)
        assertEquals("BTS မိုးပျံရထား", bts?.nameMm)
        assertEquals("รถไฟฟ้าบีทีเอส (BTS)", bts?.nameTh)
        assertEquals("BTS Skytrain", bts?.nameEn)
        assertEquals("06:00 - 24:00", bts?.operatingHours)
        assertTrue(bts?.fareRangeMm?.contains("၁၇") == true)
        assertTrue(bts?.highlightFeaturesMm?.isNotEmpty() == true)

        // Verify MRT by type lookup
        val mrt = repository.getTransportationTypeByType(com.example.data.model.TransportType.MRT)
        assertNotNull(mrt)
        assertEquals("MRT", mrt?.id)
        assertTrue(mrt?.nameMm?.contains("MRT") == true)

        // Verify Bus by case-insensitive id
        val bus = repository.getTransportationTypeById("bus")
        assertNotNull(bus)
        assertEquals(com.example.data.model.TransportType.BUS, bus?.type)
        assertTrue(bus?.fareRangeMm?.contains("၈") == true)

        // Verify SRT by id
        val srt = repository.getTransportationTypeById("SRT")
        assertNotNull(srt)
        assertEquals(com.example.data.model.TransportType.SRT, srt?.type)
        assertTrue(srt?.descriptionMm?.contains("မဟာချိုင်") == true)

        // Verify Taxi by id
        val taxi = repository.getTransportationTypeById("TAXI")
        assertNotNull(taxi)
        assertEquals(com.example.data.model.TransportType.TAXI, taxi?.type)
        assertTrue(taxi?.fareRangeMm?.contains("၃၅") == true)
    }

    @Test
    fun testTransportationTypeNavigationRoutingInViewModel() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.viewmodel.BangkokTransportViewModel(app)
        val repository = viewModel.transportationTypeRepository

        // Verify ViewModel loads transportation types into state
        val stateTypes = viewModel.uiState.value.transportationTypes
        assertEquals(5, stateTypes.size)

        // 1. Select BTS: Should navigate to LINES tab with BTS selected
        val bts = repository.getTransportationTypeById("BTS")!!
        viewModel.selectTransportationType(bts)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.BTS, viewModel.uiState.value.selectedTransitLineType)

        // 2. Select MRT: Should navigate to LINES tab with MRT selected
        val mrt = repository.getTransportationTypeById("MRT")!!
        viewModel.selectTransportationType(mrt)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.MRT, viewModel.uiState.value.selectedTransitLineType)

        // 3. Select SRT: Should navigate to LINES tab with SRT selected
        val srt = repository.getTransportationTypeById("SRT")!!
        viewModel.selectTransportationType(srt)
        assertEquals(com.example.ui.viewmodel.AppTab.LINES, viewModel.uiState.value.activeTab)
        assertEquals(com.example.data.model.TransportType.SRT, viewModel.uiState.value.selectedTransitLineType)

        // 4. Select Bus: Should navigate to BUS tab
        val bus = repository.getTransportationTypeById("BUS")!!
        viewModel.selectTransportationType(bus)
        assertEquals(com.example.ui.viewmodel.AppTab.BUS, viewModel.uiState.value.activeTab)

        // 5. Select Taxi: Should navigate to TAXI tab
        val taxi = repository.getTransportationTypeById("TAXI")!!
        viewModel.selectTransportationType(taxi)
        assertEquals(com.example.ui.viewmodel.AppTab.TAXI, viewModel.uiState.value.activeTab)
    }
}
