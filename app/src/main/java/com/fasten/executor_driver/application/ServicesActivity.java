package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

public class ServicesActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_services);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        // никуда не переходим
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        // никуда не переходим
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
