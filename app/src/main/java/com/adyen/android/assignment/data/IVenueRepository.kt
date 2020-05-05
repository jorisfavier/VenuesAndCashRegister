package com.adyen.android.assignment.data

import com.adyen.android.assignment.api.model.Venue

interface IVenueRepository {
    
    /**
     * Return the best venues around a given location
     *
     * @param longitude
     * @param latitude
     * @return
     */
    suspend fun getVenuesByLocation(longitude: Double, latitude: Double): List<Venue>
}
