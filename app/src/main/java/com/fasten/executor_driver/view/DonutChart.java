package com.fasten.executor_driver.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.view.model.ChartElement;
import java.util.ArrayList;
import java.util.List;

public class DonutChart extends View {

  private float radius;
  private float calculatedRadius;
  private float innerRadiusRatio;
  private boolean sumValueVisible;
  private boolean valuesVisible;
  private int sumValueColor;

  private Paint mPaint;

  private Path mPath;

  private RectF outerCircle;
  private RectF innerCircle;
  private ArrayList<ChartElement> mChartElements;

  public DonutChart(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.DonutChart,
        0, 0
    );

    try {
      radius = a.getDimension(R.styleable.DonutChart_radius, -1.0f);
      innerRadiusRatio = a.getFloat(R.styleable.DonutChart_inner_radius_ratio, 0.4f);
      sumValueVisible = a.getBoolean(R.styleable.DonutChart_sum_value_visible, true);
      valuesVisible = a.getBoolean(R.styleable.DonutChart_values_visible, true);
      sumValueColor = a.getColor(R.styleable.DonutChart_sum_value_color, 0xDF000000);
      float value = a.getFloat(R.styleable.DonutChart_value, -1);
      int valueColor = a.getColor(R.styleable.DonutChart_value_color, 0xFF00FF00);
      int nonValueColor = a.getColor(R.styleable.DonutChart_empty_color, 0x80000000);
      if (value >= 0 && valueColor <= 100) {
        mChartElements = new ArrayList<>();
        mChartElements.add(new ChartElement(value, valueColor));
        mChartElements.add(new ChartElement(100 - value, nonValueColor, false));
      }
    } finally {
      a.recycle();
    }

    calculatedRadius = radius >= 0 ? radius : 20f;

    mPaint = new Paint();
    mPaint.setDither(true);
    mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setAntiAlias(true);
    //mPaint.setStrokeWidth(radius / 14.0f);

    mPath = new Path();

    outerCircle = new RectF();
    innerCircle = new RectF();

    float adjust = 0 * calculatedRadius;
    outerCircle.set(adjust, adjust, calculatedRadius * 2 - adjust, calculatedRadius * 2 - adjust);

