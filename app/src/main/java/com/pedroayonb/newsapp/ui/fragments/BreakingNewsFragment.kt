package com.pedroayonb.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pedroayonb.newsapp.R
import com.pedroayonb.newsapp.databinding.FragmentBreakingNewsBinding
import com.pedroayonb.newsapp.models.Article
import com.pedroayonb.newsapp.presentation.NewsViewModel
import com.pedroayonb.newsapp.ui.MainActivity
import com.pedroayonb.newsapp.ui.adapters.NewsAdapter
import com.pedroayonb.newsapp.utils.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var binding: FragmentBreakingNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var firstTimeLoadingPages = true
    var lastVisibleItemBeforeLoadingMorePages: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBreakingNewsBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        viewModel.breakingNewsData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter = NewsAdapter(newsResponse.articles.toList())
                        newsAdapter.setOnItemClickListener { openArticle(it) }
                        setupRecyclerView()
                        val totalPages = newsResponse.totalResults / Constants.queryPageSize + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                    if (!firstTimeLoadingPages) {
                        binding.rvBreakingNews.scrollToPosition(
                            lastVisibleItemBeforeLoadingMorePages
                        )
                    }
                    firstTimeLoadingPages = false
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d("BreakingNewsFragment", "An error occurred: $message")
                    }
                }
                is Resource.Loading -> showProgressBar()

            }
        })
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.show()
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.hide()
        isLoading = false
    }


    private fun openArticle(article: Article) {
        val action =
            BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(
                article
            )
        findNavController().navigate(action)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.rvBreakingNews.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotAtTheLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.queryPageSize
            val shouldPaginate =
                isNotLoadingAndNotAtTheLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                lastVisibleItemBeforeLoadingMorePages = firstVisibleItemPosition+1
                viewModel.getBreakingNews(Constants.countryCode)
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvBreakingNews
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}