package com.fasten.executor_driver.presentation.ordertime;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.OrderFulfillmentTimeUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class OrderTimeViewModelImpl extends ViewModel implements OrderTimeViewModel {

  @NonNull
  private final OrderFulfillmentTimeUseCase orderCurrentCostUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderTimeViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  OrderTimeViewModelImpl(@NonNull OrderFulfillmentTimeUseCase orderCurrentCostUseCase) {
    this.orderCurrentCostUseCase = orderCurrentCostUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderTimeViewState(0));
    loadOptions();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderTimeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  private void loadOptions() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = orderCurrentCostUseCase.getOrderElapsedTime()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            time -> viewStateLiveData.postValue(new OrderTimeViewStateIdle(time)),
            throwable -> {
              viewStateLiveData.postValue(new OrderTimeViewStateError(0));
              throwable.printStackTrace();
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
