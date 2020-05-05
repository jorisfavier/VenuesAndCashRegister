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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.databinding.VenueListFragmentBinding
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

    private lateinit var binding: VenueListFragmentBinding
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.venue_list_fragment, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
        viewModel.loadVenues()
    }

    private fun initObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
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

    private fun requestFineLocationPermission() {
        if (!isFineLocationGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                fineLocationPermissionID
            )
        }
    }

    private fun displayError(message: String? = null) {
        listError.isVisible = true
        listLoader.isVisible = false
        venueList.isVisible = false
        listError.text = message ?: getString(R.string.an_error_occurred)
    }

}
