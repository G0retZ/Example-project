package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.NextExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewActions;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.cargopull.executor_driver.presentation.oderfulfillmentactions.OrderFulfillmentActionsNavigate;
import java.util.HashMap;
import javax.inject.Inject;

/**
 * Отображает меню действий во время выполнения заказа.
 */

public class OrderFulfillmentActionsDialogFragment extends BaseDialogFragment implements
    NextRoutePointViewActions {

  private EventLogger eventLogger;
  private NextRoutePointViewModel nextRoutePointViewModel;
  private NextExecutorStateViewModel completeOrderViewModel;
  private View completeTheOrderAction;
  private Context context;

  @Inject
  public void setNextRoutePointViewModel(@NonNull NextRoutePointViewModel nextRoutePointViewModel) {
    this.nextRoutePointViewModel = nextRoutePointViewModel;
  }

  @Inject
  public void setCompleteOrderViewModel(
      @NonNull NextExecutorStateViewModel nextExecutorStateViewModel) {
    this.completeOrderViewModel = nextExecutorStateViewModel;
  }

  @Inject
  public void setEventLogger(@NonNull EventLogger eventLogger) {
    this.eventLogger = eventLogger;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_order_fulfillment_actions, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ((Toolbar) view.findViewById(R.id.appBar)).setNavigationOnClickListener(v -> dismiss());
    view.findViewById(R.id.orderRoute).setOnClickListener(
        v -> {
          eventLogger.reportEvent("order_fulfillment_action_route", new HashMap<>());
          navigate(OrderFulfillmentActionsNavigate.ORDER_ROUTE);
        }
    );
    view.findViewById(R.id.orderDetails).setOnClickListener(
        v -> {
          eventLogger.reportEvent("order_fulfillment_action_order_info", new HashMap<>());
          navigate(OrderFulfillmentActionsNavigate.ORDER_INFORMATION);
        }
    );
    view.findViewById(R.id.callToClient).setOnClickListener(
        v -> {
          eventLogger.reportEvent("order_fulfillment_action_call", new HashMap<>());
          navigate(OrderFulfillmentActionsNavigate.CALL_TO_CLIENT);
        }
    );
    view.findViewById(R.id.reportAProblem).setOnClickListener(
        v -> {
          eventLogger.reportEvent("order_fulfillment_action_problems", new HashMap<>());
          navigate(OrderFulfillmentActionsNavigate.REPORT_A_PROBLEM);
        }
    );
    completeTheOrderAction = view.findViewById(R.id.completeTheOrder);
    completeTheOrderAction.setOnClickListener(
        v -> {
          eventLogger.reportEvent("order_fulfillment_action_incomplete", new HashMap<>());
          new Builder(context)
              .setMessage(R.string.order_complete_question)
              .setPositiveButton(
                  getString(android.R.string.ok),
                  ((dialog, which) -> {
                    eventLogger.reportEvent("order_fulfillment_action_incomplete_completed",
                        new HashMap<>());
                    completeOrderViewModel.routeToNextState();
                  })
              ).setNegativeButton(getString(android.R.string.cancel), null)
              .create()
              .show();
        }
    );
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    nextRoutePointViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    nextRoutePointViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  void navigate(@NonNull String destination) {
    dismiss();
    super.navigate(destination);
  }

  @Override
  public void showNextRoutePointPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showNextRoutePoint(@NonNull String url) {
  }

  @Override
  public void showNextRoutePointAddress(@NonNull String coordinates, @NonNull String address) {
  }

  @Override
  public void showNextRoutePointComment(@NonNull String comment) {
  }

  @Override
  public void showCloseNextRoutePointAction(boolean show) {
  }

  @Override
  public void showCompleteOrderAction(boolean show) {
    completeTheOrderAction.setVisibility(!show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showNoRouteRide(boolean show) {
  }
}
