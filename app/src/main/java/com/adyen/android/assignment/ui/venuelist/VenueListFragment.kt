package com.adyen.android.assignment.ui.venuelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.databinding.VenueListFragmentBinding
import kotlinx.android.synthetic.main.venue_list_fragment.*

class VenueListFragment : Fragment() {

    private val viewModel: VenueListViewModel by viewModels {
        VenueListViewModelFactory(PlacesService.instance)
    }

    private lateinit var binding: VenueListFragmentBinding
    private var venueAdapter = VenueListAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.venue_list_fragment, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            listLoader.isVisible = state == VenueListViewModel.State.Loading
            venueList.isVisible = state == VenueListViewModel.State.Loaded
            listError.isVisible = state == VenueListViewModel.State.Error
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        venueList.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        venueList.adapter = venueAdapter

        viewModel.venues.observe(viewLifecycleOwner, Observer { venues ->
            venueAdapter.updateVenueList(venues)
        })

        viewModel.getVenuesAroundMe()
    }

}
