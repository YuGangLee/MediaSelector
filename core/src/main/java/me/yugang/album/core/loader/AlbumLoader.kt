package me.yugang.album.core.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import me.yugang.album.core.DifferentialColumnName

class AlbumLoader private constructor(context: Context, uri: Uri) : CursorLoader(
    context,
    uri,
    PROJECTION,
    null,
    null,
    DEFAULT_ORDER_BY
) {
    companion object {

        private val DEFAULT_ORDER_BY =
            "${DifferentialColumnName.BUCKET_NAME} COLLATE NOCASE ASC"

        private val PROJECTION =
            arrayOf(
                MediaStore.MediaColumns._ID,
                DifferentialColumnName.BUCKET_ID,
                DifferentialColumnName.BUCKET_NAME
            )

        @JvmStatic
        fun newPhotoAlbumLoader(context: Context) =
            AlbumLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        @JvmStatic
        fun newVideoAlbumLoader(context: Context) =
            AlbumLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
    }
}