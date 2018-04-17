package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import com.google.gson.Gson;
import java.util.List;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OfferApiMapper implements Mapper<String, Offer> {

  @Inject
  public OfferApiMapper() {
  }

  @NonNull
  @Override
  public Offer map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    OrderDto orderDto;
    try {
      orderDto = gson.fromJson(from, OrderDto.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    if (orderDto.route == null) {
      throw new DataMappingException("Ошибка маппинга: маршрут не должен быть null!");
    }
    if (orderDto.route.isEmpty()) {
      throw new DataMappingException(
          "Ошибка маппинга: маршрут должен содержать хотя бы одну точку!"
      );
    }
    String address = orderDto.route.get(0).address;
    if (address == null) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть null!");
    }
    if (address.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть пустым!");
    }
    if (orderDto.executorDistance == null) {
      throw new DataMappingException("Ошибка маппинга: Дистанция не должна быть null!");
    }
    String comment = orderDto.route.get(0).comment;
    return new Offer(
        orderDto.id,
        orderDto.comment == null ? "" : orderDto.comment,
        orderDto.executorDistance.distance,
        orderDto.estimatedAmount,
        orderDto.passengers,
        orderDto.porters,
        orderDto.timeout,
        new RoutePoint(
            orderDto.route.get(0).latitude,
            orderDto.route.get(0).longitude,
            comment == null ? "" : comment,
            address
        )
    );
  }

  @SuppressWarnings("unused")
  private class DriverDistancePair {

    int distance;
  }

  @SuppressWarnings("unused")
  private class RPoint {

    double longitude;
    double latitude;
    @Nullable
    String comment;
    @Nullable
    String address;
  }

  @SuppressWarnings("unused")
  private class OrderDto {

    long id;
    long estimatedAmount;
    @Nullable
    String comment;
    int passengers;
    int porters;
    int timeout;
    @Nullable
    DriverDistancePair executorDistance;
    @Nullable
    List<RPoint> route;
  }
}
