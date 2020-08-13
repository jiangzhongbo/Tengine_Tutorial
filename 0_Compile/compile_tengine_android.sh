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