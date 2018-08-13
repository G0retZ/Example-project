package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderNavigate;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.cargopull.executor_driver.presentation.waitingforclientactions.WaitingForClientActionsNavigate;
import com.cargopull.executor_driver.view.CallToClientFragment;
import com.cargopull.executor_driver.view.CallToOperatorFragment;
import com.cargopull.executor_driver.view.CancelOrderDialogFragment;
import com.cargopull.executor_driver.view.WaitingForClientActionsDialogFragment;

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
      toolbar.findViewById(R.id.orderActions).setOnClickListener(v -> {
        if (getSupportFragmentManager().findFragmentByTag("menu") == null) {
          new WaitingForClientActionsDialogFragment().show(getSupportFragmentManager(), "menu");
        }
      });
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case WaitingForClientActionsNavigate.ORDER_ROUTE:
        startActivity(new Intent(this, WaitingForClientRouteActivity.class));
        break;
      case WaitingForClientActionsNavigate.REPORT_A_PROBLEM:
        if (getSupportFragmentManager().findFragmentByTag("reportAProblem") == null) {
          new CancelOrderDialogFragment().show(getSupportFragmentManager(), "reportAProblem");
        }
        break;
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