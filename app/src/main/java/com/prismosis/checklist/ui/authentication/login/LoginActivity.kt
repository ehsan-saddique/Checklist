package com.prismosis.checklist.ui.authentication.login

import android.app.ProgressDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

import com.prismosis.checklist.R
import com.prismosis.checklist.ui.authentication.phone.PhoneAuthenticationActivity
import com.prismosis.checklist.ui.authentication.signup.SignupActivity
import com.prismosis.checklist.ui.task.TaskListActivity
import com.prismosis.checklist.utils.ChecklistApplication
import com.prismosis.checklist.utils.Utils
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "Login"
        ChecklistApplication.instance?.appComponent?.inject(this)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val signup = findViewById<Button>(R.id.signup)
        val phoneAuth = findViewById<Button>(R.id.phone_authentication)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging In")
        progressDialog.setMessage("Please wait..")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            progressDialog.hide()
            if (loginResult.error != null) {
                onLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                onLoginSuccess(loginResult.success)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            login.setOnClickListener {
                Utils.instance.hideSoftKeyboard(this@LoginActivity)
                progressDialog.show()
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            signup.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }

            phoneAuth.setOnClickListener {
                val intent = Intent(this@LoginActivity, PhoneAuthenticationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun onLoginSuccess(msg: String) {
        val intent = Intent(this@LoginActivity, TaskListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun onLoginFailed(errorString: String) {
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
