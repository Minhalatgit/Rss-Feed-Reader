package com.free.printable.coupons

import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal

class RssFeedApplication : Application() {

    private val ONESIGNAL_APP_ID = "7348e9d0-d11b-497d-8d8d-83189ea17d32"

    override fun onCreate() {
        super.onCreate()

        instance = this

        //fan ads
        AudienceNetworkAds.initialize(this)
        //admob ads
        MobileAds.initialize(this)

        //RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("74B265AA1B8C63133239D7C1844D480A"))

        //fan test ad
        AdSettings.setTestMode(true)

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        OneSignal.setNotificationWillShowInForegroundHandler {
            Log.d("RssFeedApplication", it.toString())
            it.complete(it.notification)
        }

        FirebaseApp.initializeApp(this)
    }

    companion object {
        lateinit var instance: RssFeedApplication
        fun getCtx(): Context {
            return instance.applicationContext
        }
    }
}