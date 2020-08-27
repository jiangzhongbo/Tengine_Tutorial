#include <jni.h>
#include "UltraFace.hpp"
#include "AndroidLog.h"
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
