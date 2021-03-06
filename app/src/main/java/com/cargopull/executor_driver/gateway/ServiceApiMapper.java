package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.entity.Service;

import javax.inject.Inject;

/**
 * Преобразуем услугу из ответа сервера в бизнес объект услуги.
 */
public class ServiceApiMapper implements Mapper<ApiServiceItem, Service> {

  @Inject
  public ServiceApiMapper() {
  }

  @NonNull
  @Override
  public Service map(@NonNull ApiServiceItem from) throws Exception {
    if (from.getName() == null) {
      throw new DataMappingException("Mapping error: option name must not be null!");
    }
    if (from.getPrice() == null) {
      throw new DataMappingException("Mapping error: cost value must not be null!");
    }
    return new Service(from.getId(), from.getName(), from.getPrice(), from.isSelected());
  }
}
