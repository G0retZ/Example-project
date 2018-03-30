package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleNavigate;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsNavigate;

public class VehicleOptionsActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vehicle_options);
    setTitle(R.string.check_list);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        break;
      case VehicleOptionsNavigate.SERVICES:
        startActivity(new Intent(this, ServicesActivity.class));
        finish();
        break;
      case SelectedVehicleNavigate.VEHICLES:
        startActivity(new Intent(this, ChooseVehicleActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
