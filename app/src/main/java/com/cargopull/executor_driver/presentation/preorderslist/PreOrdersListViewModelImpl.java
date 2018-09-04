package com.cargopull.executor_driver.presentation.preorderslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrdersUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class PreOrdersListViewModelImpl extends ViewModel implements PreOrdersListViewModel {

  @NonNull
  private final OrdersUseCase ordersUseCase;
  @NonNull
  private final PreOrdersListItemsMapper mapper;
  @NonNull
  private final MutableLiveData<ViewState<PreOrdersListViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable optionsDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<PreOrdersListViewActions> lastViewState;

  @Inject
  public PreOrdersListViewModelImpl(@NonNull OrdersUseCase ordersUseCase,
      @NonNull PreOrdersListItemsMapper mapper) {
    this.ordersUseCase = ordersUseCase;
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
    if (!optionsDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new PreOrdersListViewStatePending(lastViewState));
    optionsDisposable = ordersUseCase.getOrdersList()
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
              if (throwable instanceof DataMappingException) {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              } else {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              }
            }
        );

  }

  @Override
  protected void onCleared() {
    super.onCleared();
    optionsDisposable.dispose();
  }
}
