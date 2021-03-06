package com.cargopull.executor_driver.presentation.choosevehicle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.cargopull.executor_driver.presentation.SingleLiveEvent;
import com.cargopull.executor_driver.presentation.ViewState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ChooseVehicleViewModelImpl extends ViewModel implements ChooseVehicleViewModel {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final VehicleChoiceUseCase vehicleChoiceUseCase;
  @NonNull
  private final MutableLiveData<ViewState<ChooseVehicleViewActions>> viewStateLiveData;
  @NonNull
  private final SingleLiveEvent<String> navigateLiveData;
  @NonNull
  private Disposable vehiclesDisposable = EmptyDisposable.INSTANCE;
  @NonNull
  private Disposable choiceDisposable = EmptyDisposable.INSTANCE;

  @Inject
  public ChooseVehicleViewModelImpl(@NonNull ErrorReporter errorReporter,
      @NonNull VehicleChoiceUseCase vehicleChoiceUseCase) {
    this.errorReporter = errorReporter;
    this.vehicleChoiceUseCase = vehicleChoiceUseCase;
    viewStateLiveData = new MutableLiveData<>();
    navigateLiveData = new SingleLiveEvent<>();
    loadVehicles();
  }


  @NonNull
  @Override
  public LiveData<ViewState<ChooseVehicleViewActions>> getViewStateLiveData() {
    return viewStateLiveData;
  }

  @NonNull
  @Override
  public LiveData<String> getNavigationLiveData() {
    return navigateLiveData;
  }

  @Override
  public void selectItem(ChooseVehicleListItem chooseVehicleListItem) {
    if (!choiceDisposable.isDisposed()) {
      return;
    }
    choiceDisposable = vehicleChoiceUseCase.selectVehicle(chooseVehicleListItem.getVehicle())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> navigateLiveData.postValue(ChooseVehicleNavigate.VEHICLE_OPTIONS),
            throwable -> {
              if (throwable instanceof IllegalArgumentException
                  || throwable instanceof EmptyListException
                  || throwable instanceof IndexOutOfBoundsException) {
                errorReporter.reportError(throwable);
              }
            }
        );
  }

  private void loadVehicles() {
    if (!vehiclesDisposable.isDisposed()) {
      return;
    }
    viewStateLiveData.postValue(new ChooseVehicleViewStatePending());
    vehiclesDisposable = vehicleChoiceUseCase.getVehicles()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::consumeVehicles, this::consumeError);
  }

  private void consumeVehicles(List<Vehicle> vehicles) {
    ArrayList<ChooseVehicleListItem> chooseVehicleListItems = new ArrayList<>();
    for (Vehicle vehicle : vehicles) {
      chooseVehicleListItems.add(new ChooseVehicleListItem(vehicle));
    }
    viewStateLiveData.postValue(new ChooseVehicleViewStateReady(chooseVehicleListItems));
  }

  private void consumeError(Throwable error) {
    errorReporter.reportError(error);
    if (error instanceof EmptyListException) {
      viewStateLiveData.postValue(new ChooseVehicleViewStateError(R.string.no_vehicles_message));
    } else {
      viewStateLiveData.postValue(new ChooseVehicleViewStateError(R.string.error));
    }
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    vehiclesDisposable.dispose();
    choiceDisposable.dispose();
  }
}
