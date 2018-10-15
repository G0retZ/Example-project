package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiOrderCostDetails;
import com.cargopull.executor_driver.backend.websocket.incoming.ApiOrderOptionCost;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import com.cargopull.executor_driver.entity.PackageCostDetails;
import com.cargopull.executor_driver.utils.Pair;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Преобразуем строку из ответа сервера в бизнес объект детального расчета заказа.
 */
public class OrderCostDetailsApiMapper implements Mapper<StompMessage, OrderCostDetails> {

  @Inject
  public OrderCostDetailsApiMapper() {
  }

  @NonNull
  @Override
  public OrderCostDetails map(@NonNull StompMessage from) throws Exception {
    if (from.getPayload() == null) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть null!");
    }
    if (from.getPayload().trim().isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrderCostDetails apiOrderCostDetails;
    try {
      apiOrderCostDetails = gson.fromJson(from.getPayload(), ApiOrderCostDetails.class);
    } catch (Exception e) {
      throw new DataMappingException("Ошибка маппинга: не удалось распарсить JSON: " + from, e);
    }
    if (apiOrderCostDetails.getApiOrderOverPackage() == null) {
      throw new DataMappingException("Ошибка маппинга: Нет данных детализации и сверх пакета!");
    }
    if (apiOrderCostDetails.getApiOrderOptionsCostDetails() == null) {
      throw new DataMappingException("Ошибка маппинга: Нет данных детализации по опциям!");
    }
    List<Pair<String, Long>> estimatedOptions = new ArrayList<>();
    if (apiOrderCostDetails.getApiOrderOptionsCostDetails().getOptionsCosts() != null) {
      for (ApiOrderOptionCost apiOrderOptionCost :
          apiOrderCostDetails.getApiOrderOptionsCostDetails().getOptionsCosts()) {
        if (apiOrderOptionCost.getOptionName() == null) {
          throw new DataMappingException("Ошибка маппинга: Имя опциии не должно быть нуль!");
        }
        estimatedOptions.add(new Pair<>(
            apiOrderOptionCost.getOptionName(),
            apiOrderOptionCost.getOptionPrice()
        ));
      }
    }
    List<Pair<String, Long>> overPackageOptions = new ArrayList<>();
    String overPackageMoverCostName = apiOrderCostDetails.getApiOrderOverPackage()
        .getOverPackageMoverCostName();
    long overPackageMoverCost = apiOrderCostDetails.getApiOrderOverPackage()
        .getOverPackageMoverCost();
    if (overPackageMoverCost > 0 && overPackageMoverCostName != null) {
      overPackageOptions.add(new Pair<>(overPackageMoverCostName, overPackageMoverCost));
    }
    List<Pair<String, Long>> overPackageOptionsTariffs = new ArrayList<>();
    String overPackageMoverTariffName = apiOrderCostDetails.getApiOrderOverPackage()
        .getOverPackageMoverTariffName();
    long overPackageMoverTariff = apiOrderCostDetails.getApiOrderOverPackage()
        .getOverPackageMoverTariff();
    if (overPackageMoverTariff > 0 && overPackageMoverTariffName != null) {
      overPackageOptionsTariffs.add(new Pair<>(overPackageMoverTariffName, overPackageMoverTariff));
    }
    return new OrderCostDetails(
        apiOrderCostDetails.getTotalAmount(),
        apiOrderCostDetails.getEstimatedAmount() == 0 ? null :
            new PackageCostDetails(
                apiOrderCostDetails.getEstimatedTime(),
                apiOrderCostDetails.getEstimatedRouteDistance(),
                apiOrderCostDetails.getEstimatedAmount(),
                apiOrderCostDetails.getApiOrderOverPackage().getEstimatedPackageCost(),
                estimatedOptions
            ),
        apiOrderCostDetails.getApiOrderOverPackage().getOverPackageCost() == 0 ? null :
            new PackageCostDetails(
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTime(),
                0,
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageCost()
                    + (overPackageMoverCostName == null ? 0 : overPackageMoverCost),
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageCost(),
                overPackageOptions
            ),
        apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff() == 0 ? null :
            new PackageCostDetails(
                0, 0,
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff()
                    + (overPackageMoverTariffName == null ? 0 : overPackageMoverTariff),
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff(),
                overPackageOptionsTariffs
            )
    );
  }
}
