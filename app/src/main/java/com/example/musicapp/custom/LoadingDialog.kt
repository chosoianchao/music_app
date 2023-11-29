package com.example.musicapp.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.musicapp.R

class LoadingDialog(context: Context) : Dialog(context), LifecycleEventObserver {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.loading_dialog)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val color = ContextCompat.getColor(context, R.color.black)
        progressBar.indeterminateDrawable.colorFilter =
            PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    fun toggle(status: Boolean = true) {
        if (status) {
            if (!isShowing) {
                show()
            }
        } else {
            cancel()
        }
    }

    private fun release() {
        cancel()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_STOP) {
            release()
        }
    }
}