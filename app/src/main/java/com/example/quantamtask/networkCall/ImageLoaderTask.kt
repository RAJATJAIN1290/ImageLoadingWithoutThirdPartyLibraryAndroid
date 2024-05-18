package com.example.quantamtask.networkCall

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.example.quantamtask.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import okhttp3.Response

class ImageLoaderTask(
    private val imageView: ImageView,
    private val imageUrl: String,
    private val diskCache: DiskLruCacheHelper,
    private val memoryCache: MemoryCacheHelper,
    private val client: OkHttpClient
) : AsyncTask<Void, Void, Bitmap?>() {

    override fun doInBackground(vararg params: Void?): Bitmap? {
        return try {
            val request = Request.Builder().url(imageUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val byteArray = response.body?.bytes() ?: return null
                diskCache.put(imageUrl, byteArray)
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            memoryCache.put(imageUrl, result)
            imageView.setImageBitmap(result)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background) // Set an error placeholder image
        }
    }
}
