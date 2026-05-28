package com.lmu.setupmanager.ui.saved

import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedSetupsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepo: FakeSetupRepository
    private lateinit var viewModel: SavedSetupsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeSetupRepository()
        viewModel = SavedSetupsViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── exportSetupJson ───────────────────────────────────────────────────────

    @Test
    fun `exportSetupJson produces JSON that round-trips back to same Setup fields`() {
        val setup = testSetup()
        val json = viewModel.exportSetupJson(setup)
        val parsed = Json.decodeFromString<Setup>(json)

        assertEquals(setup.name, parsed.name)
        assertEquals(setup.carId, parsed.carId)
        assertEquals(setup.trackId, parsed.trackId)
        assertEquals(setup.conditions, parsed.conditions)
        assertEquals(setup.values, parsed.values)
        assertEquals(setup.notes, parsed.notes)
    }

    // ── importSetupFromJson ───────────────────────────────────────────────────

    @Test
    fun `importSetupFromJson with valid JSON saves a setup with a new id`() = runTest {
        val original = testSetup(id = "original-id")
        val json = viewModel.exportSetupJson(original)

        val result = viewModel.importSetupFromJson(json)

        assertTrue(result)
        assertEquals(1, fakeRepo.savedSetups.size)
        assertEquals(original.name, fakeRepo.savedSetups[0].name)
        // id must be different — importSetupFromJson assigns a fresh UUID
        assertFalse(original.id == fakeRepo.savedSetups[0].id)
    }

    @Test
    fun `importSetupFromJson with malformed JSON returns false and does not crash`() = runTest {
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val result = viewModel.importSetupFromJson("{not valid json!!!}")
        advanceUntilIdle()

        assertFalse(result)
        assertTrue(fakeRepo.savedSetups.isEmpty())
        assertTrue(viewModel.uiState.value.importError)
        collectJob.cancel()
    }

    // ── duplicateSetup ────────────────────────────────────────────────────────

    @Test
    fun `duplicateSetup calls repository duplicateSetup with the correct id`() = runTest {
        viewModel.duplicateSetup("some-setup-id")
        advanceUntilIdle()

        assertEquals("some-setup-id", fakeRepo.lastDuplicatedId)
    }

    // ── renameSetup ───────────────────────────────────────────────────────────

    @Test
    fun `renameSetup calls repository with trimmed name`() = runTest {
        viewModel.renameSetup("some-id", "  New Name  ")
        advanceUntilIdle()

        assertEquals("some-id", fakeRepo.lastRenamedId)
        assertEquals("New Name", fakeRepo.lastRenamedName)
    }

    // ── Rename dialog ─────────────────────────────────────────────────────────

    @Test
    fun `openRenameDialog sets renameTargetId and renameCurrentName`() = runTest {
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.openRenameDialog("id123", "My Setup")
        advanceUntilIdle()

        assertEquals("id123", viewModel.uiState.value.renameTargetId)
        assertEquals("My Setup", viewModel.uiState.value.renameCurrentName)
        collectJob.cancel()
    }

    @Test
    fun `closeRenameDialog clears renameTargetId`() = runTest {
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.openRenameDialog("id123", "My Setup")
        advanceUntilIdle()
        viewModel.closeRenameDialog()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.renameTargetId)
        collectJob.cancel()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun testSetup(
        id: String = "test-id",
        name: String = "Test Setup"
    ) = Setup(
        id = id,
        name = name,
        carId = "bmw-m4-gt3",
        trackId = "le-mans",
        conditions = Conditions.DRY,
        values = mapOf("springRate_FL" to 120f),
        notes = "test notes",
        createdAt = 1_000L,
        updatedAt = 2_000L
    )
}

// ── Fake repository ───────────────────────────────────────────────────────────

private class FakeSetupRepository : SetupRepository {

    private val _setups = MutableStateFlow<List<Setup>>(emptyList())

    val savedSetups: List<Setup> get() = _setups.value
    var lastRenamedId: String? = null
    var lastRenamedName: String? = null
    var lastDuplicatedId: String? = null

    override fun getAllSetups(): Flow<List<Setup>> = _setups

    override suspend fun getSetupById(id: String): Setup? =
        _setups.value.firstOrNull { it.id == id }

    override suspend fun saveSetup(setup: Setup) {
        _setups.value = _setups.value + setup
    }

    override suspend fun deleteSetup(id: String) {
        _setups.value = _setups.value.filter { it.id != id }
    }

    override suspend fun renameSetup(id: String, newName: String) {
        lastRenamedId = id
        lastRenamedName = newName
    }

    override suspend fun duplicateSetup(id: String): Setup {
        lastDuplicatedId = id
        // Return a minimal copy; tests only verify the id was forwarded correctly
        return Setup(
            id = "copy-id",
            name = "Copy",
            carId = "",
            trackId = "",
            conditions = Conditions.DRY,
            values = emptyMap(),
            notes = "",
            createdAt = 0L,
            updatedAt = 0L
        )
    }
}
