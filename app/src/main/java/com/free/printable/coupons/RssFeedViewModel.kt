package com.free.printable.coupons

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.free.printable.coupons.network.Article
import com.free.printable.coupons.room.RoomDb
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class RssFeedViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "RssFeedViewModel"

    var context: Context = application.applicationContext

    private var parser: Parser = Parser.Builder()
        .context(application)
        .build()

    private var _rssFeedList = MutableLiveData<List<Article>>()
    val rssFeedList: LiveData<List<Article>>
        get() = _rssFeedList

    fun getFeed() {

        val dao = RoomDb.getDatabase(context).articleDao()

        GlobalScope.launch {
            try {
                val channel =
                    parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
                withContext(Dispatchers.Main) {

                    val articleListDb = dao.getArticles() //getting all from local db

                    //here we check if item exist in local db or not
                    //if does not exist then insert, otherwise delete
                    var i = 0

                    if (articleListDb.isNotEmpty()) {
                        for (article in channel.articles) {

                            if (dao.itemExist(article.guid!!) == null) {
                                Log.d(TAG, "item does not exists in feed")
                                val articleToInsert = Article(
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
                                dao.insert(articleToInsert)
                            } else {
                                Log.d(TAG, "item exist in feed")
                            }
                        }
                    } else { //local db is empty(first time opening app)
                        for (article in channel.articles) { //loop for all articles from API
                            val articleToInsert = Article(
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
                            dao.insert(articleToInsert)
                        }
                    }

//                    FirebaseDatabase.getInstance().reference.child("days")
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onCancelled(error: DatabaseError) {
//                                Log.e(TAG, "onCancelled: ${error.message}")
//                            }
//
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                Log.d(TAG, "onDataChange: ${snapshot.value}")
//                            }
//
//                        })

                    //get all data from local and check if is 30 day old;
                    //those who are not old, delete them

                    val currentDate = Date() //current date
                    val c = Calendar.getInstance()
                    c.time = currentDate
                    c.add(Calendar.DATE, -30)
                    val thirtyDaysBackDate = c.time //Aj se 30 din pehle ki date nikaal li

                    for (article in dao.getArticles()) {
//                        Log.d(TAG, "Guid of feed from local: ${article.guid}")
                        if (getDate(article.pubDate!!).before(thirtyDaysBackDate)) {
                            //article ki date 30 din pehle ki date se pehle ki hai, so removing it
                            dao.delete(article)
                        } else {
                            //article ki date 30 din pehle ki date se agay ki hai, so keeping it
                        }
                    }

                    _rssFeedList.value = dao.getArticles()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _rssFeedList.value = dao.getArticles()
                }
                Log.d(TAG, "getFeed failed ${e.message}")
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