package com.prismosis.checklist.data.repositories

import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.*
import com.prismosis.checklist.data.Result
import com.prismosis.checklist.utils.Utils
import java.util.concurrent.TimeUnit

class UserRepository {

    fun signup(username: String, password: String, callback: (Result<String>)->Unit) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                callback.invoke(Result.Success("You have signed up successfully."))
            } else {
                try {
                    throw task.exception ?: java.lang.Exception("Unknown error")
                }
                catch (ex: FirebaseAuthUserCollisionException) {
                    callback.invoke(Result.Error(Exception("Email already exists. Please use a different email.")))
                }
                catch (ex: java.lang.Exception) {
                    callback.invoke(Result.Error(Exception("Error signing up: ${task.exception.toString()}")))
                }
            }
        }
    }

    fun login(username: String, password: String, callback: (Result<String>)->Unit) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                callback.invoke(Result.Success("Logged in successfully"))
            } else {
                try {
                    throw task.exception ?: java.lang.Exception("Unknown error")
                }
                catch (ex: FirebaseAuthInvalidUserException) {
                    callback.invoke(Result.Error(Exception("Invalid email or password")))
                }
                catch (ex: FirebaseAuthInvalidCredentialsException) {
                    callback.invoke(Result.Error(Exception("Invalid email or password")))
                }
                catch (ex: java.lang.Exception) {
                    callback.invoke(Result.Error(Exception("Error logging in: ${task.exception.toString()}")))
                }
            }
        }
    }

    fun authenticate(authCredential: PhoneAuthCredential, callback: (Result<String>)->Unit) {

        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener { task ->
            if(task.isSuccessful){
                callback.invoke(Result.Success("Verification successful"))
            } else {
                try {
                    throw task.exception ?: java.lang.Exception("Unknown error")
                }
                catch (ex: FirebaseAuthInvalidCredentialsException) {
                    callback.invoke(Result.Error(Exception("Verification code is invalid.")))
                }
                catch (ex: java.lang.Exception) {
                    callback.invoke(Result.Error(Exception("Verification failed: ${task.exception.toString()}")))
                }
            }
        }
    }

    fun logout() {
        Utils.setIsTasksFetched(false)
        FirebaseAuth.getInstance().signOut()
    }
}
