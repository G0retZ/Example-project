package com.fasten.executor_driver.view;

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
import com.fasten.executor_driver.presentation.order.OrderViewActions;
import com.fasten.executor_driver.presentation.order.OrderViewModel;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientViewActions;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientViewModel;
import java.text.DecimalFormat;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает ожидание клиента.
 */

public class WaitingForClientFragment extends BaseFragment implements
    WaitingForClientViewActions, OrderViewActions {

  private WaitingForClientViewModel waitingForClientViewModel;
  private OrderViewModel orderViewModel;
  private TextView addressText1;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView estimationText;
  private TextView serviceText;
  private TextView cargoDescTitleText;
  private TextView cargoDescText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private boolean waitingForClientPending;
  private boolean orderPending;

  @Inject
  public void setWaitingForClientViewModel(
      @NonNull WaitingForClientViewModel waitingForClientViewModel) {
    this.waitingForClientViewModel = waitingForClientViewModel;
  }

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_waiting_for_client, container, false);
    addressText1 = view.findViewById(R.id.addressText1);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    estimationText = view.findViewById(R.id.estimationText);
    serviceText = view.findViewById(R.id.serviceText);
    cargoDescTitleText = view.findViewById(R.id.cargoDescTitleText);
    cargoDescText = view.findViewById(R.id.cargoDescText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    Button callToClient = view.findViewById(R.id.callToClient);
    Button startLoading = view.findViewById(R.id.startLoading);
    callToClient.setOnClickListener(v -> {
      navigate(WaitingForClientNavigate.CALL_TO_CLIENT);
      callToClient.setEnabled(false);
      callToClient.postDelayed(() -> callToClient.setEnabled(true), 10_000);
    });
    startLoading.setOnClickListener(v -> waitingForClientViewModel.startLoading());
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
    waitingForClientViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    waitingForClientViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showWaitingForClientPending(boolean pending) {
    if (this.waitingForClientPending != pending) {
      showPending(pending);
    }
    this.waitingForClientPending = pending;
  }

  @Override
  public void showOrderPending(boolean pending) {
    if (this.orderPending != pending) {
      showPending(pending);
    }
    this.orderPending = pending;
  }

  @Override
  public void showLoadPoint(@NonNull String url) {

  }

  @Override
  public void showNextPointAddress(@NonNull String coordinates, @NonNull String address) {
    addressText1.setText(address);
  }

  @Override
  public void showNextPointComment(@NonNull String comment) {
    if (comment.trim().isEmpty()) {
      commentTitleText.setVisibility(View.GONE);
      commentText.setVisibility(View.GONE);
    } else {
      commentTitleText.setVisibility(View.VISIBLE);
      commentText.setVisibility(View.VISIBLE);
      commentText.setText(comment);
    }
  }

  @Override
  public void showLastPointAddress(@NonNull String address) {

  }

  @Override
  public void showRoutePointsCount(int count) {

  }

  @Override
  public void showServiceName(@NonNull String serviceName) {
    serviceText.setText(serviceName);
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
  public void showTimeout(int progress, long timeout) {

  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {

  }

  @Override
  public void showOrderConditions(@NonNull String routeDistance, int time, int cost) {
    LocalTime localTime = LocalTime.fromMillisOfDay(time * 1000);
    estimationText.setText(getString(
        R.string.km_h_m_p, routeDistance,
        localTime.getHourOfDay(),
        localTime.getMinuteOfHour(),
        new DecimalFormat(getString(R.string.currency_format)).format(cost))
    );
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
      cargoDescTitleText.setVisibility(View.GONE);
      cargoDescText.setVisibility(View.GONE);
    } else {
      cargoDescTitleText.setVisibility(View.VISIBLE);
      cargoDescText.setVisibility(View.VISIBLE);
      cargoDescText.setText(comment);
    }
  }
}
