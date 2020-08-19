package me.yugang.album.core.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import me.yugang.album.core.DifferentialColumnName
import me.yugang.album.core.bean.AlbumBean

class MediaLoader : CursorLoader {
    companion object {
        private val DEFAULT_ORDER_BY = "${DifferentialColumnName.DATA_TAKEN} DESC"

        private val PROJECTION =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                DifferentialColumnName.BUCKET_ID
            )

        private const val SELECT_ALL = "${MediaStore.MediaColumns.SIZE}>0"
        private val SELECT_ALL_ARGS = arrayOf<String>()

        private val SELECT_ALBUM =
            "${DifferentialColumnName.BUCKET_ID}=? AND ${MediaStore.MediaColumns.SIZE}>0"

        @JvmStatic
        fun newImageLoader(context: Context) =
            MediaLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        @JvmStatic
        fun newImageLoader(context: Context, album: AlbumBean?) =
            MediaLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .also {
                    album?.let { album ->
                        it.selection = SELECT_ALBUM
                        it.selectionArgs = arrayOf("${album.bucketId}")
                    }
                }

        @JvmStatic
        fun newImageLoader(
            context: Context,
            selection: String?,
            selectionArgs: Array<String>?,
            orderBy: String
        ) = MediaLoader(
            context,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            selection,
            selectionArgs,
            orderBy
        )

        @JvmStatic
        fun newVideoLoader(context: Context) =
            MediaLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)

        @JvmStatic
        fun newVideoLoader(context: Context, album: AlbumBean?) =
            MediaLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .also {
                    album?.let { album ->
                        it.selection = SELECT_ALBUM
                        it.selectionArgs = arrayOf("${album.bucketId}")
                    }
                }

        @JvmStatic
        fun newVideoLoader(
            context: Context,
            selection: String?,
            selectionArgs: Array<String>?,
            orderBy: String
        ) = MediaLoader(
            context,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            selection,
            selectionArgs,
            orderBy
        )
    }

    private constructor(context: Context, uri: Uri) : this(
        context,
        uri,
        SELECT_ALL,
        SELECT_ALL_ARGS,
        DEFAULT_ORDER_BY
    )

    private constructor(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
        orderBy: String
    ) : super(
        context,
        uri,
        PROJECTION,
        selection,
        selectionArgs,
        orderBy
    )
}