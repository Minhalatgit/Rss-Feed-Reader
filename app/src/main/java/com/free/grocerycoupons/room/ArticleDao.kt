package com.free.grocerycoupons.room

import androidx.room.*
import com.free.grocerycoupons.network.Article

@Dao
interface ArticleDao {

    @Insert
    suspend fun insert(articleList: MutableList<Article>)

    @Query("DELETE FROM article_table")
    suspend fun clearAll()

    @Query("SELECT * FROM article_table")
    suspend fun getArticles(): List<Article>

}