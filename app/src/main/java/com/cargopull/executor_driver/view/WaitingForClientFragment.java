package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewActions;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewModel;
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
  private ShakeItPlayer shakeItPlayer;
  private TextView addressText1;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView estimationText;
  private TextView serviceText;
  private TextView cargoDescTitleText;
  private TextView cargoDescText;
  private TextView optionsTitleText;
  private TextView optionsText;
  @Nullable
  private ObjectAnimator delayAnimator;
  @Nullable
  private ObjectAnimator resetAnimator;

  @Inject
  public void setWaitingForClientViewModel(
      @NonNull WaitingForClientViewModel waitingForClientViewModel) {
    this.waitingForClientViewModel = waitingForClientViewModel;
  }

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
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
    View view = inflater.inflate(R.layout.fragment_waiting_for_client, container, false);
    addressText1 = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    estimationText = view.findViewById(R.id.estimationText);
    serviceText = view.findViewById(R.id.serviceText);
    cargoDescTitleText = view.findViewById(R.id.cargoDescTitleText);
    cargoDescText = view.findViewById(R.id.cargoDescText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    Button callToClient = view.findViewById(R.id.callToClient);
    ProgressBar startLoading = view.findViewById(R.id.startLoading);
    callToClient.setOnClickListener(v -> {
      navigate(WaitingForClientNavigate.CALL_TO_CLIENT);
      callToClient.setEnabled(false);
      callToClient.postDelayed(() -> callToClient.setEnabled(true), 10_000);
    });
    delayAnimator = ObjectAnimator.ofInt(startLoading, "progress", 0, 100);
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
          waitingForClientViewModel.startLoading();
          shakeItPlayer.shakeIt(R.raw.single_shot_vibro);
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

    startLoading.setOnTouchListener((v, event) -> {
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
            .ofInt(startLoading, "progress", startLoading.getProgress(), 0);
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
  public void showWaitingForClientPending(boolean pending) {
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

  @Override
  public void showOrderExpiredMessage(@Nullable String message) {
  }

  @Override
  public void showOrderCancelledMessage(boolean show) {
  }
}
