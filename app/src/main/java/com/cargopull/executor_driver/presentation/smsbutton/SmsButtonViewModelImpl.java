package com.cargopull.executor_driver.presentation.smsbutton;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.interactor.auth.CodeUseCase;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class SmsButtonViewModelImpl extends ViewModel implements SmsButtonViewModel {

  @NonNull
  private final CodeUseCase codeUseCase;
  @NonNull
  private final MutableLiveData<ViewState<FragmentViewActions>> viewStateLiveData;
  @NonNull
  private Disposable sendDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timerDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public SmsButtonViewModelImpl(@NonNull CodeUseCase codeUseCase) {
    this.codeUseCase = codeUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new SmsButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<FragmentViewActions
      >> getViewStateLiveData() {
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
    sendDisposable = codeUseCase.sendMeCode()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            this::holdButton,
            throwable -> viewStateLiveData.postValue(new SmsButtonViewStateError())
        );
  }

  private void holdButton(int duration) {
    timerDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
        .take(duration)
        .map(count -> duration - count)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            count -> viewStateLiveData.postValue(new SmsButtonViewStateHold(count)),
            Throwable::printStackTrace,
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
