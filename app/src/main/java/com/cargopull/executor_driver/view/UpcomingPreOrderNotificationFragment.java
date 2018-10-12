package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewActions;
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModel;
import javax.inject.Inject;

/**
 * Отображает оповещение о предстоящем предварительном заказе.
 */

public class UpcomingPreOrderNotificationFragment extends BaseFragment implements
    UpcomingPreOrderViewActions, OrderViewActions {

  private OrderViewModel upcomingPreOrderViewModel;
  private UpcomingPreOrderViewModel upcomingPreOrderNotificationViewModel;
  private View rootView;
  private TextView addressText;
  private TextView occupationTimeText;

  @Inject
  public void setUpcomingPreOrderViewModel(@NonNull OrderViewModel upcomingPreOrderViewModel) {
    this.upcomingPreOrderViewModel = upcomingPreOrderViewModel;
  }

  @Inject
  public void setUpcomingPreOrderNotificationViewModel(
      @NonNull UpcomingPreOrderViewModel upcomingPreOrderNotificationViewModel) {
    this.upcomingPreOrderNotificationViewModel = upcomingPreOrderNotificationViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_upcoming_pre_order, container, false);
    rootView
        .setOnClickListener(v -> upcomingPreOrderNotificationViewModel.upcomingPreOrderConsumed());
    addressText = rootView.findViewById(R.id.addressText);
    occupationTimeText = rootView.findViewById(R.id.occupationTime);
    return rootView;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    upcomingPreOrderViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    upcomingPreOrderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    upcomingPreOrderNotificationViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    upcomingPreOrderNotificationViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void showUpcomingPreOrderAvailable(boolean show) {
    rootView.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showOrderPending(boolean pending) {
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
  }

  @Override
  public void showTimeout(int timeout) {
  }

  @Override
  public void showFirstPointDistance(String distance) {
  }

  @Override
  public void showFirstPointEta(int etaTime) {
  }

  @Override
  public void showNextPointAddress(@NonNull String coordinates, @NonNull String address) {
    addressText.setText(address);
  }

  @Override
  public void showNextPointComment(@NonNull String comment) {
  }

  @Override
  public void showLastPointAddress(@NonNull String address) {
  }

  @Override
  public void showRoutePointsCount(int count) {
  }

  @Override
  public void showServiceName(@NonNull String serviceName) {
  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {
  }

  @Override
  public void showOrderConditions(@NonNull String routeDistance, int time, long cost) {
  }

  @Override
  public void showOrderOccupationTime(@NonNull String occupationTime) {
    occupationTimeText.setText(occupationTime);
  }

  @Override
  public void showOrderOccupationDate(@NonNull String occupationDate) {
  }

  @Override
  public void showOrderOptionsRequirements(@NonNull String options) {
  }

  @Override
  public void showComment(@NonNull String comment) {
  }

  @Override
  public void showOrderExpiredMessage(@Nullable String message) {
  }

  @Override
  public void showOrderCancelledMessage(boolean show) {
  }
}
