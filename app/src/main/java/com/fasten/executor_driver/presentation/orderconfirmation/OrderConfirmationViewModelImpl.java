package com.fasten.executor_driver.presentation.orderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.OrderConfirmationUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
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
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public OrderConfirmationViewModelImpl(
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase) {
    this.orderConfirmationUseCase = orderConfirmationUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderConfirmationViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void acceptOrder() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
    disposable = orderConfirmationUseCase.sendDecision(true)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
              navigateLiveData.postValue(OrderConfirmationNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  public void declineOrder() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
    disposable = orderConfirmationUseCase.sendDecision(false)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
              navigateLiveData.postValue(OrderConfirmationNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  public void counterTimeOut() {
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
