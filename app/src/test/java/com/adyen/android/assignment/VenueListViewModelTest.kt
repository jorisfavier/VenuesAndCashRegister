package com.adyen.android.assignment

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.adyen.android.assignment.Util.getOrAwaitValue
import com.adyen.android.assignment.api.model.Venue
import com.adyen.android.assignment.api.model.VenuePage
import com.adyen.android.assignment.data.IVenueRepository
import com.adyen.android.assignment.ui.venuelist.VenueListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class VenueListViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private val fakeLocation = mock<Location>() {
        on { latitude } doReturn 1.0
        on { longitude } doReturn 1.0
    }

    private val fakeLocationDTO = com.adyen.android.assignment.api.model.Location(
        "",
        "",
        "",
        "",
        "",
        0,
        listOf(),
        listOf(),
        0.0,
        0.0,
        "",
        ""
    )

    private val fakeVenueList = listOf(
        Venue(listOf(), "1", fakeLocationDTO, "test", 0.0, VenuePage("1")),
        Venue(listOf(), "2", fakeLocationDTO, "test 2", 0.0, VenuePage("2"))
    )

    private val locationProviderOnSuccess = mock<FusedLocationProviderClient>() {
        on { lastLocation } doReturn mock()
        whenever(this.mock.lastLocation.addOnSuccessListener(any())).then {
            val failListener = it.getArgument<OnSuccessListener<Location>>(0)
            failListener.onSuccess(fakeLocation)
            null
        }
    }

    @Test
    fun `not granting fine location should emit FineLocationNotGranted state`() {
        //given
        val locationProvider = mock<FusedLocationProviderClient>() {
            on { lastLocation } doReturn mock()
            whenever(this.mock.lastLocation.addOnFailureListener(any())).then {
                val failListener = it.getArgument<OnFailureListener>(0)
                failListener.onFailure(Exception())
                null
            }
        }
        val viewModel = VenueListViewModel(mock(), locationProvider)

        //when
        viewModel.loadVenues()

        //then
        assert(viewModel.state.getOrAwaitValue() == VenueListViewModel.State.FineLocationNotGranted)
    }

    @Test
    fun `granting fine location should load the venues around me`() {
        //given
        val venueRepo = mock<IVenueRepository> {
            onBlocking { getVenuesByLocation(any(), any()) } doReturn fakeVenueList
        }
        val viewModel = VenueListViewModel(venueRepo, locationProviderOnSuccess)
        mainCoroutineRule.pauseDispatcher()

        //when
        viewModel.loadVenues()

        //then
        assert(viewModel.state.getOrAwaitValue() == VenueListViewModel.State.Loading)
        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.state.getOrAwaitValue() is VenueListViewModel.State.Loaded)
        assert(viewModel.venues.getOrAwaitValue() == fakeVenueList)

    }

    @Test
    fun `empty venue list should emit an Empty state`() {
        //given
        val venueRepo = mock<IVenueRepository> {
            onBlocking { getVenuesByLocation(any(), any()) } doReturn listOf()
        }
        val viewModel = VenueListViewModel(venueRepo, locationProviderOnSuccess)
        mainCoroutineRule.pauseDispatcher()

        //when
        viewModel.loadVenues()

        //then
        assert(viewModel.state.getOrAwaitValue() == VenueListViewModel.State.Loading)

        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.state.getOrAwaitValue() == VenueListViewModel.State.Empty)
    }

    @Test
    fun `granting fine location with a repository exception should emit an error state`() {
        val venueRepo = mock<IVenueRepository> {
            onBlocking {
                getVenuesByLocation(
                    any(),
                    any()
                )
            } doAnswer { throw Exception() }
        }
        val viewModel = VenueListViewModel(venueRepo, locationProviderOnSuccess)

        mainCoroutineRule.pauseDispatcher()

        viewModel.loadVenues()

        //then
        assert(viewModel.state.getOrAwaitValue() == VenueListViewModel.State.Loading)

        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.state.getOrAwaitValue() is VenueListViewModel.State.Error)
    }
}
