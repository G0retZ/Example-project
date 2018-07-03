package com.fasten.executor_driver.presentation.onlinebutton;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.DriverBlockedException;
import com.fasten.executor_driver.entity.NoFreeVehiclesException;
import com.fasten.executor_driver.entity.NoVehiclesAvailableException;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCase;
import com.fasten.executor_driver.presentation.SingleLiveEvent;
import com.fasten.executor_driver.presentation.ViewState;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
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
    viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    loadDisposable = vehiclesAndOptionsUseCase.loadVehiclesAndOptions()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              navigateLiveData.setValue(OnlineButtonNavigate.VEHICLE_OPTIONS);
              holdButton(DURATION_AFTER_SUCCESS);
            },
            throwable -> {
              throwable.printStackTrace();
              if (throwable instanceof DriverBlockedException) {
                navigateLiveData.setValue(OnlineButtonNavigate.DRIVER_BLOCKED);
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
  }

  @Override
  public void consumeError() {
    if (!loadDisposable.isDisposed() || !timerDisposable.isDisposed()) {
      viewStateLiveData.postValue(new OnlineButtonViewStateHold());
    } else {
      viewStateLiveData.postValue(new OnlineButtonViewStateReady());
    }
  }

  private void holdButton(int duration) {
    timerDisposable = Completable.timer(duration, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> {
          if (viewStateLiveData.getValue() instanceof OnlineButtonViewStateHold) {
            viewStateLiveData.postValue(new OnlineButtonViewStateReady());
          }
        }, Throwable::printStackTrace);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    timerDisposable.dispose();
    loadDisposable.dispose();
  }
}
