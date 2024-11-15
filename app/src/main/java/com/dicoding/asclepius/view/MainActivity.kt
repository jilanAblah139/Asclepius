package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener{
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.currentImageURI?.let { uri ->
            showImage(uri)
        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.save_result_page -> {
                val intent = Intent(this, SaveResultActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                viewModel.currentImageURI = selectedImageUri
                currentImageUri = selectedImageUri
                showImage(selectedImageUri)
            }
        }
    }

    private fun showImage(resultUri: Uri) {
        binding.previewImageView.setImageURI(resultUri)
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            imageClassifierHelper.classifyStaticImage(uri)
        }?: showToast("Silahkan pilih image terlebih dahulu")
    }

    override fun onError(error: String) {
        showToast("Error: $error")
    }

    override fun onResults(results: List<Classifications>?, inferenceTime : Long){
        val resultPrediction = results?.flatMap { classification ->
            classification.categories
        }?.maxByOrNull { it.score }

        val resultText = if (resultPrediction != null){
            resultPrediction.label
        }else{
            "Tidak Ditemukan Hasil"
        }
        val confidenceScore = if (resultPrediction != null){
            "Confidence Score: ${(resultPrediction.score * 100).toInt()}%"
        }else {
            "Tidak Ada Hasil"
        }

        moveToResult(resultText, confidenceScore, currentImageUri)
    }


    private fun moveToResult(resultText: String, confidenceScore: String, imageUri: Uri?) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT_TEXT", resultText)
            putExtra("IMAGE_URI", imageUri.toString())
            putExtra("CONFIDENCE_SCORE", confidenceScore)

        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}