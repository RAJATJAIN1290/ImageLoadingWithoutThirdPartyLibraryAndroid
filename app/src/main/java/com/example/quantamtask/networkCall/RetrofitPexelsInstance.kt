package com.example.quantamtask.networkCall

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitPexelsInstance {

    private const val BASE_URL = "https://api.pexels.com/v1/"
    private const val API_KEY = "GtiCvZBvZdNWCwmuZZ4i1OcpdBVFmKYBYMag0rRS10sQiJYTAAIcd5fV" // Replace with your actual API key

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", API_KEY) // Add the API key to the request header
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Set the OkHttpClient with the added API key interceptor
            .build()
            .create(RetrofitService::class.java)
    }

}