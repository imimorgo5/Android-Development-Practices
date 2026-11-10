package com.example.android_development_practices.feature.profile.di

import com.example.android_development_practices.feature.profile.data.LocalProfileRepository
import com.example.android_development_practices.feature.profile.data.reminder.AndroidClassReminderScheduler
import com.example.android_development_practices.feature.profile.domain.ClassReminderScheduler
import com.example.android_development_practices.feature.profile.domain.ProfileRepository
import com.example.android_development_practices.feature.profile.domain.ReminderClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProfileIoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    fun provideReminderClock(): ReminderClock = ReminderClock { System.currentTimeMillis() }

    @Provides
    @Singleton
    fun provideClassReminderScheduler(scheduler: AndroidClassReminderScheduler): ClassReminderScheduler = scheduler

    @Provides
    @Singleton
    fun provideProfileRepository(repository: LocalProfileRepository): ProfileRepository = repository

    @Provides
    @ProfileIoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
