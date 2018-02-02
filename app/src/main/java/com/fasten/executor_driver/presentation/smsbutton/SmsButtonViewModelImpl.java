package com.fasten.executor_driver.presentation.smsbutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;

public class SmsButtonViewModelImpl extends ViewModel implements SmsButtonViewModel {

  private final int duration;
  private Disposable disposable;

  @NonNull
  private final MutableLiveData<ViewState<SmsButtonViewActions>> viewStateLiveData;

  @SuppressWarnings("SameParameterValue")
  @Inject
  SmsButtonViewModelImpl(@Named("timeoutDuration") int duration) {
    this.duration = duration;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SmsButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<SmsButtonViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @Override
  public boolean buttonClicked() {
    if (disposable != null && !disposable.isDisposed()) {
      return false;
    }
    disposable = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
        .take(duration)
        .map(count -> duration - count)
        .subscribe(
            count -> viewStateLiveData.postValue(new SmsButtonViewStateHold(count)),
            throwable -> {
            },
            () -> viewStateLiveData.postValue(new SmsButtonViewStateReady())
        );
    return true;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
