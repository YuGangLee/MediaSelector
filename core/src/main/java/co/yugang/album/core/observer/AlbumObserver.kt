package co.yugang.album.core.observer

import androidx.lifecycle.Observer
import co.yugang.album.core.bean.AlbumBean

abstract class AlbumObserver : Observer<List<AlbumBean>> {
    override fun onChanged(t: List<AlbumBean>?) {
        if (t == null) {
            onAlbumResult(listOf())
        } else {
            onAlbumResult(t)
        }
    }

    abstract fun onAlbumResult(albums: List<AlbumBean>)

    class EmptyAlbumObserver : AlbumObserver() {
        override fun onAlbumResult(albums: List<AlbumBean>) {

        }
    }
}