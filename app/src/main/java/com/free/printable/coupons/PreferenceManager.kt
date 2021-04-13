package com.free.printable.coupons

import android.content.Context

object PreferenceManager {

    private val preferences =
        RssFeedApplication.getCtx()
            .getSharedPreferences("my_pref", Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun setIsFirstLaunch(isLogin: Boolean) {
        editor.putBoolean("is_first_launch", isLogin)
        editor.apply()
    }

    fun getIsFirstLaunch() = preferences.getBoolean("is_first_launch", true)

    fun setLaunchCount(count: Int) {
        editor.putInt("launch_count", count)
        editor.apply()
    }

    fun getLaunchCount() = preferences.getInt("launch_count", 0)

}