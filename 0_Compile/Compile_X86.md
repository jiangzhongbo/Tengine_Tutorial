# Tengine X86版本编译

## 环境
1. System: Ubuntu18.04
2. CMAKE: 3.15.3

为了确保后续执行没有问题，先执行
```
sudo apt-get install cmake make g++ git
```
安装完后，终端中会显示下面内容
```
make 已经是最新版
cmake 已经是最新版
g++ 已经是最新版
git 已经是最新版
```
## 克隆Tengine源码

```
git clone https://github.com/OAID/Tengine.git
```

## 编译

```
cd Tengine
mkdir build 
cd build
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

## 测试

编译没有问题的话，把 https://github.com/jiangzhongbo/Tengine_Tutorial/tree/master/0_Compile 中的cat.jpg，squeezenet_caffe.tmfile放到Tengine/build/install/bin目录下，然后执行下面命令
```
cd Tengine/build/install/bin
./tm_classification -m squeezenet_caffe.tmfile -i ./cat.jpg
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
自此Tengine X86版本编译成功。

## 自动编译脚本

```
sudo apt-get install cmake make g++ git -y

git clone https://github.com/OAID/Tengine.git
git clone https://github.com/jiangzhongbo/Tengine_Tutorial
cd Tengine
mkdir build 
cd build
cmake ..
make -j4 && make install

cd ./install/bin
cp ../../../../Tengine_Tutorial/0_Compile/cat.jpg ./
cp ../../../../Tengine_Tutorial/0_Compile/squeezenet_caffe.tmfile ./

./tm_classification -m squeezenet_caffe.tmfile -i ./cat.jpg
```
你可以直接
```
wget https://raw.githubusercontent.com/jiangzhongbo/Tengine_Tutorial/master/0_Compile/compile_tengine_x86.sh
sh compile_tengine_x86.sh
```