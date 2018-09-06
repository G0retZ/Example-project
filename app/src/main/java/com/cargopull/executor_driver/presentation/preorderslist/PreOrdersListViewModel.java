package com.cargopull.executor_driver.presentation.preorderslist;

import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна списка предзаказов исполнителя.
 */
public interface PreOrdersListViewModel extends ViewModel<PreOrdersListViewActions> {

  /**
   * Передает выбранный предзаказ.
   *
   * @param selectedOrder - список сервисов
   */
  void setSelectedOrder(Order selectedOrder);
}
