package com.cargopull.executor_driver.presentation.currentcostpolling;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CurrentCostPollingViewModelImpl extends ViewModel implements
    CurrentCostPollingViewModel {

  @NonNull
  private final CurrentCostPollingUseCase currentCostPollingUseCase;
  @NonNull
  private final SingleLiveEvent<ViewState<CurrentCostPollingViewActions>> messageLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  private boolean completed;

  @Inject
  public CurrentCostPollingViewModelImpl(
      @NonNull CurrentCostPollingUseCase currentCostPollingUseCase) {
    this.currentCostPollingUseCase = currentCostPollingUseCase;
    navigateLiveData = new MutableLiveData<>();
    messageLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CurrentCostPollingViewActions>> getViewStateLiveData() {
    return messageLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void initializeCurrentCostPolling() {
    disposable.dispose();
    disposable = currentCostPollingUseCase.listenForPolling()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .doAfterTerminate(() -> {
          if (completed) {
            completed = false;
            initializeCurrentCostPolling();
          }
        }).subscribe(
            () -> completed = true,
            throwable -> {
              throwable.printStackTrace();
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
            });
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
