package com.koders.rssfeed

import android.app.Application
import android.util.Log
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal

class RssFeedApplication : Application() {

    private val ONESIGNAL_APP_ID = "7348e9d0-d11b-497d-8d8d-83189ea17d32"

    override fun onCreate() {
        super.onCreate()
        AudienceNetworkAds.initialize(this)
        MobileAds.initialize(this)
        //test ad enable for my device
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(
                mutableListOf("74B265AA1B8C63133239D7C1844D480A")
            ).build()
        )

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        OneSignal.setNotificationWillShowInForegroundHandler {
            Log.d("RssFeedApplication", it.toString())
            it.complete(it.notification)
        }

        FirebaseApp.initializeApp(this)
    }
}