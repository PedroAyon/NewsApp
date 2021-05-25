package com.pedroayonb.newsapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pedroayonb.newsapp.R
import com.pedroayonb.newsapp.databinding.ActivityMainBinding
import com.pedroayonb.newsapp.db.ArticleDatabase
import com.pedroayonb.newsapp.presentation.NewsViewModel
import com.pedroayonb.newsapp.presentation.NewsViewModelProviderFactory
import com.pedroayonb.newsapp.repository.NewsRepository
import com.pedroayonb.newsapp.utils.hide
import com.pedroayonb.newsapp.utils.show

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val viewModel by viewModels<NewsViewModel> {
        NewsViewModelProviderFactory(application, NewsRepository(ArticleDatabase.getDatabase(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.articleFragment -> {
                    binding.bottomNavigationView.hide()
                    binding.appBarLayout.hide()
                }
                else -> {
                    binding.bottomNavigationView.show()
                    binding.appBarLayout.show()
                }
            }
        }

    }
}