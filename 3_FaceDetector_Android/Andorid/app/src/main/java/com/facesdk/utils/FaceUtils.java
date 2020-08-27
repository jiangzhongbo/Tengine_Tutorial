package com.facesdk.utils;

import android.graphics.Path;

import com.tenginekit.model.FaceLandmarkInfo;

import java.util.ArrayList;
import java.util.List;

public class FaceUtils {
    int[][] triangles = {
            {1,160,4},
    };

    public static List<Path> getFaceTriangles(FaceLandmarkInfo fi){
        List<Path> paths = new ArrayList<>();
        Path path = new Path();
        path.moveTo(
                fi.landmarks.get(0).X,
                fi.landmarks.get(0).Y
        );
        path.lineTo(
                fi.landmarks.get(159).X,
                fi.landmarks.get(159).Y
        );
        path.lineTo(
                fi.landmarks.get(3).X,
                fi.landmarks.get(3).Y
        );
        path.close();
        paths.add(path);
        return paths;
    }
}
