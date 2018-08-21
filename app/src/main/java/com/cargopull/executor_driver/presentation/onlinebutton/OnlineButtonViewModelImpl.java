package com.cargopull.executor_driver.presentation.onlinebutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class OnlineButtonViewModelImpl extends ViewModel implements OnlineButtonViewModel {

  private static final int DURATION_AFTER_SUCCESS = 5;
  private static final int DURATION_AFTER_FAIL = 5;
  @NonNull
  private final VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase;
  @NonNull
  private final MutableLiveData<ViewState<OnlineButtonViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable loadDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable timerDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public OnlineButtonViewModelImpl(@NonNull VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase) {
    this.vehiclesAndOptionsUseCase = vehiclesAndOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    viewStateLiveData.postValue(new OnlineButtonViewStateReady());
  }

  @NonNull
  @Override
  public LiveData<ViewState<OnlineButtonViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void goOnline() {
    if (!loadDisposable.isDisposed() || !timerDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OnlineButtonViewStateHoldPending());
    loadDisposable = vehiclesAndOptionsUseCase.loadVehiclesAndOptions()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              navigateLiveData.postValue(OnlineButtonNavigate.VEHICLE_OPTIONS);
              holdButton(DURATION_AFTER_SUCCESS);
            },
            throwable -> {
              if (throwable instanceof DriverBlockedException) {
                navigateLiveData.postValue(OnlineButtonNavigate.DRIVER_BLOCKED);
              } else if (throwable instanceof NoSuchElementException) {
                navigateLiveData.postValue(OnlineButtonNavigate.NO_FREE_VEHICLES);
              } else if (throwable instanceof EmptyListException) {
                navigateLiveData.postValue(OnlineButtonNavigate.NO_VEHICLES);
              } else if (throwable instanceof NoNetworkException) {
                navigateLiveData.postValue(CommonNavigate.NO_CONNECTION);
              } else {
                navigateLiveData.postValue(CommonNavigate.SERVER_DATA_ERROR);
              }
              holdButton(DURATION_AFTER_FAIL);
            }
        );
  }

  private void holdButton(int duration) {
    viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    timerDisposable = Completable.timer(duration, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> viewStateLiveData.postValue(new OnlineButtonViewStateReady()),
            throwable -> {
            }
        );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    timerDisposable.dispose();
    loadDisposable.dispose();
  }
}
