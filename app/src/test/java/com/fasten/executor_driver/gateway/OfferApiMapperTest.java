package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;

public class OfferApiMapperTest {

  private final Offer offer = new Offer(7, "com", 1200239, 7000, 2, 1, 25,
      new RoutePoint(123, 456, "com", "add"));
  private Mapper<String, Offer> mapper;

  @Before
  public void setUp() {
    mapper = new OfferApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в предложение заказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToOffer() throws Exception {
    // Дано и Действие:
    Offer offer1 = mapper.map("{\n"
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
    assertEquals(offer1, offer);
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
    Offer offer1 = mapper.map("{\n"
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
    assertEquals(offer1, new Offer(7, "com", 1200239, 7000, 2, 1, 20,
        new RoutePoint(123, 456, "com", "add")));
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