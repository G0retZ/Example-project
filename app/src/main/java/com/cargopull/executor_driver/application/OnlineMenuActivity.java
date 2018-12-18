package com.cargopull.executor_driver.application;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.view.AboutDialogFragment;
import java.util.HashMap;
import javax.inject.Inject;

public class OnlineMenuActivity extends BaseActivity {

  private EventLogger eventLogger;
  private DialogFragment aboutFragment;

  @Inject
  public void setEventLogger(@NonNull EventLogger eventLogger) {
    this.eventLogger = eventLogger;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_online_menu);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
      View view = toolbar.findViewById(R.id.serverSettings);
      if (view != null) {
        view.setOnClickListener(v -> startActivity(new Intent(this, ServerSettingsActivity.class)));
      }
    }
    aboutFragment = new AboutDialogFragment();
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
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
        startActivity(new Intent(this, CurrentVehicleOptionsActivity.class));
        finish();
        break;
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        finish();
        break;
      case MenuNavigate.PRE_ORDERS:
        eventLogger.reportEvent("pre_orders_list_open", new HashMap<>());
        startActivity(new Intent(this, PreOrdersActivity.class));
        finish();
        break;
      case MenuNavigate.ORDERS_HISTORY:
        startActivity(new Intent(this, OrdersHistoryActivity.class));
        break;
      case MenuNavigate.NIGHT_MODE:
        startActivity(new Intent(this, NightModeActivity.class));
        break;
      case MenuNavigate.ABOUT:
        aboutFragment.show(getSupportFragmentManager(), "about");
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        eventLogger.reportEvent("online_menu_pre_order_notification", new HashMap<>());
        startActivity(new Intent(this, DriverPreOrderBookingActivity.class));
        finish();
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
