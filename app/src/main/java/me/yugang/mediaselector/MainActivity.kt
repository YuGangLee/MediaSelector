package me.yugang.mediaselector

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import me.yugang.album.core.AlbumType
import me.yugang.album.core.MediaSelector
import me.yugang.album.core.bean.AlbumBean
import me.yugang.album.core.loader.AlbumLoader
import me.yugang.album.core.loader.MediaLoader
import me.yugang.album.core.utils.UriUtils
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "",
                1,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "",
                1,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        test()
    }

    private fun test() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                MediaSelector.with(this)
                    .newViewModelSelector()
                    .onAlbumResult(Observer {
                        it?.let { list ->
                            Log.i("Lee", "albums--> $list")
                        }
                    })
                    .onMediaResult(Observer {
                        it?.let { list ->
                            Log.i("Lee", "images--> $list")
                        }
                    })
                    .get()
                    .loadImages()
            }
        } else {
            if (EasyPermissions.hasPermissions(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                MediaSelector.with(this)
                    .newViewModelSelector()
                    .onAlbumResult(Observer { })
                    .onMediaResult(Observer {
                        it?.let { list ->
                            Log.i("Lee", list.toString())
                        }
                    })
                    .get()
                    .loadImages()
            }
        }
    }
}