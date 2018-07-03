package com.fasten.executor_driver.presentation.serverconnection;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.AuthorizationException;
import com.fasten.executor_driver.interactor.ServerConnectionUseCase;
import com.fasten.executor_driver.presentation.ViewState;
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
        .subscribe(
            connected ->
                viewStateLiveData.postValue(viewActions ->
                    viewActions.showConnectionReady(connected)
                ),
            throwable -> {
              throwable.printStackTrace();
              if ((throwable instanceof AuthorizationException)) {
                navigateLiveData.postValue(ServerConnectionNavigate.AUTHORIZE);
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
