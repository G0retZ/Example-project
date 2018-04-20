package com.fasten.executor_driver.view;

import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.offer.OfferViewActions;
import com.fasten.executor_driver.presentation.offer.OfferViewModel;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class OfferFragment extends BaseFragment implements OfferViewActions {

  private OfferViewModel offerViewModel;
  private ImageButton declineAction;
  private ImageView mapImage;
  private DonutChart timeoutChart;
  private TextView distanceText;
  private TextView addressText;
  private TextView portersCountText;
  private TextView passengersCountText;
  private TextView commentText;
  private Button acceptAction;
  private FrameLayout pendingIndicator;
  private Context context;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setOfferViewModel(@NonNull OfferViewModel offerViewModel) {
    this.offerViewModel = offerViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_offer, container, false);
    declineAction = view.findViewById(R.id.declineButton);
    mapImage = view.findViewById(R.id.mapImage);
    timeoutChart = view.findViewById(R.id.timeoutChart);
    distanceText = view.findViewById(R.id.distanceText);
    addressText = view.findViewById(R.id.addressText);
    portersCountText = view.findViewById(R.id.portersCountText);
    passengersCountText = view.findViewById(R.id.passengersCountText);
    commentText = view.findViewById(R.id.commentText);
    acceptAction = view.findViewById(R.id.acceptButton);
    pendingIndicator = view.findViewById(R.id.pending);
    acceptAction.setOnClickListener(v -> offerViewModel.acceptOffer());
    declineAction.setOnClickListener(v -> offerViewModel.declineOffer());
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
    offerViewModel.getViewStateLiveData().observe(this, viewState -> {
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
  public void showOfferPending(boolean pending) {
    pendingIndicator.setVisibility(pending ? View.VISIBLE : View.GONE);
  }

  @Override
  public void showLoadPoint(@NonNull String url) {
    Picasso.with(context).load(url)
        .transform(new CropTransformation())
        .transform(new RoundedCornerTransformation(2, 0))
        .into(mapImage);
  }

  @Override
  public void showDistance(long distance) {
    distanceText.setText(getString(R.string.balance_amount, distance));
  }

  @Override
  public void showTimeout(long progress, long timeout) {
    ObjectAnimator animation = ObjectAnimator.ofFloat(timeoutChart, "value", progress, 0);
    animation.setDuration(timeout);
    animation.start();
  }

  @Override
  public void showLoadPointAddress(String address) {
    addressText.setText(address);
  }

  @Override
  public void showPortersCount(int count) {
    portersCountText.setText(String.valueOf(count));
  }

  @Override
  public void showPassengersCount(int count) {
    passengersCountText.setText(String.valueOf(count));
  }

  @Override
  public void showOfferComment(String comment) {
    commentText.setText(comment);
  }

  @Override
  public void showOfferAvailabilityError(boolean show) {
    if (show) {
      new Builder(context)
          .setTitle(R.string.error)
          .setMessage("Этот заказ недоступен для принятия.")
          .setPositiveButton(getString(android.R.string.ok), null)
          .create()
          .show();
    }
  }

  @Override
  public void showOfferNetworkErrorMessage(boolean show) {
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
