package com.adyen.android.assignment.ui.venuelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Venue

class VenueListAdapter(private var venuesList: List<Venue>) :
    RecyclerView.Adapter<VenueViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.venue_list_item, parent, false)

        return VenueViewHolder(view)
    }

    override fun getItemCount(): Int {
        return venuesList.size
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(venuesList[position])
    }

    fun updateVenueList(venues: List<Venue>) {
        venuesList = venues
        notifyDataSetChanged()
    }

}
