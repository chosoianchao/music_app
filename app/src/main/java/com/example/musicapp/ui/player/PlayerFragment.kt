package com.example.musicapp.ui.player

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.DrawableRes
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.example.musicapp.Constants.ServicePosition
import com.example.musicapp.R
import com.example.musicapp.Utils
import com.example.musicapp.base.BaseFragment
import com.example.musicapp.callback.OnActionPlaying
import com.example.musicapp.databinding.PlayerFragmentBinding
import com.example.musicapp.service.MusicService
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BaseFragment<PlayerFragmentBinding>(), OnActionPlaying {
    override val viewModel: PlayerViewModel by viewModel()
    override val layoutResource: Int
        get() = R.layout.player_fragment

    override fun initViewBinding(view: View): PlayerFragmentBinding {
        return PlayerFragmentBinding.bind(view)
    }

    override fun initViews() {
        setContentMusic()
        initMusic()
    }

    private fun setContentMusic() {
        with(binding) {
            tvSongName.text = viewModel.musicFiles[viewModel.position].title
            tvSongArtist.text = viewModel.musicFiles[viewModel.position].artist
            val durationTotal =
                Integer.parseInt(viewModel.musicFiles[viewModel.position].duration) / 1000
            binding.tvDurationTotal.text = viewModel.formattedTime(durationTotal)
            val art = Utils.getAlbumArt(viewModel.musicFiles[viewModel.position].path)
            val bitmap: Bitmap
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
                imageAnimation(requireContext(), binding.ivCoverArt, bitmap)
                Palette.from(bitmap).generate { palette ->
                    val swatch = palette?.dominantSwatch
                    if (swatch != null) {
                        binding.ivGredient.setBackgroundResource(R.drawable.gredient_bg)
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg)
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, 0x00000000)
                        )
                        binding.ivGredient.background = gradientDrawable
                        val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(swatch.rgb, swatch.rgb)
                        )
                        binding.mContainer.background = gradientDrawableBg
                        binding.tvSongName.setTextColor(swatch.titleTextColor)
                        binding.tvSongArtist.setTextColor(swatch.bodyTextColor)
                    } else {
                        binding.ivGredient.setBackgroundResource(R.drawable.gredient_bg)
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg)
                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0x00000000)
                        )
                        binding.ivGredient.background = gradientDrawable
                        val gradientDrawableBg = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(0xff000000.toInt(), 0xff000000.toInt())
                        )
                        binding.mContainer.background = gradientDrawableBg
                        binding.tvSongName.setTextColor(Color.WHITE)
                        binding.tvSongArtist.setTextColor(Color.DKGRAY)
                    }
                }
            } else {
                Glide.with(requireContext()).load(R.drawable.broken_image)
                    .into(binding.ivCoverArt)
                binding.ivGredient.setBackgroundResource(R.drawable.gredient_bg)
                binding.mContainer.setBackgroundResource(R.drawable.main_bg)
                binding.tvSongName.setTextColor(Color.WHITE)
                binding.tvSongArtist.setTextColor(Color.DKGRAY)
            }
        }
    }

    private fun initMusic() {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra(ServicePosition, viewModel.position)
        context?.startService(intent)
        activity?.musicService?.actionPlaying = this
        binding.flPlayPause.setImageResource(R.drawable.ic_pause)
        binding.sbDurationSong.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    activity?.musicService?.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        viewModel.launchMusic {
            setProgressMusic()
        }
    }

    override fun setOnClick() {
        binding.flPlayPause.setOnClickListener {
            clickPlayPause()
        }
        binding.ivPrev.setOnClickListener {
            clickPrev()
        }
        binding.ivNext.setOnClickListener {
            clickNext()
        }
        binding.ivShuffle.setOnClickListener {
            clickShuffle()
        }
        binding.ivRepeat.setOnClickListener {
            clickRepeat()
        }
        binding.btnBack.setOnClickListener {
            OnBackPressedDispatcher().onBackPressed()
        }
        binding.btnMenu.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Tôi đang nghe nhạc : ${viewModel.musicFiles[viewModel.position].title}"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun clickRepeat() {
        with(viewModel) {
            if (isRepeat) {
                isRepeat = false
                setImageResource(binding.ivRepeat, R.drawable.ic_repeat_off)
            } else {
                isRepeat = true
                setImageResource(binding.ivRepeat, R.drawable.ic_repeat_on)
            }
            activity?.musicService?.setLooping(isRepeat)
        }
    }

    private fun clickShuffle() {
        with(viewModel) {
            if (isShuffle) {
                isShuffle = false
                setImageResource(binding.ivShuffle, R.drawable.ic_shuffle_off)
            } else {
                isShuffle = true
                setImageResource(binding.ivShuffle, R.drawable.ic_shuffle_on)
            }
        }
    }

    override fun clickPrev() {
        if (_binding != null) {
            if (activity?.musicService?.isPlaying() == true) {
                releaseMusic()
                with(viewModel) {
                    createMusicNextPrev(false)
                    setContentMusic()
                    launchMusic {
                        setProgressMusic()
                    }
                    setMediaOnCompletion()
                    activity?.musicService?.showNotification(R.drawable.ic_pause)
                    setBackgroundResource(binding.flPlayPause, R.drawable.ic_pause)
                    activity?.musicService?.start()
                }
            } else {
                releaseMusic()
                with(viewModel) {
                    createMusicNextPrev(false)
                    setContentMusic()
                    launchMusic {
                        setProgressMusic()
                    }
                    setMediaOnCompletion()
                    activity?.musicService?.showNotification(R.drawable.ic_play)
                    setBackgroundResource(binding.flPlayPause, R.drawable.ic_play)
                }
            }
        } else {
            releaseMusic()
            createMusicNextPrev(false)
            setMediaOnCompletion()
            activity?.musicService?.showNotification(R.drawable.ic_pause)
            activity?.musicService?.start()
        }
    }

    private fun releaseMusic() {
        activity?.musicService?.stop()
        activity?.musicService?.release()
    }

    override fun clickNext() {
        if (_binding != null) {
            if (activity?.musicService?.isPlaying() == true) {
                releaseMusic()
                with(viewModel) {
                    createMusicNextPrev(true)
                    setContentMusic()
                    launchMusic {
                        setProgressMusic()
                    }
                    setMediaOnCompletion()
                    activity?.musicService?.showNotification(R.drawable.ic_pause)
                    setBackgroundResource(binding.flPlayPause, R.drawable.ic_pause)
                    activity?.musicService?.start()
                }
            } else {
                releaseMusic()
                with(viewModel) {
                    createMusicNextPrev(true)
                    setContentMusic()
                    launchMusic {
                        setProgressMusic()
                    }
                    setMediaOnCompletion()
                    activity?.musicService?.showNotification(R.drawable.ic_play)
                    setBackgroundResource(binding.flPlayPause, R.drawable.ic_play)
                }
            }
        } else {
            releaseMusic()
            createMusicNextPrev(true)
            setMediaOnCompletion()
            activity?.musicService?.showNotification(R.drawable.ic_pause)
            activity?.musicService?.start()
        }
    }

    override fun initStateMusic() {
        if (_binding != null) {
            binding.sbDurationSong.max = activity?.musicService?.getDuration()?.div(1000) ?: 0
        }
        activity?.musicService?.showNotification(R.drawable.ic_pause)
        setMediaOnCompletion()
    }

    private fun createMusicNextPrev(hasClickNext: Boolean) {
        with(viewModel) {
            getRandomMusic(hasClickNext)
            activity?.musicService?.createMediaPlayer(position)
            if (_binding != null) {
                binding.sbDurationSong.max = activity?.musicService?.getDuration()?.div(1000) ?: 0
            }
        }
    }

    private fun setBackgroundResource(view: ImageView, @DrawableRes resId: Int) {
        view.setBackgroundResource(resId)
    }

    private fun setImageResource(view: ImageView, @DrawableRes resId: Int) {
        view.setImageResource(resId)
    }

    private fun setProgressMusic() {
        val currentPosition = activity?.musicService?.getCurrentPosition()?.div(1000)
        if (currentPosition != null) {
            binding.sbDurationSong.progress = currentPosition
            binding.tvDurationPlayed.text = viewModel.formattedTime(currentPosition)
        }
    }

    override fun clickPlayPause() {
        if (activity?.musicService?.isPlaying() == true) {
            activity?.musicService?.showNotification(R.drawable.ic_play)
            activity?.musicService?.pause()
            if (_binding == null) {
                return
            }
            setImageResource(binding.flPlayPause, R.drawable.ic_play)
            binding.sbDurationSong.max = activity?.musicService?.getDuration()?.div(1000) ?: 0
            viewModel.launchMusic {
                setProgressMusic()
            }
        } else {
            activity?.musicService?.showNotification(R.drawable.ic_pause)
            activity?.musicService?.start()
            if (_binding == null) {
                return
            }
            setImageResource(binding.flPlayPause, R.drawable.ic_pause)
            binding.sbDurationSong.max = activity?.musicService?.getDuration()?.div(1000) ?: 0
            viewModel.launchMusic {
                setProgressMusic()
            }
        }
    }

    private fun imageAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                animIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
                imageView.startAnimation(animIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        imageView.startAnimation(animOut)
    }

    private fun setMediaOnCompletion() {
        activity?.musicService?.onCompleted()
    }
}