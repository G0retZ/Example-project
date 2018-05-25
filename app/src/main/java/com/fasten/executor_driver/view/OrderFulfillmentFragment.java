package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.nextroutepoint.NextRoutePointViewActions;
import com.fasten.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.fasten.executor_driver.presentation.ordercost.OrderCostViewActions;
import com.fasten.executor_driver.presentation.ordercost.OrderCostViewModel;
import com.fasten.executor_driver.presentation.ordertime.OrderTimeViewActions;
import com.fasten.executor_driver.presentation.ordertime.OrderTimeViewModel;
import javax.inject.Inject;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Отображает выполнение заказа.
 */

public class OrderFulfillmentFragment extends BaseFragment implements OrderCostViewActions,
    OrderTimeViewActions, NextRoutePointViewActions {

  private OrderCostViewModel orderCostViewModel;
  private OrderTimeViewModel orderTimeViewModel;
  private NextRoutePointViewModel nextRoutePointViewModel;
  private TextView totalTimeText;
  private TextView totalCostText;
  private TextView freeRideText;
  private TextView addressText;
  private TextView commentTitleText;
  private TextView commentText;
  private Button getDirectionsAction;
  private Button closeRoutePointAction;
  private Button completeTheOrderAction;
  private Context context;
  private boolean pending;

  @Inject
  public void setOrderCostViewModel(OrderCostViewModel orderCostViewModel) {
    this.orderCostViewModel = orderCostViewModel;
  }

  @Inject
  public void setOrderTimeViewModel(OrderTimeViewModel orderTimeViewModel) {
    this.orderTimeViewModel = orderTimeViewModel;
  }

  @Inject
  public void setNextRoutePointViewModel(NextRoutePointViewModel nextRoutePointViewModel) {
    this.nextRoutePointViewModel = nextRoutePointViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_order_fulfillment, container, false);
    totalTimeText = view.findViewById(R.id.timeText);
    totalCostText = view.findViewById(R.id.costText);
    freeRideText = view.findViewById(R.id.freeRideText);
    addressText = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    getDirectionsAction = view.findViewById(R.id.openNavigator);
    closeRoutePointAction = view.findViewById(R.id.closeRoutePoint);
    closeRoutePointAction.setOnClickListener(v -> nextRoutePointViewModel.closeRoutePoint());
    completeTheOrderAction = view.findViewById(R.id.completeTheOrder);
    completeTheOrderAction.setOnClickListener(v -> nextRoutePointViewModel.completeTheOrder());
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    orderCostViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    orderTimeViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    nextRoutePointViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void showNextRoutePointPending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void showNextRoutePoint(@NonNull String url) {

  }

  @Override
  public void showNextRoutePointCoordinates(@NonNull String coordinates) {
    if (coordinates.trim().isEmpty()) {
      getDirectionsAction.setVisibility(View.GONE);
    } else {
      getDirectionsAction.setVisibility(View.VISIBLE);
      getDirectionsAction.setOnClickListener(v -> {
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW);
        navigationIntent.setData(Uri.parse("geo:" + coordinates + "?q=" + coordinates
            + "(" + getString(R.string.client) + ")"));
        if (navigationIntent.resolveActivity(context.getPackageManager()) != null) {
          startActivity(navigationIntent);
        } else {
          new Builder(context)
              .setTitle(R.string.error)
              .setMessage(R.string.install_geo_app)
              .setPositiveButton(getString(android.R.string.ok), null)
              .create()
              .show();
        }
      });
    }
  }

  @Override
  public void showNextRoutePointAddress(@NonNull String address) {
    addressText.setText(address);
  }

  @Override
  public void showNextRoutePointComment(@NonNull String comment) {
    if (comment.trim().isEmpty()) {
      commentTitleText.setVisibility(View.GONE);
    } else {
      commentTitleText.setVisibility(View.VISIBLE);
    }
    commentText.setText(comment);
  }

  @Override
  public void showCloseNextRoutePointAction(boolean show) {
    closeRoutePointAction.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showCompleteOrderAction(boolean show) {
    completeTheOrderAction.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showNoRouteRide(boolean show) {
    freeRideText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showNextRoutePointNetworkErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void setOrderCostText(int textId, int currentCost) {
    totalCostText.setText(getString(textId, currentCost));
  }

  @Override
  public void showOrderCostNetworkErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void setOrderTimeText(long currentSeconds) {
    totalTimeText.setText(
        DateTimeFormat.forPattern("HH:mm:ss").print(
            LocalTime.fromMillisOfDay(currentSeconds * 1000)
        )
    );
  }

  @Override
  public void showOrderTimeNetworkErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }
}
