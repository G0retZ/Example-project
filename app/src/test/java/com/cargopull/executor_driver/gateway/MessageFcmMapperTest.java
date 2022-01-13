package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    // Action:
    String message = mapper.map(new HashMap<>());

    // Effect:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из мапы.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringSuccess() throws Exception {
    // Action:
    String message = mapper.map(Collections.singletonMap("body", ""));

    // Effect:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 пробела.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutSpaceSuccess() throws Exception {
    // Action:
    String message = mapper.map(Collections.singletonMap("body", " "));

    // Effect:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из 1 переноса строки.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutNewLineSuccess() throws Exception {
    // Action:
    String message = mapper.map(Collections.singletonMap("body", "\n"));

    // Effect:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringSuccess() throws Exception {
    // Action:
    String message = mapper.map(Collections.singletonMap("body", "Message test super nova"));

    // Effect:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних пробелов и переносов строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringSuccess() throws Exception {
    // Action:
    String message = mapper
        .map(Collections.singletonMap("body", "  \n Message test super nova  \n "));

    // Effect:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringWithQuotesSuccess() throws Exception {
    // Action:
    String message = mapper.map(Collections.singletonMap("body", "Message \"test super\" nova"));

    // Effect:
    assertEquals("Message \"test super\" nova", message);
  }
}