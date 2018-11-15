package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewActions;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModel;
import com.cargopull.executor_driver.presentation.movingtoclienttimer.MovingToClientTimerViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import javax.inject.Inject;

/**
 * Отображает движение к клиенту.
 */

public class MovingToClientFragment extends BaseFragment implements MovingToClientViewActions,
    OrderViewActions {

  private OrderViewModel orderViewModel;
  private MovingToClientTimerViewModel movingToClientTimerViewModel;
  private MovingToClientViewModel movingToClientViewModel;
  private ShakeItPlayer shakeItPlayer;
  private Button callAction;
  private Context context;
  @Nullable
  private ObjectAnimator delayAnimator;
  @Nullable
  private ObjectAnimator resetAnimator;


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setOrderViewModel(@NonNull OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
  }

  @Inject
  public void setMovingToClientTimerViewModel(
      @NonNull MovingToClientTimerViewModel movingToClientTimerViewModel) {
    this.movingToClientTimerViewModel = movingToClientTimerViewModel;
  }

  @Inject
  public void setMovingToClientViewModel(@NonNull MovingToClientViewModel movingToClientViewModel) {
    this.movingToClientViewModel = movingToClientViewModel;
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
    View view = inflater.inflate(R.layout.fragment_moving_to_client, container, false);
    callAction = view.findViewById(R.id.callToClient);
    ProgressBar arrivedAction = view.findViewById(R.id.reportArrived);
    callAction.setOnClickListener(v -> movingToClientViewModel.callToClient());
    delayAnimator = ObjectAnimator.ofInt(arrivedAction, "progress", 0, 100);
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
          movingToClientViewModel.reportArrival();
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

    arrivedAction.setOnTouchListener((v, event) -> {
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
            .ofInt(arrivedAction, "progress", arrivedAction.getProgress(), 0);
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
  public void setFormattedText(int id, int stringId, Object... formatArgs) {
    if (id == R.id.openNavigator) {
      View view = findViewById(id);
      if (view != null) {
        view.setOnClickListener(v -> {
          Intent navigationIntent = new Intent(Intent.ACTION_VIEW);
          navigationIntent.setData(Uri.parse(getString(stringId, formatArgs)));
          if (navigationIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(navigationIntent);
          } else {
            new Builder(context)
                .setTitle(R.string.error)
                .setMessage(R.string.install_geo_app)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create()
                .show();
          }
        });
      }
      return;
    }
    super.setFormattedText(id, stringId, formatArgs);
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
    movingToClientTimerViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    movingToClientViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    movingToClientViewModel.getNavigationLiveData().observe(this, destination -> {
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
    context = null;
  }

  @Override
  public void showMovingToClientPending(boolean pending) {
    showPending(pending, toString() + "0");
  }

  @Override
  public void enableMovingToClientCallButton(boolean enable) {
    callAction.setEnabled(enable);
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
