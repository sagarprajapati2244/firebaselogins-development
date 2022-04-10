package com.example.firebaselogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.concurrent.TimeUnit

class FirebasePhoneAuth(context: Context) {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var storedVerificationId:String
     lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
     var phoneNumberUtil:PhoneNumberUtil = PhoneNumberUtil.getInstance()
     var activity: Activity = Activity()

    var callBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            Log.d("TAG","onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    fun phoneLogin(phoneNumber:String)
    {
        firebaseAuth = FirebaseAuth.getInstance()
        if (phoneNumber.isNotEmpty())
        {
            sendVerificationCode(phoneNumber)
        }
        else
        {
            Toast.makeText(activity, "Enter Mobile Number", Toast.LENGTH_SHORT).show()
            return
        }
    }

    fun checkVerification(number: String)
    {
        if(number.isNotEmpty()){
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId, number)
            signInWithPhoneAuthCredential(credential)
        }else{
            Toast.makeText(activity,"Enter OTP",Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(activity, "Success Logged In", Toast.LENGTH_SHORT).show()
            }
//            .addOnCompleteListener(context) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(context, "Successfully Verified", Toast.LENGTH_SHORT).show()
//// ...
//                } else {
//// Sign in failed, display a message and update the UI
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//// The verification code entered was invalid
//                        Toast.makeText(context,"Invalid OTP",Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
            .addOnFailureListener {
                Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callBacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendVerificationCode(phone:String)
    {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callBacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(verificationId:String,code:String)
    {
        val credential = PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)
    }
}