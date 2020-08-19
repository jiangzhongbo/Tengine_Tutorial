# Tengine 转换模型

## 环境
**System**: Ubuntu18.04

**CMAKE**: 3.15.3


## 编译模型转化工具

Tengine其实是提供Linux版本的二进制模型转化工具（[下载](https://github.com/OAID/Tengine/releases)）,为了让读者了解的更详细些，还是把工具编译过程写一下。

### 准备工作

为了确保后续执行没有问题，先执行
```
sudo apt install libprotobuf-dev protobuf-compiler
```
安装完后，终端中会显示下面内容
```
libprotobuf-dev 已经是最新版
protobuf-compiler 已经是最新版
```

### 克隆Tengine-Convert-Tools源码

```
git clone https://github.com/OAID/Tengine-Convert-Tools
```

### 编译

```
cd Tengine-Convert-Tools
mkdir build && cd build
cmake ..
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

编译完成后该工具放在 **./build/install/bin**

## 转换模型

![](./face_detect.jpg)

[Ultra-Light-Fast-Generic-Face-Detector-1MB](https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB)

该模型是针对边缘计算设备设计的轻量人脸检测模型:

1. 在模型大小上，默认FP32精度下（.pth）文件大小为 1.04~1.1MB，推理框架int8量化后大小为 300KB 左右。
2. 在模型计算量上，320x240的输入分辨率下 90~109 MFlops左右。
3. 模型有两个版本，version-slim(主干精简速度略快)，version-RFB(加入了修改后的RFB模块，精度更高)。
4. 提供320x240、640x480不同输入分辨率下使用widerface训练的预训练模型，更好的工作于不同的应用场景。
5. 支持onnx导出。

我们将把该模型改为Tengine可用模型。

我们先下载Ultra-Light-Fast-Generic-Face-Detector-1MB

```
git clone https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB
```

将./Ultra-Light-Fast-Generic-Face-Detector-1MB/models/onnx下的version-RFB-320_simplified.onnx复制到./Tengine-Convert-Tools/build/install/bin

```
cp ./Ultra-Light-Fast-Generic-Face-Detector-1MB/models/onnx/version-RFB-320_simplified.onnx ./Tengine-Convert-Tools/build/install/bin/
```

这个模型是经过[onnx-simplifier](https://github.com/daquexian/onnx-simplifier)优化的，如果不操作此过程，可能会保留部分不支持算子。

### 转换

```
./tm_convert_tool -f onnx -m version-RFB-320_simplified.onnx -o version-RFB-320_simplified.tmfile
```

输出

```
Create tengine model file done: version-RFB-320_simplified.tmfile
```

自此转化成功

## 自动编译脚本

```
sudo apt install libprotobuf-dev protobuf-compiler

git clone https://github.com/OAID/Tengine-Convert-Tools
git clone https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB

cd Tengine-Convert-Tools
mkdir build && cd build
cmake ..
make -j4 && make install

cd ../..

cp ./Ultra-Light-Fast-Generic-Face-Detector-1MB/models/onnx/version-RFB-320_simplified.onnx ./Tengine-Convert-Tools/build/install/bin/

cd ./Tengine-Convert-Tools/build/install/bin/

./tm_convert_tool -f onnx -m version-RFB-320_simplified.onnx -o version-RFB-320_simplified.tmfile
```

你可以直接
```
wget https://raw.githubusercontent.com/jiangzhongbo/Tengine_Tutorial/master/1_Convert/convert_facedetect_onnx_2_tmfile.sh
sh convert_facedetect_onnx_2_tmfile.sh
```

## 参考

[https://github.com/OAID/Tengine-Convert-Tools](https://github.com/OAID/Tengine-Convert-Tools)

[github.com/daquexian/onnx-simplifier](github.com/daquexian/onnx-simplifier)

[Ultra-Light-Fast-Generic-Face-Detector-1MB](https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB)
