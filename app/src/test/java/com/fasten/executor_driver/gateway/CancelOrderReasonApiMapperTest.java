package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.entity.CancelOrderReason;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class CancelOrderReasonApiMapperTest {

  private Mapper<String, List<CancelOrderReason>> mapper;

  @Before
  public void setUp() {
    mapper = new CancelOrderReasonApiMapper();
  }

  /**
   * Должен успешно преобразовать JSON в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        "["
            + "{\"id\":1,\"description\":\"one\"},"
            + "{\"id\":2,\"description\":\"two\"},"
            + "{\"id\":3,\"description\":\"three\"},"
            + "{\"id\":4,\"description\":\"four\"}"
            + "]"
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
  }

  /**
   * Должен успешно преобразовать JSON с пустыми описаниями в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyNamesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        "["
            + "{\"id\":1,\"description\":\"one\"},"
            + "{\"id\":2,\"description\":\"\"},"
            + "{\"id\":3,\"description\":\"three\"},"
            + "{\"id\":4,\"description\":\"\"}"
            + "]"
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "");
  }

  /**
   * Должен успешно преобразовать JSON с цифрами в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithNumbersToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        "["
            + "{\"id\":1,\"description\":10},"
            + "{\"id\":2,\"description\":20},"
            + "{\"id\":3,\"description\":30},"
            + "{\"id\":4,\"description\":40}"
            + "]"
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "10");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "20");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "30");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "40");
  }

  /**
   * Должен успешно преобразовать JSON без причин в пустой список.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEntriesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map("[]");

    // Результат:
    assertTrue(cancelOrderReasons.isEmpty());
  }

  /**
   * Должен дать ошибку, если какое-либо описание null в JSON.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWitNullNameToCancelOrderReasonsFail() throws Exception {
    // Дано, Действие и Результат:
    mapper.map(
        "["
            + "{\"id\":1,\"description\":\"one\"},"
            + "{\"id\":2,\"description\":null},"
            + "{\"id\":3,\"description\":\"three\"},"
            + "{\"id\":4,\"description\":\"four\"}"
            + "]"
    );
  }

  /**
   * Должен дать ошибку, если нет какого-либо описания в JSON.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingJsonStringWithoutNameToCancelOrderReasonsFail() throws Exception {
    // Дано, Действие и Результат:
    mapper.map(
        "["
            + "{\"id\":1,\"description\":\"one\"},"
            + "{\"id\":2},"
            + "{\"id\":3,\"description\":\"three\"},"
            + "{\"id\":4,\"description\":\"four\"}"
            + "]"
    );
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
  public void mappingObjectFail() throws Exception {
    // Дано и Действие:
    mapper.map("{}");
  }
}