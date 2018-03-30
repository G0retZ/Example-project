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
      case ExecutorStateNavigate.NO_NETWORK:
        super.navigate(destination);
        break;
      default:
        navigateDelayed(destination);
    }
  }

  private void navigateDelayed(@NonNull String destination) {
    new Handler().postDelayed(() -> super.navigate(destination), 1500);
  }
}
