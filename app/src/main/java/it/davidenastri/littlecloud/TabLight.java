package it.davidenastri.littlecloud;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;



/**
 * Created by DNastri on 5/3/2017.
 */

public class TabLight extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_light, container, false);

        final ColorPicker colorPicker;
        final Button button;
        final FloatingActionButton floatingActionButton;

        colorPicker = (ColorPicker) rootView.findViewById(R.id.colorPicker);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                int color = colorPicker.getColor();
                String rgbString = Color.red(color) + "," + Color.green(color) + "," +
                        Color.blue(color);
                Log.e("RGB color code is: ", rgbString);

                /*
                Snackbar.make(v, "RGB color code is: " + rgbString, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */


    // Http call

                String PARTICLE_DEVICE_ID = "310031000447353138383138";
                String PARTICLE_TOKEN_ID = "1d89ba47f0c5c9ee72b7ebb12ac171bf5c1f9234";
                String PARTICLE_API_URL = "https://api.particle.io/v1/devices/";
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("access_token", PARTICLE_TOKEN_ID);
                params.put("args", rgbString);
                client.post(PARTICLE_API_URL + PARTICLE_DEVICE_ID + "/setColor", params, new TextHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String res) {
                                // called when response HTTP status is "200 OK"
                                Log.e("Done","Cool! :D");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                Log.e("Dead","Not cool :(");
                            }
                        }
                );

                // Http call













            }

        } );

        return rootView;

    }




}
