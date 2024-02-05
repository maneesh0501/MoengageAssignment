package com.example.myapplication.response

import com.bumptech.glide.load.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class APIResource<out T> {

    data class Success<out T>(val value: T) : APIResource<T>()

    data class Error(
        val isNetworkError: Boolean?,
        val errorCode: Int?,
        val errorBody: String?
    ) : APIResource<Nothing>()

    object Loading: APIResource<Nothing>()

}

suspend fun <T : Any> safeAPICall(
    apiCall: suspend () -> T,
) : APIResource<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall.invoke()
            APIResource.Success(response)
        } catch (throwable: Throwable) {
            when(throwable) {
                is HttpException -> {
                    APIResource.Error(false, throwable.statusCode, throwable.message)
                }
                else -> {
                    APIResource.Error(true, null, throwable.message)
                }
            }
        }
    }
}