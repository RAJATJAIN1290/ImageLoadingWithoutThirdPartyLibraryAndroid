package com.example.quantamtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quantamtask.databinding.ActivityMainBinding
import com.example.quantamtask.networkCall.DiskLruCacheHelper
import com.example.quantamtask.networkCall.MemoryCacheHelper
import com.example.quantamtask.networkCall.PexelsResponse
import com.example.quantamtask.networkCall.RetrofitPexelsInstance
//import okhttp3.Call
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageAdapter
    private val pexelsService = RetrofitPexelsInstance.api
    private var currentPage = 1
    private val perPage = 30
    val apiKey = "GtiCvZBvZdNWCwmuZZ4i1OcpdBVFmKYBYMag0rRS10sQiJYTAAIcd5fV"

    private val diskCache: DiskLruCacheHelper by lazy { DiskLruCacheHelper(this) }
    private val memoryCache: MemoryCacheHelper by lazy { MemoryCacheHelper() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        imageAdapter = ImageAdapter(listOf(), diskCache, memoryCache)
        binding.recyclerView.adapter = imageAdapter

        loadImages(currentPage)
        pagination()

    }


    private fun pagination(){
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // If we have reached the end of the list and there are more pages to load
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    currentPage++
                    loadImages(currentPage)
                    cancelOngoingImageLoading()
                }
            }
        })

    }

    private fun loadImages(page: Int) {
        pexelsService.getPhotos("nature",page,perPage,apiKey).enqueue(object : Callback<PexelsResponse> {
           override fun onResponse(call: Call<PexelsResponse>,response: Response<PexelsResponse>) {
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    val imageUrls = it.photos.map { photo -> photo.src.original }
                    imageAdapter.updateData(imageAdapter.imageUrls + imageUrls)
                }
            } else {
                Log.e("MainActivity", "Unsuccessful response: ${response.code()}")
            }
        }

         override fun onFailure(call: Call<PexelsResponse>, t:Throwable) {
             Log.e("MainActivity", "API call failed", t)
         }
        })
    }

    private fun cancelOngoingImageLoading() {
        // Iterate through all child views of RecyclerView and cancel any ongoing image loading tasks
        for (i in 0 until binding.recyclerView.childCount) {
            val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder is ImageAdapter.ImageViewHolder) {
                viewHolder.binding.imageView.setImageBitmap(null) // Clear the ImageView
            }
        }
    }


}