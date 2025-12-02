package it.davidenastri.littlecloud.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

data class ParticleResponse(
    val id: String?,
    val name: String?,
    val connected: Boolean,
    val return_value: Int
)

interface ParticleApiService {
    @FormUrlEncoded
    @POST("v1/devices/{deviceId}/setColor")
    suspend fun setColor(
        @Path("deviceId") deviceId: String,
        @Field("access_token") accessToken: String,
        @Field("args") args: String
    ): ParticleResponse

    @FormUrlEncoded
    @POST("v1/devices/{deviceId}/dfMini")
    suspend fun controlAudio(
        @Path("deviceId") deviceId: String,
        @Field("access_token") accessToken: String,
        @Field("args") args: String
    ): ParticleResponse
}
