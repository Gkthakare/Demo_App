package com.example.registrationscreen.utils

import com.example.registrationscreen.data.NewsResponse
import retrofit2.http.GET

interface NewsService {
    @GET("everything?q=tesla&from=2022-08-25&sortBy=publishedAt&apiKey=12b0cb6c831842bf924747df98723d1b")
    suspend fun getHeadline(): NewsResponse
}