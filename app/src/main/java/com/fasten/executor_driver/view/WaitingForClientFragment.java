package com.fasten.executor_driver.view;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.order.OrderViewActions;
import com.fasten.executor_driver.presentation.order.OrderViewModel;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientViewActions;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientViewModel;
import javax.inject.Inject;

/**
 * Отображает ожидание клиента.
 */

public class WaitingForClientFragment extends BaseFragment implements
    WaitingForClientViewActions, OrderViewActions {

  private WaitingForClientViewModel waitingForClientViewModel;
  private OrderViewModel orderViewModel;
  private TextView commentTitleText;
  private TextView commentText;
  private TextView optionsTitleText;
  private TextView optionsText;
  private TextView priceTitleText;
  private TextView priceText;
  private Context context;
  private boolean waitingForClientPending;
  private boolean orderPending;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Inject
  public void setWaitingForClientViewModel(
      @NonNull WaitingForClientViewModel waitingForClientViewModel) {
    this.waitingForClientViewModel = waitingForClientViewModel;
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
    View view = inflater.inflate(R.layout.fragment_waiting_for_client, container, false);
    commentTitleText = view.findViewById(R.id.commentTitleText);
    commentText = view.findViewById(R.id.commentText);
    optionsTitleText = view.findViewById(R.id.optionsTitleText);
    optionsText = view.findViewById(R.id.optionsText);
    priceTitleText = view.findViewById(R.id.priceTitleText);
    priceText = view.findViewById(R.id.priceText);
    Button callToClient = view.findViewById(R.id.callToClient);
    Button startLoading = view.findViewById(R.id.startLoading);
    callToClient.setOnClickListener(v -> {
      navigate(WaitingForClientNavigate.CALL_TO_CLIENT);
      callToClient.setEnabled(false);
      callToClient.postDelayed(() -> callToClient.setEnabled(true), 10_000);
    });
    startLoading.setOnClickListener(v -> waitingForClientViewModel.startLoading());
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
    super.onDetach();
    context = null;
  }

  @Override
  public void showWaitingForClientPending(boolean pending) {
    if (this.waitingForClientPending != pending) {
      showPending(pending);
    }
    this.waitingForClientPending = pending;
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

  }

  @Override
  public void showLoadPointAddress(@NonNull String coordinates, @NonNull String address) {

  }

  @Override
  public void showTimeout(int timeout) {

  }

  @Override
  public void showTimeout(int progress, long timeout) {

  }

  @Override
  public void showDistance(String distance) {

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
  public void showOrderServerDataError() {
    new Builder(context)
        .setTitle(R.string.error)
        .setMessage(R.string.server_data_format_error)
        .setPositiveButton(getString(android.R.string.ok), null)
        .create()
        .show();
  }
}
