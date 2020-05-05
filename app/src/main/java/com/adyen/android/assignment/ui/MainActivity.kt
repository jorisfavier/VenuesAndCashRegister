package com.adyen.android.assignment.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adyen.android.assignment.R
import com.adyen.android.assignment.ui.venuelist.VenueListFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        viewModel.setFineLocationGranted(
            requestCode == VenueListFragment.fineLocationPermissionID && grantResults.isNotEmpty()
                    && grantResults.first() == PackageManager.PERMISSION_GRANTED
        )
    }
}
