package com.fasten.executor_driver.presentation.movingtoclient;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.MovingToClientUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class MovingToClientViewModelImpl extends ViewModel implements MovingToClientViewModel {

  @NonNull
  private final MovingToClientUseCase movingToClientUseCase;
  @NonNull
  private final MutableLiveData<ViewState<MovingToClientViewActions>> viewStateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable ordersDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable actionsDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private RouteItem routeItem;

  @Inject
  public MovingToClientViewModelImpl(
      @NonNull MovingToClientUseCase movingToClientUseCase,
      @NonNull TimeUtils timeUtils) {
    this.movingToClientUseCase = movingToClientUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new MovingToClientViewStatePending(routeItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<MovingToClientViewActions>> getViewStateLiveData() {
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
    routeItem = new RouteItem(order, timeUtils);
    viewStateLiveData.postValue(new MovingToClientViewStateIdle(routeItem));
  }

  private void consumeError(Throwable throwable) {
    if (throwable instanceof NoOrdersAvailableException) {
      viewStateLiveData.postValue(new MovingToClientViewStateUnavailableError(routeItem));
    } else {
      viewStateLiveData.postValue(new MovingToClientViewStateNetworkError(routeItem));
    }
  }

  @Override
  public void callToClient() {
    if (!actionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new MovingToClientViewStatePending(routeItem));
    actionsDisposable = movingToClientUseCase.callToClient()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new MovingToClientViewStateIdle(routeItem)),
            this::consumeError
        );
  }

  @Override
  public void reportArrival() {
    if (!actionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new MovingToClientViewStatePending(routeItem));
    actionsDisposable = movingToClientUseCase.reportArrival()
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
    routeItem = null;
    if (!ordersDisposable.isDisposed()) {
      ordersDisposable.dispose();
    }
    if (!actionsDisposable.isDisposed()) {
      actionsDisposable.dispose();
    }
  }
}
