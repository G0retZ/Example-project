package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;

public class MapActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case "chooseVehicle":
        startActivity(new Intent(this, ChooseVehicleActivity.class));
        break;
    }
  }
}
