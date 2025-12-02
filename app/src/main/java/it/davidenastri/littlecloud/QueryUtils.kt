package it.davidenastri.littlecloud

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject

object QueryUtils {

    private const val SETTINGS = "SETTINGS"
    private const val PARTICLE_DEVICE_ID_KEY = "particleDeviceId"
    private const val PARTICLE_TOKEN_ID_KEY = "particleTokenId"
    private const val PARTICLE_API_URL_KEY = "particleApiUrl"
    private const val DEFAULT_NAME = "defaultName"
    private const val LOG_TAG = "QueryUtils"

    @Volatile
    private var isRequestInProgress = false

    @Synchronized
    private fun startRequest(): Boolean {
        if (isRequestInProgress) return false
        isRequestInProgress = true
        return true
    }

    @Synchronized
    private fun endRequest() {
        isRequestInProgress = false
    }

    private fun getSharedPreferences(view: View): SharedPreferences {
        Log.d(LOG_TAG, "Getting SharedPreferences for view: $view")
        val sharedPreferences = view.context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
        Log.d(LOG_TAG, "SharedPreferences content: ${sharedPreferences.all}")
        return sharedPreferences
    }

    private fun getParticleDetails(view: View): Array<String> {
        Log.d(LOG_TAG, "Fetching particle details from SharedPreferences")
        val sharedPreferences = getSharedPreferences(view)
        val deviceId = sharedPreferences.getString(PARTICLE_DEVICE_ID_KEY, DEFAULT_NAME) ?: DEFAULT_NAME
        val tokenId = sharedPreferences.getString(PARTICLE_TOKEN_ID_KEY, DEFAULT_NAME) ?: DEFAULT_NAME
        val apiUrl = sharedPreferences.getString(PARTICLE_API_URL_KEY, DEFAULT_NAME) ?: DEFAULT_NAME
        Log.d(LOG_TAG, "Particle details - Device ID: $deviceId, Token ID: $tokenId, API URL: $apiUrl")
        return arrayOf(deviceId, tokenId, apiUrl)
    }

    private fun createRequestParams(tokenId: String, args: String): RequestParams {
        Log.d(LOG_TAG, "Creating request parameters with token ID: $tokenId and args: $args")
        return RequestParams().apply {
            put("access_token", tokenId)
            put("args", args)
        }
    }

    fun changeColor(rgbString: String, colorSet: String, onClickView: View) {
        if (!startRequest()) {
            Toast.makeText(onClickView.context, "Please wait, request is already in progress.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(LOG_TAG, "Changing color to: $colorSet with RGB string: $rgbString")
        val (deviceId, tokenId, apiUrl) = getParticleDetails(onClickView)

        val url = "$apiUrl$deviceId/setColor"
        val params = createRequestParams(tokenId, rgbString)
        val client = AsyncHttpClient()

        Log.d(LOG_TAG, "Sending POST request to URL: $url with params: $params")
        client.post(url, params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, res: String) {
                endRequest()
                Log.i(LOG_TAG, "Color set to: $colorSet successfully. Response: $res")
                try {
                    val response = JSONObject(res)
                    val connected = response.getBoolean("connected")
                    val returnValue = response.getInt("return_value")

                    val message = if (connected && returnValue == 1) {
                        "Lamp color changed successfully! Lamp is online."
                    } else {
                        "Lamp color changed, but the lamp might be offline."
                    }
                    Toast.makeText(onClickView.context, message, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    Log.e(LOG_TAG, "Failed to parse response JSON", e)
                    Toast.makeText(onClickView.context, "Lamp color changed, but failed to confirm lamp status.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
                endRequest()
                Log.e(LOG_TAG, "Failed to set color. Status code: $statusCode, Response: $res", t)
                
                val message = when (statusCode) {
                    0 -> "No network connection. Please check your internet."
                    401 -> "Invalid access token. Please check settings."
                    404 -> "Device not found. Please check Device ID."
                    408 -> "Request timeout. Please try again."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "Failed to communicate with lamp. Error: $statusCode"
                }
                
                Toast.makeText(onClickView.context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun changeAudio(commandString: String, onClickView: View) {
        if (!startRequest()) {
            Toast.makeText(onClickView.context, "Please wait, request is already in progress.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(LOG_TAG, "Changing audio with command: $commandString")
        val (deviceId, tokenId, apiUrl) = getParticleDetails(onClickView)

        val url = "$apiUrl$deviceId/dfMini"
        val params = createRequestParams(tokenId, commandString)
        val client = AsyncHttpClient()

        Log.d(LOG_TAG, "Sending POST request to URL: $url with params: $params")
        client.post(url, params, object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, res: String) {
                endRequest()
                Log.i(LOG_TAG, "Audio command sent: $commandString successfully. Response: $res")
                try {
                    val response = JSONObject(res)
                    val connected = response.getBoolean("connected")
                    val returnValue = response.getInt("return_value")

                    val message = if (connected && returnValue == 1) {
                        "Lamp audio changed successfully! Lamp is online."
                    } else {
                        "Lamp audio changed, but the lamp might be offline."
                    }
                    Toast.makeText(onClickView.context, message, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    Log.e(LOG_TAG, "Failed to parse response JSON", e)
                    Toast.makeText(onClickView.context, "Lamp audio changed, but failed to confirm lamp status.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
                endRequest()
                Log.e(LOG_TAG, "Failed to change audio. Status code: $statusCode, Response: $res", t)
                
                val message = when (statusCode) {
                    0 -> "No network connection. Please check your internet."
                    401 -> "Invalid access token. Please check settings."
                    404 -> "Device not found. Please check Device ID."
                    408 -> "Request timeout. Please try again."
                    in 500..599 -> "Server error. Please try again later."
                    else -> "Failed to communicate with lamp. Error: $statusCode"
                }
                
                Toast.makeText(onClickView.context, message, Toast.LENGTH_LONG).show()
            }
        })
    }
}
