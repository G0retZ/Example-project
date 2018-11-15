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
import javax.inject.Inject;

/**
 * Отображает ожидание клиента.
 */

public class WaitingForClientFragment extends BaseFragment implements
    WaitingForClientViewActions, OrderViewActions {

  private WaitingForClientViewModel waitingForClientViewModel;
  private OrderViewModel orderViewModel;
  private ShakeItPlayer shakeItPlayer;
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
  public boolean isShowCents() {
    return getResources().getBoolean(R.bool.show_cents);
  }

  @Override
  @NonNull
  public String getCurrencyFormat() {
    return getString(R.string.currency_format);
  }
}
