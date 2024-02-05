package com.example.myapplication

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.NewsAdapter
import com.example.myapplication.data.NewsArticle
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.networking.NetworkState
import com.example.myapplication.networking.RemoteAPIService
import com.example.myapplication.repository.NewsRepository
import com.example.myapplication.repository.NewsRepositoryImpl
import com.example.myapplication.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NewsAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: NewsAdapter
    private lateinit var mSorting: TextView

    private val networkState by lazy {
        NetworkState(getSystemService(ConnectivityManager::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isNetworkConnected = networkState.performAction {

            val newsViewModel = NewsViewModel(NewsRepositoryImpl(RemoteAPIService()))

            mAdapter = NewsAdapter(this)
            mSorting = binding.sort
            mRecyclerView = binding.recyclerView
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mRecyclerView.adapter = mAdapter

            newsViewModel.fetchNews.observe(this) { news ->
                mAdapter.saveData(news)
            }

            newsViewModel.errorResponse.observe(this) { errorMessage ->
                Snackbar.make(binding.root, errorMessage.toString(), Snackbar.LENGTH_SHORT).show()
            }

            newsViewModel.getNews()


            mSorting.setOnClickListener {view ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.menuInflater.inflate(R.menu.sorting, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item?.itemId) {
                            R.id.sort_by_asc -> {
                                newsViewModel.sortData(true)
                                return true
                            }
                            R.id.sort_by_des -> {
                                newsViewModel.sortData(false)
                                return true
                            }
                            else -> return false
                        }
                    }
                })

                // Show the PopupMenu
                popupMenu.show()
            }
        }

        if (isNetworkConnected) {
            binding.group.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
        } else {
            binding.noInternet.visibility = View.VISIBLE
            binding.group.visibility = View.GONE
        }
    }


    // Click on News Article to view complete news on browser
    override fun onItemClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}