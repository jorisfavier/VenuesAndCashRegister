package com.adyen.android.assignment.ui.venuelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.model.Venue
import com.adyen.android.assignment.data.IVenueRepository
import kotlinx.coroutines.*


class VenueListViewModel(private val venueRepository: IVenueRepository) : ViewModel() {
    sealed class State {
        object Loading : State()
        object Empty : State()
        object Loaded : State()
        object Error : State()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.value = State.Error
    }


    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _venues: MutableLiveData<List<Venue>> = MutableLiveData()
    val venues: LiveData<List<Venue>> = _venues

    fun getVenuesAroundMe() = viewModelScope.launch(exceptionHandler) {
        _state.value = State.Loading
        withContext(Dispatchers.IO) {
            val venueList = venueRepository.getVenuesByLocation()
            withContext(Dispatchers.Main) {
                _venues.value = venueList
                _state.value = if (venueList.isNotEmpty()) State.Loaded else State.Empty
            }
        }
    }
}
