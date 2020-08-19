package me.yugang.album.core.observer

import androidx.lifecycle.Observer
import me.yugang.album.core.bean.AlbumBean
import me.yugang.album.core.bean.MediaBean

abstract class MediaObserver : Observer<List<MediaBean>> {
    override fun onChanged(t: List<MediaBean>?) {
        if (t == null) {
            onMediaResult(listOf())
        } else {
            onMediaResult(t)
        }
    }

    abstract fun onMediaResult(albums: List<MediaBean>)

    class EmptyMediaObserver : MediaObserver() {
        override fun onMediaResult(albums: List<MediaBean>) {
        }
    }
}