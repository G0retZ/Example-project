package com.cargopull.executor_driver.presentation.servertime;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.ServerTimeUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class ServerTimeViewModelImpl extends ViewModel implements ServerTimeViewModel {

  @NonNull
  private final ServerTimeUseCase serverTimeUseCase;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ServerTimeViewModelImpl(@NonNull ServerTimeUseCase serverTimeUseCase) {
    this.serverTimeUseCase = serverTimeUseCase;
    navigateLiveData = new MutableLiveData<>();
    loadServerTime();
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

  private void loadServerTime() {
    disposable.dispose();
    disposable = serverTimeUseCase.getServerTime()
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
