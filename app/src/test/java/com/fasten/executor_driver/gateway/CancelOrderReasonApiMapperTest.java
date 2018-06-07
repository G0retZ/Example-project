package com.fasten.executor_driver.gateway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasten.executor_driver.entity.CancelOrderReason;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import ua.naiksoftware.stomp.client.StompMessage;

public class CancelOrderReasonApiMapperTest {

  private Mapper<StompMessage, List<CancelOrderReason>> mapper;

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
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":\"two\",\"name\":\"unused2\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":\"unused4\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertEquals(cancelOrderReasons.get(1).getUnusedName(), "unused2");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
    assertEquals(cancelOrderReasons.get(3).getUnusedName(), "unused4");
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
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":\"\",\"name\":\"unused2\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"\",\"name\":\"unused4\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "");
    assertEquals(cancelOrderReasons.get(1).getUnusedName(), "unused2");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "");
    assertEquals(cancelOrderReasons.get(3).getUnusedName(), "unused4");
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
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":10,\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":20,\"name\":\"unused2\"},"
                + "{\"id\":3,\"description\":30,\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":40,\"name\":\"unused4\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "10");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "20");
    assertEquals(cancelOrderReasons.get(1).getUnusedName(), "unused2");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "30");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "40");
    assertEquals(cancelOrderReasons.get(3).getUnusedName(), "unused4");
  }

  /**
   * Должен успешно преобразовать JSON без причин в пустой список.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutEntriesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper
        .map(new StompMessage("", new ArrayList<>(), "[]"));

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
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":null,\"name\":\"unused2\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":\"unused4\"}"
                + "]"
        )
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
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"name\":\"unused1\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":\"unused4\"}"
                + "]"
        )
    );
  }

  /**
   * Должен успешно преобразовать JSON с пустыми именами в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithEmptyUnusedNamesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":\"two\",\"name\":\"\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":\"\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertEquals(cancelOrderReasons.get(1).getUnusedName(), "");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
    assertEquals(cancelOrderReasons.get(3).getUnusedName(), "");
  }

  /**
   * Должен успешно преобразовать JSON с пустыми именами в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithNumbersInNameToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"1\"},"
                + "{\"id\":2,\"description\":\"two\",\"name\":\"2\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":\"4\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertEquals(cancelOrderReasons.get(1).getUnusedName(), "2");
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
    assertEquals(cancelOrderReasons.get(3).getUnusedName(), "4");
  }

  /**
   * Должен успешно преобразовать JSON с пустыми именами в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithNullUnusedNamesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":\"two\",\"name\":null},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\",\"name\":null}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertNull(cancelOrderReasons.get(1).getUnusedName());
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
    assertNull(cancelOrderReasons.get(3).getUnusedName());
  }

  /**
   * Должен успешно преобразовать JSON с пустыми именами в список причин для отказа.
   *
   * @throws Exception ошибка
   */
  @Test
  public void mappingJsonStringWithoutUnusedNamesToCancelOrderReasonsSuccess() throws Exception {
    // Дано и Действие:
    List<CancelOrderReason> cancelOrderReasons = mapper.map(
        new StompMessage("", new ArrayList<>(),
            "["
                + "{\"id\":1,\"description\":\"one\",\"name\":\"unused1\"},"
                + "{\"id\":2,\"description\":\"two\"},"
                + "{\"id\":3,\"description\":\"three\",\"name\":\"unused3\"},"
                + "{\"id\":4,\"description\":\"four\"}"
                + "]"
        )
    );

    // Результат:
    assertEquals(cancelOrderReasons.size(), 4);
    assertNotNull(cancelOrderReasons.get(0));
    assertEquals(cancelOrderReasons.get(0).getId(), 1);
    assertEquals(cancelOrderReasons.get(0).getName(), "one");
    assertEquals(cancelOrderReasons.get(0).getUnusedName(), "unused1");
    assertNotNull(cancelOrderReasons.get(1));
    assertEquals(cancelOrderReasons.get(1).getId(), 2);
    assertEquals(cancelOrderReasons.get(1).getName(), "two");
    assertNull(cancelOrderReasons.get(1).getUnusedName());
    assertNotNull(cancelOrderReasons.get(2));
    assertEquals(cancelOrderReasons.get(2).getId(), 3);
    assertEquals(cancelOrderReasons.get(2).getName(), "three");
    assertEquals(cancelOrderReasons.get(2).getUnusedName(), "unused3");
    assertNotNull(cancelOrderReasons.get(3));
    assertEquals(cancelOrderReasons.get(3).getId(), 4);
    assertEquals(cancelOrderReasons.get(3).getName(), "four");
    assertNull(cancelOrderReasons.get(3).getUnusedName());
  }

  /**
   * Должен дать ошибку, если строка пустая.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingEmptyFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("", new ArrayList<>(), ""));
  }

  /**
   * Должен дать ошибку, если пришла просто строка.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingStringFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("", new ArrayList<>(), "dasie"));
  }

  /**
   * Должен дать ошибку, если пришло просто число.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingNumberFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("", new ArrayList<>(), "12"));
  }

  /**
   * Должен дать ошибку, если пришел массив.
   *
   * @throws Exception ошибка
   */
  @Test(expected = DataMappingException.class)
  public void mappingObjectFail() throws Exception {
    // Дано и Действие:
    mapper.map(new StompMessage("", new ArrayList<>(), "{}"));
  }
}