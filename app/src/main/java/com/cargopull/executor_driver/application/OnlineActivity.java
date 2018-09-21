package com.cargopull.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderNavigate;

public class OnlineActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, OnlineMenuActivity.class))
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
      case OnlineButtonNavigate.VEHICLE_OPTIONS:
        startActivity(new Intent(this, SelectedVehicleOptionsActivity.class));
        break;
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        startActivity(new Intent(this, DriverPreOrderBookingActivity.class));
        break;
      case UpcomingPreOrderNavigate.UPCOMING_PRE_ORDER:
        startActivity(new Intent(this, UpcomingPreOrderActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
