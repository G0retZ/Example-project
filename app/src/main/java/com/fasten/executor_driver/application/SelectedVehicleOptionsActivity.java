package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleNavigate;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsNavigate;

public class SelectedVehicleOptionsActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selected_vehicle_options);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
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
