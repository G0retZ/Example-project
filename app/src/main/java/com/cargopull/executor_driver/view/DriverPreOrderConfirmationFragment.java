package com.cargopull.executor_driver.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewActions;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Отображает заказ.
 */

public class DriverPreOrderConfirmationFragment extends BaseFragment implements
    OrderConfirmationViewActions {

  private OrderConfirmationViewModel orderConfirmationViewModel;
  private ImageButton declineAction;
  private Button setOutAction;
  @NonNull
  private Disposable timeoutAnimation = Disposables.disposed();

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
    View view = inflater.inflate(R.layout.fragment_driver_pre_order_confirmation, container, false);
    declineAction = view.findViewById(R.id.declineButton);
    setOutAction = view.findViewById(R.id.setOutButton);
    setOutAction.setOnClickListener(v -> orderConfirmationViewModel.acceptOrder());
    declineAction.setOnClickListener(v -> orderConfirmationViewModel.declineOrder());
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
    timeoutAnimation.dispose();
    super.onDetach();
  }

  @Override
  public void showDriverOrderConfirmationPending(boolean pending) {
    showPending(pending, toString());
  }

  @Override
  public void showTimeout(long timeout) {
    timeoutAnimation.dispose();
    if (timeout > 0) {
      long period = TimeUnit.MILLISECONDS.toSeconds(timeout);
      timeoutAnimation = Observable.interval(period, TimeUnit.SECONDS)
          .subscribeOn(AndroidSchedulers.mainThread())
          .subscribe(tick -> setOutAction.setText(
              getString(R.string.accept_timed, period - tick)),
              Throwable::printStackTrace,
              orderConfirmationViewModel::counterTimeOut
          );
    } else if (timeout == 0) {
      orderConfirmationViewModel.counterTimeOut();
    }
  }

  @Override
  public void enableDeclineButton(boolean enable) {
    declineAction.setEnabled(enable);
  }

  @Override
  public void enableAcceptButton(boolean enable) {
    setOutAction.setEnabled(enable);
  }

  @Override
  public void showAcceptedMessage(@Nullable String message) {
  }

  @Override
  public void showDeclinedMessage(@Nullable String message) {
  }

  @Override
  public void showFailedMessage(@Nullable String message) {
  }
}
