package com.fasten.executor_driver.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
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
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.driverorderconfirmation.DriverOrderConfirmationViewActions;
import com.fasten.executor_driver.presentation.driverorderconfirmation.DriverOrderConfirmationViewModel;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class DriverOrderConfirmationFragment extends BaseFragment implements
    DriverOrderConfirmationViewActions {

  private DriverOrderConfirmationViewModel driverOrderConfirmationViewModel;
  private ImageButton declineAction;
  private ImageView mapImage;
  private ProgressBar timeoutChart;
  private TextView distanceText;
  private TextView addressText;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private TextView priceTitleText;
  private TextView priceText;
  private Button acceptAction;
  private Context context;
  private boolean pending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setDriverOrderConfirmationViewModel(
      @NonNull DriverOrderConfirmationViewModel driverOrderConfirmationViewModel) {
    this.driverOrderConfirmationViewModel = driverOrderConfirmationViewModel;
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
    addressText = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    priceTitleText = view.findViewById(R.id.priceTitleText);
    priceText = view.findViewById(R.id.priceText);
    acceptAction = view.findViewById(R.id.acceptButton);
    acceptAction.setOnClickListener(v -> driverOrderConfirmationViewModel.acceptOrder());
    declineAction.setOnClickListener(v -> driverOrderConfirmationViewModel.declineOrder());
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
    driverOrderConfirmationViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    if (this.pending != pending) {
      showPending(pending);
    }
    this.pending = pending;
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.with(context).load(url)
        .into(mapImage);
  }

  @Override
  public void showDistance(String distance) {
    distanceText.setText(getString(R.string.km, distance));
  }

  @Override
  public void showTimeout(int progress, long timeout) {
    if (timeout > 0) {
      ObjectAnimator animation = ObjectAnimator.ofInt(timeoutChart, "progress", progress, 0);
      animation.setDuration(timeout);
      animation.setInterpolator(new LinearInterpolator());
      animation.addListener(new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          driverOrderConfirmationViewModel.counterTimeOut();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
      });
      animation.start();
    } else {
      driverOrderConfirmationViewModel.counterTimeOut();
    }
  }

  @Override
  public void showLoadPointAddress(@NonNull String address) {
    addressText.setText(address);
  }

  @Override
  public void showEstimatedPrice(@NonNull String priceText) {
    if (priceText.trim().isEmpty()) {
      priceTitleText.setVisibility(View.GONE);
      this.priceText.setVisibility(View.GONE);
    } else {
      priceTitleText.setVisibility(View.VISIBLE);
      this.priceText.setVisibility(View.VISIBLE);
      this.priceText.setText(priceText);
    }
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
      commentTitleText.setVisibility(View.GONE);
      commentText.setVisibility(View.GONE);
    } else {
      commentTitleText.setVisibility(View.VISIBLE);
      commentText.setVisibility(View.VISIBLE);
      commentText.setText(comment);
    }
  }

  @Override
  public void showOrderAvailabilityError(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.order_unavailable_for_accept)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void showNetworkErrorMessage(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage(R.string.no_network_connection)
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void enableDeclineButton(boolean enable) {
    declineAction.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    acceptAction.setEnabled(enable);
  }
}
