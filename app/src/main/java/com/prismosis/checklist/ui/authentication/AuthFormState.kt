package com.prismosis.checklist.ui.authentication

/**
 * Data validation state of the login form.
 */
data class AuthFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val phoneNumberError: Int? = null,
    val isDataValid: Boolean = false
)
