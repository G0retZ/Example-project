package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.balance.BalanceNavigate;

public class BalanceActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_balance);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case BalanceNavigate.PAYMENT_OPTIONS:
        startActivity(new Intent(this, PaymentOptionsActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
