package com.pedroayonb.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pedroayonb.newsapp.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticle(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM articles WHERE id = (SELECT MAX(id) FROM articles)")
    suspend fun deleteLastArticle()
}