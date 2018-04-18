package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import io.reactivex.Completable;
import io.reactivex.Single;
import javax.inject.Inject;

public class LastUsedVehicleGatewayImpl implements LastUsedVehicleGateway {

  @NonNull
  private final AppSettingsService appSettingsService;
  @Nullable
  private Long lastUsedVehicleId;

  @Inject
  public LastUsedVehicleGatewayImpl(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @NonNull
  @Override
  public Single<Long> getLastUsedVehicleId() {
    if (lastUsedVehicleId != null) {
      return Single.just(lastUsedVehicleId);
    }
    return Single.fromCallable(() -> {
      String string = appSettingsService.getData("lastUsedVehicle");
      try {
        return lastUsedVehicleId = Long.parseLong(string);
      } catch (Exception e) {
        e.printStackTrace();
        return lastUsedVehicleId = -1L;
      }
    });
  }

  @NonNull
  @Override
  public Completable saveLastUsedVehicleId(@NonNull Vehicle vehicle) {
    return Completable.fromAction(() -> {
      lastUsedVehicleId = vehicle.getId();
      appSettingsService.saveData("lastUsedVehicle", lastUsedVehicleId.toString());
    });
  }
}
