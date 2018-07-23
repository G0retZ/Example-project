package com.cargopull.executor_driver.presentation.ordecostdetails;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.utils.Pair;
import java.util.List;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrderCostDetailsViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showOrderCostDetailsPending(boolean pending);

  /**
   * Показать общую стоимость заказа.
   *
   * @param totalCost - общая стоимость заказа
   */
  void showOrderTotalCost(long totalCost);

  /**
   * Показать пакет предрасчета.
   *
   * @param show - показать или нет?
   */
  void showEstimatedOrderPackage(boolean show);

  /**
   * Показать предрасчетную стоимость заказа.
   *
   * @param cost - стоимость заказа
   */
  void showEstimatedOrderCost(long cost);

  /**
   * Показать предрасчетное время заказа.
   *
   * @param time - время заказа в милисекундах
   */
  void showEstimatedOrderTime(long time);

  /**
   * Показать предрасчетное растояние заказа.
   *
   * @param distance - расстояние
   */
  void showEstimatedOrderDistance(@Nullable String distance);

  /**
   * Показать предрасчетную стоимость услуги заказа.
   *
   * @param cost - стоимость услуги заказа
   */
  void showEstimatedOrderServiceCost(long cost);

  /**
   * Показать предрасчетные стоимости опций заказа.
   *
   * @param optionsCosts - стоимости опций заказа
   */
  void showEstimatedOrderOptionsCosts(@NonNull List<Pair<String, Integer>> optionsCosts);

  /**
   * Показать пакет превышения.
   *
   * @param show - показать или нет?
   */
  void showOverPackage(boolean show);

  /**
   * Показать стоимость превышенния заказа.
   *
   * @param cost - стоимость превышенния заказа
   */
  void showOverPackageCost(long cost);

  /**
   * Показать время превышенния заказа.
   *
   * @param time - время заказа в милисекундах
   */
  void showOverPackageTime(long time);

  /**
   * Показать стоимость услуги превышенния заказа.
   *
   * @param cost - стоимость услуги заказа
   */
  void showOverPackageServiceCost(long cost);

  /**
   * Показать стоимости опций превышенния заказа.
   *
   * @param optionsCosts - стоимости опций заказа
   */
  void showOverPackageOptionsCosts(@NonNull List<Pair<String, Integer>> optionsCosts);

  /**
   * Показать тарифы превышения.
   *
   * @param show - показать или нет?
   */
  void showOverPackageTariff(boolean show);

  /**
   * Показать тариф превышенния заказа.
   *
   * @param tariff - тариф превышенния заказа
   */
  void showOverPackageTariffCost(long tariff);

  /**
   * Показать тариф услуги превышенния заказа.
   *
   * @param tariff - стоимость услуги заказа
   */
  void showOverPackageServiceTariff(long tariff);

  /**
   * Показать тарифы опций превышенния заказа.
   *
   * @param optionsTariffs - стоимости опций заказа
   */
  void showOverPackageOptionsTariffs(@NonNull List<Pair<String, Integer>> optionsTariffs);
}
