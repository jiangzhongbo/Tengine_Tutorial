cmake_minimum_required(VERSION 3.10)
project(Ultra-face-tengine)

set(CMAKE_CXX_STANDARD 11)


find_package(OpenCV REQUIRED)
include_directories(tengine/include/)

add_library(tengine SHARED IMPORTED)
set_target_properties(tengine PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/tengine/lib/libtengine-lite.so)

add_executable(Ultra-face-tengine src/main.cpp src/UltraFace.cpp)
target_link_libraries(Ultra-face-tengine tengine ${OpenCV_LIBS})