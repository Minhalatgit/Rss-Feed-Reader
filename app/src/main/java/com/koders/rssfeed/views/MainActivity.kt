package com.koders.rssfeed.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.koders.rssfeed.R
import com.koders.rssfeed.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var fanAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("MainActivity", "Firebase database failed ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseResponse = JSONObject(snapshot.value as Map<*, *>)
                Log.d("MainActivity", firebaseResponse.toString())

                initAds(
                    firebaseResponse.getString("fan_placement_id"),
                    firebaseResponse.getString("admob_id"),
                    firebaseResponse.getString("ad")
                )
            }
        })

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

    private fun initAds(fanId: String, adMobId: String, ad: String) {
        Log.d("MainActivity", "Init ads with $ad")

        if (ad.equals("fan", true)) {
            Log.d("MainActivity", "FAN ads")
            // Fb ads
            // TODO: Need to replace with placement id with original id
            fanAdView = AdView(this, fanId, AdSize.BANNER_HEIGHT_50)
            binding.fbAddBanner.removeAllViews()
            binding.fbAddBanner.addView(fanAdView)
            fanAdView.loadAd()

            binding.fbAddBanner.visibility = View.VISIBLE
            binding.adMobView.visibility = View.GONE
        } else if (ad.equals("adMob", true)) {
            Log.d("MainActivity", "AdMob ads")
            // AdMob ads
            // TODO: Need to replace with unit id with original id
            val adRequest = AdRequest.Builder().build()
            val adMobView = com.google.android.gms.ads.AdView(this)
            adMobView.adSize = com.google.android.gms.ads.AdSize.BANNER
            adMobView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            binding.adMobView.removeAllViews()
            binding.adMobView.addView(adMobView)
            adMobView.loadAd(adRequest)

            InterstitialAd(this).apply {
                adUnitId = "ca-app-pub-3940256099942544/1033173712"
                loadAd(adRequest)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        Log.d("MainActivity", "onAdLoaded")
                        show()
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                        Log.d("MainActivity", "onAdFailedToLoad")
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                        Log.d("MainActivity", "onAdOpened")
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                        Log.d("MainActivity", "onAdClicked")
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                        Log.d("MainActivity", "onAdLeftApplication")
                    }

                    override fun onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        Log.d("MainActivity", "onAdClosed")
                    }
                }
            }

            binding.adMobView.visibility = View.VISIBLE
            binding.fbAddBanner.visibility = View.GONE
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
        if (this::fanAdView.isInitialized) {
            fanAdView.destroy()
        }
        super.onDestroy()
    }
}