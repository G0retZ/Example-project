package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;

public class RoutePointApiMapperTest {

  private Mapper<ApiRoutePoint, RoutePoint> mapper;

  @Before
  public void setUp() {
    mapper = new RoutePointApiMapper();
  }

  /**
   * Должен успешно преобразовать в точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingSuccess() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", false);

    // Действие:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

    // Результат:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "comment");
    assertEquals(routePoint.getAddress(), "address");
    assertFalse(routePoint.isChecked());
  }

  /**
   * Должен успешно преобразовать в точку маршрута без комментария.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithoutCommentSuccess() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, null, "address", false);

    // Действие:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

    // Результат:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "");
    assertEquals(routePoint.getAddress(), "address");
    assertFalse(routePoint.isChecked());
  }

  /**
   * Должен успешно преобразовать в точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithEmptyCommentSuccess() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "", "address", false);

    // Действие:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

    // Результат:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "");
    assertEquals(routePoint.getAddress(), "address");
    assertFalse(routePoint.isChecked());
  }

  /**
   * Должен успешно преобразовать в точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingCheckedSuccess() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", true);

    // Действие:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

    // Результат:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "comment");
    assertEquals(routePoint.getAddress(), "address");
    assertTrue(routePoint.isChecked());
  }

  /**
   * Должен дать ошибку, если адрес - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutAddressFail() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", null, true);

    // Действие:
    mapper.map(apiRoutePoint);
  }

  /**
   * Должен дать ошибку, если адрес пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithEmptyAddressFail() throws Exception {
    // Дано:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "", false);

    // Действие:
    mapper.map(apiRoutePoint);
  }
}