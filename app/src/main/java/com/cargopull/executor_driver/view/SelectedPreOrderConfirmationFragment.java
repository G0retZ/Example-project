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
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class SelectedPreOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private ShakeItPlayer shakeItPlayer;
  private RingTonePlayer ringTonePlayer;
  private Button setOutAction;
  private ProgressBar declineAction;
  private TextView declineActionText;
  @Nullable
  private ObjectAnimator declineDelayAnimator;
  @Nullable
  private ObjectAnimator declineResetAnimator;
  @Nullable
  private AlertDialog alertDialog;
  private Context context;

  @Inject
  public void setShakeItPlayer(@NonNull ShakeItPlayer shakeItPlayer) {
    this.shakeItPlayer = shakeItPlayer;
  }

  public void setRingTonePlayer(@NonNull RingTonePlayer ringTonePlayer) {
    this.ringTonePlayer = ringTonePlayer;
  }

  @Inject
  public void setOrderConfirmationViewModel(
      @NonNull OrderConfirmationViewModel orderConfirmationViewModel) {
    this.orderConfirmationViewModel = orderConfirmationViewModel;
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
    View view = inflater
        .inflate(R.layout.fragment_selected_pre_order_confirmation, container, false);
    setOutAction = view.findViewById(R.id.setOutButton);
    declineAction = view.findViewById(R.id.declineChart);
    declineActionText = view.findViewById(R.id.declineText);

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
        declineResetAnimator = ObjectAnimator
            .ofInt(declineAction, "progress", declineAction.getProgress(), 0);
        declineResetAnimator.setDuration(150);
        declineResetAnimator.setInterpolator(new LinearInterpolator());
        declineResetAnimator.start();
        if (!canceled) {
          orderConfirmationViewModel.declineOrder();
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
  public void onDetach() {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    if (declineResetAnimator != null) {
      declineResetAnimator.cancel();
    }
    if (declineDelayAnimator != null) {
      declineDelayAnimator.cancel();
    }
    super.onDetach();
    context = null;
  }

  @Override
  protected void navigate(@NonNull String destination) {
    if (destination.equals(CommonNavigate.NO_CONNECTION)) {
      if (alertDialog != null) {
        alertDialog.dismiss();
      }
      alertDialog = new Builder(context)
          .setMessage(getString(R.string.sms_network_error))
          .setPositiveButton(getString(android.R.string.ok), null)
          .create();
      alertDialog.show();
    }
    super.navigate(destination);
  }

  @Override
  public void showTimeout(int progress, long timeout) {
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void enableDeclineButton(boolean enable) {
    declineAction.setEnabled(enable);
    declineActionText.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    setOutAction.setEnabled(false);
  }

  @Override
  public void showAcceptedMessage(@Nullable String message) {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    if (message != null) {
      alertDialog = new Builder(context)
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderConfirmationViewModel.messageConsumed())
          .create();
      alertDialog.show();
    }
  }

  @Override
  public void showDeclinedMessage(@Nullable String message) {
    if (message != null) {
      ringTonePlayer.playRingTone(R.raw.decline_offer);
      orderConfirmationViewModel.messageConsumed();
    }
  }

  @Override
  public void showExpiredMessage(@Nullable String message) {
    if (alertDialog != null) {
      alertDialog.dismiss();
    }
    if (message != null) {
      alertDialog = new Builder(context)
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton(getString(android.R.string.ok),
              (a, b) -> orderConfirmationViewModel.messageConsumed())
          .create();
      alertDialog.show();
    }
  }
}
