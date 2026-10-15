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
}
