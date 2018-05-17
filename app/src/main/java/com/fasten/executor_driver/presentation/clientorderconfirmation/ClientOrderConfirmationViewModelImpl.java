package com.fasten.executor_driver.presentation.clientorderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.ClientOrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ClientOrderConfirmationViewModelImpl extends ViewModel implements
    ClientOrderConfirmationViewModel {

  @NonNull
  private final ClientOrderConfirmationUseCase clientOrderConfirmationUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ClientOrderConfirmationViewActions>> viewStateLiveData;
  @NonNull
  private Disposable orderConfirmationDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable cancelOrderDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private OrderItem orderItem;

  @Inject
  public ClientOrderConfirmationViewModelImpl(
      @NonNull ClientOrderConfirmationUseCase clientOrderConfirmationUseCase) {
    this.clientOrderConfirmationUseCase = clientOrderConfirmationUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new ClientOrderConfirmationViewStatePending(orderItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<ClientOrderConfirmationViewActions>> getViewStateLiveData() {
    loadOrders();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOrders() {
    if (orderConfirmationDisposable.isDisposed()) {
      orderConfirmationDisposable = clientOrderConfirmationUseCase.getOrders()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOrder, this::consumeError);
    }
  }


  private void consumeOrder(@NonNull Order order) {
    orderItem = new OrderItem(order);
    viewStateLiveData.postValue(new ClientOrderConfirmationViewStateIdle(orderItem));
  }

  private void consumeError(Throwable throwable) {
    throwable.printStackTrace();
    if (throwable instanceof NoOrdersAvailableException) {
      viewStateLiveData
          .postValue(new ClientOrderConfirmationViewStateUnavailableError(orderItem));
    } else {
      viewStateLiveData
          .postValue(new ClientOrderConfirmationViewStateNetworkError(orderItem));
    }
  }

  @Override
  public void cancelOrder() {
    if (!cancelOrderDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new ClientOrderConfirmationViewStatePending(orderItem));
    cancelOrderDisposable = clientOrderConfirmationUseCase.cancelOrder()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, this::consumeError
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    orderItem = null;
    if (!orderConfirmationDisposable.isDisposed()) {
      orderConfirmationDisposable.dispose();
    }
    if (!cancelOrderDisposable.isDisposed()) {
      cancelOrderDisposable.dispose();
    }
  }
}
