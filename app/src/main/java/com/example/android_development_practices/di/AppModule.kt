package com.example.android_development_practices.di

import com.example.android_development_practices.data.remote.SpaceApi
import com.example.android_development_practices.data.repository.SpaceRepositoryImpl
import com.example.android_development_practices.domain.repository.SpaceRepository
import com.example.android_development_practices.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSpaceApi(): SpaceApi {
        val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SPACE_API_BASE_URL)
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
