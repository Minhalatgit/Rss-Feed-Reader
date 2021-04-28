package com.free.printable.coupons.room

import androidx.room.*
import com.free.printable.coupons.network.Article

@Dao
interface ArticleDao {

    @Insert
    suspend fun insertAll(articleList: MutableList<Article>)

    @Insert
    suspend fun insert(articleList: Article)

    @Query("SELECT * FROM article_table WHERE guid= :guid ")
    suspend fun itemExist(guid: String): Article?

    @Delete
    suspend fun delete(article: Article)

    @Query("DELETE FROM article_table")
    suspend fun clearAll()

    @Query("SELECT * FROM article_table ORDER BY pubDate DESC")
    suspend fun getArticles(): List<Article>

}