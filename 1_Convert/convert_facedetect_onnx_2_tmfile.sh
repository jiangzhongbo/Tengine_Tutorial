sudo apt install libprotobuf-dev protobuf-compiler

git https://github.com/OAID/Tengine-Convert-Tools
git clone https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB

cd Tengine-Convert-Tools
mkdir build && cd build
cmake ..
make -j4 && make install

cp ./Ultra-Light-Fast-Generic-Face-Detector-1MB/models/onnx/version-RFB-320_simplified.onnx ./Tengine-Convert-Tools/build/install/bin/

./tm_convert_tool -f onnx -m version-RFB-320_simplified.onnx -o version-RFB-320_simplified.tmfile