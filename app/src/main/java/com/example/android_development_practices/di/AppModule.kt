package com.example.android_development_practices.di

import com.example.android_development_practices.data.remote.SpaceApi
import com.example.android_development_practices.data.repository.SpaceRepositoryImpl
import com.example.android_development_practices.domain.repository.SpaceRepository
import com.example.android_development_practices.BuildConfig
import android.util.Log
import dagger.Module
import dagger.Provides
import androidx.room.Room
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.android_development_practices.data.local.SpaceDatabase
import com.example.android_development_practices.data.local.FavoriteDao
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSpaceDatabase(@ApplicationContext context: Context): SpaceDatabase =
        Room.databaseBuilder(context, SpaceDatabase::class.java, "space_catalog.db")
            .build()

    @Provides
    fun provideFavoriteDao(database: SpaceDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    @Singleton
    fun provideSpaceApi(): SpaceApi {
        val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
        val httpClient = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor { message -> Log.d("SpaceApi", message) }
                        .setLevel(HttpLoggingInterceptor.Level.BASIC),
                )
            }
        }.build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SPACE_API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
            .create(SpaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpaceRepository(api: SpaceApi): SpaceRepository = SpaceRepositoryImpl(api)

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
