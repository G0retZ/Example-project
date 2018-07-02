package com.fasten.executor_driver.presentation.calltooperator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class CallToOperatorViewModelImpl extends ViewModel implements CallToOperatorViewModel {

  @NonNull
  private final MutableLiveData<ViewState<CallToOperatorViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public CallToOperatorViewModelImpl() {
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new CallToOperatorViewStateNotCalling());
  }

  @NonNull
  @Override
  public LiveData<ViewState<CallToOperatorViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void callToOperator() {
    if (!disposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new CallToOperatorViewStateCalling());
    disposable = Completable.complete()
        .delay(10, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new CallToOperatorViewStateNotCalling()),
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new CallToOperatorViewStateNotCalling());
            }
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
