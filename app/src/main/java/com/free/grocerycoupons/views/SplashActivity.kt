package com.free.grocerycoupons.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.free.grocerycoupons.PreferenceManager
import com.free.grocerycoupons.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val intent: Intent? = if (PreferenceManager.getIsFirstLaunch()) {
                PreferenceManager.setIsFirstLaunch(false)
                Intent(this, TutorialActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}