package com.example.android_development_practices.ui.common

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FilterBadgeCache @Inject constructor() {
    private val mutableActive = MutableStateFlow(false)
    val active = mutableActive.asStateFlow()
    fun setActive(value: Boolean) { mutableActive.value = value }
}
