package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener{
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Aplikasi Deteksi Kanker"

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        binding.analyzeButton.setOnClickListener{
            analyzeImage()
        }
        binding.galleryButton.setOnClickListener{
            startGallery()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK && result.data != null){
            currentImageUri = result.data?.data
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        } ?: showToast("Image tidak terpilih")
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            imageClassifierHelper.classifyStaticImage(uri)
        }?: showToast("Silahkan pilih image terlebih dahulu")
    }

    override fun onError(error: String) {
        showToast("Error: $error")
    }

    override fun onResults(result: List<Classifications>?, inferenceTime : Long){
        val resultText = result?.joinToString("\n"){ classifications ->
            classifications.categories.joinToString { category ->
                "${category.label}: ${(category.score * 100).toInt()}%"
            }
        }?: "Tidak Ditemukan Hasil"

        val resultKlasifikasi = "Results:\n" +
                "$resultText\n" +
                "Inference time: $inferenceTime ms"

        moveToResult(resultKlasifikasi, currentImageUri)
    }



    private fun moveToResult(resultText: String, imageUri: Uri?) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("RESULT_TEXT", resultText)
        intent.putExtra("IMAGE_URI", imageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}