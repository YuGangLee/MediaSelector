package co.yugang.album.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import co.yugang.album.core.selector.ViewModelSelector
import java.io.File
import java.lang.IllegalArgumentException

class MediaSelector {

    private val context: Any

    companion object {
        @JvmStatic
        fun with(fragment: Fragment): MediaSelector {
            return MediaSelector(fragment)
        }

        @JvmStatic
        fun with(activity: AppCompatActivity): MediaSelector {
            return MediaSelector(activity)
        }
    }

    /**
     * 获取相册选择控制器(ViewModel模式)
     * 一个Fragment或一个Activity中只会有一个ViewModel控制器
     * */
    fun getViewModelSelector() = ViewModelSelector(context)

    /**
     * 打开系统相机拍照，不保存至文件
     * 成功拍照后在onActivityResult中返回的Intent中将附带缩略图Bitmap数据
     * 可通过intent.extras.get("data")获取缩略图
     */
    fun takePictureWithoutSave(requestCode: Int) {
        val activity = getActivity()
        if (!checkCamera(activity)) {
            Log.e("MediaSelector", "no camera found")
            return
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.let {
                activity.startActivityForResult(takePictureIntent, requestCode)
            }
        }
    }

    /**
     * 打开系统相机拍照，保存至默认的文件目录
     */
    fun takePicture(requestCode: Int) {
        takePicture(requestCode, null)
    }


    /**
     * 打开系统相机拍照，保存至默认文件目录的指定文件
     */
    fun takePicture(requestCode: Int, name: String) {
        val activity = getActivity()
        if (!checkCamera(activity)) {
            Log.e("MediaSelector", "no camera found")
            return
        }

        val uri = FileProvider.getUriForFile(
            activity,
            "MediaSelector.default.fileProvider",
            File(activity.getExternalFilesDir("pictures"), "$name.jpg")
        )
        takePicture(requestCode, uri)
    }

    /**
     * 打开系统相机拍照，保存至指定文件
     *
     * 从 Android N 开始，file://方式的Uri无法在应用间传递使用，使用该方式会抛出FileUriExposedException异常
     * 建议使用FileProvider生成Uri
     *
     * @param fileUri: 文件的保存目录，若为空则默认保存至应用外部存储的picture文件夹下
     *
     */
    fun takePicture(requestCode: Int, fileUri: Uri?) {
        val activity = getActivity()
        if (!checkCamera(activity)) {
            Log.e("MediaSelector", "no camera found")
            return
        }

        val uri = fileUri ?: FileProvider.getUriForFile(
            activity,
            "MediaSelector.default.fileProvider",
            File(activity.getExternalFilesDir("pictures"), "JPEG_${System.currentTimeMillis()}.jpg")
        )
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.let {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                activity.startActivityForResult(takePictureIntent, requestCode)
            }
        }
    }

    fun checkCamera(activity: Context) =
        activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private constructor(fragment: Fragment) {
        context = fragment
    }

    private constructor(activity: AppCompatActivity) {
        context = activity
    }

    private fun getActivity() = when (context) {
        is Fragment -> context.activity
            ?: throw IllegalStateException("fragment not attached to an activity")
        is AppCompatActivity -> context
        else -> throw IllegalArgumentException("context require Fragment or AppCompatActivity")
    }
}

