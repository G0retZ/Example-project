package com.fasten.executor_driver.presentation.onlinebutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.online.OnlineUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class OnlineButtonViewModelImpl extends ViewModel implements OnlineButtonViewModel {

  private static final int DURATION_AFTER_SUCCESS = 5;
  private static final int DURATION_AFTER_FAIL = 5;
  private Disposable disposable;

  @NonNull
  private final OnlineUseCase onlineUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OnlineButtonViewActions>> viewStateLiveData;

  @Inject
  public OnlineButtonViewModelImpl(@NonNull OnlineUseCase onlineUseCase) {
    this.onlineUseCase = onlineUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OnlineButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<OnlineButtonViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @Override
  public void goOnline() {
    if (disposable != null && !disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    Disposable disposable = onlineUseCase.goOnline()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> holdButton(DURATION_AFTER_SUCCESS, true),
            throwable -> {
              viewStateLiveData.postValue(new OnlineButtonViewStateError(throwable));
              holdButton(DURATION_AFTER_FAIL, false);
            }
        );
    if (this.disposable == null || this.disposable.isDisposed()) {
      this.disposable = disposable;
    }
  }

  private void holdButton(int duration, boolean succeed) {
    disposable = Completable.complete()
        .delay(duration, TimeUnit.SECONDS, Schedulers.io())
        .subscribe(() -> viewStateLiveData.postValue(
            succeed ? new OnlineButtonViewStateProceed() : new OnlineButtonViewStateReady()
        ));
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposable != null) {
      disposable.dispose();
    }
  }
}
