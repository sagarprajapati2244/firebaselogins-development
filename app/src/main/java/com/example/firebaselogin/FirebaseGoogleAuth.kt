package com.example.firebaselogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

open class FirebaseGoogleAuth(private val context: Context) {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 1
        const val TAG = "FirebaseGoogleAuth"
    }

    fun connectGoogle(applicationContext: Context) {
        firebaseAuth = FirebaseAuth.getInstance()

        val gsm = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.web_client_id)) //Kindly Take A String Of Your Firebase Google Authentication Web Client Id In Resource String
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gsm)


    }

    fun signInGoogle(signInContext: Activity) {
        val intent = googleSignInClient.signInIntent
        signInContext.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun signOutGoogle(signOutContext:Context)
    {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(context, "Logging Out", Toast.LENGTH_SHORT).show()
        }
    }

    fun userExistORNot():Boolean
    {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user!=null) {
            Log.e("UserLogin","----User  Logged In $user")
            true
        } else {
            Log.e("UserLogin","----User Not Logged In $user")
            false
        }

    }

    fun onActivityResult(requestCode: Int, data: Intent?) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val authentication = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(authentication)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(context, "Sign in Successfully${it.result}", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d(TAG, "signInWithCredential:failure${it.exception}")
                }
            }

    }

}