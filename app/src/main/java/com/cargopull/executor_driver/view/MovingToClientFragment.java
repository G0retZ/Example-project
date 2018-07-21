package com.cargopull.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewActions;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModel;
import com.cargopull.executor_driver.presentation.order.OrderViewActions;
import com.cargopull.executor_driver.presentation.order.OrderViewModel;
import com.cargopull.executor_driver.utils.Pair;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import javax.inject.Inject;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Отображает движение к клиенту.
 */

public class MovingToClientFragment extends BaseFragment implements MovingToClientViewActions,
    OrderViewActions {

  private MovingToClientViewModel movingToClientViewModel;
  private OrderViewModel orderViewModel;
  @Nullable
  private ShakeItPlayer shakeItPlayer;
  private ImageView mapImage;
  private TextView addressText;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView timerText;
  private Button callAction;
  private Button navigationAction;
  private Context context;
  @Nullable
  private ValueAnimator timeoutAnimator;
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
  public void setOrderViewModel(OrderViewModel orderViewModel) {
    this.orderViewModel = orderViewModel;
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
    mapImage = view.findViewById(R.id.mapImage);
    addressText = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    timerText = view.findViewById(R.id.timerText);
    navigationAction = view.findViewById(R.id.openNavigator);
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
          if (shakeItPlayer != null) {
            shakeItPlayer.shakeIt(Collections.singletonList(new Pair<>(200L, 255)));
          }
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
    if (timeoutAnimator != null) {
      timeoutAnimator.cancel();
    }
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
  public void showOrderPending(boolean pending) {
    showPending(pending, toString() + "1");
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.with(context).load(url).into(mapImage);
  }

  @Override
  public void showNextPointAddress(@NonNull String coordinates, @NonNull String address) {
    addressText.setText(address);
    navigationAction.setOnClickListener(v -> {
      Intent navigationIntent = new Intent(Intent.ACTION_VIEW);
      navigationIntent.setData(Uri.parse("geo:" + coordinates + "?q=" + address
          + "(" + getString(R.string.client) + ")"));
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

  }

  @Override
  public void showTimeout(int timeout) {
    if (timeoutAnimator != null && timeoutAnimator.isStarted()) {
      timeoutAnimator.cancel();
    }
    int toTime = -7200;
    if (timeout < 0) {
      toTime = timeout + toTime;
    }
    timeoutAnimator = ValueAnimator.ofInt(timeout, toTime);
    timeoutAnimator.setDuration((timeout - toTime) * 1000);
    timeoutAnimator.setInterpolator(new LinearInterpolator());
    timeoutAnimator.addUpdateListener(animation -> {
      int time = (int) animation.getAnimatedValue();
      if (VERSION.SDK_INT >= VERSION_CODES.M) {
        timerText.setTextColor(
            getResources()
                .getColor(time < 0 ? R.color.colorError : android.R.color.primary_text_dark,
                    null)
        );
      } else {
        timerText.setTextColor(
            getResources()
                .getColor(time < 0 ? R.color.colorError : android.R.color.primary_text_dark)
        );
      }
      timerText.setText(
          DateTimeFormat.forPattern((time < 0 ? "-" : "") + "HH:mm:ss")
              .print(LocalTime.fromMillisOfDay(Math.abs(time) * 1000))
      );
    });
    timeoutAnimator.start();
  }

  @Override
  public void showTimeout(int progress, long timeout) {

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
  public void showOrderConditions(@NonNull String routeDistance, int time, int cost) {

  }

  @Override
  public void showOrderOptionsRequirements(@NonNull String options) {

  }

  @Override
  public void showComment(@NonNull String comment) {

  }
}
