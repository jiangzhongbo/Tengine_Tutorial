# Tengine 人脸检测 Android Camera版

这篇是[第4篇-Tengine 人脸检测 Android版](https://zhuanlan.zhihu.com/p/202616595)的扩展，我们将用[TengineKit](https://github.com/OAID/TengineKit)，把Android Camera输入的YUV格式的视频流转为一帧帧的Bitmap，然后用上第4篇写的人脸检测，快速的变成一个Android Camera应用。

源码在此处[FaceDetector_Android_Camera](https://github.com/jiangzhongbo/Tengine_Tutorial/tree/master/4_FaceDetector_Android_Camera)

## TengineKit

[TengineKit](https://github.com/OAID/TengineKit)是一个易于集成的人脸检测和人脸关键点SDK。它可以在各种手机上以非常低的延迟运行。

![](./imgs/TengineKitDemo2.gif)

### TengineKit图片处理功能

TengineKit.Image类封装了一系列工具API，帮助开发者快速方便的处理图片相关的功能，具体功能请查阅[文档](https://github.com/OAID/TengineKit/blob/master/docs/Api_CN.md)，这里我们将用这个TengineKit.Image里面的API实现Android前置摄像头视频流转为Bitmap（YUV_NV21转RGBA）。

## 实现

TengineKit的配置和Camera应用的结构都是参考这篇文章

[用开源212点人脸关键点实现Android人脸实时打码，内附Github地址](https://zhuanlan.zhihu.com/p/161038093)

我们拿核心部分讲一下

### YUV转Bitmap

1. previewWidth, previewHeight 是摄像头输入视频流的宽高
2. outputWidth, outputHeight 是希望输出的宽高
3. 为什么要旋转-90度和镜像翻转？因为我们用的前置摄像头，前置摄像头输出的视频流旋转-90度然镜像反正下，得到的就是我们正常看到的照片

```java
@Override
protected void processImage(byte[] yuv, int previewWidth, int previewHeight, int outputWidth, int outputHeight) {

    if(faceBitmap != null){
        faceBitmap.recycle();
    }
    faceBitmap = com.tenginekit.Face.Image.convertCameraYUVData(
            yuv,
            previewWidth, previewHeight,
            outputWidth, outputHeight,
            - 90,
            true);

    faceInfos =  FaceDetector.detectByBitmap(faceBitmap);

    for(Bitmap bitmap : testFaceBitmaps){
        bitmap.recycle();
    }
    testFaceBitmaps.clear();

    runInBackground(new Runnable() {
        @Override
        public void run() {
            trackingOverlay.postInvalidate();
        }
    });
}
```
相对于上篇文章，这里给FaceDetector新封装了一个API，可以直接传入Bitmap得到人脸数据。
```java
public static List<FaceInfo> detectByBitmap(Bitmap image){
    return detectByBytes(bitmap2Bytes(image), image.getWidth(), image.getHeight());
}

private static byte[] bitmap2Bytes(Bitmap image) {
    // calculate how many bytes our image consists of
    int bytes = image.getByteCount();
    ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
    image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
    byte[] temp = buffer.array(); // Get the underlying array containing the
    return temp;
}
```
## 参考

1. [https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB](https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB)

2. [https://github.com/OAID/Tengine](https://github.com/OAID/Tengine)

3. [https://github.com/OAID/TengineKit](https://github.com/OAID/TengineKit)

4. [https://github.com/jiangzhongbo/TengineKit_Demo_Identity_Protection](https://github.com/jiangzhongbo/TengineKit_Demo_Identity_Protection)