package com.free.printable.coupons.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.amazon.device.ads.AdLayout
import com.amazon.device.ads.AdProperties
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
import com.free.printable.coupons.addLimit
import com.free.printable.coupons.databinding.ActivityMainBinding
import hotchemi.android.rate.AppRate
import org.json.JSONObject
import com.free.printable.coupons.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var fanAdView: AdView
    private lateinit var fanInterstitial: com.facebook.ads.InterstitialAd

    private lateinit var amazonAdView: AdLayout
    private lateinit var amazonInterstitial: com.amazon.device.ads.InterstitialAd

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        centerTitle()

        AppRate.with(this)
            .setInstallDays(1)
            .setLaunchTimes(3)
            .setRemindInterval(2)
            .monitor()

        AppRate.showRateDialogIfMeetsConditions(this)

        FirebaseDatabase.getInstance().reference.child("ad")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange AdType for banner: ${snapshot.value}")
                    // initialize banner ads
                    initBannerAds(snapshot.value as String)
                }

            })

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
                initInterstitialAds()
                Log.d("AddCount", "Add limit value set to $addLimit")
                addLimit = 0
            }
        }
    }

    fun initInterstitialAds() {
        FirebaseDatabase.getInstance().reference.child("ad")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Firebase database failed ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange AdType for interstitial ads: ${snapshot.value}")
                    val adType = snapshot.value as String;

                    // got ad type here
                    //getting interstitial ad ids
                    FirebaseDatabase.getInstance().reference.child("interstitial_android")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                Log.d(TAG, "onCancelled: ${error.message}")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val interstitialAdId = JSONObject(snapshot.value as Map<*, *>)
                                Log.d(TAG, "onDataChange Interstitial ad ids: $interstitialAdId")

                                if (adType.equals("fan", true)) {
                                    fanInterstitial = InterstitialAd(
                                        this@MainActivity,
                                        interstitialAdId.getString("fan")
                                    )
                                    val interstitialAdListener = object : InterstitialAdListener {
                                        override fun onInterstitialDisplayed(p0: Ad?) {

                                        }

                                        override fun onAdClicked(p0: Ad?) {
                                            Log.d(TAG, "onAdClicked $p0")
                                        }

                                        override fun onInterstitialDismissed(p0: Ad?) {
                                            Log.d(TAG, "onInterstitialDismissed: ")
                                        }

                                        override fun onError(p0: Ad?, p1: AdError?) {
                                            Log.d(TAG, "onError: FAN Interstitial ")
                                        }

                                        override fun onAdLoaded(p0: Ad?) {
                                            Log.d(TAG, "onAdLoaded: ")
                                            fanInterstitial.show();
                                        }

                                        override fun onLoggingImpression(p0: Ad?) {
                                            Log.d(TAG, "onLoggingImpression: ")
                                        }
                                    }

                                    fanInterstitial.loadAd(
                                        fanInterstitial.buildLoadAdConfig()
                                            .withAdListener(interstitialAdListener)
                                            .build()
                                    )

                                } else if (adType.equals("amazon", true)) {
                                    // amazon interstitial ads will setup here
                                    amazonInterstitial =
                                        com.amazon.device.ads.InterstitialAd(this@MainActivity)
                                    amazonInterstitial.setListener(object :
                                        com.amazon.device.ads.AdListener {
                                        override fun onAdFailedToLoad(
                                            p0: com.amazon.device.ads.Ad?,
                                            p1: com.amazon.device.ads.AdError?
                                        ) {
                                            Log.d(TAG, "onAdFailedToLoad Amz: ${p1?.message}")
                                        }

                                        override fun onAdExpanded(p0: com.amazon.device.ads.Ad?) {
                                            Log.d(TAG, "onAdExpanded: ")
                                        }

                                        override fun onAdDismissed(p0: com.amazon.device.ads.Ad?) {
                                            Log.d(TAG, "onAdDismissed: ")
                                        }

                                        override fun onAdCollapsed(p0: com.amazon.device.ads.Ad?) {
                                            Log.d(TAG, "onAdCollapsed: ")
                                        }

                                        override fun onAdLoaded(
                                            p0: com.amazon.device.ads.Ad?,
                                            p1: AdProperties?
                                        ) {
                                            Log.d(TAG, "onAdLoaded: ")
                                        }

                                    })
                                    amazonInterstitial.loadAd()
                                } else {
                                    // else admob will show
                                    val adRequest = AdRequest.Builder().build()
                                    InterstitialAd(this@MainActivity).apply {
                                        adUnitId = interstitialAdId.getString("admob")
                                        loadAd(adRequest)
                                        adListener = object : AdListener() {
                                            override fun onAdLoaded() {
                                                Log.d(TAG, "onAdLoaded")
                                                show()
                                            }

                                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                                Log.d(
                                                    TAG, "onAdFailedToLoad ${adError.message}"
                                                )
                                            }

                                            override fun onAdOpened() {
                                                Log.d(TAG, "onAdOpened")
                                            }

                                            override fun onAdClicked() {
                                                Log.d(TAG, "onAdClicked")
                                            }

                                            override fun onAdLeftApplication() {
                                                Log.d(TAG, "onAdLeftApplication")
                                            }

                                            override fun onAdClosed() {
                                                Log.d(TAG, "onAdClosed")
                                            }
                                        }
                                    }
                                }
                            }
                        })
                }
            })
    }

    private fun initBannerAds(adType: String) {
        //getting banner ad ids
        FirebaseDatabase.getInstance().reference.child("banner_android")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val bannerIds = JSONObject(snapshot.value as Map<*, *>)
                    Log.d(TAG, "onDataChange Banner ad ids: $bannerIds")
                    //check for ad type and show accordingly

                    if (adType.equals("fan", true)) {
                        fanAdView =
                            AdView(
                                this@MainActivity,
                                bannerIds.getString("fan"),
                                AdSize.BANNER_HEIGHT_50
                            )
                        binding.fbAddBanner.removeAllViews()
                        binding.fbAddBanner.addView(fanAdView)
                        fanAdView.loadAd(
                            fanAdView.buildLoadAdConfig()
                                .withAdListener(object : com.facebook.ads.AdListener {
                                    override fun onAdClicked(p0: Ad?) {
                                    }

                                    override fun onError(p0: Ad?, p1: AdError?) {
                                        Log.d(
                                            "MainActivity",
                                            "onError: FAN banner ${p1?.errorMessage}"
                                        )
                                    }

                                    override fun onAdLoaded(p0: Ad?) {
                                    }

                                    override fun onLoggingImpression(p0: Ad?) {
                                    }

                                }).build()
                        )
                        binding.fbAddBanner.visibility = View.VISIBLE
                        binding.adMobView.visibility = View.GONE
                        binding.amazonAddBanner.visibility = View.GONE
                    } else if (adType.equals("amazon", true)) {
                        // amazon banner ad work will go here
                        amazonAdView = AdLayout(
                            this@MainActivity
                        )
                        amazonAdView.timeout = 20000
                        binding.amazonAddBanner.removeAllViews()

                        binding.amazonAddBanner.addView(amazonAdView)
                        val amazonLoadAdSuccess = amazonAdView.loadAd()

                        Log.d(TAG, "Amazon load ad $amazonLoadAdSuccess")
                        Log.d(TAG, "Amazon ad loading ${amazonAdView.isLoading}")
                        Log.d(TAG, "Amazon ad showing ${amazonAdView.isShowing}")

                        binding.amazonAddBanner.visibility = View.VISIBLE
                        binding.adMobView.visibility = View.GONE
                        binding.fbAddBanner.visibility = View.GONE
                    } else {
                        //else admob will show
                        val adRequest = AdRequest.Builder().build()
                        val adMobView = com.google.android.gms.ads.AdView(this@MainActivity)
                        adMobView.adSize = com.google.android.gms.ads.AdSize.BANNER
                        adMobView.adUnitId = bannerIds.getString("admob")
                        binding.adMobView.removeAllViews()
                        binding.adMobView.addView(adMobView)
                        adMobView.loadAd(adRequest)

                        binding.adMobView.visibility = View.VISIBLE
                        binding.fbAddBanner.visibility = View.GONE
                        binding.amazonAddBanner.visibility = View.GONE
                    }
                }

            })
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
        if (this::fanInterstitial.isInitialized) {
            fanInterstitial.destroy();
        }
        if (this::amazonAdView.isInitialized) {
            amazonAdView.destroy();
        }
        super.onDestroy()
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