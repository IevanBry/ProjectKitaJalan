package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.data.model.CommentModel
import com.example.KitaJalan.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter : ListAdapter<CommentModel, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentModel) {
            binding.commentUsername.text = comment.namaUser

            binding.commentText.text = comment.komentar

            val fullDateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
            val displayDateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
            val formattedDate = try {
                val date = fullDateFormat.parse(comment.tanggal)
                displayDateFormat.format(date)
            } catch (e: Exception) {
                comment.tanggal
            }
            binding.commentTime.text = formattedDate
            binding.commentRatingBar.rating = comment.rating.toFloat()
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem.userId == newItem.userId && oldItem.tanggal == newItem.tanggal
        }

        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }
    }
}