package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;

public class SplashScreenActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
  }

  @Override
  void init() {
    new Handler().postDelayed(super::init, 1000);
  }
}
