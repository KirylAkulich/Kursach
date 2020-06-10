package com.example.kursach1;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Trace;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.support.v7.app.AppCompatActivity;
public class Transformer {

    TensorFlowInferenceInterface inferenceInterface;
    private String path;

    public Transformer(AssetManager assetManager, String path){
        inferenceInterface=new TensorFlowInferenceInterface(assetManager,path);

    }
    public void SetModel(AssetManager assetManager,String path){
        inferenceInterface=new TensorFlowInferenceInterface(assetManager,path);
    }

    public Bitmap stylizeImage(Bitmap bitmap,int INPUT_SIZE) {
        Bitmap scaledBitmap = scaleBitmap(bitmap, INPUT_SIZE, INPUT_SIZE);
        int [] intValues=new int[INPUT_SIZE*INPUT_SIZE];
        float[] floatValues=new float[INPUT_SIZE*INPUT_SIZE*3];
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3 + 0] = ((val >> 16) & 0xFF) * 1.0f;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) * 1.0f;
            floatValues[i * 3 + 2] = (val & 0xFF) * 1.0f;
        }

        Trace.beginSection("feed");
        inferenceInterface.feed("input", floatValues, INPUT_SIZE, INPUT_SIZE, 3);
        Trace.endSection();

        Trace.beginSection("run");
        inferenceInterface.run(new String[]{"output"});
        Trace.endSection();

        Trace.beginSection("fetch");
        inferenceInterface.fetch("output", floatValues);
        Trace.endSection();

        for (int i = 0; i < intValues.length; ++i) {
            intValues[i] =
                    0xFF000000
                            | (((int) (floatValues[i * 3 + 0])) << 16)
                            | (((int) (floatValues[i * 3 + 1])) << 8)
                            | ((int) (floatValues[i * 3 + 2]));
        }
        scaledBitmap.setPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        return scaledBitmap;
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBitmap;
    }


}
