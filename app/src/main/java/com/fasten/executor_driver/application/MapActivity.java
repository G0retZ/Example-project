package com.fasten.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonNavigate;

public class MapActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case OnlineButtonNavigate.DRIVER_BLOCKED:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Тебя забанили!")
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.INSUFFICIENT_CREDITS:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Мало бабок на счету!")
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.NO_FREE_VEHICLES:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Нету тачки для тебя!")
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.NO_VEHICLES:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("У тебя вообще нет тачек!")
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.VEHICLE_OPTIONS:
        startActivity(new Intent(this, VehicleOptionsActivity.class));
        break;
      case OnlineButtonNavigate.VEHICLES:
        startActivity(new Intent(this, ChooseVehicleActivity.class));
        break;
    }
  }
}
