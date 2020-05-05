package com.adyen.android.assignment.ui.venuelist

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.model.Venue
import com.adyen.android.assignment.data.IVenueRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VenueListViewModel(
    private val venueRepository: IVenueRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {
    sealed class State {
        object Loading : State()
        object Empty : State()
        object Loaded : State()
        class Error(val throwable: Throwable) : State()
        object FineLocationNotGranted : State()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.value = State.Error(throwable)
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _venues: MutableLiveData<List<Venue>> = MutableLiveData()
    val venues: LiveData<List<Venue>> = _venues

    fun loadVenues() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            getVenuesAroundMe(it)
        }
        fusedLocationProviderClient.lastLocation.addOnFailureListener {
            _state.value = State.FineLocationNotGranted
        }
    }

    private fun getVenuesAroundMe(location: Location) = viewModelScope.launch(exceptionHandler) {
        _state.value = State.Loading

        withContext(Dispatchers.IO) {
            val venueList =
                venueRepository.getVenuesByLocation(location.longitude, location.latitude)
            withContext(Dispatchers.Main) {
                if (venueList.isNotEmpty()) {
                    _venues.value = venueList
                    _state.value = State.Loaded
                } else {
                    _state.value = State.Empty
                }
            }
        }
    }
}
