package com.example.ar_natk.data.repository

import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.utils.AuthState
import com.example.ar_natk.utils.Constants
import com.example.ar_natk.utils.DeviceName
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val userStorage: UserDataStorage
) : IAuth {
    override suspend fun signIn(name: String?): AuthState? {
        var state: AuthState? = null
        val db = Firebase.firestore
        val user = hashMapOf(
            "name" to name,
            "count" to 0,
            "device" to DeviceName.deviceName(),
            "date" to Timestamp.now()
        )

        db.collection(Constants.FIRE_COLLECTION_USER)
            .add(user)
            .addOnSuccessListener {
                if (name != null) {
                    saveUserData(name, it.id)
                }
                state = AuthState.SUCCESS
            }
            .addOnFailureListener {
                state = AuthState.FAILURE
            }

        return state
    }

    private fun saveUserData(name: String, id: String) {
        with(userStorage) {
            userName = name
            userId = id
        }
    }
}