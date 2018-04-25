package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleNavigate;

public class ChooseVehicleActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_vehicle);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ChooseVehicleNavigate.VEHICLE_OPTIONS:
        onBackPressed();
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
