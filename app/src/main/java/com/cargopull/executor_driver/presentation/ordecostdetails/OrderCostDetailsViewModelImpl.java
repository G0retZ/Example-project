package com.cargopull.executor_driver.presentation.ordecostdetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class OrderCostDetailsViewModelImpl extends ViewModel implements
    OrderCostDetailsViewModel {

  @NonNull
  private final OrderCostDetailsUseCase orderCostDetailsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderCostDetailsViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderCostDetailsViewActions> lastViewState;

  @Inject
  public OrderCostDetailsViewModelImpl(@NonNull OrderCostDetailsUseCase orderCostDetailsUseCase) {
    this.orderCostDetailsUseCase = orderCostDetailsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new OrderCostDetailsViewStatePending(lastViewState));
    loadOrders();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderCostDetailsViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }


  private void loadOrders() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new OrderCostDetailsViewStatePending(lastViewState));
      disposable = orderCostDetailsUseCase.getOrderCostDetails()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOrder,
              throwable -> {
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }


  private void consumeOrder(@NonNull OrderCostDetails orderCostDetails) {
    lastViewState = new OrderCostDetailsViewStateIdle(new OrderCostDetailsItem(orderCostDetails));
    viewStateLiveData.postValue(lastViewState);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
