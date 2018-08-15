package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import javax.inject.Inject;

/**
 * Отображает ожидание клиента.
 */

public class OrderCostDetailsOrderDetailsFragment extends BaseFragment implements OrderViewActions {

  private OrderViewModel orderViewModel;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private TextView priceTitleText;
  private TextView priceText;

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater
        .inflate(R.layout.fragment_order_cost_details_order_details, container, false);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    priceTitleText = view.findViewById(R.id.priceTitleText);
    priceText = view.findViewById(R.id.priceText);
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
    orderViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    orderViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showOrderPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showLoadPoint(@NonNull String url) {

  }

  @Override
  public void showNextPointAddress(@NonNull String coordinates, @NonNull String address) {

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
  public void showTimeout(int timeout) {

  }

  @Override
  public void showTimeout(int progress, long timeout) {

  }

  @Override
  public void showFirstPointDistance(String distance) {

  }

  @Override
  public void showFirstPointEta(int etaTime) {

  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {
    if (priceText.trim().isEmpty()) {
      priceTitleText.setVisibility(View.GONE);
      this.priceText.setVisibility(View.GONE);
    } else {
      priceTitleText.setVisibility(View.VISIBLE);
      this.priceText.setVisibility(View.VISIBLE);
      this.priceText.setText(priceText);
    }
  }

  @Override
  public void showOrderConditions(@NonNull String routeDistance, int time, long cost) {

  }

  @Override
  public void showOrderOccupationTime(@NonNull String occupationTime) {

  }

  @Override
  public void showOrderOccupationDate(@NonNull String occupationDate) {

  }

  @Override
  public void showOrderOptionsRequirements(@NonNull String options) {
    if (options.trim().isEmpty()) {
      optionsTitleText.setVisibility(View.GONE);
      optionsText.setVisibility(View.GONE);
    } else {
      optionsTitleText.setVisibility(View.VISIBLE);
      optionsText.setVisibility(View.VISIBLE);
      optionsText.setText(options);
    }
  }

  @Override
  public void showComment(@NonNull String comment) {
    if (comment.trim().isEmpty()) {
      commentTitleText.setVisibility(View.GONE);
      commentText.setVisibility(View.GONE);
    } else {
      commentTitleText.setVisibility(View.VISIBLE);
      commentText.setVisibility(View.VISIBLE);
      commentText.setText(comment);
    }
  }
}
