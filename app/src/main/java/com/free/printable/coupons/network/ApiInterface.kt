package com.free.printable.coupons.network

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("gc-rss.asp?cp=2&afid=357373/")
    fun login(): Call<Channel>
}