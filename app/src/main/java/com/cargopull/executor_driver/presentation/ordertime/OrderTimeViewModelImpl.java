package com.cargopull.executor_driver.presentation.ordertime;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderTimeViewModelImpl extends ViewModel implements OrderTimeViewModel {

  @NonNull
  private final OrderFulfillmentTimeUseCase orderCurrentCostUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderTimeViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public OrderTimeViewModelImpl(@NonNull OrderFulfillmentTimeUseCase orderCurrentCostUseCase) {
    this.orderCurrentCostUseCase = orderCurrentCostUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new OrderTimeViewState(0));
    loadOrderTime();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderTimeViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadOrderTime() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = orderCurrentCostUseCase.getOrderElapsedTime()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            time -> viewStateLiveData.postValue(new OrderTimeViewState(time)),
            throwable -> {
              throwable.printStackTrace();
              navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
