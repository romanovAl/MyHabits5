package com.example.myhabits3.restful

import com.example.myhabits3.model.PostDone
import com.example.myhabits3.model.ServerHabit
import com.example.myhabits3.model.Uid
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("api/habit")
    suspend fun getHabits(
    ) :List<ServerHabit>

    @HTTP(method = "DELETE", path = "api/habit", hasBody = true)
    suspend fun deleteHabit(
        @Body uid: Uid
    )

    @POST("api/habit_done")
    suspend fun postHabitDone(
        @Body postDone: PostDone
    )

    @PUT("api/habit")
    suspend fun putHabit(
        @Body habit: ServerHabit
    )

    companion object Factory {

        const val API_BASE_URL = "https://droid-test-server.doubletapp.ru/"
        const val CONTENT_TYPE = "application/json"
        const val API_KEY = "5cbb174f-cd85-4ad0-91d4-93be374c9883"

        fun create(): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(chain
                        .request().newBuilder()
                        .addHeader("accept", CONTENT_TYPE)
                        .addHeader("Authorization", API_KEY)
                        .addHeader("Content-Type", CONTENT_TYPE)
                        .build()
                    )
                }
                .connectTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .build()

            val gson = GsonBuilder()
                .registerTypeAdapter(
                    ServerHabit::class.java,
                    ServerHabit.HabitJsonDeserializer()
                )
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}