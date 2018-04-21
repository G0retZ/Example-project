package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchNavigate;

public class OnlineActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case NAVIGATION_UP:
        startActivity(new Intent(this, MenuActivity.class));
        break;
      case OnlineSwitchNavigate.SERVICES:
        startActivity(new Intent(this, ServicesActivity.class));
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        // никуда не переходим
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        // никуда не переходим
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
