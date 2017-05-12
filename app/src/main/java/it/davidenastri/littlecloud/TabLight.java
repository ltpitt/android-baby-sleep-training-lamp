package it.davidenastri.littlecloud;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import static it.davidenastri.littlecloud.R.id.fab;


/**
 * Created by DNastri on 5/3/2017.
 */

public class TabLight extends Fragment{

    /**
     * Log tag for EditorActivity
     */
    private static final String LOG_TAG = TabLight.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_light, container, false);

        final ColorPicker colorPicker;
        final FloatingActionButton floatingActionButton;

        colorPicker = (ColorPicker) rootView.findViewById(R.id.colorPicker);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(fab);

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#33691E")));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                final ProgressBar spinner;
                spinner = (ProgressBar)rootView.findViewById(R.id.progressBarLight);

                // Prevent multiple requests by the user if another request is happening
                if (spinner.getVisibility() == View.INVISIBLE) {
                    spinner.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    spinner.setVisibility(View.VISIBLE);

                    int color = colorPicker.getColor();
                    String rgbString = Color.red(color) + "," + Color.green(color) + "," +
                            Color.blue(color);
                    final String colorSet = "Red: " + Color.red(color) + "Green: " + Color.green(color) +
                            "Blue: " + Color.blue(color);
                    Log.e("RGB color code is: ", rgbString);

                    // Http call begin
                    final String PARTICLE_DEVICE_ID = "310031000447353138383138";
                    final String PARTICLE_TOKEN_ID = "1d89ba47f0c5c9ee72b7ebb12ac171bf5c1f9234";
                    final String PARTICLE_API_URL = "https://api.particle.io/v1/devices/";
                    AsyncHttpClient client = new AsyncHttpClient();

                    final RequestParams params = new RequestParams();
                    params.put("access_token", PARTICLE_TOKEN_ID);
                    params.put("args", rgbString);
                    client.post(PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor", params, new TextHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String res) {
                                    // Called when response HTTP status is "200 OK"
                                    Log.i(LOG_TAG,"Lamp color was successfully changed. Cool! :D");
                                    Toast.makeText(rootView.getContext(), colorSet, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(v, "Lamp color changed successfully, yey! :D", Snackbar.LENGTH_LONG).show();
                                    spinner.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                    // Called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                    Log.e(LOG_TAG, PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor" + params);
                                    Toast.makeText(rootView.getContext(), PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor" + params, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(v, "A terrible error prevented lamp color change :(", Snackbar.LENGTH_LONG).show();
                                    spinner.setVisibility(View.INVISIBLE);
                                }
                            }
                    );
                    // Http call end
                }

            }

        } );

        return rootView;

    }
}
