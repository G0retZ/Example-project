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
import java.util.Collections;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class DriverPreOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions, OrderViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private OrderViewModel orderViewModel;
  private ShakeItPlayer shakeItPlayer;
  private ProgressBar declineAction;
  private TextView declineActionText;
  private ProgressBar setOutAction;
  private TextView setOutActionText;
  @Nullable
  private ObjectAnimator declineDelayAnimator;
  @Nullable
  private ObjectAnimator declineResetAnimator;
  @Nullable
  private ObjectAnimator animation;

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

  @SuppressLint("ClickableViewAccessibility")
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_driver_pre_order_confirmation, container, false);
    declineAction = view.findViewById(R.id.declineChart);
    declineActionText = view.findViewById(R.id.declineText);
    setOutAction = view.findViewById(R.id.setOutChart);
    setOutActionText = view.findViewById(R.id.setOutText);
    setOutAction.setOnClickListener(v -> orderConfirmationViewModel.acceptOrder());

    declineDelayAnimator = ObjectAnimator.ofInt(declineAction, "progress", 0, 100);
    declineDelayAnimator.setDuration(1500);
    declineDelayAnimator.setInterpolator(new DecelerateInterpolator());
    declineDelayAnimator.addListener(new AnimatorListener() {
      private boolean canceled;

      @Override
      public void onAnimationStart(Animator animation) {
        canceled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled) {
          orderConfirmationViewModel.declineOrder();
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

    declineAction.setOnTouchListener((v, event) -> {
      int i = event.getAction();
      if (i == MotionEvent.ACTION_DOWN) {
        declineDelayAnimator.start();
        if (declineResetAnimator != null) {
          declineResetAnimator.cancel();
        }
        return true;
      } else if (i == MotionEvent.ACTION_UP) {
        declineDelayAnimator.cancel();
        declineResetAnimator = ObjectAnimator
            .ofInt(declineAction, "progress", declineAction.getProgress(), 0);
        declineResetAnimator.setDuration(150);
        declineResetAnimator.setInterpolator(new LinearInterpolator());
        declineResetAnimator.start();
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
    if (declineResetAnimator != null) {
      declineResetAnimator.cancel();
    }
    if (declineDelayAnimator != null) {
      declineDelayAnimator.cancel();
    }
    super.onDetach();
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString());
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
  public void showTimeout(int progress, long timeout) {
    if (animation != null) {
      animation.cancel();
    }
    if (timeout > 0) {
      animation = ObjectAnimator.ofInt(setOutAction, "progress", progress, 0);
      animation.setDuration(timeout);
      animation.setInterpolator(new LinearInterpolator());
      animation.addListener(new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          orderConfirmationViewModel.counterTimeOut();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
      });
      animation.start();
    } else if (timeout == 0) {
      orderConfirmationViewModel.counterTimeOut();
    }
  }

  @Override
  public void showFirstPointDistance(String distance) {
  }

  @Override
  public void showFirstPointEta(int etaTime) {
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
  public void showEstimatedPrice(@NonNull String priceText) {
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
    declineActionText.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    setOutAction.setEnabled(enable);
    setOutActionText.setEnabled(enable);
  }

  @Override
  public void showBlockingMessage(@Nullable String message) {
  }
}
