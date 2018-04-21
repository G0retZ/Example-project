package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.balance.BalanceNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.view.BalanceFragment;

public class BalanceActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_balance);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("error")) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      BalanceFragment balanceFragment = (BalanceFragment) fragmentManager
          .findFragmentById(R.id.fragment_balance);
      if (balanceFragment != null) {
        balanceFragment.showError();
      }
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case BalanceNavigate.PAYMENT_OPTIONS:
        startActivity(new Intent(this, PaymentOptionsActivity.class));
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
