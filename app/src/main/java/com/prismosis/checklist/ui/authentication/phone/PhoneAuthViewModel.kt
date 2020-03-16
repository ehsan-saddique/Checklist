package com.prismosis.checklist.ui.authentication.phone

import android.telephony.PhoneNumberUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prismosis.checklist.R
import com.prismosis.checklist.data.Result

import com.prismosis.checklist.data.repositories.UserRepository
import com.prismosis.checklist.ui.authentication.AuthFormState
import com.prismosis.checklist.ui.authentication.AuthResult
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import javax.xml.datatype.DatatypeConstants.SECONDS
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PhoneAuthViewModel @Inject constructor (private val userRepository: UserRepository) : ViewModel() {

    private val _phoneAuthForm = MutableLiveData<AuthFormState>()
    val phoneAuthFormState: LiveData<AuthFormState> = _phoneAuthForm

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var mFirebaseCallbacks: PhoneCallbacks? = null

    fun formDataChanged(phoneNumber: String) {
        if (!isPhoneNumberValid(phoneNumber)) {
            _phoneAuthForm.value = AuthFormState(phoneNumberError = R.string.invalid_phone_number)
        } else {
            _phoneAuthForm.value = AuthFormState(isDataValid = true)
        }
    }

    fun authenticate(authCredential: PhoneAuthCredential) {
        userRepository.authenticate(authCredential, callback = { result ->
            if (result is Result.Success) {
                _authResult.value = AuthResult(success = result.data)
            } else {
                _authResult.value = AuthResult(error = (result as Result.Error).exception.localizedMessage)
            }
        })
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank() || phoneNumber.isEmpty()) {
            return false
        }
        else if (phoneNumber.length < 10) {
            return false
        }
        else if ((phoneNumber.firstOrNull().toString()) != "+") {
            return false
        }
        else if (PhoneNumberUtils.formatNumberToE164(phoneNumber, "ARE") == null) {
            return false
        }
        return true
    }

}
