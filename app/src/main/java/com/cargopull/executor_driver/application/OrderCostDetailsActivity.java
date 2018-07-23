package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;

public class OrderCostDetailsActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_cost_details);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
      toolbar.findViewById(R.id.orderActions).setOnClickListener(v -> {
        if (getSupportFragmentManager().findFragmentByTag("menu") == null) {
          new OrderFulfillmentActionsDialogFragment().show(getSupportFragmentManager(), "menu");
        }
      });
    }
  }
}
