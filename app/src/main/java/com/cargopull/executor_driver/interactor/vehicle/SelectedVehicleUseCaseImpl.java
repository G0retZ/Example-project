package com.cargopull.executor_driver.interactor.vehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.DataReceiver;
import io.reactivex.Observable;
import javax.inject.Inject;

public class SelectedVehicleUseCaseImpl implements SelectedVehicleUseCase {

  @NonNull
  private final DataReceiver<Vehicle> vehicleChoiceReceiver;

  @Inject
  public SelectedVehicleUseCaseImpl(@NonNull DataReceiver<Vehicle> vehicleChoiceReceiver) {
    this.vehicleChoiceReceiver = vehicleChoiceReceiver;
  }


  @NonNull
  @Override
  public Observable<Vehicle> getSelectedVehicle() {
    return vehicleChoiceReceiver.get();
  }
}
