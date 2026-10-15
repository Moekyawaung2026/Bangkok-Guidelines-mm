package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.BangkokGoTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BangkokTransportViewModel
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private val viewModel: BangkokTransportViewModel by viewModels()
    private val chatViewModel: com.example.ui.viewmodel.ChatViewModel by viewModels()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            // Location Permission Launcher
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                            if (loc != null) {
                                viewModel.updateLocation(loc.latitude, loc.longitude, true)
                            } else {
                                viewModel.updateLocation(13.7628, 100.5372, true)
                            }
                        }
                    } catch (e: SecurityException) {
                        viewModel.updateLocation(13.7628, 100.5372, false)
                    }
                } else {
                    viewModel.updateLocation(13.7628, 100.5372, false)
                }
            }

            fun requestLocation() {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                            if (loc != null) {
                                viewModel.updateLocation(loc.latitude, loc.longitude, true)
                            } else {
                                viewModel.updateLocation(13.7628, 100.5372, true)
                            }
                        }
                    } catch (e: SecurityException) {
                        viewModel.updateLocation(13.7628, 100.5372, false)
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            fun dialNumber(number: String) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
            }

            // Snackbar listener
            LaunchedEffect(state.snackbarMessage) {
                state.snackbarMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearSnackbar()
                }
            }

            // Android Hardware Back Button Handling
            BackHandler(enabled = state.activeTab != AppTab.HOME) {
                viewModel.setTab(AppTab.HOME)
            }

            BangkokGoTheme(darkTheme = state.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        BangkokTopAppBar(
                            isBurmese = state.isBurmese,
                            isDarkMode = state.isDarkMode,
                            onToggleLanguage = {
                                viewModel.toggleLanguage()
                                chatViewModel.toggleLanguage()
                            },
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onEmergencyClick = { viewModel.setTab(AppTab.SAFETY) },
                            onAiAssistantClick = { viewModel.setTab(AppTab.AI_ASSISTANT) }
                        )
                    },
                    bottomBar = {
                        BangkokBottomNavigationBar(
                            currentTab = state.activeTab,
                            isBurmese = state.isBurmese,
                            onSelectTab = { viewModel.setTab(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.activeTab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    state = state,
                                    places = viewModel.getAllPlaces(),
                                    onNavigateTab = { viewModel.setTab(it) },
                                    onGpsClick = {
                                        requestLocation()
                                        viewModel.setTab(AppTab.NEARBY)
                                    },
                                    onSearchChange = { viewModel.onSearchQueryChanged(it) },
                                    onQuickDestinationSelect = { dest ->
                                        viewModel.setPlannerTo(dest)
                                        viewModel.setTab(AppTab.PLANNER)
                                    },
                                    onPlaceClick = { viewModel.selectPlace(it) },
                                    onDirectionsClick = { place ->
                                        viewModel.setPlannerTo(place.nameMm)
                                        viewModel.setTab(AppTab.PLANNER)
                                    },
                                    onSavePlace = { viewModel.savePlace(it) },
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    },
                                    onAiAssistantClick = { viewModel.setTab(AppTab.AI_ASSISTANT) }
                                )
                            }

                            AppTab.NEARBY -> {
                                NearbyScreen(
                                    state = state,
                                    onRequestLocationPermission = { requestLocation() },
                                    onSelectRadius = { viewModel.setRadius(it) },
                                    onSelectCategory = { viewModel.setCategoryFilter(it) },
                                    onPlaceClick = { viewModel.selectPlace(it) },
                                    onDirectionsClick = { place ->
                                        viewModel.setPlannerTo(place.nameMm)
                                        viewModel.setTab(AppTab.PLANNER)
                                    },
                                    onSavePlace = { viewModel.savePlace(it) },
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    }
                                )
                            }

                            AppTab.PLANNER -> {
                                RoutePlannerScreen(
                                    state = state,
                                    majorHubs = viewModel.getMajorTransitHubs(),
                                    onFromChange = { viewModel.setPlannerFrom(it) },
                                    onToChange = { viewModel.setPlannerTo(it) },
                                    onSwapEndpoints = { viewModel.swapPlannerEndpoints() },
                                    onUseCurrentLocationForFrom = {
                                        requestLocation()
                                        viewModel.setPlannerFrom(if (state.isBurmese) "ကျွန်ုပ်၏ လက်ရှိတည်နေရာ" else "ตำแหน่งปัจจุบันของฉัน")
                                    },
                                    onSelectMode = { viewModel.setRouteMode(it) },
                                    onSaveRoute = { viewModel.saveRoute(it) },
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    },
                                    onSpeakThai = { viewModel.speakThaiText(it) }
                                )
                            }

                            AppTab.BUS -> {
                                BusGuideScreen(
                                    state = state,
                                    busRoutes = viewModel.getBusRoutes(),
                                    onSelectBusNumber = { viewModel.setSelectedBusNumber(it) },
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    },
                                    onSpeakThai = { viewModel.speakThaiText(it) }
                                )
                            }

                            AppTab.LINES -> {
                                TransitLinesScreen(
                                    state = state,
                                    places = viewModel.getAllPlaces(),
                                    onPlaceClick = { viewModel.selectPlace(it) },
                                    onDirectionsClick = { place ->
                                        viewModel.setPlannerTo(place.nameMm)
                                        viewModel.setTab(AppTab.PLANNER)
                                    },
                                    onSavePlace = { viewModel.savePlace(it) },
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    }
                                )
                            }

                            AppTab.TAXI -> {
                                TaxiSafetyScreen(
                                    state = state,
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    },
                                    onSpeakThai = { viewModel.speakThaiText(it) },
                                    onCallEmergency = { dialNumber(it) }
                                )
                            }

                            AppTab.SAFETY -> {
                                SafetyEmergencyScreen(
                                    state = state,
                                    contacts = viewModel.getEmergencyContacts(),
                                    onDialEmergencyNumber = { dialNumber(it) }
                                )
                            }

                            AppTab.PHRASEBOOK -> {
                                PhrasebookScreen(
                                    state = state,
                                    phrasebookItems = viewModel.getPhrasebook(),
                                    onSelectCategory = { viewModel.setSelectedPhraseCategory(it) },
                                    onSpeakThai = { viewModel.speakThaiText(it) },
                                    onShowLargeText = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    }
                                )
                            }

                            AppTab.AI_ASSISTANT -> {
                                AiAssistantScreen(
                                    chatViewModel = chatViewModel,
                                    onShowDriverClick = { thaiText, burmeseMeaning ->
                                        viewModel.showDriverFlashcard(thaiText)
                                    }
                                )
                            }

                            AppTab.SAVED -> {
                                SavedPlacesScreen(
                                    state = state,
                                    onRemoveSavedPlace = { viewModel.removeSavedPlace(it) },
                                    onRemoveSavedRoute = { viewModel.removeSavedRoute(it) },
                                    onGoToPlace = { dest ->
                                        viewModel.setPlannerTo(dest)
                                        viewModel.setTab(AppTab.PLANNER)
                                    }
                                )
                            }

                            AppTab.ADMIN -> {
                                AdminVerificationScreen(
                                    state = state,
                                    places = viewModel.getAllPlaces(),
                                    onSubmitCommunityReport = { place, issue, details ->
                                        viewModel.submitReport(place, issue, details)
                                    }
                                )
                            }
                        }

                        // Driver Flashcard Modal Dialog
                        state.driverFlashcardText?.let { flashText ->
                            DriverFlashcardDialog(
                                thaiText = flashText,
                                burmeseMeaning = "ယာဉ်မောင်းအားပြသရန် ထိုင်းစာသား",
                                onSpeakThai = { viewModel.speakThaiText(flashText) },
                                onDismiss = { viewModel.hideDriverFlashcard() }
                            )
                        }

                        // Selected Place Bottom Sheet
                        state.selectedPlace?.let { place ->
                            PlaceDetailSheet(
                                place = place,
                                isBurmese = state.isBurmese,
                                onDismiss = { viewModel.selectPlace(null) },
                                onDirectionsClick = {
                                    viewModel.setPlannerTo(place.nameMm)
                                    viewModel.selectPlace(null)
                                    viewModel.setTab(AppTab.PLANNER)
                                },
                                onSaveClick = { viewModel.savePlace(place) },
                                onShowDriverClick = { thaiText, burmeseMeaning ->
                                    viewModel.showDriverFlashcard(thaiText)
                                },
                                onSpeakThai = { viewModel.speakThaiText(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
