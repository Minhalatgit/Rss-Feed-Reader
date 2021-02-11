package com.koders.rssfeed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RssFeedViewModel(application: Application) : AndroidViewModel(application) {

    private var parser: Parser = Parser.Builder()
        .context(application)
        // If you want to provide a custom charset (the default is utf-8):
        // .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()

    private var _rssFeedList = MutableLiveData<MutableList<Article>>()
    val rssFeedList: LiveData<MutableList<Article>>
        get() = _rssFeedList

    fun getFeed() {
        GlobalScope.launch {
            val channel =
                parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
            withContext(Dispatchers.Main) {
                _rssFeedList.value = channel.articles
            }
        }
    }

}