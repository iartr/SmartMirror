package com.iartr.smartmirror.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iartr.smartmirror.news.impl.R
import javax.inject.Inject

internal class NewsAdapter @Inject constructor() : ListAdapter<News, NewsAdapter.ArticlesViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        return ArticlesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false))
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ArticlesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.article_image)
        private val title: TextView = view.findViewById(R.id.article_title)

        fun bind(news: News) {
            Glide.with(itemView).load(news.urlToImage).into(imageView)
            title.text = news.title
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }

    }
}