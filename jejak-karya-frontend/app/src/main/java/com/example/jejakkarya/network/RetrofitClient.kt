package com.example.jejakkarya.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 192.168.1.7 adalah IP komputer Anda di jaringan Wi-Fi lokal, JANGAN LUPA PORT 3000
    private const val BASE_URL = "http://192.168.1.7:3000/" 

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        retrofit.create(ApiService::class.java)
    }
}
