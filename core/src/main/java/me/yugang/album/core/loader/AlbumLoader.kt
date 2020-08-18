package me.yugang.album.core.loader

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

class AlbumLoader(context: Context) : CursorLoader(
    context,
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) PROJECTION_29 else PROJECTION,
    null,
    null,
    DEFAULT_ORDER_BY
) {
    companion object {
        private const val BUCKET_ID = "bucket_id"
        private const val BUCKET_DISPLAY_NAME = "bucket_display_name"

        private const val DEFAULT_ORDER_BY = "datetaken DESC"

        private val PROJECTION =
            arrayOf(MediaStore.MediaColumns._ID, BUCKET_ID, BUCKET_DISPLAY_NAME)

        @SuppressLint("InlinedApi")
        private val PROJECTION_29 =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
            )
    }

}