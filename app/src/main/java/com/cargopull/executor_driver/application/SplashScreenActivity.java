package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;

public class SplashScreenActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
  }
}
