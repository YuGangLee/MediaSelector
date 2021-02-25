package co.yugang.album.core.selector

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.yugang.album.core.bean.AlbumBean
import co.yugang.album.core.bean.MediaBean
import co.yugang.album.core.observer.AlbumObserver
import co.yugang.album.core.observer.MediaObserver
import co.yugang.album.core.viewmodel.MediaViewModel
import java.lang.IllegalArgumentException

class ViewModelSelector internal constructor(private val context: Any) {
    private var onAlbumResult: Observer<List<AlbumBean>> = AlbumObserver.EmptyAlbumObserver()
    private var onMediaResult: Observer<List<MediaBean>> = MediaObserver.EmptyMediaObserver()

    private val mediaViewModel: MediaViewModel = when (context) {
        is AppCompatActivity -> {
            ViewModelProvider(context).get(MediaViewModel::class.java).also {
                it.applicationContext = context.applicationContext
            }
        }
        is Fragment -> {
            ViewModelProvider(context).get(MediaViewModel::class.java).also {
                it.applicationContext = context.requireContext().applicationContext
            }
        }
        else -> throw IllegalArgumentException("context require Fragment or AppCompatActivity")
    }

    fun onAlbumResult(onAlbumResult: Observer<List<AlbumBean>>): ViewModelSelector {
        this.onAlbumResult = onAlbumResult
        return this
    }

    fun onMediaResult(onMediaResult: Observer<List<MediaBean>>): ViewModelSelector {
        this.onMediaResult = onMediaResult
        return this
    }

    fun get() = mediaViewModel.also {
        when (context) {
            is AppCompatActivity -> {
                it.albumLiveData.removeObservers(context)
                it.mediaLiveData.removeObservers(context)
                it.albumLiveData.observe(context, onAlbumResult)
                it.mediaLiveData.observe(context, onMediaResult)
            }
            is Fragment -> {
                it.albumLiveData.removeObservers(context)
                it.mediaLiveData.removeObservers(context)
                it.albumLiveData.observe(context, onAlbumResult)
                it.mediaLiveData.observe(context, onMediaResult)
            }
            else -> throw IllegalArgumentException("context require fragment or activity")
        }
    }
}