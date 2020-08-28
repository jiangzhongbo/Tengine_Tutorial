package com.facesdk.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.facesdk.FaceDetector;
import com.facesdk.FaceInfo;
import com.facesdk.R;
import com.facesdk.camera.CameraEngine;
import com.facesdk.currencyview.OverlayView;
import com.facesdk.utils.FoundationDraw;
import com.facesdk.utils.LipDraw;
import com.facesdk.utils.SensorEventUtil;
import com.tenginekit.AndroidConfig;
import com.tenginekit.Face;
import com.tenginekit.model.FaceLandmarkInfo;

import java.util.ArrayList;
import java.util.List;


public class ClassifierActivity extends CameraActivity {
    private static final String TAG = "ClassifierActivity";

    private OverlayView trackingOverlay;

    List<Bitmap> testFaceBitmaps = new ArrayList<>();
    private final Paint circlePaint = new Paint();
    Paint beautyPaint = new Paint();
    Bitmap faceBitmap;
    List<FaceInfo> faceInfos;
    private SensorEventUtil sensorEventUtil;


    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected Size getDesiredPreviewFrameSize() {
        return new Size(1280, 960);
    }

    @Override
    public void onInit(int previewWidth, int previewHeight, int outputWidth, int outputHeight) {

        FaceDetector.init();
        com.tenginekit.Face.init(getBaseContext(),
                AndroidConfig.create()
                        .setInputImageFormat(AndroidConfig.ImageFormat.YUV_NV21)
        );

        com.tenginekit.Face.Camera.switchCamera(false);

        sensorEventUtil = new SensorEventUtil(this);

        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStrokeWidth((float) 2.0);
        circlePaint.setStyle(Paint.Style.STROKE);

        beautyPaint.setColor(Color.WHITE);
        beautyPaint.setAlpha(50);
        beautyPaint.setStyle(Paint.Style.FILL);

        trackingOverlay = findViewById(R.id.facing_overlay);
        trackingOverlay.addCallback(new OverlayView.DrawCallback() {
            @Override
            public void drawCallback(final Canvas canvas) {
                if(faceBitmap != null){
                    canvas.drawBitmap(faceBitmap, 0,0, null);

                }
                if(faceInfos != null){
                    for(int i = 0; i < faceInfos.size(); i++){
                        canvas.drawRect(faceInfos.get(i).getRect(), circlePaint);
                    }
                }
            }
        });


    }

    @Override
    protected void processImage(byte[] yuv, int previewWidth, int previewHeight, int outputWidth, int outputHeight) {

        if(faceBitmap != null){
            faceBitmap.recycle();
        }
        faceBitmap = com.tenginekit.Face.Image.convertCameraYUVData(
                yuv,
                previewWidth, previewHeight,
                outputWidth, outputHeight,
                - 90,
                true);

        faceInfos =  FaceDetector.detectByBitmap(faceBitmap);

        for(Bitmap bitmap : testFaceBitmaps){
            bitmap.recycle();
        }
        testFaceBitmaps.clear();

        runInBackground(new Runnable() {
            @Override
            public void run() {
                trackingOverlay.postInvalidate();
            }
        });
    }


    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        com.tenginekit.Face.release();
        FaceDetector.release();
    }

}