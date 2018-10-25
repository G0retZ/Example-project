package com.cargopull.executor_driver.application;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
    if (!BalanceNavigate.PAYMENT_OPTIONS.equals(destination)) {
      super.navigate(destination);
    }
  }

  @Override
  protected boolean showGeolocationStateAllowed() {
    return true;
  }
}
