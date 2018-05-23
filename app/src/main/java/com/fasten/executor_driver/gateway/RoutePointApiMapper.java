package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.fasten.executor_driver.entity.RoutePoint;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class RoutePointApiMapper implements Mapper<ApiRoutePoint, RoutePoint> {

  @Inject
  public RoutePointApiMapper() {
  }

  @NonNull
  @Override
  public RoutePoint map(@NonNull ApiRoutePoint from) throws Exception {
    if (from.getAddress() == null) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть null!");
    }
    if (from.getAddress().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть пустым!");
    }
    return new RoutePoint(
        from.getId(),
        from.getLatitude(),
        from.getLongitude(),
        from.getComment() == null ? "" : from.getComment(),
        from.getAddress(),
        from.isChecked()
    );
  }
}
