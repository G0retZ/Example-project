package com.fasten.executor_driver.presentation.driverorderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
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
  private Disposable offersDisposable = EmptyDisposable.INSTANCE;
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
    loadOffers();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOffers() {
    if (offersDisposable.isDisposed()) {
      offersDisposable = driverOrderConfirmationUseCase.getOffers()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOffer, this::consumeError);
    }
  }


  private void consumeOffer(@NonNull Order order) {
    orderItem = new OrderItem(order, timeUtils);
    viewStateLiveData.postValue(new DriverOrderConfirmationViewStateIdle(orderItem));
  }

  private void consumeError(Throwable throwable) {
    if (throwable instanceof NoOffersAvailableException) {
      viewStateLiveData.postValue(new DriverOrderConfirmationViewStateUnavailableError(orderItem));
    } else {
      viewStateLiveData.postValue(new DriverOrderConfirmationViewStateNetworkError(orderItem));
    }
  }

  @Override
  public void acceptOffer() {
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
  public void declineOffer() {
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
    if (!offersDisposable.isDisposed()) {
      offersDisposable.dispose();
    }
    if (!decisionDisposable.isDisposed()) {
      decisionDisposable.dispose();
    }
  }
}
