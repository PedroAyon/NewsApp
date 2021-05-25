package com.pedroayonb.newsapp.models

import com.pedroayonb.newsapp.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)