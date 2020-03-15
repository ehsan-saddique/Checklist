package com.prismosis.checklist.ui.authentication

/**
 * Authentication result : success (user details) or error message.
 */
data class AuthResult(
    val success: String? = null,
    val error: String? = null
)
