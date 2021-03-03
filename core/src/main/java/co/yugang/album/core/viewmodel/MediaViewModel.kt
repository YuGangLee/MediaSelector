package co.yugang.album.core.viewmodel

import android.annotation.SuppressLint
import android.provider.MediaStore
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import co.yugang.album.core.AlbumType
import co.yugang.album.core.DifferentialColumnName
import co.yugang.album.core.MediaType
import co.yugang.album.core.bean.AlbumBean
import co.yugang.album.core.bean.MediaBean
import co.yugang.album.core.loader.AlbumLoader
import co.yugang.album.core.loader.MediaLoader
import co.yugang.album.core.application
import co.yugang.album.core.utils.UriUtils
import java.lang.Exception

class MediaViewModel : ViewModel() {
    /**
     * 最新查询条件下的媒体内容
     */
    internal val mediaLiveData = MutableLiveData<List<MediaBean>>()
        .also { it.value = listOf() }

    /**
     * 相册列表
     */
    internal val albumLiveData = MutableLiveData<List<AlbumBean>>()
        .also { it.value = listOf() }

    private val applicationContext = application
        ?: throw RuntimeException("co.yugang.album.core.application is not be set, you should set it first")

    /**
     * 加载相册列表
     */
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

    /**
     * 加载相册列表
     */
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
    @JvmOverloads
    fun loadImages(album: AlbumBean? = null) {
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
    @JvmOverloads
    fun loadVideos(album: AlbumBean? = null) {
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