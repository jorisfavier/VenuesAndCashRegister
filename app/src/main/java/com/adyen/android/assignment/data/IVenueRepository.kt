package com.adyen.android.assignment.data

import com.adyen.android.assignment.api.model.Venue

interface IVenueRepository {
    suspend fun getVenuesByLocation(longitude: Double, latitude: Double): List<Venue>
}
