package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Utils
import com.example.myapplication.data.NewsArticle
import com.example.myapplication.repository.NewsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection

class NewsViewModel constructor(val newsRepository: NewsRepository) : ViewModel() {

    private val _fetchNews = MutableLiveData<List<NewsArticle>>()

    val fetchNews : LiveData<List<NewsArticle>> get() = _fetchNews

    private val _errorResponse = MutableLiveData<String>()

    val errorResponse : LiveData<String> get() = _errorResponse

    fun getNews() {
        viewModelScope.launch {
            val newsResponse = newsRepository.getNews(Utils.BASE_URL)

            //Parsing the JSON Data into NewsArticle
            val articles = parseJsonResponse(newsResponse.second)

            if (newsResponse.first == HttpURLConnection.HTTP_OK) {
                _fetchNews.postValue(articles)
            } else {
                _errorResponse.postValue(newsResponse.second.toString())
            }
        }
    }

    fun sortData(asc: Boolean) {
        if (asc) {
            val currentData = _fetchNews.value ?: emptyList()
            val sortedData = currentData.sortedBy { it.publishedAt }
            _fetchNews.value = sortedData
        } else {
            val currentData = _fetchNews.value ?: emptyList()
            val sortedData = currentData.sortedByDescending { it.publishedAt }
            _fetchNews.value = sortedData
        }
    }

    private fun parseJsonResponse(response: JSONObject) : List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()

        val jsonArray = response.getJSONArray("articles")

        try {
            for (i in 0 until jsonArray.length()) {

                val jsonObject: JSONObject = jsonArray.getJSONObject(i)

                val author = jsonObject.optString(Utils.AUTHOR)
                val headline = jsonObject.optString(Utils.TITLE)
                val description = jsonObject.optString(Utils.DESCRIPTION)
                val url = jsonObject.optString(Utils.URL)
                val urlToImage = jsonObject.optString(Utils.URL_TO_IMAGE)
                var publishedAt = jsonObject.optString(Utils.PUBLISHED_AT)
                val content = jsonObject.optString(Utils.CONTENT)
                val time = publishedAt.split("T")
                publishedAt = time[0] + " " + time[1].split("Z")[0]

                val newsArticle = NewsArticle(author, headline, description,  url, urlToImage, publishedAt, content)

                articles.add(newsArticle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }
}