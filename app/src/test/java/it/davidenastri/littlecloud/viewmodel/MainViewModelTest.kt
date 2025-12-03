package it.davidenastri.littlecloud.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import it.davidenastri.littlecloud.R
import it.davidenastri.littlecloud.network.ParticleResponse
import it.davidenastri.littlecloud.repository.ParticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    @Mock
    private lateinit var repository: ParticleRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(application.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        
        // Mock string resources
        `when`(application.getString(R.string.particle_api_url)).thenReturn("default_url")
        `when`(application.getString(R.string.particle_device_id)).thenReturn("default_device")
        `when`(application.getString(R.string.particle_token_id)).thenReturn("default_token")
        `when`(application.getString(R.string.favourite_color)).thenReturn("default_color")
        `when`(application.getString(R.string.msg_settings_saved)).thenReturn("Settings saved")
        `when`(application.getString(R.string.msg_lamp_color_success)).thenReturn("Color success")
        `when`(application.getString(R.string.msg_lamp_color_offline)).thenReturn("Color offline")
        `when`(application.getString(R.string.error_no_network)).thenReturn("No network")
        
        // Default shared prefs values
        `when`(sharedPreferences.getString("particleApiUrl", "default_url")).thenReturn("saved_url")
        `when`(sharedPreferences.getString("particleDeviceId", "default_device")).thenReturn("saved_device")
        `when`(sharedPreferences.getString("particleTokenId", "default_token")).thenReturn("saved_token")
        `when`(sharedPreferences.getString("favouriteColor", "default_color")).thenReturn("saved_color")

        viewModel = MainViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSettings loads values from SharedPreferences`() {
        assertEquals("saved_url", viewModel.particleApiUrl)
        assertEquals("saved_device", viewModel.particleDeviceId)
        assertEquals("saved_token", viewModel.particleTokenId)
        assertEquals("saved_color", viewModel.favouriteColor)
    }

    @Test
    fun `saveSettings saves values and updates LiveData`() {
        viewModel.saveSettings("new_url", "new_device", "new_token", "new_color")

        verify(editor).putString("particleApiUrl", "new_url")
        verify(editor).putString("particleDeviceId", "new_device")
        verify(editor).putString("particleTokenId", "new_token")
        verify(editor).putString("favouriteColor", "new_color")
        verify(editor).apply()

        assertEquals("Settings saved", viewModel.toastMessage.value)
    }

    @Test
    fun `changeColor success updates LiveData`() = runTest(testDispatcher) {
        val response = ParticleResponse(id = "1", name = "test", connected = true, return_value = 1)
        `when`(repository.setColor("saved_url", "saved_device", "saved_token", "rgb"))
            .thenReturn(Result.success(response))

        println("Before changeColor: URL=${viewModel.particleApiUrl}")
        viewModel.changeColor("rgb", "Red")
        testDispatcher.scheduler.advanceUntilIdle()
        println("After changeColor: Toast=${viewModel.toastMessage.value}")

        assertEquals("Color success", viewModel.toastMessage.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `changeColor failure network error updates LiveData`() = runTest(testDispatcher) {
        `when`(repository.setColor("saved_url", "saved_device", "saved_token", "rgb"))
            .thenReturn(Result.failure(IOException()))

        viewModel.changeColor("rgb", "Red")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No network", viewModel.toastMessage.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}
