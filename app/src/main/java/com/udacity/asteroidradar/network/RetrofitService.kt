package com.udacity.asteroidradar.network


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.NetworkPictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

enum class NETWORK_STATUS {INITIALIZING, CONNECTED, DISCONNECTED}

interface AsteroidsService {
    @GET("neo/rest/v1/feed/")
    suspend fun getAsteroidList(
            @Query("start_date") startDate: String,
            @Query("end_date")   endDate: String,
            @Query("api_key")    apiKey: String): String

    @GET("planetary/apod")
    suspend fun getPictureOfDay(
            @Query("api_key")   apiKey: String): NetworkPictureOfDay
}

object Network {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build()
    }

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val retrofitAsteroidsService: AsteroidsService by lazy {
        retrofit.create(AsteroidsService::class.java)
    }
}