package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.paymentoptions.PaymentOptionsNavigate;

public class PaymentOptionsActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payment_options);
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
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        // никуда не переходим
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        // никуда не переходим
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        // никуда не переходим
        break;
      default:
        super.navigate(destination);
    }
  }
}
