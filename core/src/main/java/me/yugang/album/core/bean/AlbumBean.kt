package me.yugang.album.core.bean

import me.yugang.album.core.AlbumType

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