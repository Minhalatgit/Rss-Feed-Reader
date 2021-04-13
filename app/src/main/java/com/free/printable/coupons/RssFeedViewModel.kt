package com.free.printable.coupons

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.free.printable.coupons.room.RoomDb
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

    private var _rssFeedList = MutableLiveData<List<com.free.printable.coupons.network.Article>>()
    val rssFeedList: LiveData<List<com.free.printable.coupons.network.Article>>
        get() = _rssFeedList

    fun getFeed() {

        val dao = RoomDb.getDatabase(context).articleDao()

        GlobalScope.launch {
            try {
                val channel =
                    parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
                withContext(Dispatchers.Main) {
                    val articleListDb = dao.getArticles() //getting all from local db
                    val articleList = ArrayList<com.free.printable.coupons.network.Article>()
                    for (article in channel.articles) { //loop for all articles from API

                        val currentDate = Date() //current date
                        val c = Calendar.getInstance()
                        c.time = currentDate
                        c.add(Calendar.DATE, -30)
                        val thirtyDaysBackDate = c.time //date to 30 days back

                        var isExists = false

                        if (articleListDb.isNotEmpty()) { //if local database is empty
                            for (articleDb in articleListDb) { //loop on local database list
                                if (article.guid == articleDb.guid) { //remote data already exists in local db
                                    isExists = true
                                    break
                                } else {
                                    isExists = false
                                }
                            }
                            if (!isExists) { // if api item not exist in local db
                                if (getDate(article.pubDate!!)
                                        .after(thirtyDaysBackDate)) {

                                    articleList.add(
                                        com.free.printable.coupons.network.Article(
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
                            if (getDate(article.pubDate!!)
                                    .after(thirtyDaysBackDate)) {

                                articleList.add(
                                    com.free.printable.coupons.network.Article(
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
                    val toast = Toast.makeText(
                        context,
                        "Couldn't refresh feed, check your internet",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
//                    toast.view.background=
                    toast.show()
                }
            }
        }
    }

}