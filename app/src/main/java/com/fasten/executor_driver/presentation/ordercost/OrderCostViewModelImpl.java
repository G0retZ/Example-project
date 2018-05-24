package com.fasten.executor_driver.presentation.ordercost;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.OrderCurrentCostUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class OrderCostViewModelImpl extends ViewModel implements OrderCostViewModel {

  private final OrderCurrentCostUseCase orderCurrentCostUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderCostViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  OrderCostViewModelImpl(OrderCurrentCostUseCase orderCurrentCostUseCase) {
    this.orderCurrentCostUseCase = orderCurrentCostUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderCostViewState(0));
    loadOptions();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderCostViewActions>> getViewStateLiveData() {
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
    disposable = orderCurrentCostUseCase.getOrderCurrentCost()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            integer -> viewStateLiveData.postValue(new OrderCostViewStateIdle(integer)),
            throwable -> {
              viewStateLiveData.postValue(new OrderCostViewStateError(0));
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
