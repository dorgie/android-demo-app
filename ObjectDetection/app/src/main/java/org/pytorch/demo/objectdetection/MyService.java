package org.pytorch.demo.objectdetection;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends Service {
    public MyService() {
    }

    private final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {
        Module mModule = null;
        float mThreshold = 0.0f;
        public int detect(Bitmap bitmap, float[] rects, int[] classes, float[] scores){
            if (mModule == null) {
                try {
                    mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "best.torchscript"));
                } catch (IOException e) {
                }
            }
            Log.d("detect", String.format("width %d height %d", bitmap.getWidth(), bitmap.getHeight()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
            final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
            IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
            final Tensor outputTensor = outputTuple[0].toTensor();
            final float[] outputs = outputTensor.getDataAsFloatArray();
            long[] shape = outputTensor.shape(); // [1, 1575, 6]
            float maxScore = 0;
            float argmax = -1;
            for(int i = 0; i < shape[1]; i++){
                float score = outputs[(int) (i * shape[2] + 4)];
                if(score > maxScore && score > mThreshold){
                    maxScore = score;
                    argmax = i;
                    rects[0] = outputs[(int) (i*shape[2]+0)];
                    rects[1] = outputs[(int) (i*shape[2]+1)];
                    rects[2] = outputs[(int) (i*shape[2]+2)];
                    rects[3] = outputs[(int) (i*shape[2]+3)];
                    scores[0] = score;
                    classes[0] = 0;
                }
            }
            Log.d("maxScore", String.valueOf(maxScore));
            return argmax >= 0? 1: 0;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}