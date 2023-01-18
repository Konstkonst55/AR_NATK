package com.example.ar_natk.data.repository

import com.example.ar_natk.utils.AuthState

interface IAuth {
    suspend fun signIn(name: String? = null): AuthState?
}