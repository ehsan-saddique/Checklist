package com.prismosis.checklist.ui.authentication.signup

import android.app.Activity
import android.app.ProgressDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

import com.prismosis.checklist.R
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Utils
import javax.inject.Inject

class SignupActivity : AppCompatActivity() {

    @Inject
    lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Sign Up"
        ChecklistApplication.instance?.appComponent?.inject(this)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)
        val signup = findViewById<Button>(R.id.signup)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing Up")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)

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

            progressDialog.hide()
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
            Utils.instance.hideSoftKeyboard(this)
            progressDialog.show()
            signupViewModel.signup(username.text.toString(), password.text.toString())
        }
    }

    private fun showSignupSuccess(successString: String) {
        Toast.makeText(this, successString, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showSignupFailed(errorString: String) {
        Utils.instance.showSnackBar(window.decorView.rootView, errorString)
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
