package com.scavara.otium

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Data Classes mapping specifically to the Unsplash JSON keys
data class UnsplashImage(
    val id: String,
    val description: String?,
    val urls: UnsplashUrls
)

data class UnsplashUrls(
    val regular: String // "regular" is the perfect size for 1080p TVs
)

// 2. The API Interface
interface UnsplashApiService {
    @GET("photos/random")
    suspend fun getRandomNatureImage(
        @Query("query") query: String = "nature,landscape",
        @Query("orientation") orientation: String = "landscape"
    ): UnsplashImage
}

// 3. Retrofit Builder setup (Matches your QuoteApi structure)
object UnsplashApi {
    private const val BASE_URL = "https://api.unsplash.com/"

    // Pulling the hidden key from local.properties via BuildConfig
    private const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY

    // Interceptor to automatically attach the API key to every request
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Client-ID $CLIENT_ID")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Lazy initialization of the service
    val service: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApiService::class.java)
    }
}