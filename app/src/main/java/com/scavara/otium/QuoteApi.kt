package com.scavara.otium

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Data Class mapping exactly to your Heroku JSON keys
data class QuoteResponse(
    val quoteText: String,
    val author: String,
    val emoji: String
)

// 2. The API Interface
interface QuoteApiService {
    @GET("api/quotes/random")
    suspend fun getRandomQuote(): QuoteResponse
}

// 3. Retrofit Builder setup
object QuoteApi {
    private const val BASE_URL = "https://statusquo-1c0c04fdc62e.herokuapp.com/"

    val service: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }
}