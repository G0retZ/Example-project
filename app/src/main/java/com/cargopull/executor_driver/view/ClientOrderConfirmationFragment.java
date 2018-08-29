package com.cargopull.executor_driver.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает подтверждение заказа.
 */

public class ClientOrderConfirmationFragment extends BaseFragment implements OrderViewActions {

  private OrderViewModel orderViewModel;
  private ImageView mapImage;
  private TextView distanceText;
  private TextView etaText;
  private TextView addressText1;
  private TextView addressText2;
  private TextView positionText2;
  private TextView estimationText;
  private TextView serviceText;
  private TextView cargoDescTitleText;
  private TextView cargoDescText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private Context context;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
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
    View view = inflater.inflate(R.layout.fragment_client_order_confirmation, container, false);
    mapImage = view.findViewById(R.id.mapImage);
    distanceText = view.findViewById(R.id.distanceText);
    etaText = view.findViewById(R.id.etaText);
    addressText1 = view.findViewById(R.id.addressText1);
    addressText2 = view.findViewById(R.id.addressText2);
    positionText2 = view.findViewById(R.id.positionText2);
    estimationText = view.findViewById(R.id.estimationText);
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
    super.onDetach();
    context = null;
  }

  @Override
  public void showOrderPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.get().load(url).into(mapImage);
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
  public void showTimeout(int progress, long timeout) {

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
      cargoDescTitleText.setVisibility(View.GONE);
      cargoDescText.setVisibility(View.GONE);
    } else {
      cargoDescTitleText.setVisibility(View.VISIBLE);
      cargoDescText.setVisibility(View.VISIBLE);
      cargoDescText.setText(comment);
    }
  }
}
