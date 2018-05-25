package com.fasten.executor_driver.presentation.nextroutepoint;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.interactor.OrderRouteUseCase;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import javax.inject.Inject;

public class NextRoutePointViewModelImpl extends ViewModel implements NextRoutePointViewModel {

  @NonNull
  private final OrderRouteUseCase orderRouteUseCase;
  @NonNull
  private final MutableLiveData<ViewState<NextRoutePointViewActions>> viewStateLiveData;
  @NonNull
  private Disposable disposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable closeRoutePointDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private RoutePointItem lastRoutePointItem;

  @Inject
  public NextRoutePointViewModelImpl(@NonNull OrderRouteUseCase orderRouteUseCase) {
    this.orderRouteUseCase = orderRouteUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new NextRoutePointViewStatePending(
        lastRoutePointItem = new RoutePointItem(Collections.singletonList(
            new RoutePoint(0, 0, 0, "", "", false)
        ))
    ));
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
    return new MutableLiveData<>();
  }

  @Override
  public void closeRoutePoint() {
    if (!closeRoutePointDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new NextRoutePointViewStatePending(lastRoutePointItem));
    closeRoutePointDisposable = orderRouteUseCase
        .closeRoutePoint(lastRoutePointItem.getRoutePoint())
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new NextRoutePointViewStateError(lastRoutePointItem));
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
            routePoints -> viewStateLiveData.postValue(new NextRoutePointViewStateIdle(
                lastRoutePointItem = new RoutePointItem(routePoints)
            )),
            throwable -> {
              throwable.printStackTrace();
              viewStateLiveData.postValue(new NextRoutePointViewStateError(lastRoutePointItem));
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    disposable.dispose();
    closeRoutePointDisposable.dispose();
  }
}
