package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.calltoclient.CallToClientNavigate;
import com.fasten.executor_driver.presentation.oderfulfillmentmenu.OrderFulfillmentMenuNavigate;
import com.fasten.executor_driver.view.CallToClientFragment;
import com.fasten.executor_driver.view.OrderFulfillmentActionsDialogFragment;

public class OrderFulfillmentActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_fulfillment);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
      toolbar.findViewById(R.id.orderActions).setOnClickListener(
          v -> {
            if (getSupportFragmentManager().findFragmentByTag("menu") == null) {
              new OrderFulfillmentActionsDialogFragment().show(getSupportFragmentManager(), "menu");
            }
          }
      );
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case OrderFulfillmentMenuNavigate.ORDER_ROUTE:
        startActivity(new Intent(this, OrderRouteActivity.class));
        break;
      case OrderFulfillmentMenuNavigate.ORDER_INFORMATION:
        startActivity(new Intent(this, OrderFulfillmentDetailsActivity.class));
        break;
      case OrderFulfillmentMenuNavigate.CALL_TO_CLIENT:
        fragment = getSupportFragmentManager().findFragmentByTag("callToClient");
        if (fragment == null) {
          getSupportFragmentManager().beginTransaction()
              .add(R.id.callingMessage, new CallToClientFragment(), "callToClient").commit();
        }
        break;
      case CallToClientNavigate.FINISHED:
        fragment = getSupportFragmentManager().findFragmentByTag("callToClient");
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        break;
      default:
        super.navigate(destination);
    }
  }
}
