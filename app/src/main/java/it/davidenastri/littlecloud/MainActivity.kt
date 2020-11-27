package it.davidenastri.littlecloud

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerView


class MainActivity : AppCompatActivity() {

    private var currentVolume = 0
    private var isPlaying = false

    private fun getSelectedItem(bottomNavigationView: BottomNavigationView): String {
        val menu = bottomNavigationView.menu
        for (i in 0 until bottomNavigationView.menu.size()) {
            val menuItem = menu.getItem(i)
            if (menuItem.isChecked) {
                return menuItem.title.toString()
            }
        }
        return "none"
    }

    fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness > 0.5
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_light -> {
                colorPicker.visibility = View.VISIBLE
                musicPlayer.visibility = View.GONE
                settingsForm.visibility = View.GONE
                val tagLightStatus = fabButton.getTag(R.id.light_status)
                val tagFabBgColor = fabButton.getTag(R.id.fab_bg_color) as Int
                val tagFabIconColor = fabButton.getTag(R.id.fab_icon_color) as Int
                fabButton.setBackgroundTintList(ColorStateList.valueOf(tagFabBgColor))
                fabButton.setColorFilter(tagFabIconColor)
                if (tagLightStatus == "lightOn") {
                    fabButton.hide()
                    fabButton.setImageResource(R.drawable.ic_lightbulb_on_black)
                    fabButton.show()
                } else {
                    fabButton.hide()
                    fabButton.setImageResource(R.drawable.ic_lightbulb_outline_black)
                    fabButton.show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_music -> {
                colorPicker.visibility = View.GONE
                musicPlayer.visibility = View.VISIBLE
                settingsForm.visibility = View.GONE
                fabButton.hide()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                val sharedPreference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                val editTextParticleApiUrl = findViewById<EditText>(R.id.particle_api_url)
                val editTextParticleDeviceId = findViewById<EditText>(R.id.particle_device_id)
                val editTextParticleTokenId = findViewById<EditText>(R.id.particle_token_id)
                val editTextFavouriteColor = findViewById<EditText>(R.id.favourite_color)
                val buttonFavouriteColor = findViewById<Button>(R.id.favourite_color_button)
                editTextParticleApiUrl.setText(sharedPreference.getString("particleApiUrl", "defaultName").toString())
                editTextParticleDeviceId.setText(
                    sharedPreference.getString(
                        "particleDeviceId",
                        "defaultName"
                    ).toString()
                )
                editTextParticleTokenId.setText(sharedPreference.getString("particleTokenId", "defaultName").toString())
                editTextFavouriteColor.setText(sharedPreference.getString("favouriteColor", "defaultName").toString())
                val favouriteColor = sharedPreference.getString("favouriteColor", "defaultName")
                if (favouriteColor != null && favouriteColor != "") {
                    val favouriteColor = Integer.parseInt(favouriteColor)
                    editTextFavouriteColor.setTextColor(favouriteColor)
                    buttonFavouriteColor.setBackgroundColor(favouriteColor)
                    if (isColorDark(favouriteColor)) {
                        buttonFavouriteColor.setTextColor(Color.WHITE)
                    } else {
                        buttonFavouriteColor.setTextColor(Color.BLACK)
                    }
                }
                colorPicker.visibility = View.GONE
                musicPlayer.visibility = View.GONE
                settingsForm.visibility = View.VISIBLE
                fabButton.hide()
                fabButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)))
                fabButton.setColorFilter(Color.WHITE)
                fabButton.setImageResource(R.drawable.ic_save_black)
                fabButton.show()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val colorPickerView: ColorPickerView = findViewById(R.id.colorPicker)
        colorPickerView.setInitialColor(Color.RED)

        val fabButton: FloatingActionButton = findViewById(R.id.fabButton)
        fabButton.setTag(R.id.light_status, "lightOff")
        fabButton.setTag(R.id.fab_icon_color, Color.BLACK)
        fabButton.setTag(R.id.fab_bg_color, Color.WHITE)
        colorPickerView.subscribe { color, _, _ ->
            if (isColorDark(color)) {
                fabButton.setColorFilter(Color.WHITE)
                fabButton.setTag(R.id.fab_icon_color, Color.WHITE)
            } else {
                fabButton.setColorFilter(Color.BLACK)
                fabButton.setTag(R.id.fab_icon_color, Color.BLACK)
            }
            fabButton.setBackgroundTintList(ColorStateList.valueOf(color))
            fabButton.setTag(R.id.fab_bg_color, color)
        }

