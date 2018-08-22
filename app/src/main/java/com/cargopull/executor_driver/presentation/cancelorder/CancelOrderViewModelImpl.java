package com.cargopull.executor_driver.presentation.cancelorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class CancelOrderViewModelImpl extends ViewModel implements CancelOrderViewModel {

  @NonNull
  private final CancelOrderUseCase cancelOrderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CancelOrderViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CancelOrderViewModelImpl(@NonNull CancelOrderUseCase cancelOrderUseCase) {
    this.cancelOrderUseCase = cancelOrderUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CancelOrderViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void selectItem(CancelOrderReason cancelOrderReason) {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(viewActions -> viewActions.showCancelOrderPending(true));
    disposable = cancelOrderUseCase.cancelOrder(cancelOrderReason)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              viewStateLiveData.postValue(viewActions -> viewActions.showCancelOrderPending(false));
              navigateLiveData.postValue(CancelOrderNavigate.ORDER_CANCELED);
            },
            throwable -> {
              viewStateLiveData.postValue(viewActions -> viewActions.showCancelOrderPending(false));
              if (throwable instanceof IllegalStateException) {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
