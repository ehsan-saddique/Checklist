package com.prismosis.checklist.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.prismosis.checklist.data.Result

import com.prismosis.checklist.R
import com.prismosis.checklist.data.repositories.UserRepository

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signupForm = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _signupForm

    private val _signupResult = MutableLiveData<SignupResult>()
    val signupResult: LiveData<SignupResult> = _signupResult

    fun signup(username: String, password: String) {
        userRepository.signup(username, password, callback = { result ->
            if (result is Result.Success) {
                _signupResult.value = SignupResult(success = result.data)
            } else {
                _signupResult.value = SignupResult(error = result.toString())
            }
        })
    }

    fun signupDataChanged(username: String, password: String, confirmPassword: String) {
        if (!isUserNameValid(username)) {
            _signupForm.value = SignupFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _signupForm.value = SignupFormState(passwordError = R.string.invalid_password)
        } else if (!passwordsMatch(password, confirmPassword)) {
            _signupForm.value = SignupFormState(confirmPasswordError = R.string.invalid_password_match)
        }else {
            _signupForm.value = SignupFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password.equals(confirmPassword)
    }


}
