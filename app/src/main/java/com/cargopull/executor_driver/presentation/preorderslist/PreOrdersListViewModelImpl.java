package com.cargopull.executor_driver.presentation.preorderslist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrdersUseCase;
import com.cargopull.executor_driver.interactor.SelectedOrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class PreOrdersListViewModelImpl extends ViewModel implements PreOrdersListViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final OrdersUseCase ordersUseCase;
  @NonNull
  private final SelectedOrderUseCase selectedOrderUseCase;
  @NonNull
  private final PreOrdersListItemsMapper mapper;
  @NonNull
  private final MutableLiveData<ViewState<PreOrdersListViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable preOrdersDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable orderSelectionDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<PreOrdersListViewActions> lastViewState;

  @Inject
  public PreOrdersListViewModelImpl(
      @NonNull ErrorReporter errorReporter,
      @NonNull OrdersUseCase ordersUseCase,
      @NonNull SelectedOrderUseCase selectedOrderUseCase,
      @NonNull PreOrdersListItemsMapper mapper) {
    this.errorReporter = errorReporter;
    this.ordersUseCase = ordersUseCase;
    this.selectedOrderUseCase = selectedOrderUseCase;
    this.mapper = mapper;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadPreOrders();
  }

  @NonNull
  @Override
  public LiveData<ViewState<PreOrdersListViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  private void loadPreOrders() {
    if (!preOrdersDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new PreOrdersListViewStatePending(lastViewState));
    preOrdersDisposable = ordersUseCase.getOrdersSet()
        .observeOn(AndroidSchedulers.mainThread())
        .map(mapper)
        .subscribe(
            items -> {
              if (items.isEmpty()) {
                lastViewState = new PreOrdersListViewStateEmpty();
              } else {
                lastViewState = new PreOrdersListViewStateReady(items);
              }
              viewStateLiveData.postValue(lastViewState);
            },
            throwable -> {
              errorReporter.reportError(throwable);
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );

  }

  @Override
  public void setSelectedOrder(Order selectedOrder) {
    orderSelectionDisposable.dispose();
    orderSelectionDisposable = selectedOrderUseCase.setSelectedOrder(selectedOrder)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(PreOrdersListNavigate.PRE_ORDER),
            errorReporter::reportError
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    preOrdersDisposable.dispose();
    orderSelectionDisposable.dispose();
  }
}
