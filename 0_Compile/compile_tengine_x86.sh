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