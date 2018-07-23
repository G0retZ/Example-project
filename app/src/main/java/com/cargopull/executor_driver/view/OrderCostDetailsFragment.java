package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewActions;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.utils.Pair;
import java.text.DecimalFormat;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает детализацию расчета стоимости заказа.
 */

public class OrderCostDetailsFragment extends BaseFragment implements OrderCostDetailsViewActions {

  private OrderCostDetailsViewModel orderCostDetailsViewModel;
  private TextView totalPaymentText;
  private TextView estimatedPackageTitle;
  private TextView estimatedPackageCost;
  private TextView estimatedPackageTimeTitle;
  private TextView estimatedPackageTime;
  private TextView estimatedPackageDistanceTitle;
  private TextView estimatedPackageDistance;
  private TextView estimatedPackageServiceCostTitle;
  private TextView estimatedPackageServiceCost;
  private LinearLayout estimatedOptionsCosts;
  private TextView overPackageTitle;
  private TextView overPackageCost;
  private TextView overPackageTimeTitle;
  private TextView overPackageTime;
  private TextView overPackageServiceCostTitle;
  private TextView overPackageServiceCost;
  private LinearLayout overPackageOptionsCosts;
  private TextView overPackageTariffTitle;
  private TextView overPackageTariffCost;
  private TextView overPackageTariffServiceCostTitle;
  private TextView overPackageTariffServiceCost;
  private LinearLayout overPackageTariffOptionsCosts;
  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  private Button paidAction;

