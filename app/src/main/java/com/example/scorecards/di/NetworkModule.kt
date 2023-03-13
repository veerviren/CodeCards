package com.example.scorecards.di

import com.example.scorecards.BuildConfig
import com.example.scorecards.utils.Constants.Companion.CODEFORCES_API_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import zechs.codeforcesapi.data.remote.CodeforcesApi
import zechs.codeforcesapi.repository.CodeforcesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor {
            if (!it.contains("�")) {
                Platform.get().log(it)
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: Lazy<HttpLoggingInterceptor>,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .also {
                if (BuildConfig.DEBUG) {
                    // Logging only in debug builds
                    it.addInterceptor(logging.get())
                }
            }.build()
    }

    @Singleton
    @Provides
    fun provideCodeforcesApi(
        client: OkHttpClient,
        moshi: Moshi
    ): CodeforcesApi {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(CODEFORCES_API_URL)
            .build().create(CodeforcesApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCodeforcesRepository(
        codeforcesApi: CodeforcesApi
    ): CodeforcesRepository {
        return CodeforcesRepository(codeforcesApi)
    }

}