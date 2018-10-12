package com.cargopull.executor_driver.presentation.currentcostpolling;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class CurrentCostPollingViewModelImpl extends ViewModel implements
    CurrentCostPollingViewModel {

  @NonNull
  private final CurrentCostPollingUseCase useCase;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CurrentCostPollingViewModelImpl(@NonNull CurrentCostPollingUseCase useCase) {
    this.useCase = useCase;
    navigateLiveData = new MutableLiveData<>();
    startCurrentCostPolling();
  }

  @NonNull
  @Override
  public LiveData<ViewState<Runnable>> getViewStateLiveData() {
    return new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void startCurrentCostPolling() {
    disposable.dispose();
    disposable = useCase.listenForPolling()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
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
