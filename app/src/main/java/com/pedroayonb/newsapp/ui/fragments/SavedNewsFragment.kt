package com.pedroayonb.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pedroayonb.newsapp.R
import com.pedroayonb.newsapp.models.Article
import com.pedroayonb.newsapp.presentation.NewsViewModel
import com.pedroayonb.newsapp.databinding.FragmentSavedNewsBinding
import com.pedroayonb.newsapp.ui.MainActivity
import com.pedroayonb.newsapp.ui.adapters.NewsAdapter

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedNewsBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            articles?.let { articleList ->
                newsAdapter = NewsAdapter(articleList)
                newsAdapter.setOnItemClickListener { openArticle(it) }
                setupRecyclerView()

                val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val article = newsAdapter.articleList[position]
                        viewModel.deleteArticle(article)
                        Snackbar.make(
                            binding.root,
                            "Article deleted successfully",
                            Snackbar.LENGTH_SHORT
                        ).setAction("UNDO") {
                            viewModel.saveArticle(article)
                        }.show()
                    }

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return true
                    }
                }
                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedNews)
            }
        })


    }

    private fun openArticle(article: Article) {
        val action =
            SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(
                article
            )
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        binding.rvSavedNews
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}