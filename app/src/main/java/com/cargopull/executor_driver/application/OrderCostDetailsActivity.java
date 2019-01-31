package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.ordercostdetailsactions.OrderCostDetailsActionsNavigate;
import com.cargopull.executor_driver.presentation.preorder.PreOrderNavigate;
import com.cargopull.executor_driver.presentation.reportproblem.ReportProblemNavigate;
import com.cargopull.executor_driver.view.CallToOperatorFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsActionsDialogFragment;
import com.cargopull.executor_driver.view.ReportProblemDialogFragment;
import java.util.HashMap;
import javax.inject.Inject;

public class OrderCostDetailsActivity extends BaseActivity {

  private EventLogger eventLogger;

  @Inject
  public void setEventLogger(@NonNull EventLogger eventLogger) {
    this.eventLogger = eventLogger;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_cost_details);
    Toolbar toolbar = findViewById(R.id.appBar);
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(
          v -> startActivity(new Intent(this, MenuActivity.class))
      );
      toolbar.findViewById(R.id.orderActions).setOnClickListener(v -> {
        if (getSupportFragmentManager().findFragmentByTag("menu") == null) {
          eventLogger.reportEvent("order_cost_details_actions", new HashMap<>());
          new OrderCostDetailsActionsDialogFragment().show(getSupportFragmentManager(), "menu");
        }
      });
    }
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    super.onDependencyInject(appComponent);
    appComponent.inject(this);
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case OrderCostDetailsActionsNavigate.ORDER_ROUTE:
        startActivity(new Intent(this, OrderCostDetailsRouteActivity.class));
        break;
      case OrderCostDetailsActionsNavigate.ORDER_INFORMATION:
        startActivity(new Intent(this, OrderCostDetailsOrderDetailsActivity.class));
        break;
      case OrderCostDetailsActionsNavigate.REPORT_A_PROBLEM:
        if (getSupportFragmentManager().findFragmentByTag("reportAProblem") == null) {
          new ReportProblemDialogFragment().show(getSupportFragmentManager(), "reportAProblem");
        }
        break;
      case ReportProblemNavigate.ORDER_CANCELED:
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_call_to_operator);
        if (fragment instanceof CallToOperatorFragment) {
          ((CallToOperatorFragment) fragment).callToOperator();
        }
        break;
      case PreOrderNavigate.ORDER_APPROVAL:
        eventLogger.reportEvent("order_cost_details_pre_order_notification", new HashMap<>());
        startActivity(new Intent(this, DriverPreOrderBookingActivity.class));
        break;
      default:
        super.navigate(destination);
    }
  }
}
