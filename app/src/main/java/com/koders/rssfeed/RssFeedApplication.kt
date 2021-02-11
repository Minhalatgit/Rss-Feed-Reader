package com.koders.rssfeed

import android.app.Application
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds

class RssFeedApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
    }
}