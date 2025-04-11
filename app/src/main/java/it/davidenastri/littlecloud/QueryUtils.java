package it.davidenastri.littlecloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public final class QueryUtils {

    private static final String SETTINGS = "SETTINGS";
    private static final String PARTICLE_DEVICE_ID_KEY = "particleDeviceId";
    private static final String PARTICLE_TOKEN_ID_KEY = "particleTokenId";
    private static final String PARTICLE_API_URL_KEY = "particleApiUrl";
    private static final String DEFAULT_NAME = "defaultName";
    private static final String LOG_TAG = "QueryUtils";

    private QueryUtils() {
    }

    private static SharedPreferences getSharedPreferences(View view) {
        Log.d(LOG_TAG, "Getting SharedPreferences for view: " + view);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        Log.d(LOG_TAG, "SharedPreferences content: " + sharedPreferences.getAll().toString());
        return sharedPreferences;
    }

    private static String[] getParticleDetails(View view) {
        Log.d(LOG_TAG, "Fetching particle details from SharedPreferences");
        SharedPreferences sharedPreferences = getSharedPreferences(view);
        String deviceId = sharedPreferences.getString(PARTICLE_DEVICE_ID_KEY, DEFAULT_NAME);
        String tokenId = sharedPreferences.getString(PARTICLE_TOKEN_ID_KEY, DEFAULT_NAME);
        String apiUrl = sharedPreferences.getString(PARTICLE_API_URL_KEY, DEFAULT_NAME);
        Log.d(LOG_TAG, "Particle details - Device ID: " + deviceId + ", Token ID: " + tokenId + ", API URL: " + apiUrl);
        return new String[]{deviceId, tokenId, apiUrl};
    }

    private static RequestParams createRequestParams(String tokenId, String args) {
        Log.d(LOG_TAG, "Creating request parameters with token ID: " + tokenId + " and args: " + args);
        RequestParams params = new RequestParams();
        params.put("access_token", tokenId);
        params.put("args", args);
        return params;
    }

    public static void changeColor(String rgbString, final String colorSet, final View onClickView) {
        Log.d(LOG_TAG, "Changing color to: " + colorSet + " with RGB string: " + rgbString);
        String[] particleDetails = getParticleDetails(onClickView);
        String deviceId = particleDetails[0];
        String tokenId = particleDetails[1];
        String apiUrl = particleDetails[2];

        String url = apiUrl + deviceId + "/setColor";
        RequestParams params = createRequestParams(tokenId, rgbString);
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d(LOG_TAG, "Sending POST request to URL: " + url + " with params: " + params);
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Log.i(LOG_TAG, "Color set to: " + colorSet + " successfully. Response: " + res);
                try {
                    JSONObject response = new JSONObject(res);
                    boolean connected = response.getBoolean("connected");
                    int returnValue = response.getInt("return_value");

                    if (connected && returnValue == 1) {
                        Toast.makeText(onClickView.getContext(), "Lamp color changed successfully! Lamp is online.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(onClickView.getContext(), "Lamp color changed, but the lamp might be offline.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Failed to parse response JSON", e);
                    Toast.makeText(onClickView.getContext(), "Lamp color changed, but failed to confirm lamp status.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e(LOG_TAG, "Failed to set color. Status code: " + statusCode + ", Response: " + res, t);
                Toast.makeText(onClickView.getContext(), "Little Cloud is offline...", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void changeAudio(final String commandString, final View onClickView) {
        Log.d(LOG_TAG, "Changing audio with command: " + commandString);
        String[] particleDetails = getParticleDetails(onClickView);
        String deviceId = particleDetails[0];
        String tokenId = particleDetails[1];
        String apiUrl = particleDetails[2];

        String url = apiUrl + deviceId + "/dfMini";
        RequestParams params = createRequestParams(tokenId, commandString);
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d(LOG_TAG, "Sending POST request to URL: " + url + " with params: " + params);
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Log.i(LOG_TAG, "Audio command sent: " + commandString + " successfully. Response: " + res);
                try {
                    JSONObject response = new JSONObject(res);
                    boolean connected = response.getBoolean("connected");
                    int returnValue = response.getInt("return_value");

                    if (connected && returnValue == 1) {
                        Toast.makeText(onClickView.getContext(), "Lamp audio changed successfully! Lamp is online.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(onClickView.getContext(), "Lamp audio changed, but the lamp might be offline.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Failed to parse response JSON", e);
                    Toast.makeText(onClickView.getContext(), "Lamp audio changed, but failed to confirm lamp status.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e(LOG_TAG, "Failed to change audio. Status code: " + statusCode + ", Response: " + res, t);
                Toast.makeText(onClickView.getContext(), "Little Cloud is offline...", Toast.LENGTH_LONG).show();
            }
        });
    }
}
