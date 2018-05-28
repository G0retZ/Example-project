package com.fasten.executor_driver.presentation.orderroute;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.interactor.OrderRouteUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

class OrderRouteViewModelImpl extends ViewModel implements
    OrderRouteViewModel {

  @NonNull
  private final OrderRouteUseCase orderRouteUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OrderRouteViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable nextPointDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<OrderRouteViewActions> lastViewState;

  @Inject
  OrderRouteViewModelImpl(@NonNull OrderRouteUseCase orderRouteUseCase) {
    this.orderRouteUseCase = orderRouteUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OrderRouteViewStatePending(lastViewState));
    loadRoutePoints();
  }

  @NonNull
  @Override
  public LiveData<ViewState<OrderRouteViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return new MutableLiveData<>();
  }

  @Override
  public void selectNextRoutePoint(RoutePointItem routePointItem) {
    if (!nextPointDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OrderRouteViewStatePending(lastViewState));
    nextPointDisposable = orderRouteUseCase
        .nextRoutePoint(routePointItem.getRoutePoint())
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            },
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(lastViewState);
            }
        );
  }

  private void loadRoutePoints() {
    if (!disposable.isDisposed()) {
      return;
    }
    disposable = orderRouteUseCase.getOrderRoutePoints()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .switchMap(routePoints -> Flowable
            .fromIterable(routePoints)
            .map(RoutePointItem::new)
            .toList()
            .map(RoutePointItems::new)
            .toFlowable()
        )
        .subscribe(
            routePointItems -> {
              nextPointDisposable.dispose();
              viewStateLiveData.postValue(lastViewState = new OrderRouteViewState(routePointItems));
            },
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new OrderRouteViewStateError(lastViewState));
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
    nextPointDisposable.dispose();
  }
}
