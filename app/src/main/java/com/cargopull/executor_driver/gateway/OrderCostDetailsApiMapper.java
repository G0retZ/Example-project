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

/**
 * Преобразуем строку из ответа сервера в бизнес объект детального расчета заказа.
 */
public class OrderCostDetailsApiMapper implements Mapper<String, OrderCostDetails> {

  @Inject
  public OrderCostDetailsApiMapper() {

  }

  @NonNull
  @Override
  public OrderCostDetails map(@NonNull String from) throws Exception {
    if (from.isEmpty()) {
      throw new DataMappingException("Ошибка маппинга: данные не должны быть пустыми!");
    }
    Gson gson = new Gson();
    ApiOrderCostDetails apiOrderCostDetails;
    try {
      apiOrderCostDetails = gson.fromJson(from, ApiOrderCostDetails.class);
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
    if (apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverCost() > 0 &&
        apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverCostName() != null) {
      overPackageOptions.add(new Pair<>(
          apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverCostName(),
          apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverCost()
      ));
    }
    List<Pair<String, Long>> overPackageOptionsTariffs = new ArrayList<>();
    if (apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverTariff() > 0 &&
        apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverTariffName() != null) {
      overPackageOptionsTariffs.add(new Pair<>(
          apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverTariffName(),
          apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverTariff()
      ));
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
                    + apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverCost(),
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageCost(),
                overPackageOptions
            ),
        apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff() == 0 ? null :
            new PackageCostDetails(
                0, 0,
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff()
                    + apiOrderCostDetails.getApiOrderOverPackage().getOverPackageMoverTariff(),
                apiOrderCostDetails.getApiOrderOverPackage().getOverPackageTariff(),
                overPackageOptionsTariffs
            )
    );
  }
}
