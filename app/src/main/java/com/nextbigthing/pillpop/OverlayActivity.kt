package com.nextbigthing.pillpop

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nextbigthing.pillpop.databinding.ActivityOverlayBinding

class OverlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Make the activity full-screen and non-dismissible
        this.window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        // Close the activity when a button is clicked (optional)
        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressedDispatcher.onBackPressed()
        // Optional: Disable the back button
    }
}