package it.davidenastri.littlecloud

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_light -> {
                textMessage.setText(R.string.title_light)
                colorPicker.visibility = View.VISIBLE;
                settingsForm.visibility = View.GONE;
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_music -> {
                textMessage.setText(R.string.title_music)
                colorPicker.visibility = View.GONE;
                settingsForm.visibility = View.GONE;
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                textMessage.setText(R.string.title_settings)
                colorPicker.visibility = View.GONE;
                settingsForm.visibility = View.VISIBLE;
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}
