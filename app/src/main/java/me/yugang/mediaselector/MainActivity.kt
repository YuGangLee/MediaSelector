package me.yugang.mediaselector

import android.Manifest
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import me.yugang.album.core.MediaSelector
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "",
                1,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
        btSelect.setOnClickListener {
            MediaSelector.with(this).getViewModelSelector().onMediaResult {
                if (!it.isNullOrEmpty()) {
                    val media = it[0]
                    Thread {
                        val bitmap = Glide.with(this).asBitmap().load(media.uri).submit().get()
                        runOnUiThread {
                            test.setImageBitmap(bitmap)
                        }
                    }.start()
                }
            }.get().loadImages()
        }
        btCut.setOnClickListener {
            clTest.background = BitmapDrawable(resources, test.getCutBitmap())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Lee", data?.extras.toString())
        test.setImageBitmap(data?.getParcelableExtra("data") ?: return)
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
                    .getViewModelSelector()
                    .onAlbumResult {
                        it?.let { list ->
                        }
                    }
                    .onMediaResult {
                        it?.let { list ->
                        }
                    }
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
                    .getViewModelSelector()
                    .onAlbumResult { }
                    .onMediaResult {
                        it?.let { list ->
                            Log.i("Lee", list.toString())
                        }
                    }
                    .get()
                    .loadImages()
            }
        }
    }
}