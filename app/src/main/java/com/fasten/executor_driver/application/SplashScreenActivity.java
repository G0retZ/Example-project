package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

public class SplashScreenActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.AUTHORIZE:
        navigateDelayed(destination);
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        navigateDelayed(destination);
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        navigateDelayed(destination);
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        navigateDelayed(destination);
        break;
      default:
        super.navigate(destination);
    }
  }

  private void navigateDelayed(@NonNull String destination) {
    new Handler().postDelayed(() -> super.navigate(destination), 1500);
  }
}
