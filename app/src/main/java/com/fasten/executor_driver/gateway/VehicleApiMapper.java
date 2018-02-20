package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Преобразуем ТС из ответа сервера в бизнес объект ТС.
 */
public class VehicleApiMapper implements Mapper<ApiVehicle, Vehicle> {

  private final Mapper<ApiVehicleOptionItem, VehicleOption> vehicleOptionMapper;

  @Inject
  VehicleApiMapper(
      @Named("vehicleOptionMapper") Mapper<ApiVehicleOptionItem, VehicleOption> vehicleOptionMapper) {
    this.vehicleOptionMapper = vehicleOptionMapper;
  }

  @NonNull
  @Override
  public Vehicle map(@NonNull ApiVehicle from) throws Exception {
    if (from.getLicensePlate() == null) {
      throw new DataMappingException("Ошибка маппинга: гос номер не должен быть null!");
    }
    if (from.getMarkName() == null) {
      throw new DataMappingException("Ошибка маппинга: марка и ее имя не должны быть null!");
    }
    if (from.getModelName() == null) {
      throw new DataMappingException("Ошибка маппинга: модель и ее имя не должны быть null!");
    }
    if (from.getColor() == null) {
      throw new DataMappingException("Ошибка маппинга: цвет и его имя не должны быть null!");
    }
    if (from.getVehicleOptionItems() == null) {
      throw new DataMappingException("Ошибка маппинга: список опций не должен быть null!");
    }
    Vehicle vehicle = new Vehicle(
        from.getId(),
        from.getMarkName(),
        from.getModelName(),
        from.getColor(),
        from.getLicensePlate(),
        from.isBusy()
    );
    for (ApiVehicleOptionItem vehicleOptionItem : from.getVehicleOptionItems()) {
      vehicle.addVehicleOptions(vehicleOptionMapper.map(vehicleOptionItem));
    }
    return vehicle;
  }
}
