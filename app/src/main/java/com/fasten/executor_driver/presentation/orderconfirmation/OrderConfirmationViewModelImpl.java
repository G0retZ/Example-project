package com.fasten.executor_driver.presentation.orderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.interactor.OrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationViewModelImpl extends ViewModel implements
    OrderConfirmationViewModel {

  @NonNull
  private final OrderConfirmationUseCase orderConfirmationUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderConfirmationViewActions>> viewStateLiveData;
  @NonNull
  private Disposable orderConfirmationDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable cancelOrderDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private OrderConfirmationItem orderConfirmationItem;

  @Inject
  public OrderConfirmationViewModelImpl(
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase) {
    this.orderConfirmationUseCase = orderConfirmationUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending(orderConfirmationItem));
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderConfirmationViewActions>> getViewStateLiveData() {
    loadOffers();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOffers() {
    if (orderConfirmationDisposable.isDisposed()) {
      orderConfirmationDisposable = orderConfirmationUseCase.getOffers()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOffer, this::consumeError);
    }
  }


  private void consumeOffer(@NonNull Offer offer) {
    orderConfirmationItem = new OrderConfirmationItem(offer);
    viewStateLiveData.postValue(new OrderConfirmationViewStateIdle(orderConfirmationItem));
  }

  private void consumeError(Throwable throwable) {
    if (throwable instanceof NoOffersAvailableException) {
      viewStateLiveData
          .postValue(new OrderConfirmationViewStateUnavailableError(orderConfirmationItem));
    } else {
      viewStateLiveData
          .postValue(new OrderConfirmationViewStateNetworkError(orderConfirmationItem));
    }
  }

  @Override
  public void cancelOrder() {
    if (!cancelOrderDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending(orderConfirmationItem));
    cancelOrderDisposable = orderConfirmationUseCase.cancelOrder()
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
    orderConfirmationItem = null;
    if (!orderConfirmationDisposable.isDisposed()) {
      orderConfirmationDisposable.dispose();
    }
    if (!cancelOrderDisposable.isDisposed()) {
      cancelOrderDisposable.dispose();
    }
  }
}
