package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class MessageFcmMapperTest {

  private Mapper<Map<String, String>, String> mapper;

  @Before
  public void setUp() {
    mapper = new MessageFcmMapper();
  }

  /**
   * Должен успешно извлечь пустую строку из null.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractNullStringSuccess() throws Exception {
    // Действие:
    String message = mapper.map(new HashMap<>());

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из мапы.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringSuccess() throws Exception {
    // Действие:
    String message = mapper.map(Collections.singletonMap("body", ""));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 пробела.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutSpaceSuccess() throws Exception {
    // Действие:
    String message = mapper.map(Collections.singletonMap("body", " "));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 переноса строки.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutNewLineSuccess() throws Exception {
    // Действие:
    String message = mapper.map(Collections.singletonMap("body", "\n"));

    // Результат:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringSuccess() throws Exception {
    // Действие:
    String message = mapper.map(Collections.singletonMap("body", "Message test super nova"));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних пробелов и переносов строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringSuccess() throws Exception {
    // Действие:
    String message = mapper
        .map(Collections.singletonMap("body", "  \n Message test super nova  \n "));

    // Результат:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringWithQuotesSuccess() throws Exception {
    // Действие:
    String message = mapper.map(Collections.singletonMap("body", "Message \"test super\" nova"));

    // Результат:
    assertEquals("Message \"test super\" nova", message);
  }
}