package com.fasten.executor_driver.presentation.cancelorderreasons;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.gateway.DataMappingException;
import com.fasten.executor_driver.interactor.CancelOrderUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CancelOrderReasonsViewModelImpl extends ViewModel implements
    CancelOrderReasonsViewModel {

  @NonNull
  private final CancelOrderUseCase cancelOrderUseCase;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CancelOrderReasonsViewModelImpl(@NonNull CancelOrderUseCase cancelOrderUseCase) {
    this.cancelOrderUseCase = cancelOrderUseCase;
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CancelOrderReasonsViewActions>> getViewStateLiveData() {
    return new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void initializeCancelOrderReasons(boolean reset) {
    disposable.dispose();
    disposable = cancelOrderUseCase.getCancelOrderReasons(reset)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            cancelOrderReasons -> {
            },
            throwable -> {
              throwable.printStackTrace();
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CancelOrderReasonsNavigate.SERVER_DATA_ERROR);
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
