package com.example.musicapp.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.musicapp.Application
import com.example.musicapp.Constants.ActionName
import com.example.musicapp.Constants.MUSIC_FILE
import com.example.musicapp.Constants.Next
import com.example.musicapp.Constants.PlayPause
import com.example.musicapp.Constants.Previous
import com.example.musicapp.Constants.ServicePosition
import com.example.musicapp.R
import com.example.musicapp.SharedPreferencesHelper
import com.example.musicapp.Utils
import com.example.musicapp.callback.OnActionPlaying
import com.example.musicapp.model.MusicFiles
import com.example.musicapp.receiver.NotificationReceiver
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicService : Service(),
    MediaPlayer.OnCompletionListener {
    private var mBinder: IBinder = MyBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var position: Int = -1
    private var uri: Uri? = null
    private var musicFiles: ArrayList<MusicFiles> = arrayListOf()
    private var mediaSessionCompat: MediaSessionCompat? = null
    var actionPlaying: OnActionPlaying? = null

    override fun onCreate() {
        super.onCreate()
        mediaSessionCompat = MediaSessionCompat(baseContext, "My audio")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("Bind", "onBind: Method")
        return mBinder
    }

    private fun playMedia(startPosition: Int) {
        if (mediaPlayer != null) {
            stop()
            release()
            createMediaPlayer(startPosition)
            start()
        } else {
            createMediaPlayer(startPosition)
            start()
        }
        actionPlaying?.initStateMusic()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        musicFiles = Application.state.musicFiles
        val myPosition = intent?.getIntExtra(ServicePosition, -1)
        val actionName = intent?.getStringExtra(ActionName)
        myPosition?.let { handleMusicList(it) }
        handleClickNotification(actionName)
        return START_STICKY
    }

    private fun handleClickNotification(actionName: String?) {
        when (actionName) {
            PlayPause -> {
                playPauseBtnClicked()
            }

            Next -> {
                nextBtnClicked()
            }

            Previous -> {
                prevBtnClicked()
            }
        }
    }

    private fun handleMusicList(myPosition: Int) {
        if (myPosition != -1) {
            playMedia(myPosition)
        }
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun release() {
        mediaPlayer?.release()
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun setLooping(isLoop: Boolean) {
        mediaPlayer?.isLooping = isLoop
    }

    fun createMediaPlayer(position: Int) {
        this.position = position
        uri = Uri.parse(musicFiles[position].path)
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val json = gson.toJson(musicFiles[position])
            SharedPreferencesHelper(baseContext).putString(MUSIC_FILE, json)
        }
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    fun onCompleted() {
        mediaPlayer?.setOnCompletionListener(this)
    }

    fun getDuration(): Int = mediaPlayer?.duration ?: 0
    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    override fun onCompletion(mp: MediaPlayer?) {
        actionPlaying?.clickNext()
        if (mediaPlayer != null) {
            createMediaPlayer(position)
            start()
            onCompleted()
            showNotification(R.drawable.ic_pause)
        }
    }

    fun showNotification(playPauseBtn: Int) {
        val context = this
        val prevIntent =
            Intent(
                context,
                NotificationReceiver::class.java
            ).setAction(Application.ACTION_PREVIOUS)
        val prevPending =
            PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_IMMUTABLE)
        val pauseIntent =
            Intent(context, NotificationReceiver::class.java).setAction(Application.ACTION_PLAY)
        val pausePending =
            PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextIntent =
            Intent(context, NotificationReceiver::class.java).setAction(Application.ACTION_NEXT)
        val nextPending =
            PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        var picture: ByteArray?
        musicFiles[position].path.let { uri -> picture = Utils.getAlbumArt(uri) }
        val thumb: Bitmap = if (picture != null) {
            BitmapFactory.decodeByteArray(picture, 0, picture?.size ?: 0)
        } else {
            getBitmapFromVectorDrawable(context, R.drawable.broken_image)
        }
        val notification: Notification =
            NotificationCompat.Builder(
                context,
                Application.CHANNEL_ID
            )
                .setSmallIcon(playPauseBtn).setLargeIcon(thumb)
                .setContentTitle(musicFiles[position].title)
                .setContentText(musicFiles[position].artist)
                .addAction(
                    R.drawable.ic_skip_previous,
                    "Previous",
                    prevPending
                )
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(
                    R.drawable.ic_skip_next,
                    "Next",
                    nextPending
                )
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat?.sessionToken)
                ).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
        startForeground(2, notification)
    }

    // Function that converts the vector form to Bitmap form.
    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0,
            drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    private fun nextBtnClicked() {
        actionPlaying?.clickNext()
    }

    private fun prevBtnClicked() {
        actionPlaying?.clickPrev()
    }

    private fun playPauseBtnClicked() {
        actionPlaying?.clickPlayPause()
    }

    inner class MyBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
}