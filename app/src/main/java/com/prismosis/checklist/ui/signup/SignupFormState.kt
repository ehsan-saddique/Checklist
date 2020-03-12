package com.prismosis.checklist.ui.signup

/**
 * Data validation state of the login form.
 */
data class SignupFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isDataValid: Boolean = false
)
