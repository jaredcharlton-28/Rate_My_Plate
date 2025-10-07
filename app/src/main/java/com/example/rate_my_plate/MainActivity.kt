package com.example.rate_my_plate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rate_my_plate.databinding.ActivityMainBinding
import com.example.rate_my_plate.ui.business.AddRestaurantActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var isAdmin: Boolean = false

    // Put your real UID(s) here EXACTLY as shown in Firebase Console
    // Example: "E3q9X3m7abCDEFghiJKLmnOPQR2"
    private val adminUidAllowlist = setOf(
        "AGj6BGgYRygocFv8hjIgqpEZ6fR2"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Your toolbar (keeps menu/consistent UI)
        setSupportActionBar(binding.toolbar)

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Welcome text
        binding.textViewWelcome.text = "Welcome ${user.displayName ?: user.email ?: "User"}"

        // Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Settings
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Add restaurant (admin only)
        binding.btnAddRestaurant.setOnClickListener {
            startActivity(Intent(this, AddRestaurantActivity::class.java))
        }

        // 1) Immediate allowlist check (no network) so the button can show right away
        isAdmin = adminUidAllowlist.contains(user.uid)
        Log.d("MainActivity", "UID=${user.uid}, allowlistHit=$isAdmin (pre-claims)")
        updateAdminUi()

        // 2) Then refresh token to read custom claims (e.g., isAdmin) and update again
        user.getIdToken(true)
            .addOnSuccessListener { result ->
                val claim = (result.claims["isAdmin"] as? Boolean) == true
                val newIsAdmin = claim || adminUidAllowlist.contains(user.uid)
                Log.d(
                    "MainActivity",
                    "UID=${user.uid}, claim.isAdmin=$claim, allowlist=${adminUidAllowlist.contains(user.uid)}, final=$newIsAdmin"
                )
                if (newIsAdmin != isAdmin) {
                    isAdmin = newIsAdmin
                    updateAdminUi()
                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "getIdToken failed: ${e.message}")
                // keep allowlist result already applied
            }
    }

    private fun updateAdminUi() {
        binding.btnAddRestaurant.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        // If you change claims server-side while app is backgrounded, re-apply allowlist quickly
        if (auth.currentUser != null) {
            isAdmin = adminUidAllowlist.contains(auth.currentUser!!.uid)
            updateAdminUi()
        }
    }
}
