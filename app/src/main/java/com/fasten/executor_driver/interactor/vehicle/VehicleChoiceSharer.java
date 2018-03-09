package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.MemoryDataSharer;
import javax.inject.Inject;

public class VehicleChoiceSharer extends MemoryDataSharer<Vehicle> {

  @Inject
  public VehicleChoiceSharer() {
  }
}
