package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataReceiver;
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
