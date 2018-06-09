package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.fasten.executor_driver.entity.RoutePoint;
import com.fasten.executor_driver.entity.RoutePointState;
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
    if (from.getStatus() == null) {
      throw new DataMappingException("Ошибка маппинга: статус не должен быть null!");
    }
    if (from.getStatus().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: статус не должен быть пустым!");
    }
    RoutePointState routePointState;
    switch (from.getStatus()) {
      case "COMPLETED":
        routePointState = RoutePointState.PROCESSED;
        break;
      case "IN_PROGRESS":
        routePointState = RoutePointState.ACTIVE;
        break;
      case "WAITING":
        routePointState = RoutePointState.QUEUED;
        break;
      default:
        throw new DataMappingException(
            "Ошибка маппинга: неизвестный статус \"" + from.getStatus() + "\" !");
    }
    return new RoutePoint(
        from.getIndex(),
        from.getLatitude(),
        from.getLongitude(),
        from.getComment() == null ? "" : from.getComment(),
        from.getAddress(),
        routePointState
    );
  }
}
