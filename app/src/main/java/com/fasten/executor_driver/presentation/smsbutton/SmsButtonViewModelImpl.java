package com.fasten.executor_driver.presentation.smsbutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class SmsButtonViewModelImpl extends ViewModel implements SmsButtonViewModel {

  private static final int DURATION_AFTER_SUCCESS = 30;
  private static final int DURATION_AFTER_FAIL = 5;
  private Disposable disposable;

  @NonNull
  private final SmsUseCase smsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<SmsButtonViewActions>> viewStateLiveData;

  @Inject
  SmsButtonViewModelImpl(@NonNull SmsUseCase smsUseCase) {
    this.smsUseCase = smsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SmsButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<SmsButtonViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  private void holdButton(int duration) {
    disposable = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
        .take(duration)
        .map(count -> duration - count)
        .subscribe(
            count -> viewStateLiveData.postValue(new SmsButtonViewStateHold(count)),
            throwable -> {
            },
            () -> viewStateLiveData.postValue(new SmsButtonViewStateReady())
        );
  }

  @Override
  public void sendMeSms() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new SmsButtonViewStatePending());
    Disposable disposable = smsUseCase.sendMeCode()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> holdButton(DURATION_AFTER_SUCCESS),
            throwable -> {
              viewStateLiveData.postValue(new SmsButtonViewStateError(throwable));
              holdButton(DURATION_AFTER_FAIL);
            }
        );
    if (this.disposable == null || this.disposable.isDisposed()) {
      this.disposable = disposable;
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
