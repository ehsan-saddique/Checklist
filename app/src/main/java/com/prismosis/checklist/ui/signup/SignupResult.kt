package com.prismosis.checklist.ui.signup

/**
 * Authentication result : success (user details) or error message.
 */
data class SignupResult(
    val success: Int? = null,
    val error: Int? = null
)
