package com.fasten.executor_driver.view;

import android.animation.ValueAnimator;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.movingtoclient.MovingToClientViewActions;
import com.fasten.executor_driver.presentation.movingtoclient.MovingToClientViewModel;
import com.fasten.executor_driver.presentation.order.OrderViewActions;
import com.fasten.executor_driver.presentation.order.OrderViewModel;
import com.squareup.picasso.Picasso;
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
  private ImageView mapImage;
  private TextView addressText;
  private TextView timerText;
  private Button callAction;
  private Button navigationAction;
  private Context context;
  private boolean movingToClientPending;
  private boolean orderPending;
  @Nullable
  private ValueAnimator valueAnimator;

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

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_moving_to_client, container, false);
    mapImage = view.findViewById(R.id.mapImage);
    addressText = view.findViewById(R.id.addressText);
    timerText = view.findViewById(R.id.timerText);
    navigationAction = view.findViewById(R.id.openNavigator);
    callAction = view.findViewById(R.id.callToClient);
    Button arrivedAction = view.findViewById(R.id.reportArrived);
    callAction.setOnClickListener(v -> movingToClientViewModel.callToClient());
    arrivedAction.setOnClickListener(v -> movingToClientViewModel.reportArrival());
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
    if (valueAnimator != null) {
      valueAnimator.cancel();
    }
    super.onDetach();
    context = null;
  }

  @Override
  public void showMovingToClientPending(boolean pending) {
    if (this.movingToClientPending != pending) {
      showPending(pending);
    }
    this.movingToClientPending = pending;
  }

  @Override
  public void enableMovingToClientCallButton(boolean enable) {
    callAction.setEnabled(enable);
  }

  @Override
  public void showOrderPending(boolean pending) {
    if (this.orderPending != pending) {
      showPending(pending);
    }
    this.orderPending = pending;
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.with(context).load(url).into(mapImage);
  }

  @Override
  public void showLoadPointAddress(@NonNull String coordinates, @NonNull String address) {
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
  public void showTimeout(int timeout) {
    if (valueAnimator != null && valueAnimator.isStarted()) {
      valueAnimator.cancel();
    }
    valueAnimator = ValueAnimator.ofInt(timeout, -7200);
    valueAnimator.setDuration(timeout * 1000 + 7200000);
    valueAnimator.setInterpolator(new LinearInterpolator());
    valueAnimator.addUpdateListener(animation -> {
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
    valueAnimator.start();
  }

  @Override
  public void showTimeout(int progress, long timeout) {

  }

  @Override
  public void showDistance(String distance) {

  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {

  }

  @Override
  public void showOrderOptionsRequirements(@NonNull String options) {

  }

  @Override
  public void showComment(@NonNull String comment) {

  }

  @Override
  public void showOrderServerDataError() {
    new Builder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.server_data_format_error)
        .setPositiveButton(getString(android.R.string.ok), null)
        .create()
        .show();
  }
}
