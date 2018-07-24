package com.cargopull.executor_driver.presentation.ordercost;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderCostViewModelImpl extends ViewModel implements OrderCostViewModel {

  @NonNull
  private final OrderCurrentCostUseCase orderCurrentCostUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderCostViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public OrderCostViewModelImpl(@NonNull OrderCurrentCostUseCase orderCurrentCostUseCase) {
    this.orderCurrentCostUseCase = orderCurrentCostUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new OrderCostViewState(0));
    loadOrderCosts();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderCostViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadOrderCosts() {
    disposable.dispose();
    disposable = orderCurrentCostUseCase.getOrderCurrentCost()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            integer -> viewStateLiveData.postValue(new OrderCostViewState(integer)),
            throwable -> navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR)
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
