package com.prismosis.checklist.ui.authentication.phone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prismosis.checklist.data.repositories.UserRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class PhoneAuthViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneAuthViewModel::class.java)) {
            return PhoneAuthViewModel(
                userRepository = UserRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
