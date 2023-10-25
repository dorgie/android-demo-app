package com.idaka.objectdetect;

/**
 *  Usage:
 *  const int MAX_DETECTIONS = 15;
 *  float[] rects = new float[4 * MAX_DETECTIONS];
 *  float[] scores = new float[MAX_DETECTIONS];
 *  int[] classes = new int[MAX_DETECTIONS];
 *  int num = objectDetect.detect(bitmap, 0.5f, rects, scores, classes);
 *  for (int i = 0; i < num; i++) {
 *      float left = rects[4 * i];
 *      float top = rects[4 * i + 1];
 *      float right = rects[4 * i + 2];
 *      float bottom = rects[4 * i + 3];
 *      float score = scores[i]; // 0.0 ~ 1.0
 *      int cls = classes[i]; // 0 = safety vest
 *  }
 */
interface IObjectDetect {
    int detect(in Bitmap bitmap, in float threshold, out float[] rects, out float[] scores, out int[] classes);
}
