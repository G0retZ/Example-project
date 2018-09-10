package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import javax.inject.Inject;
import org.joda.time.LocalTime;

/**
 * Отображает заказ.
 */

public class DriverOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions, OrderViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private OrderViewModel orderViewModel;
  private ImageButton declineAction;
  private ImageView mapImage;
  private ProgressBar timeoutChart;
  private TextView distanceText;
  private TextView etaText;
  private TextView addressText1;
  private TextView addressText2;
  private TextView positionText2;
  private TextView estimationText;
  private TextView serviceText;
  private Button acceptAction;
  @Nullable
  private ObjectAnimator timeoutAnimation;

  @Inject
  public void setOrderConfirmationViewModel(
      @NonNull OrderConfirmationViewModel orderConfirmationViewModel) {
    this.orderConfirmationViewModel = orderConfirmationViewModel;
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
    View view = inflater.inflate(R.layout.fragment_driver_order_confirmation, container, false);
    declineAction = view.findViewById(R.id.declineButton);
    mapImage = view.findViewById(R.id.mapImage);
    timeoutChart = view.findViewById(R.id.timeoutChart);
    distanceText = view.findViewById(R.id.distanceText);
    etaText = view.findViewById(R.id.etaText);
    addressText1 = view.findViewById(R.id.addressText1);
    addressText2 = view.findViewById(R.id.addressText2);
    positionText2 = view.findViewById(R.id.positionText2);
    estimationText = view.findViewById(R.id.estimationText);
    serviceText = view.findViewById(R.id.serviceText);
    acceptAction = view.findViewById(R.id.acceptButton);
    acceptAction.setOnClickListener(v -> orderConfirmationViewModel.acceptOrder());
    declineAction.setOnClickListener(v -> orderConfirmationViewModel.declineOrder());
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
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void showOrderPending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.get().load(url).into(mapImage);
  }

  @Override
  public void showTimeout(int timeout) {
  }

  @Override
  public void showTimeout(int progress, long timeout) {
    if (timeoutAnimation != null) {
      timeoutAnimation.cancel();
    }
    if (timeout > 0) {
      timeoutAnimation = ObjectAnimator.ofInt(timeoutChart, "progress", progress, 0);
      timeoutAnimation.setDuration(timeout);
      timeoutAnimation.setInterpolator(new LinearInterpolator());
      timeoutAnimation.addListener(new AnimatorListener() {
        private boolean canceled;

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          if (!canceled) {
            orderConfirmationViewModel.counterTimeOut();
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
      timeoutAnimation.start();
    } else if (timeout == 0) {
      orderConfirmationViewModel.counterTimeOut();
    }
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
  }

  @Override
  public void showComment(@NonNull String comment) {
  }

  @Override
  public void showOrderExpiredMessage(@Nullable String message) {
  }

  @Override
  public void enableDeclineButton(boolean enable) {
    declineAction.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    acceptAction.setEnabled(enable);
  }

  @Override
  public void showAcceptedMessage(@Nullable String message) {
  }

  @Override
  public void showDeclinedMessage(@Nullable String message) {
  }

  @Override
  public void showExpiredMessage(@Nullable String message) {
  }
}
