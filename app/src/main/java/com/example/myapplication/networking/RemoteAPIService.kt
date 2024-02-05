package com.example.myapplication.networking

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class RemoteAPIService {

    suspend fun makeGetRequest(apiUrl: String): Pair<Int, JSONObject> {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            // Set request method to GET
            connection.requestMethod = "GET"

            // Set timeouts (optional)
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            // Set Content type
            connection.setRequestProperty("Content-Type", "application/json")

            // Get the response code
            val responseCode = connection.responseCode

            //Read the response body
            val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                JSONObject(response.toString())
            } else {
                JSONObject().put("error", "Error : ${connection.responseMessage}")
            }

            return Pair(responseCode, responseBody)
        } finally {
            connection.disconnect()
        }
    }
}