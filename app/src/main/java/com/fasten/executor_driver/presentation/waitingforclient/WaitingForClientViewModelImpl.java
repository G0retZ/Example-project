package com.fasten.executor_driver.presentation.waitingforclient;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.WaitingForClientUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class WaitingForClientViewModelImpl extends ViewModel implements WaitingForClientViewModel {

  @NonNull
  private final WaitingForClientUseCase movingToClientUseCase;
  @NonNull
  private final MutableLiveData<ViewState<WaitingForClientViewActions>> viewStateLiveData;
  @NonNull
  private Disposable ordersDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable actionsDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private OrderItem orderItem;

  @Inject
  WaitingForClientViewModelImpl(@NonNull WaitingForClientUseCase movingToClientUseCase) {
    this.movingToClientUseCase = movingToClientUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new WaitingForClientViewStatePending(orderItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<WaitingForClientViewActions>> getViewStateLiveData() {
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
      ordersDisposable = movingToClientUseCase.getOrders()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOrder, this::consumeError);
    }
  }


  private void consumeOrder(@NonNull Order order) {
    orderItem = new OrderItem(order);
    viewStateLiveData.postValue(new WaitingForClientViewStateIdle(orderItem));
  }

  private void consumeError(Throwable throwable) {
    throwable.printStackTrace();
    if (throwable instanceof NoOrdersAvailableException) {
      viewStateLiveData.postValue(new WaitingForClientViewStateUnavailableError(orderItem));
    } else {
      viewStateLiveData.postValue(new WaitingForClientViewStateNetworkError(orderItem));
    }
  }

  @Override
  public void callToClient() {
    if (!actionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new WaitingForClientViewStatePending(orderItem));
    actionsDisposable = movingToClientUseCase.callToClient()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new WaitingForClientViewStateIdle(orderItem)),
            this::consumeError
        );
  }

  @Override
  public void startLoading() {
    if (!actionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new WaitingForClientViewStatePending(orderItem));
    actionsDisposable = movingToClientUseCase.startTheOrder()
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
    if (!ordersDisposable.isDisposed()) {
      ordersDisposable.dispose();
    }
    if (!actionsDisposable.isDisposed()) {
      actionsDisposable.dispose();
    }
  }
}
