package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.PersistentDataSharer;
import javax.inject.Inject;

public class LastUsedVehicleSharer extends PersistentDataSharer<Vehicle> {

  @Inject
  public LastUsedVehicleSharer(@NonNull AppSettingsService appSettingsService) {
    super(appSettingsService);
  }

  @NonNull
  @Override
  protected String getKey() {
    return "lastUsedVehicle";
  }

  @Nullable
  @Override
  protected String serialize(Vehicle data) {
    return String.valueOf(data.getId());
  }

  @NonNull
  @Override
  protected Vehicle deserialize(@NonNull String string) {
    return new Vehicle(Long.parseLong(string), "m", "m", "c", "l", false);
  }
}
