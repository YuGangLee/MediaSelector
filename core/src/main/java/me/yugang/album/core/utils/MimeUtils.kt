package me.yugang.album.core.utils

import me.yugang.album.core.MimeType.MIME_IMAGE_START
import me.yugang.album.core.MimeType.MIME_TYPE_GIF
import me.yugang.album.core.MimeType.MIME_VIDEO_START

internal object MimeUtils {

    fun isImage(mimeType: String) = mimeType.startsWith(MIME_IMAGE_START)

    fun isVideo(mimeType: String) = mimeType.startsWith(MIME_VIDEO_START)

    fun isGif(mimeType: String) = MIME_TYPE_GIF == mimeType
}