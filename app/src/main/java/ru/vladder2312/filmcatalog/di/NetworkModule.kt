package ru.vladder2312.filmcatalog.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.vladder2312.filmcatalog.data.ConstantsAPI
import ru.vladder2312.filmcatalog.data.MovieService
import ru.vladder2312.filmcatalog.data.QueryInterceptor
import ru.vladder2312.filmcatalog.data.repositories.MovieRepository
import javax.inject.Singleton

/**
 * Модуль работы с сетью
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(QueryInterceptor()).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ConstantsAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieService(): MovieService {
        return provideRetrofit().create(MovieService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(): MovieRepository {
        return MovieRepository(provideMovieService())
    }
}