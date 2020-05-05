package com.adyen.android.assignment.ui.venuelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.data.impl.VenueRepository
import com.google.android.gms.location.FusedLocationProviderClient

class VenueListViewModelFactory(
    private val placesService: PlacesService,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VenueListViewModel::class.java)) {
            return VenueListViewModel(
                VenueRepository(placesService),
                fusedLocationProviderClient
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
