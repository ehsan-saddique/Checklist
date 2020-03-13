package com.prismosis.checklist.ui.signup

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*

import com.prismosis.checklist.R
import com.prismosis.checklist.utils.Utils

class SignupActivity : AppCompatActivity() {

    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        supportActionBar?.title = "Login"

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)
        val signup = findViewById<Button>(R.id.signup)
        val loading = findViewById<ProgressBar>(R.id.loading)

        signupViewModel = ViewModelProviders.of(this, SignupViewModelFactory())
            .get(SignupViewModel::class.java)

        signupViewModel.signupFormState.observe(this, Observer {
            val signupState = it ?: return@Observer

            // disable signup button unless username / password is valid
            signup.isEnabled = signupState.isDataValid

            if (signupState.usernameError != null) {
                username.error = getString(signupState.usernameError)
            }
            if (signupState.passwordError != null) {
                password.error = getString(signupState.passwordError)
            }
            if (signupState.confirmPasswordError != null) {
                confirmPassword.error = getString(signupState.confirmPasswordError)
            }
        })

        signupViewModel.signupResult.observe(this@SignupActivity, Observer {
            val signupResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (signupResult.error != null) {
                showSignupFailed(signupResult.error)
            }
            if (signupResult.success != null) {
                showSignupSuccess(signupResult.success)
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        username.afterTextChanged {
            signupViewModel.signupDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        password.afterTextChanged {
            signupViewModel.signupDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        confirmPassword.afterTextChanged {
            signupViewModel.signupDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        signup.setOnClickListener {
            Utils.hideSoftKeyboard(this)
            loading.visibility = View.VISIBLE
            signupViewModel.signup(username.text.toString(), password.text.toString())
        }
    }

    private fun showSignupSuccess(successString: String) {
        Toast.makeText(this, successString, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showSignupFailed(errorString: String) {
        Utils.showSnackBar(window.decorView.rootView, errorString)
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
