package com.fasten.executor_driver.presentation.code;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.NoNetworkException;
import com.fasten.executor_driver.entity.ValidationException;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CodeViewModelImpl extends ViewModel implements CodeViewModel {

  @NonNull
  private final PasswordUseCase passwordUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CodeViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CodeViewModelImpl(@NonNull PasswordUseCase passwordUseCase) {
    this.passwordUseCase = passwordUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new CodeViewStateInitial());
  }

  @NonNull
  @Override
  public LiveData<ViewState<CodeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void setCode(@NonNull String code) {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = passwordUseCase.authorize(
        code.replaceAll("[^\\d]", ""),
        Completable.create(e -> {
          viewStateLiveData.postValue(new CodeViewStatePending());
          e.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.single())
    )
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(CodeNavigate.ENTER_APP),
            throwable -> {
              throwable.printStackTrace();
              if (throwable instanceof NoNetworkException) {
                viewStateLiveData.postValue(new CodeViewStateNetworkError());
              } else if (throwable instanceof ValidationException) {
                if (!(viewStateLiveData.getValue() instanceof CodeViewStateInitial)) {
                  viewStateLiveData.postValue(new CodeViewStateInitial());
                }
              } else {
                viewStateLiveData.postValue(new CodeViewStateError());
              }
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
