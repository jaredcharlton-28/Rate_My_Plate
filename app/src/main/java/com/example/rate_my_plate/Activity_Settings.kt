package com.example.rate_my_plate

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rate_my_plate.databinding.ActivitySettingBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // simple local prefs for notification + (legacy) settings
    private val prefs by lazy { getSharedPreferences("user_settings", MODE_PRIVATE) }

    // used to prevent spinner onItemSelected firing during initial setup
    private var isLanguageInit = true

    // Apply saved language to this Activity
    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleManager.getSavedLanguage(newBase)
        val wrapped = LocaleManager.setLocale(newBase, lang)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar back
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Load account details
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val created = user.metadata?.creationTimestamp ?: 0L
        val lastSignIn = user.metadata?.lastSignInTimestamp ?: 0L

        binding.tvUid.text = getString(R.string.uid_fmt, user.uid)
        binding.tvEmail.text = getString(R.string.email_fmt, user.email ?: "—")
        binding.tvVerified.text = getString(
            R.string.verified_fmt,
            if (user.isEmailVerified) "Yes" else "No"
        )
        binding.tvCreated.text = getString(R.string.created_fmt, formatTs(created))
        binding.tvLastSignIn.text = getString(R.string.last_signin_fmt, formatTs(lastSignIn))

        // Change email (reauth then update)
        binding.btnUpdateEmail.setOnClickListener {
            val newEmail = binding.etNewEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            when {
                newEmail.isBlank() -> {
                    binding.tilNewEmail.error = "Enter a valid email"
                    return@setOnClickListener
                }

                password.length < 6 -> {
                    binding.tilPassword.error = "Enter your current password"
                    return@setOnClickListener
                }

                else -> {
                    binding.tilNewEmail.error = null
                    binding.tilPassword.error = null
                }
            }

            val currentEmail = user.email ?: run {
                Toast.makeText(this, "Current email unavailable", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            user.reauthenticate(credential).addOnCompleteListener { reauth ->
                if (!reauth.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Re-auth failed: ${reauth.exception?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnCompleteListener
                }
                user.updateEmail(newEmail).addOnCompleteListener { upd ->
                    if (upd.isSuccessful) {
                        Toast.makeText(this, "Email updated to $newEmail", Toast.LENGTH_SHORT)
                            .show()
                        binding.tvEmail.text = getString(R.string.email_fmt, newEmail)
                        user.sendEmailVerification()
                    } else {
                        Toast.makeText(
                            this,
                            "Update failed: ${upd.exception?.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // === Preferences: notifications + language ===

        // Notifications
        val notifOn = prefs.getBoolean("notifications", true)
        binding.switchNotifications.isChecked = notifOn
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
        }

        // Language: labels + ISO codes
        val languageLabels = listOf("English", "isiZulu", "Afrikaans")
        val languageCodes = listOf("en", "zu", "af")

        val langAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageLabels
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerLanguage.adapter = langAdapter

        // Load saved language code from LocaleManager
        val savedCode = LocaleManager.getSavedLanguage(this)
        val selectedIndex = languageCodes.indexOf(savedCode).takeIf { it >= 0 } ?: 0

        // Mark that we're in init phase before setting selection
        isLanguageInit = true
        binding.spinnerLanguage.setSelection(selectedIndex, false)
        // After the next layout pass, we allow handling real user selections
        binding.spinnerLanguage.post { isLanguageInit = false }

        binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Ignore callbacks fired during initialization
                    if (isLanguageInit) return

                    val selectedCode = languageCodes[position]

                    // If user chose the same language, do nothing
                    val current = LocaleManager.getSavedLanguage(this@SettingsActivity)
                    if (selectedCode == current) return

                    // Save new language; attachBaseContext will apply it on recreate
                    LocaleManager.saveLanguage(this@SettingsActivity, selectedCode)

                    // Recreate AFTER spinner popup has closed
                    binding.spinnerLanguage.post {
                        recreate()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // no-op
                }
            }

        // === Sync Now / Offline status ===
        binding.btnSyncNow.setOnClickListener {
            lifecycleScope.launch {
                // Show that offline/sync mode is active
                binding.tvOfflineStatus.text = getString(R.string.offline_mode_active)

                // Simulate some sync work (network/db)
                delay(1000)

                // Mark as complete
                binding.tvOfflineStatus.text = getString(R.string.sync_complete)
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.sync_complete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
//
    private fun formatTs(ts: Long): String =
        if (ts <= 0L) "—" else DateFormat.format("yyyy-MM-dd HH:mm", Date(ts)).toString()
}
