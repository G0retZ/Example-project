package com.fasten.executor_driver.presentation.phone;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PhoneViewModelImpl extends ViewModel implements PhoneViewModel {

  @NonNull
  private final LoginUseCase loginUseCase;

  @NonNull
  private final MutableLiveData<ViewState<PhoneViewActions>> viewStateLiveData;
  @NonNull
  private final MutableLiveData<String> navigateLiveData;

  private Disposable disposable;

  @Inject
  public PhoneViewModelImpl(@NonNull LoginUseCase loginUseCase) {
    this.loginUseCase = loginUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new PhoneViewStateInitial());
    navigateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<PhoneViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void phoneNumberChanged(@NonNull String phoneNumber) {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    disposable = loginUseCase.validateLogin(phoneNumber.replaceAll("[^\\d]", ""))
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::switchToSuccess, throwable -> switchToError());
    if (disposable.isDisposed()) {
      disposable = null;
    }
  }

  @SuppressLint("CheckResult")
  @Override
  public void nextClicked() {
    if (viewStateLiveData.getValue() instanceof PhoneViewStateReady) {
      loginUseCase.rememberLogin()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              () -> navigateLiveData.postValue(PhoneNavigate.PASSWORD),
              Throwable::printStackTrace
          );
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
      disposable = null;
    }
  }

  private void switchToSuccess() {
    disposable = null;
    viewStateLiveData.postValue(new PhoneViewStateReady());
  }

  private void switchToError() {
    disposable = null;
    if (!(viewStateLiveData.getValue() instanceof PhoneViewStateInitial)) {
      viewStateLiveData.setValue(new PhoneViewStateInitial());
    }
  }
}
