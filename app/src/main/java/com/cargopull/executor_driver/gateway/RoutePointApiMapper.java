package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;

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
      throw new DataMappingException("Mapping error: address must not be null!");
    }
    if (from.getAddress().isEmpty()) {
      throw new DataMappingException("Mapping error: address must not be empty!");
    }
    if (from.getStatus() == null) {
      throw new DataMappingException("Mapping error: status must not be null!");
    }
    if (from.getStatus().isEmpty()) {
      throw new DataMappingException("Mapping error: status must not be empty!");
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
                "Mapping error: unknown status \"" + from.getStatus() + "\" !");
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
