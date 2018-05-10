package com.fasten.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.websocket.incoming.ApiOffer;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.RoutePoint;
import com.google.gson.Gson;
import javax.inject.Inject;

/**
 * Преобразуем статус из ответа сервера в бизнес объект статуса исполнителя.
 */
public class OfferApiMapper implements Mapper<String, Offer> {

  private final Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Inject
  public OfferApiMapper(Mapper<ApiOptionItem, Option> apiOptionMapper) {
    this.apiOptionMapper = apiOptionMapper;
  }

  @NonNull
  @Override
  public Offer map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOffer apiOffer;
    try {
      apiOffer = gson.fromJson(from, ApiOffer.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON!", e);
    }
    if (apiOffer.getRoute() == null) {
      throw new DataMappingException("Ошибка маппинга: маршрут не должен быть null!");
    }
    if (apiOffer.getRoute().isEmpty()) {
      throw new DataMappingException(
          "Ошибка маппинга: маршрут должен содержать хотя бы одну точку!"
      );
    }
    String address = apiOffer.getRoute().get(0).getAddress();
    if (address == null) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть null!");
    }
    if (address.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: адрес не должен быть пустым!");
    }
    if (apiOffer.getExecutorDistance() == null) {
      throw new DataMappingException("Ошибка маппинга: Дистанция не должна быть null!");
    }
    String comment = apiOffer.getRoute().get(0).getComment();
    Offer offer = new Offer(
        apiOffer.getId(),
        apiOffer.getComment() == null ? "" : apiOffer.getComment(),
        apiOffer.getExecutorDistance().getDistance(),
        apiOffer.getEstimatedAmount(),
        // TODO: это костыль, который подменяет таймаут 0 на 20
        apiOffer.getTimeout() == 0 ? 20 : apiOffer.getTimeout(),
        new RoutePoint(
            apiOffer.getRoute().get(0).getLatitude(),
            apiOffer.getRoute().get(0).getLongitude(),
            comment == null ? "" : comment,
            address
        )
    );
    if (apiOffer.getOptions() != null && !apiOffer.getOptions().isEmpty()) {
      for (ApiOptionItem vehicleOptionItem : apiOffer.getOptions()) {
        offer.addOptions(apiOptionMapper.map(vehicleOptionItem));
      }
    }
    return offer;
  }
}
