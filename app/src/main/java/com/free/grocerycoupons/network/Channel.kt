package com.free.grocerycoupons.network

data class Channel(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val lastBuildDate: String? = null,
    val updatePeriod: String? = null,
    val articles: MutableList<Article> = mutableListOf()
)