package com.scavara.otium

import com.scavara.otium.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class QuoteResponse(
    val quoteText: String,
    val author: String,
    val emoji: String
) {
    // This custom getter formats the string as: "some message" --author
    val formattedQuote: String
        get() = "\"$quoteText\" --$author"
}

interface QuoteApiService {
    @GET("api/quotes/random")
    suspend fun getRandomQuote(): QuoteResponse
}

object QuoteApi {
    private val BASE_URL = BuildConfig.QUOTE_BASE_URL

    val service: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }
}