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
    if (from.getOption() == null || from.getOption().getName() == null) {
      throw new DataMappingException("Ошибка маппинга: тип опции и его имя не должны быть null!");
    }
    VehicleOption vehicleOption;
    if (from.getOption().isNumeric()) {
      int minValue = 0;
      int maxValue = 0;
      if (from.getOption().isDynamic()) {
        if (from.getLimits() == null) {
          throw new DataMappingException("Ошибка маппинга: пределы не должны быть null!");
        }
        if (from.getLimits().getMinValue() >= from.getLimits().getMaxValue()) {
          throw new DataMappingException(
              "Ошибка маппинга: минимальное значение должно быть меньше максимального!");
        }
        minValue = from.getLimits().getMinValue();
        maxValue = from.getLimits().getMaxValue();
      } else if (from.getLimits() != null) {
        minValue = from.getLimits().getMinValue();
        maxValue = from.getLimits().getMaxValue();
      }
      try {
        vehicleOption = new VehicleOptionNumeric(
            from.getId(),
            from.getOption().getName(),
            from.getOption().isDynamic(),
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
          from.getOption().getName(),
          from.getOption().isDynamic(),
          Boolean.valueOf(from.getValue())
      );
    }
    return vehicleOption;
  }
}
