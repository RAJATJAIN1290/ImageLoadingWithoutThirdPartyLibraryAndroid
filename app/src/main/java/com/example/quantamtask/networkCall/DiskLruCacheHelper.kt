package com.example.quantamtask.networkCall

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class DiskLruCacheHelper(context: Context) {
    private val cacheDir: File = context.cacheDir

    fun put(key: String, data: ByteArray) {
        try {
            val file = File(cacheDir, hashKeyForDisk(key))
            FileOutputStream(file).use {
                it.write(data)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun get(key: String): Bitmap? {
        return try {
            val file = File(cacheDir, hashKeyForDisk(key))
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun hashKeyForDisk(key: String): String {
        return try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            key.hashCode().toString()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (byte in bytes) {
            val hex = Integer.toHexString(0xFF and byte.toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}
