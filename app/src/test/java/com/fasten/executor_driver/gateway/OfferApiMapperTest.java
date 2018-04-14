package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.RoutePoint;
import org.junit.Before;
import org.junit.Test;

public class OfferApiMapperTest {

  private Mapper<String, Offer> mapper;
  private Offer offer = new Offer(7, "com", 1200239, 7000, 2, 1, 20,
      new RoutePoint(123, 456, "com", "add"));

  @Before
  public void setUp() {
    mapper = new OfferApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в статус "смена закрыта".
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingStringToOffer() throws Exception {
    // Дано и Действие:
    Offer offer1 = mapper.map("{\n"
        + "    \"id\": \"7\",\n"
        + "    \"comment\": \"com\",\n"
        + "    \"estimatedAmount\": \"7000\",\n"
        + "    \"passengers\": \"2\",\n"
        + "    \"porters\": \"1\",\n"
        + "    \"timeout\": \"20\",\n"
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
        + "    \"timeout\": \"20\",\n"
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
        + "    \"timeout\": \"20\",\n"
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
        + "    \"timeout\": \"20\",\n"
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
        + "    \"timeout\": \"20\",\n"
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
        + "    \"timeout\": \"20\",\n"
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