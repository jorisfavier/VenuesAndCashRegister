package com.adyen.android.assignment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _fineLocationGranted = MutableLiveData<Boolean>()
    val fineLocationGranted: LiveData<Boolean> = _fineLocationGranted

    fun setFineLocationGranted(granted: Boolean) {
        _fineLocationGranted.value = granted
    }
}
