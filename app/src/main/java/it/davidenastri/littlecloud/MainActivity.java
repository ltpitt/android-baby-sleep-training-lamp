package it.davidenastri.littlecloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static it.davidenastri.littlecloud.R.id.container;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView menuActionsMain = (BottomNavigationView) findViewById(R.id.menu_actions_main);
        // Execute selected action
        if (menuActionsMain != null) {
            // Set action to perform when any menu-item is selected.
            menuActionsMain.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            int id = item.getItemId();
                            TabSound myTabSound = new TabSound();
                            switch (id) {
                                case R.id.menu_sound_previous:
                                    Log.i("Menu item pressed:", item.getTitle().toString());
                                    QueryUtils.changeAudio("playPrevious," + String.valueOf(myTabSound.getCurrentVolume()), mViewPager);
                                    return true;
                                case R.id.menu_sound_play:
                                    Log.i("Menu item pressed:", item.getTitle().toString());
                                    if (item.getTitle().toString().contains("Play")) {
                                        Log.i("Current Volume:", String.valueOf(myTabSound.getCurrentVolume()));
                                        QueryUtils.changeAudio("play," + String.valueOf(myTabSound.currentVolume), mViewPager);
                                        item.setTitle("Pause");
                                        item.setIcon(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                                        return true;
                                    } else if (item.getTitle().toString().contains("Pause")) {
                                        Log.i("Current Volume:", String.valueOf(myTabSound.getCurrentVolume()));
                                        QueryUtils.changeAudio("pause," + String.valueOf(myTabSound.getCurrentVolume()), mViewPager);
                                        item.setTitle("Play");
                                        item.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                                        return true;
                                    }
                                    break;
                                case R.id.menu_sound_next:
                                    Log.i("Menu item pressed:", item.getTitle().toString());
                                    QueryUtils.changeAudio("playNext," + String.valueOf(myTabSound.currentVolume), mViewPager);
                                    return true;
                            }
                            return false;
                        }
                    });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) { // Tab Light
                    BottomNavigationView menuActionsMain = (BottomNavigationView) findViewById(R.id.menu_actions_main);
                    menuActionsMain.setVisibility(View.INVISIBLE);
                    Log.i("Current page", "Tab Light");
                } else if (position == 1) { // Tab Sound
                    BottomNavigationView menuActionsMain = (BottomNavigationView) findViewById(R.id.menu_actions_main);
                    menuActionsMain.setVisibility(View.VISIBLE);
                    Log.i("Current page", "Tab Sound");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toggle_swipe_scroll) {
            // Save data into SharedPrefrences
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Settings", "Un Mucchio di roba");
            editor.commit();
            return true;
        }

        if (id == R.id.toggle_color_fader) {
            // Save data into SharedPrefrences
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            //String SavedPreferences = getResources().getString(0);
            String value = sharedPref.getString("Name", "");
            Log.e("Pref", value);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new TabLight();
                case 1:
                    return new TabSound();
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LIGHT";
                case 1:
                    return "SOUND";
            }
            return null;
        }

    }
}
