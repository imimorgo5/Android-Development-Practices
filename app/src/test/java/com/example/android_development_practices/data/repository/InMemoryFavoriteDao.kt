package com.example.android_development_practices.data.repository

import com.example.android_development_practices.data.local.FavoriteDao
import com.example.android_development_practices.data.local.FavoriteEntity
import kotlinx.coroutines.flow.MutableStateFlow

internal class InMemoryFavoriteDao : FavoriteDao {
    private val items = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    override fun observeAll() = items
    override suspend fun add(item: FavoriteEntity) {
        items.value = items.value.filterNot { it.key == item.key } + item
    }
    override suspend fun remove(key: String) {
        items.value = items.value.filterNot { it.key == key }
    }
    override suspend fun contains(key: String) = items.value.any { it.key == key }
    override suspend fun get(key: String) = items.value.find { it.key == key }
    override suspend fun updateDetail(key: String, payload: String) {
        items.value = items.value.map { if (it.key == key) it.copy(detailPayload = payload) else it }
    }
}