  @Inject
  public void setOrderCostDetailsViewModel(
      @NonNull OrderCostDetailsViewModel orderCostDetailsViewModel) {
    this.orderCostDetailsViewModel = orderCostDetailsViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_order_cost_details, container, false);
    totalPaymentText = view.findViewById(R.id.totalPaymentText);
    estimatedPackageTitle = view.findViewById(R.id.estimatedPackageTitle);
    estimatedPackageCost = view.findViewById(R.id.estimatedPackageCost);
    estimatedPackageTimeTitle = view.findViewById(R.id.estimatedPackageTimeTitle);
    estimatedPackageTime = view.findViewById(R.id.estimatedPackageTime);
    estimatedPackageDistanceTitle = view.findViewById(R.id.estimatedPackageDistanceTitle);
    estimatedPackageDistance = view.findViewById(R.id.estimatedPackageDistance);
    estimatedPackageServiceCostTitle = view.findViewById(R.id.estimatedPackageServiceCostTitle);
    estimatedPackageServiceCost = view.findViewById(R.id.estimatedPackageServiceCost);
    estimatedOptionsCosts = view.findViewById(R.id.estimatedOptionsCosts);
    overPackageTitle = view.findViewById(R.id.overPackageTitle);
    overPackageCost = view.findViewById(R.id.overPackageCost);
    overPackageTimeTitle = view.findViewById(R.id.overPackageTimeTitle);
    overPackageTime = view.findViewById(R.id.overPackageTime);
    overPackageServiceCostTitle = view.findViewById(R.id.overPackageServiceCostTitle);
    overPackageServiceCost = view.findViewById(R.id.overPackageServiceCost);
    overPackageOptionsCosts = view.findViewById(R.id.overPackageOptionsCosts);
    overPackageTariffTitle = view.findViewById(R.id.overPackageTariffTitle);
    overPackageTariffCost = view.findViewById(R.id.overPackageTariffCost);
    overPackageTariffServiceCostTitle = view.findViewById(R.id.overPackageTariffServiceCostTitle);
    overPackageTariffServiceCost = view.findViewById(R.id.overPackageTariffServiceCost);
    overPackageTariffOptionsCosts = view.findViewById(R.id.overPackageTariffOptionsCosts);
    paidAction = view.findViewById(R.id.orderPaid);
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
    orderCostDetailsViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    orderCostDetailsViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showOrderCostDetailsPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showOrderTotalCost(long totalCost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      totalCost = Math.round(totalCost / 100f);
    }
    totalPaymentText.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(totalCost)
    );
  }

  @Override
  public void showEstimatedOrderPackage(boolean show) {
    estimatedPackageTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageCost.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageTimeTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageTime.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageDistanceTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageDistance.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageServiceCostTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    estimatedPackageServiceCost.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showEstimatedOrderCost(long cost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      cost = Math.round(cost / 100f);
    }
    estimatedPackageCost.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(cost)
    );
  }

  @Override
  public void showEstimatedOrderTime(long time) {
    LocalTime localTime = LocalTime.fromMillisOfDay(time);
    estimatedPackageTime.setText(getString(R.string.h_m,
        localTime.getHourOfDay(), localTime.getMinuteOfHour()));
  }

  @Override
  public void showEstimatedOrderDistance(@Nullable String distance) {
    estimatedPackageDistance.setText(getString(R.string.km, distance));
  }

  @Override
  public void showEstimatedOrderServiceCost(long cost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      cost = Math.round(cost / 100f);
    }
    estimatedPackageServiceCost.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(cost)
    );
  }

  @Override
  public void showEstimatedOrderOptionsCosts(@NonNull List<Pair<String, Long>> optionsCosts) {
    estimatedOptionsCosts.removeAllViews();
    for (Pair<String, Long> pair : optionsCosts) {
      View view = getLayoutInflater().inflate(
          R.layout.fragment_order_cost_details_option_item, estimatedOptionsCosts, false);
      ((TextView) view.findViewById(R.id.optionNameText))
          .setText(getString(R.string.option_name, pair.first));
      long cost = pair.second;
      if (!getResources().getBoolean(R.bool.show_cents)) {
        cost = Math.round(cost / 100f);
      }
      ((TextView) view.findViewById(R.id.optionCostText)).setText(
          new DecimalFormat(getString(R.string.currency_format)).format(cost)
      );
      estimatedOptionsCosts.addView(view);
    }
  }

  @Override
  public void showOverPackage(boolean show) {
    overPackageTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageCost.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageTimeTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageTime.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageServiceCostTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageServiceCost.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showOverPackageCost(long cost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      cost = Math.round(cost / 100f);
    }
    overPackageCost.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(cost)
    );
  }

  @Override
  public void showOverPackageTime(long time) {
    LocalTime localTime = LocalTime.fromMillisOfDay(time);
    overPackageTime.setText(getString(R.string.h_m,
        localTime.getHourOfDay(), localTime.getMinuteOfHour()));
  }

  @Override
  public void showOverPackageServiceCost(long cost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      cost = Math.round(cost / 100f);
    }
    overPackageServiceCost.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(cost)
    );
  }

  @Override
  public void showOverPackageOptionsCosts(@NonNull List<Pair<String, Long>> optionsCosts) {
    overPackageOptionsCosts.removeAllViews();
    for (Pair<String, Long> pair : optionsCosts) {
      View view = getLayoutInflater().inflate(
          R.layout.fragment_order_cost_details_option_item, overPackageOptionsCosts, false);
      ((TextView) view.findViewById(R.id.optionNameText))
          .setText(getString(R.string.option_name, pair.first));
      long cost = pair.second;
      if (!getResources().getBoolean(R.bool.show_cents)) {
        cost = Math.round(cost / 100f);
      }
      ((TextView) view.findViewById(R.id.optionCostText)).setText(
          new DecimalFormat(getString(R.string.currency_format)).format(cost)
      );
      overPackageOptionsCosts.addView(view);
    }
  }

  @Override
  public void showOverPackageTariff(boolean show) {
    overPackageTariffTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageTariffCost.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageTariffServiceCostTitle.setVisibility(show ? View.VISIBLE : View.GONE);
    overPackageTariffServiceCost.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showOverPackageTariffCost(long tariff) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      tariff = Math.round(tariff / 100f);
    }
    overPackageTariffCost.setText(
        new DecimalFormat(getString(R.string.currency_per_min_format)).format(tariff)
    );
  }

  @Override
  public void showOverPackageServiceTariff(long tariff) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      tariff = Math.round(tariff / 100f);
    }
    overPackageTariffServiceCost.setText(
        new DecimalFormat(getString(R.string.currency_per_min_format)).format(tariff)
    );
  }

  @Override
  public void showOverPackageOptionsTariffs(@NonNull List<Pair<String, Long>> optionsTariffs) {
    overPackageTariffOptionsCosts.removeAllViews();
    for (Pair<String, Long> pair : optionsTariffs) {
      View view = getLayoutInflater().inflate(
          R.layout.fragment_order_cost_details_option_item, overPackageTariffOptionsCosts, false);
      ((TextView) view.findViewById(R.id.optionNameText))
          .setText(getString(R.string.option_name, pair.first));
      long cost = pair.second;
      if (!getResources().getBoolean(R.bool.show_cents)) {
        cost = Math.round(cost / 100f);
      }
      ((TextView) view.findViewById(R.id.optionCostText)).setText(
          new DecimalFormat(getString(R.string.currency_format)).format(cost)
      );
      overPackageTariffOptionsCosts.addView(view);
    }
  }
}
