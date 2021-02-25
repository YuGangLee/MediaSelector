package co.yugang.album.core.bean

import android.net.Uri
import co.yugang.album.core.MediaType

/**
 * 媒体Bean
 * @param mediaType: 媒体类型
 * @param name: 媒体文件的显示名称
 * @param uri: 媒体文件的Uri路径
 * @param mimeType: 文件的MIME_TYPE值
 *
 * @see MediaType
 */
data class MediaBean(
    val mediaType: MediaType,
    val name: String,
    val uri: Uri,
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is MediaBean) {
            return uri.toString() == other.uri.toString()
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}