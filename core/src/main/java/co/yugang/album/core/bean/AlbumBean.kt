package co.yugang.album.core.bean

import android.os.Parcelable
import co.yugang.album.core.AlbumType
import kotlinx.android.parcel.Parcelize

/**
 * 媒体Bean
 * @param type: 相册类型
 * @param bucketId: 相册在MediaStore中的id
 * @param bucketName: 相册名
 *
 * @see AlbumType
 */
@Parcelize
data class AlbumBean(val type: AlbumType, val bucketId: Long, val bucketName: String?) :
    Parcelable {
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