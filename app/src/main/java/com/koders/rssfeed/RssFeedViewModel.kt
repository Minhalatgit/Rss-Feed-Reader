package com.koders.rssfeed

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.koders.rssfeed.room.RoomDb
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class RssFeedViewModel(application: Application) : AndroidViewModel(application) {

    var context: Context = application.applicationContext

    private var parser: Parser = Parser.Builder()
        .context(application)
        .build()

    private var _rssFeedList = MutableLiveData<List<com.koders.rssfeed.network.Article>>()
    val rssFeedList: LiveData<List<com.koders.rssfeed.network.Article>>
        get() = _rssFeedList

    fun getFeed() {

        val dao = RoomDb.getDatabase(context).articleDao()

        GlobalScope.launch {
            try {
                val channel =
                    parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
                withContext(Dispatchers.Main) {
                    val articleListDb = dao.getArticles()
                    val articleList = ArrayList<com.koders.rssfeed.network.Article>()
                    for (article in channel.articles) {

                        val currentDate = Date()
                        val c = Calendar.getInstance()
                        c.time = currentDate
                        c.add(Calendar.DATE, -30)
                        val thirtyDaysBackDate = c.time

                        var isExists = false

                        if (articleListDb.isNotEmpty()) {
                            for (articleDb in articleListDb) {
                                if (article.guid == articleDb.guid) {
                                    Log.d("RssViewModel", "Item already exists in local db")
                                    isExists = true
                                    break
                                }
                            }
                            if (!isExists) {
                                if (getDate(article.pubDate!!).after(thirtyDaysBackDate)) {

                                    articleList.add(
                                        com.koders.rssfeed.network.Article(
                                            0,
                                            article.guid,
                                            article.title,
                                            article.author,
                                            article.link,
                                            article.pubDate,
                                            article.description,
                                            article.content,
                                            article.image,
                                            article.audio,
                                            article.video,
                                            article.sourceName,
                                            article.sourceUrl
                                        )
                                    )
                                }
                            }

                        } else {
                            if (getDate(article.pubDate!!).after(thirtyDaysBackDate)) {

                                articleList.add(
                                    com.koders.rssfeed.network.Article(
                                        0,
                                        article.guid,
                                        article.title,
                                        article.author,
                                        article.link,
                                        article.pubDate,
                                        article.description,
                                        article.content,
                                        article.image,
                                        article.audio,
                                        article.video,
                                        article.sourceName,
                                        article.sourceUrl
                                    )
                                )
                            }
                        }
                    }

                    dao.insert(articleList)
                    _rssFeedList.value = dao.getArticles()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _rssFeedList.value = dao.getArticles()
                }
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