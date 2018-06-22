package com.fasten.executor_driver.presentation.phone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PhoneViewModelImpl extends ViewModel implements PhoneViewModel {

  @NonNull
  private final LoginUseCase loginUseCase;

  @NonNull
  private final MutableLiveData<ViewState<PhoneViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public PhoneViewModelImpl(@NonNull LoginUseCase loginUseCase) {
    this.loginUseCase = loginUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new PhoneViewStateInitial());
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
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = loginUseCase.validateLogin(phoneNumber.replaceAll("[^\\d]", ""))
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::switchToSuccess, this::switchToError);
  }

  @Override
  public void nextClicked() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = loginUseCase.rememberLogin()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              viewStateLiveData.postValue(new PhoneViewStateInitial());
              navigateLiveData.postValue(PhoneNavigate.PASSWORD);
            },
            Throwable::printStackTrace
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }

  private void switchToSuccess() {
    viewStateLiveData.postValue(new PhoneViewStateReady());
  }

  private void switchToError(Throwable throwable) {
    throwable.printStackTrace();
    if (!(viewStateLiveData.getValue() instanceof PhoneViewStateInitial)) {
      viewStateLiveData.setValue(new PhoneViewStateInitial());
    }
  }
}