        val previousButton: ImageButton = findViewById(R.id.previous_song_button)
        val previousButtonClickListener = View.OnClickListener { view ->
            when (view.getId()) {
                R.id.previous_song_button -> {
                    //QueryUtils.changeAudio("playPrevious,$currentVolume", view)
                    Snackbar.make(view, "Previous", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        previousButton.setOnClickListener(previousButtonClickListener)

        val playButton: ImageButton = findViewById(R.id.play_pause_song_button)
        val playButtonClickListener = View.OnClickListener { view ->
            when (view.getId()) {
                R.id.play_pause_song_button -> {
                    if (isPlaying) {
                        isPlaying = false
                        playButton.setImageResource(R.drawable.ic_pause_black)
                        QueryUtils.changeAudio("pause,$currentVolume", view)
                    } else {
                        isPlaying = true
                        playButton.setImageResource(R.drawable.ic_play_arrow_black)
                        QueryUtils.changeAudio("play,$currentVolume", view)
                    }
                    //Snackbar.make(view, "Play/Pause", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        playButton.setOnClickListener(playButtonClickListener)

        val nextButton: ImageButton = findViewById(R.id.next_song_button)
        val nextButtonClickListener = View.OnClickListener { view ->
            when (view.getId()) {
                R.id.next_song_button -> {
                    QueryUtils.changeAudio("playNext,$currentVolume", view)
//                    Snackbar.make(view, "Next", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        nextButton.setOnClickListener(nextButtonClickListener)

        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                currentVolume = i
                QueryUtils.changeAudio("setVolume,$i", findViewById(R.id.volumeSeekbar))
                Toast.makeText(applicationContext, "Volume: $i", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

        val shortClickListenerFab = View.OnClickListener { view ->
            when (view.id) {
                R.id.fabButton ->
                    if (getSelectedItem(navView).equals(getString(R.string.title_light))) {
                        // If pressed in Light section
                        val tagLightStatus = fabButton.getTag(R.id.light_status) as String
                        if (tagLightStatus.equals("lightOn")) {
                            // Prepare rgbString that will be sent to the lamp
                            val rgbString = "0,0,0,1000"
                            // Prepare a human readable colorSet string for debug
                            val colorSet = "Red: 0 Green: 0 Blue: 0"
                            // Send the http call to change color
                            QueryUtils.changeColor(rgbString, colorSet, findViewById(R.id.fabButton))
                            Snackbar.make(view, "Turning off...", Snackbar.LENGTH_SHORT).show()
                            fabButton.hide()
                            fabButton.setImageResource(R.drawable.ic_lightbulb_outline_black)
                            fabButton.show()
                            fabButton.setTag(R.id.light_status, "lightOff")
                        } else {
                            val chosenColor: Int = fabButton.getTag(R.id.fab_bg_color) as Int
                            val red = Color.red(chosenColor)
                            val green = Color.green(chosenColor)
                            val blue = Color.blue(chosenColor)
                            // Prepare rgbString that will be sent to the lamp
                            val rgbString = "$red,$green,$blue,1000"
                            // Prepare a human readable colorSet string for debug
                            val colorSet = "Red: $red Green: $green Blue: $blue"
                            // Send the http call to change color
                            QueryUtils.changeColor(rgbString, colorSet, findViewById(R.id.fabButton))
                            Snackbar.make(view, "Turning on...", Snackbar.LENGTH_SHORT).show()
                            fabButton.hide()
                            fabButton.setImageResource(R.drawable.ic_lightbulb_on_black)
                            fabButton.show()
                            fabButton.setTag(R.id.light_status, "lightOn")
                        }
                    } else if (getSelectedItem(navView).equals(getString(R.string.title_settings))) {
                        // If pressed in Settings section
                        Snackbar.make(view, "I'll keep those settings in mind :)", Snackbar.LENGTH_SHORT).show()
                        val sharedPreference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        var editTextParticleApiUrl = findViewById(R.id.particle_api_url) as EditText
                        var editTextParticleDeviceId = findViewById(R.id.particle_device_id) as EditText
                        var editTextParticleTokenId = findViewById(R.id.particle_token_id) as EditText
                        var editTextFavouriteColor = findViewById(R.id.favourite_color) as EditText
                        editor.putString("particleApiUrl", editTextParticleApiUrl.text.toString())
                        editor.putString("particleDeviceId", editTextParticleDeviceId.text.toString())
                        editor.putString("particleTokenId", editTextParticleTokenId.text.toString())
                        editor.putString("favouriteColor", editTextFavouriteColor.text.toString())
                        editor.apply()
                    }
            }
        }

        fabButton.setOnClickListener(shortClickListenerFab)

        fabButton.setOnLongClickListener{
            if (colorPicker.visibility == View.VISIBLE){
                Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show()
            }
            return@setOnLongClickListener true
        }

        val chooseFavouriteColorButton: Button = findViewById(R.id.favourite_color_button)
        val chooseFavouriteColorButtonListener = View.OnClickListener { view ->
            when (view.getId()) {
                R.id.favourite_color_button ->
                    ColorPickerPopup.Builder(this)
                        .initialColor(Color.WHITE) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(false) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                            override fun onColorPicked(color: Int) {
                                chooseFavouriteColorButton.setBackgroundColor(color)
                                if (isColorDark(color)) {
                                    chooseFavouriteColorButton.setTextColor(Color.WHITE)
                                } else {
                                    chooseFavouriteColorButton.setTextColor(Color.BLACK)
                                }

                                val chooseFavouriteColorEditText: EditText = findViewById(R.id.favourite_color)
                                chooseFavouriteColorEditText.setText(color.toString())
                            }

                            fun onColor(color: Int, fromUser: Boolean) {

                            }
                        })
            }
        }
        chooseFavouriteColorButton.setOnClickListener(chooseFavouriteColorButtonListener)

    }


}
