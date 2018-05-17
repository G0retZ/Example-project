package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.DriverOrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class DriverOrderConfirmationViewModelImpl extends ViewModel implements
    DriverOrderConfirmationViewModel {

  @NonNull
  private final DriverOrderConfirmationUseCase driverOrderConfirmationUseCase;
  @NonNull
  private final MutableLiveData<ViewState<DriverOrderConfirmationViewActions>> viewStateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable ordersDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable decisionDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private OrderItem orderItem;

  @Inject
  public DriverOrderConfirmationViewModelImpl(
      @NonNull DriverOrderConfirmationUseCase driverOrderConfirmationUseCase,
      @NonNull TimeUtils timeUtils) {
    this.driverOrderConfirmationUseCase = driverOrderConfirmationUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new DriverOrderConfirmationViewStatePending(orderItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<DriverOrderConfirmationViewActions>> getViewStateLiveData() {
    loadOrders();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOrders() {
    if (ordersDisposable.isDisposed()) {
      ordersDisposable = driverOrderConfirmationUseCase.getOrders()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOrder, this::consumeError);
    }
  }


  private void consumeOrder(@NonNull Order order) {
    orderItem = new OrderItem(order, timeUtils);
    viewStateLiveData.postValue(new DriverOrderConfirmationViewStateIdle(orderItem));
  }

  private void consumeError(Throwable throwable) {
    throwable.printStackTrace();
    if (throwable instanceof NoOrdersAvailableException) {
      viewStateLiveData.postValue(new DriverOrderConfirmationViewStateUnavailableError(orderItem));
    } else {
      viewStateLiveData.postValue(new DriverOrderConfirmationViewStateNetworkError(orderItem));
    }
  }

  @Override
  public void acceptOrder() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new DriverOrderConfirmationViewStatePending(orderItem));
    decisionDisposable = driverOrderConfirmationUseCase.sendDecision(true)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, this::consumeError
        );
  }

  @Override
  public void declineOrder() {
    if (!decisionDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new DriverOrderConfirmationViewStatePending(orderItem));
    decisionDisposable = driverOrderConfirmationUseCase.sendDecision(false)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, this::consumeError
        );
  }

  @Override
  public void counterTimeOut() {
    if (!(viewStateLiveData.getValue() instanceof DriverOrderConfirmationViewStatePending)) {
      viewStateLiveData.postValue(new DriverOrderConfirmationViewStatePending(orderItem));
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    orderItem = null;
    if (!ordersDisposable.isDisposed()) {
      ordersDisposable.dispose();
    }
    if (!decisionDisposable.isDisposed()) {
      decisionDisposable.dispose();
    }
  }
}
