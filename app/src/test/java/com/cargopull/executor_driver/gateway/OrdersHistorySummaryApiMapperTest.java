package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class OrdersHistorySummaryApiMapperTest {

  private Mapper<Map<String, ApiOrdersSummary>, OrdersHistorySummary> mapper;

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
  public void mappingToOrdersHistorySummary() throws Exception {
    // Given и Action:
    OrdersHistorySummary ordersHistorySummary = mapper
        .map(new HashMap<String, ApiOrdersSummary>() {{
          put("successOrders", new ApiOrdersSummary(0, 10));
          put("refusedOrders", new ApiOrdersSummary(1, 32));
          put("skippedOrders", new ApiOrdersSummary(2, 54));
          put("cancelledOrders", new ApiOrdersSummary(3, 76));
        }});

    // Effect:
    assertEquals(ordersHistorySummary.getCompletedOrders(), 10);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 32);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 76);
    assertEquals(ordersHistorySummary.getMissedOrders(), 54);
  }

  /**
   * Должен успешно преобразовать сводку истории заказов из АПИ в бизнес-сущность.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingEmptyMapToOrdersHistorySummary() throws Exception {
    // Given и Action:
    OrdersHistorySummary ordersHistorySummary = mapper.map(new HashMap<>());

    // Effect:
    assertEquals(ordersHistorySummary.getCompletedOrders(), 0);
    assertEquals(ordersHistorySummary.getRejectedOrders(), 0);
    assertEquals(ordersHistorySummary.getCancelledOrders(), 0);
    assertEquals(ordersHistorySummary.getMissedOrders(), 0);
  }
}