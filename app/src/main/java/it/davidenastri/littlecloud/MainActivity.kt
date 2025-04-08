package it.davidenastri.littlecloud

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import it.davidenastri.littlecloud.databinding.ActivityMainBinding
import androidx.core.view.size
import androidx.core.view.get


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentVolume = 0
    private var isPlaying = false

    private fun getSelectedItem(bottomNavigationView: BottomNavigationView): String {
        val menu = bottomNavigationView.menu
        for (i in 0 until bottomNavigationView.menu.size) {
            val menuItem = menu[i]
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
                handleLightNavigation()
                true
            }
            R.id.navigation_music -> {
                handleMusicNavigation()
                true
            }
            R.id.navigation_settings -> {
                handleSettingsNavigation()
                true
            }
            else -> false
        }
    }


    private fun handleSettingsNavigation() {
        val sharedPreference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        binding.particleApiUrl.setText(sharedPreference.getString("particleApiUrl", "defaultName").toString())
        binding.particleDeviceId.setText(sharedPreference.getString("particleDeviceId", "defaultName").toString())
        binding.particleTokenId.setText(sharedPreference.getString("particleTokenId", "defaultName").toString())
        binding.favouriteColor.setText(sharedPreference.getString("favouriteColor", "defaultName").toString())
        val favouriteColor = sharedPreference.getString("favouriteColor", "defaultName")
        if (favouriteColor != null && favouriteColor != "") {
            val favouriteColorInt = Integer.parseInt(favouriteColor)
            binding.favouriteColor.setTextColor(favouriteColorInt)
            binding.favouriteColorButton.setBackgroundColor(favouriteColorInt)
            if (isColorDark(favouriteColorInt)) {
                binding.favouriteColorButton.setTextColor(Color.WHITE)
            } else {
                binding.favouriteColorButton.setTextColor(Color.BLACK)
            }
        }
        binding.colorPicker.visibility = View.GONE
        binding.musicPlayer.visibility = View.GONE
        binding.settingsForm.visibility = View.VISIBLE
        binding.fabButton.hide()
        binding.fabButton.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(android.R.color.holo_green_dark)))
        binding.fabButton.setColorFilter(Color.WHITE)
        binding.fabButton.setImageResource(R.drawable.ic_save_black)
        binding.fabButton.show()
    }

    private fun handleMusicNavigation() {
        binding.colorPicker.visibility = View.GONE
        binding.musicPlayer.visibility = View.VISIBLE
        binding.settingsForm.visibility = View.GONE
        binding.fabButton.hide()
    }

    private fun handleLightNavigation() {
        binding.colorPicker.visibility = View.VISIBLE
        binding.musicPlayer.visibility = View.GONE
        binding.settingsForm.visibility = View.GONE
        val tagLightStatus = binding.fabButton.getTag(R.id.light_status)
        val tagFabBgColor = binding.fabButton.getTag(R.id.fab_bg_color) as Int
        val tagFabIconColor = binding.fabButton.getTag(R.id.fab_icon_color) as Int
        binding.fabButton.backgroundTintList = ColorStateList.valueOf(tagFabBgColor)
        binding.fabButton.setColorFilter(tagFabIconColor)
        if (tagLightStatus == "lightOn") {
            binding.fabButton.hide()
            binding.fabButton.setImageResource(R.drawable.ic_lightbulb_on_black)
            binding.fabButton.show()
        } else {
            binding.fabButton.hide()
            binding.fabButton.setImageResource(R.drawable.ic_lightbulb_outline_black)
            binding.fabButton.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupColorPicker()
        setupFabButton()
        setupMusicControls()
        setupVolumeSeekbar()
    }

    private fun setupNavigation() {
        binding.navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun setupColorPicker() {
        binding.colorPicker.setInitialColor(Color.RED)
        binding.colorPicker.subscribe { color, _, _ ->
            if (isColorDark(color)) {
                binding.fabButton.setColorFilter(Color.WHITE)
                binding.fabButton.setTag(R.id.fab_icon_color, Color.WHITE)
            } else {
                binding.fabButton.setColorFilter(Color.BLACK)
                binding.fabButton.setTag(R.id.fab_icon_color, Color.BLACK)
            }
            binding.fabButton.setBackgroundTintList(ColorStateList.valueOf(color))
            binding.fabButton.setTag(R.id.fab_bg_color, color)
        }
    }

    private fun setupFabButton() {
        binding.fabButton.setTag(R.id.light_status, "lightOff")
        binding.fabButton.setTag(R.id.fab_icon_color, Color.BLACK)
        binding.fabButton.setTag(R.id.fab_bg_color, Color.WHITE)

        binding.fabButton.setOnClickListener { view ->
            when (view.id) {
                R.id.fabButton -> handleFabButtonClick(view)
            }
        }

        binding.fabButton.setOnLongClickListener {
            if (binding.colorPicker.visibility == View.VISIBLE) {
                Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun handleFabButtonClick(view: View) {
        if (getSelectedItem(binding.navView) == getString(R.string.title_light)) {
            val tagLightStatus = binding.fabButton.getTag(R.id.light_status) as String
            if (tagLightStatus == "lightOn") {
                val rgbString = "0,0,0,1000"
                val colorSet = "Red: 0 Green: 0 Blue: 0"
                QueryUtils.changeColor(rgbString, colorSet, binding.fabButton)
                Snackbar.make(view, "Turning off...", Snackbar.LENGTH_SHORT).show()
                binding.fabButton.hide()
                binding.fabButton.setImageResource(R.drawable.ic_lightbulb_outline_black)
                binding.fabButton.show()
                binding.fabButton.setTag(R.id.light_status, "lightOff")
            } else {
                val chosenColor: Int = binding.fabButton.getTag(R.id.fab_bg_color) as Int
                val red = Color.red(chosenColor)
                val green = Color.green(chosenColor)
                val blue = Color.blue(chosenColor)
                val rgbString = "$red,$green,$blue,1000"
                val colorSet = "Red: $red Green: $green Blue: $blue"
                QueryUtils.changeColor(rgbString, colorSet, binding.fabButton)
                Snackbar.make(view, "Turning on...", Snackbar.LENGTH_SHORT).show()
                binding.fabButton.hide()
                binding.fabButton.setImageResource(R.drawable.ic_lightbulb_on_black)
                binding.fabButton.show()
                binding.fabButton.setTag(R.id.light_status, "lightOn")
            }
        } else if (getSelectedItem(binding.navView) == getString(R.string.title_settings)) {
            Snackbar.make(view, "I'll keep those settings in mind :)", Snackbar.LENGTH_SHORT).show()
            val sharedPreference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString("particleApiUrl", binding.particleApiUrl.text.toString())
            editor.putString("particleDeviceId", binding.particleDeviceId.text.toString())
            editor.putString("particleTokenId", binding.particleTokenId.text.toString())
            editor.putString("favouriteColor", binding.favouriteColor.text.toString())
            editor.apply()
        }
    }

    private fun setupMusicControls() {
        binding.previousSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.previous_song_button -> {
                    Snackbar.make(view, "Previous", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.playPauseSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.play_pause_song_button -> {
                    if (isPlaying) {
                        isPlaying = false
                        binding.playPauseSongButton.setImageResource(R.drawable.ic_pause_black)
                        QueryUtils.changeAudio("pause,$currentVolume", view)
                    } else {
                        isPlaying = true
                        binding.playPauseSongButton.setImageResource(R.drawable.ic_play_arrow_black)
                        QueryUtils.changeAudio("play,$currentVolume", view)
                    }
                }
            }
        }

        binding.nextSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.next_song_button -> {
                    QueryUtils.changeAudio("playNext,$currentVolume", view)
                }
            }
        }
    }

    private fun setupVolumeSeekbar() {
        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                currentVolume = i
                QueryUtils.changeAudio("setVolume,$i", binding.volumeSeekbar)
                Toast.makeText(applicationContext, "Volume: $i", Toast.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
    }

}
