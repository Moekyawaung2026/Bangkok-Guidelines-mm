package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.BangkokStaticData
import com.example.data.local.AppDatabase
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
}

