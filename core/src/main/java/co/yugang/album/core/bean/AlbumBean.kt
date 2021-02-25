package co.yugang.album.core.bean

import co.yugang.album.core.AlbumType

/**
 * 媒体Bean
 * @param type: 相册类型
 * @param bucketId: 相册在MediaStore中的id
 * @param bucketName: 相册名
 *
 * @see AlbumType
 */
data class AlbumBean(val type: AlbumType, val bucketId: Long, val bucketName: String?) {
    override fun equals(other: Any?): Boolean {
        if (other is AlbumBean) {
            return bucketId == other.bucketId
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}