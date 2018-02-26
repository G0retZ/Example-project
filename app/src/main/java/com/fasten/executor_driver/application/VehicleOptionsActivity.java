package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
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
    Intent intent;
    switch (destination) {
      case VehicleOptionsNavigate.READY_FOR_ORDERS:
        intent = new Intent(this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        break;
      case SelectedVehicleNavigate.VEHICLES:
        intent = new Intent(this, ChooseVehicleActivity.class);
        startActivity(intent);
        break;
    }
  }
}
