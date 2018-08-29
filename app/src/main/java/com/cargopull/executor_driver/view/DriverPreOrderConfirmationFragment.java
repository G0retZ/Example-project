package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import com.cargopull.executor_driver.utils.Pair;
import java.text.DecimalFormat;
import java.util.Collections;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает заказ.
 */

public class DriverPreOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions, OrderViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private OrderViewModel orderViewModel;
  private ShakeItPlayer shakeItPlayer;
  private TextView scheduledTimeText;
  private TextView scheduledDateText;
  private TextView estimationText;
  private TextView addressText1;
  private TextView addressText2;
  private TextView positionText2;
  private TextView serviceText;
  private TextView cargoDescTitleText;
  private TextView cargoDescText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private Button declineAction;
  private ProgressBar acceptAction;
  private TextView acceptActionText;
  @Nullable
  private ObjectAnimator acceptDelayAnimator;
  @Nullable
  private ObjectAnimator acceptResetAnimator;
  @Nullable
  private AlertDialog confirmationDialog;
  private AlertDialog expirationDialog;
  private Context context;

  @Inject
  public void setShakeItPlayer(@NonNull ShakeItPlayer shakeItPlayer) {
    this.shakeItPlayer = shakeItPlayer;
  }

  @Inject
  public void setOrderConfirmationViewModel(
      @NonNull OrderConfirmationViewModel orderConfirmationViewModel) {
    this.orderConfirmationViewModel = orderConfirmationViewModel;
  }

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_driver_pre_order_confirmation, container, false);
    scheduledTimeText = view.findViewById(R.id.timeText);
    scheduledDateText = view.findViewById(R.id.dateText);
    addressText1 = view.findViewById(R.id.addressText1);
    addressText2 = view.findViewById(R.id.addressText2);
    positionText2 = view.findViewById(R.id.positionText2);
    estimationText = view.findViewById(R.id.estimationText);
    serviceText = view.findViewById(R.id.serviceText);
    cargoDescTitleText = view.findViewById(R.id.cargoDescTitleText);
    cargoDescText = view.findViewById(R.id.cargoDescText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    declineAction = view.findViewById(R.id.declineButton);
    acceptAction = view.findViewById(R.id.acceptChart);
    acceptActionText = view.findViewById(R.id.acceptText);
    declineAction.setOnClickListener(v -> orderConfirmationViewModel.declineOrder());

    acceptDelayAnimator = ObjectAnimator.ofInt(acceptAction, "progress", 0, 100);
    acceptDelayAnimator.setDuration(1500);
    acceptDelayAnimator.setInterpolator(new DecelerateInterpolator());
    acceptDelayAnimator.addListener(new AnimatorListener() {
      private boolean canceled;

      @Override
      public void onAnimationStart(Animator animation) {
        canceled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled) {
          orderConfirmationViewModel.acceptOrder();
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

    acceptAction.setOnTouchListener((v, event) -> {
      int i = event.getAction();
      if (i == MotionEvent.ACTION_DOWN) {
        acceptDelayAnimator.start();
        if (acceptResetAnimator != null) {
          acceptResetAnimator.cancel();
        }
        return true;
      } else if (i == MotionEvent.ACTION_UP) {
        acceptDelayAnimator.cancel();
        acceptResetAnimator = ObjectAnimator
            .ofInt(acceptAction, "progress", acceptAction.getProgress(), 0);
        acceptResetAnimator.setDuration(150);
        acceptResetAnimator.setInterpolator(new LinearInterpolator());
        acceptResetAnimator.start();
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
    orderConfirmationViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    if (confirmationDialog != null) {
      confirmationDialog.dismiss();
    }
    if (expirationDialog != null) {
      expirationDialog.dismiss();
    }
    if (acceptResetAnimator != null) {
      acceptResetAnimator.cancel();
    }
    if (acceptDelayAnimator != null) {
      acceptDelayAnimator.cancel();
    }
    super.onDetach();
    context = null;
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showOrderPending(boolean pending) {
    showPending(pending, toString() + "1");
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
  public void showOrderExpired(boolean show) {
    if (show) {
      if (confirmationDialog != null) {
        confirmationDialog.dismiss();
      }
      expirationDialog = new Builder(context)
          .setMessage(R.string.order_expired)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderViewModel.messageConsumed())
          .create();
      expirationDialog.show();
    } else {
      if (expirationDialog != null) {
        expirationDialog.dismiss();
      }
    }
  }

  @Override
  public void enableDeclineButton(boolean enable) {
    declineAction.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    acceptAction.setEnabled(enable);
    acceptActionText.setEnabled(enable);
  }

  @Override
  public void showBlockingMessage(@Nullable String message) {
    if (message != null) {
      if (expirationDialog != null) {
        expirationDialog.dismiss();
      }
      confirmationDialog = new Builder(context)
          .setMessage(message)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderConfirmationViewModel.messageConsumed())
          .create();
      confirmationDialog.show();
    } else {
      if (confirmationDialog != null) {
        confirmationDialog.dismiss();
      }
    }
  }
}
