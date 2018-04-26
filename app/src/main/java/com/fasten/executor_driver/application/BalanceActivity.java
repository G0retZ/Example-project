package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.balance.BalanceNavigate;
import com.fasten.executor_driver.view.BalanceFragment;

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
      default:
        super.navigate(destination);
    }
  }
}
