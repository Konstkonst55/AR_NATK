package com.example.ar_natk.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ar_natk.data.repository.IAuth
import com.example.ar_natk.utils.AuthState
import com.example.ar_natk.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: IAuth
) : ViewModel() {

    val userName = MutableLiveData("")

    private val _state: MutableLiveData<State> = MutableLiveData(State.Idle)
    val state: LiveData<State>
        get() = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action>
        get() = _actions.receiveAsFlow()

    init {
        viewModelScope.launch { validate() }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (auth.signIn(userName.value)) {
                AuthState.SUCCESS -> {
                    _actions.send(Action.RouteToMain)
                    _state.postValue(State.Idle)
                }
                AuthState.FAILURE -> {
                    _actions.send(Action.ShowError)
                    _state.postValue(State.Idle)
                }
                else -> {}
            }
        }
    }

    private fun validate(): ValidationState {
        val result = userName.value!!.validateName()

        when (result) {
            ValidationState.VALID -> _state.value = State.Idle
            ValidationState.EMPTY -> {}
        }
        return result
    }

    private fun String.validateName(): ValidationState {
        return when {
            this.isEmpty() -> ValidationState.EMPTY
            else -> ValidationState.VALID
        }
    }

    fun onButtonNextPressed() {
        when (val result = validate()) {
            ValidationState.EMPTY -> _state.value = State.InvalidInput(result.message)
            ValidationState.VALID -> signIn()
        }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val message: String) : State
    }

    enum class ValidationState(val message: String) {
        VALID(Constants.VALIDATION_VALID),
        EMPTY(Constants.VALIDATION_EMPTY)
    }

    sealed interface Action {
        object ShowError : Action
        object RouteToMain : Action
    }
}
