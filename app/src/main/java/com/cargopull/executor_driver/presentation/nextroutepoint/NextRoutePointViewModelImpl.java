package com.cargopull.executor_driver.presentation.nextroutepoint;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;
import com.cargopull.executor_driver.interactor.OrderRouteUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class NextRoutePointViewModelImpl extends ViewModel implements NextRoutePointViewModel {

  @NonNull
  private final OrderRouteUseCase orderRouteUseCase;
  @NonNull
  private final MutableLiveData<ViewState<NextRoutePointViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable completeDisposable = EmptyDisposable.INSTANCE;
  @Nullable
  private ViewState<NextRoutePointViewActions> lastViewState;
  @Nullable
  private RoutePoint lastRoutePoint;

  @Inject
  public NextRoutePointViewModelImpl(@NonNull OrderRouteUseCase orderRouteUseCase) {
    this.orderRouteUseCase = orderRouteUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new NextRoutePointViewStatePending(null));
    loadRoutePoints();
  }

  @NonNull
  @Override
  public LiveData<ViewState<NextRoutePointViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void closeRoutePoint() {
    if (!completeDisposable.isDisposed() || lastRoutePoint == null) {
      return;
    }
    viewStateLiveData.postValue(new NextRoutePointViewStatePending(lastViewState));
    completeDisposable = orderRouteUseCase
        .closeRoutePoint(lastRoutePoint)
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              viewStateLiveData.postValue(lastViewState);
              navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
            }
        );
  }

  @Override
  public void completeTheOrder() {
    if (!completeDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new NextRoutePointViewStatePending(lastViewState));
    completeDisposable = orderRouteUseCase
        .completeTheOrder()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              viewStateLiveData.postValue(lastViewState);
              navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
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
        .subscribe(
            routePoints -> {
              completeDisposable.dispose();
              for (RoutePoint routePoint : routePoints) {
                lastRoutePoint = routePoint;
                if (lastRoutePoint.getRoutePointState() == RoutePointState.ACTIVE) {
                  viewStateLiveData.postValue(lastViewState = new NextRoutePointViewStateEnRoute(
                      new RoutePointItem(lastRoutePoint)
                  ));
                  return;
                }
              }
              viewStateLiveData.postValue(
                  lastViewState = new NextRoutePointViewStateNoRoute(routePoints.size() < 2)
              );
            },
            throwable -> navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR)
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
    completeDisposable.dispose();
  }
}
