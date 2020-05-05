package com.adyen.android.assignment

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.model.*
import com.adyen.android.assignment.data.impl.VenueRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test
import retrofit2.Response
import retrofit2.mock.Calls

class VenueRepositoryTest {

    private val fakeLatlng = LatLng(0.0, 0.0)
    private val fakeEmptyVenueRecommendation = VenueRecommendationsResponse(
        listOf(),
        "",
        "",
        "",
        SuggestedBounds(fakeLatlng, fakeLatlng),
        0,
        0,
        Warning("")
    )

    @Test
    fun `empty foursquare response should return empty list`() {
        //given
        val emptyBodyResponse =
            Response.success<ResponseWrapper<VenueRecommendationsResponse>>(
                200, ResponseWrapper(
                    Meta(200, "test"),
                    fakeEmptyVenueRecommendation
                )
            )
        val placesService: PlacesService = mock {
            on { getVenueRecommendations(any()) } doReturn Calls.response(emptyBodyResponse)
        }
        val venueRepo = VenueRepository(placesService)

        //when
        val result = runBlocking { venueRepo.getVenuesByLocation(0.0, 0.0) }

        //then
        assert(result.isEmpty())
    }

    @Test(expected = IllegalStateException::class)
    fun `meta code different than 200 should throw an exception`() {
        //given
        val emptyBodyResponse =
            Response.success<ResponseWrapper<VenueRecommendationsResponse>>(
                200, ResponseWrapper(
                    Meta(400, "test"),
                    fakeEmptyVenueRecommendation
                )
            )
        val placesService: PlacesService = mock {
            on { getVenueRecommendations(any()) } doReturn Calls.response(emptyBodyResponse)
        }
        val venueRepo = VenueRepository(placesService)

        //when
        runBlocking { venueRepo.getVenuesByLocation(0.0, 0.0) }

        //then
        Assert.fail()
    }

    @Test(expected = IllegalStateException::class)
    fun `non successful http code should throw an exception`() {
        //given
        val emptyBodyResponse =
            Response.error<ResponseWrapper<VenueRecommendationsResponse>>(
                400,
                ResponseBody.create(null, "")
            )
        val placesService: PlacesService = mock {
            on { getVenueRecommendations(any()) } doReturn Calls.response(emptyBodyResponse)
        }
        val venueRepo = VenueRepository(placesService)

        //when
        runBlocking { venueRepo.getVenuesByLocation(0.0, 0.0) }

        //then
        Assert.fail()

    }

}
