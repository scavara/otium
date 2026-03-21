package com.scavara.otium

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of DataStore for the entire app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "otium_settings")

// Enums to define our available options
enum class QuoteSize { SMALL, MEDIUM, LARGE }
enum class QuotePosition { TOP_START, CENTER, BOTTOM_START }

class SettingsRepository(private val context: Context) {

    // Define the keys we will use to save data
    private companion object {
        val SHOW_QUOTES = booleanPreferencesKey("show_quotes")
        val QUOTE_SIZE = stringPreferencesKey("quote_size")
        val QUOTE_POSITION = stringPreferencesKey("quote_position")
    }

    // --- READ PREFERENCES (Returns flows that automatically update the UI when changed) ---
    val showQuotesFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_QUOTES] ?: true }

    val quoteSizeFlow: Flow<QuoteSize> = context.dataStore.data.map { prefs ->
        // Default to MEDIUM if no setting is saved yet
        QuoteSize.valueOf(prefs[QUOTE_SIZE] ?: QuoteSize.MEDIUM.name)
    }

    val quotePositionFlow: Flow<QuotePosition> = context.dataStore.data.map { prefs ->
        // Default to BOTTOM_START if no setting is saved yet
        QuotePosition.valueOf(prefs[QUOTE_POSITION] ?: QuotePosition.BOTTOM_START.name)
    }

    // --- WRITE PREFERENCES ---
    suspend fun toggleShowQuotes(current: Boolean) {
        context.dataStore.edit { it[SHOW_QUOTES] = !current }
    }

    suspend fun setQuoteSize(size: QuoteSize) {
        context.dataStore.edit { it[QUOTE_SIZE] = size.name }
    }

    suspend fun setQuotePosition(position: QuotePosition) {
        context.dataStore.edit { it[QUOTE_POSITION] = position.name }
    }
}