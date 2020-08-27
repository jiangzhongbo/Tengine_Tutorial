package com.facesdk;

import android.graphics.Rect;

public class FaceInfo {
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float score;

    public Rect getRect(){
        return new Rect((int)x1, (int)y1, (int)x2, (int)y2);
    }
}
