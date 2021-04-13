package com.free.printable.coupons.network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_table")
data class Article(

    @PrimaryKey(autoGenerate = true)
    var id:Int,

    var guid: String? = null,
    var title: String? = null,
    var author: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var content: String? = null,
    var image: String? = null,
    var audio: String? = null,
    var video: String? = null,
    var sourceName: String? = null,
    var sourceUrl: String? = null
)