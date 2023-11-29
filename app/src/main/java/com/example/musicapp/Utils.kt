package com.example.musicapp

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Utils {
    companion object {
        fun getAlbumArt(uri: String, action: (ByteArray?) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(uri)
                val art = retriever.embeddedPicture
                retriever.release()
                withContext(Dispatchers.Main) {
                    action(art)
                }
            }
        }

        fun getAlbumArt(uri: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            return art
        }
    }
}