package it.davidenastri.littlecloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public final class QueryUtils {

    private static final String SETTINGS = "SETTINGS";
    private static final String PARTICLE_DEVICE_ID_KEY = "particle_device_id";
    private static final String PARTICLE_TOKEN_ID_KEY = "particle_token_id";
    private static final String PARTICLE_API_URL_KEY = "particle_token_url";
    private static final String DEFAULT_NAME = "defaultName";
    private static final String LOG_TAG = "QueryUtils";

    private QueryUtils() {
    }

    private static SharedPreferences getSharedPreferences(View view) {
        return view.getContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    private static String[] getParticleDetails(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(view);
        String deviceId = sharedPreferences.getString(PARTICLE_DEVICE_ID_KEY, DEFAULT_NAME);
        String tokenId = sharedPreferences.getString(PARTICLE_TOKEN_ID_KEY, DEFAULT_NAME);
        String apiUrl = sharedPreferences.getString(PARTICLE_API_URL_KEY, DEFAULT_NAME);
        return new String[]{deviceId, tokenId, apiUrl};
    }

    private static RequestParams createRequestParams(String tokenId, String args) {
        RequestParams params = new RequestParams();
        params.put("access_token", tokenId);
        params.put("args", args);
        return params;
    }

    public static void changeColor(String rgbString, final String colorSet, final View onClickView) {
        String[] particleDetails = getParticleDetails(onClickView);
        String deviceId = particleDetails[0];
        String tokenId = particleDetails[1];
        String apiUrl = particleDetails[2];
        RequestParams params = createRequestParams(tokenId, rgbString);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(apiUrl + deviceId + "/setColor", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Log.i(LOG_TAG, "Color set to: " + colorSet);
                Snackbar.make(onClickView, "Lamp color changed successfully!", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e(LOG_TAG, "Failed to set color: " + res);
                Snackbar.make(onClickView, "Little Cloud is offline...", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static void changeAudio(final String commandString, final View onClickView) {
        String[] particleDetails = getParticleDetails(onClickView);
        String deviceId = particleDetails[0];
        String tokenId = particleDetails[1];
        String apiUrl = particleDetails[2];
        RequestParams params = createRequestParams(tokenId, commandString);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(apiUrl + deviceId + "/dfMini", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Log.i(LOG_TAG, "Audio command sent: " + commandString);
                Snackbar.make(onClickView, "Lamp audio changed successfully!", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e(LOG_TAG, "Failed to change audio: " + res);
                Snackbar.make(onClickView, "Little Cloud is offline...", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
