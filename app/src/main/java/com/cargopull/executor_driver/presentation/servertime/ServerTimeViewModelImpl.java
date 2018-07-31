package com.cargopull.executor_driver.presentation.servertime;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerTimeViewModelImpl extends ViewModel implements ServerTimeViewModel {

  @NonNull
  private final ServerTimeUseCase serverTimeUseCase;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  ServerTimeViewModelImpl(@NonNull ServerTimeUseCase serverTimeUseCase) {
    this.serverTimeUseCase = serverTimeUseCase;
    navigateLiveData = new MutableLiveData<>();
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

  @Override
  public void initializeServerTime() {
    disposable.dispose();
    disposable = serverTimeUseCase.getServerTime()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
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
