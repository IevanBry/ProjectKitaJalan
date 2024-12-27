package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.data.model.CommentModel
import com.example.KitaJalan.data.repository.CommentRepository
import com.example.KitaJalan.databinding.FragmentCommentBinding
import com.example.KitaJalan.ui.adapter.CommentAdapter
import com.example.KitaJalan.ui.viewModel.CommentViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    private val commentViewModel: CommentViewModel by viewModels {
        ViewModelFactory(CommentViewModel::class.java) {
            val repository = CommentRepository()
            CommentViewModel(repository)
        }
    }

    private val commentAdapter = CommentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observeViewModel()

        binding.postCommentButton.setOnClickListener {
            val destinasiId = arguments?.getString("id") ?: ""
            if (destinasiId.isNotEmpty()) {
                val comment = createComment(destinasiId)
                if (comment != null) {
                    lifecycleScope.launch {
                        commentViewModel.addComment(comment)
                    }
                } else {
                    Toast.makeText(context, "Tolong berikan ulasan anda", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Destination ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        fetchComments()
    }

    private fun setupRecyclerView() {
        binding.commentRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun observeViewModel() {
        commentViewModel.comments.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.emptyComment.root.visibility = View.GONE
                    binding.loadingComment.root.visibility = View.VISIBLE
                    binding.errorComment.root.visibility = View.GONE
                    binding.commentRecyclerView.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.emptyComment.root.visibility = View.GONE
                    binding.loadingComment.root.visibility = View.GONE
                    binding.errorComment.root.visibility = View.GONE
                    binding.commentRecyclerView.visibility = View.VISIBLE

                    val comments = resource.data ?: emptyList()
                    commentAdapter.submitList(comments)

                    updateCommentCount(comments.size)
                }
                is Resource.Error -> {
                    binding.emptyComment.root.visibility = View.GONE
                    binding.loadingComment.root.visibility = View.GONE
                    binding.errorComment.root.visibility = View.VISIBLE
                    binding.commentRecyclerView.visibility = View.GONE
                    binding.errorComment.errorMessage.text = resource.message
                    binding.errorComment.retryButton.setOnClickListener {
                        fetchComments()
                    }
                }
                is Resource.Empty -> {
                    binding.emptyComment.root.visibility = View.VISIBLE
                    binding.loadingComment.root.visibility = View.GONE
                    binding.errorComment.root.visibility = View.GONE
                    binding.commentRecyclerView.visibility = View.GONE
                    binding.emptyComment.emptyMessage.text = resource.message

                    updateCommentCount(0)
                }
            }
        }

        commentViewModel.addCommentStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.postCommentButton.isEnabled = false
                is Resource.Success -> {
                    binding.postCommentButton.isEnabled = true
                    fetchComments()

                    val destinasiId = arguments?.getString("id") ?: ""
                    if (destinasiId.isNotEmpty()) {
                        commentViewModel.calculateAverageRating(destinasiId) { averageRating ->
                            sendAverageRatingToDetailView(averageRating)
                        }
                    }
                    clearCommentInput()
                    Toast.makeText(context, "Berhasil menambahkan komentar", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.postCommentButton.isEnabled = true
                    Toast.makeText(context, "Gagal menambahkan komentar: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun sendAverageRatingToDetailView(averageRating: Double) {
        parentFragmentManager.setFragmentResult(
            "updateRating",
            Bundle().apply {
                putDouble("averageRating", averageRating)
            }
        )
    }

    private fun fetchComments() {
        val destinasiId = arguments?.getString("id") ?: ""
        if (destinasiId.isNotEmpty()) {
            commentViewModel.fetchComments(destinasiId)
        } else {
            Toast.makeText(requireContext(), "Destination ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createComment(destinasiId: String): CommentModel? {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return null
        }

        val userId = currentUser.uid
        val email = currentUser.email ?: ""
        val namaUser = email.substringBefore("@")

        val commentText = binding.commentEditText.text.toString()
        val rating = binding.ratingBar.rating.toDouble()

        val currentDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        return if (commentText.isNotEmpty() && rating > 0) {
            CommentModel(
                destinasiId = destinasiId,
                userId = userId,
                namaUser = namaUser,
                komentar = commentText,
                rating = rating,
                tanggal = formattedDate
            )
        } else {
            Toast.makeText(requireContext(), "Tolong berikan rating anda terlebih dahulu", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun clearCommentInput() {
        binding.commentEditText.text.clear()
        binding.ratingBar.rating = 0.0f
    }

    private fun updateCommentCount(count: Int) {
        val formattedCount = String.format("%,d", count)
        binding.commentTitle.text = "$formattedCount Comments"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}