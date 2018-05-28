package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;

public class OrderFulfillmentActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_fulfillment);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
      toolbar.findViewById(R.id.orderActions).setOnClickListener(
          v -> startActivity(new Intent(this, OrderRouteActivity.class))
      );
    }
  }
}
