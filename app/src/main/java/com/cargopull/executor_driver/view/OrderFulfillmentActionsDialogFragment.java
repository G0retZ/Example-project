package com.cargopull.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewActions;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.cargopull.executor_driver.presentation.oderfulfillmentactions.OrderFulfillmentActionsNavigate;
import javax.inject.Inject;

/**
 * Отображает меню действий во время выполнения заказа.
 */

public class OrderFulfillmentActionsDialogFragment extends BaseDialogFragment implements
    NextRoutePointViewActions {

  private NextRoutePointViewModel nextRoutePointViewModel;
  private View completeTheOrderAction;
  private Context context;

  @Inject
  public void setNextRoutePointViewModel(@NonNull NextRoutePointViewModel nextRoutePointViewModel) {
    this.nextRoutePointViewModel = nextRoutePointViewModel;
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
    Window window = getDialog().getWindow();
    if (window != null) {
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorWindowBackground, null)));
      } else {
        window.setBackgroundDrawable(
            new ColorDrawable(getResources().getColor(R.color.colorWindowBackground))
        );
      }
    }
    ((Toolbar) view.findViewById(R.id.appBar)).setNavigationOnClickListener(v -> dismiss());
    view.findViewById(R.id.orderRoute).setOnClickListener(
        v -> navigate(OrderFulfillmentActionsNavigate.ORDER_ROUTE)
    );
    view.findViewById(R.id.orderDetails).setOnClickListener(
        v -> navigate(OrderFulfillmentActionsNavigate.ORDER_INFORMATION)
    );
    view.findViewById(R.id.callToClient).setOnClickListener(
        v -> navigate(OrderFulfillmentActionsNavigate.CALL_TO_CLIENT)
    );
    view.findViewById(R.id.reportAProblem).setOnClickListener(
        v -> navigate(OrderFulfillmentActionsNavigate.REPORT_A_PROBLEM)
    );
    completeTheOrderAction = view.findViewById(R.id.completeTheOrder);
    completeTheOrderAction.setOnClickListener(
        v -> new Builder(context)
            .setMessage(R.string.order_complete_question)
            .setPositiveButton(
                getString(android.R.string.ok),
                ((dialog, which) -> nextRoutePointViewModel.completeTheOrder())
            ).setNegativeButton(getString(android.R.string.cancel), null)
            .create()
            .show()

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