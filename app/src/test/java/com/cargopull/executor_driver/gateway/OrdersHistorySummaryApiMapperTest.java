package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersHistorySummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import org.junit.Before;
import org.junit.Test;

public class OrdersHistorySummaryApiMapperTest {

  private Mapper<ApiOrdersHistorySummary, OrdersHistorySummary> mapper;

  @Before
  public void setUp() {
    mapper = new OrdersHistorySummaryApiMapper();
  }


  /**
   * Должен успешно преобразовать сводку истории заказов из АПИ в бизнес-сущность.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingToUnselectedService() throws Exception {
    // Дано и Действие:
    OrdersHistorySummary ordersHistorySummary = mapper
        .map(new ApiOrdersHistorySummary(10, 32, 54, 76));

    // Результат:
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 32);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 76);
    assertEquals(ordersHistorySummary.getMissedOrders(), 54);
  }

}