package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultText = intent.getStringExtra("RESULT_TEXT") ?: "Tidak ada hasil"
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        if (imageUri != null) {
            binding.resultImage.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Image tidak tersedia", Toast.LENGTH_SHORT).show()
        }

        binding.resultText.text = resultText
    }


}