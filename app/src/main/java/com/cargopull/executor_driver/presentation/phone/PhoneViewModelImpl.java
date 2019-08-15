package com.cargopull.executor_driver.presentation.phone;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.auth.LoginUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
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
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::switchToSuccess, throwable -> switchToError());
  }

  @Override
  public void nextClicked() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = loginUseCase.rememberLogin()
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

  private void switchToError() {
    if (!(viewStateLiveData.getValue() instanceof PhoneViewStateInitial)) {
      viewStateLiveData.setValue(new PhoneViewStateInitial());
    }
  }
}
