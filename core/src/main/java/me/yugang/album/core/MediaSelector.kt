package me.yugang.album.core

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import me.yugang.album.core.selector.ViewModelSelector

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
}

