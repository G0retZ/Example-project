package com.fasten.executor_driver.interactor.vehicle;

import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.interactor.MemoryDataSharer;
import java.util.List;
import javax.inject.Inject;

public class DriverOptionsSharer extends MemoryDataSharer<List<Option>> {

  @Inject
  DriverOptionsSharer() {
  }
}
