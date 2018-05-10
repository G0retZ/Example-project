package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.fasten.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

/**
 * Отображает подтверждение заказа.
 */

public class OrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private ImageButton declineAction;
  private ImageView mapImage;
  private TextView distanceText;
  private TextView addressText;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private TextView priceTitleText;
  private TextView priceText;
  private Context context;
  private boolean pending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @SuppressWarnings("unused")
  @Inject
  public void setOrderConfirmationViewModel(
      @NonNull OrderConfirmationViewModel orderConfirmationViewModel) {
    this.orderConfirmationViewModel = orderConfirmationViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);
    declineAction = view.findViewById(R.id.declineButton);
    mapImage = view.findViewById(R.id.mapImage);
    distanceText = view.findViewById(R.id.distanceText);
    addressText = view.findViewById(R.id.addressText);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    priceTitleText = view.findViewById(R.id.priceTitleText);
    priceText = view.findViewById(R.id.priceText);
//    declineAction.setOnClickListener(v -> orderConfirmationViewModel.cancelOrder());
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
  }

  @Override
  public void onDetach() {
    super.onDetach();
    context = null;
  }

  @Override
  public void showOrderConfirmationPending(boolean pending) {
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
  public void showOrderConfirmationComment(@NonNull String comment) {
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
  public void showOrderConfirmationAvailabilityError(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage("Этот заказ недоступен.")
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void showOrderConfirmationNetworkErrorMessage(boolean show) {
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
}
