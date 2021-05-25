package com.pedroayonb.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.pedroayonb.newsapp.R
import com.pedroayonb.newsapp.databinding.FragmentArticleBinding
import com.pedroayonb.newsapp.presentation.NewsViewModel
import com.pedroayonb.newsapp.ui.MainActivity


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel: NewsViewModel
    private val args by navArgs<ArticleFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        binding.webView.apply {
            webViewClient = WebViewClient()
            args.article.url?.let { loadUrl(it) }
        }
        binding.fab.setOnClickListener { fab ->
            fab.isEnabled = false
            viewModel.saveArticle(args.article)
            Snackbar.make(binding.root, "Article saved successfully", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    viewModel.deleteLastArticle()
                    fab.isEnabled = true
                }.show()


        }

    }
}