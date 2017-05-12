package it.davidenastri.littlecloud;

/**
 * Created by DNastri on 5/11/2017.
 */

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by DNastri on 5/11/2017.
 */

public class QueryUtils {


    // Http call
    final String PARTICLE_DEVICE_ID = "310031000447353138383138";
    final String PARTICLE_TOKEN_ID = "1d89ba47f0c5c9ee72b7ebb12ac171bf5c1f9234";
    final String PARTICLE_API_URL = "https://api.particle.io/v1/devices/";
    AsyncHttpClient client = new AsyncHttpClient();

    QueryUtils() {

    }

    public void executeQuery(String rgbString, final String colorSet, final View v, final View rootView  ) {


        final FloatingActionButton floatingActionButton;
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        final RequestParams params = new RequestParams();
        params.put("access_token", PARTICLE_TOKEN_ID);
        params.put("args", rgbString);
        final ProgressBar spinner;
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBarLight);
        client.post(PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                // called when response HTTP status is "200 OK"
                Log.i(LOG_TAG, "Lamp color was successfully changed. Cool! :D");
                Toast.makeText(rootView.getContext(), colorSet, Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "Lamp color changed successfully, yey! :D", Snackbar.LENGTH_LONG).show();
                spinner.setVisibility(View.GONE);
                floatingActionButton.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(rootView.getContext(), PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor" + params, Toast.LENGTH_SHORT).show();
                Snackbar.make(v, "A terrible error prevented lamp color change :(", Snackbar.LENGTH_LONG).show();
                Log.e(LOG_TAG, PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor" + params);
                spinner.setVisibility(View.GONE);
                floatingActionButton.show();
            }
        });
    }
}
