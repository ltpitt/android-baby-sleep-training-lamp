package it.davidenastri.littlecloud;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import static android.graphics.Color.red;
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

        // Inflate tab_light layout
        final View tabLightView = inflater.inflate(R.layout.tab_light, container, false);

        final ColorPicker colorPicker;
        colorPicker = (ColorPicker) tabLightView.findViewById(R.id.colorPicker);

        final FloatingActionButton FABcustomColor;
        FABcustomColor = (FloatingActionButton) tabLightView.findViewById(fab);

        FABcustomColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View onClickView) {
                final ProgressBar spinner;
                spinner = (ProgressBar)tabLightView.findViewById(R.id.progressBarLight);
                // Prevent multiple requests by the user if another request is happening
                if (spinner.getVisibility() == View.INVISIBLE) {
                    // Change spinner color
                    spinner.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    // Make spinner visible
                    spinner.setVisibility(View.VISIBLE);
                    // Get color from colorpicker
                    int color = colorPicker.getColor();
                    // Prepare rgbString that will be sent to the lamp
                    String rgbString = red(color) + "," + Color.green(color) + "," +
                            Color.blue(color);
                    // Prepare a human readable colorSet string for debug
                    final String colorSet = "Red: " + red(color) + " Green: " + Color.green(color) +
                            " Blue: " + Color.blue(color);
                    // Send the http call to change color
                    ColorChanger.executeQuery(rgbString, colorSet, onClickView, tabLightView, spinner);
                }
            }

        } );

        final FloatingActionButton FABsleepColor;
        FABsleepColor = (FloatingActionButton) tabLightView.findViewById(R.id.fabSleepColor);

        FABsleepColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View onClickView) {
                final ProgressBar spinner;
                spinner = (ProgressBar)tabLightView.findViewById(R.id.progressBarSleepColor);
                // Prevent multiple requests by the user if another request is happening
                if (spinner.getVisibility() == View.INVISIBLE) {
                    // Change spinner color
                    spinner.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    // Make spinner visible
                    spinner.setVisibility(View.VISIBLE);
                    // Prepare rgbString that will be sent to the lamp
                    String rgbString = "255,0,0";
                    // Prepare a human readable colorSet string for debug
                    final String colorSet = "Red: 255 Green: 0 Blue: 0";
                    // Send the http call to change color
                    ColorChanger.executeQuery(rgbString, colorSet, onClickView, tabLightView, spinner);
                }

            }

        } );

        final FloatingActionButton FABwakeupColor;
        FABwakeupColor = (FloatingActionButton) tabLightView.findViewById(R.id.fabWakeupColor);

        FABwakeupColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View onClickView) {
                final ProgressBar spinner;
                spinner = (ProgressBar)tabLightView.findViewById(R.id.progressBarWakeupColor);
                // Prevent multiple requests by the user if another request is happening
                if (spinner.getVisibility() == View.INVISIBLE) {
                    // Change spinner color
                    spinner.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    // Make spinner visible
                    spinner.setVisibility(View.VISIBLE);
                    // Prepare rgbString that will be sent to the lamp
                    String rgbString = "0,255,0";
                    // Prepare a human readable colorSet string for debug
                    final String colorSet = "Red: 0 Green: 255 Blue: 0";
                    // Send the http call to change color
                    ColorChanger.executeQuery(rgbString, colorSet, onClickView, tabLightView, spinner);
                }

            }

        } );

        return tabLightView;
    }
}
