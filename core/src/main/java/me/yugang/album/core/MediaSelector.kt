package me.yugang.album.core

import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import me.yugang.album.core.selector.ViewModelSelector
import java.lang.IllegalArgumentException

class MediaSelector {

    private val context: Any

    companion object {
        @JvmStatic
        fun with(fragment: Fragment): MediaSelector {
            return MediaSelector(fragment)
        }

        @JvmStatic
        fun with(activity: AppCompatActivity): MediaSelector {
            return MediaSelector(activity)
        }
    }

    private constructor(fragment: Fragment) {
        context = fragment
    }

    private constructor(activity: AppCompatActivity) {
        context = activity
    }

    fun newViewModelSelector() = ViewModelSelector(context)

    fun capture(requestCode: Int) {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val activity = when (context) {
            is Fragment -> context.activity
                ?: throw IllegalStateException("fragment not attached to an activity")
            is FragmentActivity -> context
            else -> throw IllegalArgumentException("context require Fragment or FragmentActivity")
        }
        activity.startActivityForResult(captureIntent, requestCode)
    }
}

