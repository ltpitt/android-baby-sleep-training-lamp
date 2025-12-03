package it.davidenastri.littlecloud.repository

import it.davidenastri.littlecloud.network.ParticleApiService
import it.davidenastri.littlecloud.network.ParticleResponse
import it.davidenastri.littlecloud.network.RetrofitClient

open class ParticleRepository {

    open suspend fun setColor(apiUrl: String, deviceId: String, token: String, rgbString: String): Result<ParticleResponse> {
        return try {
            val apiService = RetrofitClient.getClient(apiUrl).create(ParticleApiService::class.java)
            val response = apiService.setColor(deviceId, token, rgbString)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun controlAudio(apiUrl: String, deviceId: String, token: String, command: String): Result<ParticleResponse> {
        return try {
            val apiService = RetrofitClient.getClient(apiUrl).create(ParticleApiService::class.java)
            val response = apiService.controlAudio(deviceId, token, command)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
