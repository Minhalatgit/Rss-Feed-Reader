package com.koders.rssfeed.views

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.facebook.ads.*
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
import com.koders.rssfeed.addLimit
import com.koders.rssfeed.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var fanAdView: AdView
    private lateinit var fanInterstitial: com.facebook.ads.InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        centerTitle()

//        getFirebaseDataForAds()

        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        navView.menu.findItem(R.id.rateUs).setOnMenuItemClickListener {
            showAlert()
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return@setOnMenuItemClickListener true
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            addLimit++
            if (addLimit > 4) {
                getFirebaseDataForAds()
                Log.d("AddCount", "Add limit value set to $addLimit")
                addLimit = 0
            }
        }
    }

    public fun getFirebaseDataForAds() {
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("MainActivity", "Firebase database failed ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseResponse = JSONObject(snapshot.value as Map<*, *>)
                Log.d("MainActivity", firebaseResponse.toString())

                initAds(
                    firebaseResponse.getString("fan_placement_id"),
                    firebaseResponse.getString("fan_placement_id_interstitial_android"),
                    firebaseResponse.getString("admob_id"),
                    firebaseResponse.getString("admob_int"),
                    firebaseResponse.getString("ad")
                )
            }
        })
    }

    private fun initAds(
        fanId: String,
        fanIntId: String,
        adMobId: String,
        adMobInt: String,
        ad: String
    ) {
        Log.d("MainActivity", "Init ads with $ad")

        if (ad.equals("fan", true)) {
            fanAdView = AdView(this, fanId, AdSize.BANNER_HEIGHT_50)
            binding.fbAddBanner.removeAllViews()
            binding.fbAddBanner.addView(fanAdView)
            fanAdView.loadAd(
                fanAdView.buildLoadAdConfig().withAdListener(object : com.facebook.ads.AdListener {
                    override fun onAdClicked(p0: Ad?) {
                    }

                    override fun onError(p0: Ad?, p1: AdError?) {
                        Log.d("MainActivity", "onError: FAN banner ${p1?.errorMessage}")
                    }

                    override fun onAdLoaded(p0: Ad?) {
                    }

                    override fun onLoggingImpression(p0: Ad?) {
                    }

                }).build()
            )

            fanInterstitial = InterstitialAd(this, fanIntId)
            val interstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(p0: Ad?) {

                }

                override fun onAdClicked(p0: Ad?) {
                    Log.d("MainActivity", "onAdClicked $p0")
                }

                override fun onInterstitialDismissed(p0: Ad?) {
                    Log.d("MainActivity", "onInterstitialDismissed: ")
                }

                override fun onError(p0: Ad?, p1: AdError?) {
                    Log.d("MainActivity", "onError: FAN Interstitial ")
                }

                override fun onAdLoaded(p0: Ad?) {
                    Log.d("MainActivity", "onAdLoaded: ")
                    fanInterstitial.show();
                }

                override fun onLoggingImpression(p0: Ad?) {
                    Log.d("MainActivity", "onLoggingImpression: ")
                }
            }

            fanInterstitial.loadAd(
                fanInterstitial.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            )

            binding.fbAddBanner.visibility = View.VISIBLE
            binding.adMobView.visibility = View.GONE
        } else if (ad.equals("adMob", true)) {
            val adRequest = AdRequest.Builder().build()
            val adMobView = com.google.android.gms.ads.AdView(this)
            adMobView.adSize = com.google.android.gms.ads.AdSize.BANNER
            adMobView.adUnitId = adMobId
            binding.adMobView.removeAllViews()
            binding.adMobView.addView(adMobView)
            adMobView.loadAd(adRequest)

            InterstitialAd(this).apply {
                adUnitId = adMobInt
                loadAd(adRequest)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("MainActivity", "onAdLoaded")
                        show()
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("MainActivity", "onAdFailedToLoad ${adError.message}")
                    }

                    override fun onAdOpened() {
                        Log.d("MainActivity", "onAdOpened")
                    }

                    override fun onAdClicked() {
                        Log.d("MainActivity", "onAdClicked")
                    }

                    override fun onAdLeftApplication() {
                        Log.d("MainActivity", "onAdLeftApplication")
                    }

                    override fun onAdClosed() {
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
        builder.setMessage("If you enjoy using the app, would you mind taking a moment to rate it? It won't take more than a minute. Thank you for your support!")
        builder.setIcon(R.drawable.rss)

        builder.setNeutralButton("Later") { dialog, _ ->
            dialog.cancel()
        }
        builder.setPositiveButton("Rate now") { _, _ ->
            Toast.makeText(
                applicationContext,
                "It will open Play store when live",
                Toast.LENGTH_LONG
            ).show()
        }
        builder.setNegativeButton("No, Thanks") { dialog, _ ->
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
        if (this::fanInterstitial.isInitialized) {
            fanInterstitial.destroy();
        }
        super.onDestroy()
    }

    private fun centerToolbarText() {
        val mTitleTextView = AppCompatTextView(this)
        mTitleTextView.text = "Printable Coupons"
        mTitleTextView.setSingleLine()
        val layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        supportActionBar?.setCustomView(mTitleTextView, layoutParams)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
    }

    private fun centerTitle() {
        val textViews: ArrayList<View> = ArrayList()
        window.decorView
            .findViewsWithText(textViews, title, View.FIND_VIEWS_WITH_TEXT)
        if (textViews.size > 0) {
            var appCompatTextView: AppCompatTextView? = null
            if (textViews.size == 1) {
                appCompatTextView = textViews[0] as AppCompatTextView
            } else {
                for (v in textViews) {
                    if (v.parent is Toolbar) {
                        appCompatTextView = v as AppCompatTextView
                        break
                    }
                }
            }
            if (appCompatTextView != null) {
                val params: ViewGroup.LayoutParams = appCompatTextView.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                appCompatTextView.layoutParams = params
                appCompatTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        }
    }
}