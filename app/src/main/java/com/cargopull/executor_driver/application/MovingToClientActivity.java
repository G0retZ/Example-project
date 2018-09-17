package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderNavigate;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientNavigate;
import com.cargopull.executor_driver.presentation.movingtoclientactions.MovingToClientActionsNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.view.CallToClientFragment;
import com.cargopull.executor_driver.view.CallToOperatorFragment;
import com.cargopull.executor_driver.view.CancelOrderDialogFragment;
import com.cargopull.executor_driver.view.MovingToClientActionsDialogFragment;

public class MovingToClientActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_moving_to_client);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
      toolbar.findViewById(R.id.orderActions).setOnClickListener(v -> {
        if (getSupportFragmentManager().findFragmentByTag("menu") == null) {
          new MovingToClientActionsDialogFragment().show(getSupportFragmentManager(), "menu");
        }
      });
    }
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case MovingToClientActionsNavigate.ORDER_ROUTE:
        startActivity(new Intent(this, MovingToClientRouteActivity.class));
        break;
      case MovingToClientActionsNavigate.ORDER_INFORMATION:
        startActivity(new Intent(this, MovingToClientDetailsActivity.class));
        break;
      case MovingToClientActionsNavigate.REPORT_A_PROBLEM:
        if (getSupportFragmentManager().findFragmentByTag("reportAProblem") == null) {
          new CancelOrderDialogFragment().show(getSupportFragmentManager(), "reportAProblem");
        }
        break;
      case MovingToClientNavigate.CALL_TO_CLIENT:
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
      case PreOrderNavigate.ORDER_APPROVAL:
        startActivity(new Intent(this, DriverPreOrderBookingActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
