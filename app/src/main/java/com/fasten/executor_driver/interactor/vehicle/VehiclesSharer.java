package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.MemoryDataSharer;
import java.util.List;
import javax.inject.Inject;

public class VehiclesSharer extends MemoryDataSharer<List<Vehicle>> {

  @Inject
  VehiclesSharer() {
  }
}
