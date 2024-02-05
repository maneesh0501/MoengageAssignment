package com.example.myapplication.data

data class NewsArticle(
    val author: String,
    val headline: String, //Title
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)