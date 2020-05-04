package com.adyen.android.assignment.data

import com.adyen.android.assignment.api.model.Venue

interface IVenueRepository {
    suspend fun getVenuesByLocation(): List<Venue>
}
