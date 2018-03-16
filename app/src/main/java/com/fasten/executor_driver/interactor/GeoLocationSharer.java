package com.fasten.executor_driver.interactor;

import com.fasten.executor_driver.entity.GeoLocation;
import javax.inject.Inject;

public class GeoLocationSharer extends MemoryDataSharer<GeoLocation> {

  @Inject
  public GeoLocationSharer() {
  }
}
