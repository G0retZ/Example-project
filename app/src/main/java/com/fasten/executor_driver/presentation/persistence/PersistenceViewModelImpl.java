package com.fasten.executor_driver.presentation.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PersistenceViewModelImpl implements PersistenceViewModel {

  @NonNull
  private final DataReceiver<ExecutorState> executorStateReceiver;

  @NonNull
  private final MutableLiveData<ViewState<PersistenceViewActions>> viewStateLiveData;

  @Nullable
  private Disposable disposable;

  @Inject
  public PersistenceViewModelImpl(@NonNull DataReceiver<ExecutorState> executorStateReceiver) {
    this.executorStateReceiver = executorStateReceiver;
    viewStateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<PersistenceViewActions>> getViewStateLiveData() {
    loadExecutorState();
    return viewStateLiveData;
  }

  private void loadExecutorState() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = executorStateReceiver.get()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .doAfterTerminate(this::loadExecutorState)
          .subscribe(
              this::consumeExecutorState, throwable -> {
              }
          );
    }
  }

  private void consumeExecutorState(ExecutorState executorState) {
    switch (executorState) {
      case UNAUTHORIZED:
        viewStateLiveData.postValue(new PersistenceViewStateStop());
        break;
      case SHIFT_CLOSED:
        viewStateLiveData.postValue(new PersistenceViewStateStop());
        break;
      case SHIFT_OPENED:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.online, R.string.no_orders));
        break;
      case ONLINE:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders));
        break;
    }
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }
}
