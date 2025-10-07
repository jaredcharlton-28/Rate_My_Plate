package com.example.rate_my_plate.ui.business

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rate_my_plate.R
import com.example.rate_my_plate.data.repository.BusinessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddRestaurantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_restaurant)

        val etName = findViewById<EditText>(R.id.etName)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etThumb = findViewById<EditText>(R.id.etThumbnail)
        val btn = findViewById<Button>(R.id.btnSave)

        btn.setOnClickListener {
            val name = etName.text.toString().trim()
            val cat = etCategory.text.toString().trim()
            val desc = etDescription.text.toString().trim().ifEmpty { null }
            val rawThumb = etThumb.text.toString().trim()
            val thumb = rawThumb.ifEmpty { null }?.let { u ->
                if (u.startsWith("http://") || u.startsWith("https://")) u else "https://$u"
            }

            if (name.isEmpty() || cat.isEmpty()) {
                Toast.makeText(this, "Name and category required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("AddRestaurant", "Create -> name=$name, category=$cat, desc=$desc, thumb=$thumb")
                    val resp = BusinessRepository().createBusiness(name, cat, desc, thumb)
                    if (resp.isSuccessful) {
                        Toast.makeText(this@AddRestaurantActivity, "Added!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK); finish()
                    } else {
                        val err = resp.errorBody()?.string()
                        Log.e("AddRestaurant", "POST failed: code=${resp.code()} body=$err")
                        Toast.makeText(
                            this@AddRestaurantActivity,
                            "Error ${resp.code()} ${resp.message()} ${err ?: ""}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("AddRestaurant", "Exception creating business", e)
                    Toast.makeText(this@AddRestaurantActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
