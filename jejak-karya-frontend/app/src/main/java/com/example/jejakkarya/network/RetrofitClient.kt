package com.example.jejakkarya.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 192.168.1.7 adalah IP komputer Anda di jaringan Wi-Fi lokal, JANGAN LUPA PORT 3000
    private const val BASE_URL = "http://192.168.1.7:3000/" 

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)    // Endpoint /api/artworks butuh waktu lebih
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(ApiService::class.java)
    }
}
