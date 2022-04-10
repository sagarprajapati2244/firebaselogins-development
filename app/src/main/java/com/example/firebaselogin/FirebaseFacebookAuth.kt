package com.example.firebaselogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject


class FirebaseFacebookAuth(private val context: Context) {
    private lateinit var firebaseAuth: FirebaseAuth
    var callbackManager: CallbackManager? = null
    fun facebookLogin(logContext: Context) {
        callbackManager = create()
        firebaseAuth = FirebaseAuth.getInstance()

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {

            override fun onCancel() {
                Toast.makeText(logContext, "cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(logContext, "error log", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                Toast.makeText(logContext, "Successfully login", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener {
                Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                val email = it.user?.email
                val graphRequest = GraphRequest.newMeRequest(accessToken){`object`,response->
                    getFacebookData(`object`)
                }
                val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
                Toast.makeText(context, "You logged with this email $email", Toast.LENGTH_SHORT).show()
            }

    }

    private fun getFacebookData(jsonObject: JSONObject?) {
        try {
                val firstName = jsonObject?.getString("first_name")
                val lastName = jsonObject?.getString("last_name")
                val email = jsonObject?.getString("email")
                val id = jsonObject?.getString("id")
            Log.d("Name", "$firstName $lastName")
                Log.d("email", email!!)
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(context, "exception", Toast.LENGTH_SHORT).show()
                Log.d("Error_fb", "fb" + e.message)
            }
    }

    fun onActivityResultFB(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    fun executeLogin(activity:Activity)
    {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email","public_profile"))
    }

    var accessTokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) {
                Toast.makeText(context, "User Logged out", Toast.LENGTH_LONG).show()
//            } else loadUserProfile(currentAccessToken)
            }
        }
    }

//    private fun loadUserProfile(newAccessToken: AccessToken?) {
//        val request = GraphRequest.newMeRequest(newAccessToken
//        ) { `object`, _ ->
//            try {
//                val firstName = `object`!!.getString("first_name")
//                val lastName = `object`.getString("last_name")
//                val email = `object`.getString("email")
//                val id = `object`.getString("id")
//                val imageUrl =
//                    "https://graph.facebook.com/$id/picture?type=normal"
//                Log.d("Name", "$firstName $lastName")
//                Log.d("email", email)
//                Log.d("image", imageUrl)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//                Toast.makeText(context, "exception", Toast.LENGTH_SHORT).show()
//                Log.d("Error_fb", "fb" + e.message)
//            }
//        }
//        val parameters = Bundle()
//        parameters.putString("fields", "first_name,last_name,email,id")
//        request.parameters = parameters
//        request.executeAsync()
//    }
//
//    fun checkLoginStatus() {
//        if (AccessToken.getCurrentAccessToken() != null) {
//            loadUserProfile(AccessToken.getCurrentAccessToken())
//        }
//    }
}

