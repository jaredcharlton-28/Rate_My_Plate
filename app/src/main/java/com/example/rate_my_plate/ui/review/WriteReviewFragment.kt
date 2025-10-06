package com.ratemyplate.ui.review

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ratemyplate.R
import com.ratemyplate.data.model.Review
import com.ratemyplate.vm.ReviewViewModel
import kotlinx.coroutines.launch

class WriteReviewFragment : Fragment(R.layout.fragment_write_review) {

    private lateinit var ivPhoto: ImageView
    private lateinit var btnChoose: Button
    private lateinit var ratingBar: RatingBar
    private lateinit var etComment: EditText
    private lateinit var btnSubmit: Button
    private lateinit var vm: ReviewViewModel
    private var selectedImageUri: Uri? = null

    companion object {
        private const val REQUEST_IMAGE = 101
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPhoto = view.findViewById(R.id.ivPhoto)
        btnChoose = view.findViewById(R.id.btnChoosePhoto)
        ratingBar = view.findViewById(R.id.ratingBar)
        etComment = view.findViewById(R.id.etComment)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        vm = ViewModelProvider(this).get(ReviewViewModel::class.java)

        btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val comment = etComment.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(requireContext(), "Please give a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                Toast.makeText(requireContext(), "Please write a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val restaurantId = arguments?.getString("restaurantId") ?: "demo_restaurant"
            val userId = "demo_user" // replace with FirebaseAuth.getInstance().uid if you use Firebase

            val review = Review(
                restaurantId = restaurantId,
                userId = userId,
                userName = "Teshar",
                rating = rating,
                comment = comment,
                imageUrl = null // We'll send null; replace if your API supports image upload
            )

            lifecycleScope.launch {
                vm.postReview(review)
            }

            vm.postResult.observe(viewLifecycleOwner) { res ->
                res.onSuccess {
                    Toast.makeText(requireContext(), "Review posted!", Toast.LENGTH_SHORT).show()
                }
                res.onFailure { e ->
                    Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Glide.with(this).load(selectedImageUri).into(ivPhoto)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
           }
        }
}