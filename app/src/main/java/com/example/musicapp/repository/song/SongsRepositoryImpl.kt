package com.example.musicapp.repository.song

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.musicapp.Application
import com.example.musicapp.Constants
import com.example.musicapp.Constants.SortByDate
import com.example.musicapp.Constants.SortByName
import com.example.musicapp.Constants.SortBySize
import com.example.musicapp.Constants.Sorting
import com.example.musicapp.SharedPreferencesHelper
import com.example.musicapp.model.MusicFiles
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongsRepositoryImpl : SongRepository {
    override suspend fun getAudioAll(): ArrayList<MusicFiles> {
        val sortOrder = Application.Instance?.applicationContext?.let {
            SharedPreferencesHelper(it).getString(
                Sorting,
                SortByName
            )
        }
        val tempAudioList = ArrayList<MusicFiles>()
        var order = ""
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when (sortOrder) {
            SortByName -> {
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            }

            SortByDate -> {
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
            }

            SortBySize -> {
                order = MediaStore.MediaColumns.SIZE + " DESC"
            }
        }
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )
        val cursor = Application.Instance?.applicationContext?.contentResolver?.query(
            uri,
            projection,
            null,
            null,
            order
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val id = cursor.getString(5)
                val musicFile = MusicFiles(
                    album = album,
                    title = title,
                    duration = duration,
                    path = path,
                    artist = artist,
                    id = id
                )
                Log.i(TAG, "Path: $path, Album: $album")
                tempAudioList.add(musicFile)
            }
            cursor.close()
        }
        return tempAudioList
    }

    override suspend fun deleteFile(
        position: Int,
        currentList: MutableList<MusicFiles>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currentList[position].id.toLong()
        )
        try {
            Application.Instance?.applicationContext?.contentResolver?.delete(
                contentUri,
                null,
                null
            )
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Application.Instance?.applicationContext?.contentResolver?.let {
                        MediaStore.createDeleteRequest(
                            it, listOf(contentUri)
                        ).intentSender
                    }
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            intentSender?.let {
                intentSenderLauncher.launch(IntentSenderRequest.Builder(it).build())
            }
        }
    }

    override fun getContentSongs(newText: String): String {
        return newText.lowercase()
    }

    override suspend fun querySongs(
        content: String,
        songs: ArrayList<MusicFiles>,
    ): ArrayList<MusicFiles> {
        val songsAfterQuery = ArrayList<MusicFiles>()
        return if (content.isEmpty() || content.isBlank()) {
            getAudioAll()
        } else {
            for (song in songs) {
                if (song.title.lowercase().contains(content)) {
                    songsAfterQuery.add(song)
                }
            }
            songsAfterQuery
        }
    }

    override suspend fun getMusic(action: (MusicFiles?) -> Unit) {
        Application.Instance?.applicationContext?.let {
            val json: String = SharedPreferencesHelper(it).getString(Constants.MUSIC_FILE, "")
            if (json == "") {
                action(null)
            } else {
                val gson = Gson()
                val musicFile: MusicFiles = gson.fromJson(json, MusicFiles::class.java)
                withContext(Dispatchers.Main) {
                    action(musicFile)
                }
            }
        }
    }

    companion object {
        private val TAG = SongsRepositoryImpl::class.java.name
    }
}