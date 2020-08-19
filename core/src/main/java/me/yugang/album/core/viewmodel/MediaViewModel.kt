package me.yugang.album.core.viewmodel

import android.content.Context
import android.provider.MediaStore
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.yugang.album.core.AlbumType
import me.yugang.album.core.DifferentialColumnName
import me.yugang.album.core.MediaType
import me.yugang.album.core.bean.AlbumBean
import me.yugang.album.core.bean.MediaBean
import me.yugang.album.core.loader.AlbumLoader
import me.yugang.album.core.loader.MediaLoader
import me.yugang.album.core.utils.UriUtils

class MediaViewModel : ViewModel() {
    internal val mediaLiveData = MutableLiveData<List<MediaBean>>()
        .also { it.value = listOf() }

    internal val albumLiveData = MutableLiveData<List<AlbumBean>>()
        .also { it.value = listOf() }

    internal lateinit var applicationContext: Context

    @MainThread
    fun loadImageAlbum() {
        val loader = AlbumLoader.newPhotoAlbumLoader(applicationContext)
        viewModelScope.launch(Dispatchers.IO) {
            loader.loadInBackground()?.use { cursor ->
                val list = mutableListOf<AlbumBean>()
                while (cursor.moveToNext()) {
                    val bean = AlbumBean(
                        AlbumType.Image,
                        cursor.getLong(cursor.getColumnIndex(DifferentialColumnName.BUCKET_ID)),
                        cursor.getString(cursor.getColumnIndex(DifferentialColumnName.BUCKET_NAME))
                    )
                    if (!list.contains(bean)) {
                        list.add(bean)
                    }
                }
                albumLiveData.postValue(list)
            }
        }
    }

    @MainThread
    fun loadVideoAlbum() {
        val loader = AlbumLoader.newVideoAlbumLoader(applicationContext)
        viewModelScope.launch(Dispatchers.IO) {
            loader.loadInBackground()?.use { cursor ->
                val list = mutableListOf<AlbumBean>()
                while (cursor.moveToNext()) {
                    val bean = AlbumBean(
                        AlbumType.Video,
                        cursor.getLong(cursor.getColumnIndex(DifferentialColumnName.BUCKET_ID)),
                        cursor.getString(cursor.getColumnIndex(DifferentialColumnName.BUCKET_NAME))
                    )
                    if (!list.contains(bean)) {
                        list.add(bean)
                    }
                }
                albumLiveData.postValue(list)
            }
        }
    }

    @MainThread
    fun loadImages() {
        loadImages(null)
    }

    @MainThread
    fun loadImages(album: AlbumBean?) {
        val list = mutableListOf<MediaBean>()
        val loader = MediaLoader.newImageLoader(applicationContext, album)
        viewModelScope.launch(Dispatchers.IO) {
            loader.loadInBackground()
                ?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val bean = MediaBean(
                            MediaType.Image,
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)),
                            UriUtils.getImageUri(cursor),
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                        )
                        if (!list.contains(bean)) {
                            list.add(bean)
                        }
                    }
                    mediaLiveData.postValue(list)
                }
        }
    }

    @MainThread
    fun loadVideos() {
        loadVideos(null)
    }

    @MainThread
    fun loadVideos(album: AlbumBean?) {
        val list = mutableListOf<MediaBean>()
        val loader = MediaLoader.newVideoLoader(applicationContext, album)
        viewModelScope.launch(Dispatchers.IO) {
            loader.loadInBackground()
                ?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val bean = MediaBean(
                            MediaType.Video,
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)),
                            UriUtils.getImageUri(cursor),
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                        )
                        if (!list.contains(bean)) {
                            list.add(bean)
                        }
                    }
                    mediaLiveData.postValue(list)
                }
        }
    }
}