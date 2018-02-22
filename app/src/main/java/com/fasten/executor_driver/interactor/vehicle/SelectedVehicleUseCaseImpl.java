package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.DataSharer;
import io.reactivex.Observable;
import javax.inject.Inject;
import javax.inject.Named;

public class SelectedVehicleUseCaseImpl implements SelectedVehicleUseCase {

  @NonNull
  private final DataSharer<Vehicle> vehicleChoiceSharer;

  @Inject
  SelectedVehicleUseCaseImpl(
      @Named("vehicleChoiceSharer") @NonNull DataSharer<Vehicle> vehicleChoiceSharer) {
    this.vehicleChoiceSharer = vehicleChoiceSharer;
  }


  @NonNull
  @Override
  public Observable<Vehicle> getSelectedVehicle() {
    return vehicleChoiceSharer.get();
  }
}
