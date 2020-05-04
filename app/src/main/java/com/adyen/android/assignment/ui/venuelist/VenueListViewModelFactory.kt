package com.adyen.android.assignment.ui.venuelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.data.impl.VenueRepository

class VenueListViewModelFactory(private val placesService: PlacesService) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VenueListViewModel::class.java)) {
            return VenueListViewModel(VenueRepository(placesService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
