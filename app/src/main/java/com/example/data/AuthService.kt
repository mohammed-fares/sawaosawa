package com.example.data

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object AuthService {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Start phone number verification via Firebase PhoneAuthProvider.
     */
    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onVerificationCompleted(credential)
                        } else {
                            onError(task.exception?.localizedMessage ?: "فشل تسجيل الدخول التلقائي")
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // If Play Services/SafetyNet isn't configured in test container, allow smooth fallback
                // with fallback verificationId
                onError(e.localizedMessage ?: "فشل إرسال رمز التحقق عبر SMS")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "حدث خطأ أثناء طلب رمز التحقق")
        }
    }

    /**
     * Verify OTP code entered by user.
     */
    fun verifyOtp(
        verificationId: String,
        code: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(auth.currentUser)
                    } else {
                        onError(task.exception?.localizedMessage ?: "رمز التحقق غير صحيح أو منتهي الصلاحية")
                    }
                }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "فشل في معالجة رمز التحقق")
        }
    }

    /**
     * Email / Password Login.
     */
    fun signInWithEmail(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(auth.currentUser)
                } else {
                    onError(task.exception?.localizedMessage ?: "فشل تسجيل الدخول بالبريد الإلكتروني")
                }
            }
    }

    /**
     * Email / Password Registration.
     */
    fun registerWithEmail(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(auth.currentUser)
                } else {
                    onError(task.exception?.localizedMessage ?: "فشل إنشاء الحساب بالبريد الإلكتروني")
                }
            }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
    }
}
