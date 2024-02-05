package com.example.myapplication.repository

import com.example.myapplication.networking.RemoteAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

interface NewsRepository {
    suspend fun getNews(apiUrl: String) : Pair<Int, JSONObject>
}

class NewsRepositoryImpl constructor(val apiService: RemoteAPIService) : NewsRepository {
    override suspend fun getNews(apiUrl: String) : Pair<Int, JSONObject> {

        return withContext(Dispatchers.IO) {
            apiService.makeGetRequest(apiUrl)
        }
    }
}