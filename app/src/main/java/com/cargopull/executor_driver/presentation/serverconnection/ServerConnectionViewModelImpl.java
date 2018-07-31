package com.cargopull.executor_driver.presentation.serverconnection;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.DeprecatedVersionException;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ServerConnectionViewModelImpl extends ViewModel implements ServerConnectionViewModel {

  @NonNull
  private final ServerConnectionUseCase serverConnectionUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ServerConnectionViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public ServerConnectionViewModelImpl(@NonNull ServerConnectionUseCase serverConnectionUseCase) {
    this.serverConnectionUseCase = serverConnectionUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<ServerConnectionViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void connectServer() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = serverConnectionUseCase.connect()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCancel(
            () -> viewStateLiveData.postValue(viewActions -> viewActions.showConnectionReady(false))
        ).subscribe(
            connected ->
                viewStateLiveData.postValue(viewActions ->
                    viewActions.showConnectionReady(connected)
                ),
            throwable -> {
              if ((throwable instanceof AuthorizationException)) {
                navigateLiveData.postValue(ServerConnectionNavigate.AUTHORIZE);
              } else if ((throwable instanceof DeprecatedVersionException)) {
                navigateLiveData.postValue(ServerConnectionNavigate.VERSION_DEPRECATED);
              }
            }
        );
  }

  @Override
  public void disconnectServer() {
    disposable.dispose();
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
