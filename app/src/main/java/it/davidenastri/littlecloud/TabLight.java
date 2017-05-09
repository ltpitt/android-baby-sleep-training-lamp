package it.davidenastri.littlecloud;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by DNastri on 5/3/2017.
 */

public class TabLight extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_light, container, false);

        final ColorPicker colorPicker;
        final Button button;

        colorPicker = (ColorPicker) rootView.findViewById(R.id.colorPicker);

        button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int color = colorPicker.getColor();
                String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
                Log.e("RGB color code is: ", rgbString);

            }
        });

        return rootView;

    }




}
