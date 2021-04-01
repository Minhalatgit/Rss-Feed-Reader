package com.koders.rssfeed

import android.content.Context

object PreferenceManager {

    private val preferences =
        RssFeedApplication.getCtx().getSharedPreferences("my_pref", Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun setIsFirstLaunch(isLogin: Boolean) {
        editor.putBoolean("is_first_launch", isLogin)
        editor.apply()
    }

    fun getIsFirstLaunch() = preferences.getBoolean("is_first_launch", true)
}