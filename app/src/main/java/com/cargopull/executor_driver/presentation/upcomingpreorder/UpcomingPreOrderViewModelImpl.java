package com.cargopull.executor_driver.presentation.upcomingpreorder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCancelledException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.cargopull.executor_driver.gateway.DataMappingException;
import com.cargopull.executor_driver.interactor.OrderUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import javax.inject.Inject;

public class UpcomingPreOrderViewModelImpl extends ViewModel implements
    UpcomingPreOrderViewModel {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final MutableLiveData<ViewState<UpcomingPreOrderViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;

  @Inject
  public UpcomingPreOrderViewModelImpl(@NonNull OrderUseCase orderUseCase) {
    this.orderUseCase = orderUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadOrders();
  }

  @NonNull
  @Override
  public LiveData<ViewState<UpcomingPreOrderViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }


  private void loadOrders() {
    if (disposable.isDisposed()) {
      viewStateLiveData.postValue(new UpcomingPreOrderViewStateUnAvailable());
      disposable = orderUseCase.getOrders()
          .observeOn(AndroidSchedulers.mainThread())
          .doOnError(throwable -> {
            if (throwable instanceof OrderOfferExpiredException
                || throwable instanceof OrderCancelledException
                || throwable instanceof OrderOfferDecisionException) {
              viewStateLiveData.postValue(new UpcomingPreOrderViewStateUnAvailable());
            }
          })
          .retry(throwable -> throwable instanceof OrderOfferExpiredException
              || throwable instanceof OrderCancelledException
              || throwable instanceof OrderOfferDecisionException)
          .subscribe(order -> viewStateLiveData.postValue(new UpcomingPreOrderViewStateAvailable()),
              throwable -> {
                if (throwable instanceof DataMappingException) {
                  navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
                }
              }
          );
    }
  }

  @Override
  public void preOrderConsumed() {
    navigateLiveData.postValue(UpcomingPreOrderNavigate.UPCOMING_PRE_ORDER);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
  }
}
