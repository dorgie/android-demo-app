package org.pytorch.demo.objectdetection;
interface IMyAidlInterface {
    int detect(in Bitmap bitmap, out float[] rects, out int[] classes, out float[] scores);
}