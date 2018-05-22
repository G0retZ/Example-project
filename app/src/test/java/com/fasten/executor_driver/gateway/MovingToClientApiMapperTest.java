package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.backend.websocket.incoming.ApiRoutePoint;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.Order;
import com.fasten.executor_driver.entity.RoutePoint;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientApiMapperTest {

  private Mapper<String, Order> mapper;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;
  @Mock
  private Mapper<ApiRoutePoint, RoutePoint> routePointMapper;
  @Mock
  private OptionBoolean optionBoolean;
  @Mock
  private RoutePoint routePoint;
  @Mock
  private RoutePoint routePoint2;

  @Before
  public void setUp() throws Exception {
    when(apiOptionMapper.map(any(ApiOptionItem.class))).thenReturn(optionBoolean);
    when(routePointMapper.map(any(ApiRoutePoint.class))).thenReturn(routePoint, routePoint2);
    mapper = new MovingToClientApiMapper(apiOptionMapper, routePointMapper);
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrder() throws Exception {
    // Дано и Действие:
    Order order = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"optionsMobile\": [\n"
        + "        {\n"
        + "            \"id\": 56,\n"
        + "            \"name\": \"Грузчики\",\n"
        + "            \"value\": \"2\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": 0,\n"
        + "            \"max\": 2,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 55,\n"
        + "            \"name\": \"Ремни крепления\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Имеются стяжные ремни для для фиксации груза.\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 6,\n"
        + "            \"name\": \"Безналичная оплата\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 57,\n"
        + "            \"name\": \"Гидроборт\",\n"
        + "            \"value\": \"1500\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": false,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Поднимающая штуковина\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPrice(), "7000");
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1200);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions(),
        Arrays.asList(optionBoolean, optionBoolean, optionBoolean, optionBoolean)
    );
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа без опций.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderWithoutOptions() throws Exception {
    // Дано и Действие:
    Order order = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPrice(), "7000");
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1200);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions().size(), 0);
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа без дистанции.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOrderWithoutDistance() throws Exception {
    // Дано и Действие:
    Order order = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 0);
    assertEquals(order.getEstimatedPrice(), "7000");
    assertEquals(order.getTimeout(), 25);
    assertEquals(order.getEtaToStartPoint(), 1200);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions().size(), 0);
  }

  /**
   * Должен успешно преобразовать JSON без таймаута.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutTimeoutToOrder() throws Exception {
    // Дано и Действие:
    Order order = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(order.getId(), 7);
    assertEquals(order.getComment(), "com");
    assertEquals(order.getDistance(), 1200239);
    assertEquals(order.getEstimatedPrice(), "7000");
    assertEquals(order.getTimeout(), 0);
    assertEquals(order.getEtaToStartPoint(), 1200);
    assertEquals(order.getConfirmationTime(), 1234567890);
    assertEquals(order.getRoutePath(), Arrays.asList(routePoint, routePoint2, routePoint2));
    assertEquals(order.getOptions().size(), 0);
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано и Действие:
    mapper.map("");
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано и Действие:
    mapper.map("dasie");
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано и Действие:
    mapper.map("12");
  }

  /**
   * Должен дать ошибку, если пришел массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingArrayFail() throws Exception {
    // Дано и Действие:
    mapper.map("[]");
  }

  /**
   * Должен дать ошибку, если маршрута нет.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullRouteFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    }\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если в маршруте нет точек.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyRouteFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если ETA = 0.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingZeroEtaFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"etaToStartPoint\": \"0\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если время подтверждения = 0.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingZeroConfirmationTimeFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"etaToStartPoint\": \"0\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга адреса.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForRoutePointMappingError() throws Exception {
    // Дано
    doThrow(new DataMappingException()).when(routePointMapper).map(any(ApiRoutePoint.class));

    // Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"optionsMobile\": [\n"
        + "        {\n"
        + "            \"id\": 56,\n"
        + "            \"name\": \"Грузчики\",\n"
        + "            \"value\": \"2\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": 0,\n"
        + "            \"max\": 2,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 55,\n"
        + "            \"name\": \"Ремни крепления\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Имеются стяжные ремни для для фиксации груза.\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 6,\n"
        + "            \"name\": \"Безналичная оплата\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 57,\n"
        + "            \"name\": \"Гидроборт\",\n"
        + "            \"value\": \"1500\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": false,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Поднимающая штуковина\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если была ошибка маппинга опции.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingFailForOptionMappingError() throws Exception {
    // Дано
    doThrow(new DataMappingException()).when(apiOptionMapper).map(any(ApiOptionItem.class));

    // Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"timeOut\": \"25\",\n"
        + "    \"etaToStartPoint\": \"1200\",\n"
        + "    \"confirmationTime\": \"1234567890\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"id\":\"7\",\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com1\",\n"
        + "            \"address\":\"add1\",\n"
        + "            \"checked\":\"false\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"8\",\n"
        + "            \"longitude\":\"123\",\n"
        + "            \"latitude\":\"456\",\n"
        + "            \"comment\":\"com2\",\n"
        + "            \"address\":\"add2\",\n"
        + "            \"checked\":\"true\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\":\"9\",\n"
        + "            \"longitude\":\"321\",\n"
        + "            \"latitude\":\"654\",\n"
        + "            \"comment\":\"com3\",\n"
        + "            \"address\":\"add3\",\n"
        + "            \"checked\":\"false\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"optionsMobile\": [\n"
        + "        {\n"
        + "            \"id\": 56,\n"
        + "            \"name\": \"Грузчики\",\n"
        + "            \"value\": \"2\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": 0,\n"
        + "            \"max\": 2,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 55,\n"
        + "            \"name\": \"Ремни крепления\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Имеются стяжные ремни для для фиксации груза.\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 6,\n"
        + "            \"name\": \"Безналичная оплата\",\n"
        + "            \"value\": \"true\",\n"
        + "            \"numeric\": false,\n"
        + "            \"dynamic\": true,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": null\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": 57,\n"
        + "            \"name\": \"Гидроборт\",\n"
        + "            \"value\": \"1500\",\n"
        + "            \"numeric\": true,\n"
        + "            \"dynamic\": false,\n"
        + "            \"min\": null,\n"
        + "            \"max\": null,\n"
        + "            \"description\": \"Поднимающая штуковина\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }
}