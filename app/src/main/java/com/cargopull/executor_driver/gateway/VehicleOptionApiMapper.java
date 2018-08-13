package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.entity.Option;
import com.cargopull.executor_driver.entity.OptionBoolean;
import com.cargopull.executor_driver.entity.OptionNumeric;
import javax.inject.Inject;

/**
 * Преобразуем опцию ТС из ответа сервера в бизнес объект опции ТС.
 */
public class VehicleOptionApiMapper implements Mapper<ApiOptionItem, Option> {

  @Inject
  public VehicleOptionApiMapper() {
  }

  @NonNull
  @Override
  public Option map(@NonNull ApiOptionItem from) throws Exception {
    if (from.getValue() == null) {
      throw new DataMappingException("Ошибка маппинга: значение опции не должно быть null!");
    }
    if (from.getName() == null) {
      throw new DataMappingException("Ошибка маппинга: имя опции не должно быть null!");
    }
    Option option;
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
        option = new OptionNumeric(
            from.getId(),
            from.getName(),
            from.getDescription(),
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
      option = new OptionBoolean(
          from.getId(),
          from.getName(),
          from.getDescription(),
          from.isDynamic(),
          Boolean.valueOf(from.getValue())
      );
    }
    return option;
  }
}