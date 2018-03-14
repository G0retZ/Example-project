package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.GeoLocation;
import javax.inject.Inject;

class GeoLocationSharer extends MemoryDataSharer<GeoLocation> {

  @Inject
  GeoLocationSharer() {
  }
}
