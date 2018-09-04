package com.cargopull.executor_driver.presentation.preorderslist;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка предзаказов исполнителя.
 */
public interface PreOrdersListViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showPreOrdersListPending(boolean pending);

  /**
   * Показать список предзаказов.
   *
   * @param show - показать или нет?
   */
  void showPreOrdersList(boolean show);

  /**
   * Передать список моделей предзаказов и заголовков.
   *
   * @param preOrdersListItems - список моделей предзаказов и заголовков.
   */
  void setPreOrdersListItems(@NonNull List<PreOrdersListItem> preOrdersListItems);

  /**
   * Показать сообщение об отсустсвии предзаказов.
   *
   * @param show - показать или нет?
   */
  void showEmptyPreOrdersList(boolean show);
}
