package com.example.rate_my_plate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rate_my_plate.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If already logged in, go to Home
        auth.currentUser?.let { goToHome() }

        binding.btnSignUp.setOnClickListener { emailSignUp() }
        binding.btnLogin.setOnClickListener { emailLogin() }
    }

    private fun emailSignUp() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val pass = binding.etPassword.text?.toString().orEmpty()

        if (email.isBlank() || pass.length < 6) {
            toast("Enter valid email and 6+ char password")
            return
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) goToHome()
            else toast("Sign up failed: ${it.exception?.localizedMessage}")
        }
    }

    private fun emailLogin() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val pass = binding.etPassword.text?.toString().orEmpty()

        if (email.isBlank() || pass.isBlank()) {
            toast("Email & password required")
            return
        }

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) goToHome()
            else toast("Login failed: ${it.exception?.localizedMessage}")
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
