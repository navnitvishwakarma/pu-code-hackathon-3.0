package com.teamrocket.passengerapp.data.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthManager(private val activity: Activity) {

    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    interface AuthCallback {
        fun onCodeSent()
        fun onVerificationCompleted() // Auto-retrieval
        fun onVerificationFailed(error: String)
        fun onSignInSuccess()
    }

    fun sendOtp(phoneNumber: String, callback: AuthCallback) {
        // Safety check: Needs +91 or similar code. Appending it if missing.
        val validNumber = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(validNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval or Instant verification handling
                    signInWithPhoneAuthCredential(credential, callback)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    callback.onVerificationFailed(e.localizedMessage ?: "Verification Failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Save verification ID and resend token so we can use them later
                    this@AuthManager.verificationId = verificationId
                    this@AuthManager.resendToken = token
                    callback.onCodeSent()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String, callback: AuthCallback) {
        val id = verificationId
        if (id == null) {
            callback.onVerificationFailed("OTP not sent yet")
            return
        }
        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithPhoneAuthCredential(credential, callback)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, callback: AuthCallback) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    callback.onSignInSuccess()
                    callback.onVerificationCompleted()
                } else {
                    callback.onVerificationFailed(task.exception?.localizedMessage ?: "Sign In Failed")
                }
            }
    }
    
    fun getCurrentUser() = auth.currentUser
    fun signOut() = auth.signOut()
}
