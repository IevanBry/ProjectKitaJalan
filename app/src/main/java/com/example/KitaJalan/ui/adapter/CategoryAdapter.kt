package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.databinding.ItemCategoryBinding

data class CategoryItem(val title: String, val imageRes: Int)

class CategoryAdapter(
    private val items: List<CategoryItem>,
    private val onItemClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = items[position]
        holder.bind(categoryItem)
    }

    // Mengembalikan jumlah item dalam data
    override fun getItemCount(): Int = items.size

    // ViewHolder yang menggunakan ViewBinding
    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        // Mengikat data ke ViewBinding
        fun bind(categoryItem: CategoryItem) {
            binding.categoryTitle.text = categoryItem.title
            binding.categoryImage.setImageResource(categoryItem.imageRes)

            // Menangani klik item
            binding.root.setOnClickListener {
                onItemClick(categoryItem)  // Memanggil lambda onItemClick
            }
        }
    }
}
