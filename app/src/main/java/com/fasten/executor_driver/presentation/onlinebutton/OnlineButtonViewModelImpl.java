package com.fasten.executor_driver.presentation.onlinebutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.InsufficientCreditsException;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
  @Nullable
  private Disposable loadDisposable;
  @Nullable
  private Disposable timerDisposable;

  @Inject
  public OnlineButtonViewModelImpl(@NonNull VehiclesAndOptionsUseCase vehiclesAndOptionsUseCase) {
    this.vehiclesAndOptionsUseCase = vehiclesAndOptionsUseCase;
    viewStateLiveData = new MutableLiveData<>();
    viewStateLiveData.postValue(new OnlineButtonViewStateReady());
    navigateLiveData = new SingleLiveEvent<>();
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
    if ((loadDisposable != null && !loadDisposable.isDisposed())
        || timerDisposable != null && !timerDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    Disposable disposable = vehiclesAndOptionsUseCase.loadVehiclesAndOptions()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              navigateLiveData.setValue(OnlineButtonNavigate.VEHICLE_OPTIONS);
              holdButton(DURATION_AFTER_SUCCESS);
            },
            throwable -> {
              if (throwable instanceof DriverBlockedException) {
                navigateLiveData.setValue(OnlineButtonNavigate.DRIVER_BLOCKED);
              } else if (throwable instanceof InsufficientCreditsException) {
                navigateLiveData.setValue(OnlineButtonNavigate.INSUFFICIENT_CREDITS);
              } else if (throwable instanceof NoFreeVehiclesException) {
                navigateLiveData.setValue(OnlineButtonNavigate.NO_FREE_VEHICLES);
              } else if (throwable instanceof NoVehiclesAvailableException) {
                navigateLiveData.setValue(OnlineButtonNavigate.NO_VEHICLES);
              } else {
                viewStateLiveData.postValue(new OnlineButtonViewStateError(throwable));
              }
              holdButton(DURATION_AFTER_FAIL);
            }
        );
    if (this.loadDisposable == null || this.loadDisposable.isDisposed()) {
      this.loadDisposable = disposable;
    }
  }

  @Override
  public void consumeError() {
    if ((loadDisposable != null && !loadDisposable.isDisposed())
        || timerDisposable != null && !timerDisposable.isDisposed()) {
      viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    } else {
      viewStateLiveData.postValue(new OnlineButtonViewStateReady());
    }
  }

  private void holdButton(int duration) {
    timerDisposable = Completable.complete()
        .delay(duration, TimeUnit.SECONDS, Schedulers.io())
        .subscribe(() -> {
          if (viewStateLiveData.getValue() instanceof OnlineButtonViewStateHold) {
            viewStateLiveData.postValue(new OnlineButtonViewStateReady());
          }
        }, Throwable::printStackTrace);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (timerDisposable != null) {
      timerDisposable.dispose();
    }
    if (loadDisposable != null) {
      loadDisposable.dispose();
    }
  }
}
