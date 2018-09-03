package com.cargopull.executor_driver.presentation.cancelorderreasons;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class CancelOrderReasonsViewModelImpl extends ViewModel implements
    CancelOrderReasonsViewModel {

  @NonNull
  private final CancelOrderReasonsUseCase cancelOrderReasonsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CancelOrderReasonsViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<CancelOrderReasonsViewActions> lastViewState;

  @Inject
  public CancelOrderReasonsViewModelImpl(
      @NonNull CancelOrderReasonsUseCase cancelOrderReasonsUseCase) {
    this.cancelOrderReasonsUseCase = cancelOrderReasonsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadCancelOrderReasons();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CancelOrderReasonsViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadCancelOrderReasons() {
    disposable.dispose();
    viewStateLiveData.postValue(new CancelOrderReasonsViewStatePending(lastViewState));
    disposable = cancelOrderReasonsUseCase.getCancelOrderReasons()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            cancelOrderReasons -> viewStateLiveData.postValue(
                lastViewState = new CancelOrderReasonsViewState(cancelOrderReasons)
            ),
            throwable -> {
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
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
