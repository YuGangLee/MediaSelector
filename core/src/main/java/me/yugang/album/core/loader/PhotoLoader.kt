package me.yugang.album.core.loader

import android.content.Context
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

class PhotoLoader(context: Context) : CursorLoader(
    context,
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    PROJECTION,
    null,
    null,
    DEFAULT_ORDER_BY
) {
    companion object {
        private const val DEFAULT_ORDER_BY = "datetaken DESC"

        private val PROJECTION =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE
            )
    }
}