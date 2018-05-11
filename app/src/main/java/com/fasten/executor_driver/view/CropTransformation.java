package com.fasten.executor_driver.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import com.squareup.picasso.Transformation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
public class CropTransformation implements Transformation {

//  private static final String TAG = "PicassoTransformation";

  private final float mAspectRatio;
  @Gravity
  private final int mGravity;

  /**
   * Crops to the largest image that will fit the given aspectRatio.
   * This will effectively chop off either the top/bottom or left/right of the source image.
   */
  CropTransformation() {
    mAspectRatio = 1;
    mGravity = Gravity.GRAVITY_CENTER;
  }

  /**
   * Crops to the largest image that will fit the given aspectRatio.
   * This will effectively chop off either the top/bottom or left/right of the source image.
   *
   * @param aspectRatio width/height: greater than 1 is landscape, less than 1 is portrait, 1 is
   * square
   */
  @SuppressWarnings("unused")
  CropTransformation(float aspectRatio) {
    mAspectRatio = aspectRatio;
    mGravity = Gravity.GRAVITY_CENTER;
  }

  /**
   * Crops to the largest image that will fit the given aspectRatio.
   * This will effectively chop off either the top/bottom or left/right of the source image.
   *
   * @param aspectRatio width/height: greater than 1 is landscape, less than 1 is portrait, 1 is
   * square
   * @param gravity position of the cropped area within the larger source image
   */
  @SuppressWarnings("unused")
  CropTransformation(float aspectRatio, @Gravity int gravity) {
    mAspectRatio = aspectRatio;
    mGravity = gravity;
  }

  @Override
  public Bitmap transform(Bitmap source) {
//        Log.v(TAG, "transform(): called, " + key());
    if (mAspectRatio == 0) {
      return source;
    }

    int left = 0;
    int top = 0;
    int width = 0;
    int height = 0;

    float sourceRatio = (float) source.getWidth() / (float) source.getHeight();

//        Log.v(TAG,"transform(): mAspectRatio: " + mAspectRatio + ", sourceRatio: " + sourceRatio);

    if (sourceRatio > mAspectRatio) {
      // source is wider than we want, restrict by height
      height = source.getHeight();
    } else {
      // source is taller than we want, restrict by width
      width = source.getWidth();
    }

//        Log.v(TAG, "transform(): before setting other of h/w: mAspectRatio: " + mAspectRatio + ", set one of width: " + width + ", height: " + height);

    if (width != 0) {
      height = (int) ((float) width / mAspectRatio);
    } else {
      if (height != 0) {
        width = (int) ((float) height * mAspectRatio);
      }
    }

//        Log.v(TAG, "transform(): mAspectRatio: " + mAspectRatio + ", set width: " + width + ", height: " + height);

    if (width == 0) {
      width = source.getWidth();
    }

    if (height == 0) {
      height = source.getHeight();
    }

    switch (mGravity) {
      case Gravity.GRAVITY_BOTTOM_LEFT:
        left = 0;
        top = source.getWidth() - width;
        break;
      case Gravity.GRAVITY_BOTTOM_RIGHT:
        left = source.getHeight() - height;
        top = source.getWidth() - width;
        break;
      case Gravity.GRAVITY_BOTTOM:
        left = (source.getWidth() - width) / 2;
        top = source.getWidth() - width;
        break;
      case Gravity.GRAVITY_LEFT:
        left = 0;
        top = (source.getHeight() - height) / 2;
        break;
      case Gravity.GRAVITY_RIGHT:
        left = source.getHeight() - height;
        top = (source.getHeight() - height) / 2;
        break;
      case Gravity.GRAVITY_CENTER:
        left = (source.getWidth() - width) / 2;
        top = (source.getHeight() - height) / 2;
        break;
      case Gravity.GRAVITY_TOP_LEFT:
        top = 0;
        left = 0;
        break;
      case Gravity.GRAVITY_TOP_RIGHT:
        left = source.getHeight() - height;
        top = 0;
        break;
      case Gravity.GRAVITY_TOP:
        left = (source.getWidth() - width) / 2;
        top = 0;
        break;
    }

    Rect sourceRect = new Rect(left, top, (left + width), (top + height));
    Rect targetRect = new Rect(0, 0, width, height);

//        Log.v(TAG, "transform(): created sourceRect with mLeft: " + left + ", mTop: " + top + ", right: " + (left + width) + ", bottom: " + (top + height));

    Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

//        Log.v(TAG, "transform(): copying from source with width: " + source.getWidth() + ", height: " + source.getHeight());
    canvas.drawBitmap(source, sourceRect, targetRect, null);

    source.recycle();

//        Log.v(TAG, "transform(): returning bitmap with width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

    return bitmap;
  }

  @Override
  public String key() {
    return "CropTransformation(mAspectRatio=" + mAspectRatio + ", gravity=" + mGravity + ")";
  }

  @IntDef({
      Gravity.GRAVITY_TOP_LEFT,
      Gravity.GRAVITY_TOP,
      Gravity.GRAVITY_TOP_RIGHT,
      Gravity.GRAVITY_LEFT,
      Gravity.GRAVITY_CENTER,
      Gravity.GRAVITY_RIGHT,
      Gravity.GRAVITY_BOTTOM_LEFT,
      Gravity.GRAVITY_BOTTOM,
      Gravity.GRAVITY_BOTTOM_RIGHT,
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Gravity {

    int GRAVITY_TOP_LEFT = 0;
    int GRAVITY_TOP = 1;
    int GRAVITY_TOP_RIGHT = 2;
    int GRAVITY_LEFT = 3;
    int GRAVITY_CENTER = 4;
    int GRAVITY_RIGHT = 5;
    int GRAVITY_BOTTOM_LEFT = 6;
    int GRAVITY_BOTTOM = 7;
    int GRAVITY_BOTTOM_RIGHT = 8;
  }
}
