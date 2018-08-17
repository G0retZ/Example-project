package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationNavigate;

public class DriverPreOrderConfirmationActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_driver_pre_order_confirmation);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case OrderConfirmationNavigate.CLOSE:
        finish();
        break;
      default:
        super.navigate(destination);
        break;
    }
  }
}
