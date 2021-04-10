package com.free.grocerycoupons

import android.app.Application
import android.content.Context
import android.util.Log
import com.amazon.device.ads.AdRegistration
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal

class RssFeedApplication : Application() {

    private val ONESIGNAL_APP_ID = "7348e9d0-d11b-497d-8d8d-83189ea17d32"

    override fun onCreate() {
        super.onCreate()

        instance = this

        //Amazon ads
        AdRegistration.setAppKey("0123456789ABCDEF0123456789ABCDEF");

        //fan ads
        AudienceNetworkAds.initialize(this)
        //admob ads
        MobileAds.initialize(this)
        //test ad enable for my device(admob)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(
                mutableListOf("74B265AA1B8C63133239D7C1844D480A")
            ).build()
        )
        //fan test ad
        AdSettings.addTestDevice("ad1dd237-8917-4dd3-91fb-c6e4732b3e19")

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