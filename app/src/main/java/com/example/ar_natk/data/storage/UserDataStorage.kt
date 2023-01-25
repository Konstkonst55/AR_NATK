package com.example.ar_natk.data.storage

import android.content.Context
import com.example.ar_natk.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserDataStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(Constants.USER_PREFS_KEY, Context.MODE_PRIVATE)

    var userName: String?
        get() = prefs.getString(Constants.USER_NAME_KEY, null)
        set(value) {
            prefs.edit().putString(Constants.USER_NAME_KEY, value).apply()
        }

    var collection: Set<String>?
        get() = prefs.getStringSet(Constants.USER_COLLECTION_KEY, HashSet())
        set(hashSet) {
            prefs.edit().putStringSet(Constants.USER_COLLECTION_KEY, hashSet).apply()
        }

    var collectionIntegerList: ArrayList<Int>
        get() {
            val listIds = ArrayList<Int>()
            collection?.forEach { str -> str.toIntOrNull()?.let { listIds.add(it) } }
            return listIds
        }
        set(arrayInt) {
            val stringSet = HashSet<String>()
            arrayInt.forEach { id -> stringSet.add(id.toString()) }
            collection = stringSet
        }

    var userId: String?
        get() = prefs.getString(Constants.USER_ID_KEY, null)
        set(value) {
            prefs.edit().putString(Constants.USER_ID_KEY, value).apply()
        }

    fun clearData() {
        prefs.edit().clear().apply()
    }
}