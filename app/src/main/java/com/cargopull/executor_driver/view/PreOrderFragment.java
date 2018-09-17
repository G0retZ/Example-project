package com.cargopull.executor_driver.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
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
import java.text.DecimalFormat;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает предварительный заказ.
 */

public class PreOrderFragment extends BaseFragment implements OrderViewActions {

  private OrderViewModel orderViewModel;
  private TextView scheduledTimeText;
  private TextView scheduledDateText;
  private TextView estimationText;
  private TextView distanceText;
  private TextView etaText;
  private TextView addressText1;
  private TextView addressText2;
  private TextView positionText2;
  private TextView serviceText;
  private TextView cargoDescTitleText;
  private TextView cargoDescText;
  private TextView optionsTitleText;
  private TextView optionsText;
  @Nullable
  private AlertDialog alertDialog;
  private Context context;

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
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
    View view = inflater.inflate(R.layout.fragment_pre_order, container, false);
    scheduledTimeText = view.findViewById(R.id.timeText);
    scheduledDateText = view.findViewById(R.id.dateText);
    addressText1 = view.findViewById(R.id.addressText1);
    addressText2 = view.findViewById(R.id.addressText2);
    positionText2 = view.findViewById(R.id.positionText2);
    estimationText = view.findViewById(R.id.estimationText);
    distanceText = view.findViewById(R.id.distanceText);
    etaText = view.findViewById(R.id.etaText);
    serviceText = view.findViewById(R.id.serviceText);
    cargoDescTitleText = view.findViewById(R.id.cargoDescTitleText);
    cargoDescText = view.findViewById(R.id.cargoDescText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
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
  public void onDetach() {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    super.onDetach();
    context = null;
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
    addressText1.setText(address);
  }

  @Override
  public void showNextPointComment(@NonNull String comment) {
  }

  @Override
  public void showLastPointAddress(@NonNull String address) {
    addressText2.setText(address.isEmpty() ? getString(R.string.free_ride) : address);
  }

  @Override
  public void showRoutePointsCount(int count) {
    positionText2.setText(String.valueOf(count < 2 ? 2 : count));
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
    distanceText.setText(getString(R.string.km, distance));
  }

  @Override
  public void showFirstPointEta(int etaTime) {
    etaText.setText(getString(R.string.eta, Math.round(etaTime / 60F)));
  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {
  }

  @Override
  public void showOrderConditions(@NonNull String routeDistance, int time, long cost) {
    LocalTime localTime = LocalTime.fromMillisOfDay(time * 1000);
    if (!getResources().getBoolean(R.bool.show_cents)) {
      cost = Math.round(cost / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    estimationText.setText(getString(
        R.string.km_h_m_p, routeDistance,
        localTime.getHourOfDay(),
        localTime.getMinuteOfHour(),
        decimalFormat.format(cost))
    );
  }

  @Override
  public void showOrderOccupationTime(@NonNull String occupationTime) {
    scheduledTimeText.setText(occupationTime);
  }

  @Override
  public void showOrderOccupationDate(@NonNull String occupationDate) {
    scheduledDateText.setText(occupationDate);
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

  @Override
  public void showOrderExpiredMessage(@Nullable String message) {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    if (message != null) {
      alertDialog = new Builder(context)
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderViewModel.messageConsumed())
          .create();
      alertDialog.show();
    }
  }

  @Override
  public void showOrderCancelledMessage(boolean show) {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    if (show) {
      alertDialog = new Builder(context)
          .setMessage(R.string.order_cancelled)
          .setCancelable(false)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderViewModel.messageConsumed())
          .create();
      alertDialog.show();
    }
  }
}
