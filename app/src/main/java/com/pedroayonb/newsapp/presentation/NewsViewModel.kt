package com.pedroayonb.newsapp.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pedroayonb.newsapp.NewsApplication
import com.pedroayonb.newsapp.models.Article
import com.pedroayonb.newsapp.models.NewsResponse
import com.pedroayonb.newsapp.repository.NewsRepository
import com.pedroayonb.newsapp.utils.Constants
import com.pedroayonb.newsapp.utils.InternetCheck
import com.pedroayonb.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, private val newsRepository: NewsRepository) :
    AndroidViewModel(app) {
    val breakingNewsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNewsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null


    init {
        getBreakingNews(Constants.countryCode)
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNewsData.postValue(Resource.Loading())
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsData.postValue(Resource.Loading())
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.addArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun deleteLastArticle() = viewModelScope.launch {
        newsRepository.deleteLastArticle()
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNewsData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNewsData.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNewsData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNewsData.postValue(Resource.Error("Network Failure"))
                else -> breakingNewsData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNewsData.postValue(Resource.Loading())
        try {
            if (InternetCheck.istNetworkAvailable()) {
                val response = newsRepository.searchNews(searchQuery, breakingNewsPage)
                searchNewsData.postValue(handleSearchNewsResponse(response))
            } else {
                searchNewsData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNewsData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) or
                    capabilities.hasTransport(TRANSPORT_CELLULAR) or
                    capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}