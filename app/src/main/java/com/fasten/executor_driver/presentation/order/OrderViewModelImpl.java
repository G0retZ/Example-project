package com.fasten.executor_driver.presentation.order;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOrdersAvailableException;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.interactor.OrderUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import com.fasten.executor_driver.utils.TimeUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderViewModelImpl extends ViewModel implements
    OrderViewModel {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderViewActions>> viewStateLiveData;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderViewActions> lastViewState;

  @Inject
  public OrderViewModelImpl(@NonNull OrderUseCase orderUseCase, @NonNull TimeUtils timeUtils) {
    this.orderUseCase = orderUseCase;
    this.timeUtils = timeUtils;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderViewStatePending(lastViewState));
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderViewActions>> getViewStateLiveData() {
    loadOrders();
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }


  private void loadOrders() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new OrderViewStatePending(lastViewState));
      disposable = orderUseCase.getOrders()
          .subscribeOn(Schedulers.single())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::consumeOrder, this::consumeError);
    }
  }


  private void consumeOrder(@NonNull Order order) {
    lastViewState = new OrderViewStateIdle(new OrderItem(order, timeUtils));
    viewStateLiveData.postValue(lastViewState);
  }

  private void consumeError(Throwable throwable) {
    throwable.printStackTrace();
    if (throwable instanceof NoOrdersAvailableException) {
      viewStateLiveData.postValue(new OrderViewStateUnavailableError(lastViewState));
    } else {
      viewStateLiveData.postValue(new OrderViewStateServerDataError(lastViewState));
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }
}
