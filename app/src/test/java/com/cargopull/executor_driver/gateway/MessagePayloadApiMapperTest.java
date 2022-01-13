package com.cargopull.executor_driver.gateway;

import static org.junit.Assert.assertEquals;

import com.cargopull.executor_driver.backend.stomp.Command;
import com.cargopull.executor_driver.backend.stomp.StompFrame;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class MessagePayloadApiMapperTest {

  private Mapper<StompFrame, String> mapper;

  @Before
  public void setUp() {
    mapper = new MessagePayloadApiMapper();
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringSuccess() throws Exception {
    // Action:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), ""));

    // Effect:
    assertEquals("", message);
  }

  /**
   * Должен успешно извлечь строку в 1 символ из сообщения.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractOneCharStringSuccess() throws Exception {
    // Action:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\""));

    // Effect:
    assertEquals("\"", message);
  }

  /**
   * Должен успешно извлечь пустую строку из сообщения из двух кавычек.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractEmptyStringWithoutQuotesSuccess() throws Exception {
    // Action:
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\"\""));

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
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), " "));

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
    String message = mapper.map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\n"));

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
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(), "Message test super nova"));

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
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "  \n Message test super nova  \n "));

    // Effect:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних кавычек.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringFromQuotesSuccess() throws Exception {
    // Action:
    String message = mapper
        .map(
            new StompFrame(Command.MESSAGE, Collections.emptyMap(), "\"Message test super nova\""));

    // Effect:
    assertEquals("Message test super nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения без крайних кавычек пробелов и переносов строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringFromQuotesSuccess() throws Exception {
    // Action:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"  \n Message test super nova  \n \""));

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
    String message = mapper
        .map(
            new StompFrame(Command.MESSAGE, Collections.emptyMap(), "Message \"test super\" nova"));

    // Effect:
    assertEquals("Message \"test super\" nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте, но удалив крайние.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractStringWithQuotesWithoutLeadingAndTrailingOnesSuccess() throws Exception {
    // Action:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"Message \"test super\" nova\""));

    // Effect:
    assertEquals("Message \"test super\" nova", message);
  }

  /**
   * Должен успешно извлечь строку из сообщения, сохранив кавычки в тексте, но удалив крайние,
   * пробелы и переносы строк.
   *
   * @throws Exception ошибка
   */
  @Test
  public void extractTrimmedStringWithQuotesWithoutLeadingAndTrailingOnesSuccess()
      throws Exception {
    // Action:
    String message = mapper
        .map(new StompFrame(Command.MESSAGE, Collections.emptyMap(),
            "\"  \n Message \"test super\" nova  \n \""));

    // Effect:
    assertEquals("Message \"test super\" nova", message);
  }
}