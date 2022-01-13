package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.entity.RoutePoint;
import com.cargopull.executor_driver.entity.RoutePointState;

import org.junit.Before;
import org.junit.Test;

public class RoutePointApiMapperTest {

  private Mapper<ApiRoutePoint, RoutePoint> mapper;

  @Before
  public void setUp() {
    mapper = new RoutePointApiMapper();
  }

  /**
   * Должен успешно преобразовать в завершенную точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingCompletedSuccess() throws Exception {
    // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", "COMPLETED");

      // Action:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

      // Effect:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "comment");
    assertEquals(routePoint.getAddress(), "address");
    assertEquals(routePoint.getRoutePointState(), RoutePointState.PROCESSED);
  }

  /**
   * Должен успешно преобразовать в активную точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingActiveSuccess() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address",
        "IN_PROGRESS");

      // Action:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

      // Effect:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "comment");
    assertEquals(routePoint.getAddress(), "address");
    assertEquals(routePoint.getRoutePointState(), RoutePointState.ACTIVE);
  }

  /**
   * Должен успешно преобразовать в предстоящую точку маршрута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingQueuedSuccess() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", "WAITING");

      // Action:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

      // Effect:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "comment");
    assertEquals(routePoint.getAddress(), "address");
    assertEquals(routePoint.getRoutePointState(), RoutePointState.QUEUED);
  }

  /**
   * Должен успешно преобразовать в точку маршрута без комментария.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithoutCommentSuccess() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, null, "address", "COMPLETED");

      // Action:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

      // Effect:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "");
    assertEquals(routePoint.getAddress(), "address");
    assertEquals(routePoint.getRoutePointState(), RoutePointState.PROCESSED);
  }

  /**
   * Должен успешно преобразовать в точку маршрута с пустым комментарием.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingWithEmptyCommentSuccess() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "", "address", "COMPLETED");

      // Action:
    RoutePoint routePoint = mapper.map(apiRoutePoint);

      // Effect:
    assertEquals(routePoint.getId(), 7);
    assertEquals(routePoint.getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(routePoint.getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(routePoint.getComment(), "");
    assertEquals(routePoint.getAddress(), "address");
    assertEquals(routePoint.getRoutePointState(), RoutePointState.PROCESSED);
  }

  /**
   * Должен дать ошибку, если адрес - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithoutAddressFail() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", null, "COMPLETED");

      // Action:
    mapper.map(apiRoutePoint);
  }

  /**
   * Должен дать ошибку, если адрес пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithEmptyAddressFail() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "", "COMPLETED");

      // Action:
    mapper.map(apiRoutePoint);
  }

  /**
   * Должен дать ошибку, если статус пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithEmptyStatusFail() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", "");

      // Action:
    mapper.map(apiRoutePoint);
  }

  /**
   * Должен дать ошибку, если статус - нуль.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithNullStatusFail() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", null);

      // Action:
    mapper.map(apiRoutePoint);
  }

  /**
   * Должен дать ошибку, если статус неверный.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingWithWrongStatusFail() throws Exception {
      // Given:
    ApiRoutePoint apiRoutePoint = new ApiRoutePoint(7, 123, 456, "comment", "address", "waiting");

      // Action:
    mapper.map(apiRoutePoint);
  }
}