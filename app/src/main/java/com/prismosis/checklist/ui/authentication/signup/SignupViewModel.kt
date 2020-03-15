package com.prismosis.checklist.ui.authentication.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.prismosis.checklist.data.Result

import com.prismosis.checklist.R
import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.ui.authentication.AuthFormState
import com.prismosis.checklist.ui.authentication.AuthResult

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signupForm = MutableLiveData<AuthFormState>()
    val signupFormState: LiveData<AuthFormState> = _signupForm

    private val _signupResult = MutableLiveData<AuthResult>()
    val signupResult: LiveData<AuthResult> = _signupResult

    fun signup(username: String, password: String) {
        userRepository.signup(username, password, callback = { result ->
            if (result is Result.Success) {
                _signupResult.value = AuthResult(success = result.data)
            } else {
                _signupResult.value = AuthResult(error = (result as Result.Error).exception.localizedMessage)
            }
        })
    }

    fun signupDataChanged(username: String, password: String, confirmPassword: String) {
        if (!isUserNameValid(username)) {
            _signupForm.value = AuthFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _signupForm.value = AuthFormState(passwordError = R.string.invalid_password)
        } else if (!passwordsMatch(password, confirmPassword)) {
            _signupForm.value = AuthFormState(confirmPasswordError = R.string.invalid_password_match)
        }else {
            _signupForm.value = AuthFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password.equals(confirmPassword)
    }


}
