# Tengine Android版本编译

## 环境
1. System: Ubuntu18.04
2. CMAKE: 3.15.3

## 下载Android NDK

```
wget ftp://ftp.openailab.net.cn/Tengine_android_build/android-ndk-r16b-linux-x86_64.zip
unzip android-ndk-r16b-linux-x86_64.zip
```

## 克隆Tengine源码

```
git clone https://github.com/OAID/Tengine.git
```

## 编译

### 设置ANDROID_NDK环境变量
**ANDROID_NDK为刚才解压的zip文件**
```
export ANDROID_NDK=/home/oal/ssd_data/workspace/tmp/android-ndk-r16b
```

### 编译ARM32

```
cd Tengine
mkdir build_android_32
cd build_android_32
cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake -DANDROID_ABI="armeabi-v7a" -DANDROID_PLATFORM=android-22 -DANDROID_STL=c++_shared -DANDROID_ARM_NEON=ON -DCONFIG_ARCH_ARM32=ON -DANDROID_ALLOW_UNDEFINED_SYMBOLS=TRUE ..
```
如果输出下面内容，说明没有问题
```
-- Configuring done
-- Generating done
```
编译
```
make -j4 && make install
```

### 编译ARM64

```
cd Tengine
mkdir build_android_64
cd build_android_64
cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake -DANDROID_ABI="arm64-v8a" -DANDROID_PLATFORM=android-22 -DANDROID_STL=c++_shared -DANDROID_ARM_NEON=ON -DCONFIG_ARCH_ARM64=ON -DANDROID_ALLOW_UNDEFINED_SYMBOLS=TRUE ..
```
如果输出下面内容，说明没有问题
```
-- Configuring done
-- Generating done
```
编译
```
make -j4 && make install
```

## 测试

绝大多数手机都支持ARM32，我们用编译好的ARM32版本测试编译出来的so有没有问题。

把 https://github.com/jiangzhongbo/Tengine_Tutorial/tree/master/0_Compile 中的cat.jpg，squeezenet_caffe.tmfile放到Tengine/build_android_32/install/bin目录下

然后push到手机的/data/local/tmp/目录下，这个tmp目录是Android系统的特殊目录，放在里面的文件可以被赋予可执行权限。

```
cd Tengine/build_android_32/install
adb push ./lib/libtengine-lite.so /data/local/tmp/
adb push ./bin/tm_classification /data/local/tmp/
adb push ./bin/cat.jpg /data/local/tmp/
adb push ./bin/squeezenet_caffe.tmfile /data/local/tmp/
```
**有些手机可能无法直接push到/data/local/tmp文件夹，这种情况下可以先push到/sdcard/文件夹，然后在mv到/data/local/tmp/**

用下面命令进入手机终端并进行测试
```
adb shell
cd /data/local/tmp/
export LD_LIBRARY_PATH=.
chmod +x tm_classification
./tm_classification -m squeezenet_caffe.tmfile -i cat.jpg
```
输出
```
Image height not specified, use default 227
Image width not specified, use default  227
Scale value not specified, use default  1.0, 1.0, 1.0
Mean value not specified, use default   104.0, 116.7, 122.7
tengine-lite library version: 0.2-dev

model file : squeezenet_caffe.tmfile
image file : ./cat.jpg
img_h, img_w, scale[3], mean[3] : 227 227 , 1.000 1.000 1.000, 104.0 116.7 122.7
Repeat 1 times, thread 1, avg time 33.31 ms, max_time 33.31 ms, min_time 33.31 ms
--------------------------------------
0.273201, 281
0.267551, 282
0.181003, 278
0.081799, 285
0.072407, 151
--------------------------------------
```
自此Tengine Android版本编译成功。

## 自动化编译脚本

```
wget ftp://ftp.openailab.net.cn/Tengine_android_build/android-ndk-r16b-linux-x86_64.zip
unzip android-ndk-r16b-linux-x86_64.zip
export ANDROID_NDK=`pwd`/android-ndk-r16b


git clone https://github.com/OAID/Tengine.git
git clone https://github.com/jiangzhongbo/Tengine_Tutorial
cd Tengine

mkdir build_android_32
cd build_android_32
cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake -DANDROID_ABI="armeabi-v7a" -DANDROID_PLATFORM=android-22 -DANDROID_STL=c++_shared -DANDROID_ARM_NEON=ON -DCONFIG_ARCH_ARM32=ON -DANDROID_ALLOW_UNDEFINED_SYMBOLS=TRUE ..
make -j4 && make install

cd ./install/bin
cp ../../../../Tengine_Tutorial/0_Compile/cat.jpg ./
cp ../../../../Tengine_Tutorial/0_Compile/squeezenet_caffe.tmfile ./

cd ../../..

mkdir build_android_64
cd build_android_64
cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake -DANDROID_ABI="arm64-v8a" -DANDROID_PLATFORM=android-22 -DANDROID_STL=c++_shared -DANDROID_ARM_NEON=ON -DCONFIG_ARCH_ARM64=ON -DANDROID_ALLOW_UNDEFINED_SYMBOLS=TRUE ..
make -j4 && make install

cd ./install/bin
cp ../../../../Tengine_Tutorial/0_Compile/cat.jpg ./
cp ../../../../Tengine_Tutorial/0_Compile/squeezenet_caffe.tmfile ./

```

为了方便，你可以直接
```
wget https://raw.githubusercontent.com/jiangzhongbo/Tengine_Tutorial/master/0_Compile/compile_tengine_android.sh
sh compile_tengine_android.sh
```

## 参考

[https://github.com/OAID/Tengine/wiki/Tengine源码编译](https://github.com/OAID/Tengine/wiki/Tengine源码编译)