    //adjust = .276f * radius;
    adjust = innerRadiusRatio * calculatedRadius;
    innerCircle.set(adjust, adjust, calculatedRadius * 2 - adjust, calculatedRadius * 2 - adjust);

  }

  @SuppressWarnings("unused")
  public void setValues(List<ChartElement> chartElements) {
    if (chartElements == null) {
      mChartElements = null;
    } else {
      if (mChartElements == null) {
        mChartElements = new ArrayList<>();
      }
      mChartElements.clear();
      mChartElements.addAll(chartElements);
    }
    invalidate();
  }

  @SuppressWarnings("unused")
  public void setValue(float value) {
    if (value >= 0 && value <= 100) {
      if (mChartElements == null) {
        mChartElements = new ArrayList<>();
      }
      if (mChartElements.size() < 1) {
        mChartElements.add(new ChartElement(value, 0xFF00FF00));
      } else {
        mChartElements.get(0).setValue(value);
      }
      if (mChartElements.size() < 2) {
        mChartElements.add(new ChartElement(100 - value, 0x80000000, false));
      } else {
        mChartElements.get(1).setValue(100 - value);
      }
      while (mChartElements.size() > 2) {
        mChartElements.remove(2);
      }
      invalidate();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    float sum = 0;
    if (mChartElements == null || mChartElements.size() < 1) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setSolid(getResources().getColor(R.color.colorDivider, null));
      } else {
        setSolid(getResources().getColor(R.color.colorDivider));
      }
      drawDonut(canvas, mPaint, -90, 359, "0", 0xffFFFFFF);
    } else {
      for (ChartElement chartElement : mChartElements) {
        sum += chartElement.getValue();
      }

      float start = -90;
      for (ChartElement chartElement : mChartElements) {
        setSolid(chartElement.getSectorColor());
        float size = chartElement.getValue() * 360 / sum;
        drawDonut(canvas, mPaint, start, size - 1,
            chartElement.isValueVisible() ? String.valueOf(chartElement.getValue())
                .replaceAll(".0$", "") : null, chartElement.getTextColor());
        start += size;
      }
    }
    if (sumValueVisible) {
      drawSum(canvas, mPaint, String.valueOf(sum).replaceAll(".0$", ""), sumValueColor);
    }
  }

  private void drawDonut(Canvas canvas, Paint paint, float start, float sweep, String text,
      int textColor) {
    mPath.reset();
    mPath.arcTo(outerCircle, start, sweep, false);
    mPath.arcTo(innerCircle, start + sweep, -sweep, false);
    mPath.close();
    canvas.drawPath(mPath, paint);
    if (text != null && valuesVisible) {
      paint.setColor(textColor);
      paint.setTextSize(calculatedRadius * 0.2f);
      float angle = start + sweep / 2;
      Rect r = new Rect();
      paint.getTextBounds(text, 0, text.length(), r);
      float xOffset = r.width() / 2;
      float yOffset = r.height() / 2;
      float x = calculatedRadius * 0.8f * (float) Math.cos(angle * Math.PI / 180);
      float y = calculatedRadius * 0.8f * (float) Math.sin(angle * Math.PI / 180);
      canvas
          .drawText(text, x - xOffset + canvas.getWidth() / 2, y + yOffset + canvas.getHeight() / 2,
              paint);
    }
  }

  private void drawSum(Canvas canvas, Paint paint, String text, int textColor) {
    paint.setColor(textColor);
    paint.setTextSize(calculatedRadius * 0.4f);
    Rect r = new Rect();
    paint.getTextBounds(text, 0, text.length(), r);
    float xOffset = r.width() / 2;
    float yOffset = r.height() / 2;
    canvas
        .drawText(text, -xOffset + canvas.getWidth() / 2, yOffset + canvas.getHeight() / 2, paint);
  }

  private void setSolid(int color) {
    mPaint.setShader(null);
    mPaint.setColor(color);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int desiredWidth = (int) calculatedRadius * 2;
    int desiredHeight = (int) calculatedRadius * 2;

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int width;
    int height;

    if (radius >= 0) {
      //70dp exact
      switch (widthMode) {
        case MeasureSpec.EXACTLY:
          width = widthSize;
          break;
        case MeasureSpec.AT_MOST:
          //wrap content
          width = Math.min(desiredWidth, widthSize);
          break;
        case MeasureSpec.UNSPECIFIED:
          width = desiredWidth;
          break;
        default:
          width = desiredWidth;
          break;
      }

      //Measure Height
      switch (heightMode) {
        case MeasureSpec.EXACTLY:
          height = heightSize;
          break;
        case MeasureSpec.AT_MOST:
          height = Math.min(desiredHeight, heightSize);
          break;
        case MeasureSpec.UNSPECIFIED:
          height = desiredHeight;
          break;
        default:
          height = desiredHeight;
          break;
      }

    } else {
      calculatedRadius = Math.min(widthSize, heightSize) / 2f;
      float adjust = 0 * calculatedRadius;
      outerCircle.set(adjust, adjust, calculatedRadius * 2 - adjust, calculatedRadius * 2 - adjust);

      //adjust = .276f * radius;
      adjust = innerRadiusRatio * calculatedRadius;
      innerCircle.set(adjust, adjust, calculatedRadius * 2 - adjust, calculatedRadius * 2 - adjust);

      //70dp exact
      switch (widthMode) {
        case MeasureSpec.EXACTLY:
          width = widthSize;
          break;
        case MeasureSpec.AT_MOST:
          //wrap content
          width = Math.min(desiredWidth, widthSize);
          break;
        case MeasureSpec.UNSPECIFIED:
          width = widthSize;
          break;
        default:
          width = widthSize;
          break;
      }

      //Measure Height
      switch (heightMode) {
        case MeasureSpec.EXACTLY:
          height = heightSize;
          break;
        case MeasureSpec.AT_MOST:
          height = Math.min(desiredHeight, heightSize);
          break;
        case MeasureSpec.UNSPECIFIED:
          height = heightSize;
          break;
        default:
          height = heightSize;
          break;
      }
    }
    //MUST CALL THIS
    setMeasuredDimension(width, height);
  }
}
