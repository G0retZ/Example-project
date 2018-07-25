package com.cargopull.executor_driver.presentation.cancelorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CancelOrderViewModelImpl extends ViewModel implements CancelOrderViewModel {

  @NonNull
  private final CancelOrderUseCase cancelOrderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CancelOrderViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable reasonsDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable choiceDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<CancelOrderViewActions> lastViewState;

  @Inject
  public CancelOrderViewModelImpl(@NonNull CancelOrderUseCase cancelOrderUseCase) {
    this.cancelOrderUseCase = cancelOrderUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadCancelOrderReasons();
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
    if (!choiceDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new CancelOrderViewStatePending(lastViewState));
    choiceDisposable = cancelOrderUseCase.cancelOrder(cancelOrderReason)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              viewStateLiveData.postValue(lastViewState);
              navigateLiveData.postValue(CancelOrderNavigate.ORDER_CANCELED);
            },
            throwable -> {
              viewStateLiveData.postValue(lastViewState);
              if (throwable instanceof IllegalStateException) {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );
  }

  private void loadCancelOrderReasons() {
    if (!reasonsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new CancelOrderViewStatePending(lastViewState));
    reasonsDisposable = cancelOrderUseCase.getCancelOrderReasons(false)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            cancelOrderReasons -> viewStateLiveData
                .postValue(lastViewState = new CancelOrderViewState(cancelOrderReasons)),
            throwable -> navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR)
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    reasonsDisposable.dispose();
    choiceDisposable.dispose();
  }
}
