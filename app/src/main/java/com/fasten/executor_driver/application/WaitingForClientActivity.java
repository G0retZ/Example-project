package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.cancelorder.CancelOrderNavigate;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.fasten.executor_driver.view.CallToClientFragment;
import com.fasten.executor_driver.view.CallToOperatorFragment;
import com.fasten.executor_driver.view.CancelOrderDialogFragment;

public class WaitingForClientActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_waiting_for_client);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
      toolbar.findViewById(R.id.cancelOrder).setOnClickListener(v -> {
        if (getSupportFragmentManager().findFragmentByTag("cancelOrder") == null) {
          new CancelOrderDialogFragment().show(getSupportFragmentManager(), "cancelOrder");
        }
        v.setEnabled(false);
        v.postDelayed(() -> v.setEnabled(true), 10_000);
      });
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case WaitingForClientNavigate.CALL_TO_CLIENT:
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_call_to_client);
        if (fragment != null && fragment instanceof CallToClientFragment) {
          ((CallToClientFragment) fragment).callToClient();
        }
        break;
      case CancelOrderNavigate.ORDER_CANCELED:
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_call_to_operator);
        if (fragment != null && fragment instanceof CallToOperatorFragment) {
          ((CallToOperatorFragment) fragment).callToOperator();
        }
        break;
      default:
        super.navigate(destination);
    }
  }
}
