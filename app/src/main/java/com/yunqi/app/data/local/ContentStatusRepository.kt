package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.contentStatusDataStore by preferencesDataStore(
    name = "content_status",
)

class ContentStatusRepository(
    private val context: Context,
) {
    val statusFlow: Flow<ContentStatus> = context.contentStatusDataStore.data.map { preferences ->
        ContentStatus(
            readContentIds = preferences[Keys.readContentIds].orEmpty(),
            favoriteContentIds = preferences[Keys.favoriteContentIds].orEmpty(),
            hiddenContentIds = preferences[Keys.hiddenContentIds].orEmpty(),
        )
    }

    suspend fun toggleRead(contentId: String) {
        context.contentStatusDataStore.edit { preferences ->
            preferences[Keys.readContentIds] = preferences[Keys.readContentIds]
                .orEmpty()
                .toggleContentId(contentId)
        }
    }

    suspend fun toggleFavorite(contentId: String) {
        context.contentStatusDataStore.edit { preferences ->
            preferences[Keys.favoriteContentIds] = preferences[Keys.favoriteContentIds]
                .orEmpty()
                .toggleContentId(contentId)
        }
    }

    suspend fun toggleHidden(contentId: String) {
        context.contentStatusDataStore.edit { preferences ->
            preferences[Keys.hiddenContentIds] = preferences[Keys.hiddenContentIds]
                .orEmpty()
                .toggleContentId(contentId)
        }
    }

    suspend fun clearStatus() {
        context.contentStatusDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val readContentIds = stringSetPreferencesKey("read_content_ids")
        val favoriteContentIds = stringSetPreferencesKey("favorite_content_ids")
        val hiddenContentIds = stringSetPreferencesKey("hidden_content_ids")
    }
}

data class ContentStatus(
    val readContentIds: Set<String> = emptySet(),
    val favoriteContentIds: Set<String> = emptySet(),
    val hiddenContentIds: Set<String> = emptySet(),
)

/**
 * Toggles a normalized local content id and ignores blank values before they reach DataStore.
 */
internal fun Set<String>.toggleContentId(value: String): Set<String> {
    val normalizedValue = value.trim()
    if (normalizedValue.isBlank()) return this
    return if (normalizedValue in this) this - normalizedValue else this + normalizedValue
}
