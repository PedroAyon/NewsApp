package com.pedroayonb.newsapp.repository

import com.pedroayonb.newsapp.api.RetrofitInstance
import com.pedroayonb.newsapp.db.ArticleDatabase
import com.pedroayonb.newsapp.models.Article

class NewsRepository(private val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun addArticle(article: Article) = db.getArticleDao().addArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    suspend fun deleteLastArticle() = db.getArticleDao().deleteLastArticle()
}