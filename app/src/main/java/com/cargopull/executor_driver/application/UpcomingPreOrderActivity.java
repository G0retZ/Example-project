package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.order.OrderNavigate;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationNavigate;

public class UpcomingPreOrderActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selected_pre_order);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case OrderConfirmationNavigate.CLOSE:
        finish();
        break;
      case OrderNavigate.CLOSE:
        finish();
        break;
      default:
        super.navigate(destination);
    }
  }
}
