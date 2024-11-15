package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.local.Entity.SaveResult
import com.dicoding.asclepius.data.local.Room.ResultDatabase
import com.dicoding.asclepius.databinding.ActivitySaveResultBinding
import kotlinx.coroutines.launch

class SaveResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveResultBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var database: ResultDatabase

    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Hasil Prediksi"

        database = ResultDatabase.getDatabase(this)

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                setupRecyclerView()
                loadResults()
            }
        } else {
            setupRecyclerView()
            loadResults()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSaveResult.layoutManager = LinearLayoutManager(this)
        adapter = ResultAdapter(emptyList()) { result ->
            openResultDetail(result)
        }
        binding.rvSaveResult.adapter = adapter
    }

    private fun loadResults() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            try {
                val results = database.saveResultDao().getAllResult()
                if (results.isNotEmpty()) {
                    adapter.updateResults(results)
                    binding.rvSaveResult.visibility = View.VISIBLE
                    binding.tvEmptyMessage.visibility = View.GONE
                } else {
                    binding.rvSaveResult.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvEmptyMessage.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun openResultDetail(result: SaveResult) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT_TEXT", result.result)
            putExtra("IMAGE_URI", result.imageUri)
            putExtra("CONFIDENCE_SCORE", result.confidenceScore.toString())
            putExtra("IS_SAVE_RESULT", true)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRecyclerView()
                loadResults()
            } else {
                Toast.makeText(this, "Permission denied. Cannot access storage.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
