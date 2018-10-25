package com.cargopull.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonNavigate;

public class MapActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case OnlineButtonNavigate.DRIVER_BLOCKED:
        new Builder(this)
            .setMessage(R.string.driver_blocked_message)
            .setPositiveButton(getString(android.R.string.ok), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.NO_FREE_VEHICLES:
        new Builder(this)
            .setMessage(R.string.no_free_vehicle_message)
            .setPositiveButton(getString(android.R.string.ok), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.NO_VEHICLES:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.no_vehicles_message)
            .setPositiveButton(getString(android.R.string.ok), null)
            .create()
            .show();
        break;
      case CommonNavigate.NO_CONNECTION:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.no_network_connection)
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineButtonNavigate.VEHICLE_OPTIONS:
        startActivity(new Intent(this, VehicleOptionsActivity.class));
        break;
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }

  @Override
  protected boolean showGeolocationStateAllowed() {
    return true;
  }
}
