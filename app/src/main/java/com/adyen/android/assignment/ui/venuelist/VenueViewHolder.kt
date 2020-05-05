package com.adyen.android.assignment.ui.venuelist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.api.model.Venue
import kotlinx.android.synthetic.main.venue_list_item.view.*

class VenueViewHolder(venueView: View) : RecyclerView.ViewHolder(venueView) {

    private var view = itemView

    fun bind(venue: Venue) {
        view.venueName.text = venue.name
        view.venueAddress.text = venue.location.formattedAddress.joinToString(" - ")
    }
}
