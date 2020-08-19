package me.yugang.album.core

import android.annotation.SuppressLint
import android.os.Build
import android.provider.MediaStore

object MimeType {
    const val MIME_IMAGE_START = "image"
    const val MIME_VIDEO_START = "video"

    const val MIME_TYPE_GIF = "image/gif"
}

@SuppressLint("InlinedApi")
object DifferentialColumnName {
    val BUCKET_ID =
        if (isBeforeAndroidQ) "bucket_id"
        else MediaStore.MediaColumns.BUCKET_ID

    val BUCKET_NAME =
        if (isBeforeAndroidQ) "bucket_display_name"
        else MediaStore.MediaColumns.BUCKET_DISPLAY_NAME

    val DATA_TAKEN =
        if (isBeforeAndroidQ) "datetaken"
        else MediaStore.MediaColumns.DATE_TAKEN
}

enum class AlbumType {
    Image,
    Video,
    Mix
}

enum class MediaType {
    Image,
    Video,
    Other
}

val isBeforeAndroidQ get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q