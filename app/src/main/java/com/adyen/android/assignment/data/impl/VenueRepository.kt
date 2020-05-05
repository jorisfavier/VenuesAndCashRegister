package com.adyen.android.assignment.data.impl

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.Venue
import com.adyen.android.assignment.data.IVenueRepository
import retrofit2.awaitResponse

class VenueRepository(private val placesService: PlacesService) : IVenueRepository {
    override suspend fun getVenuesByLocation(longitude: Double, latitude: Double): List<Venue> {
        val query = VenueRecommendationsQueryBuilder()
            .setLatitudeLongitude(latitude, longitude)
            .build()
        val response = placesService
            .getVenueRecommendations(query).awaitResponse()
        return response.body()?.response?.groups?.flatMap { group -> group.items.map { it.venue } }
            ?: listOf()
    }
}
