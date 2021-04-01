package com.example.myhabits3.di.modules

import com.example.myhabits3.core.Config
import com.example.myhabits3.core.Config.API_KEY
import com.example.myhabits3.core.Config.CONTENT_TYPE
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.romanoval.data.mapper.ErrorConverter
import ru.romanoval.data.restful.ApiService
import ru.romanoval.data.source.cloud.BaseCloudRepository
import ru.romanoval.data.source.cloud.CloudRepository
import ru.romanoval.domain.model.restful.ServerHabit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain
                        .request().newBuilder()
                        .addHeader("accept", CONTENT_TYPE)
                        .addHeader("Authorization", API_KEY)
                        .addHeader("Content-Type", CONTENT_TYPE)
                        .build()
                )
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesErrorConverter(
        retrofit: Retrofit
    ) : ErrorConverter =
        ErrorConverter(retrofit)


    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                ServerHabit::class.java,
                ServerHabit.HabitJsonDeserializer()
            )
            .create()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudRepository(service: ApiService): BaseCloudRepository {
        return CloudRepository(service)
    }

}