package it.davidenastri.littlecloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

/**
 * Created by DNastri on 5/3/2017.
 */

public class TabSoundActivity extends Fragment{

    public int currentVolume;

    public int getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View tabSoundView = inflater.inflate(R.layout.tab_sound, container, false);
        final SeekBar volumeSeekbar = (SeekBar) tabSoundView.findViewById(R.id.volumeSeekbar);
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                setCurrentVolume(progress);
                QueryUtils.changeAudio("setVolume," + String.valueOf(currentVolume), tabSoundView);
                Log.i("Current Volume:", String.valueOf(currentVolume));

            }
        });

        int currentVolume = volumeSeekbar.getProgress();
        Log.i("Current volume:", Integer.toString(currentVolume) );
        return tabSoundView;
    }
}
