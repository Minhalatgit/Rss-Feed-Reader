package com.koders.rssfeed.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.koders.rssfeed.R
import com.koders.rssfeed.databinding.ActivityMainBinding
import com.facebook.ads.*
import com.google.android.gms.ads.AdRequest


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
                DataBindingUtil.setContentView<ActivityMainBinding>(
                        this,
                        R.layout.activity_main
                )

        // Fb ads
        // TODO: Need to replace with placement id with original id
        adView = AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50)
        binding.fbAddBanner.addView(adView)
        adView.loadAd()

        // AdMob ads
        // TODO: Need to replace with unit id with original id
        val adRequest = AdRequest.Builder().build()
        binding.adMobView.loadAd(adRequest)
        //binding.adMobView.visibility = View.GONE

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        navView.menu.findItem(R.id.contact).setOnMenuItemClickListener {
            Toast.makeText(applicationContext, "Contact", Toast.LENGTH_SHORT).show()
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return@setOnMenuItemClickListener true
        }

        navView.menu.findItem(R.id.rateUs).setOnMenuItemClickListener {

            showAlert()
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rate this app")
        builder.setMessage("Are you enjoying our app? Please give us a review.")
        builder.setIcon(R.drawable.rss)

        builder.setPositiveButton("Rate now") { _, _ ->
            Toast.makeText(
                    applicationContext,
                    "It will open Play store when live",
                    Toast.LENGTH_LONG
            ).show()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}