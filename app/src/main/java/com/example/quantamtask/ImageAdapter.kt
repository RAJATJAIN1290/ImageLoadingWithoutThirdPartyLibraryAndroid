package com.example.quantamtask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.quantamtask.databinding.EachItemBinding
import com.example.quantamtask.networkCall.DiskLruCacheHelper
import com.example.quantamtask.networkCall.MemoryCacheHelper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
//import retrofit2.Call
//import retrofit2.Callback
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executors

class ImageAdapter(
    var imageUrls: List<String>,
    private val diskCache: DiskLruCacheHelper,
    private val memoryCache: MemoryCacheHelper
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val executor = Executors.newFixedThreadPool(5)
    private val client = OkHttpClient()

    class ImageViewHolder(val binding: EachItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding =
            EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.binding.imageView.setImageResource(R.drawable.ic_launcher_background)

        // Load image asynchronously
        executor.submit {
            val cachedBitmap = memoryCache.get(imageUrl)
            if (cachedBitmap != null) {
                holder.binding.imageView.post {
                    holder.binding.imageView.setImageBitmap(cachedBitmap)
                }
            } else {
                val diskCachedBitmap = diskCache.get(imageUrl)
                if (diskCachedBitmap != null) {
                    memoryCache.put(imageUrl, diskCachedBitmap)
                    holder.binding.imageView.post {
                        holder.binding.imageView.setImageBitmap(diskCachedBitmap)
                    }
                } else {
                    loadImage(imageUrl, holder.binding.imageView)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    fun updateData(newImageUrls: List<String>) {
        imageUrls = newImageUrls
        notifyDataSetChanged()
    }


    private fun loadImage(url: String, imageView: ImageView) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                imageView.post {
                    imageView.setImageResource(R.drawable.ic_launcher_background) // Set an error placeholder image
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody: ResponseBody? = response.body
                    responseBody?.use { body ->
                        val inputStream: InputStream = body.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {
                            diskCache.put(url, bitmapToByteArray(bitmap))
                            memoryCache.put(url, bitmap)
                            imageView.post {
                                imageView.setImageBitmap(bitmap)
                            }
                        } else {
                            imageView.post {
                                imageView.setImageResource(R.drawable.ic_launcher_background) // Set an error placeholder image
                            }
                        }
                    } ?: run {
                        // Handle null response body
                        imageView.post {
                            imageView.setImageResource(R.drawable.ic_launcher_background) // Set an error placeholder image
                        }
                    }
                } else {
                    // Handle unsuccessful response
                    imageView.post {
                        imageView.setImageResource(R.drawable.ic_launcher_background) // Set an error placeholder image
                    }
                }
            }
        })
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
