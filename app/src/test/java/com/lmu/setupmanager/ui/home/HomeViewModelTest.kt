package com.lmu.setupmanager.ui.home

import com.lmu.setupmanager.data.static.tracks
import com.lmu.setupmanager.domain.model.Conditions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── filteredTracks ────────────────────────────────────────────────────────

    @Test
    fun `setTrackFilter filters tracks by name case-insensitively`() = runTest {
        val collectJob = launch { viewModel.filteredTracks.collect {} }

        viewModel.setTrackFilter("spa")
        advanceUntilIdle()

        val lowerResult = viewModel.filteredTracks.value
        assertEquals(1, lowerResult.size)
        assertEquals("spa", lowerResult[0].id)

        viewModel.setTrackFilter("SPA")
        advanceUntilIdle()

        val upperResult = viewModel.filteredTracks.value
        assertEquals(1, upperResult.size)
        assertEquals("spa", upperResult[0].id)

        collectJob.cancel()
    }

    @Test
    fun `empty filter returns all tracks`() = runTest {
        val collectJob = launch { viewModel.filteredTracks.collect {} }

        viewModel.setTrackFilter("spa")
        viewModel.setTrackFilter("")
        advanceUntilIdle()

        assertEquals(tracks.size, viewModel.filteredTracks.value.size)
        collectJob.cancel()
    }

    // ── selectTrack ───────────────────────────────────────────────────────────

    @Test
    fun `selectTrack sets selectedTrackId`() {
        viewModel.selectTrack("le-mans")

        assertEquals("le-mans", viewModel.uiState.value.selectedTrackId)
    }

    // ── setConditions ─────────────────────────────────────────────────────────

    @Test
    fun `setConditions sets selectedConditions`() {
        viewModel.setConditions(Conditions.WET)

        assertEquals(Conditions.WET, viewModel.uiState.value.selectedConditions)
    }
}
