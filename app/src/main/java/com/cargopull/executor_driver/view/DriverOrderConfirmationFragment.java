package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class DriverOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions, OrderViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private OrderViewModel orderViewModel;
  private ImageButton declineAction;
  private ProgressBar timeoutChart;
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
    timeoutChart = view.findViewById(R.id.timeoutChart);
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
    orderConfirmationViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString() + "0");
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
  public boolean isShowCents() {
    return getResources().getBoolean(R.bool.show_cents);
  }

  @Override
  @NonNull
  public String getCurrencyFormat() {
    return getString(R.string.currency_format);
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
  public void showFailedMessage(@Nullable String message) {
  }
}
