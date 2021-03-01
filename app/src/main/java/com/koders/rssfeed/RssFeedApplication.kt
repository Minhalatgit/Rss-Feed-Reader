package com.koders.rssfeed

import android.app.Application
import android.util.Log
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.onesignal.OneSignal

class RssFeedApplication : Application() {

    private val ONESIGNAL_APP_ID = "7348e9d0-d11b-497d-8d8d-83189ea17d32"

    override fun onCreate() {
        super.onCreate()
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        OneSignal.setNotificationWillShowInForegroundHandler {
            Log.d("RssFeedApplication", it.toString())
            it.complete(it.notification)
        }
    }
}