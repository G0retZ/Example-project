package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiVehicle;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.Vehicle;
import javax.inject.Inject;

/**
 * Преобразуем ТС из ответа сервера в бизнес объект ТС.
 */
public class VehicleApiMapper implements Mapper<ApiVehicle, Vehicle> {

  private final Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Inject
  public VehicleApiMapper(Mapper<ApiOptionItem, Option> apiOptionMapper) {
    this.apiOptionMapper = apiOptionMapper;
  }

  @NonNull
  @Override
  public Vehicle map(@NonNull ApiVehicle from) throws Exception {
    if (from.getLicensePlate() == null) {
      throw new DataMappingException("Ошибка маппинга: гос номер не должен быть null!");
    }
    if (from.getMarkName() == null) {
      throw new DataMappingException("Ошибка маппинга: имя марки не должно быть null!");
    }
    if (from.getModelName() == null) {
      throw new DataMappingException("Ошибка маппинга: имя модели не должно быть null!");
    }
    if (from.getColor() == null) {
      throw new DataMappingException("Ошибка маппинга: имя цвета не должно быть null!");
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
    for (ApiOptionItem vehicleOptionItem : from.getVehicleOptionItems()) {
      vehicle.addVehicleOptions(apiOptionMapper.map(vehicleOptionItem));
    }
    return vehicle;
  }
}
