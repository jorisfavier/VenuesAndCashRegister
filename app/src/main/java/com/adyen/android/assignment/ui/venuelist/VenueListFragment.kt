package com.adyen.android.assignment.ui.venuelist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.ui.MainActivityViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.venue_list_fragment.*


class VenueListFragment : Fragment() {

    companion object {
        const val fineLocationPermissionID = 54
    }

    private val viewModel: VenueListViewModel by viewModels {
        VenueListViewModelFactory(
            PlacesService.instance,
            LocationServices.getFusedLocationProviderClient(requireActivity())
        )
    }
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private var venueAdapter = VenueListAdapter(ArrayList())
    private val isFineLocationGranted: Boolean
        get() =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.venue_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
        viewModel.loadVenues()
    }

    private fun initObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            handleUIState(state)
        })

        sharedViewModel.fineLocationGranted.observe(viewLifecycleOwner, Observer { granted ->
            if (granted) {
                viewModel.loadVenues()
            } else {
                displayError(getString(R.string.location_not_granted_error))
            }
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        venueList.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        venueList.adapter = venueAdapter

        viewModel.venues.observe(viewLifecycleOwner, Observer { venues ->
            venueAdapter.updateVenueList(venues)
        })
    }

    /**
     * Show and hide the different UI elements based on the given state
     *
     * @param state
     */
    private fun handleUIState(state: VenueListViewModel.State) {
        listLoader.isVisible = state == VenueListViewModel.State.Loading
        venueList.isVisible = state == VenueListViewModel.State.Loaded
        listError.isVisible = state is VenueListViewModel.State.Error
                || state is VenueListViewModel.State.Empty

        when (state) {
            is VenueListViewModel.State.Error -> {
                Log.w(VenueListFragment::class.java.simpleName, state.throwable)
                displayError()
            }
            VenueListViewModel.State.Empty -> {
                displayError(getString(R.string.no_result))
            }
            VenueListViewModel.State.FineLocationNotGranted -> {
                requestFineLocationPermission()
            }
        }
    }

    /**
     * If the fine location has not been granted a native dialog will be prompt to the user
     * asking for the permission
     */
    private fun requestFineLocationPermission() {
        if (!isFineLocationGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                fineLocationPermissionID
            )
        }
    }

    /**
     * Display an error message in the center of the screen
     *
     * @param message the message to be displayed
     */
    private fun displayError(message: String? = null) {
        listError.isVisible = true
        listLoader.isVisible = false
        venueList.isVisible = false
        listError.text = message ?: getString(R.string.an_error_occurred)
    }

}
