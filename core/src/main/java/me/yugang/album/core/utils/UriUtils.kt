package me.yugang.album.core.utils

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object UriUtils {
    fun getImageUri(cursor: Cursor): Uri {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        return ContentUris.withAppendedId(contentUri, id)
    }

    fun getVideoUri(cursor: Cursor): Uri {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        return ContentUris.withAppendedId(contentUri, id)
    }
}