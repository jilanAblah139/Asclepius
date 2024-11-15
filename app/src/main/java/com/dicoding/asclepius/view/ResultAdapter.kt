package com.dicoding.asclepius.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.Entity.SaveResult
import com.dicoding.asclepius.databinding.ItemListBinding

class ResultAdapter(
    private var results: List<SaveResult>,
    private val onItemClick: (SaveResult) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(
        private val binding: ItemListBinding,
        private val onItemClick: (SaveResult) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SaveResult) {
            binding.tvItemName.text = result.result
            binding.tvItemDescription.text = result.confidenceScore
            val imageUri = Uri.parse(result.imageUri)
            binding.imgItemPhoto.setImageURI(imageUri)

            itemView.setOnClickListener {
                onItemClick(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding, onItemClick)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(results[position])
    }

    fun updateResults(newResults: List<SaveResult>) {
        results = newResults
        notifyDataSetChanged()
    }
}
