package com.facesdk.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facesdk.FaceDetector;
import com.facesdk.FaceInfo;
import com.facesdk.R;
import com.facesdk.utils.BitmapUtils;
import com.facesdk.utils.FileUtils;
import com.tenginekit.AndroidConfig;
import com.tenginekit.Face;
import com.tenginekit.model.FaceLandmarkInfo;
import com.tenginekit.model.FaceLandmarkPoint;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;


public class ClassifierActivity extends AppCompatActivity {
    private static final String TAG = "ClassifierActivity";

    ImageView showImage;

    List<FaceLandmarkInfo> faceLandmarks;
    private final Paint circlePaint = new Paint();
    private Paint paint = new Paint();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);
        FileUtils.copyAllAssets(this,"sdcard/OAL/");

        onInit();
    }

    public void onInit() {

        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.YELLOW);
        circlePaint.setStrokeWidth((float) 3);
        circlePaint.setStyle(Paint.Style.STROKE);

        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth((float) 5);
        paint.setStyle(Paint.Style.FILL);

        showImage = findViewById(R.id.show_image);

        Drawable d = null;
        Bitmap bb = null;


        try {
            d = Drawable.createFromStream(getAssets().open("girls.jpg"), null);
            showImage.setImageDrawable(d);
            bb = ((BitmapDrawable)d).getBitmap();

        }catch (Exception e){
            e.printStackTrace();
        }


        byte[] girl = bitmap2Bytes(bb);

        FaceDetector.init();

        List<FaceInfo> faceInfos = FaceDetector.detectByBytes(girl, showImage.getDrawable().getIntrinsicWidth(), showImage.getDrawable().getIntrinsicHeight());
        FaceDetector.release();



        Bitmap out_bitmap = Bitmap.createBitmap(
            showImage.getDrawable().getIntrinsicWidth(),
            showImage.getDrawable().getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(out_bitmap);
        canvas.drawBitmap(bb, 0,0 , null);

        if(faceInfos != null){
            for(int i = 0; i < faceInfos.size(); i++){
                canvas.drawRect(faceInfos.get(i).getRect(), circlePaint);
            }
        }

        showImage.setImageBitmap(out_bitmap);

        BitmapUtils.saveBitmap(out_bitmap, "/sdcard/girls.png");
    }


    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        com.tenginekit.Face.release();
    }

    private byte[] bitmap2Bytes(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer
        byte[] temp = buffer.array(); // Get the underlying array containing the
        return temp;
    }

    static private Bitmap bytes2bitmap(byte[] byteArray, int ImageW, int ImageH) {
        Bitmap image1 = Bitmap.createBitmap(ImageW,ImageH, Bitmap.Config.ARGB_8888);
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.get(byteArray);
        Buffer temp = buffer.rewind();

        image1.copyPixelsFromBuffer(temp);
        return image1;
    }
}