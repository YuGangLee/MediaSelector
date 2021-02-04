package co.yugang.album.core.bean

import android.net.Uri
import co.yugang.album.core.MediaType

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