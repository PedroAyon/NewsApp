package com.pedroayonb.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pedroayonb.newsapp.databinding.ItemArticlePreviewBinding
import com.pedroayonb.newsapp.models.Article

class NewsAdapter(val articleList: List<Article>) :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articleList[position]
        Glide.with(holder.binding.root).load(article.urlToImage).into(holder.binding.ivArticleImage)
        holder.binding.tvSource.text = article.source?.name
        holder.binding.tvTitle.text = article.title
        holder.binding.tvDescription.text = article.description
        article.publishedAt?.let {
            holder.binding.tvPublishedAt.text =
                article.publishedAt.dropLast(article.publishedAt.length - 10)
        }
        holder.binding.root.setOnClickListener {
            onItemClickListener?.let { it(article) }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = articleList.size
}