package it.davidenastri.littlecloud

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.davidenastri.littlecloud.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentVolume = 0
    private var isPlaying = false
    private var lastFabButtonClickTime: Long = 0
    private val debounceInterval: Long = 1000

    private fun getSelectedItem(bottomNavigationView: BottomNavigationView): String {
        val menu = bottomNavigationView.menu
        for (i in 0 until menu.size) {
            val menuItem = menu[i]
            if (menuItem.isChecked) {
                Log.d("SelectedItem", "Selected: ${menuItem.title}")
                return menuItem.title.toString()
            }
        }
        return "none"
    }

    private fun isColorDark(color: Int): Boolean {
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
        val sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        with(binding) {
            val defaultApiUrl = getString(R.string.particle_api_url)
            val defaultDeviceId = getString(R.string.particle_device_id)
            val defaultTokenId = getString(R.string.particle_token_id)
            val defaultFavouriteColor = getString(R.string.favourite_color)

            particleApiUrl.setText(sharedPreferences.getString("particle_api_url", ""))
            particleDeviceId.setText(sharedPreferences.getString("particle_device_id", ""))
            particleTokenId.setText(sharedPreferences.getString("particle_token_id", ""))
            favouriteColor.setText(sharedPreferences.getString("favourite_color", ""))

            setupField(particleApiUrl, defaultApiUrl)
            setupField(particleDeviceId, defaultDeviceId)
            setupField(particleTokenId, defaultTokenId)
            setupField(favouriteColor, defaultFavouriteColor)

            val favouriteColorValue = sharedPreferences.getString("favourite_color", "")
            favouriteColorValue?.let {
                if (it.all { char -> char.isDigit() }) {
                    val favouriteColorInt = it.toInt()
                    favouriteColor.setTextColor(favouriteColorInt)
                    favouriteColorButton.setBackgroundColor(favouriteColorInt)
                    favouriteColorButton.setTextColor(if (isColorDark(favouriteColorInt)) Color.WHITE else Color.BLACK)
                }
            }

            colorPicker.visibility = View.GONE
            musicPlayer.visibility = View.GONE
            settingsForm.visibility = View.VISIBLE

            fabButton.apply {
                hide()
                backgroundTintList = ColorStateList.valueOf(resources.getColor(android.R.color.holo_green_dark))
                setColorFilter(Color.WHITE)
                setImageResource(R.drawable.ic_save_black)
                setOnClickListener { handleFabButtonClick(it) }
                show()
            }
        }
    }

    /**
     * Sets up EditText behavior for the settings screen.
     * - When focused: if the text is default or empty, clears text and sets color to black.
     * - When focus is lost: if the text is empty, resets to default text and sets color to gray.
     */
    private fun setupField(editText: EditText, defaultText: String) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editText.text.toString() == defaultText || editText.text.isEmpty()) {
                    editText.setText("")
                    editText.setTextColor(Color.BLACK)
                }
            } else {
                if (editText.text.isEmpty()) {
                    editText.setText(defaultText)
                    editText.setTextColor(Color.GRAY)
                }
            }
        }
    }

    private fun updateTextColor(editText: EditText, defaultText: String) {
        editText.setTextColor(if (editText.text.toString() == defaultText || editText.text.isEmpty()) Color.GRAY else Color.BLACK)
    }

    /**
     * Handles navigation for music settings.
     * - Hides color picker and settings form.
     * - Shows music player.
     * - Hides FAB button.
     */
    private fun handleMusicNavigation() {
        // Hide color picker and settings form
        binding.colorPicker.visibility = View.GONE
        binding.settingsForm.visibility = View.GONE

        // Show music player
        binding.musicPlayer.visibility = View.VISIBLE

        // Hide FAB button
        binding.fabButton.hide()
    }


    /**
     * Handles navigation for light settings.
     * - Shows color picker and hides music player and settings form.
     * - Sets FAB button background and icon colors based on tags.
     * - If light is on, updates FAB button to show "light on" icon.
     * - If light is off, updates FAB button to show "light off" icon.
     */
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
            binding.fabButton.backgroundTintList = ColorStateList.valueOf(color)
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
            if (binding.colorPicker.isVisible) {
                Toast.makeText(this, "Long click detected", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun handleFabButtonClick(view: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFabButtonClickTime < this.debounceInterval) {
            Toast.makeText(view.context, "Please wait...", Toast.LENGTH_SHORT).show()
            return
        }
        lastFabButtonClickTime = currentTime

        val selectedItem = getSelectedItem(binding.navView)
        Log.d("handleFabButtonClick", "Selected item: $selectedItem")

        when (selectedItem) {
            getString(R.string.title_light) -> {
                Log.d("handleFabButtonClick", "Light screen active")
                val tagLightStatus = binding.fabButton.getTag(R.id.light_status) as String
                if (tagLightStatus == "lightOn") {
                    val rgbString = "0,0,0,1000"
                    val colorSet = "Red: 0 Green: 0 Blue: 0"
                    Toast.makeText(view.context, "Turning off...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(view.context, "Turning on...", Toast.LENGTH_SHORT).show()
                    binding.fabButton.hide()
                    binding.fabButton.setImageResource(R.drawable.ic_lightbulb_on_black)
                    binding.fabButton.show()
                    binding.fabButton.setTag(R.id.light_status, "lightOn")
                }
            }
            getString(R.string.title_settings) -> {
                Log.d("handleFabButtonClick", "Settings screen active")
                Toast.makeText(view.context, "I'll keep those settings in mind :)", Toast.LENGTH_SHORT).show()
                val sharedPreference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("particleApiUrl", binding.particleApiUrl.text.toString())
                editor.putString("particleDeviceId", binding.particleDeviceId.text.toString())
                editor.putString("particleTokenId", binding.particleTokenId.text.toString())
                editor.putString("favouriteColor", binding.favouriteColor.text.toString())
                editor.apply()
            }
            else -> {
                Log.d("handleFabButtonClick", "Unknown screen active")
                Toast.makeText(view.context, "Unknown screen", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setupMusicControls() {
        binding.previousSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.previous_song_button -> {
                    Toast.makeText(view.context, "Previous", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.playPauseSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.play_pause_song_button -> {
                    if (isPlaying) {
                        isPlaying = false
                        binding.playPauseSongButton.setImageResource(R.drawable.ic_pause_black)
                        //QueryUtils.changeAudio("pause,$currentVolume", view)
                    } else {
                        isPlaying = true
                        binding.playPauseSongButton.setImageResource(R.drawable.ic_play_arrow_black)
                        //QueryUtils.changeAudio("play,$currentVolume", view)
                    }
                }
            }
        }

        binding.nextSongButton.setOnClickListener { view ->
            when (view.id) {
                R.id.next_song_button -> {
                    //QueryUtils.changeAudio("playNext,$currentVolume", view)
                }
            }
        }
    }

    private fun setupVolumeSeekbar() {
        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                currentVolume = i
                // QueryUtils.changeAudio("setVolume,$i", binding.volumeSeekbar)
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
