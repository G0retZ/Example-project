package com.cargopull.executor_driver.interactor.vehicle;

import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.interactor.MemoryDataSharer;
import javax.inject.Inject;

public class VehicleChoiceSharer extends MemoryDataSharer<Vehicle> {

  @Inject
  public VehicleChoiceSharer() {
  }
}
