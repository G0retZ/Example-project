package com.cargopull.executor_driver.presentation.smsbutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.interactor.auth.SmsUseCase;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class SmsButtonViewModelImpl extends ViewModel implements SmsButtonViewModel {

  private static final int DURATION_AFTER_SUCCESS = 30;
  @NonNull
  private final SmsUseCase smsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SmsButtonViewActions>> viewStateLiveData;
  @NonNull
  private Disposable sendDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timerDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public SmsButtonViewModelImpl(@NonNull SmsUseCase smsUseCase) {
    this.smsUseCase = smsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SmsButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<SmsButtonViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void sendMeSms() {
    if (!sendDisposable.isDisposed() || !timerDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new SmsButtonViewStatePending());
    sendDisposable = smsUseCase.sendMeCode()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::holdButton,
            throwable -> {
              if (throwable instanceof NoNetworkException) {
                viewStateLiveData.postValue(new SmsButtonViewStateError());
              } else {
                holdButton();
              }
            }
        );
  }

  private void holdButton() {
    timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
        .take(DURATION_AFTER_SUCCESS)
        .map(count -> DURATION_AFTER_SUCCESS - count)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            count -> viewStateLiveData.postValue(new SmsButtonViewStateHold(count)),
            throwable -> {
            },
            () -> viewStateLiveData.postValue(new SmsButtonViewStateReady())
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    timerDisposable.dispose();
    sendDisposable.dispose();
  }
}
