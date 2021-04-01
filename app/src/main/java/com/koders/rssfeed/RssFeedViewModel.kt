package com.koders.rssfeed

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.koders.rssfeed.network.Channel
import com.koders.rssfeed.network.RetrofitClient
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RssFeedViewModel(application: Application) : AndroidViewModel(application) {

    var context: Context = application.applicationContext

    private var parser: Parser = Parser.Builder()
        .context(application)
        // If you want to provide a custom charset (the default is utf-8):
        // .charset(Charset.forName("ISO-8859-7"))
        //.cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()

    private var _rssFeedList = MutableLiveData<MutableList<Article>>()
    val rssFeedList: LiveData<MutableList<Article>>
        get() = _rssFeedList

    fun getFeed() {
        GlobalScope.launch {
            try {
//                RetrofitClient.apiInterface.login().enqueue(object : Callback<Channel> {
//                    override fun onFailure(call: Call<Channel>, t: Throwable) {
//                    }
//
//                    override fun onResponse(call: Call<Channel>, response: Response<Channel>) {
//                        Log.d("RssViewModel", "onResponse: ${response.body()}")
//                    }
//                })
                val channel =
                    parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
                withContext(Dispatchers.Main) {
                    _rssFeedList.value = channel.articles
                }
            } catch (e: Exception) {
                Log.d("RssViewModel", "getFeed failed ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Your internet seems slow, reload please",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}