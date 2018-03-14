package com.fasten.executor_driver.presentation.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.interactor.DataSharer;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class PersistenceViewModelImpl implements PersistenceViewModel {

  @NonNull
  private final DataSharer<ExecutorState> executorStateSharer;

  @NonNull
  private final MutableLiveData<ViewState<PersistenceViewActions>> viewStateLiveData;

  @Nullable
  private Disposable disposable;

  @Inject
  public PersistenceViewModelImpl(@NonNull DataSharer<ExecutorState> executorStateSharer) {
    this.executorStateSharer = executorStateSharer;
    viewStateLiveData = new MutableLiveData<>();
  }

  @NonNull
  @Override
  public LiveData<ViewState<PersistenceViewActions>> getViewStateLiveData() {
    if (disposable == null || disposable.isDisposed()) {
      disposable = executorStateSharer.get()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeExecutorState, throwable -> {
          });
    }
    return viewStateLiveData;
  }

  private void consumeExecutorState(ExecutorState executorState) {
    switch (executorState) {
      case APPROACHING_LOADING_POINT:
        viewStateLiveData.postValue(
            new PersistenceViewStateStart(R.string.executing, R.string.to_loading_point));
        break;
      case APPROACHING_UNLOADING_POINT:
        viewStateLiveData.postValue(
            new PersistenceViewStateStart(R.string.executing, R.string.to_unloading_point));
        break;
      case UNAUTHORIZED:
        viewStateLiveData.postValue(new PersistenceViewStateStop());
        break;
      case CLOSED_SHIFT:
        viewStateLiveData.postValue(new PersistenceViewStateStop());
        break;
      case OPENED_SHIFT:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.online, R.string.no_orders));
        break;
      case READY_FOR_ORDERS:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.online, R.string.wait_for_orders));
        break;
      case LOADING:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.executing, R.string.loading));
        break;
      case UNLOADING:
        viewStateLiveData
            .postValue(new PersistenceViewStateStart(R.string.executing, R.string.unloading));
        break;
    }
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new SingleLiveEvent<>();
  }
}
