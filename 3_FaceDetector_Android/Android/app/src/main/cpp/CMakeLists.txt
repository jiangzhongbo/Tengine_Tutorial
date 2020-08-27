cmake_minimum_required(VERSION 3.10)
project(FaceDetect)

set (
        CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS} -std=c++11 -fstrict-aliasing -ffast-math -flax-vector-conversions -O1")
set (
        CMAKE_C_FLAGS
        "${CMAKE_C_FLAGS} -std=gnu99 -fvisibility=hidden -fomit-frame-pointer -fstrict-aliasing -ffunction-sections -fdata-sections -ffast-math -flax-vector-conversions")


set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../libs/${ANDROID_ABI})

set(OpenCV_DIR "D:/Android_ENV/OpenCV-android-sdk/sdk/native/jni")
find_package(OpenCV 3.4 REQUIRED)

include_directories(tengine/include/)
include_directories(D:/Android_ENV/OpenCV-android-sdk/sdk/native/jni/include)

add_library(tengine SHARED IMPORTED)
set_target_properties(tengine PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/libtengine-lite.so)

find_library(
        log-lib
        log )

add_library(FaceDetect SHARED UltraFace.cpp UltraFace.hpp FaceDetector.cpp AndroidLog.h)


target_link_libraries(FaceDetect tengine ${OpenCV_LIBS} ${log-lib} z jnigraphics)