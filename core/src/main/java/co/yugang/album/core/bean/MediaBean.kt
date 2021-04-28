package co.yugang.album.core.bean

import android.net.Uri
import android.os.Parcelable
import co.yugang.album.core.MediaType
import kotlinx.android.parcel.Parcelize

/**
 * 媒体Bean
 * @param mediaType: 媒体类型
 * @param name: 媒体文件的显示名称
 * @param uri: 媒体文件的Uri路径
 * @param mimeType: 文件的MIME_TYPE值
 *
 * @see MediaType
 */
@Parcelize
data class MediaBean(
    val mediaType: MediaType,
    val name: String?,
    val uri: Uri,
    val mimeType: String?
) : Parcelable {
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