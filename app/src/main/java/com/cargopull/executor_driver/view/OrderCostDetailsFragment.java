package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewActions;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewModel;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewActions;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel;
import com.cargopull.executor_driver.utils.Pair;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает детализацию расчета стоимости заказа.
 */

public class OrderCostDetailsFragment extends BaseFragment implements OrderCostDetailsViewActions,
    ConfirmOrderPaymentViewActions {

  private OrderCostDetailsViewModel orderCostDetailsViewModel;
  private ConfirmOrderPaymentViewModel confirmOrderPaymentViewModel;
  private ShakeItPlayer shakeItPlayer;
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
  private ProgressBar paidAction;
  @Nullable
  private ObjectAnimator delayAnimator;
  @Nullable
  private ObjectAnimator resetAnimator;

  @Inject
  public void setOrderCostDetailsViewModel(
      @NonNull OrderCostDetailsViewModel orderCostDetailsViewModel) {
    this.orderCostDetailsViewModel = orderCostDetailsViewModel;
  }

  @Inject
  public void setConfirmOrderPaymentViewModel(
      @NonNull ConfirmOrderPaymentViewModel confirmOrderPaymentViewModel) {
    this.confirmOrderPaymentViewModel = confirmOrderPaymentViewModel;
  }

  @Inject
  public void setShakeItPlayer(@NonNull ShakeItPlayer shakeItPlayer) {
    this.shakeItPlayer = shakeItPlayer;
  }

  @SuppressLint("ClickableViewAccessibility")
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
    delayAnimator = ObjectAnimator.ofInt(paidAction, "progress", 0, 100);
    delayAnimator.setDuration(1500);
    delayAnimator.setInterpolator(new DecelerateInterpolator());
    delayAnimator.addListener(new AnimatorListener() {
      private boolean canceled;

      @Override
      public void onAnimationStart(Animator animation) {
        canceled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled) {
          confirmOrderPaymentViewModel.confirmPayment();
          shakeItPlayer.shakeIt(Collections.singletonList(new Pair<>(200L, 255)));
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        canceled = true;
      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    paidAction.setOnTouchListener((v, event) -> {
      int i = event.getAction();
      if (i == MotionEvent.ACTION_DOWN) {
        delayAnimator.start();
        if (resetAnimator != null) {
          resetAnimator.cancel();
        }
        return true;
      } else if (i == MotionEvent.ACTION_UP) {
        delayAnimator.cancel();
        resetAnimator = ObjectAnimator
            .ofInt(paidAction, "progress", paidAction.getProgress(), 0);
        resetAnimator.setDuration(150);
        resetAnimator.setInterpolator(new LinearInterpolator());
        resetAnimator.start();
        return true;
      }
      return false;
    });
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
    confirmOrderPaymentViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    confirmOrderPaymentViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void onDetach() {
    if (resetAnimator != null) {
      resetAnimator.cancel();
    }
    if (delayAnimator != null) {
      delayAnimator.cancel();
    }
    super.onDetach();
  }

  @Override
  public void showOrderCostDetailsPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showOrderTotalCost(long totalCost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      totalCost = Math.round(totalCost / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    totalPaymentText.setText(decimalFormat.format(totalCost));
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
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    estimatedPackageCost.setText(decimalFormat.format(cost));
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
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    estimatedPackageServiceCost.setText(decimalFormat.format(cost));
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
      DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
      decimalFormat.setMaximumFractionDigits(0);
      ((TextView) view.findViewById(R.id.optionCostText)).setText(decimalFormat.format(cost));
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
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    overPackageCost.setText(decimalFormat.format(cost));
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
    DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    overPackageServiceCost.setText(decimalFormat.format(cost));
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
      DecimalFormat decimalFormat = new DecimalFormat(getString(R.string.currency_format));
      decimalFormat.setMaximumFractionDigits(0);
      ((TextView) view.findViewById(R.id.optionCostText)).setText(decimalFormat.format(cost));
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
    overPackageTariffCost.setText(
        new DecimalFormat(getString(R.string.currency_per_min_format)).format(tariff / 100d)
    );
  }

  @Override
  public void showOverPackageServiceTariff(long tariff) {
    overPackageTariffServiceCost.setText(
        new DecimalFormat(getString(R.string.currency_per_min_format)).format(tariff / 100d)
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
      ((TextView) view.findViewById(R.id.optionCostText)).setText(
          new DecimalFormat(getString(R.string.currency_per_min_format)).format(pair.second / 100d)
      );
      overPackageTariffOptionsCosts.addView(view);
    }
  }

  @Override
  public void ConfirmOrderPaymentPending(boolean pending) {
    showPending(pending, toString() + "1");
  }
}
