package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.menu.MenuNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import java.util.HashMap;
import javax.inject.Inject;

public class MenuActivity extends BaseActivity {

  private EventLogger eventLogger;

  @Inject
  public void setEventLogger(@NonNull EventLogger eventLogger) {
    this.eventLogger = eventLogger;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case MenuNavigate.BALANCE:
        startActivity(new Intent(this, BalanceActivity.class));
        finish();
        break;
      case MenuNavigate.PRE_ORDERS:
        eventLogger.reportEvent("pre_orders_list_open", new HashMap<>());
        startActivity(new Intent(this, PreOrdersActivity.class));
        finish();
        break;
      case MenuNavigate.NIGHT_MODE:
        startActivity(new Intent(this, NightModeActivity.class));
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        eventLogger.reportEvent("menu_pre_order_notification", new HashMap<>());
        startActivity(new Intent(this, DriverPreOrderBookingActivity.class));
        finish();
        break;
      default:
        super.navigate(destination);
    }
  }
}
