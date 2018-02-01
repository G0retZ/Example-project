package com.fasten.executor_driver.presentation.code;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.ValidationException;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class CodeViewModelImpl extends ViewModel implements CodeViewModel {

  @NonNull
  private final PasswordUseCase passwordUseCase;
  @NonNull
  private final SmsUseCase smsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<CodeViewActions>> viewStateLiveData;
  private Disposable disposable;

  @Inject
  CodeViewModelImpl(@NonNull PasswordUseCase passwordUseCase, @NonNull SmsUseCase smsUseCase) {
    this.passwordUseCase = passwordUseCase;
    this.smsUseCase = smsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new CodeViewStateInitial());
  }

  @NonNull
  @Override
  public LiveData<ViewState<CodeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @Override
  public void setCode(@NonNull String code) {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = passwordUseCase.authorize(
        code,
        Completable.create(e -> {
          viewStateLiveData.postValue(new CodeViewStatePending());
          e.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.single()))
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new CodeViewStateSuccess()),
            throwable -> {
              if (throwable instanceof ValidationException) {
                if (!(viewStateLiveData.getValue() instanceof CodeViewStateInitial)) {
                  viewStateLiveData.postValue(new CodeViewStateInitial());
                }
              } else {
                viewStateLiveData.postValue(new CodeViewStateError(throwable));
              }
            }
        );
  }

  @Override
  public void sendMeSms() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new CodeViewStatePending());
    disposable = smsUseCase.sendMeCode()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new CodeViewStateInitial()),
            throwable -> viewStateLiveData.postValue(new CodeViewStateError(throwable)
            )
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }
}
