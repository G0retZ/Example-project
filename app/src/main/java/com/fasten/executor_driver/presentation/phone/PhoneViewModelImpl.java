package com.fasten.executor_driver.presentation.phone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.presentation.ViewState;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PhoneViewModelImpl extends ViewModel implements PhoneViewModel {

  @NonNull
  private final LoginUseCase loginUseCase;

  @NonNull
  private final MutableLiveData<ViewState<PhoneViewActions>> viewStateLiveData;

  private Disposable disposable;

  private String lastLogin;

  @Inject
  public PhoneViewModelImpl(@NonNull LoginUseCase loginUseCase) {
    this.loginUseCase = loginUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new PhoneViewStateInitial());
  }

  @NonNull
  @Override
  public LiveData<ViewState<PhoneViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @Override
  public void phoneNumberChanged(@NonNull String phoneNumber) {
    lastLogin = phoneNumber.replaceAll("[^\\d]", "");
    if (disposable != null) {
      return;
    }
    if (viewStateLiveData.getValue() instanceof PhoneViewStateError) {
      viewStateLiveData.postValue(new PhoneViewStateInitial());
    }
    disposable = loginUseCase.validateLogin(lastLogin)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::checkNumber, throwable -> disposable = null);
    if (disposable.isDisposed()) {
      disposable = null;
    }
  }

  @Override
  public void nextClicked() {
    viewStateLiveData.postValue(new PhoneViewStateProceed(lastLogin));
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
      disposable = null;
    }
  }

  private void checkNumber() {
    viewStateLiveData.postValue(new PhoneViewStatePending());
    disposable = loginUseCase.checkLogin(lastLogin)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::switchToSuccess, this::switchToError);
    if (disposable.isDisposed()) {
      disposable = null;
    }
  }

  private void switchToSuccess() {
    disposable = null;
    viewStateLiveData.postValue(new PhoneViewStateReady());
  }

  private void switchToError(Throwable throwable) {
    disposable = null;
    viewStateLiveData.postValue(new PhoneViewStateError(throwable));
  }
}
