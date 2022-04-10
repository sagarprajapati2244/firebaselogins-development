package com.example.firebaselogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firebaselogin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseGoogleAuth: FirebaseGoogleAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseFacebookAuth: FirebaseFacebookAuth
    private lateinit var firebasePhoneAuth: FirebasePhoneAuth
    private val user = FirebaseAuth.getInstance().currentUser
    private var number =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Google Authentication
        firebaseGoogleAuth = FirebaseGoogleAuth(this)
        firebaseGoogleAuth.connectGoogle(applicationContext)



        if (firebaseGoogleAuth.userExistORNot()) {

            Toast.makeText(this, "The User Is Logged In$user", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "The User Is Not Logged In$user", Toast.LENGTH_SHORT).show()
        }
        binding.btnSign.setOnClickListener {
            firebaseGoogleAuth.signInGoogle(this)
            binding.btnSignOut.visibility = View.VISIBLE
            binding.btnSign.visibility = View.GONE
        }

        binding.btnSignOut.setOnClickListener {
            firebaseGoogleAuth.signOutGoogle(this)
            binding.btnSignOut.visibility = View.GONE
            binding.btnSign.visibility = View.VISIBLE
        }


        //Facebook Authentication
        firebaseFacebookAuth = FirebaseFacebookAuth(this)
        firebaseFacebookAuth.facebookLogin(this)
        binding.btnFacebook.setOnClickListener {
            firebaseFacebookAuth.executeLogin(this)
        }


        // Phone Authentication
        firebasePhoneAuth = FirebasePhoneAuth(this)


        binding.loginBtn.setOnClickListener {
            number = binding.phoneNumber.text.toString().trim()
            Log.e("num",number)
            firebasePhoneAuth.phoneLogin(number)
            binding.oneNumber.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
            binding.loginVBtn.visibility = View.VISIBLE
        }
        binding.loginVBtn.setOnClickListener {
            number = binding.oneNumber.text.toString().trim()
            firebasePhoneAuth.checkVerification(number)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        firebaseGoogleAuth.onActivityResult(requestCode, data)
        firebaseFacebookAuth.onActivityResultFB(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }
}