package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.NewsArticle
import com.example.myapplication.R
import com.example.myapplication.databinding.NewsItemBinding

class NewsAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(url: String)
    }

    inner class ViewHolder(var binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val article = asyncListDiffer.currentList[position]
                listener.onItemClick(article.url)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(dataResponse: List<NewsArticle>) {
        asyncListDiffer.submitList(dataResponse)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = asyncListDiffer.currentList[position]

        holder.binding.apply {
            headlineTextView.text = currentArticle.headline
            author.text = currentArticle.author
            date.text = currentArticle.publishedAt
        Glide
            .with(holder.itemView.context)
            .load(currentArticle.urlToImage)
            .centerCrop()
            .into(imageView)
        }
    }
}