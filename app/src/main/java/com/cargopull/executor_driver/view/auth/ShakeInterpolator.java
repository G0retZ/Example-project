package com.cargopull.executor_driver.view.auth;

import android.view.animation.Interpolator;

public class ShakeInterpolator implements Interpolator {

  @Override
  public float getInterpolation(float input) {
    return (float)(Math.sin(input * Math.PI * 6) / 2.0f) * (1 - input) + input;
  }
}
