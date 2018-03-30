package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

public class ChooseVehicleActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_vehicle);
    setTitle(R.string.choose_vehicle);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        break;
      case ChooseVehicleNavigate.VEHICLE_OPTIONS:
        finish();
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
