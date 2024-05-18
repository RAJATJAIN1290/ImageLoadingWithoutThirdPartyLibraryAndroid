package com.example.quantamtask.networkCall

import android.graphics.Bitmap
import androidx.collection.LruCache

class MemoryCacheHelper {

    private val memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(getDefaultLruCacheSize()) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun get(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    fun put(key: String, bitmap: Bitmap) {
        memoryCache.put(key, bitmap)
    }

    companion object {
        fun getDefaultLruCacheSize(): Int {
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            return maxMemory / 8
        }
    }
}
