package it.davidenastri.littlecloud.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.davidenastri.littlecloud.R
import it.davidenastri.littlecloud.repository.ParticleRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ParticleRepository()
    private val sharedPreferences = application.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Settings
    var particleApiUrl: String = ""
    var particleDeviceId: String = ""
    var particleTokenId: String = ""
    var favouriteColor: String = ""

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val defaultParticleApiUrl = getApplication<Application>().getString(R.string.particle_api_url)
        val defaultParticleDeviceId = getApplication<Application>().getString(R.string.particle_device_id)
        val defaultParticleTokenId = getApplication<Application>().getString(R.string.particle_token_id)
        val defaultFavouriteColor = getApplication<Application>().getString(R.string.favourite_color)

        particleApiUrl = sharedPreferences.getString("particleApiUrl", defaultParticleApiUrl) ?: defaultParticleApiUrl
        particleDeviceId = sharedPreferences.getString("particleDeviceId", defaultParticleDeviceId) ?: defaultParticleDeviceId
        particleTokenId = sharedPreferences.getString("particleTokenId", defaultParticleTokenId) ?: defaultParticleTokenId
        favouriteColor = sharedPreferences.getString("favouriteColor", defaultFavouriteColor) ?: defaultFavouriteColor
    }

    fun saveSettings(url: String, deviceId: String, token: String, favColor: String) {
        sharedPreferences.edit().apply {
            putString("particleApiUrl", url)
            putString("particleDeviceId", deviceId)
            putString("particleTokenId", token)
            putString("favouriteColor", favColor)
            apply()
        }
        loadSettings()
        _toastMessage.value = "I'll keep those settings in mind :)"
    }

    private fun validateSettings(): Boolean {
        // Basic validation - can be improved
        if (particleApiUrl.isEmpty() || particleDeviceId.isEmpty() || particleTokenId.isEmpty()) {
            _toastMessage.value = "Please check your settings. Missing configuration."
            return false
        }
        return true
    }

    fun changeColor(rgbString: String, colorSetDescription: String) {
        if (!validateSettings()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.setColor(particleApiUrl, particleDeviceId, particleTokenId, rgbString)
            _isLoading.value = false
            
            result.onSuccess { response ->
                Log.i("MainViewModel", "Color set to: $colorSetDescription successfully. Response: $response")
                val message = if (response.connected && response.return_value == 1) {
                    "Lamp color changed successfully! Lamp is online."
                } else {
                    "Lamp color changed, but the lamp might be offline."
                }
                _toastMessage.value = message
            }.onFailure { e ->
                handleError(e)
            }
        }
    }

    fun changeAudio(commandString: String) {
        if (!validateSettings()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.controlAudio(particleApiUrl, particleDeviceId, particleTokenId, commandString)
            _isLoading.value = false

            result.onSuccess { response ->
                Log.i("MainViewModel", "Audio command sent: $commandString successfully. Response: $response")
                val message = if (response.connected && response.return_value == 1) {
                    "Lamp audio changed successfully! Lamp is online."
                } else {
                    "Lamp audio changed, but the lamp might be offline."
                }
                _toastMessage.value = message
            }.onFailure { e ->
                handleError(e)
            }
        }
    }

    private fun handleError(e: Throwable) {
        Log.e("MainViewModel", "Request failed", e)
        val message = when (e) {
            is IOException -> "No network connection. Please check your internet."
            is HttpException -> {
                when (e.code()) {
                    401 -> "Invalid access token. Please check settings."
                    404 -> "Device not found. Please check Device ID."
                    408 -> "Request timeout. Please try again."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "Failed to communicate with lamp. Error: ${e.code()}"
                }
            }
            else -> "Failed to communicate with lamp."
        }
        _toastMessage.value = message
    }
}
