package com.fasten.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.menu.MenuNavigate;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchNavigate;

public class OnlineActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online);
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
      case OnlineSwitchNavigate.DRIVER_BLOCKED:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Тебя забанили!")
            .setPositiveButton(getString(android.R.string.ok), null)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show();
        break;
      case OnlineSwitchNavigate.VEHICLE_OPTIONS:
        startActivity(new Intent(this, SelectedVehicleOptionsActivity.class));
        break;
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
