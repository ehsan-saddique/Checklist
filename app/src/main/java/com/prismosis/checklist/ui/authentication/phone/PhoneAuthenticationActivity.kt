package com.prismosis.checklist.ui.authentication.phone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import com.prismosis.checklist.R
import com.prismosis.checklist.ui.authentication.signup.afterTextChanged
import com.prismosis.checklist.ui.task.TaskListActivity
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Utils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhoneAuthenticationActivity : AppCompatActivity(), PhoneCallbacks.PhoneCallbacksListener {
    @Inject
    lateinit var phoneAuthViewModel: PhoneAuthViewModel
    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var progressDialog: ProgressDialog
    private lateinit var phoneCallbacks: PhoneCallbacks
    private lateinit var phoneNumber: EditText
    private lateinit var verificationCode: EditText
    private lateinit var btnAuthenticate: Button
    private lateinit var btnVerifyCode: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_phone_authentication)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Phone Verification"
        ChecklistApplication.instance?.appComponent?.inject(this)

        phoneNumber = findViewById<EditText>(R.id.phone_number)
        btnAuthenticate = findViewById<Button>(R.id.authenticate)
        verificationCode = findViewById<EditText>(R.id.verification_code)
        btnVerifyCode = findViewById<Button>(R.id.verify_code)

        phoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        phoneCallbacks = PhoneCallbacks(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Sending verification code")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)


        phoneAuthViewModel.authResult.observe(this, Observer {
            val signupResult = it ?: return@Observer

            progressDialog.hide()
            if (signupResult.error != null) {
                showAuthFailure(signupResult.error)
            }
            if (signupResult.success != null) {
                showAuthSuccess(signupResult.success)
            }
        })

        phoneAuthViewModel.phoneAuthFormState.observe(this, Observer {
            val authFormState = it ?: return@Observer

            btnAuthenticate.isEnabled = authFormState.isDataValid

            if (authFormState.phoneNumberError != null) {
                phoneNumber.error = getString(authFormState.phoneNumberError)
            }
        })

        phoneNumber.afterTextChanged {
            phoneAuthViewModel.formDataChanged(
                phoneNumber.text.toString()
            )
        }

        btnAuthenticate.setOnClickListener {
            Utils.hideSoftKeyboard(this)
            progressDialog.show()
            sendVerificationCode(phoneNumber.text.toString())
        }

        btnVerifyCode.setOnClickListener{
            Utils.hideSoftKeyboard(this)
            if (verificationCode.text.isNullOrEmpty()) {
                verificationCode.error = "Please enter the verification code"
            }
            else {
                verifyVerificationCode(verificationCode.text.toString())
            }
        }
    }

    private fun showCodeVerificationView() {
        phoneNumber.visibility = View.GONE
        btnAuthenticate.visibility = View.GONE
        verificationCode.visibility = View.VISIBLE
        btnVerifyCode.visibility = View.VISIBLE
    }

    private fun showVerificationDialog() {
        progressDialog.setTitle("Verifying code")
        progressDialog.show()
    }

    private fun showAuthSuccess(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, TaskListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun showAuthFailure(errorString: String) {
        Utils.showSnackBar(window.decorView.rootView, errorString)
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumberToE164(phoneNumber, "")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(formattedPhoneNumber, 60, TimeUnit.SECONDS, this, phoneCallbacks)
    }

    private fun verifyVerificationCode(code: String) {
        storedVerificationId?.let {
            val authCredential = PhoneAuthProvider.getCredential(it, code)
            showVerificationDialog()
            phoneAuthViewModel.authenticate(authCredential)
        }
    }

    override fun onVerificationCompleted(credential: PhoneAuthCredential?) {
        if (credential != null && storedVerificationId != null) {
            verificationCode.setText(credential.smsCode ?: "")
            showVerificationDialog()
            phoneAuthViewModel.authenticate(credential)
        }
        else {
            showAuthFailure("Something went wrong, please try later.")
        }
    }

    override fun onVerificationFailed(exception: FirebaseException?) {
        progressDialog.hide()
        if (exception is FirebaseAuthInvalidCredentialsException) {
            showAuthFailure("Invalid phone number.")
        }
        else {
            showAuthFailure(exception?.localizedMessage ?: "Something went wrong, please try later.")
        }
    }

    override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
        progressDialog.hide()
        storedVerificationId = verificationId!!
        resendToken = token!!
        Toast.makeText(this, "Verification code has been sent to your phone number", Toast.LENGTH_SHORT).show()
        showCodeVerificationView()
    }

}

class PhoneCallbacks(private val listener : PhoneCallbacksListener) : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

    interface PhoneCallbacksListener {
        fun onVerificationCompleted(credential: PhoneAuthCredential?)
        fun onVerificationFailed(exception: FirebaseException?)
        fun onCodeSent(
            verificationId: String?,
            token: PhoneAuthProvider.ForceResendingToken?
        )
    }

    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
        listener.onVerificationCompleted(phoneAuthCredential)
    }

    override fun onVerificationFailed(exception: FirebaseException) {
        listener.onVerificationFailed(exception)
    }

    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        listener.onCodeSent(verificationId,token)
    }
}
