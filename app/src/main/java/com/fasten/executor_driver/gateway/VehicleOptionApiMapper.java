package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.entity.VehicleOptionBoolean;
import com.fasten.executor_driver.entity.VehicleOptionNumeric;
import javax.inject.Inject;

/**
 * Преобразуем опцию ТС из ответа сервера в бизнес объект опции ТС.
 */
public class VehicleOptionApiMapper implements Mapper<ApiVehicleOptionItem, VehicleOption> {

  @Inject
  VehicleOptionApiMapper() {
  }

  @NonNull
  @Override
  public VehicleOption map(@NonNull ApiVehicleOptionItem from) throws Exception {
    if (from.getValue() == null) {
      throw new DataMappingException("Ошибка маппинга: значение опции не должно быть null!");
    }
    if (from.getName() == null) {
      throw new DataMappingException("Ошибка маппинга: имя опции не должно быть null!");
    }
    VehicleOption vehicleOption;
    if (from.isNumeric()) {
      int minValue = 0;
      int maxValue = 0;
      if (from.isDynamic()) {
        if (from.getMinValue() == null || from.getMaxValue() == null) {
          throw new DataMappingException("Ошибка маппинга: пределы не должны быть null!");
        }
        if (from.getMinValue() >= from.getMaxValue()) {
          throw new DataMappingException(
              "Ошибка маппинга: минимальное значение должно быть меньше максимального!");
        }
        minValue = from.getMinValue();
        maxValue = from.getMaxValue();
      } else if (from.getMinValue() != null && from.getMaxValue() != null) {
        minValue = from.getMinValue();
        maxValue = from.getMaxValue();
      }
      try {
        vehicleOption = new VehicleOptionNumeric(
            from.getId(),
            from.getName(),
            from.isDynamic(),
            Integer.valueOf(from.getValue()),
            minValue,
            maxValue
        );
      } catch (NumberFormatException nfe) {
        throw new DataMappingException("Ошибка маппинга: неверный формат числового значения!", nfe);
      }
    } else {
      if (!from.getValue().equalsIgnoreCase("true") && !from.getValue().equalsIgnoreCase("false")) {
        throw new DataMappingException("Ошибка маппинга: неверный формат двоичного значения!");
      }
      vehicleOption = new VehicleOptionBoolean(
          from.getId(),
          from.getName(),
          from.isDynamic(),
          Boolean.valueOf(from.getValue())
      );
    }
    return vehicleOption;
  }
}
