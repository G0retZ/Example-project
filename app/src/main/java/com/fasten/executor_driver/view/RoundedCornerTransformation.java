package com.fasten.executor_driver.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

class RoundedCornerTransformation implements Transformation {

  private final int cornerRadiusFactor;
  private final int marginFactor;

  @SuppressWarnings("SameParameterValue")
  RoundedCornerTransformation(int cornerRadiusFactor, int marginFactor) {
    this.cornerRadiusFactor = cornerRadiusFactor;
    this.marginFactor = marginFactor;
  }

  @Override
  public Bitmap transform(Bitmap source) {
    if (cornerRadiusFactor < 2 && marginFactor < 3) {
      return source;
    }
    float size = Math.min(source.getWidth(), source.getHeight());
    float margin;
    if (marginFactor < 3) {
      margin = 0;
    } else {
      margin = size / marginFactor;
    }
    float radius;
    if (cornerRadiusFactor < 2) {
      radius = 0;
    } else {
      radius = (size - 2 * margin) / cornerRadiusFactor;
    }
    final Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

    Bitmap output = Bitmap
        .createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    canvas.drawRoundRect(
        new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius,
        radius, paint);

    if (source != output) {
      source.recycle();
    }

    return output;
  }

  @Override
  public String key() {
    return "RoundedCornerTransformation(cornerRadiusFactor=" + cornerRadiusFactor
        + ", marginFactor=" + marginFactor + ")";
  }
}
