package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OfferApiMapperTest {

  private Mapper<String, Offer> mapper;

  @Mock
  private Mapper<ApiOptionItem, Option> apiOptionMapper;

  @Before
  public void setUp() throws Exception {
    when(apiOptionMapper.map(any(ApiOptionItem.class)))
        .thenReturn(new OptionBoolean(0, "n", "d", false, false));
    mapper = new OfferApiMapper(apiOptionMapper);
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOffer() throws Exception {
    // Дано и Действие:
    Offer offer = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\",\n"
        + "            \"address\":\"add\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"options\": [\n"
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
    assertEquals(offer.getId(), 7);
    assertEquals(offer.getComment(), "com");
    assertEquals(offer.getDistance(), 1200239);
    assertEquals(offer.getEstimatedPrice(), "7000");
    assertEquals(offer.getTimeout(), 25);
    assertEquals(offer.getRoutePoint().getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getComment(), "com");
    assertEquals(offer.getRoutePoint().getAddress(), "add");
    assertEquals(offer.getOptions(), Arrays.asList(
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false),
        new OptionBoolean(0, "n", "d", false, false)
    ));
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа без опций.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOfferWithoutOptions() throws Exception {
    // Дано и Действие:
    Offer offer = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\",\n"
        + "            \"address\":\"add\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(offer.getId(), 7);
    assertEquals(offer.getComment(), "com");
    assertEquals(offer.getDistance(), 1200239);
    assertEquals(offer.getEstimatedPrice(), "7000");
    assertEquals(offer.getTimeout(), 25);
    assertEquals(offer.getRoutePoint().getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getComment(), "com");
    assertEquals(offer.getRoutePoint().getAddress(), "add");
    assertEquals(offer.getOptions().size(), 0);
  }

  /**
   * Должен успешно преобразовать JSON без таймаута в предложение заказа с таймаутом 20.
   *
   * @throws Exception ошибка
   */
  // TODO: это костыль, который подменяет таймаут 0 на 20
  @Test
  public void mappingJsonStringWithoutTimeoutToOffer() throws Exception {
    // Дано и Действие:
    Offer offer = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\",\n"
        + "            \"address\":\"add\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");

    // Результат:
    assertEquals(offer.getId(), 7);
    assertEquals(offer.getComment(), "com");
    assertEquals(offer.getDistance(), 1200239);
    assertEquals(offer.getEstimatedPrice(), "7000");
    assertEquals(offer.getTimeout(), 20);
    assertEquals(offer.getRoutePoint().getLatitude(), 123, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getLongitude(), 456, Double.MIN_VALUE);
    assertEquals(offer.getRoutePoint().getComment(), "com");
    assertEquals(offer.getRoutePoint().getAddress(), "add");
    assertEquals(offer.getOptions().size(), 0);
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
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
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
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если нет адреса.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullAddressFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если адрес пустой.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyAddressFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"executorDistance\": {\n"
        + "        \"executorId\": \"5\",\n"
        + "        \"distance\": \"1200239\"\n"
        + "    },\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\",\n"
        + "            \"address\":\"\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }

  /**
   * Должен дать ошибку, если дистанция null.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNullStatusHeaderFail() throws Exception {
    // Дано и Действие:
    mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"25\",\n"
        + "    \"route\": [\n"
        + "        {\n"
        + "            \"longitude\":\"456\",\n"
        + "            \"latitude\":\"123\",\n"
        + "            \"comment\":\"com\",\n"
        + "            \"address\":\"add\"\n"
        + "        }\n"
        + "    ]\n"
        + "}");
  }
}