package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.paymentoptions.PaymentOptionsNavigate;

public class PaymentOptionsActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payment_options);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case PaymentOptionsNavigate.QIWI:
        onBackPressed();
        break;
      case PaymentOptionsNavigate.SBERBANK_ONLINE:
        onBackPressed();
        break;
      default:
        super.navigate(destination);
    }
  }
}
