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
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewActions;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewActions;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModel;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewActions;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModel;
import java.text.DecimalFormat;
import javax.inject.Inject;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Отображает выполнение заказа.
 */

public class OrderFulfillmentFragment extends BaseFragment implements OrderCostViewActions,
    OrderTimeViewActions, NextRoutePointViewActions {

  private OrderCostViewModel orderCostViewModel;
  private OrderTimeViewModel orderTimeViewModel;
  private NextRoutePointViewModel nextRoutePointViewModel;
  private TextView totalTimeText;
  private TextView totalCostText;
  private TextView freeRideText;
  private TextView addressText;
  private TextView commentTitleText;
  private TextView commentText;
  private Button getDirectionsAction;
  private ProgressBar closeRoutePointAction;
  private TextView closeRoutePointText;
  private ProgressBar completeTheOrderAction;
  private TextView completeTheOrderText;
  private Context context;
  @Nullable
  private ObjectAnimator closeRoutePointDelayAnimator;
  @Nullable
  private ObjectAnimator closeRoutePointResetAnimator;
  @Nullable
  private ObjectAnimator completeTheOrderDelayAnimator;
  @Nullable
  private ObjectAnimator completeTheOrderResetAnimator;

  @Inject
  public void setOrderCostViewModel(OrderCostViewModel orderCostViewModel) {
    this.orderCostViewModel = orderCostViewModel;
  }

  @Inject
  public void setOrderTimeViewModel(OrderTimeViewModel orderTimeViewModel) {
    this.orderTimeViewModel = orderTimeViewModel;
  }

  @Inject
  public void setNextRoutePointViewModel(NextRoutePointViewModel nextRoutePointViewModel) {
    this.nextRoutePointViewModel = nextRoutePointViewModel;
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
    View view = inflater.inflate(R.layout.fragment_order_fulfillment, container, false);
    totalTimeText = view.findViewById(R.id.timeText);
    totalCostText = view.findViewById(R.id.costText);
    freeRideText = view.findViewById(R.id.freeRideText);
    addressText = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    getDirectionsAction = view.findViewById(R.id.openNavigator);
    closeRoutePointAction = view.findViewById(R.id.closeRoutePoint);
    closeRoutePointText = view.findViewById(R.id.closeRoutePointText);
    completeTheOrderAction = view.findViewById(R.id.completeTheOrder);
    completeTheOrderText = view.findViewById(R.id.completeTheOrderText);

    closeRoutePointDelayAnimator = ObjectAnimator.ofInt(closeRoutePointAction, "progress", 0, 100);
    closeRoutePointDelayAnimator.setDuration(1500);
    closeRoutePointDelayAnimator.setInterpolator(new DecelerateInterpolator());
    closeRoutePointDelayAnimator.addListener(new AnimatorListener() {
      private boolean canceled;

      @Override
      public void onAnimationStart(Animator animation) {
        canceled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled) {
          nextRoutePointViewModel.closeRoutePoint();
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

    closeRoutePointAction.setOnTouchListener((v, event) -> {
      int i = event.getAction();
      if (i == MotionEvent.ACTION_DOWN) {
        closeRoutePointDelayAnimator.start();
        if (closeRoutePointResetAnimator != null) {
          closeRoutePointResetAnimator.cancel();
        }
        return true;
      } else if (i == MotionEvent.ACTION_UP) {
        closeRoutePointDelayAnimator.cancel();
        closeRoutePointResetAnimator = ObjectAnimator
            .ofInt(closeRoutePointAction, "progress", closeRoutePointAction.getProgress(), 0);
        closeRoutePointResetAnimator.setDuration(150);
        closeRoutePointResetAnimator.setInterpolator(new LinearInterpolator());
        closeRoutePointResetAnimator.start();
        return true;
      }
      return false;
    });

    completeTheOrderDelayAnimator = ObjectAnimator
        .ofInt(completeTheOrderAction, "progress", 0, 100);
    completeTheOrderDelayAnimator.setDuration(1500);
    completeTheOrderDelayAnimator.setInterpolator(new DecelerateInterpolator());
    completeTheOrderDelayAnimator.addListener(new AnimatorListener() {
      private boolean canceled;

      @Override
      public void onAnimationStart(Animator animation) {
        canceled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled) {
          nextRoutePointViewModel.completeTheOrder();
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

    completeTheOrderAction.setOnTouchListener((v, event) -> {
      int i = event.getAction();
      if (i == MotionEvent.ACTION_DOWN) {
        completeTheOrderDelayAnimator.start();
        if (completeTheOrderResetAnimator != null) {
          completeTheOrderResetAnimator.cancel();
        }
        return true;
      } else if (i == MotionEvent.ACTION_UP) {
        completeTheOrderDelayAnimator.cancel();
        completeTheOrderResetAnimator = ObjectAnimator
            .ofInt(completeTheOrderAction, "progress", completeTheOrderAction.getProgress(), 0);
        completeTheOrderResetAnimator.setDuration(150);
        completeTheOrderResetAnimator.setInterpolator(new LinearInterpolator());
        completeTheOrderResetAnimator.start();
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
    orderCostViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    orderCostViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    orderTimeViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    orderTimeViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    nextRoutePointViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    nextRoutePointViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void onDetach() {
    if (closeRoutePointResetAnimator != null) {
      closeRoutePointResetAnimator.cancel();
    }
    if (closeRoutePointDelayAnimator != null) {
      closeRoutePointDelayAnimator.cancel();
    }
    if (completeTheOrderResetAnimator != null) {
      completeTheOrderResetAnimator.cancel();
    }
    if (completeTheOrderDelayAnimator != null) {
      completeTheOrderDelayAnimator.cancel();
    }
    super.onDetach();
    context = null;
  }

  @Override
  public void showNextRoutePointPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showNextRoutePoint(@NonNull String url) {

  }

  @Override
  public void showNextRoutePointAddress(@NonNull String coordinates, @NonNull String address) {
    addressText.setText(address);
    if (coordinates.trim().isEmpty()) {
      getDirectionsAction.setVisibility(View.GONE);
    } else {
      getDirectionsAction.setVisibility(View.VISIBLE);
      getDirectionsAction.setOnClickListener(v -> {
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
  }

  @Override
  public void showNextRoutePointComment(@NonNull String comment) {
    if (comment.trim().isEmpty()) {
      commentTitleText.setVisibility(View.GONE);
    } else {
      commentTitleText.setVisibility(View.VISIBLE);
    }
    commentText.setText(comment);
  }

  @Override
  public void showCloseNextRoutePointAction(boolean show) {
    closeRoutePointAction.setVisibility(show ? View.VISIBLE : View.GONE);
    closeRoutePointText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showCompleteOrderAction(boolean show) {
    completeTheOrderAction.setVisibility(show ? View.VISIBLE : View.GONE);
    completeTheOrderText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showNoRouteRide(boolean show) {
    freeRideText.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setOrderCostText(int currentCost) {
    if (!getResources().getBoolean(R.bool.show_cents)) {
      currentCost = Math.round(currentCost / 100f);
    }
    totalCostText.setText(
        new DecimalFormat(getString(R.string.currency_format)).format(currentCost)
    );
  }

  @Override
  public void setOrderTimeText(long currentSeconds) {
    totalTimeText.setText(
        DateTimeFormat.forPattern("HH:mm:ss").print(
            LocalTime.fromMillisOfDay(currentSeconds * 1000)
        )
    );
  }
}
