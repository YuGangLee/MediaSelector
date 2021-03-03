# MediaSelector
>MediaSelector的目标是成为一个高兼容、轻量级的图片选择框架  

待办项目：

- 核心模块
    - [x] 获取相册列表
    - [x] 获取图片/视频列表（支持按相册获取）
    - [x] 调用系统相机拍照
- 扩展模块
    - [x] 图片裁剪(添加了一个简单的图片裁剪控件)
    - [ ] 图片加载引擎
    - [ ] 视频播放引擎
    - [ ] 媒体文件列表展示
    - [ ] 媒体文件预览
    - [ ] 相机预览
    - [ ] 自定相机预览界面
- 待补充

 ### 文档
 #### 使用方式
 ##### 导入

 ``` groovy
    // 核心模块
    implementation 'co.yugang:YGAlbum-Core:1.0.0'
 ```

 ##### 获取相册/图片/视频
 ``` kotlin
    // kotlin
    // ViewModel模式（需要依附于AppCompatActivity/Fragment）
    /**
     * 获取MediaSelector控制器
     *
     * !! 请注意，如果在Fragment中使用时，
     * !! 需要先将Fragment绑定至Activity之后再获取MediaViewModel
     */
    val mediaViewModel = MediaSelector.with(context) // context可以为AppCompatActivity/Fragment的子类
        .viewModelSelector()
        .onAlbumResult { list: -> 
            // list: 获取到的相册列表 List<AlbumBean>
        }
        .onMediaResult { list: -> 
            // list: 获取到的图片列表 List<MediaBean>
        }
        .get() // MediaViewModel

    /**
     * 获取图片相册
     */
    mediaViewModel.loadImageAlbum()

    /**
     * 获取视频相册
     */
    mediaViewModel.loadVideoAlbum()

    /**
     * 获取指定相册下的图片（不传入AlbunBean以获取所有图片）
     */
    mediaViewModel.loadImages(bean: AlbunBean?) // loadImages有两种重载方式：loadImages() / loadImages(album： AlbunBean?)

    /**
     * 获取指定相册下的视频（不传入AlbunBean以获取所有视频）
     */
    mediaViewModel.loadImages(bean: AlbunBean?) // loadImages有两种重载方式：loadImages() / loadImages(album： AlbunBean?)
 ```
 ##### 拍照功能
 ``` kotlin

    /**
     * 检查当前设备相机是否可用
     */
    MediaSelector.checkCamera(activity: Context): Boolean

    /**
     * 打开系统相机拍照，不保存至文件
     * 成功拍照后在onActivityResult中返回的Intent中将附带缩略图Bitmap数据
     * 可通过intent.extras.get("data")获取缩略图
     */
    MediaSelector.takePictureWithoutSave(requestCode: Int)

    /**
     * 打开系统相机拍照，保存至默认的文件目录 (getExternalFilesDir("pictures"))
     * 可以选择单参数的同名方法以使用默认的文件名保存
     * 
     *  @param name：指定的文件名称
     */
    MediaSelector.takePicture(requestCode: Int, name: String)
    
    /**
     * 打开系统相机拍照，保存至指定文件
     *
     * 从 Android N 开始，file://方式的Uri无法在应用间传递使用，使用该方式会抛出FileUriExposedException异常
     * 建议使用FileProvider生成Uri
     *
     * @param fileUri: 文件的保存目录，若为空则默认保存至应用外部存储的picture文件夹下
     *
     */
    MediaSelector.takePicture(requestCode: Int, fileUri: Uri?)
 ```