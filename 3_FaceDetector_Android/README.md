# Tengine 人脸检测 Android版

**所有内容都在 [FaceDetector_Android](https://github.com/jiangzhongbo/Tengine_Tutorial/tree/master/3_FaceDetector_Android/Android) 如果有什么不清楚的直接看代码**

## 准备

1. 用 Android Studio 建立一个空白Android项目
2. 把[第1篇-Tengine Android版本编译](https://zhuanlan.zhihu.com/p/182743221)中编译好的so复制到Android项目中libs目录
3. 把[第2篇-Tengine 转换模型](https://zhuanlan.zhihu.com/p/187387769)中转化好的模型复制到Android项目中assets目录
4. 把[第3篇-Tengine 人脸检测X86版本](https://zhuanlan.zhihu.com/p/196450160)中的UltraFace.cpp, UltraFace.hpp， tengine_c_api.h复制到Android项目中的cpp目录

## 编译环境搭建

### Android NDK

我用的是android-ndk-r16b，在Android项目中local.properties中添加NDK地址，例如我的就是
```
sdk.dir=D\:\\Android_ENV\\Android\\Sdk
ndk.dir=D\:\\Android_ENV\\android-ndk-r16b
```

### Android OpenCV

下载[opencv-3.4.11-android-sdk.zip](https://github.com/opencv/opencv/releases/download/3.4.11/opencv-3.4.11-android-sdk.zip)，然后解压

## 编写CMakeLists.txt和配套build.gradle

1. 和X86版本比较大的区别就是改OpenCV为Android版本，OpenCV地址改为你下载解压后的地址

```
set(OpenCV_DIR "D:/Android_ENV/OpenCV-android-sdk/sdk/native/jni")
find_package(OpenCV 3.4 REQUIRED)
include_directories(D:/Android_ENV/OpenCV-android-sdk/sdk/native/jni/include)
```

2. 改了动态链接库的名字
```
project(FaceDetect)
```
这个后面写JNI的时候会用到。

3. 因为我们用了CMakeLists.txt，所以需要修改下build.gradle

```
android {
    ...
    defaultConfig {
        ...
        externalNativeBuild {
            cmake {
                cppFlags ""
                cppFlags "-std=c++11 -frtti -fexceptions"
                abiFilters 'armeabi-v7a', 'arm64-v8a'

                arguments "-DANDROID_TOOLCHAIN=clang"
                cFlags "-O2 -fvisibility=hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math "
                cppFlags "-O2 -fvisibility=hidden -fvisibility-inlines-hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math "

                //rtti(run-time type interface)运行时类型信息，这是编译器的一个特性，默认时关闭状态
                //如果需要在C/C++代码中调用Java的方法，需要手动开启此特性
                cppFlags "-frtti -fexceptions -std=c++11 -v -Wdeprecated-declarations"
            }
        }
        ndk {
            //声明启用Android日志, 在c/c++的源文件中使用的#include <android/log.h> 日志将得到输出
            ldLibs "log"

            //声明创建指定cpu架构的so库
            //如果想在模拟器运行 加上 "x86"
            abiFilters 'armeabi-v7a', 'arm64-v8a'

            stl "gnustl_static"
        }
    }
    externalNativeBuild {
        cmake {
            path 'src/main/cpp/CMakeLists.txt'
            version "3.10.2"
        }
    }
    sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
        }
    }
    ...
}
```

## 编写Java和C++的交互代码

编写JNI，让Java和C++联系起来

### java native接口
```java
public class FaceDetector {
    static {
        System.loadLibrary("FaceDetect");
    }

    public static native void init();

    public static native float[] detect(byte[] img, int w, int h);

    public static native void release();

    public static List<FaceInfo> detectByBytes(byte[] img, int w, int h){
        float[] data = detect(img, w, h);
        if(data != null && data.length % 5 == 0){
            int num = data.length / 5;
            List<FaceInfo> faceInfos = new ArrayList<>(num);
            for(int i = 0; i < num; i++){
                FaceInfo faceInfo = new FaceInfo();
                faceInfo.x1 = data[i * 5 + 0];
                faceInfo.y1 = data[i * 5 + 1];
                faceInfo.x2 = data[i * 5 + 2];
                faceInfo.y2 = data[i * 5 + 3];
                faceInfo.score = data[i * 5 + 4];
                faceInfos.add(faceInfo);
            }
            return faceInfos;
        }
        return null;
    }
}
```

### JNI接口

**如果JNI用c++写，extern "C"是一定要的，不然会出错**

```c++
extern "C"{


jfloatArray faces_to_floats(JNIEnv *env, std::vector<FaceInfo> &faces){
    jfloatArray jarr = env->NewFloatArray(faces.size() * 5);
    jfloat *arr = env->GetFloatArrayElements(jarr, NULL);
    for (int i = 0; i < faces.size(); i++) {
        arr[5 * i + 0] = faces[i].x1;
        arr[5 * i + 1] = faces[i].y1;
        arr[5 * i + 2] = faces[i].x2;
        arr[5 * i + 3] = faces[i].y2;
        arr[5 * i + 4] = faces[i].score;
    }
    env->ReleaseFloatArrayElements(jarr, arr, 0);
    return jarr;
}

UltraFace *ultraface;
JNIEXPORT void JNICALL
Java_com_facesdk_FaceDetector_init(JNIEnv *env, jclass){
    if(!ultraface){
        ultraface = new UltraFace("/sdcard/OAL/version-RFB-320_simplified.tmfile", 320, 240, 4, 0.65);
    }
}

JNIEXPORT jfloatArray JNICALL
Java_com_facesdk_FaceDetector_detect(JNIEnv *env, jclass, jbyteArray img, jint w, jint h){
    jbyte* arr = env->GetByteArrayElements(img, 0);
    cv::Mat frame(h, w, CV_8UC4, (char *)arr);
    cv::Mat rgb;
    cv::cvtColor(frame, rgb, CV_RGBA2RGB);
    std::vector<FaceInfo> face_info;
    ultraface->detect(rgb, face_info);
    env->ReleaseByteArrayElements(img, arr, 0);
    return faces_to_floats(env, face_info);
}

JNIEXPORT void JNICALL
Java_com_facesdk_FaceDetector_release(JNIEnv *env, jclass){
    if(ultraface){
        delete ultraface;
    }
}
}
```
## 测试

```java
Drawable d = null;
Bitmap bb = null;
try {
    d = Drawable.createFromStream(getAssets().open("girls.jpg"), null);
    showImage.setImageDrawable(d);
    bb = ((BitmapDrawable)d).getBitmap();
}catch (Exception e){
    e.printStackTrace();
}

byte[] girl = bitmap2Bytes(bb);

FaceDetector.init();
List<FaceInfo> faceInfos = FaceDetector.detectByBytes(girl, d.getIntrinsicWidth(), d.getIntrinsicHeight());
FaceDetector.release();
```

|  原图   | 效果  |
|  ----  | ----  |
| ![](imgs/girls.jpg)  | ![](imgs/girls.png) |

## 参考

1. [https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB](https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB)
2. [https://github.com/OAID/Tengine](https://github.com/OAID/Tengine)