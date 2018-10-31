package com.cargopull.executor_driver.application;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.order.OrderNavigate;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationNavigate;

public class DriverPreOrderBookingActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_driver_pre_order_booking);
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
