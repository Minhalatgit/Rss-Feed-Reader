package com.free.printable.coupons.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.amazon.device.ads.AdRegistration
import com.free.printable.coupons.PreferenceManager
import com.free.printable.coupons.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Amazon ads
        AdRegistration.setAppKey("2020b0e3aeea4c908162f3a9b43787f1")
//        AdRegistration.enableTesting(true)
        AdRegistration.enableLogging(true)

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