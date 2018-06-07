package com.fasten.executor_driver.presentation.calltooperator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

class CallToOperatorViewModelImpl extends ViewModel implements CallToOperatorViewModel {

  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  CallToOperatorViewModelImpl() {
    navigateLiveData = new SingleLiveEvent<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<CallToOperatorViewActions>> getViewStateLiveData() {
    return new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void callToOperator() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = Completable.complete()
        .delay(10, TimeUnit.SECONDS, Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(CallToOperatorNavigate.FINISHED),
            Throwable::printStackTrace
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
