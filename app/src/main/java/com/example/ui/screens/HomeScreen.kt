package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.TransportPlace
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BangkokUiState

/**
 * HomeScreen acts as the main entry point dashboard for Bangkok Go MM,
 * delegating to the MainDashboardScreen component which features
 * categorized navigation cards for BTS, MRT, Bus, and other transport services.
 */
@Composable
fun HomeScreen(
    state: BangkokUiState,
    places: List<TransportPlace>,
    onNavigateTab: (AppTab) -> Unit,
    onGpsClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onQuickDestinationSelect: (String) -> Unit,
    onPlaceClick: (TransportPlace) -> Unit,
    onDirectionsClick: (TransportPlace) -> Unit,
    onSavePlace: (TransportPlace) -> Unit,
    onShowDriverClick: (String, String) -> Unit,
    onAiAssistantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MainDashboardScreen(
        state = state,
        places = places,
        onNavigateTab = onNavigateTab,
        onGpsClick = onGpsClick,
        onSearchChange = onSearchChange,
        onQuickDestinationSelect = onQuickDestinationSelect,
        onPlaceClick = onPlaceClick,
        onDirectionsClick = onDirectionsClick,
        onSavePlace = onSavePlace,
        onShowDriverClick = onShowDriverClick,
        onAiAssistantClick = onAiAssistantClick,
        modifier = modifier
    )
}
