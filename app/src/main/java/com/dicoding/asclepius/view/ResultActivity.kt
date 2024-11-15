package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.Entity.SaveResult
import com.dicoding.asclepius.data.local.Room.ResultDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var database: ResultDatabase

    private fun saveResultToDatabase(imageUri: String, result: String, confidenceScore: String) {
        val saveResult = SaveResult(
            imageUri = imageUri,
            result = result,
            confidenceScore = confidenceScore
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                database.saveResultDao().insertResult(saveResult)
            }
            Toast.makeText(this@ResultActivity, "Hasil telah disimpan ke database", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ResultDatabase.getDatabase(this)

        val resultText = intent.getStringExtra("RESULT_TEXT") ?: "Tidak ada hasil"
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val confidenceScore = intent.getStringExtra("CONFIDENCE_SCORE")
        val isFromSaveResult = intent.getBooleanExtra("IS_SAVE_RESULT", false)

        val imageUri = imageUriString?.let { Uri.parse(it) }

        if (imageUri != null) {
            binding.resultImage.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Image tidak tersedia", Toast.LENGTH_SHORT).show()
        }

        binding.resultText.text = resultText
        binding.confidenceScore.text = confidenceScore

       if (!isFromSaveResult){
           saveResultToDatabase(imageUri.toString(), resultText, confidenceScore.toString())
       }

    }


}