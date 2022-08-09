package co.spiritbomb.skyc.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.spiritbomb.skyc.ui.activities.dataStore
import kotlinx.coroutines.flow.first


class Repo {

    suspend fun checkDataStoreValue(key: String, context: Context): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveUrl(url: String, context: Context) {
        val dataStoreKey = stringPreferencesKey(PREF_KEY)
        context.dataStore.edit { sharedPref ->
            sharedPref[dataStoreKey] = url
        }
    }

    companion object {
        const val DATASTORE_NAME = "sharedPref"
        const val PREF_KEY = "finalUrl"
    }
}