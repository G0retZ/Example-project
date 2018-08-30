package com.cargopull.executor_driver.presentation.orderconfirmation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
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
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
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
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> viewStateLiveData.postValue(new OrderConfirmationViewStateResult(message)),
            t -> {
              if (t instanceof PreOrderExpiredException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateResult(t.getMessage()));
              } else {
                viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
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
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            message -> navigateLiveData.postValue(OrderConfirmationNavigate.CLOSE),
            t -> {
              if (t instanceof PreOrderExpiredException) {
                viewStateLiveData.postValue(new OrderConfirmationViewStateResult(t.getMessage()));
              } else {
                viewStateLiveData.postValue(new OrderConfirmationViewStateIdle());
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  @Override
  public void counterTimeOut() {
    viewStateLiveData.postValue(new OrderConfirmationViewStatePending());
  }

  @Override
  public void messageConsumed() {
    navigateLiveData.postValue(OrderConfirmationNavigate.CLOSE);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
