package com.example.musicapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import retrofit2.HttpException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val networkCapabilities = cm.getNetworkCapabilities(network)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}


fun Throwable.getErrorMessage(context: Context): String? {
    return when (this) {
        is IOException -> context.getString(R.string.network_error)
        is HttpException -> context.getString(R.string.server_error)
        else -> this.message
    }
}

fun String.getFormattedDate(): String {
    val originalFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()
    )
    val targetFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val date: Date?
    try {
        date = originalFormat.parse(this)
    } catch (e: ParseException) {
        return ""
    }
    return date?.let { targetFormat.format(it) }.toString()
}

inline fun WebView.doOnProgressCompleted(crossinline onProgressCompleted: () -> Unit) {
    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress > 95) {
                onProgressCompleted()
            }
            super.onProgressChanged(view, newProgress)
        }
    }
}
