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

    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: CategoryItem) {
            binding.categoryTitle.text = categoryItem.title
            binding.categoryImage.setImageResource(categoryItem.imageRes)

            binding.root.setOnClickListener {
                onItemClick(categoryItem)
            }
        }
    }
}
